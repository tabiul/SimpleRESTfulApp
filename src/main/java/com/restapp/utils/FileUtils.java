package com.restapp.utils;

import java.io.File;

public class FileUtils {
    public static boolean isValidFilePath(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean isValidDirectory(String folderPath) {
        File file = new File(folderPath);
        return file.exists() && file.isDirectory();
    }

    public static boolean hasReadAndWriteAccess(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.canWrite() && file.canRead();
    }

}
