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

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class writes the conformance test for one service.
 *
 * @author Mezba Mahtab (mezba.mahtab@utoronto.ca)
 */
public class ConformanceTestWriterForOneService extends MockImplWriterForOneService {
    
    private static Logger log = LoggerFactory.getLogger(ConformanceTestWriterForOneService.class);

    /////////////////////////////
    // CONSTRUCTOR
    /////////////////////////////

    public ConformanceTestWriterForOneService
            (ServiceContractModel model,
             String directory,
             String rootPackage,
             String servKey,
             boolean isR1) {
        super (model, directory, rootPackage, servKey, isR1);
    }

    ////////////////////////
    // FUNCTIONAL
    ////////////////////////

    /**
     * Write out the entire file
     */
    public void write() {
        List<ServiceMethod> methods = finder.getServiceMethodsInService(servKey);
        if (methods.size() == 0) {
            log.warn("No methods defined for servKey: " + servKey);
            return;
        }

        // the main servKey
        log.info("Generating Conformance Tests for " + servKey);
        new ConformanceTestBaseCrudClassServiceWriter(model, directory, rootPackage, servKey, methods, isR1).write();
        new ConformanceTestExtendedCrudClassServiceWriter(model, directory, rootPackage, servKey, methods, isR1).write();
    }
}
