/**
 * Copyright (C) 2021 Urban Compass, Inc.
 */
package com.urbancompass.demo;

import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.IndicoKtorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shiqi.rao
 */
public class IndicoClientProvider {

  private static final Logger LOG = LoggerFactory.getLogger(IndicoClientProvider.class);

  /**
   * Env of which Indico cluster classification service will send to.
   */
  private final CustomFlag ENV = new CustomFlag("env", "beta");
  /**
   * Base host of the Indico cluster.
   */
  private final CustomFlag INDICO_HOST = new CustomFlag("host", "indico.rpa.compass.com");
  /**
   * Name of the Indico apiToken.
   */
  private final CustomFlag INDICO_API_TOKEN = new CustomFlag("token", null);

  public IndicoClient getIndicoClient() {
    LOG.info("API Token:" + INDICO_API_TOKEN.get());
    IndicoConfig config = new IndicoConfig.Builder()
        .host(String.format("%s-%s", ENV.get(), INDICO_HOST.get()))
        .apiToken(INDICO_API_TOKEN.get())
        .build();
    return new IndicoKtorClient(config);
  }
}
