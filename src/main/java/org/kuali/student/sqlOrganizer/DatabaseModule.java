package org.kuali.student.sqlOrganizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public enum DatabaseModule {
    // ENUM(module.endsWith)
    RICE("Rice", "rice-sql"),
    KSCORE("KS Core", "core-sql"),
    KSCM("KS Curriculum Managment", "lum-sql"),
    KSENR("KS Enrollment", "enroll-sql"),
    EXCEPTION("UNKNOWN", "exception-sql");

    private final String label;
    private final String endsWith;

    private DatabaseModule(String label, String endsWith) {
        this.label = label;
        this.endsWith = endsWith;
    }

    public String getEndsWith() {
        return endsWith;
    }

    public String getLabel() {
        return label;
    }

    public static DatabaseModule getDatabaseModule(String dirPath) {
        for (DatabaseModule moduleIter : DatabaseModule.values()) {
            if (dirPath.endsWith(moduleIter.getEndsWith())) {
                return moduleIter;
            }
        }
        return null;
    }

    public static List<DatabaseModule> getDatabaseModuleList(List<File> dirs) {
        List<DatabaseModule> modules = new ArrayList<DatabaseModule>();

        for (File dir : dirs) {
            DatabaseModule module = getDatabaseModule(dir.getPath());
            if (module != null) {
                modules.add(module);
            }
        }
        return modules;
    }
}
