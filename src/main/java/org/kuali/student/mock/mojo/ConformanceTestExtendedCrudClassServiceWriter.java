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
package org.kuali.student.mock.mojo;

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;

import java.util.List;

/**
 * This class will generate the extended class that does CRUD tests as
 * part of the Conformance Tests for services. The generated class will
 * complete the base class and is meant to be generated once and then
 * edited (and mantained) by developers.
 *
 * @author Mezba Mahtab (mezba.mahtab@utoronto.ca)
 */
public class ConformanceTestExtendedCrudClassServiceWriter extends ConformanceTestBaseCrudClassServiceWriter {

    ////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////

    public ConformanceTestExtendedCrudClassServiceWriter (ServiceContractModel model,
                                                          String directory,
                                                          String rootPackage,
                                                          String servKey,
                                                          List<ServiceMethod> methods,
                                                          boolean isR1) {
        super(model, directory, rootPackage, servKey, methods, isR1, calcPackage(servKey, rootPackage), calcClassName(servKey));
    }

    //////////////////////////
    // FUNCTIONALS
    //////////////////////////

    /**
     * Given the service key (name), returns a calculated class name for the conformance tester.
     */
    public static String calcClassName(String servKey) {
        return "Test" + GetterSetterNameCalculator.calcInitUpper(fixServKey(servKey) + "ServiceImplConformanceExtendedCrud");
    }

    /**
     * Write out the entire file
     */
    public void write() {
        // begin file
        indentPrintln("@RunWith(SpringJUnit4ClassRunner.class)");
        indentPrintln("@ContextConfiguration(locations = {\"classpath:" + servKey + "-test-with-mock-context.xml\"})");
        indentPrint("public class " + calcClassName(servKey));
        println(" extends " + super.calcClassName(servKey) + " ");
        Service serv = finder.findService(servKey);
        setPackageName(serv.getImplProject() + ".impl"); // change the package name
        importsAdd(serv.getImplProject() + "." + serv.getName()); // import for the service
        doTestImportsAdd();
        openBrace();

        indentPrintln("");

        // print out list of methods for DTO fields per DTO that need to be completed
        indentPrintDecoratedComment("DTO FIELD SPECIFIC METHODS", H1_COMMENT_CHAR, H1_COMMENT_MARK_LENGTH*2);
        indentPrintln("");

        // for each DTO, write the DOT field management methods that were left abstract in base class
        for (String dtoObjectName : calcNamesOfDTOsWithCrudManagedByService()) {
            indentPrintln("// ****************************************************");
            indentPrintln("//           " + dtoObjectName + "Info");
            indentPrintln("// ****************************************************");
            indentPrintln("");

            // get the message structures of the dto
            List<MessageStructure> messageStructures = finder.findMessageStructures(dtoObjectName + "Info");

            writetestCrudXXX_setDTOFieldsForTestCreate(dtoObjectName, messageStructures);
            writetestCrudXXX_testDTOFieldsForTestCreateUpdate(dtoObjectName, messageStructures);
            writetestCrudXXX_setDTOFieldsForTestUpdate(dtoObjectName, messageStructures);
            writetestCrudXXX_testDTOFieldsForTestReadAfterUpdate(dtoObjectName, messageStructures);
            writetestCrudXXX_setDTOFieldsForTestReadAfterUpdate(dtoObjectName, messageStructures);
            indentPrintln("");
        }

        // print out list of service operations that are not tested as abstract test methods
        indentPrintDecoratedComment("SERVICE OPS NOT TESTED IN BASE TEST CLASS", H1_COMMENT_CHAR, H1_COMMENT_MARK_LENGTH*2);
        indentPrintln("");

        // for each method, create an abstract method to test that and print it out
        for (ServiceMethod method: methods) {
            if (!isServiceMethodTestedAsPartofCrudInBaseConformanceTest (method)) {
                indentPrintln("/* Method Name: " + method.getName() + " */");
                indentPrintln("@Test");
                indentPrintln("public void test_" + method.getName() + "() ");
                if (method.getErrors().size()>0) {
                    indentPrint("throws ");
                    String comma = "";
                    for (ServiceMethodError error: method.getErrors()) {
                        indentPrint(comma + error.getClassName().trim());
                        comma = ",";
                    }
                }
                openBrace();
                closeBrace();
                indentPrintln("");
            }
        }



        // end file print out
        closeBrace ();
        println ("");

        // close and print file
        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    /**
     * Writes the section to set fields specific to this dto for testCreate section.
     */
    public void writetestCrudXXX_setDTOFieldsForTestCreate(String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintln("/*");
        incrementIndent();
        indentPrintln("A method to set the fields for a " + dtoObjectName  + " in a 'test create' section prior to calling the 'create' operation.");
        decrementIndent();
        indentPrintln("*/");
        indentPrintln("public void testCrud" + dtoObjectName + "_setDTOFieldsForTestCreate(" + dtoObjectName + "Info expected) ");
        openBrace();
        for (MessageStructure ms: messageStructures) {
            if (ms.getShortName().equals("id")) continue;
            if (ms.getShortName().equals("meta")) continue;
            if (ms.getShortName().equals("attributes")) continue;
            if (ms.getType().equals("String")) {
                indentPrintln("expected." + new GetterSetterNameCalculator (ms, this, model).calcSetter() + "(\"" + ms.getShortName()+ "01\");");
            }
            else if (ms.getShortName().equals("descr")) {
                indentPrintln("expected.setDescr(RichTextHelper.buildRichTextInfo(\"descr01\", \"descr01\"));");
            }
            else {
                indentPrintln("//TODO *TYPE = " + ms.getType() + "* expected." + new GetterSetterNameCalculator (ms, this, model).calcSetter() + "(\"" + ms.getShortName()+ "01\");");
            }
        }
        closeBrace();
        indentPrintln("");
    }

    /**
     * Writes the section to test fields specific to this dto for testCreate and testUpdate sections.
     */
    public void writetestCrudXXX_testDTOFieldsForTestCreateUpdate(String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintln("/*");
        incrementIndent();
        indentPrintln("A method to test the fields for a " + dtoObjectName  + ". This is called after:");
        indentPrintln("- creating a DTO, where actual is the DTO returned by the create operation, and expected is the dto passed in to the create operation");
        indentPrintln("- reading a DTO after creating it, and actual is the read DTO, and expected is the dto that was created");
        indentPrintln("- updating a DTO, where actual is DTO returned by the update operation, and expected is the dto that was passed in to the update operation");
        decrementIndent();
        indentPrintln("*/");
        indentPrintln("public void testCrud" + dtoObjectName + "_testDTOFieldsForTestCreateUpdate(" + dtoObjectName + "Info expected, " + dtoObjectName + "Info actual) ");
        openBrace();
        for (MessageStructure ms: messageStructures) {
            if (ms.getShortName().equals("id")) continue;
            if (ms.getShortName().equals("meta")) continue;
            if (ms.getShortName().equals("attributes")) continue;
            if (ms.getType().equals("String")) {
                indentPrintln("assertEquals (expected." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "(), actual." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "());");
            }
            else if (ms.getShortName().equals("descr")) {
                indentPrintln("new RichTextTester().check(expected.getDescr(), actual.getDescr());");
            }
            else {
                indentPrintln("//TODO *TYPE = " + ms.getType() + "* assertEquals (expected." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "(), actual." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "());");
            }
        }
        closeBrace();
        indentPrintln("");
    }


    /**
     * Writes the section to set fields specific to this dto for testUpdate sections.
     */
    public void writetestCrudXXX_setDTOFieldsForTestUpdate(String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintln("/*");
        incrementIndent();
        indentPrintln("A method to set the fields for a " + dtoObjectName + " in a 'test update' section prior to calling the 'update' operation.");
        decrementIndent();
        indentPrintln("*/");
        indentPrintln("public void testCrud" + dtoObjectName + "_setDTOFieldsForTestUpdate(" + dtoObjectName + "Info expected) ");
        openBrace();
        for (MessageStructure ms: messageStructures) {
            if (ms.getShortName().equals("id")) continue;
            if (ms.getShortName().equals("meta")) continue;
            if (ms.getShortName().equals("attributes")) continue;
            if (ms.getType().equals("String")) {
                indentPrintln("expected." + new GetterSetterNameCalculator (ms, this, model).calcSetter() + "(\"" + ms.getShortName()+ "_Updated\");");
            }
            else if (ms.getShortName().equals("descr")) {
                indentPrintln("expected.setDescr(RichTextHelper.buildRichTextInfo(\"descr_Updated\", \"descr_Updated\"));");
            }
            else {
                indentPrintln("//TODO *TYPE = " + ms.getType() + "* expected." + new GetterSetterNameCalculator (ms, this, model).calcSetter() + "(\"" + ms.getShortName()+ "_Updated\");");
            }
        }
        closeBrace();
        indentPrintln("");
    }

    /**
     * Writes the section to test fields specific to this dto for testReadAfterUpdate sections.
     */
    public void writetestCrudXXX_testDTOFieldsForTestReadAfterUpdate(String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintln("/*");
        incrementIndent();
        indentPrintln("A method to test the fields for a " + dtoObjectName  + " after an update operation, followed by a read operation,");
        indentPrintln("where actual is the DTO returned by the read operation, and expected is the dto returned by the update operation.");
        decrementIndent();
        indentPrintln("*/");
        indentPrintln("public void testCrud" + dtoObjectName + "_testDTOFieldsForTestReadAfterUpdate(" + dtoObjectName + "Info expected, " + dtoObjectName + "Info actual) ");
        openBrace();
        for (MessageStructure ms: messageStructures) {
            if (ms.getShortName().equals("meta")) continue;
            if (ms.getShortName().equals("attributes")) continue;
            if (ms.getType().equals("String")) {
                indentPrintln("assertEquals (expected." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "(), actual." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "());");
            }
            else if (ms.getShortName().equals("descr")) {
                indentPrintln("new RichTextTester().check(expected.getDescr(), actual.getDescr());");
            }
            else {
                indentPrintln("//TODO *TYPE = " + ms.getType() + "* assertEquals (expected." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "(), actual." + new GetterSetterNameCalculator (ms, this, model).calcGetter() + "());");
            }
        }
        closeBrace();
        indentPrintln("");
    }

    /**
     * Writes the section to set fields specific to this dto for testReadAfterUpdate sections.
     */
    public void writetestCrudXXX_setDTOFieldsForTestReadAfterUpdate(String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintln("/*");
        incrementIndent();
        indentPrintln("A method to set the fields for a " + dtoObjectName  + " in the 'test read after update' section.");
        indentPrintln("This dto is another (second) dto object being created for other tests.");
        decrementIndent();
        indentPrintln("*/");
        indentPrintln("public void testCrud" + dtoObjectName + "_setDTOFieldsForTestReadAfterUpdate(" + dtoObjectName + "Info expected) ");
        openBrace();
        for (MessageStructure ms: messageStructures) {
            if (ms.getShortName().equals("id")) continue;
            if (ms.getShortName().equals("typeKey")) continue;
            if (ms.getShortName().equals("stateKey")) continue;
            if (ms.getShortName().equals("meta")) continue;
            if (ms.getShortName().equals("descr")) continue;
            if (ms.getShortName().equals("attributes")) continue;
            if (ms.getType().equals("String")) {
                indentPrintln("expected." + new GetterSetterNameCalculator (ms, this, model).calcSetter() + "(\"" + ms.getShortName()+ "_Updated\");");
            } else {
                indentPrintln("//TODO *TYPE = " + ms.getType() + "* expected." + new GetterSetterNameCalculator (ms, this, model).calcSetter() + "(\"" + ms.getShortName()+ "_Updated\");");
            }
        }
        closeBrace();
        indentPrintln("");
    }

}
