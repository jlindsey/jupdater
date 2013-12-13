package org.lesmothian.jupdater.utils;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.TypeAdapter;

import org.lesmothian.jupdater.core.Manifest;
import org.lesmothian.jupdater.core.PackFile;
import org.lesmothian.jupdater.core.PackFile.PackFileType;
import org.lesmothian.jupdater.core.SidedFile;
import org.lesmothian.jupdater.core.Sided.Side;


public class ManifestAdapter extends TypeAdapter<Manifest> {
  private Logger logger;

  private Side side;

  public ManifestAdapter(Side _side) {
    logger = LogManager.getLogger();
    this.side = _side;
  }

  @Override
  public Manifest read(JsonReader reader)
  throws java.io.IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return logger.exit(null);
    }

    Manifest manifest = new Manifest();

    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      if (name.equals("packFingerprint")) {
        manifest.packFingerprint = reader.nextString();
      } else if (name.equals("forgeVersion")) {
        manifest.forgeVersion = reader.nextString();
      } else if (name.equals("mods") || name.equals("configs") || name.equals("forge")) {
        reader.beginArray();
        try {
          PackFileType type = PackFileType.fromString(name);
          readPackFileArray(reader, type, manifest);
        } catch (IllegalArgumentException e) {
          logger.error(e);
          System.exit(-1);
        }
        reader.endArray();
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();

    return manifest;
  }

  @Override
  public void write(JsonWriter writer, Manifest manifest)
  throws java.io.IOException {
    if (manifest == null) {
      writer.nullValue();
      return;
    }

    writer.beginObject();
    writer.name("packFingerprint").value(manifest.packFingerprint);
    writer.name("forgeVersion").value(manifest.forgeVersion);
    
    PackFileAdapter modsAdapter = new PackFileAdapter(PackFileType.MOD, side);
    writer.name("mods").beginArray();
    for (PackFile mod : manifest.mods) {
      modsAdapter.write(writer, mod);
    }
    writer.endArray();

    PackFileAdapter configsAdapter = new PackFileAdapter(PackFileType.CONFIG, side);
    writer.name("configs").beginArray();
    for (PackFile config : manifest.configs) {
      configsAdapter.write(writer, config);
    }
    writer.endArray();
    writer.endObject();
  }

  
  private void readPackFileArray(JsonReader reader, PackFileType type, Manifest manifest)
  throws java.io.IOException {
    PackFileAdapter adapter = new PackFileAdapter(type, side);

    ArrayList<PackFile> list = new ArrayList<PackFile>(5);
    while (reader.hasNext()) {
      PackFile packFile = adapter.read(reader);
      list.add(packFile);
    }

    logger.trace("Parsed {} {} pack files", list.size(), type);
    
    PackFile[] files = new PackFile[list.size()];
    list.toArray(files);

    switch (type) {
      case MOD:
        manifest.mods = files;
        break;
      case CONFIG:
        manifest.configs = files;
        break;
    }
  }

  protected class PackFileAdapter extends TypeAdapter<PackFile> {
    private Logger logger;

    private PackFileType type;
    private Side side;

    public PackFileAdapter(PackFileType _type, Side _side) {
      logger = LogManager.getLogger();

      this.type = _type;
      this.side = _side;
    }

    @Override
    public PackFile read(JsonReader reader)
    throws java.io.IOException {
      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return logger.exit(null);
      }

      PackFile packFile = new PackFile();
      packFile.type = type;

      reader.beginObject();
      while (reader.hasNext()) {
        String name = reader.nextName();
        if (name.equals("id")) {
          packFile.id = reader.nextString();
        } else if (name.equals("file")) {
          String fileName = reader.nextString();

          if (type != PackFileType.MOD) {
            packFile.id = fileName;
          }

          packFile.file = new SidedFile(fileName, type, side);
        } else if (name.equals("fingerprint")) {
          packFile.fingerprint = reader.nextString();
        } else {
          reader.skipValue();
        }
      }
      reader.endObject();

      return packFile;
    }

    @Override
    public void write(JsonWriter writer, PackFile pf)
    throws java.io.IOException {
      if (pf == null) {
        writer.nullValue();
        return;
      }

      writer.beginObject();
      if (type == PackFileType.MOD) {
        writer.name("id").value(pf.id);
      }
      writer.name("file").value(pf.file.toString());
      writer.name("fingerprint").value(pf.fingerprint);
      writer.endObject();
    }
  }
}
