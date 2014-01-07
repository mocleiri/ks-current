package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.annotation.AnnotationUtils;
import com.sigmasys.kuali.ksa.annotation.Auditable;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import com.sigmasys.kuali.ksa.util.LongIdGenerator;
import com.sigmasys.kuali.ksa.util.RequestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Audit trail interceptor for Hibernate.
 * Contains all the logic of capturing and storing changes to JPA entities.
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("serial")
public class AuditTrailInterceptor extends EmptyInterceptor {

    private static final Log logger = LogFactory.getLog(AuditTrailInterceptor.class);

    private static final int MAX_VALUE_LENGTH = 4000;

    private EntityManager entityManager;

    protected EntityManagerFactory findEntityManagerFactory(String unitName) {
        ListableBeanFactory beanFactory = (ListableBeanFactory) ContextUtils.getBeanFactory();
        if (StringUtils.hasLength(unitName)) {
            // See whether we can find an EntityManagerFactory with matching persistence unit name.
            String[] candidateNames =
                    BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, EntityManagerFactory.class);
            for (String candidateName : candidateNames) {
                EntityManagerFactory emf = (EntityManagerFactory) beanFactory.getBean(candidateName);
                if (emf instanceof EntityManagerFactoryInfo) {
                    if (unitName.equals(((EntityManagerFactoryInfo) emf).getPersistenceUnitName())) {
                        return emf;
                    }
                }
            }
            // No matching persistence unit found - simply take the EntityManagerFactory
            // with the persistence unit name as bean name (by convention).
            return beanFactory.getBean(unitName, EntityManagerFactory.class);
        } else {
            return BeanFactoryUtils.beanOfType(beanFactory, EntityManagerFactory.class);
        }
    }

    /**
     * This method can be overridden by subclasses to return a different instance of
     * Spring's EntityManagerFactory.
     *
     * @return EntityManagerFactory instance
     */
    protected EntityManagerFactory getEntityManagerFactory() {
        return findEntityManagerFactory(Constants.KSA_PERSISTENCE_UNIT);
    }

    /**
     * Lazily initializes the entity manager and stores it for obtaining the same instance in the future
     *
     * @return EntityManager instance
     */
    protected synchronized EntityManager getEntityManager() {
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = getEntityManagerFactory().createEntityManager();
        }
        return entityManager;
    }

    protected Session getSession() {
        return (Session) getEntityManager().getDelegate();
    }

    private void onCollectionCreateRemove(Object collectionObject, Serializable id, boolean isCreate) {

        if (collectionObject instanceof PersistentCollection) {

            PersistentCollection collection = (PersistentCollection) collectionObject;

            Auditable auditable = AnnotationUtils.getAnnotation(Auditable.class, collection.getOwner());

            if (auditable != null) {

                if (collection instanceof Collection<?>) {

                    Session session = getSession();

                    String userId = getUserId();

                    Collection<?> javaCollection = (Collection<?>) collection;
                    String propertyName = getPropertyName(collection);
                    Class ownerClass = collection.getOwner().getClass();
                    for (Object item : javaCollection) {
                        String newValue = isCreate ? getPropertyValue(item) : null;
                        String oldValue = isCreate ? null : getPropertyValue(item);
                        String logDetail = isCreate ? "Persistent collection create" : "Persistent collection remove";
                        createActivityLog(session, id, ownerClass, userId, item, propertyName,
                                oldValue, newValue, logDetail);
                    }

                    session.flush();
                }
            }
        }
    }

    @Override
    public void onCollectionRecreate(Object collectionObject, Serializable id) throws CallbackException {
        onCollectionCreateRemove(collectionObject, id, true);
    }

    @Override
    public void onCollectionRemove(Object collectionObject, Serializable id) throws CallbackException {
        onCollectionCreateRemove(collectionObject, id, false);
    }


    /**
     * This method is called if entries were added or removed from a collection
     * But Hibernate is not very smart, and can call this method even if there are no changes to be persisted,
     * so we have special logic to check and filter that out
     */
    @Override
    public void onCollectionUpdate(Object collectionObject, Serializable id) throws CallbackException {

        if (collectionObject instanceof AbstractPersistentCollection) {

            AbstractPersistentCollection collection = (AbstractPersistentCollection) collectionObject;

            if (logger.isDebugEnabled()) {
                logger.debug("Checking if auditing should be done for collection " + collection.getRole() +
                        " of " + collection.getOwner());
            }

            if (!collection.wasInitialized()) {
                logger.debug("Skipping audit because collection was not initialized");
                return;
            }

            Auditable auditable = AnnotationUtils.getAnnotation(Auditable.class, collection.getOwner());

            if (auditable != null) {

                // Collection changes should be audited
                SessionFactoryImplementor factory = collection.getSession().getFactory();
                CollectionPersister collectionPersister = factory.getCollectionPersister(collection.getRole());

                @SuppressWarnings("rawtypes")
                Iterator entries = collection.entries(collectionPersister);
                Type elemType = collectionPersister.getElementType();

                Session session = getSession();

                String userId = getUserId();

                String propertyName = getPropertyName(collection);

                Class ownerClass = collection.getOwner().getClass();

                for (int i = 0; entries.hasNext(); i++) {
                    Object entry = entries.next();
                    if (collection.needsInserting(entry, i, elemType)) {
                        String newValue = getPropertyValue(entry);
                        createActivityLog(session, id, ownerClass, userId, entry, propertyName,
                                null, newValue, "Persistent collection insert");
                    }
                }

                boolean deleteByIndex = !collectionPersister.isOneToMany() && collectionPersister.hasIndex();
                entries = collection.getDeletes(collectionPersister, !deleteByIndex);
                while (entries.hasNext()) {
                    Object entry = entries.next();
                    String oldValue = getPropertyValue(entry);
                    createActivityLog(session, id, ownerClass, userId, entry, propertyName,
                            oldValue, null, "Persistent collection remove");
                }

                session.flush();
            }
        }
    }

    /**
     * Returns bean property name that represents given collection
     * Hibernate role stores fully qualified name, so we have to clip it out
     *
     * @param collection PersistentCollection instance
     * @return a simple property name such as "groups"
     */
    private String getPropertyName(PersistentCollection collection) {

        String propertyName = collection.getRole();

        if (propertyName != null) {

            // Hibernate role is <className>.<propertyName>
            int lastDot = propertyName.lastIndexOf('.');

            if (lastDot != -1) {
                propertyName = propertyName.substring(lastDot + 1);
            }

        } else if (collection.getOwner() != null) {

            // No help from Hibernate, so try to figure out property name using reflection
            Object owner = collection.getOwner();
            PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(owner.getClass());

            for (PropertyDescriptor property : properties) {
                try {
                    if (property.getReadMethod() != null) {
                        Object value = property.getReadMethod().invoke(owner);
                        if (collection == value || collection.equals(value)) {
                            // Found
                            propertyName = property.getName();
                            break;
                        }
                    }
                } catch (Exception x) {
                    logger.warn("Failed to lookup property name for property descriptor " + property, x);
                }
            }
        }

        if (propertyName == null) {
            throw new IllegalStateException("Failed to determine property name for collection " + collection +
                    ", owner = " + collection.getOwner());
        }

        return propertyName;
    }

    protected void createActivityLog(Session session, Serializable id, Class entityClass,
                                     String userId, Object entry, String propertyName,
                                     String oldValue, String newValue, String logDetail) {

        Activity activity = new Activity();
        activity.setId(LongIdGenerator.generateLong());
        activity.setEntityId(id.toString());
        activity.setEntityType(entityClass.getSimpleName());
        activity.setCreatorId(userId);
        activity.setIpAddress(RequestUtils.getClientIpAddress());
        activity.setLogDetail(logDetail);

        if (oldValue != null && oldValue.length() > MAX_VALUE_LENGTH) {
            oldValue = oldValue.substring(0, MAX_VALUE_LENGTH - 1);
        }

        if (newValue != null && newValue.length() > MAX_VALUE_LENGTH) {
            newValue = newValue.substring(0, MAX_VALUE_LENGTH - 1);
        }

        activity.setEntityProperty(propertyName);
        activity.setOldAttribute(oldValue);
        activity.setNewAttribute(newValue);

        activity.setTimestamp(new Date());

        if (entry instanceof AccountIdAware) {
            activity.setAccountId(((AccountIdAware) entry).getAccountId());
        }

        activity.setTypeId(Constants.ACTIVITY_TYPE_DATA_CHANGE_ID);

        session.persist(activity);
    }


    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        Auditable auditable = AnnotationUtils.getAnnotation(Auditable.class, entity);

        if (auditable != null) {

            Session session = getSession();

            for (int i = 0; i < state.length; i++) {

                if (state[i] instanceof Collection<?>) {
                    // Updates to collections are tracked through different methods
                    continue;
                }

                // We do not want to see some strange state[i] in ATR table
                if (propertyNames[i].startsWith("_") && types[i] instanceof EmbeddedComponentType) {
                    continue;
                }

                String oldValue = getPropertyValue(state[i]);

                if (oldValue == null) {
                    // Nothing to save -- null to null
                    continue;
                }

                createActivityLog(session, id, entity.getClass(), getUserId(), entity, propertyNames[i], oldValue,
                        null, "Persistent entity delete");

            }

            session.flush();
        }
    }

    /**
     * This method is called when entity has changes
     */
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {

        Auditable auditable = AnnotationUtils.getAnnotation(Auditable.class, entity);

        if (auditable != null) {

            Session session = getSession();

            for (int i = 0; i < currentState.length; i++) {

                if (currentState[i] instanceof Collection<?>) {
                    // Updates to collections are tracked through different methods
                    continue;
                }

                // We do not want to see some strange state[i] in ATR table
                if (propertyNames[i].startsWith("_") && types[i] instanceof EmbeddedComponentType) {
                    continue;
                }

                if (!objectsMatch(currentState[i], previousState[i])) {

                    String newValue = getPropertyValue(currentState[i]);
                    String oldValue = getPropertyValue(previousState[i]);

                    if (objectsMatch(newValue, oldValue)) {
                        // Nothing to save -- they are equal or null
                        continue;
                    }

                    createActivityLog(session, id, entity.getClass(), getUserId(), entity, propertyNames[i], oldValue,
                            newValue, "Persistent entity update");

                }
            }

            session.flush();
        }
        return false;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        Auditable auditable = AnnotationUtils.getAnnotation(Auditable.class, entity);

        if (auditable != null) {

            Session session = getSession();

            // Insert audit trail records for each property
            for (int i = 0; i < state.length; i++) {

                if (state[i] instanceof Collection<?>) {
                    // Updates to collections are tracked through different methods
                    continue;
                }

                // We do not want to see some strange state[i] in ATR table
                if (propertyNames[i].startsWith("_") && types[i] instanceof EmbeddedComponentType) {
                    continue;
                }

                String newValue = getPropertyValue(state[i]);

                if (newValue == null) {
                    //nothing changed -- null to null 
                    continue;
                }

                createActivityLog(session, id, entity.getClass(), getUserId(), entity, propertyNames[i], null,
                        newValue, "Persistent entity create");
            }

            session.flush();
        }
        return false;
    }

    protected String getPropertyValue(Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof Identifiable) {
            // For entities use their id instead of toString()
            Serializable objectId = ((Identifiable) object).getId();
            return (objectId != null) ? objectId.toString() : null;
        }

        return (object instanceof Date) ? getFormattedDate((Date) object) : object.toString();
    }

    protected String getFormattedDate(Date date) {
        return new SimpleDateFormat(Constants.TIMESTAMP_FORMAT_NO_MS).format(date);
    }

    private boolean objectsMatch(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    private String getUserId() {
        HttpServletRequest request = RequestUtils.getThreadRequest();
        if (request != null) {
            UserSessionManager sessionManager = ContextUtils.getBean(UserSessionManager.class);
            return sessionManager.getUserId(request);
        }
        return null;
    }

}
