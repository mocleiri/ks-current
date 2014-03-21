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
package org.kuali.student.admin.ui.mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.kuali.student.contract.model.Lookup;
import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.impl.ServiceContractModelQDoxLoader;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.XmlWriter;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class AdminUiLookupViewBeanWriter {

	private static final Logger log = LoggerFactory.getLogger(AdminUiLookupViewBeanWriter.class);
    
    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;
    private Service service;
    private XmlType xmlType;
    private List<ServiceMethod> methods;
    private XmlWriter out;
    String fileName;
    String fullDirectoryPath;

    public AdminUiLookupViewBeanWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            XmlType xmlType,
            List<ServiceMethod> methods) {
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
        service = finder.findService(servKey);
        this.xmlType = xmlType;
        this.methods = methods;
    }

    /**
     * Write out the entire file
     *
     * @param out
     */
    public void write() {
        initXmlWriter();
        writeBoilerPlate();
        writeBean();
    }

    private void writeBean() {
        String infoClass = GetterSetterNameCalculator.calcInitUpper(xmlType.getName());
        String serviceClass = GetterSetterNameCalculator.calcInitUpper(service.getName());
        String serviceVar = GetterSetterNameCalculator.calcInitLower(service.getName());
        String serviceContract = service.getImplProject() + "." + service.getName();
        String lookupable = AdminUiLookupableWriter.calcPackage(servKey, rootPackage, xmlType) + "."
                + AdminUiLookupableWriter.calcClassName(servKey, xmlType);
        String viewId = "KS-" + infoClass + "-AdminLookupView";
        out.println("");
        out.incrementIndent();
        out.indentPrintln("<import resource=\"classpath:ks-" + infoClass + "-dictionary.xml\"/>");
        out.indentPrintln("<import resource=\"classpath:UifKSDefinitions.xml\"/>");
        out.indentPrintln("<!-- **********************************************");
        out.indentPrintln("Paste this link below into WEB-INF ksAdminLinks.tag or ksMaintenance.tag in KS with Rice Bundled etc");
        out.indentPrintln("");
        out.indentPrintln("<li><portal:portalLink displayTitle=\"true\" title=\"" + xmlType.getName() + " Lookup\""
                + " url=\"${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start"
                + "&dataObjectClassName=" + xmlType.getJavaPackage() + "." + infoClass + ""
                + "&viewId=" + viewId
                + "&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true\" /></li>");
        out.indentPrintln ("Also...");
        out.indentPrintln("Paste bean definition below into the list of dataDictionaryPackages in StudentSpringBeans.xml ");
        out.indentPrintln ("<value>classpath:" + fullDirectoryPath + "/" + fileName + "</value>");
        out.indentPrintln("********************************************** -->");
        out.indentPrintln("<!-- LookupView -->");
        out.indentPrintln("<bean id=\"" + viewId + "\" parent=\"KS-Uif-LookupView\"");
        out.incrementIndent();
        out.indentPrintln("p:title=\"" + xmlType.getName() + " Lookup\"");
        out.indentPrintln("p:header.headerText=\"" + xmlType.getName() + " Lookup\"");
        out.indentPrintln("p:dataObjectClassName=\"" + xmlType.getJavaPackage() + "." + infoClass + "\"");
        out.indentPrintln("p:viewHelperServiceClass=\"" + lookupable + "\">");
        out.indentPrintln("");
        out.indentPrintln("<property name=\"criteriaFields\">");
        out.incrementIndent();
        out.indentPrintln("<list>");
        out.incrementIndent();
        out.indentPrintln("<bean parent=\"Uif-LookupCriteriaInputField\" p:propertyName=\"keywordSearch\"");
        out.indentPrintln("      p:label=\"Keyword(s)\"");
        out.indentPrintln("      p:helpSummary=\"Searches fields like name and description to see if they contain the keyword\" />");
        this.writeFieldsToSearchOn (xmlType, new Stack<XmlType>(), "");
        out.indentPrintln("<bean parent=\"Uif-LookupCriteriaInputField\" p:propertyName=\"maxResultsToReturn\"");
        out.indentPrintln("      p:label=\"Max. Results\"");
        out.indentPrintln("      p:defaultValue=\"50\"");
        out.indentPrintln("      p:helpSummary=\"The maximum number of results to return from the query, leave null to not limit the results\" />");
        out.decrementIndent();
        out.indentPrintln("</list>");
        out.decrementIndent();
        out.indentPrintln("</property>");
        out.indentPrintln("<property name=\"resultFields\">");
        out.indentPrintln("    <list>");
        for (MessageStructure ms : this.getFieldsToShowOnLookup()) {
            String fieldName = GetterSetterNameCalculator.calcInitLower(ms.getShortName());
            out.indentPrint("        <bean parent=\"Uif-DataField\" p:propertyName=\"" + fieldName + "\"");
            if (ms.isPrimaryKey()) {
                out.println(">");
                out.indentPrintln("            <property name=\"inquiry\">");
                out.indentPrintln("                <bean parent=\"Uif-Inquiry\" p:dataObjectClassName=\"" + xmlType.getJavaPackage() + "." + xmlType.getName() + "\" p:inquiryParameters=\"" + ms.getShortName() + "\" />");
                out.indentPrintln("            </property>");
                out.indentPrintln("        </bean>");
            } else if (AdminUiInquiryViewBeanWriter.shouldDoLookup (ms.getLookup())) {
                
                out.println(">");
                Lookup lookup = ms.getLookup();
                XmlType msType = finder.findXmlType(lookup.getXmlTypeName());
                MessageStructure pk = this.getPrimaryKey(ms.getLookup().getXmlTypeName());
                out.indentPrintln("            <property name=\"inquiry\">");
                out.indentPrintln("                <bean parent=\"Uif-Inquiry\" p:dataObjectClassName=\""
                        + msType.getJavaPackage() + "." + msType.getName()
                        + "\" p:inquiryParameters=\"" + fieldName + ":" + pk.getShortName() + "\" />");
                out.indentPrintln("            </property>");
                out.indentPrintln("        </bean>");
            } else {
                out.println(" />");
            }
        }
        out.indentPrintln("    </list>");
        out.indentPrintln("</property>");
        out.decrementIndent();
        out.indentPrintln("</bean>");
        out.indentPrintln("");
        out.decrementIndent();
        out.indentPrintln("</beans>");
    }

    
    private MessageStructure getPrimaryKey(String xmlTypeName) {
        for (MessageStructure ms : finder.findMessageStructures(xmlTypeName)) {
            if (ms.isPrimaryKey()) {
                return ms;
            }
        }
        throw new NullPointerException ("could not find primary key for " + xmlTypeName);
    }

    
    private void writeFieldsToSearchOn(XmlType type, Stack<XmlType> parents, String prefix) {
         // avoid recursion
        if (parents.contains(type)) {
            return;
        }
        parents.push(type);
        for (MessageStructure ms : finder.findMessageStructures(type.getName())) {
            String fieldName = GetterSetterNameCalculator.calcInitLower(ms.getShortName());
            if (!prefix.isEmpty()) {
                fieldName = prefix + "." + fieldName;
            }
            String fieldNameCamel = GetterSetterNameCalculator.dot2Camel(fieldName);
            if (ms.getShortName().equalsIgnoreCase("versionInd")) {
                out.indentPrintln("<!-- TODO: deal with seaching on the version indicator which is a string in the contract but a number in the database -->");
                continue;
            }
            if (ms.getType().equalsIgnoreCase("AttributeInfoList")) {
                out.indentPrintln("<!-- TODO: deal with dynamic attributes -->");
                continue;
            }
            if (ms.getShortName ().equalsIgnoreCase("name")) {
                out.indentPrintln("<!-- skip name because keyword searching should cover it -->");
                continue;
            }
            if (ms.getShortName ().equalsIgnoreCase("descr")) {
                out.indentPrintln("<!-- skip description because keyword searching should cover it -->");
                continue;
            }
            if (ms.getType().endsWith("List")) {
                out.indentPrintln("<!-- TODO: deal with  " + fieldName + " which is a List -->");
                continue;
            }
            XmlType fieldType = finder.findXmlType(ms.getType());
            if (fieldType.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
                // complex sub-types such as rich text 
                this.writeFieldsToSearchOn(fieldType, parents, fieldName);
                continue;
            }
            if (!ms.getType().equalsIgnoreCase("String")) {
                out.indentPrintln("<!-- TODO: deal with  " + fieldName + " which is a " + ms.getType() + " -->");
                continue;
            }

            out.indentPrint("<bean parent=\"Uif-LookupCriteriaInputField\" p:propertyName=\"" + fieldName + "\"");
            if (!AdminUiInquiryViewBeanWriter.shouldDoLookup (ms.getLookup())) {
                out.println(" />");
                continue;
            }
            out.println(">");
            out.incrementIndent();
            XmlType msType = finder.findXmlType(ms.getLookup().getXmlTypeName());
            if (msType == null) {
                throw new NullPointerException ("Processing lookup for: " + type.getName () 
                        + ms.getName() 
                        + " lookup=" + ms.getLookup().getXmlTypeName());
            }
            out.indentPrintln("<property name=\"quickfinder\">");
            out.incrementIndent();
            out.indentPrintln("<bean parent=\"Uif-QuickFinder\" p:dataObjectClassName=\"" + msType.getJavaPackage() + "." + msType.getName() + "\"");
            MessageStructure pk = this.getPrimaryKey(ms.getLookup().getXmlTypeName());
            if (pk == null) {
                throw new NullPointerException("could not find primary key for " + ms.getId());
            }
            out.indentPrintln("p:fieldConversions=\"" + pk.getShortName() + ":" + fieldName + "\" />");
            out.decrementIndent();
            out.indentPrintln("</property>");
            out.decrementIndent();
            out.indentPrintln("</bean>");
        }
        parents.pop();
    }
    
    private List<MessageStructure> getFieldsToSearchOn() {
        List<MessageStructure> list = new ArrayList<MessageStructure>();
        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            // lists of values cannot be displayed within a list of values
            if (ms.getType().endsWith("List")) {
                continue;
            }
            if (ms.getType().endsWith("List")) {
                continue;
            }
            XmlType msType = finder.findXmlType(ms.getType());
            // just show fields on the main object for now don't dive down into complex fields
            if (msType.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
                continue;
            }
        }
        return list;
    }

    private List<MessageStructure> getFieldsToShowOnLookup() {
        List<MessageStructure> list = new ArrayList<MessageStructure>();
        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            // lists of values cannot be displayed within a list of values
            if (ms.getType().endsWith("List")) {
                continue;
            }
            XmlType msType = finder.findXmlType(ms.getType());
            // just show fields on the main object for now don't dive down into complex fields
            if (msType.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
                continue;
            }
            list.add(ms);
        }
        return list;
    }

    private void initXmlWriter() {
        String infoClass = GetterSetterNameCalculator.calcInitUpper(xmlType.getName());
        String serviceClass = GetterSetterNameCalculator.calcInitUpper(service.getName());
        String serviceVar = GetterSetterNameCalculator.calcInitLower(service.getName());
        String serviceContract = service.getImplProject() + "." + service.getName();

        fullDirectoryPath = AdminUiLookupableWriter.calcPackage(servKey, rootPackage, xmlType).replace('.', '/');
        fileName = infoClass + "AdminLookupView.xml";

        File dir = new File(this.directory);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("Could not create directory "
                        + this.directory);
            }
        }

        String dirStr = this.directory + "/" + "resources" + "/" + fullDirectoryPath;
        File dirFile = new File(dirStr);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                throw new IllegalStateException(
                        "Could not create directory " + dirStr);
            }
        }
        try {
            PrintStream out = new PrintStream(new FileOutputStream(
                    dirStr + "/"
                    + fileName, false));
            log.info("AdminUILookupViewBeanWriter: writing " + dirStr + "/" + fileName);
            this.out = new XmlWriter(out, 0);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void writeBoilerPlate() {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        out.println("       xmlns:p=\"http://www.springframework.org/schema/p\"");
        out.println("       xsi:schemaLocation=\"http://www.springframework.org/schema/beans");
        out.println("                    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\">");
        out.println("    <!--");
        out.println("     Copyright 2007-2012 The Kuali Foundation");
        out.println("");
        out.println("     Licensed under the Educational Community License, Version 2.0 (the \"License\");");
        out.println("     you may not use this file except in compliance with the License.");
        out.println("     You may obtain a copy of the License at");
        out.println("");
        out.println("     http://www.opensource.org/licenses/ecl2.php");
        out.println("");
        out.println("     Unless required by applicable law or agreed to in writing, software");
        out.println("     distributed under the License is distributed on an \"AS IS\" BASIS,");
        out.println("     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        out.println("     See the License for the specific language governing permissions and");
        out.println("     limitations under the License.");
        out.println("    -->");

    }
}
