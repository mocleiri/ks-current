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
package org.kuali.student.validation.decorator.mojo;

import java.util.List;

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.JavaClassWriter;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;
import org.kuali.student.contract.writer.service.MessageStructureTypeCalculator;
import org.kuali.student.contract.writer.service.ServiceExceptionWriter;

/**
 *
 * @author nwright
 */
public class ValidationDecoratorServiceWriter extends JavaClassWriter {

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;
    private List<ServiceMethod> methods;

    public ValidationDecoratorServiceWriter(ServiceContractModel model,
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
        pack = pack + "service.decorators";
        return pack;
    }

    public static String calcClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(servKey + "ServiceValidationDecorator");
    }

    public static String calcDecoratorClassName(String servKey) {
        return GetterSetterNameCalculator.calcInitUpper(servKey + "ServiceDecorator");
    }

    private static enum MethodType {

        VALIDATE, CREATE, UPDATE
    };

    private MethodType calcMethodType(ServiceMethod method) {
        if (method.getName().startsWith("validate")) {
            return MethodType.VALIDATE;
        }
        if (method.getName().startsWith("create")) {
            return MethodType.CREATE;
        }
        if (method.getName().startsWith("update")) {
            return MethodType.UPDATE;
        }
        return null;
    }

    /**
     * Write out the entire file
     * @param out
     */
    public void write() {
        indentPrint("public class " + calcClassName(servKey));
        println(" extends " + calcDecoratorClassName(servKey));
        openBrace();
        writeBoilerPlate();
        for (ServiceMethod method : methods) {
            MethodType methodType = calcMethodType(method);
            if (methodType == null) {
                continue;
            }

            indentPrintln("");
//            indentPrintln("/**");
//            indentPrintWrappedComment(method.getDescription());
//            indentPrintln("* ");
//            for (ServiceMethodParameter param : method.getParameters()) {
//                indentPrintWrappedComment("@param " + param.getName() + " - "
//                        + param.getType() + " - "
//                        + param.getDescription());
//            }
//            indentPrintWrappedComment("@return " + method.getReturnValue().
//                    getDescription());
//            indentPrintln("*/");
            indentPrintln("@Override");
            String type = method.getReturnValue().getType();
            String realType = stripList(type);
            indentPrint("public " + calcType(type, realType) + " " + method.getName()
                    + "(");
            // now do parameters
            String comma = "";
            for (ServiceMethodParameter param : method.getParameters()) {
                type = param.getType();
                realType = stripList(type);
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
            openBrace();
            switch (methodType) {
                case VALIDATE:
                    writeValidate(method);
                    break;
                case CREATE:
                    writeCreate(method);
                    break;
                case UPDATE:
                    writeUpdate(method);
                    break;
            }
            closeBrace();
        }

        closeBrace();

        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    private String initLower (String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
    
    private void writeValidate(ServiceMethod method) {
         String objectName = calcObjectName(method);
        indentPrintln("// validate");
        indentPrintln("List<ValidationResultInfo> errors;");
        indentPrintln("try {");
        indentPrintln("    errors = ValidationUtils.validateInfo(validator, validationTypeKey, " + initLower (objectName) + "Info, contextInfo);");
        indentPrint("    List<ValidationResultInfo> nextDecoratorErrors = getNextDecorator()." + method.getName());
        print("(");
        String comma = "";
        for (ServiceMethodParameter param : method.getParameters()) {
            print(comma);
            print(param.getName());
            comma = ", ";
        }
        println(");");
        indentPrintln("   errors.addAll(nextDecoratorErrors);");
        indentPrintln("} catch (DoesNotExistException ex) {");
        indentPrintln("  throw new OperationFailedException(\"Error validating\", ex);");
        indentPrintln("}");
        indentPrintln("return errors;");

    }

    private void writeCreate(ServiceMethod method) {
        indentPrintln("// create ");
        writeValidateCall(method);
        indentPrint("return getNextDecorator().");
        print(method.getName());
        print("(");
        String comma = "";
        for (ServiceMethodParameter param : method.getParameters()) {
            print(comma);
            print(param.getName());
            comma = ", ";
        }
        println(");");
    }

    private void writeValidateCall(ServiceMethod method) {
        String objectName = calcObjectName(method);
        indentPrintln("try {");
        indentPrintln("    List<ValidationResultInfo> errors = ");
        indentPrint("      this.validate" + objectName + "(DataDictionaryValidator.ValidationType.FULL_VALIDATION.toString(), ");
        String comma = "";
        for (ServiceMethodParameter param : method.getParameters()) {
            print(comma);
            print(param.getName());
            comma = ", ";
        }
        println(");");
        indentPrintln("    if (!errors.isEmpty()) {");
        indentPrintln("       throw new DataValidationErrorException(\"Error(s) occurred validating\", errors);");
        indentPrintln("    }");
        indentPrintln("} catch (DoesNotExistException ex) {");
        indentPrintln("    throw new OperationFailedException(\"Error validating\", ex);");
        indentPrintln("}");
    }

    private String calcObjectName(ServiceMethod method) {
        if (method.getName().startsWith("create")) {
            return method.getName().substring("create".length());
        }
        if (method.getName().startsWith("update")) {
            return method.getName().substring("update".length());
        }
        if (method.getName().startsWith("validate")) {
            return method.getName().substring("validate".length());
        }
        throw new IllegalArgumentException(method.getName());
    }

    private void writeUpdate(ServiceMethod method) {
        indentPrintln("// update");
        writeValidateCall(method);
        indentPrint("return getNextDecorator().");
        print(method.getName());
        print("(");
        String comma = "";
        for (ServiceMethodParameter param : method.getParameters()) {
            print(comma);
            print(param.getName());
            comma = ", ";
        }
        println(");");
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
        return MessageStructureTypeCalculator.calculate(this, model, type, realType,
                t.getJavaPackage());
    }

    private void writeBoilerPlate() {
        // validator property w/getter & setter
        indentPrintln("// validator property w/getter & setter");
        indentPrintln("private DataDictionaryValidator validator;");
        this.importsAdd("org.kuali.student.r2.common.datadictionary.DataDictionaryValidator");
//        indentPrintln("@Override"); 
        indentPrintln("public DataDictionaryValidator getValidator() {");
        indentPrintln("    return validator;");
        indentPrintln("}");
        indentPrintln("public void setValidator(DataDictionaryValidator validator) {");
        indentPrintln("    this.validator = validator;        ");
        indentPrintln("}");
    }
}
