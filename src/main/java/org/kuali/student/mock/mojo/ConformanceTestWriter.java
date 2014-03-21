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

import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.util.ServicesFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class writers the conformance tests for service
 *
 * @author Mezba Mahtab (mezba.mahtab@utoronto.ca)
 */
public class ConformanceTestWriter extends MockImplWriter {

    private static final Logger log = LoggerFactory.getLogger(ConformanceTestWriter.class);
    
    ///////////////////////
    // CONSTRUCTOR
    ///////////////////////

    public ConformanceTestWriter(ServiceContractModel model, String directory, String rootPackage, ServicesFilter filter, boolean isR1) {
        super (model, directory, rootPackage, filter, isR1);
    }

    ////////////////////////
    // FUNCTIONAL
    ////////////////////////

    /**
     * Write out the entire file
     */
    public void write() {
        this.validate();
        for (Service service : filterServices()) {
            log.info ("************** generating conformance test for service = " + service.getKey() + " **************");
            
            try {
                new ConformanceTestWriterForOneService(model, directory, rootPackage, service.getKey(), isR1).write();
            } catch (Exception e) {
                log.error ("Failed to generate conformance test for service = " + service.getKey(), e);
            }
        }
    }

}
