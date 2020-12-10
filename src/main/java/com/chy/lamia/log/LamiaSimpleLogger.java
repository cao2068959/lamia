package com.chy.lamia.log;


import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LamiaSimpleLogger {

    List<String> content = new ArrayList<>();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String logPath = "/Users/hengyuan/IdeaProjects/work/lamia/chy/123/";

    private OutputStream openFile(String path) throws FileNotFoundException {
        String fileName = "lamialog.log";

        if (path == null || path.length() == 0) {
            path = fileName;
        } else {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path = path + "/" + fileName;
        }
        File file = new File(path);
        return new FileOutputStream(file, true);
    }

    private String nowTime() {
        String time = dateTimeFormatter.format(LocalDateTime.now());
        return "[" + time + "] ";
    }

    public void log(String text) {
        String result = nowTime() + text;
        content.add(result);
    }

    public void throwableLog(Throwable throwable) {
        if (throwable == null) {
            return;
        }
        String result = nowTime() + throwable.toString() + "\n";
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            result = result + "     " + stackTraceElement.toString() + "\n";
        }
        content.add(result);
    }


    public void push() {

        if (content.size() == 0) {
            return;
        }
        OutputStream outputStream = null;
        try {
            outputStream = openFile(logPath);
            for (String txt : content) {
                wire(outputStream, txt + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            content = new ArrayList<>();
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }


    }

    private void wire(OutputStream outputStream, String context) throws IOException {
        outputStream.write(context.getBytes());
    }
}
