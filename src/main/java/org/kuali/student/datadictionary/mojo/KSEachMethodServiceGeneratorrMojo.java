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
import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.kuali.student.common.mojo.AbstractKSMojo;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.impl.ServiceContractModelCache;
import org.kuali.student.contract.model.impl.ServiceContractModelQDoxLoader;
import org.kuali.student.contract.model.validation.ServiceContractModelValidator;
import org.kuali.student.contract.writer.service.EachMethodServiceWriter;

/**
 * The plugin entrypoint which is used to generate a java interface file for each
 * service method in the source files based on the contract definition
 * @phase generate-sources
 * @goal kseachmethodservicegenerator
 */
public class KSEachMethodServiceGeneratorrMojo extends AbstractKSMojo {

    /**
     * @parameter property="outputDirectory" default-value="${project.build.directory}/generated-sources"
     */
    private File outputDirectory;

    public File getOutputDirectory() {
        return outputDirectory;
    }

   
    public void setOutputDirectory(File htmlDirectory) {
        this.outputDirectory = htmlDirectory;
    }

       /**
     * @parameter property="rootPackage" default-value="org.kuali.student.enrollment"
     */
    private String rootPackage;

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }

    
    

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("generating separate java interface files for each method in the service contracts");
        ServiceContractModel model = this.getModel();
        this.validate(model);
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
//                throw new MojoExecutionException("Could not create directory "
                throw new IllegalArgumentException("Could not create directory "
                        + this.outputDirectory.getPath());
            }
        }
        String targetDirectory = this.outputDirectory.toString();
        EachMethodServiceWriter instance =
                new EachMethodServiceWriter(model,
                targetDirectory,
                rootPackage,
                null);
        instance.write();

    }
}
