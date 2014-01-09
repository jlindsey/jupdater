package org.lesmothian.jupdater.core;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Field;
import com.google.common.base.Objects;

import org.lesmothian.jupdater.utils.OSUtils;
import org.lesmothian.jupdater.utils.FileUtils;

public class LauncherProfileData implements Cloneable {
  private static final String PROFILE_NAME = "JPack";
  private static final String JAVA_OPTS = "-Xms1G -Xmx3G -XX:MaxPermSize=128M";

  public static class LauncherProfile {
    public String name;
    public String lastVersionId;
    public String playerUUID;
    public String javaArgs;

    @Override
    public LauncherProfile clone() {
      LauncherProfile cloned = new LauncherProfile();
      cloned.name = name;
      cloned.lastVersionId = lastVersionId;
      cloned.playerUUID = playerUUID;
      cloned.javaArgs = javaArgs;

      return cloned;
    }

    public String toString() {
      Objects.ToStringHelper sh = Objects.toStringHelper(this);

      for (Field f : this.getClass().getDeclaredFields()) {
        try {
          sh.add(f.getName(), f.get(this));
        } catch (Throwable e) {
          continue;
        }
      }

      return sh.toString();
    }
  }

  public static class AuthenticationDatabase {
    public String username;
    public String accessToken;
    public String userid;
    public String uuid;
    public String displayName;

    public String toString() {
      Objects.ToStringHelper sh = Objects.toStringHelper(this);

      for (Field f : this.getClass().getDeclaredFields()) {
        try {
          sh.add(f.getName(), f.get(this));
        } catch (Throwable e) {
          continue;
        }
      }

      return sh.toString();
    }
  }

  public String selectedProfile;
  public String clientToken;
  public HashMap<String, LauncherProfile> profiles;
  public HashMap<String, AuthenticationDatabase> authenticationDatabase;

  public void installProfile() {
    if (profiles.containsKey(LauncherProfileData.PROFILE_NAME)) {
      return;
    }

    LauncherProfile forge = profiles.get("Forge");
    LauncherProfile profile = forge.clone();

    profile.name = LauncherProfileData.PROFILE_NAME;
    profile.javaArgs = LauncherProfileData.JAVA_OPTS;

    File packDir = new File(OSUtils.getVersionsDirectory(), LauncherProfileData.PROFILE_NAME);
    if (!packDir.exists()) {
      packDir.mkdirs();
    }

    FileUtils.copyFile(OSUtils.getVersionsDirectory(forge.latestVersionId, forge.latestVersionId + ".jar"), );
  }

  public String toString() {
    Objects.ToStringHelper sh = Objects.toStringHelper(this)
      .add("selectedProfile", selectedProfile)
      .add("clientToken", clientToken);

    Objects.ToStringHelper profilesSh = Objects.toStringHelper(profiles);
    Iterator<String> profileKeys = profiles.keySet().iterator();
    while (profileKeys.hasNext()) {
      String key = profileKeys.next();
      profilesSh.add(key, profiles.get(key).toString());
    }
    sh.add("profiles", profilesSh.toString());

    Objects.ToStringHelper dbSh = Objects.toStringHelper(authenticationDatabase);
    Iterator<String> dbKeys = authenticationDatabase.keySet().iterator();
    while (dbKeys.hasNext()) {
      String key = dbKeys.next();
      dbSh.add(key, authenticationDatabase.get(key).toString());
    }
    sh.add("authenticationDatabase", dbSh.toString());

    return sh.toString();
  }
}
