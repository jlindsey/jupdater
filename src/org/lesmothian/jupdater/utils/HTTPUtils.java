package org.lesmothian.jupdater.utils;

import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import com.google.common.io.Closer;
import com.google.common.io.CharStreams;
import com.google.common.base.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lesmothian.jupdater.core.PackFile;
import org.lesmothian.jupdater.core.Manifest;
import org.lesmothian.jupdater.core.Sided;

public final class HTTPUtils {
  public static final String MANIFEST_FILE = "http://minecraft.jlindsey.me/manifest.json";
  public static final String FORGE_DOWNLOAD_BASE = 
    "http://files.minecraftforge.net/minecraftforge/";

  public static Manifest fetchRemoteManifest()
  throws java.io.IOException {
    Logger logger = LogManager.getLogger("fetchRemoteManifest");
    logger.debug("File URL: {}", HTTPUtils.MANIFEST_FILE);

    HttpURLConnection conn = null;
    try {
      conn = getConnection(HTTPUtils.MANIFEST_FILE);
    } catch(Throwable e) {
      logger.error(e);
      System.exit(-1);
    }

    conn.connect();
    logger.debug("Content Type: {}; Content Length: {}", conn.getContentType(), 
                 conn.getContentLength());
    
    String manifestString = IOUtils.toString((InputStream)conn.getContent());
    Manifest manifest = JsonUtils.deserializeManifest(manifestString, Sided.Side.REMOTE);

    conn.disconnect();
    return manifest;
  }

  public static long getTotalDownloadSize(PackFile[] files) {
    Logger logger = LogManager.getLogger("getTotalDownloadSize");
    long out = 0;

    try {
      for (PackFile f : files) {
        HttpURLConnection conn = getConnection(f.getURI());
        conn.connect();
        out += conn.getContentLength();
        conn.disconnect();
      }
    } catch (Throwable e) {
      logger.error(e);
    }

    return out;
  }

  public static File downloadForgeInstaller(String version)
  throws java.io.IOException, java.net.URISyntaxException {
    File forgeTemp = File.createTempFile("forge" + version, ".jar");
    String forgeURIString = 
      HTTPUtils.FORGE_DOWNLOAD_BASE + "minecraftforge-installer-" + version + ".jar";

    downloadFile(new URI(forgeURIString), forgeTemp);

    return forgeTemp;
  }

  public static void downloadFile(URI source, File destination)
  throws java.io.IOException {
    Logger logger = LogManager.getLogger("downloadFile");

    HttpURLConnection conn = null;
    try {
      conn = getConnection(source);
    } catch (Throwable e) {
      logger.error(e);
      System.exit(-1);
    }

    conn.connect();
    int responseCode = conn.getResponseCode();

    if (responseCode != 200) {
      logger.error("Bad response code: {}", responseCode);
      return;
    }

    Closer closer = Closer.create();

    byte[] buf = new byte[5120];
    int n = 0;
    long totalRead = 0;
    InputStream in = closer.register(conn.getInputStream());
    FileOutputStream out = closer.register(new FileOutputStream(destination));

    try {
      while ((n = in.read(buf)) != -1) {
        totalRead = totalRead + n;
        out.write(buf, 0, n);
      }

      out.flush();
    } finally {
      conn.disconnect();
      closer.close();
    }
  }

  private static HttpURLConnection getConnection(String urlStr)
  throws java.net.MalformedURLException, java.io.IOException {
    URL url = new URL(urlStr);
    return getConnection(url);
  }

  private static HttpURLConnection getConnection(URI uri)
  throws java.net.MalformedURLException, java.io.IOException {
    URL url = uri.toURL();
    return getConnection(url);
  }

  private static HttpURLConnection getConnection(URL url) 
  throws java.net.MalformedURLException, java.io.IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    conn.setDoInput(true);
    conn.setDoOutput(false);
    conn.setAllowUserInteraction(false);
    conn.setUseCaches(false);

    return conn;
  }
}
