package org.kuali.student.sqlOrganizer;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.AlterTableNode;
import com.akiban.sql.parser.DDLStatementNode;
import com.akiban.sql.parser.DMLModStatementNode;
import com.akiban.sql.parser.DeleteNode;
import com.akiban.sql.parser.DropIndexNode;
import com.akiban.sql.parser.DropSequenceNode;
import com.akiban.sql.parser.DropTableNode;
import com.akiban.sql.parser.FromTable;
import com.akiban.sql.parser.InsertNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 9/6/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class SqlParserNodeVisitor implements Visitor {
    private boolean onFirst = true;
    private List<String> tableNames;

    private StatementType statementType;

    public void traverse(Visitable node) throws StandardException {
        tableNames = new ArrayList<String>();
        node.accept(this);
    }

    public List<String> getTableNames() throws StandardException {
        return tableNames;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public Visitable visit(AlterTableNode alterTableNode) throws StandardException {
        setStatementType(StatementType.DDL);
        if (alterTableNode.getObjectName() != null) {
            tableNames.add(alterTableNode.getObjectName().getTableName().toUpperCase());
        }
        return alterTableNode;
    }

    public Visitable visit(FromTable fromTable) throws StandardException {
        if (fromTable.getOrigTableName() != null) {
            tableNames.add(fromTable.getOrigTableName().getTableName().toUpperCase());
        }
        return fromTable;
    }

    public Visitable visit(DDLStatementNode node) {
        setStatementType(StatementType.DDL);
        return node;
    }

    public Visitable visit(DMLModStatementNode node) {
        setStatementType(StatementType.DML);
        return node;
    }

    // simple insert does not have have FromTable element
    public Visitable visit(InsertNode insertNode) {
        setStatementType(StatementType.DML);
        if (insertNode.getTargetTableName() != null) {
            tableNames.add(insertNode.getTargetTableName().getTableName().toUpperCase());
        }
        return insertNode;
    }

    private void setStatementType(StatementType statementType) {
        // only do this on the parent statement
        if(onFirst) {
            this.statementType = statementType;
            onFirst = false;
        }
    }

    private Method getPolymorphicMethod(Visitable node) {
        Class cl = node.getClass();  // the bottom-most class
        // Check through superclasses for matching method
        while(!cl.equals(Object.class)) {
            try {
                return this.getClass().getDeclaredMethod("visit", new Class[] { cl });
            } catch(NoSuchMethodException ex) {
                cl = cl.getSuperclass();
            }
        }
        // Check through interfaces for matching method
        Class[] interfaces = node.getClass().getInterfaces();
        for (int i=0; i<interfaces.length; i++) {
            try {
                return this.getClass().getDeclaredMethod("visit", new Class[] { interfaces[i] });
            } catch(NoSuchMethodException ex) {
            }
        }
        return null;
    }

    @Override
    public Visitable visit(Visitable node) throws StandardException {

        Method downPolymorphic = getPolymorphicMethod(node);
        if (downPolymorphic != null) {
            try {
                downPolymorphic.invoke(this, new Object[] {node});
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return node;
    }

    @Override
    public boolean visitChildrenFirst(Visitable node) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean stopTraversal() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean skipChildren(Visitable node) throws StandardException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
