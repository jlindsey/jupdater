package org.lesmothian.jupdater.forge;

import java.net.URL;
import java.io.File;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lesmothian.jupdater.utils.OSUtils;

public class ForgeInstallWrapper implements Runnable {
  private Logger logger;
  private File installerJar;

  private URLClassLoader loader;
  @SuppressWarnings("rawtypes")
  private Class installerClass;
  private Method runnerMethod;

  @SuppressWarnings("unchecked")
  public ForgeInstallWrapper(File _installerJar) {
    logger = LogManager.getLogger();
    installerJar = _installerJar;

    logger.info("Installing Forge from JAR at {}", installerJar);

    try {
      URL[] jarPath = { installerJar.toURI().toURL() };
      loader = new URLClassLoader(jarPath, this.getClass().getClassLoader());
      
      installerClass = Class.forName("cpw.mods.fml.installer.ClientInstall", true, loader);
      logger.debug("Found installer class: {}", installerClass);

      runnerMethod = installerClass.getMethod("run", File.class);
      logger.debug("Found installer run method: {}", runnerMethod);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public void run() {
    logger.info("Invoking Forge client installer");

    try {
      PrintStream oldOut = System.out;
      System.setOut(createLoggerProxy(oldOut));
      Object instance = installerClass.newInstance();
      Object result = runnerMethod.invoke(instance, OSUtils.getMinecraftDirectory());
      System.setOut(oldOut);
    } catch (Throwable e) {
      logger.fatal("Unable to install Forge: {}", e);
      System.exit(-1);
    }

    logger.info("Forge update complete");
  }

  private PrintStream createLoggerProxy(final PrintStream realStream) {
    final Logger proxyLogger = LogManager.getLogger("[ForgeInstallerProxy]");
    return new PrintStream(realStream) {
      @Override
      public void print(final String str) {
        proxyLogger.info(str);
      }
    };
  }
}
