package com.github.onlycrab.argParser.common;

import java.io.*;

/**
 * Helper class for reading data from external sources.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class ExternalReader {
    /**
     * Read data from {@code java.io.InputStream} to {@code String}.
     *
     * @param is instance of {@link InputStream}
     * @return data from input stream as string
     * @throws IOException if an I/O error occurs
     */
    public static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Read data from {@code java.io.File} to {@code String}.
     *
     * @param path path to file
     * @return data from file as string
     * @throws IOException if an I/O error occurs
     */
    public static String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (path == null) {
            throw new IOException("File is <null>");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File <" + path + "> is not exists");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            IOException th = new IOException(String.format("Error at reading file <%s> : %s", path, e.getMessage()));
            th.setStackTrace(e.getStackTrace());
            throw th;
        }

        return sb.toString();
    }
}
