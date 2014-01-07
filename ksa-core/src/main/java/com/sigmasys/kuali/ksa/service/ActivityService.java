package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.Activity;
import com.sigmasys.kuali.ksa.model.ActivityType;

import java.util.List;

/**
 * Activity Service
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface ActivityService {

    /**
     * Returns the default activity type.
     *
     * @return Activity Type instance
     */
    ActivityType getDefaultActivityType();

    /**
     * Returns the activity type by code.
     *
     * @param code Activity type code
     * @return Activity Type instance
     */
    ActivityType getActivityType(String code);

    /**
     * Returns the activity type by ID.
     *
     * @param id Activity type ID
     * @return Activity Type instance
     */
    ActivityType getActivityType(Long id);

    /**
     * Returns Activity by ID
     *
     * @param id Activity ID
     * @return Activity instance
     */
    Activity getActivity(Long id);

    /**
     * Returns all activities sorted by ID in the descendant order
     *
     * @return List of activities
     */
    List<Activity> getActivities();

    /**
     * Returns the specified number of activities sorted by ID in the descendant order
     *
     * @return List of activities
     */
    List<Activity> getActivities(int limit);

    /**
     * Returns all activities sorted by ID in the descendant order for the given Account ID.
     *
     * @param userId Account ID
     * @return List of activities
     */
    List<Activity> getActivities(String userId);

    /**
     * Returns the specified number of activities sorted by ID in the descendant order for the given Account ID.
     *
     * @param userId Account ID
     * @return List of activities
     */
    List<Activity> getActivities(String userId, int limit);

    /**
     * Persists the activity in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param activity Activity instance
     * @return Activity ID
     */
    Long persistActivity(Activity activity);

    /**
     * Removes the activity from the database.
     *
     * @param id Activity ID
     * @return true if the Activity entity has been deleted
     */
    boolean deleteActivity(Long id);

}
