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

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.JavaClassWriter;

/**
 *
 * @author nwright
 */
public class PureJavaInfcServiceWriter extends JavaClassWriter {

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;
    private List<ServiceMethod> methods;

    public PureJavaInfcServiceWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey,
            List<ServiceMethod> methods) {
        super(directory, calcPackage(servKey, rootPackage), calcClassName(servKey));
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
        this.methods = methods;
    }

    public static String calcPackage(String servKey, String rootPackage) {
        String pack = rootPackage + "." + servKey.toLowerCase() + ".";
//  StringBuffer buf = new StringBuffer (service.getVersion ().length ());
//  for (int i = 0; i < service.getVersion ().length (); i ++)
//  {
//   char c = service.getVersion ().charAt (i);
//   c = Character.toLowerCase (c);
//   if (Character.isLetter (c))
//   {
//    buf.append (c);
//    continue;
//   }
//   if (Character.isDigit (c))
//   {
//    buf.append (c);
//   }
//  }
//  pack = pack + buf.toString ();
        pack = pack + "api";
        return pack;
    }

    public static String calcClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(servKey + "ServiceInfc");
    }

    /**
     * Write out the entire file
     * @param out
     */
    public void write() {
        indentPrintln("public interface " + calcClassName(servKey));
        openBrace();

        for (ServiceMethod method : methods) {
            indentPrintln("");
            indentPrintln("/**");
            indentPrintWrappedComment(method.getDescription());
            indentPrintln("* ");
            for (ServiceMethodParameter param : method.getParameters()) {
                indentPrintWrappedComment("@param " + param.getName() + " - "
                        + param.getType() + " - "
                        + param.getDescription());
            }
            indentPrintWrappedComment("@return " + method.getReturnValue().
                    getDescription());
            indentPrintln("*/");
            String type = method.getReturnValue().getType();
            String realType = stripList(PureJavaInfcInfcWriter.calcClassName(type));
            indentPrint("public " + calcType(type, realType) + " " + method.getName()
                    + "(");
            // now do parameters
            String comma = "";
            for (ServiceMethodParameter param : method.getParameters()) {
                type = param.getType();
                realType = stripList(PureJavaInfcInfcWriter.calcClassName(type));
                print(comma);
                print(calcType(type, realType));
                print(" ");
                print(param.getName());
                comma = ", ";
            }
            println(")");
            // now do exceptions
            comma = "throws ";
            incrementIndent();
            for (ServiceMethodError error : method.getErrors()) {
                indentPrint(comma);
                String exceptionClassName = calcExceptionClassName(error);
                String exceptionPackageName = this.calcExceptionPackageName(error);
                println(exceptionClassName);
                this.importsAdd(exceptionPackageName + "." + exceptionClassName);
                comma = "      ,";
            }
            decrementIndent();
            indentPrintln(";");

        }

        closeBrace();

        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    private String stripList(String str) {
        return GetterSetterNameCalculator.stripList(str);
    }

    private String calcExceptionClassName(ServiceMethodError error) {
        if (error.getClassName() == null) {
            return ServiceExceptionWriter.calcClassName(error.getType());
        }
        return error.getClassName();
    }

    private String calcExceptionPackageName(ServiceMethodError error) {
        if (error.getClassName() == null) {
            return ServiceExceptionWriter.calcPackage(rootPackage);
        }
        return error.getPackageName();
    }

    private String calcType(String type, String realType) {
        XmlType t = finder.findXmlType(this.stripList(type));
        String pckName = PureJavaInfcInfcWriter.calcPackage(t.getService(), rootPackage);
        return MessageStructureTypeCalculator.calculate(this, model, type, realType,
                pckName);
    }
}
