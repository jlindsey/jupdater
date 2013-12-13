package org.lesmothian.jupdater.utils;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OSUtils {
  private static final Logger logger = LogManager.getLogger();
  public static File userHome = new File(System.getProperty("user.home"));

  public static enum OSType {
    WINDOWS ("WIN"),
    OS_X    ("MAC"),
    NIX     ("NIX");

    private final String shortName;

    OSType(String _shortName) { this.shortName = _shortName; }
    public String shortName() { return this.shortName; }

    public static OSType fromString(String str) throws IllegalArgumentException {
      if (str != null) {
        for (OSType t : OSType.values()) {
          if (str.toUpperCase().contains(t.shortName())) {
            return t;
          }
        }
      }

      throw new IllegalArgumentException(String.format("No OSType with shortName '%s'.", str));
    }
  }

  public static OSType getOSType() {
    String osString = System.getProperty("os.name").toUpperCase();
    return OSType.fromString(osString);
  }

  public static File getMinecraftDirectory() {
    switch(getOSType()) {
    case OS_X:
      return new File(userHome, "Library/Application Support/minecraft");
    case NIX:
      return new File(userHome, ".minecraft");
    case WINDOWS:
      return new File(System.getenv("APPDATA"), ".minecraft");
    default:
      return new File("/tmp");
    }
  }

  public static File getModsDirectory() {
    return new File(getMinecraftDirectory(), "mods");
  }

  public static File getConfigDirectory() {
    return new File(getMinecraftDirectory(), "config");
  }

  public static File getVersionsDirectory() {
    return new File(getMinecraftDirectory(), "versions");
  }

  public static File getForgeDirectory(String version) {
    return new File(getVersionsDirectory(), "1.6.4-Forge" + version);
  }

  public static File getManifestFile() {
    return new File(getMinecraftDirectory(), "manifest.json");
  }

  public static File getLauncherProfilesFile() {
    return new File(getMinecraftDirectory(), "launcher_profiles.json");
  }
}
