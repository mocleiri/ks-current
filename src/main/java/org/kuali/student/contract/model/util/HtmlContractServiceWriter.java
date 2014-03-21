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

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.writer.HtmlWriter;

/**
 *
 * @author nwright
 */
public class HtmlContractServiceWriter {

    private Service service;
    private HtmlWriter writer;
    private ServiceContractModel model;
    private ModelFinder finder;

    public HtmlContractServiceWriter(Service service, String directory,
            ServiceContractModel model) {
        this.service = service;
        this.writer = new HtmlWriter(directory, service.getKey() + "Service" + ".html",
                service.getName());
        this.model = model;
        this.finder = new ModelFinder(this.model);
    }

    public void write(String projectVersion, String formattedDate) {
        this.writeStyleSheet();
       

        VersionLinesUtility.writeVersionTag(writer, "<a href=\"index.html\">home</a>", "", projectVersion, formattedDate);

        writer.writeTag("h1", service.getName());
        
        writer.indentPrintln("<table id=\"serviceMetaTable\">");
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Key");
        writer.writeTag("td", "id=serviceName colspan=2", service.getKey());
        writer.indentPrintln("</tr>");
        
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Version");
        writer.writeTag("td", "id=serviceVersion colspan=2", service.getVersion());
        writer.indentPrintln("</tr>");
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Included Services");

        writer.writeTag("td", "id=serviceVersion colspan=2", calcIncludedServices(
                service.getIncludedServices()));
        writer.indentPrintln("</tr>");
        
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Java Package");

        writer.writeTag("td", "id=serviceVersion colspan=2", service.getImplProject());
        writer.indentPrintln("</tr>");
        

        writer.indentPrintln("</table>");

//  writer.indentPrintln ("<div class=\"panel\" style=\"border-width: 1px;\">");
//  writer.indentPrintln ("<div class=\"panelHeader\" style=\"border-bottom-width: 1px;\">");
        writer.indentPrintln("<p>");
//        writer.indentPrintln("<table id=\"serviceMetaTable\">");
//        writer.indentPrintln("<tr>");
//        writer.indentPrintln("<td>");
        writer.indentPrintln(
                "<div class=\"panel\" style=\"background-color: rgb(255, 255, 255); border: 1px solid rgb(204, 204, 204);\">");
          writer.indentPrintln(
                "<div class=\"panelHeader\" style=\"border-bottom: 1px solid rgb(204, 204, 204); background-color: rgb(238, 238, 238);\">");
        writer.indentPrintln(this.addHTMLBreaks(service.getComments()));
        writer.indentPrintln("</div>");
        writer.indentPrintln("</div>");
//        writer.indentPrintln("</td>");
//        writer.indentPrintln("</tr>");
//        writer.indentPrintln("</table>");

//  writer.indentPrintln ("</div>");
//  writer.indentPrintln ("</div>");

        writer.indentPrintln("<p>");
        writer.indentPrintln(
                "<div class=\"panel\" style=\"background-color: rgb(255, 255, 255); border: 1px solid rgb(204, 204, 204);\">");
        writer.indentPrintln(
                "<div class=\"panelHeader\" style=\"border-bottom: 1px solid rgb(204, 204, 204); background-color: rgb(238, 238, 238);\">");
        writer.indentPrintln("<b><a name=\"ListOfOperations\"></a>Operations</b>");
        writer.indentPrintln(
                "</div><div class=\"panelContent\" style=\"background-color: rgb(255, 255, 255);\">");
        writer.indentPrintln("<ul>");
        for (ServiceMethod method : finder.getServiceMethodsInService(
                service.getKey())) {
            writer.indentPrint("<li>");
            writer.print("<a href=\"#" + method.getService() + "-" + method.getName()
                    + "\">" + method.getName() + "</a>");
            writer.print("</li>");
        }
        writer.indentPrintln("</ul>");
        writer.indentPrintln("</div>");
        writer.indentPrintln("</div>");

        // now write out the root message structures
        writer.indentPrintln(
                "<div class=\"panel\" style=\"background-color: rgb(255, 255, 255); border: 1px solid rgb(204, 204, 204);\">");
        writer.indentPrintln(
                "<div class=\"panelHeader\" style=\"border-bottom: 1px solid rgb(204, 204, 204); background-color: rgb(238, 238, 238);\">");
        writer.indentPrintln(
                "<b><a name=\"MainMessageStructures\"></a>Main Message Structures</b>");
        writer.indentPrintln(
                "</div><div class=\"panelContent\" style=\"background-color: rgb(255, 255, 255);\">");

        writer.indentPrintln("<ul>");
        for (XmlType type : this.calcMainMessageStructures()) {
            writer.indentPrint("<li>");
            writer.print("<a href=\"" + type.getName() + ".html"
                    + "\">" + type.getName() + "</a>");
            writer.print("</li>");
        }
        writer.indentPrintln("</ul>");


        for (ServiceMethod method : finder.getServiceMethodsInService(
                service.getKey())) {
            this.writeMethod(method);
        }

        writer.writeHeaderBodyAndFooterOutToFile();
        
        writer.getOut().close();
    }

    private String calcIncludedServices(List<String> includedServices) {
        if (includedServices == null) {
            return "&nbsp;";
        }
        if (includedServices.isEmpty()) {
            return "&nbsp;";
        }
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        for (String includedService : includedServices) {
            bldr.append(comma);
            comma = ", ";
            bldr.append("<a href=\"" + includedService + ".html"
                    + "\">" + includedService + "</a>");
        }
        return bldr.toString();
    }

    private Set<XmlType> calcMainMessageStructures() {
        return calcMainMessageStructures(model, service.getKey());
    }

    public static Set<XmlType> calcMainMessageStructures(ServiceContractModel mdl,
            String serviceOptionaFilter) {
        ModelFinder fndr = new ModelFinder(mdl);
        Set<XmlType> types = new LinkedHashSet();
        for (ServiceMethod method : mdl.getServiceMethods()) {
            if (serviceOptionaFilter != null) {
                if (!method.getService().equalsIgnoreCase(serviceOptionaFilter)) {
                    continue;
                }
            }
            XmlType type = fndr.findXmlType(stripListFromType(method.getReturnValue().getType()));
            if (type != null) {
                if (type.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
                    types.add(type);
                }
            }

            for (ServiceMethodParameter param : method.getParameters()) {
                type = fndr.findXmlType(stripListFromType(param.getType()));
                if (type != null) {
                    if (type.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
                        types.add(type);
                    }
                }
                break;
            }
        }
        return types;
    }

    private String addHTMLBreaks(String str) {
        if (str == null) {
            return "&nbsp;";
        }
        return str.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
    }

    public void writeMethod(ServiceMethod method) {
        writer.indentPrintln("<p>");
        writer.indentPrintln("<a name=\"" + method.getService() + "-"
                + method.getName() + "\"></a>");
        writer.indentPrintln("<p>");
        writer.indentPrintln("<table class=\"methodTable\">");

        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Method");
        writer.writeTag("th", "class=h colspan=3 class=\"methodName\"",
                method.getName());
        writer.indentPrintln("</tr>");

        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Description");
        writer.writeTag("td", "colspan=3 class=\"methodDesc\"",
                this.addHTMLBreaks(calcDescription(method)));
        writer.indentPrintln("</tr>");

        if (method.getImplNotes() != null && !method.getImplNotes().isEmpty()) {
            writer.indentPrintln("<tr>");
            writer.writeTag("th", "class=h", "Implementation Notes");
            writer.writeTag("td", "colspan=3 class=\"methodDesc\"",
                    this.addHTMLBreaks(method.getImplNotes()));
            writer.indentPrintln("</tr>");
        }

        if (method.getParameters().isEmpty()) {
            writer.indentPrintln("<tr>");
            writer.writeTag("th", "class=h", "Parameters");
            writer.writeTag("td", "class=\"methodParamType\"", "None");
            writer.writeTag("td", "class=\"methodParamName\"", "None");
            writer.writeTag("td", "class=\"methodParamDesc\"", "No Parameters");
            writer.indentPrintln("</tr>");
        } else {
            boolean firstTime = true;
            for (ServiceMethodParameter param : method.getParameters()) {
                writer.indentPrintln("<tr>");
                if (firstTime) {
                    writer.writeTag("th", "class=h rowspan=" + method.getParameters().size(),
                            "Parameters");
                    firstTime = false;
                }
                writer.indentPrint("<td class=\"methodParamType\">");
                writer.indentPrintln("<a href=\"" + stripListFromType(param.getType())
                        + ".html" + "\">"
                        + param.getType() + "</a>");
                writer.indentPrint("</td>");
                writer.writeTag("td", "class=\"methodParamName\"", param.getName());
                writer.writeTag("td", "class=\"methodParamDesc\"", this.addHTMLBreaks(
                        param.getDescription()));
                writer.indentPrintln("</tr>");
            }
        }
        writer.indentPrintln("<tr>");
        writer.writeTag("th", "class=h", "Return");
        writer.indentPrint("<td colspan=2 class=\"methodReturnType\">");
        writer.indentPrintln("<a href=\"" + stripListFromType(
                method.getReturnValue().getType())
                + ".html" + "\">"
                + method.getReturnValue().getType() + "</a>");
        writer.indentPrint("</td>");
        writer.writeTag("td", "class=\"methodReturnDesc\"",
                this.addHTMLBreaks(
                method.getReturnValue().getDescription()));
        writer.indentPrintln("</tr>");
        writer.indentPrintln("</tr>");

        if (method.getErrors().isEmpty()) {
            writer.indentPrintln("<tr>");
            writer.writeTag("th", "class=h", "Errors");
            writer.writeTag("td", "class=\"methodErrorType\" colspan=2", "NONE");
            writer.writeTag("td", "class=\"methodErrorDesc\"", "No Errors");
            writer.indentPrintln("</tr>");
        } else {
            boolean firstTime = true;
            for (ServiceMethodError error : method.getErrors()) {
                writer.indentPrintln("<tr>");
                if (firstTime) {
                    writer.writeTag("th", "class=h rowspan=" + method.getErrors().size(),
                            "Errors");
                    firstTime = false;
                }
                writer.writeTag("td", "class=\"methodErrorType\" colspan=2",
                        error.getType()); // TODO wrap in link to type
                writer.writeTag("td", "class=\"methodErrorDesc\"", this.addHTMLBreaks(
                        error.getDescription()));
                writer.indentPrintln("</tr>");
            }
        }
        writer.indentPrintln("</table>");
        writer.indentPrintln("<p>");
        writer.indentPrintln("<a href=\"#ListOfOperations\">Back to Operations</a>");
        writer.indentPrintln("<p>");
    }

    private String calcDescription(ServiceMethod method) {
        StringBuilder sb = new StringBuilder();
        String newLine = "";
        if (method.getDescription() != null && !method.getDescription().trim().isEmpty()) {
            sb.append(newLine);
            newLine = "\n";
            sb.append(method.getDescription());
        }
        if (method.isDeprecated()) {
            sb.append(newLine);
            newLine = "\n";
            sb.append("============== Deprecated ===============");
        }
        return sb.toString();
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
        writer.indentPrintln("table#serviceMetaTable {");
        writer.indentPrintln("border-collapse:collapse;");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:95%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#serviceMetaTable th.h {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background-color:#eeeeee;");
        writer.indentPrintln("width:15%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#serviceMetaTable td#serviceName {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:85%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#serviceMetaTable td#serviceVersion {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:70%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table#serviceMetaTable td#serviceVersionHistory {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:15%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("</style>");


        writer.indentPrintln("<style type=\"text/css\">");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable {");
        writer.indentPrintln("border-collapse:collapse;");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:95%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.d {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable th.h {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background-color:#eeeeee;");
        writer.indentPrintln("width:15%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.methodName {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background-color:#f2f2f2;");
        writer.indentPrintln("color:#222222;");
        writer.indentPrintln("text-align:center;");
        writer.indentPrintln("width:85%;");
        writer.indentPrintln("font-weight:bold;");
        writer.indentPrintln("font-style:italic;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.methodDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:85%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.methodParamType {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:21%;");
        writer.indentPrintln("font-style:italic;");
        writer.indentPrintln("}");
        writer.indentPrintln("table.methodTable td.methodParamName {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:21%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table.methodTable td.methodParamDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:43%;");
        writer.indentPrintln("}");
        writer.indentPrintln("table.methodTable td.methodReturnType {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:42%;");
        writer.indentPrintln("font-style:italic;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.methodReturnDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:43%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.methodErrors {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:85%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.methodErrorType {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:42%;");
        writer.indentPrintln("font-style:italic;");
        writer.indentPrintln("}");
        writer.indentPrintln("table.methodTable td.methodErrorDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("width:43%;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.capabilityDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.usecaseDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("}");
        writer.indentPrintln("");
        writer.indentPrintln("table.methodTable td.commentsDesc {");
        writer.indentPrintln("border:1px solid #000000;");
        writer.indentPrintln("background:#ffffff;");
        writer.indentPrintln("}");
        writer.indentPrintln("</style>");

    }
}
