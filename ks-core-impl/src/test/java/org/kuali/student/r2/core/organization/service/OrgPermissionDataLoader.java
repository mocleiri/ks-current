/*
 * Copyright 2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may	obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.r2.core.organization.service;

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.student.common.test.mock.data.AbstractMockServicesAwareDataLoader;
import org.kuali.student.kim.permission.mock.KimPermissionConstants;
import org.kuali.student.kim.permission.mock.RoleAndPermissionServiceMockImpl;
import org.kuali.student.r2.core.organization.service.decorators.PermissionServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OrgPermissionDataLoader extends AbstractMockServicesAwareDataLoader {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    public static final String USER_FRED = "FRED";
    public static final String USER_EDNA = "EDNA";
    public static final String USER_ADMIN = "ADMIN";

    private void loadNeededTemplates() {
        this.createTemplate(PermissionServiceConstants.KS_SYS_NAMESPACE, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_TEMPLATE_NAME, KimPermissionConstants.DEFAULT_KIM_TYPE_ID);
    }

    private Template createTemplate(String namespaceCode, String name, String kimTypeId) {
        return this.createTemplate(namespaceCode, name, name, kimTypeId);
    }

    private Template createTemplate(String namespaceCode, String name, String description, String kimTypeId) {
        // TODO: refactor if RICE adds Create/update methods for templates
        RoleAndPermissionServiceMockImpl impl = (RoleAndPermissionServiceMockImpl) this.roleService;
        Template.Builder bldr = Template.Builder.create(namespaceCode, name, kimTypeId);
        bldr.setDescription(description);
        bldr.setActive(true);
        Template template = impl.createTemplate(bldr.build());
        return template;
    }

    private Permission createPermission(String permName, Template template, String viewId) {
        Map<String, String> details = new LinkedHashMap<String, String>();
        if (viewId != null) {
            details.put(KimPermissionConstants.VIEW_ID_ATTR_DEFINITION, viewId);
        }
        Permission permission = createPermission(template,
                PermissionServiceConstants.KS_SYS_NAMESPACE,
                permName, details);
        return permission;
    }

    private Permission createPermission(Template template, String namespaceCode, String name, Map<String, String> details) {
        Permission.Builder bldr = Permission.Builder.create(namespaceCode, name);
        bldr.setTemplate(Template.Builder.create(template));
        bldr.setActive(true);
        bldr.setAttributes(details);

        Permission fromCreate = this.permissionService.createPermission(bldr.build());
        assertEquals(namespaceCode, fromCreate.getNamespaceCode());
        assertEquals(name, fromCreate.getName());
        assertNotNull(fromCreate.getId());
        assertNotNull(fromCreate.getVersionNumber());
        assertTrue(fromCreate.isActive());
        assertEquals(details, fromCreate.getAttributes());
        assertNull(fromCreate.getDescription());

        bldr = Permission.Builder.create(fromCreate);
        bldr.setDescription(namespaceCode + " " + name);
        Permission fromUpdate = this.permissionService.updatePermission(bldr.build());
        assertEquals(fromCreate.getNamespaceCode(), fromUpdate.getNamespaceCode());
        assertEquals(fromCreate.getName(), fromUpdate.getName());
        assertEquals(fromCreate.getId(), fromUpdate.getId());
        assertNotSame(fromCreate.getVersionNumber(), fromUpdate.getVersionNumber());
        assertEquals(fromCreate.isActive(), fromUpdate.isActive());
        assertEquals(fromCreate.getAttributes(), fromUpdate.getAttributes());
        assertEquals(bldr.getDescription(), fromUpdate.getDescription());

        Permission fromGet = this.permissionService.getPermission(fromUpdate.getId());
        assertEquals(fromUpdate.getNamespaceCode(), fromGet.getNamespaceCode());
        assertEquals(fromUpdate.getName(), fromGet.getName());
        assertEquals(fromUpdate.getId(), fromGet.getId());
        assertEquals(fromUpdate.getVersionNumber(), fromGet.getVersionNumber());
        assertEquals(fromUpdate.isActive(), fromGet.isActive());
        assertEquals(fromUpdate.getAttributes(), fromGet.getAttributes());
        assertEquals(fromUpdate.getDescription(), fromGet.getDescription());

        Permission fromFind = this.permissionService.findPermByNamespaceCodeAndName(namespaceCode, name);
        assertEquals(fromUpdate.getNamespaceCode(), fromFind.getNamespaceCode());
        assertEquals(fromUpdate.getName(), fromFind.getName());
        assertEquals(fromUpdate.getId(), fromFind.getId());
        assertEquals(fromUpdate.getVersionNumber(), fromFind.getVersionNumber());
        assertEquals(fromUpdate.isActive(), fromFind.isActive());
        assertEquals(fromUpdate.getAttributes(), fromFind.getAttributes());
        assertEquals(fromUpdate.getDescription(), fromFind.getDescription());

        return fromFind;
    }

    private Role createRole(String namespaceCode, String name, String kimTypeId) {
        Role.Builder bldr = Role.Builder.create();
        bldr.setNamespaceCode(namespaceCode);
        bldr.setName(name);
        bldr.setKimTypeId(kimTypeId);
        bldr.setActive(true);

        Role fromCreate = this.roleService.createRole(bldr.build());
        assertEquals(namespaceCode, fromCreate.getNamespaceCode());
        assertEquals(name, fromCreate.getName());
        assertEquals(kimTypeId, fromCreate.getKimTypeId());
        assertNotNull(fromCreate.getId());
        assertNotNull(fromCreate.getVersionNumber());
        assertTrue(fromCreate.isActive());
        assertNull(fromCreate.getDescription());


        bldr = Role.Builder.create(fromCreate);
        bldr.setDescription(namespaceCode + " " + name);
        Role fromUpdate = this.roleService.updateRole(bldr.build());
        assertEquals(fromCreate.getNamespaceCode(), fromUpdate.getNamespaceCode());
        assertEquals(fromCreate.getName(), fromUpdate.getName());
        assertEquals(fromCreate.getId(), fromUpdate.getId());
        assertNotSame(fromCreate.getVersionNumber(), fromUpdate.getVersionNumber());
        assertEquals(fromCreate.isActive(), fromUpdate.isActive());
        assertEquals(bldr.getDescription(), fromUpdate.getDescription());

        Role fromGet = this.roleService.getRole(fromUpdate.getId());
        assertEquals(fromUpdate.getNamespaceCode(), fromGet.getNamespaceCode());
        assertEquals(fromUpdate.getName(), fromGet.getName());
        assertEquals(fromUpdate.getKimTypeId(), fromGet.getKimTypeId());
        assertEquals(fromUpdate.getId(), fromGet.getId());
        assertEquals(fromUpdate.getVersionNumber(), fromGet.getVersionNumber());
        assertEquals(fromUpdate.isActive(), fromGet.isActive());
        assertEquals(fromUpdate.getDescription(), fromGet.getDescription());

        Role fromFind = this.roleService.getRoleByNamespaceCodeAndName(namespaceCode, name);
        assertEquals(fromUpdate.getNamespaceCode(), fromFind.getNamespaceCode());
        assertEquals(fromUpdate.getName(), fromFind.getName());
        assertEquals(fromUpdate.getKimTypeId(), fromFind.getKimTypeId());
        assertEquals(fromUpdate.getId(), fromFind.getId());
        assertEquals(fromUpdate.getVersionNumber(), fromFind.getVersionNumber());
        assertEquals(fromUpdate.isActive(), fromFind.isActive());
        assertEquals(fromUpdate.getDescription(), fromFind.getDescription());

        // TODO: test update

        return fromFind;
    }

    private RoleMember assignPrincipal2Role(String principalId, Role role) {
        Map<String, String> qualifiers = new LinkedHashMap<String, String>();
        RoleMember roleMember = this.roleService.assignPrincipalToRole(principalId, role.getNamespaceCode(), role.getName(), qualifiers);
        assertEquals(principalId, roleMember.getMemberId());
        assertEquals(role.getId(), roleMember.getRoleId());
        assertEquals(KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, roleMember.getType());
        assertEquals(qualifiers, roleMember.getAttributes());
        List<String> roleIds = this.roleService.getMemberParentRoleIds(KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.getCode(), principalId);
        assertTrue(roleIds.contains(role.getId()));
        return roleMember;

    }

    private void setupPerms(String readPerm, String updatePerm, String deletePerm, Role viewRole, Role maintenanceRole, Template template) {

        Permission updateOrgPermission = createPermission(updatePerm,
                template,
                null);
        Permission readOrgPermission = createPermission(readPerm,
                template,
                null);
        Permission deleteOrgPermission = createPermission(deletePerm,
                template,
                null);

        roleService.assignPermissionToRole(updateOrgPermission.getId(), maintenanceRole.getId());
        roleService.assignPermissionToRole(readOrgPermission.getId(), maintenanceRole.getId());
        roleService.assignPermissionToRole(deleteOrgPermission.getId(), maintenanceRole.getId());

        roleService.assignPermissionToRole(readOrgPermission.getId(), viewRole.getId());

        assignPrincipal2Role(USER_ADMIN, maintenanceRole);
        assignPrincipal2Role(USER_FRED, viewRole);
    }

    @Override
    protected void initializeData() throws Exception {

        loadNeededTemplates();

        Template template =
                this.permissionService.findPermTemplateByNamespaceCodeAndName(PermissionServiceConstants.KS_SYS_NAMESPACE,
                        KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_TEMPLATE_NAME);

        // Make sure we have a handle on all the templates we need
        assertNotNull(template);

        Role viewRole = createRole(PermissionServiceConstants.KS_SYS_NAMESPACE,
                KimPermissionConstants.KS_ORG_VIEW_ROLE_NAME,
                KimPermissionConstants.DEFAULT_KIM_TYPE_ID);
        Role maintenanceRole = createRole(PermissionServiceConstants.KS_SYS_NAMESPACE,
                KimPermissionConstants.KS_ORG_MAINTENANCE_ROLE_NAME,
                KimPermissionConstants.DEFAULT_KIM_TYPE_ID);

        setupPerms(KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_DATA_PERMISSION, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_DATA_PERMISSION,
                KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_DATA_PERMISSION, viewRole, maintenanceRole, template);

        setupPerms(KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION,
                KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, viewRole, maintenanceRole, template);

        setupPerms(KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION,
                KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, viewRole, maintenanceRole, template);

        setupPerms(KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION,
                KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, viewRole, maintenanceRole, template);

    }
}
