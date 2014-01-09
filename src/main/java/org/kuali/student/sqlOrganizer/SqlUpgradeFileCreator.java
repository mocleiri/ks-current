package org.kuali.student.sqlOrganizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlUpgradeFileCreator {

    public static List<String> getFileListForSqlUpgrade(UpgradeCreationConfig config) {
       List<String> filePathList = new ArrayList<String>();
       for(String milestone : config.getMilestones()) {
           for (DatabaseDataType dataType : config.getTypes()) {
               for (DatabaseModule module : config.getModules()) {
                   String path = DirManipulationUtils.appendPath(config.getOrganizedSqlPath(),
                           milestone + DirManipulationUtils.getPathSeperator() +
                                   dataType + DirManipulationUtils.getPathSeperator() +
                                   module.getEndsWith());
                   if (DirManipulationUtils.dirExists(path)) {
                       filePathList.addAll(DirManipulationUtils.getOrderedFilePathList(path));
                   }
               }
           }
       }
       return filePathList;
    }

    public static void createAllSqlUpgradeFiles (String organizedSqlPath) {
        List<DatabaseDataType> dataTypes = new ArrayList<DatabaseDataType>();
        List<String> milestones = new ArrayList<String>();

        for(File milestoneDir : DirManipulationUtils.getOrderedDirList(organizedSqlPath)) {
            String milestone = DirManipulationUtils.extractFileName(milestoneDir.getPath());
            milestones.add(milestone);
            for(File dataTypeDir : DirManipulationUtils.getOrderedDirList(milestoneDir.getPath())) {
                DatabaseDataType dataType = DatabaseDataType.getDatabaseDataType(DirManipulationUtils.extractFileName(dataTypeDir.getPath()));
                if (!dataTypes.contains(dataType)) {
                    dataTypes.add(dataType);
                }
                UpgradeCreationConfig config = new UpgradeCreationConfig(
                        Arrays.asList(new String[] { milestone } ),
                        Arrays.asList(new DatabaseDataType[] { dataType } ),
                        Arrays.asList(DatabaseModule.values()),
                        milestone + "." + dataType.toString() + ".upgrade.sql",
                        organizedSqlPath);
                createSqlUpgradeFile(config);
            }
        }

        for(DatabaseDataType dataType : dataTypes) {
            UpgradeCreationConfig config = new UpgradeCreationConfig(
                    milestones,
                    Arrays.asList(new DatabaseDataType[] { dataType } ),
                    Arrays.asList(DatabaseModule.values()),
                    "All.Milestones." + dataType.toString() + ".upgrade.sql",
                    organizedSqlPath);
            createSqlUpgradeFile(config);
        }

        milestones.clear();
        milestones.add("RC1");
        for (DatabaseDataType type : DatabaseDataType.values()) {
            dataTypes.clear();
            dataTypes.add(type);
            ArrayList<DatabaseModule>  modules = new ArrayList<DatabaseModule> ();
            modules.add(DatabaseModule.RICE);
            UpgradeCreationConfig config =
                    new UpgradeCreationConfig(milestones, dataTypes, modules, "RC1.Rice." + type.toString() + ".upgrade.sql", organizedSqlPath);
            createSqlUpgradeFile(config);

            modules.clear();
            modules.add(DatabaseModule.KSCORE);
            modules.add(DatabaseModule.KSCM);
            modules.add(DatabaseModule.KSENR);
            config = new UpgradeCreationConfig(milestones, dataTypes, modules, "RC1.Student." + type.toString() + ".upgrade.sql", organizedSqlPath);
            createSqlUpgradeFile(config);
        }
    }


    public static void createSqlUpgradeFile(UpgradeCreationConfig config) {
        List<String> filePaths = getFileListForSqlUpgrade(config);
        PrintWriter out = null;
        if (!filePaths.isEmpty()) {
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(DirManipulationUtils.appendPath(config.getOrganizedSqlPath(), config.getOuptupFileName()), true)));
                System.out.println("created file " + config.getOuptupFileName());
            } catch (IOException e) {
                System.out.println("error opening file " + config.getOuptupFileName());
            }
            out.println("-- Upgrade file info\n" +
                        "--\n" +
                        "-- This file is an aggregation of sql files.  They are ordered by milestone, then module then \n" +
                        "-- filename. When files are out of order it is because the next module is loaded.  Milestone \n" +
                        "-- and Module precedence is set via the UpgradeCreationConfig or in the case where you're using\n"+
                        "-- createAllSqlUpgradeFiles the milestone order is based on alphanumeric sort and module order\n" +
                        "-- is based on the order of the values in\n" +
                        "-- DatabaseModule\n\n");

            for (String filePath : filePaths) {
                BufferedReader br = null;
                try {
                    String sCurrentLine;
                    br = new BufferedReader(new FileReader(filePath));
                    out.println("--\n-- File Header: " + DirManipulationUtils.extractFileName(filePath) + "\n--\n\n");
                    while ((sCurrentLine = br.readLine()) != null) {
                        out.println(sCurrentLine);
                    }
                    out.println("\n\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null)br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            out.close();
        }
    }
}
