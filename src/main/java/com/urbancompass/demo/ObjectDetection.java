/**
 * Copyright (C) 2021 Urban Compass, Inc.
 */
package com.urbancompass.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.indico.IndicoClient;
import com.indico.entity.Submission;
import com.indico.mutation.UpdateSubmission;
import com.indico.storage.Blob;
import com.indico.storage.RetrieveBlob;
import com.indico.type.SubmissionStatus;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shiqi.rao
 */
public class ObjectDetection {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectDetection.class);

  private final IndicoClientProvider indicoClientProvider;
  /**
   * Current workflow id of Object Detection is 13.
   */
  public CustomFlag WORKFLOW_ID = new CustomFlag("workflow_id", "13");

  public ObjectDetection(IndicoClientProvider indicoClientProvider) {
    this.indicoClientProvider = indicoClientProvider;
  }

  private void outputResult(String outputName, String blobString) throws IOException {
    File target = new File("src/main/resources/output/" + outputName);
    if (target.exists()) {
      if (!target.delete()) {
        LOG.error("Failed to delete legacy file" + outputName);
      } else {
        LOG.info("Success to delete legacy file " + outputName);
      }
    }
    if (!target.createNewFile()) {
      LOG.error("Failed to create file" + outputName);
    }
    FileOutputStream fos = new FileOutputStream(target, false);
    fos.write(blobString.getBytes());
    fos.close();
  }

  public void execute() throws IOException, InterruptedException {
    IndicoClient client = indicoClientProvider.getIndicoClient();
    List<File> files = Optional.ofNullable(new File("src/main/resources/input").listFiles())
        .map(Arrays::stream)
        .orElseThrow(() -> new RuntimeException(
            "There should be some binary PDF files at Path src/main/resources/pdf for the test."))
        .collect(Collectors.toList());
    List<Integer> submissions = submission(client, Integer.parseInt(WORKFLOW_ID.get()), files);
    for (Integer id : submissions) {
      retrieveResult(client, id);
    }
  }

  private void retrieveResult(IndicoClient client, Integer id)
      throws IOException, InterruptedException {
    Submission submission = client.getSubmission().submissionId(id).query();
    while (submission.status != SubmissionStatus.COMPLETE &&
        submission.status != SubmissionStatus.FAILED) {
      Thread.sleep(1000);
      LOG.info("submission Status: {}", submission.status);
      submission = client.getSubmission().submissionId(id).query();
    }
    LOG.info("submission Status: {}", submission.status);
    // retrieve blob
    String url = "https://beta-indico.rpa.compass.com/" + submission.resultFile;
    RetrieveBlob retrieveBlob = client.retrieveBlob();
    LOG.info(url);
    retrieveBlob.url(url);
    Blob blob = retrieveBlob.execute();
    String blobString = blob.asString();
    LOG.info("submission id {} blob result {}", submission.id, blobString);
    outputResult(submission.resultFile.substring(submission.resultFile.lastIndexOf("/")),
        blobString);
    blob.close();
    // update submission retrieved
    UpdateSubmission updateSubmission = client.updateSubmission();
    updateSubmission.submissionId(submission.id);
    updateSubmission.retrieved(true);
    updateSubmission.execute();
  }

  private List<Integer> submission(IndicoClient client, Integer workflowId, List<File> files)
      throws IOException {
    Map<String, byte[]> maps = new HashMap<>();
    for (File f : files) {
      maps.put(f.getName(), FileUtils.readFileToByteArray(f));
    }
    return Optional.ofNullable(client.workflowSubmission())
        .map(submission -> submission.byteStreams(maps)
            .workflowId(workflowId)
            .execute())
        .orElseThrow(() -> new RuntimeException("Indico workflowSubmission is null"));
  }
}
