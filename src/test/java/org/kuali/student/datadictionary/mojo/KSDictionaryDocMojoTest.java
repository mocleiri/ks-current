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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.student.validation.decorator.mojo.ValidationDecoratorWriterForOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author nwright
 */
@Ignore // need to figure out how to provide the path info for locating the files when running the tests.
public class KSDictionaryDocMojoTest {
    private static Logger log = LoggerFactory.getLogger(KSDictionaryDocMojoTest.class);
    
    private static final String DICTIONARY_DOC_DIRECTORY = "target/site/dictionary";
    public KSDictionaryDocMojoTest() {
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
     * Test of execute method, of class KSDictionaryDocMojo.
     */
    @Test
    public void testExecute() throws Exception {
        log.info("execute");
        KSDictionaryDocMojo instance = new KSDictionaryDocMojo();
        List<String> supportFiles = new ArrayList<String> ();
        supportFiles.add("ks-base-dictionary.xml");          
        supportFiles.add("ks-base-dictionary-validchars.xml");
        supportFiles.add("org/kuali/rice/krad/bo/datadictionary/DataDictionaryBaseTypes.xml");
        supportFiles.add("org/kuali/rice/krad/uif/UifControlDefinitions.xml");
        supportFiles.add("org/kuali/rice/krad/uif/UifWidgetDefinitions.xml");        
        supportFiles.add("org/kuali/rice/krad/uif/UifFieldDefinitions.xml");
        supportFiles.add("org/kuali/rice/krad/uif/UifGroupDefinitions.xml");
        instance.setSupportFiles(supportFiles);        
        instance.setHtmlDirectory(new File (DICTIONARY_DOC_DIRECTORY));
		
		instance.setTestDictionaryFile("ks-test-AtpInfo-dictionary.xml");
		
        instance.execute();
        assertTrue(new File(instance.getHtmlDirectory() + "/" + "index.html").exists());        
        assertTrue(new File(instance.getHtmlDirectory() + "/" + "AtpInfo.html").exists());
    }
}
