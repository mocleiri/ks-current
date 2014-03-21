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
package org.kuali.student.contract.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.writer.HtmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class HtmlContractWriter {
	
	private static final Logger log = LoggerFactory.getLogger(HtmlContractWriter.class);

    private HtmlWriter writer;
    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;

    public HtmlContractWriter(String directory,
            ServiceContractModel model) {
        this.writer = new HtmlWriter(directory, "index.html",
                "Service Contracts Index");
        this.directory = directory;
        this.model = model;
        this.finder = new ModelFinder(this.model);
    }

    public void write(String projectVersion, String formattedDate) {
        this.writeIndexPage(projectVersion, formattedDate);
        for (Service service : model.getServices()) {
            HtmlContractServiceWriter swriter = new HtmlContractServiceWriter(service,
                    directory,
                    model);
            swriter.write(projectVersion, formattedDate);
        }
        for (XmlType xmlType : model.getXmlTypes()) {
            HtmlContractMessageStructureWriter msWriter =
                    new HtmlContractMessageStructureWriter(
                    xmlType,
                    directory,
                    model);
            msWriter.write(projectVersion, formattedDate);
        }
    }
    private static final Comparator<XmlType> XML_TYPE_NAME_COMPARATOR =
            new Comparator<XmlType>() {

                @Override
                public int compare(XmlType e1, XmlType e2) {
                    return e1.getName().toLowerCase().compareTo(e2.getName().toLowerCase());
                }
            };
    private static final Comparator<Service> SERVICE_NAME_COMPARATOR =
            new Comparator<Service>() {

                @Override
                public int compare(Service e1, Service e2) {
                    return e1.getName().compareTo(e2.getName());
                }
            };
    private static final Comparator<Service> SERVICE_IMPL_NAME_COMPARATOR =
            new Comparator<Service>() {

                @Override
                public int compare(Service e1, Service e2) {
                    String x1 = calcArea(e1) + "." + e1.getName();
                    String x2 = calcArea(e2) + "." + e2.getName();
                    return x1.compareTo(x2);
                }
            };

    private String getOtherHomeTag () {
        return "<a href=\"../dictionarydocs/index.html\">Dictionary Docs Home</a>";
//        return "";
    }
    private void writeIndexPage(String projectVersion, String formattedDate) {
        
        String otherHome = this.getOtherHomeTag();
        VersionLinesUtility.writeVersionTag(writer, "<a href=\"index.html\">Home</a>", otherHome, projectVersion, formattedDate);
        
        writer.writeTag("h1", "Service Contracts");
        
        

        writer.indentPrintln(
                "<div class=\"panel\" style=\"background-color: rgb(255, 255, 255); border: 1px solid rgb(204, 204, 204);\">");
        writer.indentPrintln(
                "<div class=\"panelHeader\" style=\"border-bottom: 1px solid rgb(204, 204, 204); background-color: rgb(238, 238, 238);\">");
        writer.indentPrintln("<b><a name=\"Services\"></a>Services</b>");
        writer.indentPrintln(
                "</div><div class=\"panelContent\" style=\"background-color: rgb(255, 255, 255);\">");
        writer.indentPrintln("<ul>");
        List<Service> services = new ArrayList<Service>(model.getServices());
        Collections.sort(services, SERVICE_IMPL_NAME_COMPARATOR);
        String oldArea = "";
        for (Service service : services) {
            String newArea = calcArea(service);
            if (!newArea.equals(oldArea)) {
                if (!oldArea.isEmpty()) {
                    writer.indentPrintln("</ul>");
                    writer.decrementIndent();
                }
                writer.indentPrintln("<li>" + calcArea(service) + "</li>");
                writer.incrementIndent();
                writer.indentPrintln("<ul>");
                oldArea = newArea;
            }
            writer.indentPrint("<li>");
            writer.print("<a href=\"" + service.getKey() + "Service" + ".html"
                    + "\">" + service.getName() + "</a>");
            writer.print("</li>");
        }
        writer.indentPrintln("</ul>");
        writer.decrementIndent();
        writer.indentPrintln("</ul>");        
        writer.indentPrintln("</div>");
        writer.indentPrintln("</div>");

        this.writeMainOrRootList();

        this.writeAlphabeticalList();

        writer.writeHeaderBodyAndFooterOutToFile();

    }

    private static String calcArea(Service service) {
        return calcArea(service.getImplProject());
    }

    private static String calcArea(String implProject) {
        // group all student services together
        if (implProject.startsWith("org.kuali.student.")) {
            implProject = implProject.substring("org.kuali.student.".length());
//            return "Kuali Student Services";
        }
        if (implProject.startsWith("org.kuali.")) {
            implProject = implProject.substring("org.kuali.".length());
        }
        if (implProject.contains(".api.")) {
            implProject = implProject.substring(0, implProject.indexOf(".api."));
        }
        if (implProject.contains(".api.")) {
            implProject = implProject.substring(0, implProject.indexOf(".api."));
        }
        if (implProject.startsWith("r2.")) {
            implProject = implProject.substring("r2.".length());
        }
        if (implProject.contains(".class1.")) {
            implProject = implProject.substring(0, implProject.indexOf(".class1.")) + 
                    implProject.substring(implProject.indexOf(".class1.") + ".class1".length());
        }
        if (implProject.endsWith(".service")) {
            implProject = implProject.substring(0, implProject.length() - ".service".length());
        }
        return implProject;
    }

    private void writeMainOrRootList() {
        Stack<String> stack = new Stack();
        List<XmlType> types = this.getMainMessageStructures();
        writer.indentPrintln(
                "<div class=\"panel\" style=\"background-color: rgb(255, 255, 255); border: 1px solid rgb(204, 204, 204);\">");
        writer.indentPrintln(
                "<div class=\"panelHeader\" style=\"border-bottom: 1px solid rgb(204, 204, 204); background-color: rgb(238, 238, 238);\">");
        writer.indentPrintln(
                "<b><a name=\"MessageStructures\"></a> " + types.size() + " Main (root) Message Structures</b>");
        writer.indentPrintln(
                "</div><div class=\"panelContent\" style=\"background-color: rgb(255, 255, 255);\">");
        writer.indentPrintln("<ul>");
        for (XmlType type : types) {
            this.writeLink(type);
            if (!stack.contains(type.getName())) {
                stack.push(type.getName());
                this.writeSubStructures(type, stack);
                stack.pop();
            }
        }
        writer.indentPrintln("</ul>");
        writer.indentPrintln("</div>");
        writer.indentPrintln("</div>");
    }

    private String stripListOffEnd(String name) {
        if (name.endsWith("List")) {
            return name.substring(0, name.length() - "List".length());
        }
        return name;
    }

    private boolean shouldWriteSubStructure(XmlType st) {
        if (!st.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
            return false;
        }
        return true;
    }
    
    private void writeSubStructures(XmlType type, Stack<String> stack) {
        boolean first = true;
        for (MessageStructure ms : finder.findMessageStructures(type.getName())) {
            XmlType st = finder.findXmlType(this.stripListOffEnd(ms.getType()));
            if (st == null) {
            	log.error (ms.getType() + " does not exist in the list of types with parents " + calcParents(stack));
            	continue;
            }
            if (!shouldWriteSubStructure (st)) {
                continue;
            }
            if (first) {
                first = false;
                writer.indentPrintln("<ul>");
            }
            this.writeLink(st);
            if (!stack.contains(st.getName())) {
                stack.push(st.getName());
                this.writeSubStructures(st, stack);
                stack.pop();
            }
        }
        if (!first) {
            writer.indentPrintln("</ul>");
        }
    }

    private String calcParents(Stack<String> stack) {
        StringBuilder sb = new StringBuilder();
        String dot = "";
        Enumeration<String> en = stack.elements();
        while (en.hasMoreElements()) {
            sb.append(dot);
            dot = ".";
            sb.append(en.nextElement());
        }
        return sb.toString();
    }

    private void writeLink(XmlType type) {
        writer.indentPrint("<li>");
        writer.print("<a href=\"" + type.getName() + ".html"
                + "\">" + type.getName() + "</a>");
        writer.print("</li>");
    }

    private List<XmlType> getMainMessageStructures() {
        List<XmlType> types = new ArrayList(model.getXmlTypes().size());
        for (XmlType type : this.getComplexMessageStructures()) {
            if (isMainMessageStructure(type)) {
                types.add(type);
            }
        }
        Collections.sort(types, XML_TYPE_NAME_COMPARATOR);
        return types;
    }

    private boolean isMainMessageStructure(XmlType xmlType) {
        if (!HtmlContractMessageStructureWriter.calcOtherXmlTypeUsages(model,
                xmlType).isEmpty()) {
            return false;
        }
        return true;
    }

    private List<XmlType> getComplexMessageStructures() {
        List<XmlType> types = new ArrayList(model.getXmlTypes().size());
        for (XmlType type : model.getXmlTypes()) {
            if (type.getPrimitive() == null) {
                throw new NullPointerException(type.getName()
                        + " has no primitive flag set");
            }
            if (type.getPrimitive().equals(XmlType.COMPLEX)) {
                types.add(type);
            }
        }
        Collections.sort(types, XML_TYPE_NAME_COMPARATOR);
        return types;
    }

    private void writeAlphabeticalList() {
        List<XmlType> types = this.getComplexMessageStructures();
        writer.indentPrintln(
                "<div class=\"panel\" style=\"background-color: rgb(255, 255, 255); border: 1px solid rgb(204, 204, 204);\">");
        writer.indentPrintln(
                "<div class=\"panelHeader\" style=\"border-bottom: 1px solid rgb(204, 204, 204); background-color: rgb(238, 238, 238);\">");
        writer.indentPrintln(
                "<b><a name=\"MessageStructures\"></a>All " + types.size() + " Message Structures in Alphabetical Order</b>");
        writer.indentPrintln(
                "</div><div class=\"panelContent\" style=\"background-color: rgb(255, 255, 255);\">");
        writer.indentPrintln("<ul>");
        for (XmlType type : types) {
            this.writeLink(type);
        }
        writer.indentPrintln("</ul>");
        writer.indentPrintln("</div>");
        writer.indentPrintln("</div>");
    }
}
