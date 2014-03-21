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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.kuali.student.contract.model.Lookup;

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.writer.HtmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class HtmlContractMessageStructureWriter {

	private static final Logger log = LoggerFactory.getLogger(HtmlContractMessageStructureWriter.class);
	
    private XmlType xmlType;
    private HtmlWriter writer;
    private ServiceContractModel model;
    private ModelFinder finder;

    public HtmlContractMessageStructureWriter(XmlType xmlType, String directory,
            ServiceContractModel model) {
        this.xmlType = xmlType;
        this.writer = new HtmlWriter(directory, xmlType.getName() + ".html",
                xmlType.getName());
        this.model = model;
        this.finder = new ModelFinder(this.model);
    }

    private String initUpper(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private boolean isMainMessageStructure(XmlType xmlType) {
        if (!HtmlContractMessageStructureWriter.calcOtherXmlTypeUsages(model,
                xmlType).isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean shouldAddDictionaryLink (XmlType xmlType) {
        return this.isMainMessageStructure(xmlType);
    }
    
    public void write(String projectVersion, String formattedDate) {
       
        this.writeStyleSheet();
       
        
        if (this.shouldAddDictionaryLink(xmlType)) {
        	VersionLinesUtility.writeVersionTag(writer, "<a href=\"index.html\">home</a>", "<a href=\"../dictionarydocs/" + initUpper(xmlType.getName()) + ".html\">dictionary doc</a>", projectVersion, formattedDate);
        }
        else 
        	VersionLinesUtility.writeVersionTag(writer, "<a href=\"index.html\">home</a>", "" ,projectVersion, formattedDate);
        

        writer.writeTag("h1", xmlType.getName());
        
        writer.indentPrintln("<table id=\"structureMetaTable\">");
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Name");
        writer.writeTag("td", "id=structureName colspan=2", xmlType.getName());
        writer.indentPrintln("</tr>");
//  writer.indentPrintln ("<tr>");
//  writer.writeTag ("th", "class=h", "Version");
//  writer.writeTag ("td", "id=structureVersion colspan=2", xmlType.getVersion ());
//  writer.indentPrintln ("</tr>");
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "rowspan=3 class=h", "Usage");
        writer.indentPrint("<td id=\"structureVersion\" colspan=2>");
        for (String servKey : xmlType.getService().split(",")) {
            writer.indentPrintln("<a href=\"" + servKey + "Service" + ".html" + "\">"
                    + servKey + "Service" + "</a>");
        }
        writer.indentPrint("</td>");

        writer.indentPrintln("</tr>");
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "");
        writer.indentPrint("<td id=\"structureVersion\" colspan=2>");
        for (ServiceMethod method : this.calcUsageByMethods(xmlType)) {
            writer.indentPrintln("<a href=\"" + method.getService() + "Service"
                    + ".html" + "#" + method.getService() + "-"
                    + method.getName() + "\">"
                    + method.getName() + "</a>");
        }
        writer.indentPrint("</td>");
        writer.indentPrintln("</tr>");
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "");
        writer.indentPrint("<td id=\"structureVersion\" colspan=2>");
        for (String xmlTypeName : this.calcOtherXmlTypeUsages(xmlType)) {
            XmlType usageType = finder.findXmlType(xmlTypeName);
            if (usageType == null) {
                throw new NullPointerException("Coud not find XmlType with name="
                        + xmlTypeName);
            }
            writer.indentPrintln("<a href=\"" + usageType.getName() + ".html" + "\">"
                    + usageType.getName() + "</a>");
        }
        writer.indentPrint("</td>");
        writer.indentPrintln("</tr>");

        writer.writeTag("th", "class=h", "Type");
        writer.writeTag("td", "id=structureVersion colspan=2",
                xmlType.getPrimitive());
        writer.indentPrintln("</tr>");
        writer.indentPrintln("</table>");

        writer.writeTag("h2", "Description");
        writer.indentPrintln(this.addHTMLBreaks(calcDescription (xmlType)));

        if (!xmlType.getPrimitive().equals(XmlType.COMPLEX)) {
            return;
        }
        writer.indentPrintln(
                "<h2><a name=\"StructureDefinition\"></a>Structure Definition</h2>");


        writer.indentPrintln("<table class=\"structTable\">");
        writer.indentPrintln("<tr>");
        writer.indentPrintln("<th class=\"h\">ShortName</th>");
        writer.indentPrintln("<th class=\"h\">Name</th>");
        writer.indentPrintln("<th class=\"h\">Type</th>");
        writer.indentPrintln("<th class=\"h\">Description</th>");
        writer.indentPrintln("<th class=\"h\">Required?</th>");
        writer.indentPrintln("<th class=\"h\">Read only?</th>");
        writer.indentPrintln("<th class=\"h\">Lookup (WIP)</th>");
        writer.indentPrintln("<th class=\"h\">Implementation Notes</th>");
        writer.indentPrintln("</tr>");

        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            this.writeMessageStructure(ms);
        }
        writer.indentPrintln("</table>");
        writer.writeHeaderBodyAndFooterOutToFile();
    }

    
     private String calcDescription(XmlType xmlType) {
        StringBuilder sb = new StringBuilder();
        String newLine = "";
        if (xmlType.getDesc() != null && !xmlType.getDesc().trim().isEmpty()) {
            sb.append(newLine);
            newLine = "\n";
            sb.append(xmlType.getDesc ());
        }
        if (xmlType.isDeprecated()) {
            sb.append(newLine);
            newLine = "\n";
            sb.append("============== Deprecated ===============");
        }
        return sb.toString();
    }

    private String checkForNbsp(String str) {
        if (str == null) {
            return "&nbsp;";
        }
        if (str.trim().isEmpty()) {
            return "&nbsp;";
        }
        return str;
    }

    private String addHTMLBreaks(String str) {
        if (str == null) {
            return "&nbsp;";
        }
        return str.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
    }

    public static Set<String> calcUsageByService(ServiceContractModel mdl, XmlType xmlType) {
        Set<String> services = new HashSet();
        for (ServiceMethod method : calcUsageByMethods(mdl, xmlType)) {
            services.add(method.getService());
        }
        return services;
    }

    private Set<ServiceMethod> calcUsageByMethods(XmlType xmlType) {
        return calcUsageByMethods(model, xmlType);
    }

    public static Set<ServiceMethod> calcUsageByMethods(ServiceContractModel mdl, XmlType xmlType) {
        Set<ServiceMethod> methods = new LinkedHashSet();
        for (ServiceMethod method : mdl.getServiceMethods()) {
            if (stripListFromType(method.getReturnValue().getType()).equalsIgnoreCase(xmlType.getName())) {
                methods.add(method);
                continue;
            }
            for (ServiceMethodParameter param : method.getParameters()) {
                if (stripListFromType(param.getType()).equalsIgnoreCase(xmlType.getName())) {
                    methods.add(method);
                    break;
                }
            }
        }
        return methods;
    }

    private Set<String> calcOtherXmlTypeUsages(XmlType xmlType) {
        return calcOtherXmlTypeUsages(model, xmlType);
    }

    public static Set<String> calcOtherXmlTypeUsages(ServiceContractModel mdl,
            XmlType xmlType) {
        Set<String> xmlTypeNames = new LinkedHashSet();
        for (MessageStructure ms : mdl.getMessageStructures()) {
            if (ms.getType() == null) {
                throw new NullPointerException(ms.getId() + " has no type set");
            }
            if (stripListFromType(ms.getType()).equalsIgnoreCase(xmlType.getName())) {
                xmlTypeNames.add(ms.getXmlObject());
            }
        }
        return xmlTypeNames;
    }

    private void writeMessageStructure(MessageStructure ms) {
        writer.indentPrintln("<tr>");
        writer.writeTag("td", "class=\"structSName\"", ms.getShortName());
        writer.writeTag("td", "class=\"structLName\"", ms.getName());
        XmlType subType = finder.findXmlType(this.stripListFromType(ms.getType()));
        if (subType == null) {
//            for (XmlType xmlt : model.getXmlTypes()) {
//                System.out.println(this.getClass().getSimpleName() + ": "
//                        + xmlt.getName());
//            }
//            throw new NullPointerException(ms.getXmlObject() + "." + ms.getShortName()
//                    + " has type " + ms.getType()
//                    + " was not found in list of known types");
        	
        	log.error (ms.getXmlObject() + "." + ms.getShortName()
                  + " has type " + ms.getType()
                  + " was not found in list of known types");
        	return;
        }
        if (subType.getPrimitive().equals(XmlType.COMPLEX)) {
            writer.indentPrint("<td class=\"structType\">");
            writer.indentPrintln("<a href=\"" + subType.getName() + ".html" + "\">"
                    + ms.getType() + "</a>");
            writer.indentPrint("</td>");
        } else {
            writer.writeTag("td", "class=\"structType\"", ms.getType());
        }
        writer.writeTag("td", "class=\"structDesc\"", addHTMLBreaks(missingData(
                calcDescription(ms))));
        writer.writeTag("td", "class=\"structOpt\"", checkForNbsp(ms.getRequired()));
        writer.writeTag("td", "class=\"structOpt\"", checkForNbsp(ms.getReadOnly()));

        writer.writeTag("td", "class=\"structCard\"", addHTMLBreaks (checkForNbsp (calcLookup (ms.getLookup ()))));
//      writer.writeTag ("td", "class=\"structStatus\"", ms.getStatus ());
        writer.writeTag("td", "class=\"commentsDesc\"", addHTMLBreaks(this.checkForNbsp(ms.getImplNotes())));
        writer.indentPrintln("</tr>");

//  writer.indentPrintln ("</table>");
//  writer.indentPrintln ("<p>");

//  writer.indentPrintln ("<p>");
    }

    private String calcLookup(Lookup lookup) {
        if (lookup == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String newLine = "\n";
        sb.append(lookup.getServiceKey());
        sb.append(":");
        sb.append(lookup.getXmlTypeName());
        if (lookup.getGetMethod() != null) {
            sb.append(newLine);
            sb.append(lookup.getGetMethod());
        }
        if (lookup.getSearchMethod() != null) {
            sb.append(newLine);
            sb.append(lookup.getSearchMethod());
            if (lookup.getAdditionalCriteria() != null) {
                sb.append(newLine);
                sb.append(lookup.getAdditionalCriteria().toString());
            }
        }
        return sb.toString();
    }
    
    private String calcDescription(MessageStructure ms) {
        StringBuilder sb = new StringBuilder();
        String newLine = "";
        if (ms.getDescription() != null && !ms.getDescription().trim().isEmpty()) {
            sb.append(newLine);
            newLine = "\n";
            sb.append(ms.getDescription());
        }
        if (ms.isDeprecated()) {
            sb.append(newLine);
            newLine = "\n";
            sb.append("============== Deprecated ===============");
        }
        return sb.toString();
    }

    private String missingData(String str) {
        if (str == null) {
            return "???";
        }
        if (str.trim().isEmpty()) {
            return "???";
        }
        return str;
    }

    private static String stripListFromType(String type) {
        if (type.endsWith("List")) {
            return type.substring(0, type.length() - "List".length());
        }
        return type;
    }

    public void writeStyleSheet() {
        writer.indentPrintln("<style type=\"text/css\">");
        writer.indentPrintln("");
        writer.indentPrintln("table#structureMetaTable {");
        writer.indentPrintln("border-collapse:collapse;");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:95%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#structureMetaTable th.h {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background-color:#eeeeee;");
        writer.indentPrintln("width:15%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#structureMetaTable td#structureName {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:85%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#structureMetaTable td#structureVersion {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:70%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#structureMetaTable td#structureVersionHistory {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:15%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("</style>");

        writer.indentPrintln("<style type=\"text/css\">");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable {");
        writer.indentPrintln("border-collapse:collapse;");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:95%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.d {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable th.h {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background-color:#eeeeee;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structSName {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background-color:#f2f2f2;");
        writer.indentPrintln("color:#222222;");
        writer.indentPrintln("text-align:left;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("font-weight:bold;");
        writer.indentPrintln("font-style:italic;");
        writer.indentPrintln("");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structLName {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structType {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("font-style:italic;");
        writer.indentPrintln("");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structOpt {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("text-align: center;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structReq {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ccccff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("text-align: center;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structCard {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("text-align: center;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structAttr {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("text-align: center;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structElem {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ccccff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("text-align: center;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.structStatus {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("text-align: center;");
        writer.indentPrintln("");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.structTable td.commentsDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("vertical-align:top;");
        writer.indentPrintln("");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("");
        writer.indentPrintln("</style>");


    }
}
