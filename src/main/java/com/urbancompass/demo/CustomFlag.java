/**
 * Copyright (C) 2021 Urban Compass, Inc.
 */
package com.urbancompass.demo;

/**
 * @author shiqi.rao
 */
public class CustomFlag {

  private final String key;
  private final String defaultValue;

  public CustomFlag(String key, String defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public String get() {
    if (defaultValue == null) {
      return System.getProperty(key);
    }
    return System.getProperty(key, defaultValue);
  }
}
