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
package org.kuali.student.validation.decorator.mojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.kuali.student.common.mojo.AbstractKSMojo;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.impl.ServiceContractModelCache;
import org.kuali.student.contract.model.impl.ServiceContractModelQDoxLoader;
import org.kuali.student.contract.model.validation.ServiceContractModelValidator;

/**
 *
 * @author nwright
 * 
 *  @goal kscreatevdec
 *  @phase site
 *  @requiresProject true
 */
public class KSCreateValidationDecoratorMojo extends AbstractKSMojo {

    public KSCreateValidationDecoratorMojo() {
    }
   

   
    @Override
	public void execute() throws MojoExecutionException, MojoFailureException {
	
        ServiceContractModel model = getModel();
        validate(model);
        String targetDir = "target/generated-validation-decorators";
        ValidationDecoratorWriter instance =
                new ValidationDecoratorWriter(model,
                targetDir,
                ValidationDecoratorWriter.DEFAULT_ROOT_PACKAGE,
                null);
        instance.write();

    }
}
