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
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.util.ServicesFilter;
import org.kuali.student.contract.writer.service.GetterSetterNameCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.OperationsException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will generate the base class that does CRUD tests as
 * part of the Conformance Tests for services. The generated class will
 * need to be extended (and abstract methods filled in) to be complete.
 * This class is meant to be generated again and again, and developers
 * should customize tests in the extended class by extending or overwriting
 * methods of this class as needed.
 *
 * @author Mezba Mahtab (mezba.mahtab@utoronto.ca)
 */
public class ConformanceTestBaseCrudClassServiceWriter extends MockImplServiceWriter {
    
    private static final Logger log = LoggerFactory.getLogger(ConformanceTestExtendedCrudClassServiceWriter.class);

    /////////////////////////
    // CONSTANTS
    /////////////////////////

    public static final String ROOT_PACKAGE = "org.kuali.student";
    protected static final String H1_COMMENT_CHAR = "=";
    protected static final int H1_COMMENT_MARK_LENGTH = 20;

    ////////////////////////////
    // Data Variables
    ////////////////////////////

    private ServicesFilter filter;

    List<String> dtoObjectNamesWithCrud = null; // a list of all the DTOs managed by this class that has CRUDs

    protected List<String> getDtoObjectNamesWithCrud() {
        return dtoObjectNamesWithCrud;
    }

    ////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////

    public ConformanceTestBaseCrudClassServiceWriter(ServiceContractModel model,
                                                     String directory,
                                                     String rootPackage,
                                                     String servKey,
                                                     List<ServiceMethod> methods,
                                                     boolean isR1) {
        super(model, directory, rootPackage, servKey, methods, isR1, calcPackage(servKey, rootPackage), calcClassName(servKey));
        dtoObjectNamesWithCrud = calcNamesOfDTOsWithCrudManagedByService();
    }

    public ConformanceTestBaseCrudClassServiceWriter(ServiceContractModel model,
                                                     String directory,
                                                     String rootPackage,
                                                     String servKey,
                                                     List<ServiceMethod> methods,
                                                     boolean isR1,
                                                     String packageName,
                                                     String className) {
        super(model, directory, rootPackage, servKey, methods, isR1, packageName, className);
        dtoObjectNamesWithCrud = calcNamesOfDTOsWithCrudManagedByService();
    }

    //////////////////////////
    // FUNCTIONALS
    //////////////////////////

    public static String calcPackage(String servKey, String rootPackage) {
        String pack = rootPackage + ".";
        pack = pack + "service.test";
        return pack;
    }

    /**
     * Given the service key (name), returns a calculated class name for the conformance tester.
     */
    public static String calcClassName(String servKey) {
        return "Test" + GetterSetterNameCalculator.calcInitUpper(fixServKey(servKey) + "ServiceImplConformanceBaseCrud");
    }

    public static List<Service> filterServices(ServiceContractModel model, ServicesFilter filter) {
        if (filter == null) {
            return model.getServices();
        }
        return filter.filter(model.getServices());
    }


    /**
     * Write out the entire file
     */
    public void write() {
        // begin file
        indentPrintln("@RunWith(SpringJUnit4ClassRunner.class)");
        indentPrintln("@ContextConfiguration(locations = {\"classpath:" + servKey + "-test-with-mock-context.xml\"})");
        indentPrint("public abstract class " + calcClassName(servKey) + " ");
        // println(" implements " + calcServiceInterfaceClassName(servKey));
        Service serv = finder.findService(servKey);
        setPackageName(serv.getImplProject() + ".impl"); // change the package name
        importsAdd(serv.getImplProject() + "." + serv.getName()); // import for the service

        doTestImportsAdd();

        // begin main class
        openBrace();

        indentPrintln("");
        indentPrintDecoratedComment("SETUP", H1_COMMENT_CHAR, H1_COMMENT_MARK_LENGTH);
        indentPrintln("");

        // test service setup
        indentPrintln("@Resource");
        indentPrintln("public " + calcServiceInterfaceClassName(servKey) + " testService;");
        indentPrintln("public " + calcServiceInterfaceClassName(servKey) + " get" + calcServiceInterfaceClassName(servKey) + "() { return testService; }");
        indentPrintln("public void set" + calcServiceInterfaceClassName(servKey) + "(" + calcServiceInterfaceClassName(servKey) + " service) { testService = service; }");
        indentPrintln("");

        // context info setup
        indentPrintln("public ContextInfo contextInfo = null;");
        indentPrintln("public static String principalId = \"123\";");
        indentPrintln("");
        indentPrintln("@Before");
        indentPrintln("public void setUp()");
        openBrace();
        indentPrintln("principalId = \"123\";");
        indentPrintln("contextInfo = new ContextInfo();");
        indentPrintln("contextInfo.setPrincipalId(principalId);");
        closeBrace();
        indentPrintln("");

        // testing starts
        indentPrintDecoratedComment("TESTING", H1_COMMENT_CHAR, H1_COMMENT_MARK_LENGTH);
        indentPrintln("");
/*
                new ArrayList<String>();
        for (ServiceMethod method: methods) {
            // I am assuming all the DTOs will have a createXXX method.
            if (MethodType.CREATE.equals (calcMethodType(method))) {
                String objectName = calcObjectName(method);
                dtoObjectNames.add(objectName);
            }
        }
*/
        // for each DTO, write the testCRUD
        for (String dtoObjectName : dtoObjectNamesWithCrud) {
            writeTestCrud(dtoObjectName);
            indentPrintln("");
        }

        // print out list of service operations that were tested
        indentPrintDecoratedComment("SERVICE OPS TESTED IN BASE TEST CLASS", H1_COMMENT_CHAR, H1_COMMENT_MARK_LENGTH*2);
        indentPrintln("");

        // separate out crud and non-crud methods
        List<ServiceMethod> methodsTestedAsCrud = new ArrayList<ServiceMethod>();
        List<ServiceMethod> methodsNotTested = new ArrayList<ServiceMethod>();

        for (ServiceMethod method : methods) {
            if (isServiceMethodTestedAsPartofCrudInBaseConformanceTest (method)) {
                methodsTestedAsCrud.add(method);
            } else {
                methodsNotTested.add(method);
            }
        }

        // print out crud methods
        indentPrintln("/*");
        incrementIndent();
        indentPrintln("The following methods are tested as part of CRUD operations for this service's DTOs:");
        incrementIndent();
        for (ServiceMethod method: methodsTestedAsCrud) {
            indentPrintln(method.getName());
        }
        decrementIndent();
        decrementIndent();
        indentPrintln("*/");
        indentPrintln("");

        // print out list of service operations that are not tested as abstract test methods
        indentPrintDecoratedComment("SERVICE OPS NOT TESTED IN BASE TEST CLASS", H1_COMMENT_CHAR, H1_COMMENT_MARK_LENGTH*2);
        indentPrintln("");

        // for each method, create an abstract method to test that and print it out
        for (ServiceMethod method: methodsNotTested) {
            indentPrintln("/* Method Name: " + method.getName() + " */");
            indentPrintln("@Test");
            indentPrintln("public abstract void test_" + method.getName() + "() ");
            if (method.getErrors().size()>0) {
                indentPrint("throws ");
                String comma = "";
                for (ServiceMethodError error: method.getErrors()) {
                    indentPrint(comma + error.getClassName().trim());
                    comma = ",";
                }
            }
            indentPrintln(";");
            indentPrintln("");
        }

        // end file print out
        closeBrace ();
        println ("");

        // close and print file
        this.writeJavaClassAndImportsOutToFile();
        this.getOut().close();
    }

    /**
     * Write the CRUD test methods
     */
    public void writeTestCrud (String dtoObjectName) {

        // get the message structures of the dto
        List<MessageStructure> messageStructures = finder.findMessageStructures(dtoObjectName + "Info");

        // start method open signature
        indentPrintln("// ****************************************************");
        indentPrintln("//           " + dtoObjectName + "Info");
        indentPrintln("// ****************************************************");
        indentPrintln("@Test");
        indentPrintln("public void testCrud" + dtoObjectName + "() ");
        incrementIndent();
        indentPrintln("throws DataValidationErrorException,");
        incrementIndent();
        indentPrintln("DoesNotExistException,");
        indentPrintln("InvalidParameterException,");
        indentPrintln("MissingParameterException,");
        indentPrintln("OperationFailedException,");
        indentPrintln("PermissionDeniedException,");
        indentPrintln("ReadOnlyException,");
        indentPrintln("VersionMismatchException,");
        indentPrintln("DependentObjectsExistException");
        decrementIndent();
        decrementIndent();
        openBrace();
        // end method open signature

        // write the test portions
        incrementIndent();
        writeTestCreate(dtoObjectName, messageStructures);
        writeTestUpdate(dtoObjectName, messageStructures);
        writeTestReadAfterUpdate(dtoObjectName, messageStructures);
        writeTestDelete(dtoObjectName, messageStructures);
        decrementIndent();

        // end method
        closeBrace();
        indentPrintln("");

        // methods that will be overwritten
        writetestCrudXXX_setDTOFieldsForTestCreate(dtoObjectName, messageStructures);
        writetestCrudXXX_testDTOFieldsForTestCreateUpdate(dtoObjectName, messageStructures);
        writetestCrudXXX_setDTOFieldsForTestUpdate(dtoObjectName, messageStructures);
        writetestCrudXXX_testDTOFieldsForTestReadAfterUpdate(dtoObjectName, messageStructures);
        writetestCrudXXX_setDTOFieldsForTestReadAfterUpdate(dtoObjectName, messageStructures);
    }

    /**
     * Write the 'test create' portion.
     */
    public void writeTestCreate (String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintDecoratedComment("test create");
        indentPrintln(dtoObjectName + "Info expected = new " + dtoObjectName + "Info ();");
        // indentPrintln("expected.setName(\"Name01\");");
        // indentPrintln("expected.setDescr(new RichTextHelper().fromPlain(\"Description01\"));");
        indentPrintln("");
        indentPrintln("// METHOD TO SET DTO FIELDS HERE FOR TEST CREATE");
        indentPrintln("testCrud" + dtoObjectName + "_setDTOFieldsForTestCreate (expected);");
        indentPrintln("");

/*
        indentPrintln("if (expected.getClass().isAssignableFrom(TypeStateEntityInfo.class))");
        openBrace();
        incrementIndent();
        // indentPrintln("TypeStateEntityInfo expectedTS = (TypeStateEntityInfo) expected;");
        indentPrintln("expected.setTypeKey(\"typeKey01\");");
        indentPrintln("expected.setStateKey(\"stateKey01\");");
        decrementIndent();
        closeBrace();
*/
        indentPrintln("new AttributeTester().add2ForCreate(expected.getAttributes());");

        indentPrintln("");
        indentPrintln("// code to create actual");
        indentPrintln(dtoObjectName + "Info actual = " + getMethodCallAsString ("create" + dtoObjectName, "= null; // TODO INSERT CODE TO CREATE actual HERE", MethodType.CREATE, dtoObjectName, "expected"));
        indentPrintln("");
        
        String testerName = null;
        String keyOrId = null;
        if (finder.findMessageStructure(dtoObjectName + "Info", "id") != null) {
            keyOrId = "Id";
            if (finder.findMessageStructure(dtoObjectName + "Info", "name") != null) {
                testerName = "IdEntityTester";
            } else if (finder.findMessageStructure(dtoObjectName + "Info", "effectiveDate") != null) {
                testerName = "RelationshipTester";
            } else {
                testerName = "IdNamelessEntityTester";
                // should also detect relationships
            }
        } else if (finder.findMessageStructure(dtoObjectName + "Info", "key") != null) {
            keyOrId = "Key";
            testerName = "KeyEntityTester";
        }

        // indentPrintln("if (actual.getClass().isAssignableFrom(IdEntityInfo.class))");
        if (testerName !=null) {
            // openBrace();
            // incrementIndent();
            indentPrintln("assertNotNull(actual.get" + keyOrId + "());");
            indentPrintln("new " + testerName + "().check(expected, actual);");
            // decrementIndent();
            // closeBrace();
        }

        indentPrintln("");
        indentPrintln("// METHOD TO TEST DTO FIELDS HERE FOR TEST CREATE");
        indentPrintln("testCrud" + dtoObjectName + "_testDTOFieldsForTestCreateUpdate (expected, actual);");
        indentPrintln("");

        indentPrintln("new AttributeTester().check(expected.getAttributes(), actual.getAttributes());");
        indentPrintln("new MetaTester().checkAfterCreate(actual.getMeta());");
        indentPrintln("");

        indentPrintDecoratedComment("test read");
        indentPrintln("expected = actual;");
        indentPrintln("actual = " + getMethodCallAsString ("get" + dtoObjectName, "null; // TODO INSERT CODE TO GET actual HERE BY CALLING SERVICE OP", MethodType.GET_BY_ID, dtoObjectName, "actual"));

        if (testerName !=null) {
            indentPrintln("assertEquals(expected.get" + keyOrId + "(), actual.get" + keyOrId + "());");
            indentPrintln("new " + testerName + "().check(expected, actual);");
        }

        indentPrintln("");

        indentPrintln("// INSERT CODE FOR TESTING MORE DTO FIELDS HERE");
        indentPrintln("testCrud" + dtoObjectName + "_testDTOFieldsForTestCreateUpdate (expected, actual);");
        indentPrintln("");

        indentPrintln("new AttributeTester().check(expected.getAttributes(), actual.getAttributes());");
        indentPrintln("new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());");
        indentPrintln("");
    }

    /**
     * Write the 'test update' portion.
     */
    public void writeTestUpdate (String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintDecoratedComment("test update");
        indentPrintln(dtoObjectName + "Info original = new " + dtoObjectName + "Info (actual);");
        indentPrintln("expected = new " + dtoObjectName + "Info (actual);");
        
        // indentPrintln("expected.setName(expected.getName() + \" updated\");");
        // indentPrintln("expected.setDescr(new RichTextHelper().fromPlain(expected.getDescr().getPlain() + \"_Updated\"));");
        indentPrintln("");
        // indentPrintln("if (expected.getClass().isAssignableFrom(TypeStateEntityInfo.class))");
        if (finder.findMessageStructure(dtoObjectName + "Info", "stateKey")!=null) {
            // openBrace();
            // incrementIndent();
            // indentPrintln("TypeStateEntityInfo expectedTS = (TypeStateEntityInfo) expected;");
            indentPrintln("expected.setStateKey(expected.getState() + \"_Updated\");");
            // decrementIndent();
            // closeBrace();
        }
        indentPrintln("");
        indentPrintln("// METHOD TO INSERT CODE TO UPDATE DTO FIELDS HERE");
        indentPrintln("testCrud" + dtoObjectName + "_setDTOFieldsForTestUpdate (expected);");
        indentPrintln("");
        indentPrintln("new AttributeTester().delete1Update1Add1ForUpdate(expected.getAttributes());");

        indentPrintln("// code to update");
        indentPrintln("actual = " + getMethodCallAsString ("update" + dtoObjectName, "= null; // TODO INSERT CODE TO CALL UPDATE SERVICE OP HERE", MethodType.UPDATE, dtoObjectName, "expected"));
        indentPrintln("");
        
        String testerName = null;
        String keyOrId = null;
        if (finder.findMessageStructure(dtoObjectName + "Info", "id") != null) {
            keyOrId = "Id";
            if (finder.findMessageStructure(dtoObjectName + "Info", "name") != null) {
                testerName = "IdEntityTester";
            } else if (finder.findMessageStructure(dtoObjectName + "Info", "effectiveDate") != null) {
                testerName = "RelationshipTester";
            } else {
                testerName = "IdNamelessEntityTester";
            }
        } else if (finder.findMessageStructure(dtoObjectName + "Info", "key") != null) {
            keyOrId = "Key";
            testerName = "KeyEntityTester";
        }
        if (testerName !=null) {
            indentPrintln("assertEquals(expected.get" + keyOrId + "(), actual.get" + keyOrId + "());");
            indentPrintln("new " + testerName + "().check(expected, actual);");
        }
        indentPrintln("");
        indentPrintln("// METHOD TO INSERT CODE FOR TESTING DTO FIELDS HERE");
        indentPrintln("testCrud" + dtoObjectName + "_testDTOFieldsForTestCreateUpdate (expected, actual);");
        indentPrintln("");
        indentPrintln("new AttributeTester().check(expected.getAttributes(), actual.getAttributes());");
        indentPrintln("new MetaTester().checkAfterUpdate(expected.getMeta(), actual.getMeta());");
        indentPrintln("");
        
        indentPrintln("// Test that VersionMissmatchException's are being detected");
        indentPrintln("boolean exception = false;");        
        indentPrintln("try {");
        indent(getOut(), ' ');
        
        indentPrintln(getMethodCallAsString ("update" + dtoObjectName, "= null; // TODO INSERT CODE TO CALL UPDATE SERVICE OP HERE", MethodType.UPDATE, dtoObjectName, "original"));
        indentPrintln("}");
        indentPrintln("catch (VersionMismatchException e) { "); 
        indent(getOut(), ' ');
        indentPrint("exception = true;");
        indentPrintln("}");
        indentPrintln("");
        
        indentPrintln("Assert.assertTrue(\"VersionMissmatchException was not detected!\", exception);");
        indentPrintln("");
        
    }

    /**
     * Write the 'read after update' portion.
     */
    public void writeTestReadAfterUpdate (String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintDecoratedComment("test read after update");
        indentPrintln("");
        indentPrintln("expected = actual;");

        String testerName = null;
        String keyOrId = null;
        if (finder.findMessageStructure(dtoObjectName + "Info", "id") != null) {
            keyOrId = "Id";
            if (finder.findMessageStructure(dtoObjectName + "Info", "name") != null) {
                testerName = "IdEntityTester";
            } else if (finder.findMessageStructure(dtoObjectName + "Info", "effectiveDate") != null) {
                testerName = "RelationshipTester";
            } else {
                testerName = "IdNamelessEntityTester";
            }
        } else if (finder.findMessageStructure(dtoObjectName + "Info", "key") != null) {
            keyOrId = "Key";
            testerName = "KeyEntityTester";
        }
        
        indentPrintln("// code to get actual");
        indentPrintln("actual = " + getMethodCallAsString ("get" + dtoObjectName, "null; // TODO INSERT CODE TO GET actual HERE BY CALLING SERVICE OP", MethodType.GET_BY_ID, dtoObjectName, "actual"));
        indentPrintln("");
        
        
        if (testerName !=null) {
            indentPrintln("assertEquals(expected.get" + keyOrId + "(), actual.get" + keyOrId + "());");
            indentPrintln("new " + testerName + "().check(expected, actual);");
        }
        indentPrintln("");
        indentPrintln("// INSERT METHOD CODE FOR TESTING DTO FIELDS HERE");
        indentPrintln("testCrud" + dtoObjectName + "_testDTOFieldsForTestReadAfterUpdate (expected, actual);");
        indentPrintln("");
        indentPrintln("new AttributeTester().check(expected.getAttributes(), actual.getAttributes());");
        indentPrintln("new MetaTester().checkAfterGet(expected.getMeta(), actual.getMeta());");
        indentPrintln("");
        indentPrintln(dtoObjectName + "Info alphaDTO = actual;");
        indentPrintln("");
        indentPrintln("// create a 2nd DTO");
        indentPrintln(dtoObjectName + "Info betaDTO = new " + dtoObjectName + "Info ();");
        // indentPrintln("betaDTO.setName(\"Beta entity name\");");
        // indentPrintln("betaDTO.setDescr(new RichTextHelper().fromPlain(\"Beta entity description\"));");
        indentPrintln("");
        indentPrintln("// METHOD TO INSERT CODE TO SET MORE DTO FIELDS HERE");
        indentPrintln("testCrud" + dtoObjectName + "_setDTOFieldsForTestReadAfterUpdate (betaDTO);");
        indentPrintln("");
        // indentPrintln("if (betaDTO.getClass().isAssignableFrom(TypeStateEntityInfo.class))");
        if (finder.findMessageStructure(dtoObjectName + "Info", "typeKey")!=null) {
            // openBrace();
            // incrementIndent();
            //indentPrintln("TypeStateEntityInfo betaDTOTS = (TypeStateEntityInfo) betaDTO;");
            indentPrintln("betaDTO.setTypeKey(\"typeKeyBeta\");");
            // decrementIndent();
            // closeBrace();
        }
        if (finder.findMessageStructure(dtoObjectName + "Info", "stateKey")!=null) {
            indentPrintln("betaDTO.setStateKey(\"stateKeyBeta\");");
        }
        indentPrintln("betaDTO = " + getMethodCallAsString("create" + dtoObjectName, "null; // TODO INSERT CODE TO CREATE betaDTO", MethodType.CREATE, dtoObjectName, "betaDTO"));

        indentPrintln("");
        indentPrintDecoratedComment("test bulk get with no ids supplied");
        indentPrintln("");
        indentPrintln("List<String> " + initLower(dtoObjectName) + "Ids = new ArrayList<String>();");

        indentPrintln("// code to get DTO by Ids");
        indentPrintln("List<" + dtoObjectName + "Info> records = " + getMethodCallAsString ("get" + dtoObjectName + "sByIds", "null; // TODO INSERT CODE TO GET DTO BY IDS", MethodType.GET_BY_IDS, dtoObjectName));
        indentPrintln("");

        indentPrintln("assertEquals(" + initLower(dtoObjectName) + "Ids.size(), records.size());");
        indentPrintln("assertEquals(0, " + initLower(dtoObjectName) + "Ids.size());");
        indentPrintln("");
        indentPrintDecoratedComment("test bulk get");
        indentPrintln(initLower(dtoObjectName) + "Ids = new ArrayList<String>();");
        indentPrintln(initLower(dtoObjectName) + "Ids.add(alphaDTO.getId());");
        indentPrintln(initLower(dtoObjectName) + "Ids.add(betaDTO.getId());");

        indentPrintln("// code to get DTO by Ids");
        indentPrintln("records = " + getMethodCallAsString ("get" + dtoObjectName + "sByIds", "null; // TODO INSERT CODE TO GET DTO BY IDS", MethodType.GET_BY_IDS, dtoObjectName));
        indentPrintln("");

        indentPrintln("assertEquals(" + initLower(dtoObjectName) + "Ids.size(), records.size());");
        indentPrintln("for (" + dtoObjectName + "Info record : records)");
        openBrace();
        incrementIndent();
        indentPrintln("if (!" + initLower(dtoObjectName) + "Ids.remove(record.getId()))");
        openBrace();
        incrementIndent();
        indentPrintln("fail(record.getId());");
        decrementIndent();
        closeBrace();
        decrementIndent();
        closeBrace();
        indentPrintln("assertEquals(0, " + initLower(dtoObjectName) + "Ids.size());");
        indentPrintln("");
        indentPrintDecoratedComment("test get by type");

        indentPrintln("// code to get by specific type \"typeKey01\" ");
        indentPrintln(initLower(dtoObjectName) + "Ids = testService.get" + dtoObjectName + "IdsByType (\"typeKey_Updated\", contextInfo);");
        // indentPrintln(initLower(dtoObjectName) + "Ids = " + getMethodCallAsString ("get" + dtoObjectName + "IdsByType", "// INSERT CODE TO GET BY SPECIFIC TYPE \"typeKey01\" HERE", MethodType.GET_IDS_BY_TYPE));
        indentPrintln("");

        indentPrintln("assertEquals(1, " + initLower(dtoObjectName) + "Ids.size());");
        indentPrintln("assertEquals(alphaDTO.getId(), " + initLower(dtoObjectName) + "Ids.get(0));");
        indentPrintln("");
        indentPrintln("// test get by other type");

        indentPrintln("// code to get by specific type \"typeKeyBeta\" ");
        indentPrintln(initLower(dtoObjectName) + "Ids = testService.get" + dtoObjectName + "IdsByType (\"typeKeyBeta\", contextInfo);");
        // indentPrintln(initLower(dtoObjectName) + "Ids = " + getMethodCallAsString ("get" + dtoObjectName + "IdsByType", "// INSERT CODE TO GET BY SPECIFIC TYPE \"typeKeyBeta\" HERE", MethodType.GET_IDS_BY_TYPE));
        indentPrintln("");

        indentPrintln("assertEquals(1, " + initLower(dtoObjectName) + "Ids.size());");
        indentPrintln("assertEquals(betaDTO.getId(), " + initLower(dtoObjectName) + "Ids.get(0));");
        indentPrintln("");
    }

    /**
     * Write the 'delete' portion.
     */
    public void writeTestDelete (String dtoObjectName, List<MessageStructure> messageStructures) {
        indentPrintDecoratedComment("test delete");
        indentPrintln("");

        indentPrintln("StatusInfo status = " + getMethodCallAsString ("delete" + dtoObjectName, "null; // TODO INSERT CODE TO DELETE RECORD", MethodType.DELETE, dtoObjectName, "actual"));
        indentPrintln("");

        indentPrintln("assertNotNull(status);");
        indentPrintln("assertTrue(status.getIsSuccess());");
        indentPrintln("try");
        openBrace();
        incrementIndent();
        indentPrintln(dtoObjectName + "Info record = " + getMethodCallAsString ("get" + dtoObjectName, "null; // TODO INSERT CODE TO RETRIEVE RECENTLY DELETED RECORD", MethodType.GET_BY_ID, dtoObjectName, "actual"));
        indentPrintln("fail(\"Did not receive DoesNotExistException when attempting to get already-deleted entity\");");
        decrementIndent();
        closeBrace();
        indentPrintln("catch (DoesNotExistException dnee)");
        openBrace();
        incrementIndent();
        indentPrintln("// expected");
        decrementIndent();
        closeBrace();
        indentPrintln("");
    }

    /**
     * Writes out a decorated comment.
     */
    public void indentPrintDecoratedComment (String label) {
        indentPrintDecoratedComment(label, "-", 37);
/*
        indentPrintln("// -------------------------------------");
        indentPrintln("// " + label);
        indentPrintln("// -------------------------------------");
*/
    }

    /**
     * Writes out a decorated comment, with the decoration string passed in.
     */
    public void indentPrintDecoratedComment (String label, String decorChar, int decorLength) {
        String decorPattern = "";
        for (int i=0; i<decorLength; i++) { decorPattern += decorChar; }
        indentPrintln("// " + decorPattern);
        indentPrintln("// " + label);
        indentPrintln("// " + decorPattern);
    }

    /**
     * Gets the method with the name.
     */
    private ServiceMethod getServiceMethod (String methodName) throws OperationsException {
        for (ServiceMethod method : methods) {
            if (method.getName().equals(methodName)) return method;
        }
        throw new OperationsException("Method " + methodName + " not found!");
    }

    /**
     * Gets the string to print to call a method with the given name.
     * @param dtoObjectName 
     */
    private String getMethodCallAsString (String builtUpMethodName, String errorCode, MethodType methodType, String dtoObjectName) {
        return getMethodCallAsString (builtUpMethodName, errorCode, methodType, dtoObjectName, null);
    }

    /**
     * Gets the string to print to call a method with the given name.
     */
    private String getMethodCallAsString (String builtUpMethodName, String errorCode, MethodType methodType, String dtoName, String dtoNameReplacement) {
        try {
            ServiceMethod method = getServiceMethod(builtUpMethodName);
            String methodCallStr = "";
            methodCallStr += method.getName() + " (";
            String comma = " ";
            for (ServiceMethodParameter param : method.getParameters()) {
                methodCallStr += comma;
                if (dtoName!=null && dtoNameReplacement!=null) {
                    if ((dtoName + "Info").toLowerCase().equals(param.getName().toLowerCase())) {
                        methodCallStr += dtoNameReplacement;
                    }
                    else if (param.getName().endsWith("TypeKey")) {
                        methodCallStr += dtoNameReplacement + ".getTypeKey()";
                    }
                    else if (param.getName().endsWith("Id")) {
                        methodCallStr += dtoNameReplacement + ".getId()";
                    }
                    else {
                        methodCallStr += param.getName();
                    }
                } else {
                    methodCallStr += param.getName();
                }
                comma = ", ";
            }
            methodCallStr += ");";
            return "testService." + methodCallStr;
        } catch (Exception e) {
            
            if (dtoName != null)
                log.error("dtoName = " + dtoName + ", methodName = " + builtUpMethodName + ", errorCode = " + errorCode); 
            else
                log.error("dtoName = unknown, methodName = " + builtUpMethodName + ", errorCode = " + errorCode);
            
            return errorCode;
        }

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
        indentPrintln("public abstract void testCrud" + dtoObjectName + "_setDTOFieldsForTestCreate(" + dtoObjectName + "Info expected);");
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
        indentPrintln("public abstract void testCrud" + dtoObjectName + "_testDTOFieldsForTestCreateUpdate(" + dtoObjectName + "Info expected, " + dtoObjectName + "Info actual);");
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
        indentPrintln("public abstract void testCrud" + dtoObjectName + "_setDTOFieldsForTestUpdate(" + dtoObjectName + "Info expected);");
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
        indentPrintln("public abstract void testCrud" + dtoObjectName + "_testDTOFieldsForTestReadAfterUpdate(" + dtoObjectName + "Info expected, " + dtoObjectName + "Info actual);");
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
        indentPrintln("public abstract void testCrud" + dtoObjectName + "_setDTOFieldsForTestReadAfterUpdate(" + dtoObjectName + "Info expected);");
        indentPrintln("");
    }

    /**
     * Given a method type, returns true if this method is tested as part of CRUD operations
     * tested by the base test conformance class.
     */
        protected boolean isServiceMethodTestedAsPartofCrudInBaseConformanceTest (ServiceMethod method) {
            MethodType methodType = calcMethodType(method);
            if ((MethodType.CREATE.equals(methodType))
                || (MethodType.UPDATE.equals(methodType))
                || (MethodType.DELETE.equals(methodType))
                || (MethodType.GET_BY_ID.equals(methodType))
                || (MethodType.GET_BY_IDS.equals(methodType))
                || (MethodType.GET_IDS_BY_TYPE.equals(methodType))
                || (MethodType.GET_TYPE.equals(methodType))
                || (MethodType.GET_TYPES.equals(methodType))
                || (MethodType.GET_IDS_BY_TYPE.equals(methodType))
                ) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Gets a list of all the DTO names that are part of this service.
     */
    protected List<String> calcNamesOfDTOsWithCrudManagedByService() {
        List<String> dtoObjectNames = new ArrayList<String>();
        for (ServiceMethod method: methods) {
            // I am assuming all the DTOs will have a createXXX method.
            if (MethodType.CREATE.equals (calcMethodType(method))) {
                String objectName = calcObjectName(method);
                // check if this is a valid info object
                if (finder.findXmlType(objectName + "Info")!=null) {
                    dtoObjectNames.add(objectName);
                }
/*
                for (XmlType xmlType: model.getXmlTypes()) {
                    if (xmlType.getName().equals(objectName + "Info")) {
                        dtoObjectNames.add(objectName);
                        break;
                    }
                }
*/
            }
        }
        return dtoObjectNames;
    }

    /**
     * Does the importsAdd for all files required for testing
     */
    protected void doTestImportsAdd() {
        // kuali imports
        importsAdd("org.kuali.student.common.test.util.IdEntityTester");
        importsAdd("org.kuali.student.common.test.util.KeyEntityTester");
        importsAdd("org.kuali.student.r2.common.dto.ContextInfo");
        importsAdd("org.kuali.student.r2.common.dto.IdEntityInfo");
        importsAdd("org.kuali.student.r2.common.dto.StatusInfo");
        importsAdd("org.kuali.student.r2.common.dto.TypeStateEntityInfo");
        importsAdd("org.kuali.student.r2.common.util.RichTextHelper");
        importsAdd("org.kuali.student.r2.core.organization.service.impl.lib.AttributeTester");
        importsAdd("org.kuali.student.r2.core.organization.service.impl.lib.MetaTester");

        // import all the dto
        // for each DTO, write the testCRUD
        for (String dtoObjectName : dtoObjectNamesWithCrud) {
            try { importsAdd(finder.findXmlType(dtoObjectName + "Info").getJavaPackage() + "." + dtoObjectName + "Info"); }
            catch (Exception ignored) {}
        }

        // exceptions
        importsAdd("org.kuali.student.r2.common.exceptions.DataValidationErrorException");
        importsAdd("org.kuali.student.r2.common.exceptions.DependentObjectsExistException");
        importsAdd("org.kuali.student.r2.common.exceptions.DoesNotExistException");
        importsAdd("org.kuali.student.r2.common.exceptions.InvalidParameterException");
        importsAdd("org.kuali.student.r2.common.exceptions.MissingParameterException");
        importsAdd("org.kuali.student.r2.common.exceptions.OperationFailedException");
        importsAdd("org.kuali.student.r2.common.exceptions.PermissionDeniedException");
        importsAdd("org.kuali.student.r2.common.exceptions.ReadOnlyException");
        importsAdd("org.kuali.student.r2.common.exceptions.VersionMismatchException");

        // java imports
        importsAdd("org.springframework.test.context.ContextConfiguration");
        importsAdd("org.springframework.test.context.junit4.SpringJUnit4ClassRunner");
        importsAdd("org.junit.Assert");
        importsAdd("org.junit.Before");
        importsAdd("org.junit.Test");
        importsAdd("org.junit.runner.RunWith");
        importsAdd("static org.junit.Assert.assertEquals");
        importsAdd("static org.junit.Assert.assertFalse");
        importsAdd("static org.junit.Assert.assertNotNull");
        importsAdd("static org.junit.Assert.assertNull");
        importsAdd("static org.junit.Assert.assertTrue");
        importsAdd("static org.junit.Assert.fail");
        importsAdd("javax.annotation.Resource");
        importsAdd("java.util.ArrayList");
        importsAdd("java.util.List");
    }
}
