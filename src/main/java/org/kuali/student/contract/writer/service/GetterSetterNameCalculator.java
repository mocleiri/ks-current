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

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.writer.JavaClassWriter;

/**
 *
 * @author nwright
 */
public class GetterSetterNameCalculator {

    private MessageStructure ms;
    private JavaClassWriter writer;
    private ServiceContractModel model;

    public GetterSetterNameCalculator(MessageStructure ms,
            JavaClassWriter writer,
            ServiceContractModel model) {
        this.ms = ms;
        this.writer = writer;
        this.model = model;
    }

    /**
     * Returns the name of the getter method.
     */
    public String calcGetter() {
        if (calcFieldTypeToUse(ms.getType()).equals("Boolean")) {
            if (ms.getShortName().toLowerCase().startsWith("is")) {
                return calcInitLower(ms.getShortName());
            }
            return "is" + calcInitUpper(ms.getShortName());
        }
        return "get" + calcInitUpper(ms.getShortName());
    }

    /**
     * Returns the name of the setter method.
     */
    public String calcSetter() {
        if (calcFieldTypeToUse(ms.getType()).equals("Boolean")) {
            if (ms.getShortName().toLowerCase().startsWith("is")) {
                return "set" + calcInitUpper(ms.getShortName().substring(2));
            }
        }
        return "set" + calcInitUpper(ms.getShortName());
    }

    public static String dot2Camel (String name) {
        StringBuilder sb = new StringBuilder ();
        boolean upper = true;
        for (char c : name.toCharArray()) {
            if (c == '.') {
                upper = true;
                continue;
            }
            if (upper) {
                c = Character.toUpperCase(c);
                upper = false;
            }
            sb.append (c);
        }
        return sb.toString();
    }
    public static String calcInitUpper(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String calcInitLower(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public String calcFieldTypeToUse(String type) {
        return MessageStructureTypeCalculator.calculate(writer, model, type, type, null);
    }

    public static String stripList(String str) {
        if (str.endsWith("List")) {
            return str.substring(0, str.length() - "List".length());
        }
        return str;
    }
}
