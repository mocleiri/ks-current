package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.model.Activity;
import com.sigmasys.kuali.ksa.model.ActivityType;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.service.ActivityService;
import com.sigmasys.kuali.ksa.util.ActivityIdGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Activity Service implementation
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("activityService")
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class ActivityServiceImpl extends GenericPersistenceService implements ActivityService {

    protected static final int UNLIMITED_ITEMS_NUMBER = -1;


    private List<Activity> getActivities(Long id) {
        Query query = em.createQuery("select act from Activity act " +
                //" left outer join fetch act.type t " +
                ((id != null) ? " where act.id = :id " : "") +
                " order by act.timestamp desc");
        if (id != null) {
            query.setParameter("id", id);
        }
        return query.getResultList();
    }

    /**
     * Returns the default activity type.
     *
     * @return Activity Type instance
     */
    @Override
    public ActivityType getDefaultActivityType() {
        return getActivityType(Constants.ACTIVITY_TYPE_DATA_CHANGE_ID);
    }

    /**
     * Returns the activity type by code.
     *
     * @param code Activity type code
     * @return Activity Type instance
     */
    @Override
    public ActivityType getActivityType(String code) {
        Query query = em.createQuery("select a from ActivityType a where a.code = :code");
        query.setParameter("code", code);
        List<ActivityType> activityTypes = query.getResultList();
        return CollectionUtils.isNotEmpty(activityTypes) ? activityTypes.get(0) : null;
    }

    /**
     * Returns the activity type by ID.
     *
     * @param id Activity type ID
     * @return Activity Type instance
     */
    @Override
    public ActivityType getActivityType(Long id) {
        return getEntity(id, ActivityType.class);
    }

    /**
     * Returns Activity by ID
     *
     * @param id Activity ID
     * @return Activity instance
     */
    @Override
    public Activity getActivity(Long id) {
        List<Activity> activities = getActivities(id);
        return CollectionUtils.isNotEmpty(activities) ? activities.get(0) : null;
    }

    /**
     * Returns all activities sorted by ID in the descendant order
     *
     * @return List of activities
     */
    @Override
    public List<Activity> getActivities() {
        return getActivities(null, UNLIMITED_ITEMS_NUMBER);
    }

    /**
     * Returns the specified number of activities sorted by ID in the descendant order
     *
     * @return List of activities
     */
    public List<Activity> getActivities(int limit) {
        return getActivities(null, limit);
    }

    /**
     * Returns all activities sorted by ID in the descendant order for the given Account ID.
     *
     * @param userId Account ID
     * @return List of activities
     */
    @Override
    public List<Activity> getActivities(String userId) {
        return getActivities(userId, UNLIMITED_ITEMS_NUMBER);
    }

    /**
     * Returns the specified number of activities sorted by ID in the descendant order for the given Account ID.
     *
     * @param userId Account ID
     * @return List of activities
     */
    public List<Activity> getActivities(String userId, int limit) {
        Query query = em.createQuery("select act from Activity act " +
                //" left outer join fetch act.type t " +
                ((userId != null) ? " where act.creatorId = :userId " : "") +
                " order by act.timestamp desc");

        if (userId != null) {
            query.setParameter("userId", userId);
        }

        if (limit != UNLIMITED_ITEMS_NUMBER) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    /**
     * Persists the activity in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param activity Activity instance
     * @return Activity ID
     */
    @Override
    @Transactional(readOnly = false)
    public Long persistActivity(Activity activity) {
        activity.setTimestamp(new Date());
        if (activity.getId() == null) {
            activity.setId(ActivityIdGenerator.getInstance().generateId());
            activity.setCreatorId(userSessionManager.getUserId());
        }
        if (activity.getTypeId() == null) {
            activity.setTypeId(Constants.ACTIVITY_TYPE_DATA_CHANGE_ID);
        }
        return persistEntity(activity);
    }

    /**
     * Removes the activity from the database.
     *
     * @param id Activity ID
     * @return true if the Activity entity has been deleted
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteActivity(Long id) {
        return deleteEntity(id, Activity.class);
    }

}
