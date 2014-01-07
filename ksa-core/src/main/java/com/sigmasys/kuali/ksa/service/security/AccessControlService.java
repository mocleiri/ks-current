package com.sigmasys.kuali.ksa.service.security;

import com.sigmasys.kuali.ksa.model.security.Permission;

import java.util.List;
import java.util.Set;

/**
 * Access Control service provides information regarding user permissions used in KSA
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface AccessControlService {


    /**
     * Returns a set of Permission names for the given user ID
     *
     * @param userId Account ID
     * @return a set of permission names
     */
    Set<String> getUserPermissions(String userId);

    /**
     * Returns a set of Role names for the given user ID
     *
     * @param userId Account ID
     * @return a set of role names
     */
    Set<String> getRoles(String userId);


    /**
     * Returns the list of transaction type IDs that the given user can access.
     *
     * @param userId Account ID
     * @return a list of transaction type IDs
     */
    List<String> getAllowedTransactionTypes(String userId);

    /**
     * Returns the list of transaction type masks that the given user can access.
     *
     * @param userId Account ID
     * @return a list of transaction type masks
     */
    List<String> getAllowedTransactionTypeMasks(String userId);


    /**
     * Returns the list of transaction type IDs that the given set of roles can access.
     *
     * @param roleNames a set of role names
     * @return a list of transaction type IDs
     */
    List<String> getTransactionTypesByRoleNames(Set<String> roleNames);


    /**
     * Checks if the transaction type is allowed for the given user ID
     *
     * @param userId            AccountId
     * @param transactionTypeId Transaction type ID
     * @return true if the transaction type is allowed, false - otherwise
     */
    boolean isTransactionTypeAllowed(String userId, String transactionTypeId);

    /**
     * Checks if the user has the given permissions
     *
     * @param userId          AccountId
     * @param permissionNames one or more permission names
     * @return true if the user has the given permissions, false - otherwise
     */
    boolean hasPermissions(String userId, String... permissionNames);

    /**
     * Checks if the user has the given permission
     *
     * @param userId         AccountId
     * @param permissionName a permission name
     * @return true if the user has the given permission, false - otherwise
     */
    boolean hasPermission(String userId, String permissionName);

    /**
     * Checks if the currently authenticated user has the given permissions
     *
     * @param permissionNames one or more permission names
     * @return true if the user has the given permissions, false - otherwise
     */
    boolean hasPermissions(String... permissionNames);

    /**
     * Checks if the currently authenticated user has the given permission
     *
     * @param permissionName a permission name
     * @return true if the user has the given permission, false - otherwise
     */
    boolean hasPermission(String permissionName);

    /**
     * Checks if the currently authenticated user has the given permissions
     *
     * @param permissions one or more permissions
     * @return true if the user has the given permissions, false - otherwise
     */
    boolean hasPermissions(Permission... permissions);

    /**
     * Checks if the currently authenticated user has the given permission
     *
     * @param permission a permission
     * @return true if the user has the given permission, false - otherwise
     */
    boolean hasPermission(Permission permission);


    /**
     * Reloads user permissions and other security attributes from the database
     */
    void refresh();


}
