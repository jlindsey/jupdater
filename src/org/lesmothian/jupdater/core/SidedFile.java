package org.lesmothian.jupdater.core;

import java.io.File;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lesmothian.jupdater.utils.OSUtils;
import com.google.common.net.UrlEscapers;

public class SidedFile implements Sided {
  public static final String REMOTE_URL_BASE = "http://minecraft.jlindsey.me";

  private Logger logger;

  private URI uri;
  private File file;
  private Side side;

  public SidedFile(String filePath, PackFile.PackFileType type, Side _side) {
    logger = LogManager.getLogger();

    side = _side;
    file = buildLocalFile(filePath, type);
    uri = buildURI(filePath, type);
  }

  public File getFile() { return file; }
  public URI getURI()   { return uri; }
  public String toString() {
    if (side == Side.LOCAL) {
      return getFile().getPath();
    } else {
      return getURI().getPath();
    }
  }

  private File buildLocalFile(String path, PackFile.PackFileType type) {
    File f = null;
    switch (type) {
    case MOD:
      f = new File(OSUtils.getModsDirectory(), path);
      break;
    case CONFIG:
      f = new File(OSUtils.getConfigDirectory(), path);
      break;
    }

    return f;
  }

  private URI buildURI(String path, PackFile.PackFileType type) {
    StringBuilder sb = new StringBuilder(SidedFile.REMOTE_URL_BASE + "/");
    switch(type) {
    case MOD:
      sb.append("mods/");
      break;
    case CONFIG:
      sb.append("configs/");
      break;
    }
    sb.append(UrlEscapers.urlPathSegmentEscaper().escape(new File(path).getName()));

    try {
      return new URI(sb.toString());
    } catch(java.net.URISyntaxException e) {
      logger.error(e);
      return null;
    }
  }

  public boolean exists()
  throws UnsupportedOperationException {
    if (!isLocal()) {
      throw new UnsupportedOperationException("SidedFile only supports exists() on local files");
    }

    return this.file.exists();
  }

  // Sided implementation
  @Override
  public void setLocal() { this.side = Side.LOCAL; }
  @Override
  public void setRemote() { this.side = Side.REMOTE; }
  @Override
  public boolean isLocal() { return (this.side == Side.LOCAL); }
  @Override
  public boolean isRemote() { return (this.side == Side.REMOTE); }
}
