package org.kuali.student.sqlOrganizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpgradeCreationConfig {
    private List<String> milestones;
    private List<DatabaseDataType> types;
    private List<DatabaseModule> modules;
    private String ouptupFileName;
    private String organizedSqlPath;
    private enum operatingSystem {WINDOWS, LINUX, OSX};

    public UpgradeCreationConfig(List<String> milestones, List<DatabaseDataType> types, List<DatabaseModule> modules, String ouptupFileName, String organizedSqlPath) {
        this.milestones = milestones;
        this.types = types;
        this.modules = modules;
        this.ouptupFileName = ouptupFileName;
        this.organizedSqlPath = organizedSqlPath;
    }

    public List<String> getMilestones() {
        return milestones;
    }

    public List<DatabaseDataType> getTypes() {
        return types;
    }

    public List<DatabaseModule> getModules() {
        return modules;
    }

    public String getOuptupFileName() {
        return ouptupFileName;
    }

    public String getOrganizedSqlPath() {
        return organizedSqlPath;
    }
}
