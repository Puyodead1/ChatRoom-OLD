package optic_fusion1.server.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import optic_fusion1.server.Main;
import static optic_fusion1.server.Main.LOGGER;

public final class Utils {

  private Utils() {
  }

  public static void copy(String resource, String destination) {
    InputStream ddlStream = Main.class.getClassLoader().getResourceAsStream(resource);
    if (ddlStream == null) {
      LOGGER.warn("The resource '" + resource + " can not be found");
      return;
    }
    try (FileOutputStream fos = new FileOutputStream(destination)) {
      byte[] buf = new byte[2048];
      int r;
      while (-1 != (r = ddlStream.read(buf))) {
        fos.write(buf, 0, r);
      }
    } catch (FileNotFoundException ex) {
      LOGGER.exception(ex);
    } catch (IOException ex) {
      LOGGER.exception(ex);
    }
  }

  public static void saveResource(File dataFolder, String resourcePath, boolean replace) {
    if (resourcePath == null || resourcePath.isEmpty()) {
      throw new IllegalArgumentException("resourcePath can not be null or empty");
    }
    if (dataFolder == null || resourcePath.isEmpty()) {
      throw new IllegalArgumentException("dataFolder can not be null or empty");
    }

    resourcePath = resourcePath.replace('\\', '/');
    InputStream in = getResource(resourcePath);
    if (in == null) {
      LOGGER.warn("The embedded resource '" + resourcePath + " can not be found");
      return;
    }
    File outFile = new File(dataFolder, resourcePath);
    int lastIndex = resourcePath.lastIndexOf('/');
    File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

    if (!outDir.exists()) {
      outDir.mkdirs();
    }

    try {
      if (!outFile.exists() || replace) {
        try (OutputStream out = new FileOutputStream(outFile)) {
          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
          }
        }
        in.close();
      } else {
        LOGGER.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists");
      }
    } catch (IOException ex) {
      LOGGER.severe("Could not save " + outFile.getName() + "  to " + outFile);
      LOGGER.exception(ex);
    }
  }

  public static InputStream getResource(String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("fileName can not be null or empty");
    }
    InputStream input = Main.class.getClassLoader().getResourceAsStream(fileName);
    if (input == null) {
      LOGGER.warn("The resource '" + fileName + "' can not be found");
      return null;
    }
    return input;
  }

}
