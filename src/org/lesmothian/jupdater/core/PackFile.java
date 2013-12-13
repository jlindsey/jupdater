package org.lesmothian.jupdater.core;

import java.io.File;
import java.net.URI;
import com.google.common.base.Objects;

public class PackFile implements Sided, Comparable<PackFile> {
  public static enum PackFileType { 
    MOD     ("MOD"), 
    CONFIG  ("CONFIG");

    private final String name;

    PackFileType(String _name) { this.name = _name; }
    public String getName() { return this.name; }

    public static PackFileType fromString(String str) throws IllegalArgumentException {
      if (str != null) {
        for (PackFileType pft : PackFileType.values()) {
          if (str.toUpperCase().contains(pft.getName())) {
            return pft;
          }
        }
      }

      throw new IllegalArgumentException(String.format("No PackFileType with name '%s'", str));
    }
  }

  public String id;
  public SidedFile file;
  public String fingerprint;
  public PackFileType type;

  public File getFile() {
    return this.file.getFile();
  }

  public URI getURI() {
    return this.file.getURI();
  }

  // Sided implementation
  @Override
  public void setLocal() { this.file.setLocal(); }
  @Override
  public void setRemote() { this.file.setRemote(); }
  @Override
  public boolean isLocal() { return (this.file.isLocal()); }
  @Override
  public boolean isRemote() { return (this.file.isRemote()); }


  // Comparable implementation
  @Override
  public int compareTo(PackFile other) {
    if (other == null) {
      return 1;
    } else if (this.fingerprint.equals(other.fingerprint)) {
      return 0;
    } else {
      return -1;
    }
  }

  public String toString() {
    return Objects.toStringHelper(this)
      .add("id", id)
      .add("file", file)
      .add("sha", fingerprint)
      .toString();
  }
}
