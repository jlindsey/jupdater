package org.lesmothian.jupdater.core;

import com.google.common.base.Objects;
import java.util.Hashtable;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Manifest implements Sided {
  public String packFingerprint;
  public String forgeVersion;
  public PackFile[] mods;
  public PackFile[] configs;

  private Logger logger;

  public Manifest() {
    logger = LogManager.getLogger();
  }

  public PackFile[] filesToUpdate(Manifest other) {
    ArrayList<PackFile> files = new ArrayList<PackFile>(1);

    if (packFingerprint.equals(other.packFingerprint)) {
      return null;
    }

    for (PackFile mod : mods) {
      PackFile otherFile = findPackFile(mod.file.toString(), other.mods);
      if (mod.compareTo(otherFile) != 0) { files.add(mod); }
    }

    for (PackFile config : configs) {
      PackFile otherFile = findPackFile(config.file.toString(), other.configs);
      if (config.compareTo(otherFile) != 0) { files.add(config); } 
    }

    PackFile[] updates = new PackFile[files.size()];
    return files.toArray(updates);
  }

  public PackFile getCorrespondingPackFile(PackFile p) {
    if (p.type == PackFile.PackFileType.MOD && p.id != null) {
      return getModFileById(p.id);
    } else {
      PackFile found = null;
      if (configs != null) {
        found = findPackFile(p.getFile().getPath(), configs);
      } else {
        found = findPackFile(p.getFile().getPath(), mods);
      }

      return found;
    }
  }

  public PackFile getModFileById(String id) {
    PackFile found = null;

    if (mods != null) {
      for (PackFile pf : mods) {
        if (pf.id.equals(id)) {
          found = pf;
          break;
        }
      }
    }

    return found;
  }

  public PackFile findPackFile(String path, PackFile[] array) {
    if (array == null) {
      return null;
    }

    for (PackFile pf : array) {
      if (pf.file.toString().equals(path)) {
        return pf;
      }
    }

    return null;
  }

  // Sided implementation
  @Override
  public void setLocal() {
    for (PackFile f : mods)     { f.setLocal(); }
    for (PackFile f : configs)  { f.setLocal(); }
  }

  @Override
  public void setRemote() {
    for (PackFile f : mods)     { f.setRemote(); }
    for (PackFile f : configs)  { f.setRemote(); }
  }

  @Override
  public boolean isLocal() { return (mods[0].isLocal()); }
  @Override
  public boolean isRemote() { return (mods[0].isRemote()); }
}
