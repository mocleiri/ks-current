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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.student.contract.model;

import java.util.List;
import java.util.Map;
//import org.kuali.student.core.assembly.data.LookupMetadata;

/**
 *
 * @author nwright
 */
public interface OrchestrationModel {

    /**
     * get Orchestration Objects
     * @return
     */
    public Map<String, OrchestrationObject> getOrchestrationObjects();

    /**
     * get look ups for bank of lookups
     * @return
     */
// public List<LookupMetadata> getLookups ();
    public List<Object> getLookups();
}
