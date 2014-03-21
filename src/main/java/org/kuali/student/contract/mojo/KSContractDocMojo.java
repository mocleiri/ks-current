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
package org.kuali.student.contract.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.joda.time.DateTime;
import org.kuali.student.common.mojo.AbstractKSMojo;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.util.DateUtility;
import org.kuali.student.contract.model.util.HtmlContractWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The plugin entrypoint which is used to generate the html wiki doc of the service interface.
 * @goal kscontractdoc
 * @phase site
 * @requiresProject true
 */
public class KSContractDocMojo extends AbstractKSMojo {
    
    private static Logger log = LoggerFactory.getLogger(KSContractDocMojo.class);

    /**
     * @parameter property="htmlDirectory" default-value="${project.build.directory}/site/services/contractdocs"
     */
    private File htmlDirectory;

    public File getHtmlDirectory() {
        return htmlDirectory;
    }
  
    public void setHtmlDirectory(File htmlDirectory) {
        this.htmlDirectory = htmlDirectory;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    	
    	MavenProject project = (MavenProject) getPluginContext().get("project");
    	
    	String formattedDate = DateUtility.asYMDHMInEasternTimeZone(new DateTime());
    	
        ServiceContractModel model = null;
        HtmlContractWriter writer = null;
        getLog().info("publishing wiki contracts");
        model = this.getModel();
        this.validate(model);
        getLog().info("publishing to = " + this.htmlDirectory.toString());
        writer = new HtmlContractWriter(htmlDirectory.toString(), model);
        writer.write(project.getVersion(), formattedDate);


    }
    
    
    private static final String CORE_DIRECTORY = "D:/svn/ks/ks-api/ks-core-api/src/main";
    // "C:/svn/maven-dictionary-generator/trunk/src/main/java/org/kuali/student/core";
    private static final String COMMON_DIRECTORY = "D:/svn/ks/ks-api/ks-common-api/src/main/java";
    private static final String ENROLL_DIRECTORY = "D:/svn/ks/ks-api/ks-enroll-api/src/main/java";
    private static final String LUM_DIRECTORY = "D:/svn/ks/ks-api/ks-lum-api/src/main/java";
    private static final String KSAP_API_DIRECTORY = "D:\\svn\\ks\\trunk\\ks-api\\ks-ap-api\\src\\main\\java";
    private static final String KME_API_DIRECTORY = "D:\\svn\\kplus2\\kme\\bulletin\\api\\src\\main\\java\\org\\kuali\\mobility\\bulletin";
    private static final String KSA_DIRECTORY = "D:/svn/ks/ksa/ksa-core/src/main/java";
    private static final String TEST_SOURCE_DIRECTORY =
            "src/test/java/org/kuali/student/contract/model/test/source";
    private static final String TARGET_GENERATED_SOURCES = "target/generated-sources";
    private static final String STANDALONE_MAIN_DIRECTORY = "D:/svn/ks/ks-standalone-admin-app/src/main";

    private static final String RICE_CORE_API_DIRECTORY = "D:/svn/rice/rice-2.2.0/core/api/src/main/java";
    private static final String RICE_KIM_API_DIRECTORY = "D:/svn/rice/rice-2.2.0/kim/kim-api/src/main/java";
    private static final String RICE_KRMS_API_DIRECTORY = "D:/svn/rice/rice-2.2.0/krms/api/src/main/java";
    
    
    private static final String TRUNK_CORE_IMPL = "D:/svn/ks/trunk/ks-core/ks-core-impl/src/main/java";
    private static final String TRUNK_ENROLL_IMPL = "D:/svn/ks/trunk/ks-core/ks-core-impl/src/main/java";
    private static final String TRUNK_ENROLL_IMPL_KRMS_API = "D:/svn/ks/trunk/ks-enroll/ks-enroll-impl/src/main/java/org/kuali/rice/krms/api";
                                                   
    
    public static void main(String[] args) {
        log.info("execute");
        List<String> srcDirs = new ArrayList<String>();
        srcDirs.add(COMMON_DIRECTORY);
        srcDirs.add(CORE_DIRECTORY);
        srcDirs.add(LUM_DIRECTORY);
        srcDirs.add(ENROLL_DIRECTORY);        
//        srcDirs.add(RICE_KIM_API_DIRECTORY);     
        srcDirs.add(KSAP_API_DIRECTORY);    
//        srcDirs.add(KME_API_DIRECTORY);
        srcDirs.add(RICE_CORE_API_DIRECTORY);
        srcDirs.add(RICE_KRMS_API_DIRECTORY);
//        srcDirs.add(TRUNK_ENROLL_IMPL_KRMS_API);
        KSContractDocMojo instance = new KSContractDocMojo();
        Map pluginContext = new HashMap();
        MavenProject project = new MavenProject();
        pluginContext.put("project", project);
        instance.setPluginContext(pluginContext);
        instance.setSourceDirs(srcDirs);
        instance.setHtmlDirectory(new File (TARGET_GENERATED_SOURCES));
        try {
            instance.execute();
        } catch (MojoExecutionException ex) {
            throw new RuntimeException(ex);
        } catch (MojoFailureException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
