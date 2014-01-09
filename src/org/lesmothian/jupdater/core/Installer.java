package org.lesmothian.jupdater.core;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.lesmothian.jupdater.utils.OSUtils;
import org.lesmothian.jupdater.utils.FileUtils;

public class Installer implements Runnable {
  private Logger logger;

  private File currentJarLocation;
  private File destination;

  private Properties buildProperties;

  public Installer() {
    logger = LogManager.getLogger();
    buildProperties = new Properties();

    try {
      buildProperties.load(getClass().getResourceAsStream("/build.properties"));
      logger.debug(buildProperties);

      currentJarLocation = new File(getClass().getProtectionDomain()
                                    .getCodeSource().getLocation().toURI().getPath());
      destination = OSUtils.getVersionsDirectory();
    } catch (Throwable e) {
      logger.fatal("Unable to determine JAR's current location: {}", e);
      System.exit(-1);
    }

    logger.debug("Current Jar location: {}", currentJarLocation);
  }

  public boolean needsInstall() {
    return (currentJarLocation.getPath().equals(destination.getPath()));
  }

  @Override
  public void run() {
    File modsDir = OSUtils.getModsDirectory();
    File configsDir = OSUtils.getConfigDirectory();

    if (!modsDir.exists()) {
      modsDir.mkdirs();
    }

    if (!configsDir.exists()) {
      configsDir.mkdirs();
    }
  }
}
