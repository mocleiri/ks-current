/**
 * Copyright 2004-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.datadictionary.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.student.validation.decorator.mojo.ValidationDecoratorWriterForOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class KSDictionaryCreatorMojoTest {
    
    private static Logger log = LoggerFactory.getLogger(KSDictionaryCreatorMojoTest.class);
    

    private static final String CORE_DIRECTORY =
            "C:/svn/ks-1.3/ks-core/ks-core-api/src/main/java";
    // "C:/svn/maven-dictionary-generator/trunk/src/main/java/org/kuali/student/core";
    private static final String COMMON_DIRECTORY =
            "C:/svn/ks-1.3/ks-common/ks-common-api/src/main/java";
    private static final String ENROLL_PROJECT_SRC_MAIN = "C:/svn/ks-1.3/ks-enroll/ks-enroll-api/src/main";
    private static final String ENROLL_PROJECT_JAVA_DIRECTORY = ENROLL_PROJECT_SRC_MAIN + "/java";
    private static final String ENROLL_PROJECT_RESOURCES_DIRECTORY = ENROLL_PROJECT_SRC_MAIN + "/resources";
    private static final String LUM_DIRECTORY =
            "C:/svn/ks-1.3/ks-lum/ks-lum-api/src/main/java";
    private static final String RICE_DIRECTORY =
            "C:/svn/rice/rice-release-1-0-2-1-br/api/src/main/java";
    private static final String TEST_SOURCE_DIRECTORY =
            "src/test/java/org/kuali/student/contract/model/test/source";
    private static final String TARGET_GENERATED_SOURCES = "target/generated-sources";
    private static final String RESOURCES_DIRECTORY =
            // "C:/svn/student/ks-core/ks-core-api/src/main/java";
            "src/main/resources";
    private static final String PESC_CORE_MAIN = RESOURCES_DIRECTORY
            + "/CoreMain_v1.8.0.xsd";

    public KSDictionaryCreatorMojoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class KSDictionaryCreatorMojo.
     */
    @Test
    public void testExecute() throws Exception {
        log.info("execute");
        List<String> srcDirs = new ArrayList<String>();
        srcDirs.add(TEST_SOURCE_DIRECTORY);
//        srcDirs.add(ENROLL_PROJECT_JAVA_DIRECTORY);
        
//		srcDirs.add(CORE_DIRECTORY);
//		srcDirs.add(COMMON_DIRECTORY);
//		srcDirs.add(LUM_DIRECTORY);
        KSDictionaryCreatorMojo instance = new KSDictionaryCreatorMojo();
        Map pluginContext = new HashMap ();
        MavenProject project = new MavenProject ();
        pluginContext.put("project", project);
        instance.setPluginContext(pluginContext);
        instance.setSourceDirs(srcDirs);
        instance.setOutputDirectory(new File(TARGET_GENERATED_SOURCES)); 
//        instance.setOutputDirectory(new File(ENROLL_PROJECT_RESOURCES_DIRECTORY));
        // Be careful when you uncomment this one it will overwrite stuff in another project
//        instance.setOutputDirectory(new File(ENROLL_PROJECT_RESOURCES_DIRECTORY));
        instance.setWriteManual(true);
        instance.setWriteGenerated(true);
        instance.setThrowExceptionIfNotAllFilesProcessed(false);
        List<String> classNames = new ArrayList();
        // Atp
//        classNames.add("AtpInfo");
//        classNames.add("MilestoneInfo");
//        classNames.add("AtpMilestoneRelationInfo");
//        classNames.add("AtpAtpRelationInfo");
//        // Acal
//        classNames.add("AcademicCalendarInfo");
//        classNames.add("CampusCalendarInfo");
//        classNames.add("TermInfo");
//        classNames.add("RegistrationDateGroupInfo");
//        classNames.add("HolidayInfo");
//        classNames.add("KeyDateInfo");
//        // LPR 
//        classNames.add("LuiPersonRelationInfo");
//        classNames.add("LprRosterInfo");
//        classNames.add("LprRosterEntryInfo");
//        classNames.add("LprTransactionInfo");
//        classNames.add("LprTransactionItemInfo");
//        classNames.add("RequestOptionInfo");
//        classNames.add("LprTransactionItemResultInfo");
//        // Hold
//        classNames.add("HoldInfo");
//        classNames.add("IssueInfo");
//        classNames.add("RestrictionInfo");
//        // LUI 
//        classNames.add("LuiInfo");
//        classNames.add("LuiIdentifierInfo");
//        classNames.add("LuCodeInfo");
//        classNames.add("FeeInfo");
//        classNames.add("CurrencyAmountInfo");
//        classNames.add("RevenueInfo");
//        classNames.add("AffiliatedOrgInfo");
//        classNames.add("MeetingScheduleInfo");
//        classNames.add("LuiLuiRelationInfo");
//        classNames.add("LuiCapacityInfo");
        
//        // Course Offering
//        classNames.add("CourseOfferingInfo");
//        classNames.add("CourseWaitlistEntryInfo");
//        classNames.add("OfferingInstructorInfo");
//        classNames.add("ActivityOfferingInfo");
//        classNames.add("RegistrationGroupInfo");
//        classNames.add("SeatPoolDefinitionInfo");
//        classNames.add("StatementTreeViewInfo");
//        classNames.add("ReqComponentInfo");
//        classNames.add("ReqCompFieldInfo");

//        // Course registration
//        classNames.add("CourseRegistrationInfo");
//        classNames.add("ActivityRegistrationInfo");
//        classNames.add("RegGroupRegistrationInfo");
//        classNames.add("RegRequestInfo");
//        classNames.add("RegRequestItemInfo");
//        classNames.add("RegResponseInfo");
//        classNames.add("RegResponseItemInfo");
//
//          // Exemption
//        classNames.add("ExemptionInfo");
//        classNames.add("ExemptionRequestInfo");
//
//        // Grading
//        classNames.add("GradeRosterInfo");
//        classNames.add("GradeRosterEntryInfo");
//        classNames.add("GradeValuesGroupInfo"); 
//        
//        // LRR
//        classNames.add("LearningResultRecordInfo");
//        classNames.add("ResultSourceInfo");       
//        
//        // LRC
//        classNames.add("ResultValuesGroupInfo");
//        classNames.add("ResultValueInfo");     
//        classNames.add("ResultScaleInfo"); 
        
        instance.execute();
//        assertTrue(new File(instance.getOutputDirectory() + "/" + "ks-LprInfo-dictionary.xml").exists());
    }
}
