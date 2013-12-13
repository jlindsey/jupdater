package org.lesmothian.jupdater.forge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.lesmothian.jupdater.utils.OSUtils;

public class ForgeLauncher implements Runnable {
  private Logger logger;
  private String[] args;

  public ForgeLauncher(String[] _args) {
    logger = LogManager.getLogger();
    args = _args;
  }

  @Override
  public void run() {

  }
}
