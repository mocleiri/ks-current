package com.sigmasys.kuali.ksa.service.security;

import com.sigmasys.kuali.ksa.exception.PermissionDeniedException;
import com.sigmasys.kuali.ksa.model.Allocation;
import com.sigmasys.kuali.ksa.model.Tag;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.AccountBlockingService;
import com.sigmasys.kuali.ksa.service.UserSessionManager;
import com.sigmasys.kuali.ksa.util.ContextUtils;

import java.util.Map;

/**
 * KSA Permission utility-methods
 *
 * @author Michael Ivanov
 */
public class PermissionUtils {

    private static UserSessionManager userSessionManager;
    private static AccessControlService acService;
    private static AccountBlockingService accountBlockingService;

    private PermissionUtils() {
    }

    private static UserSessionManager getUserSessionManager() {
        if (userSessionManager == null) {
            userSessionManager = ContextUtils.getBean(UserSessionManager.class);
        }
        return userSessionManager;
    }

    private static AccessControlService getAccessControlService() {
        if (acService == null) {
            acService = ContextUtils.getBean(AccessControlService.class);
        }
        return acService;
    }

    private static AccountBlockingService getAccountBlockingService() {
        if (accountBlockingService == null) {
            accountBlockingService = ContextUtils.getBean(AccountBlockingService.class);
        }
        return accountBlockingService;
    }

    public static boolean hasPermission(Permission permission) {
        if (permission == null) {
            String userId = getUserSessionManager().getUserId();
            throw new PermissionDeniedException(userId);
        }
        return getAccessControlService().hasPermission(permission);
    }

    public static void checkPermission(Permission permission) {
        checkPermission(permission, (Permission) null);
    }

    public static void checkPermissions(Permission... permissions) {
        checkPermissions(null, permissions);
    }

    public static void checkPermissions(Object target, Permission... permissions) {
        for (Permission permission : permissions) {
            checkPermission(permission, target);
        }
    }

    public static void checkPermission(Permission permission, Map<String, Object> attributes) {
        checkPermission(permission);
        getAccountBlockingService().checkBlock(attributes, permission);
    }

    public static void checkPermission(Permission permission, Object target) {

        boolean throwException = false;
        boolean noPermission = !hasPermission(permission);

        if (target != null) {

            if (target instanceof Allocation) {
                Allocation allocation = (Allocation) target;
                switch (permission) {
                    case CREATE_ALLOCATION:
                    case REMOVE_ALLOCATION:
                        throwException = noPermission && !allocation.isLocked();
                        break;
                    case CREATE_LOCKED_ALLOCATION:
                    case REMOVE_LOCKED_ALLOCATION:
                        throwException = noPermission && allocation.isLocked();
                        break;
                    case CREATE_INTERNALLY_LOCKED_ALLOCATION:
                    case REMOVE_INTERNALLY_LOCKED_ALLOCATION:
                        throwException = noPermission && allocation.isInternallyLocked();
                        break;
                }

            } else if (target instanceof Tag) {
                Tag tag = (Tag) target;
                switch (permission) {
                    case CREATE_ADMIN_TAG:
                    case EDIT_ADMIN_TAG:
                        throwException = noPermission && tag.isAdministrative();
                        break;
                    case CREATE_TAG:
                    case EDIT_TAG:
                        throwException = noPermission;
                        break;
                }
            }

        } else {
            throwException = noPermission;
        }

        if (throwException) {

            String userId = getUserSessionManager().getUserId();

            // TODO: ugly temporary fix for KSA permissions - please remove it (Michael)
            if ("admin".equalsIgnoreCase(userId)) {
                return;
            }

            throw new PermissionDeniedException(userId, permission);
        }

    }


}
