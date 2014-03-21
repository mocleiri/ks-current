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
package org.kuali.student.mock.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.kuali.student.common.mojo.AbstractKSMojo;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class associates a mojo to create the conformance tests.
 *
 * @goal kscreateconftest
 * @phase site
 * @requiresProject true
 * @author Mezba Mahtab (mezba.mahtab@utoronto.ca)
 */
public class KSCreateConformanceTestMojo extends AbstractKSMojo {

    private static Logger log = LoggerFactory.getLogger(KSCreateConformanceTestMojo.class);
    
    ///////////////////////////
    // Data Variables
    ///////////////////////////

    private String targetDir;

    ///////////////////////////
    // Getters and Setters
    ///////////////////////////

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    ///////////////////////////
    // Constructor
    ///////////////////////////

    public KSCreateConformanceTestMojo() {}

    ////////////////////////////
    // Functional Methods
    ////////////////////////////

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        ServiceContractModel model = getModel();
        validate(model);
        if (targetDir == null) {
            targetDir = "target/generated-conf-tests";
        }
        boolean isR1 = false;
        ConformanceTestWriter instance =
                new ConformanceTestWriter(model,
                        targetDir,
                        MockImplWriter.ROOT_PACKAGE,
                        null,
                        isR1);
        instance.write();
    }

    public static void main (String [] args) {
        log.info("execute");
        List<String> srcDirs = new ArrayList<String>();
        srcDirs.add("D:/svn/ks/ks-api/ks-common-api/src/main/java"); // common
        srcDirs.add("D:/svn/ks/ks-api/ks-core-api/src/main"); // core
        srcDirs.add("D:/svn/ks/ks-api/ks-lum-api/src/main/java"); // lum
        srcDirs.add("D:/svn/ks/ks-api/ks-enroll-api/src/main/java"); // enroll
        KSCreateConformanceTestMojo instance = new KSCreateConformanceTestMojo();
        Map pluginContext = new HashMap();
        MavenProject project = new MavenProject();
        pluginContext.put("project", project);
        instance.setPluginContext(pluginContext);
        instance.setSourceDirs(srcDirs);
        instance.setTargetDir("D:/svn/ks/ks-api//target/generated-conf-tests");
        try {
            instance.execute();
            //        assertTrue(new File(instance.getOutputDirectory() + "/" + "ks-LprInfo-dictionary.xml").exists());
        } catch (MojoExecutionException ex) {
            throw new RuntimeException(ex);
        } catch (MojoFailureException ex) {
            throw new RuntimeException(ex);
        }

    }


}
