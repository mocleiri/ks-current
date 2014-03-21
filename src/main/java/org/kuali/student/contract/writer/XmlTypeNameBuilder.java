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
package org.kuali.student.contract.writer;

/**
 *
 * @author nwright
 */
public class XmlTypeNameBuilder {

    private String name;
    private String javaPackage;

    public XmlTypeNameBuilder(String name, String javaPackage) {
        this.name = name;
        this.javaPackage = javaPackage;
    }

    public String build() {
        return javaPackage + "." + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
