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
package org.kuali.student.datadictionary.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.StringEscapeUtils;
import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author nwright
 */
public class KradDictionaryCreator {

	private static final Logger log = LoggerFactory
			.getLogger(KradDictionaryCreator.class);

	private ServiceContractModel model;
	private ModelFinder finder;
	private String directory;
	private String className;
	private XmlType xmlType;
	private XmlWriter gwriter;
	private XmlWriter mwriter;
	private List<MessageStructure> messageStructures;
	private boolean writeManual;
	private boolean writeGenerated;
	private String generatedFilePath;
	private String manualFilePath;

	private boolean initialized = false;

	private List<String> enumeratedTypeList;


	public KradDictionaryCreator(String directory, ServiceContractModel model,
			String className, boolean writeManual, boolean writeGenerated, Map<String, String> typeOverrides) {
		this.directory = directory;
		this.model = model;
		this.finder = new ModelFinder(this.model);
		this.className = className;
		this.xmlType = this.finder.findXmlType(className);
		if (xmlType == null) {
			throw new IllegalArgumentException(className);
		}
		this.messageStructures = this.finder.findMessageStructures(className);
		this.writeManual = writeManual;
		this.writeGenerated = writeGenerated;
		// if (this.messageStructures.isEmpty()) {
		// throw new IllegalStateException(className);
		// }
		
		initializeTypes (typeOverrides);
	}

	private void initializeTypes(Map<String, String> typeOverrides) {
		
		if (KradDictionaryCreator.predefinedFieldMap == null) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", "BaseKuali.id");
			map.put("key", "BaseKuali.key");
			map.put("name", "BaseKuali.name");
			map.put("descr", "BaseKuali.descr");
			map.put("plain", "BaseKuali.descr.plain");
			map.put("formatted", "BaseKuali.descr.formatted");
			map.put("desc", "BaseKuali.desc"); // r1 compatibility
			map.put("typeKey", "BaseKuali.typeKey");
			map.put("stateKey", "BaseKuali.stateKey");
			map.put("type", "BaseKuali.type"); // r1 compatibility
			map.put("state", "BaseKuali.state"); // r1 compatibility
			map.put("effectiveDate", "BaseKuali.effectiveDate");
			map.put("expirationDate", "BaseKuali.expirationDate");
			map.put("meta", "BaseKuali.meta");
			map.put("createTime", "BaseKuali.meta.createTime");
			map.put("updateTime", "BaseKuali.meta.updateTime");
			map.put("createId", "BaseKuali.meta.createId");
			map.put("updateId", "BaseKuali.meta.updateId");
			map.put("versionInd", "BaseKuali.meta.versionInd");
			// convert to lower case
			predefinedFieldMap = new HashMap<String, String>(map.size());
			for (String key : map.keySet()) {
				predefinedFieldMap.put(key.toLowerCase(), map.get(key));
			}
		}
		
		
		
		if (KradDictionaryCreator.endsWithMap == null) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("startDate", "BaseKuali.startDate");
			map.put("endDate", "BaseKuali.endDate");
			map.put("start", "BaseKuali.start");
			map.put("end", "BaseKuali.end");
			map.put("OrgId", "BaseKuali.orgId");
			map.put("OrgIds", "BaseKuali.orgId");
			map.put("PersonId", "BaseKuali.personId");
			map.put("PersonIds", "BaseKuali.personId");
			map.put("PrincipalId", "BaseKuali.principalId");
			map.put("PrincipalIds", "BaseKuali.principalId");
			map.put("CluId", "BaseKuali.cluId");
			map.put("CluIds", "BaseKuali.cluId");
			map.put("LuiId", "BaseKuali.luiId");
			map.put("LuiIds", "BaseKuali.luiId");
			map.put("AtpId", "BaseKuali.atpId");
			map.put("AtpIds", "BaseKuali.atpId");
			map.put("TermId", "BaseKuali.termId");
			map.put("TermIds", "BaseKuali.termId");
			map.put("HolidayCalendarId", "BaseKuali.holidayCalendarId");
			map.put("HolidayCalendarIds", "BaseKuali.holidayCalendarId");
			map.put("Code", "BaseKuali.code");
			// convert to lower case
			endsWithMap = new HashMap<String, String>(map.size());
			for (String key : map.keySet()) {
				endsWithMap.put(key.toLowerCase(), map.get(key));
			}
		}
		
		if (KradDictionaryCreator.typeMap == null){
			Map<String, String> map = new HashMap<String, String>();
			map.put("String", "BaseKuali.string");
			map.put("DateTime", "BaseKuali.dateTime");
			map.put("Date", "BaseKuali.date");
			map.put("Boolean", "BaseKuali.boolean");
			map.put("Integer", "BaseKuali.integer");
			// having primitives is a bug but this will fix the issue with CluInfo
			// for now.
			map.put("int", "BaseKuali.integer");

			map.put("Long", "BaseKuali.long");
			map.put("Float", "BaseKuali.float");
			map.put("Double", "BaseKuali.double");
			
			// TODO: this should be externalized into mojo.
//			map.put("enum", "BaseKuali.complex");
			
			// convert to lower case
			typeMap = new HashMap<String, String>(map.size());
			for (String key : map.keySet()) {
				typeMap.put(key.toLowerCase(), map.get(key));
			}
			
			if (typeOverrides != null) {
				// apply any overrides
				for (String key : typeOverrides.keySet()) {

					String value = typeOverrides.get(key);
					typeMap.put(key, value);

					log.warn("OVERRIDING Type Mapping '" + key + "' -> '"
					        + value + "'");
				}
			}
		}
    }

	public void write() {
		this.initXmlWriters();
		if (writeGenerated) {
			this.writeSpringHeaderOpen(gwriter);
			this.writeWarning(gwriter);
			this.writeGeneratedObjectStructure(gwriter);
			this.writeSpringHeaderClose(gwriter);
		}
		if (this.writeManual) {
			this.writeSpringHeaderOpen(mwriter);
			this.writeNote(mwriter);
			this.writeManualImports(mwriter);
			this.writeManualObjectStructure(mwriter);
			this.writeSpringHeaderClose(mwriter);
		}

		initialized = true;
	}

	private void initXmlWriters() {
		String generatedFileName = "ks-" + initUpper(className)
				+ "-dictionary-generated.xml";
		String manualFileName = "ks-" + initUpper(className)
				+ "-dictionary.xml";

		File dir = new File(this.directory);
		// System.out.indentPrintln ("Writing java class: " + fileName + " to "
		// + dir.getAbsolutePath ());

		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IllegalStateException("Could not create directory "
						+ this.directory);
			}
		}

		if (writeGenerated) {
			String dirStr = this.directory + "/generated";
			File dirFile = new File(dirStr);
			if (!dirFile.exists()) {
				if (!dirFile.mkdirs()) {
					throw new IllegalStateException(
							"Could not create directory " + dirStr);
				}
			}
			try {
				PrintStream out = new PrintStream(new FileOutputStream(
						generatedFilePath = dirStr + "/"+ generatedFileName, false));
				this.gwriter = new XmlWriter(out, 0);
			} catch (FileNotFoundException ex) {
				throw new IllegalStateException(ex);
			}
		}
		if (this.writeManual) {
			String dirStr = this.directory + "/manual";
			File dirFile = new File(dirStr);
			if (!dirFile.exists()) {
				if (!dirFile.mkdirs()) {
					throw new IllegalStateException(
							"Could not create directory " + dirStr);
				}
			}
			try {
				PrintStream out = new PrintStream(new FileOutputStream(
						manualFilePath = dirStr + "/" + manualFileName, false));
				this.mwriter = new XmlWriter(out, 0);
			} catch (FileNotFoundException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

	private static String initLower(String str) {
		if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return str;
		}
		if (str.length() == 1) {
			return str.toLowerCase();
		}
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	private static String initUpper(String str) {
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

	private void writeSpringHeaderClose(XmlWriter out) {
		out.decrementIndent();
		out.indentPrintln("</beans>");
	}

	private void writeSpringHeaderOpen(XmlWriter out) {
		out.indentPrintln("<!--");
		out.indentPrintln(" Copyright 2011 The Kuali Foundation");
		out.println("");
		out.indentPrintln(" Licensed under the Educational Community License, Version 2.0 (the \"License\");");
		out.indentPrintln(" you may not use this file except in compliance with the License.");
		out.indentPrintln(" You may obtain a copy of the License at");
		out.indentPrintln("");
		out.indentPrintln(" http://www.opensource.org/licenses/ecl2.php");
		out.println("");
		out.indentPrintln(" Unless required by applicable law or agreed to in writing, software");
		out.indentPrintln(" distributed under the License is distributed on an \"AS IS\" BASIS,");
		out.indentPrintln(" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
		out.indentPrintln(" See the License for the specific language governing permissions and");
		out.indentPrintln(" limitations under the License.");
		out.indentPrintln("-->");
		out.indentPrintln("<beans xmlns=\"http://www.springframework.org/schema/beans\"");
		out.indentPrintln("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		out.indentPrintln("xsi:schemaLocation=\""
				+ "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd"
				+ "\">");
		out.println("");
		out.incrementIndent();
	}

	private void writeWarning(XmlWriter out) {
		out.println("");
		out.indentPrintln("<!-- ********************************************************");
		out.incrementIndent();
		out.indentPrintln("                       WARNING ");
		out.indentPrintln("             DO NOT UPDATE THIS FILE MANUALLY");
		out.indentPrintln("This dictionary file was automatically generated");
		out.indentPrintln("The DictionaryGeneratorMojo reads the service contract ");
		out.indentPrintln("and creates these ks-XXXX-dictionary-generated.xml files.");
		out.println("");
		out.indentPrintln("If this file is out of sync with the contract re-run the mojo.");
		out.println("");
		out.indentPrintln("To add additional constraints or change these default values (perhaps");
		out.indentPrintln("because the generator is not perfect) please update the corresponding ");
		out.indentPrintln("ks-XXXX-dictionary.xml instead of this one.");
		out.decrementIndent();
		out.indentPrintln("************************************************************* -->");
	}

	private void writeNote(XmlWriter out) {
		out.println("");
		out.indentPrintln("<!-- ********************************************************");
		out.incrementIndent();
		out.indentPrintln("                       NOTE");
		out.indentPrintln("          THIS FILE WAS INTENDED TO BE MODIFIED");
		out.println("");
		out.indentPrintln("While this file was originally generated, it");
		out.indentPrintln("was intended to be subsequently modified by hand.");
		out.indentPrintln("It imports a corresponding ks-XXXX-dictionary-generated.xml file, ");
		out.indentPrintln("that was also automatically generated by the ContractDocMojo.");
		out.indentPrintln("This file gives you the ability to layer on addiditional definitions and constrints");
		out.indentPrintln("that are not/cannot be generated simply by reading the service contract.");
		out.println("");
		out.indentPrintln("The goal of this file is to be able to re-generate the corresponding");
		out.indentPrintln("ks-XXXX-dictionary-generated.xml file without affecting these manually entered additions");
		out.indentPrintln("that are encoded here.");
		out.decrementIndent();
		out.indentPrintln("************************************************************* -->");
	}

	private void writeManualImports(XmlWriter out) {
		out.writeComment("The following file gets generated during the build and gets put into the target/classes directory");
		out.indentPrintln("<import resource=\"classpath:ks-"
				+ initUpper(className) + "-dictionary-generated.xml\"/>");
		Set<String> imports = this.getComplexSubObjectsThatAreLists();
		if (!imports.isEmpty()) {
			out.writeComment("TODO: remove these once the jira about lists of complex objects gets fixed");
			for (String impName : imports) {
				out.indentPrintln("<import resource=\"classpath:ks-"
						+ initUpper(impName) + "-dictionary.xml\"/>");
			}
		}
	}

	private Set<String> getComplexSubObjectsThatAreLists() {
		Set<String> list = new LinkedHashSet<String>();
		for (MessageStructure ms : this.messageStructures) {
			switch (this.calculateCategory(ms)) {
			case LIST_OF_COMPLEX:
				String classNameToAdd = this.stripListOffEnd(ms.getType());
				// Avoid recursive calls
				if (!classNameToAdd.equalsIgnoreCase(className)) {
					list.add(classNameToAdd);
				}

				break;
			default:
				// fall though - do nothing
				break;
			}
		}
		return list;
	}

	private String stripListOffEnd(String name) {
		if (name.endsWith("List")) {
			return name.substring(0, name.length() - "List".length());
		}
		return name;
	}

	private String calcDataObjectClass(XmlType xmlType) {
		// this is those packages that are not included in the sources for
		// Enroll-API for the model
		// so the package is null but the name is the full package spec
		if (xmlType.getJavaPackage() == null
				|| xmlType.getJavaPackage().isEmpty()) {
			return xmlType.getName();
		}
		return xmlType.getJavaPackage() + "." + initUpper(xmlType.getName());
	}

	private void writeGeneratedObjectStructure(XmlWriter out) {
		// Step 1, create the abstract structure
		out.println("");
		out.indentPrintln("<!-- " + className + "-->");
		out.indentPrintln("<bean id=\"" + initUpper(className)
				+ "-generated\" abstract=\"true\" parent=\"DataObjectEntry\">");
		out.incrementIndent();
		writeProperty("name", initLower(className), out);
		writeProperty("dataObjectClass", calcDataObjectClass(xmlType), out);
		writeProperty("objectLabel", calcObjectLabel(), out);
		writePropertyValue("objectDescription", xmlType.getDesc(), out);
		String titleAttribute = calcTitleAttribute();
		if (titleAttribute != null) {
			writeProperty("titleAttribute", titleAttribute, out);
		}
		out.indentPrintln("<property name=\"primaryKeys\">");
		List<String> pks = calcPrimaryKeys();
		if (pks != null && !pks.isEmpty()) {
			out.incrementIndent();
			out.indentPrintln("<list>");
			out.incrementIndent();
			for (String pk : pks) {
				addValue(pk);
			}
			out.decrementIndent();
			out.indentPrintln("</list>");
			out.decrementIndent();
		}
		out.indentPrintln("</property>");

		this.writeAllGeneratedAttributeRefBeans(className, null,
				new Stack<String>(), this.messageStructures, out);

		out.indentPrintln("</bean>");

		// Step 2, loop through attributes
		this.writeGeneratedAttributeDefinitions(className, null,
				new Stack<String>(), this.messageStructures, out);
	}

	private void writeAllGeneratedAttributeRefBeans(String currentClassName,
			String parentName, Stack<String> parents,
			List<MessageStructure> fields, XmlWriter out) {
		if (parents.contains(currentClassName)) {
			return;
		}
		out.println("");
		out.indentPrintln("<property name=\"attributes\">");
		out.incrementIndent();
		out.indentPrintln("<list>");
		out.incrementIndent();
		this.writeGeneratedAttributeRefBeans(currentClassName, parentName,
				parents, fields, out, Category.PRIMITIVE);
		out.decrementIndent();
		out.indentPrintln("</list>");
		out.decrementIndent();
		out.indentPrintln("</property>");

		out.println("");
		out.indentPrintln("<property name=\"complexAttributes\">");
		out.incrementIndent();
		out.indentPrintln("<list>");
		out.incrementIndent();
		this.writeGeneratedAttributeRefBeans(currentClassName, parentName,
				parents, fields, out, Category.COMPLEX);
		out.decrementIndent();
		out.indentPrintln("</list>");
		out.decrementIndent();
		out.indentPrintln("</property>");

		out.println("");
		out.indentPrintln("<property name=\"collections\">");
		out.incrementIndent();
		out.indentPrintln("<list>");
		out.incrementIndent();
		this.writeGeneratedAttributeRefBeans(currentClassName, parentName,
				parents, fields, out, Category.LIST_OF_COMPLEX);
		out.decrementIndent();
		out.indentPrintln("</list>");
		out.decrementIndent();
		out.indentPrintln("</property>");
		out.decrementIndent();
	}

	private void addValue(String value) {
		gwriter.indentPrintln("<value>" + value + "</value>");
	}

	private String calcObjectLabel() {
		String label = this.className;
		if (label.endsWith("Info")) {
			label = label.substring(0, label.length() - "Info".length());
		}
		label = initUpper(label);
		return splitCamelCase(label);
	}

	// got this from
	// http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
	private static String splitCamelCase(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}

	private enum Category {

		PRIMITIVE, COMPLEX, LIST_OF_COMPLEX, LIST_OF_PRIMITIVE, DYNAMIC_ATTRIBUTE
	};

	private Category calculateCategory(MessageStructure ms) {
		if (ms.getShortName().equals("attributes")) {
			return Category.DYNAMIC_ATTRIBUTE;
		}
		
		String childXmlTypeName = this.stripListOffEnd(ms.getType());
		XmlType childXmlType = this.finder.findXmlType(childXmlTypeName);
		if (childXmlType == null) {
			throw new IllegalStateException(childXmlTypeName);
		}
		if (ms.getType().endsWith("List")) {
			if (childXmlType.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
				return Category.LIST_OF_COMPLEX;
			}
			return Category.LIST_OF_PRIMITIVE;
		}
		if (childXmlType.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
			return Category.COMPLEX;
		}
		return Category.PRIMITIVE;
	}

	private void writeGeneratedAttributeRefBeans(String currentClass,
			String parentName, Stack<String> parents,
			List<MessageStructure> fields, XmlWriter out, Category filter) {
		if (parents.contains(currentClass)) {
			return;
		}
		for (MessageStructure ms : fields) {
			Category category = this.calculateCategory(ms);
			if (!category.equals(filter)) {
				continue;
			}
			String childXmlTypeName = this.stripListOffEnd(ms.getType());
			XmlType childXmlType = this.finder.findXmlType(childXmlTypeName);
			if (childXmlType == null) {
				throw new IllegalStateException(childXmlTypeName);
			}
			String pathName = calcPathName(parentName, ms);
			String beanName = calcBeanName(pathName);
			// TODO: change this once they fix the list of complex jira
			// if (filter.equals(Category.LIST_OF_COMPLEX)) {
			// beanName = initUpper(childXmlTypeName);
			// }
			out.indentPrintln("<ref bean=\"" + beanName + "\"/>");
			//
			// // Add complex sub-types fields
			// switch (category) {
			// case COMPLEX:
			// case LIST_OF_COMPLEX:
			// parents.push(currentClass);
			// List<MessageStructure> childFields =
			// this.finder.findMessageStructures(childXmlTypeName);
			// writeGeneratedAttributeRefBeans(childXmlTypeName, pathName,
			// parents, childFields, out, filter);
			// parents.pop();
			// }
		}
	}

	private void writeGeneratedAttributeDefinitions(String currentClassName,
			String parentName, Stack<String> parents,
			List<MessageStructure> fields, XmlWriter out) {
		if (parents.contains(currentClassName)) {
			return;
		}
		for (MessageStructure ms : fields) {
			Category category = this.calculateCategory(ms);
			switch (category) {
			case DYNAMIC_ATTRIBUTE:
				// intentionally fall through
				ENUMERATED_TYPE:
				continue; // skip

			default:
				break;
			}
			String pathName = calcPathName(parentName, ms);
			String beanName = calcBeanName(pathName);
			String childXmlTypeName = this.stripListOffEnd(ms.getType());
			XmlType childXmlType = this.finder.findXmlType(childXmlTypeName);
			if (childXmlType == null) {
				throw new IllegalStateException(childXmlTypeName);
			}
			writeGeneratedAttributeDefinition(currentClassName, parentName,
					parents, ms, out);

			// Add complex sub-types fields
			switch (category) {
			case COMPLEX:
				// case LIST_OF_COMPLEX:
				parents.push(currentClassName);
				List<MessageStructure> childFields = this.finder
						.findMessageStructures(childXmlTypeName);
				writeGeneratedAttributeDefinitions(childXmlTypeName, pathName,
						parents, childFields, out);
				parents.pop();

				break;

			default:
				// all other cases fall through
				break;
			}
		}
	}

	private boolean shouldWriteDetails(MessageStructure ms) {
		if (predefinedFieldMap.get(ms.getShortName().toLowerCase()) == null) {
			return true;
		}
		if (ms.isOverriden()) {
			return true;
		}
		// don't write out details for predefined fields that have not been
		// overridden
		return false;
	}

	private void writeGeneratedAttributeDefinition(String currentClassName,
			String parentName, Stack<String> parents, MessageStructure ms,
			XmlWriter out) {

		// Create the abstract field
		String pathName = calcPathName(parentName, ms);
		String beanName = calcBeanName(pathName);
		String baseKualiParentBean = this.calcBaseKualiParentBean(ms);
		out.println("");
		out.indentPrintln("<bean id=\"" + beanName
				+ "-generated\" abstract=\"true\" parent=\""
				+ baseKualiParentBean + "\">");
		out.incrementIndent();
		writeProperty("name", calcSimpleName(ms), out);
		switch (this.calculateCategory(ms)) {
		case PRIMITIVE:
			if (this.shouldWriteDetails(ms)) {
				writeProperty("shortLabel", calcShortLabel(ms), out);
				writePropertyValue("summary", calcSummary(ms), out);
				writeProperty("label", calcLabel(ms), out);
				writePropertyValue("description", calcDescription(ms), out);
				if (this.calcReadOnly(ms)) {
					this.writeReadOnlyAttributeSecurity(out);
				}
				writeProperty("required", calcRequired(ms), out);
			}
			break;
		case LIST_OF_PRIMITIVE:
			// TODO: deal with once https://jira.kuali.org/browse/KULRICE-5439
			// is fixed
			// for now treat the same as List of Complex, i.e.
			// CollectionDefinition
			writeProperty("shortLabel", calcShortLabel(ms), out);
			writePropertyValue("summary", calcSummary(ms), out);
			writeProperty("label", calcLabel(ms), out);
			writeProperty("elementLabel", calcElementLabel(ms), out);
			writePropertyValue("description", calcDescription(ms), out);
			writeProperty("minOccurs", calcMinOccurs(ms), out);
			writeProperty("dataObjectClass", calcDataObjectClass(ms), out);
			break;
		case LIST_OF_COMPLEX:
			writeProperty("shortLabel", calcShortLabel(ms), out);
			writePropertyValue("summary", calcSummary(ms), out);
			writeProperty("label", calcLabel(ms), out);
			writeProperty("elementLabel", calcElementLabel(ms), out);
			writePropertyValue("description", calcDescription(ms), out);
			writeProperty("minOccurs", calcMinOccurs(ms), out);
			writeProperty("dataObjectClass", calcDataObjectClass(ms), out);
			break;
		case COMPLEX:
			writeProperty("shortLabel", calcShortLabel(ms), out);
			writePropertyValue("summary", calcSummary(ms), out);
			writeProperty("label", calcLabel(ms), out);
			writePropertyValue("description", calcDescription(ms), out);
			writeProperty("required", calcRequired(ms), out);
			writePropertyStart("dataObjectEntry", out);
			out.indentPrintln("<bean parent=\"DataObjectEntry\">");
			out.incrementIndent();
			writeProperty("name", calcSimpleName(ms), out);
			writeProperty("dataObjectClass", calcDataObjectClass(ms), out);
			writeProperty("objectLabel", calcLabel(ms), out);
			writePropertyValue("objectDescription", calcDescription(ms), out);

			String childXmlTypeName = this.stripListOffEnd(ms.getType());
			List<MessageStructure> childFields = this.finder
					.findMessageStructures(childXmlTypeName);
			writeAllGeneratedAttributeRefBeans(childXmlTypeName, pathName,
					parents, childFields, out);
			out.indentPrintln("</bean>");
			writePropertyEnd(out);
			break;
		default:
			throw new IllegalStateException("unknown/unhandled type "
					+ ms.getId());
		}
		out.decrementIndent();
		// TODO: implement maxoccurs
		// if (isList(pd)) {
		// addProperty("maxOccurs", "" + DictionaryConstants.UNBOUNDED, s);
		// }
		out.indentPrintln("</bean>");
	}

	private String calcDataObjectClass(MessageStructure ms) {
		XmlType msType = this.finder.findXmlType(this.stripListOffEnd(ms
				.getType()));
		return this.calcDataObjectClass(msType);
	}

	private String calcBeanName(String pathName) {
		return initUpper(className) + "." + pathName;
	}

	private String calcPathName(String parentName, MessageStructure ms) {
		String name = this.calcSimpleName(ms);
		if (parentName == null) {
			return name;
		}
		return parentName + "." + name;
	}

	private String calcSimpleName(MessageStructure ms) {
		String name = initLower(ms.getShortName());
		return name;
	}

	private boolean calcReadOnly(MessageStructure ms) {
		if (ms.getReadOnly() == null) {
			return false;
		}
		return true;
	}

	private void writeReadOnlyAttributeSecurity(XmlWriter out) {
		out.indentPrintln("<!-- commented out until KRAD bug gets fixed that requires mask to also be entered");
		out.indentPrintln("<property name=\"attributeSecurity\">");
		out.indentPrintln("<ref bean=\"BaseKuali.readOnlyAttributeSecurity\"/>");
		out.indentPrintln("</property>");
		out.indentPrintln("-->");
	}

	private String calcElementLabel(MessageStructure ms) {
		String label = this.calcShortLabel(ms);
		if (label.endsWith("s")) {
			label = label.substring(0, label.length() - 1);
		}
		return label;
	}

	private String calcShortLabel(MessageStructure ms) {
		return this.splitCamelCase(initUpper(ms.getShortName()));
	}

	private String calcLabel(MessageStructure ms) {
		return ms.getName();
	}

	private String calcSummary(MessageStructure ms) {
		BreakIterator bi = BreakIterator.getSentenceInstance();
		String description = ms.getDescription();
		if (description == null) {
			return "???";
		}
		bi.setText(ms.getDescription());
		// one big sentence
		if (bi.next() == BreakIterator.DONE) {
			return ms.getDescription();
		}
		String firstSentence = description.substring(0, bi.current());
		return firstSentence;
	}

	private String calcDescription(MessageStructure ms) {
		return ms.getDescription();
	}

	private String calcMinOccurs(MessageStructure ms) {
		String required = this.calcRequired(ms);
		if ("false".equals(required)) {
			return "0";
		}
		return "1";
	}

	private String calcRequired(MessageStructure ms) {
		if (ms.getRequired() == null) {
			return "false";
		}
		if (ms.getRequired().equalsIgnoreCase("Required")) {
			return "true";
		}
		// TODO: figure out what to do if it is qualified like
		// "required on update"
		return "false";
	}

	private void writeManualObjectStructure(XmlWriter out) {
		// Step 1, create the parent bean
		out.println("");
		out.indentPrintln("<!-- " + className + "-->");
		// Create the actual instance of the bean
		out.indentPrintln("<bean id=\"" + initUpper(className) + "\" parent=\""
				+ initUpper(className) + "-parent\"/>");
		out.indentPrintln("<bean id=\"" + initUpper(className)
				+ "-parent\" abstract=\"true\" parent=\""
				+ initUpper(className) + "-generated\">");
		out.writeComment("insert any overrides to the generated object definitions here");
		out.indentPrintln("</bean>");

		// Step 2, loop through attributes
		this.writeManualAttributeDefinitions(className, null,
				new Stack<String>(), this.messageStructures, out);

	}

	private void writeManualAttributeDefinitions(String currentClass,
			String parentName, Stack<String> parents,
			List<MessageStructure> fields, XmlWriter out) {
		if (parents.contains(currentClass)) {
			return;
		}
		for (MessageStructure ms : fields) {
			Category cat = this.calculateCategory(ms);
			// skip dynamic attributes
			switch (cat) {
			case DYNAMIC_ATTRIBUTE:
				continue; // skip

			default:
				break;
			}

			String pathName = calcPathName(parentName, ms);
			String beanName = calcBeanName(pathName);
			String childXmlTypeName = this.stripListOffEnd(ms.getType());
			XmlType childXmlType = this.finder.findXmlType(childXmlTypeName);
			if (childXmlType == null) {
				throw new IllegalStateException(childXmlTypeName);
			}
			writeManualAttributeDefinition(currentClass, parentName, ms, out);

			// Add complex sub-types fields
			switch (cat) {
			case COMPLEX:
				parents.push(currentClass);
				List<MessageStructure> childFields = this.finder
						.findMessageStructures(childXmlTypeName);
				// if (childFields.isEmpty()) {
				// throw new IllegalStateException(childXmlTypeName);
				// }
				writeManualAttributeDefinitions(childXmlTypeName, pathName,
						parents, childFields, out);
				parents.pop();

				break;

			default:
				break;
			}
		}
	}

	private void writeManualAttributeDefinition(String currentClass,
			String parentName, MessageStructure ms, XmlWriter out) {

		// Create the abstract field
		String pathName = calcPathName(parentName, ms);
		String beanName = calcBeanName(pathName);
		// String baseKualiType = this.calcBaseKualiType(ms);
		// Create the actual bean instance
		out.println("");
		out.indentPrintln("<bean id=\"" + beanName + "\" parent=\"" + beanName
				+ "-parent\"/>");
		out.indentPrintln("<bean id=\"" + beanName
				+ "-parent\" abstract=\"true\" parent=\"" + beanName
				+ "-generated\">");
		out.writeComment("insert any overrides to the generated attribute definitions here");
		out.indentPrintln("</bean>");
	}

	/**
	 * list of predefined fields that should map to entries in
	 * ks-base-dictionary.xml
	 */
	private static Map<String, String> predefinedFieldMap = null;

	/**
	 * list of fields that if they end with the key the should be based on the
	 * entry in ks-base-dictionary.xml
	 */
	private static Map<String, String> endsWithMap = null;

	
	/**
	 * list of types that if the type matches this key then it should be based
	 * on that type entry as defined in the ks-base-dictionary.xml
	 */
	private static Map<String, String> typeMap = null;

	

	private String calcBaseKualiParentBean(MessageStructure ms) {
		switch (this.calculateCategory(ms)) {
		case COMPLEX:
			return "ComplexAttributeDefinition";
		case LIST_OF_COMPLEX:
			return "CollectionDefinition";
		case LIST_OF_PRIMITIVE:
			// TODO: deal with once https://jira.kuali.org/browse/KULRICE-5439
			// is fixed
			System.out
					.println("Treating list of primitives same as collection defintion: "
							+ ms.getId());
			return "CollectionDefinition";
		case PRIMITIVE:
			break;
		default:
			throw new IllegalStateException("unknown/uhandled category "
					+ ms.getId());
		}
		String name = ms.getShortName();
		String baseKualiType = predefinedFieldMap.get(name.toLowerCase());
		if (baseKualiType != null) {
			return baseKualiType;
		}

		// check ends with
		for (String key : endsWithMap.keySet()) {
			if (name.toLowerCase().endsWith(key)) {
				return endsWithMap.get(key);
			}
		}

		// now key off the type
		String type = this.stripListOffEnd(ms.getType());
		baseKualiType = typeMap.get(type.toLowerCase());
		if (baseKualiType != null) {
			return baseKualiType;
		}
		throw new IllegalStateException(
				"All primitives are supposed to be handled by a predefined type "
						+ ms.getId());
	}

	private String calcTitleAttribute() {
		MessageStructure ms = null;
		ms = this.findMessageStructure("name");
		if (ms != null) {
			return initLower(ms.getShortName());
		}
		ms = this.findMessageStructure("title");
		if (ms != null) {
			return initLower(ms.getShortName());
		}
		ms = this.findMessageStructureEndsWith("title");
		if (ms != null) {
			return initLower(ms.getShortName());
		}
		ms = this.findMessageStructureEndsWith("name");
		if (ms != null) {
			return initLower(ms.getShortName());
		}
		ms = this.findMessageStructure("key");
		if (ms != null) {
			return initLower(ms.getShortName());
		}
		ms = this.findMessageStructure("id");
		if (ms != null) {
			return initLower(ms.getShortName());
		}
		
		log.warn("XmlKradBaseDictionaryCreator: could not find a title attribute for "
						+ this.className);
		return null;
	}

	private MessageStructure findMessageStructureEndsWith(
			String shortNameEndsWith) {
		shortNameEndsWith = shortNameEndsWith.toLowerCase();
		for (MessageStructure ms : this.messageStructures) {
			if (ms.getShortName().toLowerCase().endsWith(shortNameEndsWith)) {
				return ms;
			}
		}
		return null;
	}

	private MessageStructure findMessageStructure(String shortName) {
		for (MessageStructure ms : this.messageStructures) {
			if (ms.getShortName().equalsIgnoreCase(shortName)) {
				return ms;
			}
		}
		return null;
	}

	private MessageStructure getMessageStructure(String shortName) {
		MessageStructure ms = this.findMessageStructure(shortName);
		if (ms == null) {
			throw new IllegalArgumentException(shortName);
		}
		return ms;
	}

	private List<String> calcPrimaryKeys() {
		MessageStructure ms = null;
		ms = this.findMessageStructure("id");
		if (ms != null) {
			return Collections.singletonList(initLower(ms.getShortName()));
		}
		ms = this.findMessageStructure("key");
		if (ms != null) {
			return Collections.singletonList(initLower(ms.getShortName()));
		}
		// just use the first field
		if (this.messageStructures.size() > 0) {
			ms = this.messageStructures.get(0);
			return Collections.singletonList(ms.getShortName());
		}
		return Collections.EMPTY_LIST;
	}

	private void writePropertyStart(String propertyName, XmlWriter out) {
		out.indentPrintln("<property name=\"" + propertyName + "\">");
		out.incrementIndent();
	}

	private void writePropertyEnd(XmlWriter out) {
		out.decrementIndent();
		out.indentPrintln("</property>");
	}

	private void writeProperty(String propertyName, String propertyValue,
			XmlWriter out) {
		out.indentPrintln("<property name=\"" + propertyName + "\" value=\""
				+ replaceDoubleQuotes(propertyValue) + "\"/>");
	}

	private void writePropertyValue(String propertyName, String propertyValue,
			XmlWriter out) {
		writePropertyStart(propertyName, out);
		out.indentPrintln("<value>");
		// TODO: worry about the value starting on a new line i.e. is it trimmed
		// when loaded?
		out.println(escapeHtml(propertyValue));
		out.indentPrintln("</value>");
		writePropertyEnd(out);
	}

	private String escapeHtml(String str) {
		if (str == null) {
			return null;
		}
		return StringEscapeUtils.escapeHtml(str);
	}

	private String replaceDoubleQuotes(String str) {
		if (str == null) {
			return null;
		}
		return str.replace("\"", "'");
	}

	/**
	 * Delete the files associated with this dictionary.
	 * 
	 * The use case is where an error is detected and we want to remove the file
	 * (would be size 0) from the disk.
	 * 
	 */
	public void delete() {

		close();

	}

	public void close() {
		if (initialized) {
			this.gwriter.getOut().close();
			this.mwriter.getOut().close();

			if (!new File(manualFilePath).delete())
				log.warn("failed to delete manual path: " + manualFilePath);

			if (!new File(generatedFilePath).delete())
				log.warn("failed to delete generated path: "
						+ generatedFilePath);

			initialized = false;
		}
		
	}
}
