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
package org.kuali.student.contract.model.test.source;


/**
 * A common interface pattern for service entities.
 *
 * @author tom
 * @Since Tue Apr 05 14:22:34 EDT 2011
 */

public interface Entity
    extends HasPrimaryKey,
	    HasType,
	    HasState,
	    HasAttributesAndMeta {

    /**
     * A display name for this entity.
     * @name Name
     */

    public String getName();


    /**
     * A description of the entity.
     * @name Description
     */

    public RichTextInfo getDescr();
}
