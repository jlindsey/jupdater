package org.lesmothian.jupdater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.SwingUtilities;
import org.lesmothian.jupdater.core.JUpdater;
import org.lesmothian.jupdater.forge.ForgeInstallWrapper;

class Main {
  protected static final Logger logger;
  protected static final Thread onShutdown;

  static {
    logger = LogManager.getLogger();
    onShutdown = new Thread() {
      @Override
      public void run() {
        logger.info("Shutting down updater");
      }
    };
  }

  public static void main(String[] args) {
    logger.info("Starting updater");
    Runtime.getRuntime().addShutdownHook(onShutdown);

    JUpdater ju = new JUpdater(args);
    SwingUtilities.invokeLater(ju);
  }
}
