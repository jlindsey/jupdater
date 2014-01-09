package org.lesmothian.jupdater.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.DecimalFormat;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Closer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lesmothian.jupdater.core.PackFile;
import org.lesmothian.jupdater.core.Manifest;
import org.lesmothian.jupdater.core.Sided;
import org.lesmothian.jupdater.core.LauncherProfileData;

public final class FileUtils {
  public static void copyFile(File from, File to) throws java.io.IOException {
    if (!to.exists()) {
      to.createNewFile();
    }

    Closer closer = Closer.create();

    FileInputStream in = closer.register(new FileInputStream(from));
    FileOutputStream out = closer.register(new FileOutputStream(to));

    try {
      byte[] buf = new byte[2048];
      int n = 0;

      while ((n = in.read(buf)) != -1) {
        out.write(buf, 0, n);
      }

      out.flush();
    } finally {
      closer.close();
    }
  }

  public static Manifest fetchLocalManifest() throws java.io.IOException {
    String manifestString = Files.asCharSource(OSUtils.getManifestFile(), Charsets.UTF_8).read();
    return JsonUtils.deserializeManifest(manifestString, Sided.Side.LOCAL);
  }

  public static LauncherProfileData fetchLauncherProfileData()
  throws java.io.IOException {
    String dataString = Files.asCharSource(OSUtils.getLauncherProfilesFile(), Charsets.UTF_8).read();
    return JsonUtils.deserializeLauncherProfileData(dataString);
  }

  public static void rewriteLocalManifest(Manifest newManifest)
  throws java.io.IOException {
    File localManifestFile = OSUtils.getManifestFile();
    String newManifestJson = JsonUtils.serializeManifest(newManifest, Sided.Side.LOCAL);

    if (localManifestFile.exists()) localManifestFile.delete();

    writeStringToFile(newManifestJson, localManifestFile);
  }

  public static void rewriteLauncherProfiles(LauncherProfileData data)
  throws java.io.IOException {
    File file = OSUtils.getLauncherProfilesFile();
    String json = JsonUtils.serializeLauncherProfileData(data);

    if (file.exists()) file.delete();

    writeStringToFile(json, file);
  }

  protected static void writeStringToFile(String str, File f)
  throws java.io.IOException {
    File file = OSUtils.getLauncherProfilesFile();

    Closer closer = Closer.create();

    try {
      FileWriter fw = closer.register(new FileWriter(f));
      fw.write(str, 0, str.length());
      fw.flush();
    } finally {
      closer.close();
    }
  }

  public static File[] getModFiles() {
    Logger logger = LogManager.getLogger();
    logger.entry();

    File modsDir = OSUtils.getModsDirectory();
    logger.debug("Mods Dir: {}", modsDir);

    File[] modFiles = modsDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return (name.contains(".jar") || name.contains(".zip"));
      }
    });

    return logger.exit(modFiles);
  }

  public static File[] getConfigFiles() {
    Logger logger = LogManager.getLogger("getConfigFiles");
    logger.entry();

    File configsDir = OSUtils.getConfigDirectory();
    logger.debug("Configs Dir: {}", configsDir);

    File[] configFiles = configsDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return (name.contains(".txt") || name.contains(".conf") ||
                name.contains(".cfg") || name.contains(".lang"));
      }
    });

    return logger.exit(configFiles);
  }

  public static String humanizedFileSize(final long bytes) {
    final String[] suffixes = { "kB", "MB", "GB", "TB", "PB" };
    int i = -1;
    double size = bytes;

    do {
      i++;
      size /= 1024.0;
    } while (size > 1024.0);

    DecimalFormat df = new DecimalFormat("##.###");
    return (df.format(Math.max(size, 0.1)) + " " + suffixes[i]);
  }
}
