package com.sigmasys.kuali.ksa.service.impl;


import javax.annotation.PostConstruct;
import javax.jws.WebMethod;
import javax.jws.WebService;

import com.sigmasys.kuali.ksa.util.JaxbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigmasys.kuali.ksa.exception.UserNotFoundException;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.service.AccountImportService;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.jaxb.StudentProfile;
import com.sigmasys.kuali.ksa.util.XmlSchemaValidator;


// TODO -> Make it work with the new FM functionality

@Service("accountImportService")
@Transactional(readOnly = false)
@WebService(serviceName = AccountImportService.SERVICE_NAME, portName = AccountImportService.PORT_NAME, targetNamespace = Constants.WS_NAMESPACE)
public class AccountImportServiceImpl implements AccountImportService {

    /**
     * XMLSchema for the Student Profile.
     */
    private static final String STUDENT_PROFILE_SCHEMA_LOCATION = "classpath*:/xsd/student-profile.xsd";

    /**
     * XML schema file.
     */
    private static final String XML_SCHEMA_LOCATION = "classpath*:/xsd/xml.xsd";

    /**
     * XML schema validator.
     */
    private XmlSchemaValidator schemaValidator;


    /**
     * The logger.
     */
    private static final Log logger = LogFactory.getLog(AccountImportServiceImpl.class);


    @Autowired
    private AccountService accountService;


    @PostConstruct
    private void postConstruct() {
        schemaValidator = new XmlSchemaValidator(XML_SCHEMA_LOCATION, STUDENT_PROFILE_SCHEMA_LOCATION);
    }


    /**
     * Performs import of a student profile from the specified XML content of the profile.
     * Refer to the "Process Diagrams" design document for a detailed depiction of the process and logic.
     * This method validates the XML schema, unmarshalls the XML text into a <code>StudentProfile</code> object
     * and invokes the overloaded "importStudentProfile" method for import.
     *
     * @param xml XML content of a Student profile.
     * @throws IllegalArgumentException If the argument is <code>null</code>.
     * @throws RuntimeException         If any error is encountered during XML schema validation or unmarshalling.
     *                                  The original exception (if it exists) is wrapped in a <code>RuntimeException</code>.
     */
    @Override
    public void importStudentProfile(String xml) {

        // Validate the input:
        if (StringUtils.isBlank(xml)) {
            throw new IllegalArgumentException("Student profile XML is null. Abort import.");
        }

        // Validate against the XML schema:
        if (!schemaValidator.validateXml(xml)) {
            throw new RuntimeException("The student profile XML is invalid");
        }

        try {

            StudentProfile profile = JaxbUtils.fromXml(xml, StudentProfile.class);

            // Invoke import:
            importStudentProfile(profile);

        } catch (Throwable t) {
            logger.error("Error importing student profile.", t);
            throw new RuntimeException("Error importing student profile.", t);
        }
    }

    /**
     * Performs import of a student profile.
     * Refer to the "Process Diagrams" design document for a detailed depiction of the process and logic.
     * <p/>
     * This method assumes that un-marshalling of an XML file containing a Student Account has already been
     * performed and the schema validation has succeeded at the time this method is being invoked.
     *
     * @param studentProfile A student's profile object.
     * @throws IllegalArgumentException If "studentProfile" is invalid.
     * @throws UserNotFoundException    If an account does not exist in the system.
     * @throws RuntimeException         If student profile import encountered an error.
     */
    @Override
    @WebMethod(exclude = true)
    public void importStudentProfile(StudentProfile studentProfile) {

        // Step 1: Validating the XML schema is done prior to invoking this method by the JAX-WS framework
        // Validate the argument object instead:
        if (studentProfile == null) {
            throw new IllegalArgumentException("Student profile is null. Abort import.");
        }

        // Step 2: Validating the account
        String accountId = studentProfile.getAccountIdentifier();
        boolean accountExists = accountService.accountExists(accountId);

        if (!accountExists) {
            throw new UserNotFoundException("Account does not exist, ID = " + accountId);
        }

        // TODO

        // Retrieve a new FeeBase for the now validated Account with ID "accountId":
        /*FeeBase feeBase = feeManagementService.getFeeBase(accountId);

        // Step 3a: Import StudentProfile.StudentInformation
        StudentProfile.StudentInformation studentInfo = studentProfile.getStudentInformation();

        createKeyPair(feeBase, "citizenship", studentInfo.getCitizenship());
        createKeyPair(feeBase, "residency", studentInfo.getResidency());
        createKeyPair(feeBase, "year-of-study", studentInfo.getYearOfStudy());
        createKeyPair(feeBase, "level-of-study", ObjectUtils.toString(studentInfo.getLevelOfStudy()));
        createKeyPair(feeBase, "campus", ObjectUtils.toString(studentInfo.getCampus()));
        createKeyPair(feeBase, "full-or-part-time", studentInfo.getFullOrPartTime());

        // Step 3b: Import StudentProfile.StudentInformation.ProgramOfStudy
        StudentProfile.StudentInformation.ProgramOfStudy programOfStudy = studentInfo.getProgramOfStudy();

        createKeyPair(feeBase, "program-of-study-major", programOfStudy.getMajor());
        createKeyPair(feeBase, "program-of-study-second-major", programOfStudy.getSecondMajor());
        createKeyPair(feeBase, "program-of-study-minor", programOfStudy.getMinor());
        createKeyPair(feeBase, "program-of-study-second-minor", programOfStudy.getSecondMinor());

        // Step 4: Import StudentProfile.StudentInformation.keyPair (List<KeyPair>)
        for (KeyPair keyPair : studentInfo.getKeyPair()) {
            createKeyPair(feeBase, keyPair.getKeyName(), keyPair.getValue());
        }

        // Step 5a: Identify the LearningPeriod:
        StudentProfile.PeriodInformation periodInfo = studentProfile.getPeriodInformation();
        String periodName = ObjectUtils.toString(periodInfo.getPeriod());
        LearningPeriod period = feeManagementService.getLearningPeriod(periodName);

        // Importing the PeriodInformation is only possible when there is an already defined LearningPeriod:
        if (period != null) {
            // Step 5b: Import StudentProfile.PeriodInformation.keyPair (List<KeyPair>)
            for (KeyPair periodKeyPair : periodInfo.getKeyPair()) {
                createPeriodKeyPair(feeBase, periodKeyPair.getKeyName(), periodKeyPair.getValue(), period);
            }

            // Step 6: Import StudentProfile.PeriodInformation.Study.learningUnit (List<LearningUnit>)
            StudentProfile.PeriodInformation.Study study = periodInfo.getStudy();

            for (com.sigmasys.kuali.ksa.jaxb.LearningUnit lu : study.getLearningUnit()) {
                importLearningUnit(feeBase, lu, period);
            }
        }*/
    }

}
