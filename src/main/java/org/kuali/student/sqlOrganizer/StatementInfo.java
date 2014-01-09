package org.kuali.student.sqlOrganizer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/27/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatementInfo {
    private List<String> tableNames;
    private StatementType statementType;

    public StatementInfo(List<String> tableNames, StatementType statementType) {
        this.tableNames = tableNames;
        this.statementType = statementType;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public StatementType getStatementType() {
        return statementType;
    }
}
