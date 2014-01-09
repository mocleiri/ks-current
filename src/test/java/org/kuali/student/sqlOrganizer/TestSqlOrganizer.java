package org.kuali.student.sqlOrganizer;



import com.akiban.sql.*;
import com.akiban.sql.parser.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.Base64;
import org.junit.*;
import org.kuali.common.jdbc.reader.DefaultSqlReader;
import org.kuali.common.jdbc.reader.SqlReader;
import org.kuali.common.jdbc.reader.model.Comments;
import org.kuali.common.jdbc.reader.model.Delimiter;
import org.kuali.common.jdbc.reader.model.LineSeparator;
import org.kuali.common.util.LocationUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: larry
 * Date: 8/30/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSqlOrganizer {
    SqlOrganizer sqlOrganizer;

    @Test
    public void testGetTablesNames()  {
        String sql = "SELECT * FROM MY_TABLE1, MY_SCHEMA.MY_TABLE2, (SELECT * FROM MY_TABLE3) AS TABLE_3" +
                " LEFT OUTER JOIN MY_TABLE4 ON TABLE_3.ID=MYTABLE4.ID2"+
                " WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6)\n/ \n" +
                "INSERT INTO KRMS_TABLE VALUES ('VAL_1', 'VAL_2', '', null)\n       /\n" +
                "UPDATE KSEN_TABLE set COL_1=true\n/\n" +
                "           DELETE FROM KSLU_TABLE WHERE ID IN (SELECT CLU_ID FROM KSEN_TABLE WHERE KSEN_COL>1)\n/";

        // split based on / on it's own line (with any whitespace before and after and a newline or EOF)
        // broken up with + for clarity
        String[] statements = sqlOrganizer.splitStatements(sql);

        assertTrue(statements.length == 4);
        checkStatement(statements[0],6);
        checkStatement(statements[1],1);
        checkStatement(statements[2],1);
        checkStatement(statements[3],2);

    }

    private void checkStatement(String statement, int expectedNumTables) {
        System.out.println(statement.trim());
        StatementInfo statementInfo = sqlOrganizer.getStatementInfoForStatement(statement);

        assertTrue(statementInfo.getTableNames().size() == expectedNumTables);
        if (statementInfo.getStatementType() == StatementType.DDL) {
            System.out.print("Structure");
        } else if (statementInfo.getStatementType() == StatementType.DML) {
            System.out.print("Data");
        } else {
            System.out.print("Unknown Statement Category");
        }

        System.out.println(statementInfo.getTableNames().toString()+"\n");
    }

    @Before
    public void init() {
        sqlOrganizer = new SqlOrganizer();
        sqlOrganizer.init();
    }

    @After
    public void printSummary() {
        if (sqlOrganizer.unparsableStmts.size() > 0) {
            System.out.println("\n\nUnparsable Statements");
            StringBuilder sbStmts = new StringBuilder();
            for (String stmt : sqlOrganizer.unparsableStmts) {
                sbStmts.append(stmt + "\n/\n");
            }
            System.out.println(sbStmts);
            System.out.println("\nNumber of Unparsable Statements: " + sqlOrganizer.unparsableStmts.size());
        }

    }


    @Test
    public void testSingle() throws IOException {
        sqlOrganizer.module = "enroll-sql";
        sqlOrganizer.processSqlFile("single.sql");
    }

    @Test
    public void testUnparsables() throws IOException {
        sqlOrganizer.module = "enroll-sql";
        sqlOrganizer.processSqlFile("unparsables.sql");
    }

    @Test
    public void testAggregateFiles() throws IOException {
        sqlOrganizer.organizeAggregateFiles();
    }



    @Test
    public void testExtractMilestone() {
        String correctWindowsPath = "gobletygook....asdlkjsdalkjoqweuwerlk\\asdasdfds\\Milestone.1\\file.name.txt";
        String correctLinuxPath = "gobletygook....asdlkjsdalkjoqweuwerlk/asdasdfds/Milestone.1/file.name.txt";
        String incorrectWindowsPath = "Milestone.1\\file.name.txt";
        String incorrectLinuxPath = "Milestone.1/file.name.txt";

        assertEquals("NO_MILESTONE_IN_PATH", sqlOrganizer.extractMilestone(incorrectLinuxPath));
        assertEquals("NO_MILESTONE_IN_PATH", sqlOrganizer.extractMilestone(incorrectWindowsPath));
        assertEquals("Milestone.1", sqlOrganizer.extractMilestone(correctLinuxPath));
        assertEquals("Milestone.1", sqlOrganizer.extractMilestone(correctWindowsPath));

    }

        @Test
    public void testSkipFile() throws IOException {
        Map<DatabaseModule, List<String>> locationMap = new HashMap<DatabaseModule, List<String>>();
        SqlReader sqlReader = sqlOrganizer.getSqlReader();
        BufferedReader reader = LocationUtils.getBufferedReader("C:\\data\\development\\intellijProjects\\enr-aggregate\\ks-enroll\\ks-enroll-sql\\src\\main\\resources\\org\\kuali\\student\\ks-enroll-sql\\upgrades\\M6\\2013-01-28-KSEN-update-COs-clu-version.sql");

        String statement = sqlReader.getSql(reader);
        assertTrue(ControlMappingUtils.skipSqlOrganization(statement));
    }

    @Test
    public void testGetModule() {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("KSLU_TABLE1");
        tableNames.add("KSLU_TABLE2");
        tableNames.add("KSLU_TABLE3");
        tableNames.add("KSLO_TABLE1");
        tableNames.add("KSLO_TABLE2");
        Assert.assertTrue(DatabaseModule.KSCM.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.KSCM)));
        Assert.assertTrue(DatabaseModule.KSCM.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.KSENR)));

        tableNames.clear();
        tableNames.add("KSXX_TABLE1");
        tableNames.add("KSXX_TABLE2");
        tableNames.add("KSXX_TABLE3");
        Assert.assertTrue(DatabaseModule.KSCORE.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.KSCORE)));
        Assert.assertTrue(DatabaseModule.KSCORE.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.KSENR)));

        tableNames.clear();
        tableNames.add("KRXX_TABLE1");
        tableNames.add("KRXX_TABLE2");
        tableNames.add("KRXX_TABLE3");
        Assert.assertTrue(DatabaseModule.RICE.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.RICE)));
        Assert.assertTrue(DatabaseModule.RICE.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.KSENR)));

        tableNames.clear();
        tableNames.add("KRXX_TABLE1");
        tableNames.add("KSXX_TABLE2");
        Assert.assertTrue(DatabaseModule.EXCEPTION.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.RICE)));
        Assert.assertTrue(DatabaseModule.EXCEPTION.equals(sqlOrganizer.getModule(tableNames, DatabaseModule.KSCORE)));

    }

    @After
    public void removeEmptyOutputDirs() {
        sqlOrganizer.removeEmptyOutputDirs();
    }



}
