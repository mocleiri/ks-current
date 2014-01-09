package org.kuali.student.sqlOrganizer;

import org.junit.Test;

import java.io.IOException;

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

        UpgradeCreationConfig config = TestSqlUpgradeFileCreator.getTestConfig();
        SqlUpgradeFileCreator.createSqlUpgradeFile(config);

    }
}
