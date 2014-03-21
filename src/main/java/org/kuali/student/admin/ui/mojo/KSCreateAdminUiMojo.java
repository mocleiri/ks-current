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
package org.kuali.student.admin.ui.mojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.kuali.student.common.mojo.AbstractKSMojo;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.util.ServicesFilterByKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 *
 * @goal kscreateadminui
 * @phase none
 * @requiresProject true
 */
public class KSCreateAdminUiMojo extends AbstractKSMojo {

    private static final Logger log = LoggerFactory.getLogger(KSCreateAdminUiMojo.class);
    
    public KSCreateAdminUiMojo() {
    }
    public String targetDir;

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ServiceContractModel model = getModel();
        validate(model);
        if (targetDir == null) {
            targetDir = "target/generated-sources/admin-ui";
        }
//        ServicesFilterKualiStudentOnly filter = new ServicesFilterKualiStudentOnly ();
        List<String> servKeys = new ArrayList<String>();
        servKeys.add("Atp");
//        servKeys.add("Type");
//        servKeys.add("State");
//        servKeys.add("LRC");
        servKeys.add("Organization");
        ServicesFilterByKeys filter = new ServicesFilterByKeys(servKeys);

        AdminUiWriter instance =
                new AdminUiWriter(model,
                targetDir,
                AdminUiWriter.DEFAULT_ROOT_PACKAGE,
                // TODO: inject filters as part of the Mojo parameters
                filter);
        instance.write();

    }
    private static final String CORE_DIRECTORY = "D:/svn/org-fixer/ks-api/ks-core-api/src/main";
    // "C:/svn/maven-dictionary-generator/trunk/src/main/java/org/kuali/student/core";
    private static final String COMMON_DIRECTORY = "D:/svn/org-fixer/ks-api/ks-common-api/src/main/java";
    private static final String ENROLL_DIRECTORY = "D:/svn/org-fixer/ks-api/ks-enroll-api/src/main/java";
    private static final String LUM_DIRECTORY = "D:/svn/org-fixer/ks-api/ks-lum-api/src/main/java";
    private static final String RICE_CORE_API_DIRECTORY = "D:/svn/rice/rice-2.2.0-M3/core/api/src/main/java";
    private static final String RICE_KIM_API_DIRECTORY = "D:/svn/rice/rice-2.2.0-M3/kim/kim-api/src/main/java";
    private static final String TEST_SOURCE_DIRECTORY =
            "src/test/java/org/kuali/student/contract/model/test/source";
    private static final String TARGET_GENERATED_SOURCES = "target/generated-sources";
    private static final String STANDALONE_MAIN_DIRECTORY = "D:/svn/ks/ks-standalone-admin-app/src/main";
    private static final String ORG_FIXER_CORE_UI_MAIN_DIRECTORY = "D:/svn/org-fixer/ks-core/ks-core-ui/src/main";

    public static void main(String[] args) {
        log.info("execute");
        List<String> srcDirs = new ArrayList<String>();
        srcDirs.add(COMMON_DIRECTORY);
        srcDirs.add(CORE_DIRECTORY);
        srcDirs.add(LUM_DIRECTORY);
        srcDirs.add(ENROLL_DIRECTORY);
//        srcDirs.add(RICE_KIM_API_DIRECTORY);
//        srcDirs.add(RICE_CORE_API_DIRECTORY);
        KSCreateAdminUiMojo instance = new KSCreateAdminUiMojo();
        Map pluginContext = new HashMap();
        MavenProject project = new MavenProject();
        pluginContext.put("project", project);
        instance.setPluginContext(pluginContext);
        instance.setSourceDirs(srcDirs);
        instance.setTargetDir(ORG_FIXER_CORE_UI_MAIN_DIRECTORY);
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
