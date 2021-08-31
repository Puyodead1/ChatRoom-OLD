package optic_fusion1.client;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Utils {

    public static String RESOURCE_FOLDER_NAME = "resources";
    public static String LOG_FOLDER_NAME = "logs";

    public static void playClip(File clipFile) throws IOException,
            UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        class AudioListener implements LineListener {
            private boolean done = false;

            @Override
            public synchronized void update(LineEvent event) {
                LineEvent.Type eventType = event.getType();
                if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
                    done = true;
                    notifyAll();
                }
            }

            public synchronized void waitUntilDone() throws InterruptedException {
                while (!done) {
                    wait();
                }
            }
        }
        AudioListener listener = new AudioListener();
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile)) {
            Clip clip = AudioSystem.getClip();
            try (clip) {
                clip.addLineListener(listener);
                clip.open(audioInputStream);
                clip.start();
                listener.waitUntilDone();
            }
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
            System.out.println("The embedded resource '" + resourcePath + " can not be found");
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
                System.out.println("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists");
            }
        } catch (IOException ex) {
            System.out.println("Could not save " + outFile.getName() + "  to " + outFile);
            System.out.println(ex);
        }
    }

    public static InputStream getResource(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName can not be null or empty");
        }
        InputStream input = Main.class.getClassLoader().getResourceAsStream(fileName);
        if (input == null) {
            System.out.println("The resource '" + fileName + "' can not be found");
            return null;
        }
        return input;
    }

    public static void playSound(String soundName) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException, URISyntaxException {
        File file = new File(getFolder(RESOURCE_FOLDER_NAME), String.format("%s.wav", soundName));
        if (!file.exists()) {
            Utils.saveResource(getFolder(RESOURCE_FOLDER_NAME), String.format("%s.wav", soundName), false);
        }
        Utils.playClip(file);
    }

    public static String getJarDirectory() throws URISyntaxException {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath().getParent().toString();
    }

    public static File getFile(String folderName, String fileName) throws URISyntaxException {
        File folder = getFolder(folderName);
        return Paths.get(folder.getPath(), fileName).toFile();
    }

    public static File getFolder(String folderName) throws URISyntaxException {
        File file = Paths.get(getJarDirectory(), folderName).toFile();
        if (!file.exists()) file.mkdir();
        return file;
    }
}
