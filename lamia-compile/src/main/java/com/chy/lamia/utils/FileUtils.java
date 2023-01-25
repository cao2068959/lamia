package com.chy.lamia.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;

public class FileUtils {

    static String classPath = null;

    public static File openFile(String fileName, String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path = path + "/" + fileName;
        return new File(path);
    }


    public static File openClasspathFile(String fileName) {
        return openFile(fileName, getClassPath());
    }

    public static <T> T readClasspathJsonFile(String fileName, Class<T> type) throws IOException {
        File file = openClasspathFile(fileName);
        if (file == null) {
            return null;
        }
        return JsonUtils.objectMapper.readValue(file, type);
    }

    public static String readSimpleClasspathFile(String fileName) throws IOException {
        File file = openClasspathFile(fileName);
        if (file == null || !file.exists()) {
            return null;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        fileInputStream.read(bytes);
        return new String(bytes);
    }


    public static String getClassPath() {
        String result = classPath;
        if (result != null && result.length() > 0) {
            return result;
        }
        URL resource = FileUtils.class.getClassLoader().getResource("");
        if (resource != null) {
            classPath = resource.getPath();
        }
        return classPath;
    }


    public static void writeClasspathFile(String fileName, String context, boolean append) throws IOException {
        File file = openClasspathFile(fileName);
        FileOutputStream outputStream = new FileOutputStream(file, append);
        outputStream.write(context.getBytes());
    }
}
