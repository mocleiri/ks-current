package org.kuali.student.sqlOrganizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirManipulationUtils {
    public static void delEmptyDirs(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                delEmptyDirs(file);
            }
            // might be empty after recursion
            if (dir.listFiles().length == 0) {
                dir.delete();
            }
        }

    }

    public static String getPathSeperator() {
        return File.separator;
    }

    public static void deleteDirFiles(File dir) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File myFile : files) {
                if (myFile.isDirectory()) {
                    deleteDirFiles(myFile);
                } else {
                    myFile.delete();
                }

            }
        }
    }

    public static boolean dirExists(String path) {
        File dir = new File(path);
        return dir.exists() && dir.isDirectory();
    }

    public static boolean mkDirCascaded(File path) {
        boolean ret = false;
        if (!path.exists()) {
            File parent = path.getParentFile();
            if (!parent.exists()) {
                mkDirCascaded(parent);
            }
            path.mkdir();
            System.out.println("created directory " + path + " successfully");
            ret = true;
        }
        return ret;
    }


    public static void cleanOutputDir(File path) {

        boolean isDirectoryCreated = mkDirCascaded(path);

        if (isDirectoryCreated) {
            System.out.println("created directory " + path + " successfully");

        } else {
            deleteDirFiles(path);  // Invoke recursive method
        }
    }

    public static void cascadeDelDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                cascadeDelDir(file);
            }
            // might be empty after recursion
            if (dir.listFiles().length == 0) {
                dir.delete();
            }
        } else {
            dir.delete();
        }
        System.out.println("deleted directory and it's contents " + dir);
    }

    public static List<String> getOrderedDirPathList(String dirPath) {
        return convertFileListToPathList(getOrderedDirList(dirPath));
    }

    private static List<String> convertFileListToPathList(List<File> fileList) {
        List<String> filePathList = new ArrayList<String>();

        for (File file : fileList) {
            filePathList.add(file.getPath());
        }

        return filePathList;
    }

    public static List<File> getOrderedDirList(String dirPath) {
        List<File> fileList = getOrderedFileList(dirPath);
        List<File> dirList = new ArrayList<File>();

        for (File file : fileList) {
            if (file.isDirectory()) {
                dirList.add(file);
            }
        }
        return dirList;
    }

    private static List<File> getOrderedFileList(String dirPath) {
        File dir = new File(dirPath);
        List<File> fileList = new ArrayList<File>(Arrays.asList(dir.listFiles()));
        Collections.sort(fileList);
        return fileList;
    }

    public static List<String> getOrderedFilePathList(String dirPath) {
        return convertFileListToPathList(getOrderedFileList(dirPath));
    }

    public static String getTargetDir(Class clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static String extractFileName( String filePathName )
    {
        if ( filePathName == null )
            return null;

        int slashPos = filePathName.lastIndexOf( '\\' );
        if ( slashPos == -1 ) {
            slashPos = filePathName.lastIndexOf( '/' );
        }

        return filePathName.substring( slashPos > 0 ? slashPos + 1 : 0 );
    }

    public static String appendPath(String startingPath, String additionalSubPath) {
        if (startingPath.endsWith(getPathSeperator())) {
            startingPath = startingPath.substring(0,startingPath.length()-1);
        }
        return startingPath + getPathSeperator() + additionalSubPath;
    }
}
