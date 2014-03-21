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

import java.util.List;

import org.kuali.student.contract.exception.DictionaryExecutionException;
import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.JavaClassWriter;

/**
 *
 * @author nwright
 */
public class PureJavaInfcInfcWriter extends JavaClassWriter {

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String service;
    private XmlType xmlType;

    public PureJavaInfcInfcWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String service,
            XmlType xmlType) {
        super(directory, calcPackage(service, rootPackage), calcClassName(
                xmlType.getName()));
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.service = service;
        this.xmlType = xmlType;
    }

    public static String calcPackage(String service, String rootPackage) {
        if (service.contains(",")) {
            service = "common";
        }
        return PureJavaInfcServiceWriter.calcPackage(service, rootPackage);
    }

    public static String calcClassName(String name) {
        if (name.endsWith("Info")) {
            name = name.substring(0, name.length() - "Info".length());
            name = name + "Infc";
        } else if (name.endsWith("InfoList")) {
            name = name.substring(0, name.length() - "InfoList".length());
            name = name + "InfcList";
        }
        return GetterSetterNameCalculator.calcInitUpper(name);
    }

    /**
     * Write out the entire file
     * @param out
     */
    public void write() {
        indentPrintln("public interface " + calcClassName(xmlType.getName()));
        openBrace();

        List<MessageStructure> list = finder.findMessageStructures(xmlType.getName());
//        if (list.size() == 0) {
//            throw new DictionaryExecutionException(
//                    "xmlType " + xmlType.getName()
//                    + " has no fields defined in the message structure tab");
//        }
        for (MessageStructure ms : list) {
            if (ms.getId().equals ("RegistrationGroupTemplateInfo.activityOfferingCombinations")) {
                continue;
            }
            String realType = stripList(calcClassName(ms.getType()));
            String type = this.calcFieldTypeToUse(ms.getType(), realType);
            indentPrintln("");
            indentPrintln("/**");
            indentPrintWrappedComment("Set " + ms.getName());
            indentPrintln("*");
            indentPrintln("* Type: " + ms.getType());
            indentPrintln("*");
            indentPrintWrappedComment(ms.getDescription());
            indentPrintln("*/");
            indentPrintln("public void " + calcSetter(ms) + "(" + type + " " + initLower(
                    ms.getShortName()) + ");");


            indentPrintln("");
            indentPrintln("/**");
            indentPrintWrappedComment("Get " + ms.getName());
            indentPrintln("*");
            indentPrintln("* Type: " + ms.getType());
            indentPrintln("*");
            indentPrintWrappedComment(ms.getDescription());
            indentPrintln("*/");
            indentPrintln("public " + type + " " + calcGetter(ms) + "();");
            indentPrintln("");

            indentPrintln("");
        }
        indentPrintln("");
        closeBrace();

        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    private String stripList(String str) {
        return GetterSetterNameCalculator.stripList(str);
    }

    private String initLower(String str) {
        return GetterSetterNameCalculator.calcInitLower(str);
    }

    private String calcGetter(MessageStructure ms) {
        return new GetterSetterNameCalculator(ms, this, model).calcGetter();
    }

    private String calcSetter(MessageStructure ms) {
        return new GetterSetterNameCalculator(ms, this, model).calcSetter();
    }

    private String calcFieldTypeToUse(String type, String realType) {
        XmlType t = finder.findXmlType(this.stripList(type));
        String pckName = calcPackage(t.getService(), rootPackage);
        return MessageStructureTypeCalculator.calculate(this, model, type, realType,
                pckName);
    }
}
