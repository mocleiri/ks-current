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
package org.kuali.student.contract.writer.service;

import java.util.List;

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.kuali.student.contract.model.util.ModelFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class EachMethodServiceWriterForOneService {
    
    private static Logger log = LoggerFactory.getLogger(EachMethodServiceWriterForOneService.class);

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;

    public EachMethodServiceWriterForOneService(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey) {
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
    }

    /**
     * Write out the entire file
     * @param out
     */
    public void write() {
        List<ServiceMethod> methods = finder.getServiceMethodsInService(servKey);
        if (methods.isEmpty()) {
            log.warn("No methods defined for servKey: " + servKey);
            return;
        }

        // the service method
        log.info("Generating info interfaces");
        for (ServiceMethod method : methods) {
            log.info("Generating method for service " + method.getService() + "." + method.getName());
            new EachMethodServiceWriterForOneMethod(model, directory, rootPackage, servKey, method).write();
        }

    }

}
