package org.kuali.student.sqlOrganizer;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDirManipulations {
    public static final String TEST_OUTPUT_DIR_PATH = DirManipulationUtils.getTargetDir(TestDirManipulations.class);

    @Test
    public void testDirManipulations() {

        List<File> dirs = new ArrayList<File>();
        System.out.println("Testing dir manipulations in " + TEST_OUTPUT_DIR_PATH);
        dirs.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1"));
        dirs.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1/dir1.1"));
        dirs.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1/dir1.2"));

        List<File> postDelDirs = new ArrayList<File>();
        dirs.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder"));
        dirs.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir2"));
        dirs.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir2/dir2.1"));
        dirs.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir2/dir2.2"));

        dirs.addAll(postDelDirs);

        List<File> files = new ArrayList<File>();
        files.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1/dir1.1/file1"));
        files.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1/dir1.1/file2"));
        files.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1/dir1.1/file3"));
        files.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1/dir1.1/file4"));

        List<File> postDelFiles = new ArrayList<File>();
        postDelFiles.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir2/file5"));
        postDelFiles.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir2/file6"));
        postDelFiles.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir2/file7"));
        postDelFiles.add(new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir2/file8"));

        files.addAll(postDelFiles);

        // create new directories
        for (File dir : dirs) {
            DirManipulationUtils.cleanOutputDir(dir);
        }

        // add files to directoriess
        for (File file : files) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // verify new directories
        for (File dir : dirs) {
            Assert.assertTrue(dir.isDirectory());
        }

        // verify new files
        for (File file : files) {
            Assert.assertTrue(file.exists());
        }

        //test cascade delete directory
        File delDir = new File(TEST_OUTPUT_DIR_PATH + "/testFolder/dir1");
        DirManipulationUtils.cascadeDelDir(delDir);
        Assert.assertFalse(delDir.exists());
        for (File dir : postDelDirs) {
            Assert.assertTrue(dir.isDirectory());
        }


        //test cleanOutputDir
        File rootDir = new File(TEST_OUTPUT_DIR_PATH + "/testFolder");
        DirManipulationUtils.cleanOutputDir(rootDir);
        for (File file : postDelFiles) {
            Assert.assertFalse(file.exists());
        }

        DirManipulationUtils.cascadeDelDir(rootDir);
        Assert.assertFalse(rootDir.exists());
    }

}
