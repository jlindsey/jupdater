package org.lesmothian.jupdater.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Closeable;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closer;

public final class IOUtils {
  public static String toString(final InputStream in)
  throws IOException {
    Closer closer = Closer.create();
    closer.register(in);

    try {
      final InputStreamReader inr = closer.register(new InputStreamReader(in, Charsets.UTF_8));
      return CharStreams.toString(inr);
    } finally {
      closer.close();
    }
  }
}
