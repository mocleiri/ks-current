package com.sigmasys.kuali.ksa.util;

import java.util.Properties;
import java.util.Set;

import com.sigmasys.kuali.ksa.annotation.AnnotationUtils;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

/**
 * DatabaseUtils contains DDL methods for creating/removing DB objects based on JPA annotations.
 *
 * @author Michael Ivanov
 */
public class DatabaseUtils {

    private DatabaseUtils() {
    }

    private static String getSql(String[] statements) {
        final StringBuilder sql = new StringBuilder();
        for (final String statement : statements) {
            sql.append(statement).append(";\n");
        }
        sql.append("\n");
        return sql.toString();
    }

    /**
     * Creates the SQL script to create all tables.
     *
     * @param em           JPA Entity Manager
     * @param packageNames array of package names where to look for annotated entities
     * @return A {@link String} representing the SQL script.
     */
    public static String generateCreateTablesSql(EntityManager em, String... packageNames) {
        Configuration configuration = getHibernateConfiguration(em, packageNames);
        final String[] statements = configuration.generateSchemaCreationScript(getDialect(em));
        return getSql(statements);
    }

    /**
     * Creates the SQL script to drop all tables.
     *
     * @param em           JPA Entity Manager
     * @param packageNames array of package names where to look for annotated entities
     * @return A {@link String} representing the SQL script.
     */
    public static String generateDropTablesSql(EntityManager em, String... packageNames) {
        Configuration configuration = getHibernateConfiguration(em, packageNames);
        final String[] statements = configuration.generateDropSchemaScript(getDialect(em));
        return getSql(statements);
    }

    public static Dialect getDialect(EntityManager em) {
        return getSessionFactoryImplementor(em).getDialect();
    }

    public static String getDefaultSchema(EntityManager em) {
        return getSessionFactoryImplementor(em).getSettings().getDefaultSchemaName();
    }

    public static SessionFactoryImplementor getSessionFactoryImplementor(EntityManager em) {
        return (SessionFactoryImplementor) ((Session) em.getDelegate()).getSessionFactory();
    }

    private static Configuration getHibernateConfiguration(EntityManager em, String... packageNames) {
        Set<Class> annotatedClasses = AnnotationUtils.findAnnotatedClasses(Entity.class, packageNames);
        Configuration configuration = new Configuration();
        for (final Class entityClass : annotatedClasses) {
            configuration.addAnnotatedClass(entityClass);
        }
        Properties props = getSessionFactoryImplementor(em).getProperties();
        configuration.setProperties(props);
        return configuration;
    }

}
