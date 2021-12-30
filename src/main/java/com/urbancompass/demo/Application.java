/**
 * Copyright (C) 2021 Urban Compass, Inc.
 */
package com.urbancompass.demo;

import java.io.IOException;

/**
 * @author shiqi.rao
 */
public class Application {

  public static void main(String[] args) throws IOException, InterruptedException {
    IndicoClientProvider indicoClientProvider = new IndicoClientProvider();
    ObjectDetection objectDetection = new ObjectDetection(indicoClientProvider);
    objectDetection.execute();
  }

}
