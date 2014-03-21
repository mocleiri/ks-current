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
import java.util.Stack;

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.XmlType;

/**
 *
 * @author nwright
 */
public class MessageStructureHierarchyDumper {

    private ServiceContractModel model;
    private ModelFinder finder;
    private PrintStream out;

    public MessageStructureHierarchyDumper(PrintStream out,
            ServiceContractModel model) {
        this.out = out;
        this.model = model;
        this.finder = new ModelFinder(model);
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
        int len = str.length();
        StringBuffer buffer = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            // skip \n, \r, \r\n
            switch (c) {
                case '\n':
                case '\r': // do lookahead
                    if (i + 1 < len && str.charAt(i + 1) == '\n') {
                        i++;
                    }

                    buffer.append(" ");
                    break;
                default:
                    buffer.append(c);
            }
        }
        return buffer.toString();
    }

    public String calcId(MessageStructure ms, Stack<String> parents) {
        StringBuilder bldr = new StringBuilder();
        {
            for (String parent : parents) {
                bldr.append(parent);
                bldr.append(".");
            }
        }
        bldr.append(ms.getShortName());
        return bldr.toString();
    }

    public void writeTabbedData(MessageStructure ms, Stack<String> parents) {
        out.print(calcId(ms, parents));
        out.print("\t");
        out.print("");
        out.print("\t");
        out.print(ms.getXmlObject());
        out.print("\t");
        out.print(ms.getShortName());
        out.print("\t");
        out.print(ms.getName());
        out.print("\t");
        out.print(ms.getType());
        out.print("\t");
        out.print(clean(ms.getDescription()));
        out.print("\t");
        out.print(ms.getRequired());
        out.print("\t");
        out.print(ms.getCardinality());
        out.print("\t");
        out.print(ms.getXmlAttribute());
        out.print("\t");
        out.print(ms.getStatus());
        out.print("\t");
        out.print(ms.getImplNotes());
        out.println("");
        XmlType st = finder.findXmlType(stripList(ms.getType()));
        if (st.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
            if (!parents.contains(st.getName())) {
                parents.push(st.getName());
                for (MessageStructure childMs : finder.findMessageStructures(st.getName())) {
                    this.writeTabbedData(childMs, parents);
                }
                parents.pop();
            }
        }
    }

    private String stripList(String type) {
        if (type.endsWith("List")) {
            return type.substring(0, type.length() - "List".length());
        }
        return type;
    }
}
