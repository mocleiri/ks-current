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
package org.kuali.student.contract.model.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.impl.ServiceContractModelCache;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.kuali.student.contract.model.impl.ServiceContractModelQDoxLoader;
import org.kuali.student.contract.model.validation.ServiceContractModelValidator;

/**
 * 
 * @author nwright
 */
public class HtmlContractWriterTest {

    public HtmlContractWriterTest() {
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
    private static final String CORE_DIRECTORY =
            "C:/svn/ks-1.3/ks-core/ks-core-api/src/main/java";
    // "C:/svn/maven-dictionary-generator/trunk/src/main/java/org/kuali/student/core";
    private static final String COMMON_DIRECTORY =
            "C:/svn/ks-1.3/ks-common/ks-common-api/src/main/java";
    private static final String ENROLL_PROJECT_SOURCE_DIRECTORY =
            "C:/svn/ks-1.3/ks-enroll/ks-enroll-api/src/main/java";
    private static final String LUM_DIRECTORY =
            "C:/svn/ks-1.3/ks-lum/ks-lum-api/src/main/java";
    private static final String RICE_DIRECTORY =
            "C:/svn/rice/rice-release-1-0-2-1-br/api/src/main/java";
    private static final String RICE_CORE_API_DIRECTORY = "C:/svn/rice/trunk/core/api/src/main/java";
    private static final String RICE_KIM_API_DIRECTORY = "C:/svn/rice/trunk/kim/kim-api/src/main/java";
    private static final String RICE_LOCATION_API_DIRECTORY = "C:/svn/rice/trunk/location/api/src/main/java";
    private static final String RICE_KEW_API_DIRECTORY = "C:/svn/rice/trunk/kew/api/src/main/java";
    private static final String RICE_KEN_API_DIRECTORY = "C:/svn/rice/trunk/ken/api/src/main/java";
    private static final String RICE_KSB_API_DIRECTORY = "C:/svn/rice/trunk/ksb/api/src/main/java";
    private static final String RICE_KRMS_API_DIRECTORY = "C:/svn/rice/trunk/krms/api/src/main/java";
    private static final String RICE_KRMS_IMPL_DIRECTORY = "C:/svn/rice/trunk/krms/impl/src/main/java";
    private static final String TEST_SOURCE_DIRECTORY =
            "src/test/java/org/kuali/student/contract/model/test/source";
    
    private static final String KSA_DIRECTORY = "D:/svn/ks/ksa/ksa-core/src/main/java";
    private static final String HTML_CONTRACT_DIRECTORY = "target/html/contract";
    private static final String HTML_CONTRACT_DIRECTORY_TEST = HTML_CONTRACT_DIRECTORY + "/test";    
    private static final String HTML_CONTRACT_DIRECTORY_ENROLL = HTML_CONTRACT_DIRECTORY + "/enroll";         
    private static final String HTML_CONTRACT_DIRECTORY_RICE = HTML_CONTRACT_DIRECTORY + "/rice";             
    private static final String HTML_CONTRACT_DIRECTORY_KSA = HTML_CONTRACT_DIRECTORY + "/ksa";      
    private static final String HTML_CONTRACT_DIRECTORY_PESC = HTML_CONTRACT_DIRECTORY + "/pesc";    
    private static final String RESOURCES_DIRECTORY =
            // "C:/svn/student/ks-core/ks-core-api/src/main/java";
            "src/main/resources";

    private ServiceContractModel getTestModel() {
        List<String> srcDirs = new ArrayList<String>();
        srcDirs.add(TEST_SOURCE_DIRECTORY);
//		srcDirs.add(ENROLL_PROJECT_SOURCE_DIRECTORY);
//		srcDirs.add(CORE_DIRECTORY);
//		srcDirs.add(COMMON_DIRECTORY);
//		srcDirs.add(LUM_DIRECTORY);
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(
                srcDirs);
        return new ServiceContractModelCache(instance);

    }
    
    
    private ServiceContractModel getRiceModel() {
        List<String> srcDirs = new ArrayList<String>();
//        srcDirs.add(TEST_SOURCE_DIRECTORY);
        srcDirs.add(RICE_CORE_API_DIRECTORY); 
        srcDirs.add(RICE_KIM_API_DIRECTORY); 
        srcDirs.add(RICE_LOCATION_API_DIRECTORY); 
        srcDirs.add(RICE_KEW_API_DIRECTORY); 
        srcDirs.add(RICE_KEN_API_DIRECTORY); 
        srcDirs.add(RICE_KSB_API_DIRECTORY); 
        srcDirs.add(RICE_KRMS_API_DIRECTORY); 
		srcDirs.add(RICE_KRMS_IMPL_DIRECTORY);
//		srcDirs.add(CORE_DIRECTORY);
//		srcDirs.add(COMMON_DIRECTORY);
//		srcDirs.add(LUM_DIRECTORY);
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(
                srcDirs, false);
        return new ServiceContractModelCache(instance);

    }    
    
    private ServiceContractModel getKSAModel() {
        List<String> srcDirs = new ArrayList<String>();
        srcDirs.add(KSA_DIRECTORY);
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(
                srcDirs, false);
        return new ServiceContractModelCache(instance);

    }    
    
    private ServiceContractModel getEnrollModel() {
        List<String> srcDirs = new ArrayList<String>();
//        srcDirs.add(TEST_SOURCE_DIRECTORY);
		srcDirs.add(ENROLL_PROJECT_SOURCE_DIRECTORY);
//		srcDirs.add(CORE_DIRECTORY);
//		srcDirs.add(COMMON_DIRECTORY);
//		srcDirs.add(LUM_DIRECTORY);
        ServiceContractModel instance = new ServiceContractModelQDoxLoader(
                srcDirs);
        return new ServiceContractModelCache(instance);

    }
    // pesc stuff
    private static final String PESC_DIRECTORY =
            RESOURCES_DIRECTORY + "/pesc";
    private static final String PESC_CORE_MAIN = PESC_DIRECTORY + "/CoreMain.xsd";
    private static final String PESC_ACAD_REC = PESC_DIRECTORY + "/AcademicRecord_v1.5.0.xsd";
    private static final String PESC_COLL_TRANS = PESC_DIRECTORY + "/CollegeTranscript_v1.2.0.xsd";

    private ServiceContractModel getPescModel() {
        List<String> xsdFileNames = new ArrayList();
//        xsdFileNames.add(PESC_CORE_MAIN);
//        xsdFileNames.add(PESC_ACAD_REC);
        xsdFileNames.add(PESC_COLL_TRANS);
        ServiceContractModel instance = new ServiceContractModelPescXsdLoader(xsdFileNames);
        instance = new ServiceContractModelCache(instance);
        validate(instance);
        return instance;
    }


    private void validate(ServiceContractModel model) {
        Collection<String> errors =
                new ServiceContractModelValidator(model).validate();
        if (errors.size() > 0) {
            StringBuilder buf = new StringBuilder();
            buf.append(errors.size()).append(" errors found while validating the data.");
            int cnt = 0;
            for (String msg : errors) {
                cnt++;
                buf.append("\n");
                buf.append("*error*").append(cnt).append(":").append(msg);
            }

            fail(buf.toString());
        }
    }

    /**
     * Test of run
     */
//    @Test
    public void testRunPesc() {
        ServiceContractModel model = null;
        HtmlContractWriter writer = null;

        model = this.getPescModel();
        this.validate(model);
        writer = new HtmlContractWriter(HTML_CONTRACT_DIRECTORY_PESC, model);
        writer.write("testVersion", DateUtility.asYMD(new Date()));

        assertTrue(new File(HTML_CONTRACT_DIRECTORY_PESC + "/" + "index.html").exists());
    }
    
    /**
     * Test of run
     */
    @Test
    public void testTestRun () {
        ServiceContractModel model = null;
        HtmlContractWriter writer = null;

        model = this.getTestModel();
        this.validate(model);
        writer = new HtmlContractWriter(HTML_CONTRACT_DIRECTORY_TEST, model);
        writer.write("testVersion", DateUtility.asYMD(new Date()));

        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "index.html").exists());
        assertTrue(
                new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "LprService.html").exists());
        assertTrue(
                new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "LprInfo.html").exists());
        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "ContextInfo.html").exists());
        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "RichTextInfo.html").exists());
        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "MetaInfo.html").exists());

        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "StateService.html").exists());
        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "StateInfo.html").exists());

        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "TypeService.html").exists());
        assertTrue(new File(HTML_CONTRACT_DIRECTORY_TEST + "/" + "TypeInfo.html").exists());
    }
    
    
    /**
     * Test of run
     */
//    @Test
    public void testEnrollRun () {
        ServiceContractModel model = null;
        HtmlContractWriter writer = null;

        model = this.getEnrollModel();
        this.validate(model);
        writer = new HtmlContractWriter(HTML_CONTRACT_DIRECTORY_ENROLL, model);
        writer.write("testVersion", DateUtility.asYMDHMInEasternTimeZone(new DateTime()));

//        assertTrue(new File(HTML_CONTRACT_DIRECTORY_ENROLL + "/" + "index.html").exists());
    }
    
    /**
     * Test of run
     */
//    @Test
    public void testRiceRun () {
        ServiceContractModel model = null;
        HtmlContractWriter writer = null;

        model = this.getRiceModel();
        this.validate(model);
        writer = new HtmlContractWriter(HTML_CONTRACT_DIRECTORY_RICE, model);
        writer.write("testVersion", DateUtility.asYMD(new Date()));

//        assertTrue(new File(HTML_CONTRACT_DIRECTORY_ENROLL + "/" + "index.html").exists());
    }
    /**
     * Test of run
     */
    @Test
    @Ignore 
    // Ignored because the ksa api is not known to the test.
    // investigate if the api can be provisioned through maven.
    public void testKSARun () {
        ServiceContractModel model = null;
        HtmlContractWriter writer = null;

        model = this.getKSAModel();
        this.validate(model);
        writer = new HtmlContractWriter(HTML_CONTRACT_DIRECTORY_KSA, model);
        writer.write("testVersion", DateUtility.asYMD(new Date()));

//        assertTrue(new File(HTML_CONTRACT_DIRECTORY_ENROLL + "/" + "index.html").exists());
    }
}
