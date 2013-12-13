package org.lesmothian.jupdater.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lesmothian.jupdater.core.LauncherProfileData;
import org.lesmothian.jupdater.core.Manifest;
import org.lesmothian.jupdater.core.PackFile;
import org.lesmothian.jupdater.core.Sided;

public final class JsonUtils {
  public static Manifest deserializeManifest(String json, Sided.Side side) {
    return getGsonForSide(side).fromJson(json, Manifest.class);
  }

  public static String serializeManifest(Manifest manifest, Sided.Side side) {
    return getGsonForSide(side).toJson(manifest);
  }

  public static LauncherProfileData deserializeLauncherProfileData(String json) {
    return getGsonForSide(null).fromJson(json, LauncherProfileData.class);
  }

  public static String serializeLauncherProfileData(LauncherProfileData data) {
    return getGsonForSide(null).toJson(data);
  }

  private static Gson getGsonForSide(Sided.Side side) {
    return new GsonBuilder()
      .registerTypeAdapter(Manifest.class, new ManifestAdapter(side))
      .registerTypeAdapter(LauncherProfileData.class, new LauncherProfileDataAdapter())
      .setPrettyPrinting()
      .create();
  }
}
