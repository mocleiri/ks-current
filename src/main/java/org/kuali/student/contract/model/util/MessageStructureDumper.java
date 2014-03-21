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
package org.kuali.student.contract.model.util;

import java.io.PrintStream;

import org.kuali.student.contract.model.MessageStructure;

/**
 *
 * @author nwright
 */
public class MessageStructureDumper {

    private MessageStructure messageStructure;
    private PrintStream out;

    public MessageStructureDumper(MessageStructure messageStructure,
            PrintStream out) {
        this.messageStructure = messageStructure;
        this.out = out;
    }

    public void dump() {
        out.println(
                messageStructure.getXmlObject() + "." + messageStructure.getShortName()
                + " - " + messageStructure.getName() + " - " + messageStructure.getType()
                + " http:XXX" + messageStructure.getUrl() + " - " + messageStructure.getDescription());
    }

    public void writeTabbedHeader() {
        out.print("id");
        out.print("\t");
        out.print("Action");
        out.print("\t");
        out.print("xmlObject");
        out.print("\t");
        out.print("ShortName");
        out.print("\t");
        out.print("Name");
        out.print("\t");
        out.print("Type");
        out.print("\t");
        out.print("Description");
        out.print("\t");
        out.print("Required");
        out.print("\t");
        out.print("Cardinality");
        out.print("\t");
        out.print("XMLAttribute");
        out.print("\t");
        out.print("Status");
        out.print("\t");
        out.print("Feedback");
        out.println("");
    }

    private String clean(String str) {
        str = str.replace("\n", " ");
        str = str.replace("\t", " ");
        str = str.replace("\r", str);
        str = str.replace("\f", " ");
        return str;
    }

    public void writeTabbedData() {
        out.print(messageStructure.getId());
        out.print("\t");
        out.print("");
        out.print("\t");
        out.print(messageStructure.getXmlObject());
        out.print("\t");
        out.print(messageStructure.getShortName());
        out.print("\t");
        out.print(messageStructure.getName());
        out.print("\t");
        out.print(messageStructure.getType());
        out.print("\t");
        out.print(clean(messageStructure.getDescription()));
        out.print("\t");
        out.print(messageStructure.getRequired());
        out.print("\t");
        out.print(messageStructure.getCardinality());
        out.print("\t");
        out.print(messageStructure.getXmlAttribute());
        out.print("\t");
        out.print(messageStructure.getStatus());
        out.print("\t");
        out.print(messageStructure.getImplNotes());
        out.println("");
    }
}
