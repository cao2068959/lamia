package com.chy.lamia.convert.core.log;


import com.chy.lamia.convert.core.utils.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LamiaSimpleLogger {

    List<String> content = new ArrayList<>();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String fileName = "lamialog.log";


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
            outputStream = new FileOutputStream(FileUtils.openClasspathFile(fileName), true);
            if (outputStream == null) {
                return;
            }
            for (String txt : content) {
                wire(outputStream, txt + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            content = new ArrayList<>();
            try {
                if (outputStream == null) {
                    return;
                }
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }


    private void wire(OutputStream outputStream, String context) throws IOException {
        outputStream.write(context.getBytes());
    }
}
