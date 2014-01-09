package org.lesmothian.jupdater.core;

import java.io.File;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.lesmothian.jupdater.utils.*;
import org.lesmothian.jupdater.forge.ForgeInstallWrapper;
import org.lesmothian.jupdater.forge.ForgeLauncher;
import org.lesmothian.jupdater.ui.ProgressWindow;

public class JUpdater implements Runnable {
  private Logger logger;
  private Manifest localManifest;
  private Manifest remoteManifest;
  private LauncherProfileData profiles;

  private ProgressWindow progressWindow;

  protected String[] args;

  public JUpdater(String[] _args) {
    logger = LogManager.getLogger();
    logger.debug("OS Type: {}", OSUtils.getOSType());

    args = _args;
    logger.debug("Args: {}", (Object)args);

    setupManifests();
  }

  @Override
  public void run() {
    ensureInstalled();

    if (!upToDate()) {
      updateForgeIfNeeded();
      updatePackFiles();
      updateLocalManifest();
      setupLauncherProfile();
    }

    launch();
  }

  protected void setupManifests() {
    try {
      localManifest = FileUtils.fetchLocalManifest();
    } catch(java.io.FileNotFoundException e) {
      logger.info("Manifest file not found at {}, assuming fresh install",
                  OSUtils.getManifestFile());
      localManifest = new Manifest();
    } catch (java.io.IOException e) {
      logger.error(e);
      logger.error("Error reading local manifest file. Cleaning it up and redownloading pack!");

      File f = OSUtils.getManifestFile();
      if (f.exists()) f.delete();
    }

    try {
      remoteManifest = HTTPUtils.fetchRemoteManifest();
    } catch (java.io.IOException e) {
      logger.error(e);
      remoteManifest = new Manifest();
    }
  }

  protected void ensureInstalled() {
    Installer installer = new Installer();
    installer.run();
  }

  protected boolean upToDate() {
    if (remoteManifest.packFingerprint.equals(localManifest.packFingerprint)) {
      logger.info("Manifest fingerprints identical: {}", localManifest.packFingerprint);
      return true;
    }

    return false;
  }


  protected void updateForgeIfNeeded() {
    if (remoteManifest.forgeVersion.equals(localManifest.forgeVersion)) return;

    logger.info("Downloading new forge version: {}", remoteManifest.forgeVersion);

    try {
      File forgeInstaller = HTTPUtils.downloadForgeInstaller(remoteManifest.forgeVersion);
      ForgeInstallWrapper wrapper = new ForgeInstallWrapper(forgeInstaller);
      wrapper.run();
    } catch (Throwable e) {
      logger.error(e);
      logger.error("Unable to install new forge! Bailing out");
      System.exit(-1);
    }
  }


  protected void updateLocalManifest() {
    try {
      logger.info("Rewriting local manifest at {}", OSUtils.getManifestFile());
      FileUtils.rewriteLocalManifest(remoteManifest);
    } catch (java.io.IOException e) {
      logger.error(e);
    }
  }

  protected void setupLauncherProfile() {
    try {
      profiles = FileUtils.fetchLauncherProfileData();
      logger.info("Read local profiles: {}", this.profiles);

      profiles.installProfile();
      FileUtils.rewriteLauncherProfiles(profiles);
    } catch (Throwable e) {
      logger.error(e);
    }
  }

  protected void updatePackFiles() {
    PackFile[] updates = remoteManifest.filesToUpdate(localManifest);
    if (updates != null) {
      long downloadSize = HTTPUtils.getTotalDownloadSize(updates);
      logger.info("Found {} mod files needing updates", updates.length);
      logger.info("Total download size: {}", FileUtils.humanizedFileSize(downloadSize));

      for (PackFile update : updates) {
        PackFile localPackFile = localManifest.getCorrespondingPackFile(update);

        if (localPackFile != null) {
          File localFile = localPackFile.getFile();
          logger.debug("Found {}", localFile);
          if (localFile.exists()) {
            logger.debug("Deleted {}", localFile);
            localFile.delete();
          }
        }

        try {
          logger.info("Downloading {} from {}", update.getFile(), update.getURI());
          HTTPUtils.downloadFile(update.getURI(), update.getFile());
          logger.info("Download complete");
        } catch(java.io.IOException e) {
          logger.error(e);
        }
      }
    }
  }

  protected void launch() {
    logger.info("Launching Forge...");
    ForgeLauncher launcher = new ForgeLauncher(args);
    launcher.run();
  }
}
