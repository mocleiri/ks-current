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

import org.kuali.student.contract.model.XmlType;

/**
 *
 * @author nwright
 */
public class XmlTypeDumper {

    private XmlType xmlType;
    private PrintStream out;

    public XmlTypeDumper(XmlType xmlType,
            PrintStream out) {
        this.xmlType = xmlType;
        this.out = out;
    }

    public void dump() {
        out.println(xmlType.getName()
                + " - " + xmlType.getPrimitive()
                + " - " + xmlType.getDesc()
                + " - Http://XXX" + xmlType.getUrl());
    }

    public void writeTabbedHeader() {
        out.print("name");
        out.print("\t");
        out.print("desc");
        out.print("\t");
        out.print("primitive");
        out.print("\t");
        out.print("hasOwnType");
        out.print("\t");
        out.print("hasOwnState");
        out.print("\t");
        out.print("hasOwnCreateUpdate");
        out.print("\t");
        out.print("Service");
        out.print("\t");
        out.print("examples");
        out.print("\t");
        out.print("javaPackage");
        out.print("\t");
        out.print("comments");

        out.println("");
    }

    public void writeTabbedData() {
        out.print(xmlType.getName());
        out.print("\t");
        out.print(xmlType.getDesc());
        out.print("\t");
        out.print(xmlType.getPrimitive());
        out.print("\t");
        out.print(xmlType.hasOwnType());
        out.print("\t");
        out.print(xmlType.hasOwnState());
        out.print("\t");
        out.print(xmlType.hasOwnCreateUpdate());
        out.print("\t");
        out.print(xmlType.getService());
        out.print("\t");
        out.print(xmlType.getExamples());
        out.print("\t");
        out.print(xmlType.getJavaPackage());
        out.print("\t");
        out.print(xmlType.getComments());
        out.println("");
    }
}
