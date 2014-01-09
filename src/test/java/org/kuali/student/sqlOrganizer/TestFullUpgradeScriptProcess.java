package org.kuali.student.sqlOrganizer;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 1/9/14
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestFullUpgradeScriptProcess {

    @Test
    public void testUpgradeProcess() throws IOException {
        SqlOrganizer sqlOrganizer = new SqlOrganizer();
        sqlOrganizer.init();
        sqlOrganizer.organizeAggregateFiles();

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
        String organizedSqlPath = SqlOrganizer.OUTPUT_DIR_PATH;

        UpgradeCreationConfig config = new UpgradeCreationConfig(milestones, dataTypes, modules, ouptupFileName, organizedSqlPath);
        SqlUpgradeFileCreator.createSqlUpgradeFile(config);

    }
}
