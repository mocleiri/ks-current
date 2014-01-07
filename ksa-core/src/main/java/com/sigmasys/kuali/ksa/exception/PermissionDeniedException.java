package com.sigmasys.kuali.ksa.exception;


import com.sigmasys.kuali.ksa.model.security.Permission;

/**
 * This exception is thrown  when a user does not have the appropriate permission.
 *
 * @author Michael Ivanov
 */
public class PermissionDeniedException extends GenericException {

    private String userId;
    private Permission permission;

    public PermissionDeniedException(String userId) {
        super("No permission found for user '" + userId + "'");
        this.userId = userId;
    }

    public PermissionDeniedException(String userId, Permission permission) {
        super("User '" + userId + "' does not have permission '" + permission.name() + "'");
        this.userId = userId;
        this.permission = permission;
    }

    public String getUserId() {
        return userId;
    }

    public Permission getPermission() {
        return permission;
    }

}
