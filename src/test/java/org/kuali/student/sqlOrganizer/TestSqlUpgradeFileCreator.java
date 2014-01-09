package org.kuali.student.sqlOrganizer;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSqlUpgradeFileCreator {

    @Test
    public void testGetFileList() {
        UpgradeCreationConfig config= getTestConfig();
        createTestFiles(config.getOrganizedSqlPath());
        List<String> filePathList = SqlUpgradeFileCreator.getFileListForSqlUpgrade(config);
    }

    @Test
    public void testCreateFile() {
        UpgradeCreationConfig config= getTestConfig();
        createTestFiles(config.getOrganizedSqlPath());
        SqlUpgradeFileCreator.createSqlUpgradeFile(config);

    }

    @Test
    public void testCreateAllSqlUpgradeFiles() {
        SqlUpgradeFileCreator.createAllSqlUpgradeFiles(SqlOrganizer.OUTPUT_DIR_PATH);
    }

    public static UpgradeCreationConfig getTestConfig() {
        List<String> milestones = new ArrayList<String>();
        milestones.add("M8");
        milestones.add("RC1");
        List<DatabaseDataType> dataTypes = new ArrayList<DatabaseDataType>();
        dataTypes.add(DatabaseDataType.STRUCTURE);
        dataTypes.add(DatabaseDataType.BOOTSTRAP);
        dataTypes.add(DatabaseDataType.MIGRATION);
        List<DatabaseModule> modules = new ArrayList<DatabaseModule>();
        modules.add(DatabaseModule.RICE);
        modules.add(DatabaseModule.KSCORE);
        modules.add(DatabaseModule.KSCM);
        modules.add(DatabaseModule.KSENR);
        String ouptupFileName = "M7.to.FR1.upgrade.script.sql";
        String organizedSqlPath = DirManipulationUtils.getTargetDir(TestSqlUpgradeFileCreator.class);

        return new UpgradeCreationConfig(milestones, dataTypes, modules, ouptupFileName, organizedSqlPath);
    }

    private void createTestFiles(String organizedSqlPath) {
        DirManipulationUtils.cleanOutputDir(new File(organizedSqlPath));

        // test directories
        // this first one should be skipped since M7 isn't in the milestone list
        File m7_structure_rice = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "M7" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.STRUCTURE.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.RICE.getEndsWith()));
        File m8_structure_rice = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "M8" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.STRUCTURE.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.RICE.getEndsWith()));
        File rc1_structure_rice = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "RC1" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.STRUCTURE.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.RICE.getEndsWith()));
        File m8_structure_kscore = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "M8" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.STRUCTURE.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.KSCORE.getEndsWith()));
        File m8_structure_kscm = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "M8" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.STRUCTURE.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.KSCM.getEndsWith()));
        File m8_structure_ksenr = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "M8" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.STRUCTURE.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.KSENR.getEndsWith()));
        File m8_bootstrap_rice = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "M8" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.BOOTSTRAP.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.RICE.getEndsWith()));
        File m8_migration_rice = new File(DirManipulationUtils.appendPath(organizedSqlPath,
                "M8" + DirManipulationUtils.getPathSeperator() +
                        DatabaseDataType.MIGRATION.toString() + DirManipulationUtils.getPathSeperator() +
                        DatabaseModule.RICE.getEndsWith()));

        DirManipulationUtils.mkDirCascaded(m7_structure_rice);
        DirManipulationUtils.mkDirCascaded(m8_structure_rice);
        DirManipulationUtils.mkDirCascaded(rc1_structure_rice);
        DirManipulationUtils.mkDirCascaded(m8_structure_kscore);
        DirManipulationUtils.mkDirCascaded(m8_structure_kscm);
        DirManipulationUtils.mkDirCascaded(m8_structure_ksenr);
        DirManipulationUtils.mkDirCascaded(m8_bootstrap_rice);
        DirManipulationUtils.mkDirCascaded(m8_migration_rice);

        // file list
        List<File> files = new ArrayList<File>();
        files.add(new File(DirManipulationUtils.appendPath(m7_structure_rice.getPath(), "0.sql")));
        files.add(new File(DirManipulationUtils.appendPath(m8_structure_rice.getPath(), "1.sql")));
        files.add(new File(DirManipulationUtils.appendPath(m8_structure_rice.getPath(), "2.sql")));
        files.add(new File(DirManipulationUtils.appendPath(m8_structure_kscore.getPath(), "3.sql")));
        files.add(new File(DirManipulationUtils.appendPath(m8_structure_kscm.getPath(), "4.sql")));
        files.add(new File(DirManipulationUtils.appendPath(m8_structure_ksenr.getPath(), "5.sql")));
        files.add(new File(DirManipulationUtils.appendPath(m8_bootstrap_rice.getPath(), "6.sql")));
        files.add(new File(DirManipulationUtils.appendPath(m8_migration_rice.getPath(), "7.sql")));
        files.add(new File(DirManipulationUtils.appendPath(rc1_structure_rice.getPath(), "8.sql")));

        try {
            for (int i=0; i<9; i++) {
                files.get(i).createNewFile();
                FileWriter fw = new FileWriter(files.get(i).getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("line " + i + "\n");
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
