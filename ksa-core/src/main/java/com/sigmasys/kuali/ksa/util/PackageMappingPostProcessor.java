package com.sigmasys.kuali.ksa.util;

import com.sigmasys.kuali.ksa.annotation.AnnotationUtils;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import javax.persistence.Entity;
import java.util.Collections;
import java.util.Set;

/**
 * PackageMappingPostProcessor is used to narrow the JPA scan scope.
 *
 * @author Michael Ivanov
 */
public class PackageMappingPostProcessor implements PersistenceUnitPostProcessor {

    private String[] packages;
    private Set<String> excludePackages = Collections.emptySet();
    private Set<String> excludeClasses = Collections.emptySet();

    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {

        Set<Class> annotatedClasses = AnnotationUtils.findAnnotatedClasses(Entity.class, packages);

        for (Class entityClass : annotatedClasses) {
            String className = entityClass.getName();
            if (!isExcludedClass(className)) {
                pui.addManagedClassName(className);
            }
        }

        pui.setExcludeUnlistedClasses(true);
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }

    public void setExcludePackages(Set<String> excludePackages) {
        this.excludePackages = (excludePackages != null) ? excludePackages : Collections.<String>emptySet();
    }

    public void setExcludeClasses(Set<String> excludeClasses) {
        this.excludeClasses = (excludeClasses != null) ? excludeClasses : Collections.<String>emptySet();
    }

    private boolean isExcludedClass(String className) {

        if (excludeClasses.contains(className)) {
            return true;
        }

        for (String packageName : excludePackages) {
            if (className.startsWith(packageName)) {
                return true;
            }
        }

        return false;
    }
}