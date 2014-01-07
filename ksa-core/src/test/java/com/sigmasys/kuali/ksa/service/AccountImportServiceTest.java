package com.sigmasys.kuali.ksa.service;

import static org.springframework.util.Assert.isTrue;


import com.sigmasys.kuali.ksa.util.JaxbUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sigmasys.kuali.ksa.exception.UserNotFoundException;
import com.sigmasys.kuali.ksa.jaxb.StudentProfile;
import com.sigmasys.kuali.ksa.util.CommonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional(readOnly = false)
public class AccountImportServiceTest extends AbstractServiceTest {

    @Autowired
    private AccountImportService accountImportService;

    @Autowired
    private AccountService accountService;


    @Test
    public void testImportStudentProfileFromObjectNullAccount() throws Exception {
        boolean caughtException = false;

        try {
            accountImportService.importStudentProfile((StudentProfile) null);
            isTrue(false);// should not be here
        } catch (Exception e) {
            caughtException = (e instanceof IllegalArgumentException);
        }

        isTrue(caughtException);
    }

    @Test
    public void testImportStudentProfileFromTextNullInput() throws Exception {
        boolean caughtException = false;

        try {
            accountImportService.importStudentProfile((String) null);
            isTrue(false);// should not be here
        } catch (Exception e) {
            caughtException = (e instanceof IllegalArgumentException);
        }

        isTrue(caughtException);
    }

    @Test
    public void testImportStudentProfileFromTextInvalidSchema() throws Exception {
        // Load contents:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-invalid-schema.xml");
        boolean schemaInvalid = true;

        try {
            // Call the service:
            accountImportService.importStudentProfile(content);
            isTrue(false); // should not be here
        } catch (Exception e) {
            schemaInvalid = StringUtils.containsIgnoreCase(e.getMessage(), "XML schema is invalid");
        }

        isTrue(schemaInvalid);
    }

    @Test
    public void testImportStudentProfileFromObjectNoAccountExists() throws Exception {

        // Create an unmarshalled object from XML text:

        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-unknown-user.xml");


        StudentProfile profile = JaxbUtils.fromXml(content, StudentProfile.class);

        // Call the service:
        boolean accountDoesNotExist = false;

        try {
            accountImportService.importStudentProfile(profile);
            isTrue(false); // should not be here
        } catch (Exception e) {
            accountDoesNotExist = (e instanceof UserNotFoundException);
        }

        isTrue(accountDoesNotExist);
    }

    @Test
    public void testImportStudentProfileFromTextNoAccountExists() throws Exception {
        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-unknown-user.xml");

        // Call the service:
        boolean accountDoesNotExist = false;

        try {
            accountImportService.importStudentProfile(content);
            isTrue(false); // should not be here
        } catch (Exception e) {
            accountDoesNotExist = (e.getCause() instanceof UserNotFoundException);
        }

        isTrue(accountDoesNotExist);
    }

    @Test
    public void testImportStudentProfileFromObjectUser1AccountExists() throws Exception {
        // Create an account:
        String userId = "user1";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user1.xml");

        StudentProfile profile = JaxbUtils.fromXml(content, StudentProfile.class);

        // Call the service:
        accountImportService.importStudentProfile(profile);
    }

    @Test
    public void testImportStudentProfileFromTextUser1AccountExists() throws Exception {
        // Create an account:
        String userId = "user1";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user1.xml");

        // Call the service:
        accountImportService.importStudentProfile(content);
    }

    @Test
    public void testImportStudentProfileFromObjectUser2AccountExists() throws Exception {
        // Create an account:
        String userId = "user2";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user2.xml");

        StudentProfile profile = JaxbUtils.fromXml(content, StudentProfile.class);

        // Call the service:
        accountImportService.importStudentProfile(profile);
    }

    @Test
    public void testImportStudentProfileFromTextUser2AccountExists() throws Exception {
        // Create an account:
        String userId = "user2";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user2.xml");

        // Call the service:
        accountImportService.importStudentProfile(content);
    }

    @Test
    public void testImportStudentProfileFromObjectUser3AccountExists() throws Exception {
        // Create an account:
        String userId = "user3";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user3.xml");

        StudentProfile profile = JaxbUtils.fromXml(content, StudentProfile.class);

        // Call the service:
        accountImportService.importStudentProfile(profile);
    }

    @Test
    public void testImportStudentProfileFromTextUser3AccountExists() throws Exception {
        // Create an account:
        String userId = "user3";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user3.xml");

        // Call the service:
        accountImportService.importStudentProfile(content);
    }

    @Test
    public void testImportStudentProfileFromObjectUser4AccountExists() throws Exception {
        // Create an account:
        String userId = "user4";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user4.xml");

        StudentProfile profile = JaxbUtils.fromXml(content, StudentProfile.class);

        // Call the service:
        accountImportService.importStudentProfile(profile);
    }

    @Test
    public void testImportStudentProfileFromTextUser4AccountExists() throws Exception {
        // Create an account:
        String userId = "user4";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user4.xml");

        // Call the service:
        accountImportService.importStudentProfile(content);
    }

    @Test
    public void testImportStudentProfileFromObjectAdmin1AccountExists() throws Exception {
        // Create an account:
        String userId = "admin1";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-admin1.xml");

        StudentProfile profile = JaxbUtils.fromXml(content, StudentProfile.class);

        // Call the service:
        accountImportService.importStudentProfile(profile);
    }

    @Test
    public void testImportStudentProfileFromTextAdmin1AccountExists() throws Exception {
        // Create an account:
        String userId = "admin1";
        accountService.getOrCreateAccount(userId);

        // Create an unmarshalled object from XML text:
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-admin1.xml");

        // Call the service:
        accountImportService.importStudentProfile(content);
    }

    /* *****************************************
      *
      * Test KD accounts.
      *
      * *****************************************/

    @Test
    public void testImportStudentProfileFromObjectKDAccounts() throws Exception {
        for (int i = 1; i <= 6; i++) {
            // Create an account:
            String userId = "testuser" + i;
            accountService.getOrCreateAccount(userId);

            // Create an unmarshalled object from XML text:
            String content = CommonUtils.getResourceAsString("xmlImport/fee-management/kd/situation-" + i + ".xml");

            StudentProfile profile = JaxbUtils.fromXml(content, StudentProfile.class);

            // Call the service:
            accountImportService.importStudentProfile(profile);
        }
    }

    @Test
    public void testImportStudentProfileFromTextKDAccounts() throws Exception {
        for (int i = 1; i <= 6; i++) {
            // Create an account:
            String userId = "testuser" + i;
            accountService.getOrCreateAccount(userId);

            // Create an unmarshalled object from XML text:
            String content = CommonUtils.getResourceAsString("xmlImport/fee-management/kd/situation-" + i + ".xml");

            // Call the service:
            accountImportService.importStudentProfile(content);
        }
    }
}
