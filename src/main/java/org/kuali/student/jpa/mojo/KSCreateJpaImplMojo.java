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
package org.kuali.student.jpa.mojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.kuali.student.common.mojo.AbstractKSMojo;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.util.ServicesFilter;
import org.kuali.student.contract.model.util.ServicesFilterByKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 * 
 *  @goal kscreatejpaimpl
 *  @phase site
 *  @requiresProject true
 */
public class KSCreateJpaImplMojo extends AbstractKSMojo {

    private static Logger log = LoggerFactory.getLogger(KSCreateJpaImplMojo.class);
    
    public KSCreateJpaImplMojo() {
    }
 
    private String targetDir;

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
            targetDir = "target/generated-jpa-impls";
        }
        List<String> servKeys = new ArrayList<String>();
//        servKeys.add ("Atp");
//        ServicesFilter filter = new ServicesFilterByKeys (servKeys);
        ServicesFilter filter = null;
        boolean isR1 = false;
        JpaImplWriter instance =
                new JpaImplWriter(model,
                targetDir,
                JpaImplWriter.ROOT_PACKAGE,
                filter,
                isR1);
        instance.write();

    }
    
    
    
    private static final String CORE_DIRECTORY = "D:/svn/ks/trunk/ks-api/ks-core-api/src/main";
    // "C:/svn/maven-dictionary-generator/trunk/src/main/java/org/kuali/student/core";
    private static final String COMMON_DIRECTORY = "D:/svn/ks/trunk/ks-api/ks-common-api/src/main/java";
    private static final String ENROLL_DIRECTORY = "D:/svn/ks/trunk/ks-api/ks-enroll-api/src/main/java";
    private static final String LUM_DIRECTORY = "D:/svn/ks/trunk/ks-api/ks-lum-api/src/main/java";
    private static final String RICE_CORE_API_DIRECTORY = "D:/svn/rice/rice-2.2.0/core/api/src/main/java";
    private static final String RICE_KIM_API_DIRECTORY = "D:/svn/rice/rice-2.2.0/kim/kim-api/src/main/java";
    private static final String RICE_KRMS_API_DIRECTORY = "D:/svn/rice/rice-2.2.0/krms/api/src/main/java";
    private static final String TEST_SOURCE_DIRECTORY =
            "src/test/java/org/kuali/student/contract/model/test/source";
    private static final String TARGET_GENERATED_SOURCES = "target/generated-sources";
    private static final String STANDALONE_MAIN_DIRECTORY = "D:/svn/ks/ks-standalone-admin-app/src/main";
    private static final String SERVICES_ENROLL_IMPL = "D:/svn/ks/services/ks-enroll/ks-enroll-impl/src/main/java";
    private static final String CM_CORE_API = "D:/svn/ks/CM-2.0/ks-api/ks-core-api/src/main/java";
    private static final String TRUNK_CORE_IMPL = "D:/svn/ks/trunk/ks-core/ks-core-impl/src/main/java";
    private static final String TRUNK_ENROLL_IMPL = "D:/svn/ks/trunk/ks-enroll/ks-enroll-impl/src/main/java";
    private static final String TRUNK_ENROLL_IMPL_KRMS_API = "D:/svn/ks/trunk/ks-enroll/ks-enroll-impl/src/main/java/org/kuali/rice/krms/api";
                                                   

    public static void main(String[] args) {
        log.info("execute");
        List<String> srcDirs = new ArrayList<String>();
        srcDirs.add(COMMON_DIRECTORY);
        srcDirs.add(CORE_DIRECTORY);
        srcDirs.add(LUM_DIRECTORY);
        srcDirs.add(ENROLL_DIRECTORY);
//        srcDirs.add(RICE_KIM_API_DIRECTORY);
//        srcDirs.add(RICE_CORE_API_DIRECTORY);
//        srcDirs.add(RICE_KRMS_API_DIRECTORY);
//        srcDirs.add(TRUNK_ENROLL_IMPL_KRMS_API);
        KSCreateJpaImplMojo instance = new KSCreateJpaImplMojo();
        Map pluginContext = new HashMap();
        MavenProject project = new MavenProject();
        pluginContext.put("project", project);
        instance.setPluginContext(pluginContext);
        instance.setSourceDirs(srcDirs);
//        instance.setTargetDir(SERVICES_ENROLL_IMPL);
//        instance.setTargetDir(CM_CORE_API);
        instance.setTargetDir(TRUNK_ENROLL_IMPL);
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
