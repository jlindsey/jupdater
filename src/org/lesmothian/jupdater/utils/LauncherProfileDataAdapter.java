package org.lesmothian.jupdater.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.TypeAdapter;

import org.lesmothian.jupdater.core.LauncherProfileData;
import org.lesmothian.jupdater.core.LauncherProfileData.LauncherProfile;
import org.lesmothian.jupdater.core.LauncherProfileData.AuthenticationDatabase;

public class LauncherProfileDataAdapter extends TypeAdapter<LauncherProfileData> {
  private Logger logger;

  private LauncherProfileAdapter profileAdapter;
  private AuthenticationDatabaseAdapter dbAdapter;

  public LauncherProfileDataAdapter() {
    logger = LogManager.getLogger();

    profileAdapter = new LauncherProfileAdapter();
    dbAdapter = new AuthenticationDatabaseAdapter();
  }

  @Override
  public LauncherProfileData read(JsonReader reader) 
  throws java.io.IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }

    LauncherProfileData lpd = new LauncherProfileData();

    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      if (name.equals("selectedProfile")) {
        lpd.selectedProfile = reader.nextString();
      } else if (name.equals("clientToken")) {
        lpd.clientToken = reader.nextString();
      } else if (name.equals("profiles")) {
        HashMap<String,LauncherProfile> profiles = new HashMap<String,LauncherProfile>();

        reader.beginObject();
        while (reader.hasNext()) {
          String key = reader.nextName();

          reader.beginObject();
          profiles.put(key, profileAdapter.read(reader));
          reader.endObject();
        }
        reader.endObject();

        lpd.profiles = profiles;
      } else if (name.equals("authenticationDatabase")) {
        HashMap<String,AuthenticationDatabase> dbs 
          = new HashMap<String,AuthenticationDatabase>();

        reader.beginObject();
        while (reader.hasNext()) {
          String key = reader.nextName();

          reader.beginObject();
          dbs.put(key, dbAdapter.read(reader));
          reader.endObject();
        }
        reader.endObject();

        lpd.authenticationDatabase = dbs;
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();

    return lpd;
  }

  @Override
  public void write(JsonWriter writer, LauncherProfileData data) 
  throws java.io.IOException {
    if (data == null) {
      writer.nullValue();
      return;
    }

    writer.beginObject();
    writer.name("selectedProfile").value(data.selectedProfile);
    writer.name("clientToken").value(data.clientToken);

    Iterator<String> profileKeys = data.profiles.keySet().iterator();
    writer.name("profiles").beginObject();
    while (profileKeys.hasNext()) {
      String key = profileKeys.next();
      writer.name(key).beginObject();
      profileAdapter.write(writer, data.profiles.get(key));
      writer.endObject(); 
    }
    writer.endObject();

    Iterator<String> dbKeys = data.authenticationDatabase.keySet().iterator();
    writer.name("authenticationDatabase").beginObject();
    while (dbKeys.hasNext()) {
      String key = dbKeys.next();
      writer.name(key).beginObject();
      dbAdapter.write(writer, data.authenticationDatabase.get(key));
      writer.endObject();
    }
    writer.endObject();

    writer.endObject();
  }

  protected class LauncherProfileAdapter extends TypeAdapter<LauncherProfile> {
    @Override
    public LauncherProfile read(JsonReader reader) 
    throws java.io.IOException {
      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return null;
      }

      LauncherProfile profile = new LauncherProfile();

      while (reader.hasNext()) {
        String name = reader.nextName();

        try {
          Field f = profile.getClass().getField(name);
          f.set(profile, reader.nextString());
        } catch (Throwable e) {
          reader.skipValue();
          logger.warn("Unable to parse key {} in LauncherProfile: {}", name, e);
        }
      }

      return profile;
    }

    @Override
    public void write(JsonWriter writer, LauncherProfile data) 
    throws java.io.IOException {
      if (data == null) {
        writer.nullValue();
        return;
      }

      writer.name("name").value(data.name);
      writer.name("lastVersionId").value(data.lastVersionId);
      writer.name("playerUUID").value(data.playerUUID);
      if (data.javaArgs != null) {
        writer.name("javaArgs").value(data.javaArgs);
      }
    }
  }

  protected class AuthenticationDatabaseAdapter extends TypeAdapter<AuthenticationDatabase> {
    @Override
    public AuthenticationDatabase read(JsonReader reader)
    throws java.io.IOException {
      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return null;
      }

      AuthenticationDatabase db = new AuthenticationDatabase();

      while (reader.hasNext()) {
        String name = reader.nextName();
        
        try {
          Field f = db.getClass().getField(name);
          f.set(db, reader.nextString());
        } catch (Throwable e) {
          reader.skipValue();
          logger.warn("Unable to parse key {} in AuthenticationDatabase: {}", name, e);
        }
      }

      return db;
    }

    @Override
    public void write(JsonWriter writer, AuthenticationDatabase data)
    throws java.io.IOException {
      if (data == null) {
        writer.nullValue();
        return;
      }

      writer.name("username").value(data.username);
      writer.name("accessToken").value(data.accessToken);
      writer.name("userid").value(data.userid);
      writer.name("uuid").value(data.uuid);
      writer.name("displayName").value(data.displayName);
    }
  }
}
