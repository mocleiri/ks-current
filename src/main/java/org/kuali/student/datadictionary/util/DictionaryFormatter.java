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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeDefinitionBase;
import org.kuali.rice.krad.datadictionary.CollectionDefinition;
import org.kuali.rice.krad.datadictionary.ComplexAttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.CommonLookupParam;
import org.kuali.rice.krad.datadictionary.validation.constraint.LookupConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.WhenConstraint;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.kuali.student.contract.model.util.VersionLinesUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictionaryFormatter {

    private static Logger log = LoggerFactory.getLogger(DictionaryFormatter.class);
    
    private DataObjectEntry doe;
    private Map<String, DataObjectEntry> beansOfType;
    private String beanId;
    private String outputFileName;

    public DictionaryFormatter(DataObjectEntry doe, Map<String, DataObjectEntry> beansOfType, String beanId, String outputFileName) {
        this.doe = doe;
        this.beansOfType = beansOfType;
        this.beanId = beanId;
        this.outputFileName = outputFileName;
    }

    public void formatForHtml(String projectVersion, String formattedDate) {
        File file = new File(this.outputFileName);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file, false);
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException(this.outputFileName, ex);
        }
        PrintStream out = new PrintStream(outputStream);
        writeHeader(out, beanId);
        writeBody(out, projectVersion, formattedDate);
        writeFooter(out);
        out.close();
    }

    public static void writeHeader(PrintStream out, String title) {
        out.println("<html>");
        out.println("<head>");
        writeTag(out, "title", title);
        out.println ("<style>li.invalid { background: red; }</style>");
        out.println("</head>");
        out.println("<body bgcolor=\"#ffffff\" topmargin=0 marginheight=0>");
    }

    public static void writeFooter(PrintStream out) {
        out.println("</body>");
        out.println("</html>");
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

    private void writeBody(PrintStream out, String projectVersion, String formattedDate) {
    	
    	VersionLinesUtility.writeVersionTag(out, "<a href=\"index.html\">home</a>", "<a href=\"../contractdocs/" + initUpper(doe.getName()) + ".html\">contract doc</a>", projectVersion, formattedDate);
//  builder.append ("======= start dump of object structure definition ========");
        out.println("<h1>" + this.beanId + "</h1>");

        out.println("<br>");
        out.println("<table border=1>");

        out.println("<tr>");
        out.println("<th bgcolor=lightblue>");
        out.println("Name");
        out.println("</th>");
        out.println("<td>");
        out.println(doe.getName());
        out.println("</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<th bgcolor=lightblue>");
        out.println("Label");
        out.println("</th>");
        out.println("<td>");
        out.println(doe.getObjectLabel());
        out.println("</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<th bgcolor=lightblue>");
        out.println("JSTL Key");
        out.println("</th>");
        out.println("<td>");
        out.println(doe.getJstlKey());
        out.println("</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<th bgcolor=lightblue>");
        out.println("Java Class");
        out.println("</th>");
        out.println("<td>");
        out.println(doe.getFullClassName());
        out.println("</td>");
        out.println("</tr>");
        out.println("<tr>");

        if (!doe.getDataObjectClass().getName().equals(doe.getFullClassName())) {
            out.println("<tr>");
            out.println("<th bgcolor=lightblue>");
            out.println("Object Class");
            out.println("</th>");
            out.println("<td>");
            out.println(doe.getDataObjectClass().getName());
            out.println("</td>");
            out.println("</tr>");
            out.println("<tr>");
        }

        if (!doe.getEntryClass().getName().equals(doe.getFullClassName())) {
            out.println("<tr>");
            out.println("<th bgcolor=lightblue>");
            out.println("Entry Class");
            out.println("</th>");
            out.println("<td>");
            out.println(doe.getEntryClass().getName());
            out.println("</td>");
            out.println("</tr>");
            out.println("<tr>");
        }

        out.println("<tr>");
        out.println("<th bgcolor=lightblue>");
        out.println("Description");
        out.println("</th>");
        out.println("<td>");
        out.println(doe.getObjectDescription());
        out.println("</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<th bgcolor=lightblue>");
        out.println("Primary Key(s)");
        out.println("</th>");
        out.println("<td>");
        StringBuilder bldr = new StringBuilder();
        String comma = "";
        if (doe.getPrimaryKeys() != null) {
            for (String pk : doe.getPrimaryKeys()) {
                bldr.append(comma);
                comma = ", ";
                bldr.append(pk);
            }
        }
        out.println(bldr.toString());
        out.println("</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<th bgcolor=lightblue>");
        out.println("Field to use as the title (or name)");
        out.println("</th>");
        out.println("<td>");
        out.println(doe.getTitleAttribute());
        out.println("</td>");
        out.println("</tr>");

        out.println("</table>");
//        out.println("<br>");

        // fields
        out.println("<h1>Field Definitions</h1>");
        // check for discrepancies first
        List<String> discrepancies = new Dictionary2BeanComparer(doe.getFullClassName(), doe).compare();
        if (discrepancies.isEmpty()) {
            out.println("No discrepancies were found between the dictionary definition and the java object -- ");
            out.println("WARNING: take this with a grain of salt - the comparison does not dig into complex sub-objects nor collections so...");
        } else {
            out.println("<b>" + discrepancies.size() + " discrepancie(s) were found between the dictionary definition and the java object" + "</b>");
            out.println("<ol>");
            for (String discrepancy : discrepancies) {
                out.println("<li>" + discrepancy);
            }
            out.println("</ol>");
        }
        // field table
        out.println("<table border=1>");
        out.println("<tr bgcolor=lightblue>");
        out.println("<th>");
        out.println("Field");
        out.println("</th>");
        out.println("<th>");
        out.println("Required?");
        out.println("</th>");
        out.println("<th>");
        out.println("DataType");
        out.println("</th>");
        out.println("<th>");
        out.println("Length");
        out.println("</th>");
        out.println("<th>");
        out.println("Short Label");
        out.println("</th>");
        out.println("<th>");
        out.println("Summary");
        out.println("</th>");
        out.println("<th>");
        out.println("Label");
        out.println("</th>");
        out.println("<th>");
        out.println("Description");
        out.println("</th>");
        out.println("<th>");
        out.println("Read Only, Dynamic, or Hidden");
        out.println("</th>");
        out.println("<th>");
        out.println("Default");
        out.println("</th>");
        out.println("<th>");
        out.println("Repeats?");
        out.println("</th>");
        out.println("<th>");
        out.println("Valid Characters");
        out.println("</th>");
        out.println("<th>");
        out.println("Lookup");
        out.println("</th>");
        out.println("<th>");
        out.println("Cross Field");
        out.println("</th>");
        out.println("<th>");
        out.println("Default Control");
        out.println("</th>");
        out.println("</tr>");
        this.writeAttributes(out, doe, new Stack<String>(), new Stack<DataObjectEntry>());
        out.println("</table>");
        return;
    }

    private void writeAttributes(PrintStream out, DataObjectEntry ode, Stack<String> parentNames, Stack<DataObjectEntry> parents) {
        // stop recursion
        if (parents.contains(ode)) {
            return;
        }
//        for (AttributeDefinition ad : getSortedFields()) {
        if (ode.getAttributes() != null) {
            for (AttributeDefinition ad : ode.getAttributes()) {
                out.println("<tr>");
                out.println("<td>");
                out.println(nbsp(calcName(ad.getName(), parentNames)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcRequired(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcDataType(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcLength(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcShortLabel(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcSummary(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcLabel(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcDescription(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcDynamicHiddenReadOnly(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcDefaultValue(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcForceUpperValidCharsMinMax(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcLookup(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcCrossField(ad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcControl(ad)));
                out.println("</td>");
                out.println("</tr>");
            }
        }
        if (ode.getComplexAttributes() != null) {
            for (ComplexAttributeDefinition cad : ode.getComplexAttributes()) {
                out.println("<tr>");
                out.println("<td>");
                out.println(nbsp(calcName(cad.getName(), parentNames)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcRequired(cad)));
                out.println("</td>");
                out.println("<td>");
                out.println("Complex");
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcShortLabel(cad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcSummary(cad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcLabel(cad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcDescription(cad)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("</tr>");
                parentNames.push(cad.getName());
                parents.push(ode);
                this.writeAttributes(out, (DataObjectEntry) cad.getDataObjectEntry(), parentNames, parents);
                parentNames.pop();
                parents.pop();
            }
        }
        if (ode.getCollections() != null) {
            for (CollectionDefinition cd : ode.getCollections()) {
                out.println("<tr>");
                out.println("<td>");
                out.println(nbsp(calcName(cd.getName(), parentNames)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcRequired(cd)));
                out.println("</td>");
                out.println("<td>");
                out.println("Complex");
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcShortLabel(cd)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcSummary(cd)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcLabel(cd)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(calcDescription(cd)));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp("Repeating"));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("<td>");
                out.println(nbsp(null));
                out.println("</td>");
                out.println("</tr>");
                DataObjectEntry childDoe = this.getDataOjbectEntry(cd.getDataObjectClass());
                if (childDoe == null) {
                    // TODO: uncomment this but right now there are xml files that don't have one defined and it seems to work so...
//                    throw new NullPointerException ("Could not find a data object entry, " + cd.getDataObjectClass() + " for field " + calcName(cd.getName(), parents));
                    log.warn("Could not find a data object entry, " + cd.getDataObjectClass() + " for field " + calcName(cd.getName(), parentNames));
                } else {
                    parentNames.push(cd.getName());
                    parents.push(ode);
                    this.writeAttributes(out, (DataObjectEntry) childDoe, parentNames, parents);
                    parentNames.pop();
                    parents.pop();
                }
            }
        }
    }

    private DataObjectEntry getDataOjbectEntry(String className) {
        for (DataObjectEntry doe : this.beansOfType.values()) {
            if (doe.getDataObjectClass().getName().equals(className)) {
                return doe;
            }
        }
        return null;
    }

    private String calcName(String name, Stack<String> parents) {
        StringBuilder sb = new StringBuilder();
        for (String parent : parents) {
            sb.append(parent);
            sb.append(".");
        }
        sb.append(name);
        return sb.toString();
    }

    private String calcShortLabel(CollectionDefinition cd) {
        return cd.getShortLabel();
    }

    private String calcShortLabel(AttributeDefinitionBase ad) {
        return ad.getShortLabel();
    }

    private String calcLabel(CollectionDefinition cd) {
        return cd.getLabel();
    }

    private String calcLabel(AttributeDefinitionBase ad) {
        return ad.getLabel();
    }

    private String calcSummary(CollectionDefinition ad) {
        return ad.getSummary();
    }

    private String calcSummary(AttributeDefinitionBase ad) {
        return ad.getSummary();
    }

    private String calcDescription(CollectionDefinition cd) {
        return cd.getDescription();
    }

    private String calcDescription(AttributeDefinitionBase ad) {
        return ad.getDescription();
    }

    private List<AttributeDefinition> getSortedFields() {
        List<AttributeDefinition> fields = doe.getAttributes();
        Collections.sort(fields, new AttributeDefinitionNameComparator());
        return fields;
    }

    private static class AttributeDefinitionNameComparator implements Comparator<AttributeDefinition> {

        @Override
        public int compare(AttributeDefinition o1, AttributeDefinition o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

    private String formatAsString(List<String> discrepancies) {
        int i = 0;
        StringBuilder builder = new StringBuilder();
        for (String discrep : discrepancies) {
            i++;
            builder.append(i + ". " + discrep + "\n");
        }
        return builder.toString();
    }

    private String calcDataType(AttributeDefinition ad) {
//        if (ad.getDataType().equals(DataType.COMPLEX)) {
//            if (ad.getDataObjectStructure() == null) {
//                throw new IllegalArgumentException(
//                        ad.getName() + " is complex but does not have a sub-structure defined");
//            }
//            Class subClazz = this.getClass(ad.getDataObjectStructure().getName());
//            String subStrucName = calcComplexSubStructureName(ad);
//            // process if explicity asking for substructures OR the field is a freestanding field
//            // so it won't be processed by just processing all of the DTO's and their sub-objects
//            if (this.processSubstructures || subClazz == null) {
//                if (!this.subStructuresAlreadyProcessed.contains(
//                        ad.getDataObjectStructure())) {
////     System.out.println ("Adding " + subStrucName + " to set to be processed");
//                    this.subStructuresToProcess.put(subStrucName, ad.getDataObjectStructure());
//                }
//            }
//            return "[" + calcNotSoSimpleName(subStrucName) + "|#" + subStrucName + "]";
//        }
        return ad.getDataType().toString();
    }

    private String calcDefaultValue(AttributeDefinition ad) {
//        if (ad.getDefaultValue() != null) {
//            return ad.getDefaultValue().toString();
//        }
        return " ";
    }

    private String calcDynamicHiddenReadOnly(AttributeDefinition ad) {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        comma = this.appendIfNotNull(sb, this.calcDynamic(ad), comma);
        comma = this.appendIfNotNull(sb, this.calcHidden(ad), comma);
        comma = this.appendIfNotNull(sb, this.calcReadOnly(ad), comma);
        return sb.toString();
    }

    private String appendIfNotNull(StringBuilder sb, String value, String comma) {
        if (value == null) {
            return comma;
        }
        sb.append(comma);
        sb.append(value);
        return ", ";
    }

    private String calcDynamic(AttributeDefinition ad) {
        // TODO: implement once KRAD team implements
//        if (ad.isDynamic()) {
//            return "dynamic";
//        }
        return null;
    }

    private String calcHidden(AttributeDefinition ad) {
        if (ad.getAttributeSecurity() == null) {
            return null;
        }
        if (ad.getAttributeSecurity().isHide()) {
            return "Hidden";
        }
        return null;

    }

    private String calcReadOnly(AttributeDefinition ad) {
        if (ad.getAttributeSecurity() == null) {
            return null;
        }
        if (ad.getAttributeSecurity().isReadOnly()) {
            return "Read only";
        }
        return null;

    }

    private String calcComplexSubStructureName(AttributeDefinition ad) {
//        if (this.processSubstructures) {
//            return name + "." + ad.getName() + "." + calcSimpleName(
//                    ad.getDataObjectStructure().getName());
//        }
//        return calcSimpleName(ad.getDataObjectStructure().getName());
        return " ";
    }

    private String calcSimpleName(String simpleName) {
        if (simpleName.lastIndexOf(".") != -1) {
            simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
        }
        return simpleName;
    }

    private String calcNotSoSimpleName(String name) {
        if (name.lastIndexOf(".") == -1) {
            return name;
        }
        String simpleName = calcSimpleName(name);
        String fieldName = calcSimpleName(name.substring(0, name.length()
                - simpleName.length()
                - 1));
        return fieldName + "." + simpleName;
    }

    private String calcRequired(CollectionDefinition cd) {
        if (cd.getMinOccurs() != null) {
            if (cd.getMinOccurs() >= 1) {
                return "required";
            }
        }
        // TODO: Deal with collections
//        if (ad.getMaximumNumberOfElements() != null) {
//            if (ad.getMaximumNumberOfElements().intValue() == 0) {
//                return "Not allowed";
//            }
//        }
//
//        if (ad.getMinimumNumberOfElements() != null) {
//            if (ad.getMinimumNumberOfElements().intValue() >= 1) {
//                return "required";
//            }
//        }
        return " ";
//  return "optional";
    }

    private String calcRequired(AttributeDefinitionBase ad) {
        if (ad.isRequired() != null) {
            if (ad.isRequired()) {
                return "required";
            }
        }
        // TODO: Deal with collections
//        if (ad.getMaximumNumberOfElements() != null) {
//            if (ad.getMaximumNumberOfElements().intValue() == 0) {
//                return "Not allowed";
//            }
//        }
//
//        if (ad.getMinimumNumberOfElements() != null) {
//            if (ad.getMinimumNumberOfElements().intValue() >= 1) {
//                return "required";
//            }
//        }
        return " ";
//  return "optional";
    }

    private String calcForceUpperCase(AttributeDefinition ad) {
        if (ad.getForceUppercase() != null && ad.getForceUppercase()) {
            return "FORCE UPPER CASE";
        }
        return " ";
    }

    private String calcValidChars(AttributeDefinition ad) {
        if (ad.getValidCharactersConstraint() == null) {
            return " ";
        }
        return calcValidChars(ad.getValidCharactersConstraint());
    }

    private String calcValidChars(ValidCharactersConstraint cons) {
        String messageKey = cons.getMessageKey();
        if (messageKey == null) {
            messageKey = "validation.validChars";
        }
        String validChars = escapeXML(cons.getValue());
        String descr = messageKey + "<br>" + validChars;
        return descr;
    }

    private String calcLookup(AttributeDefinition ad) {
        if (ad.getLookupDefinition() == null) {
            return " ";
        }
        return calcLookup(ad.getLookupDefinition());
    }

    private String calcLookup(LookupConstraint lc) {
        StringBuilder bldr = new StringBuilder();
        bldr.append(lc.getId());
//  this is the search description not the lookup description
//  builder.append (" - ");
//  builder.append (lc.getDesc ());
        String and = "";
        bldr.append("<br>");
        bldr.append("\n");
        bldr.append("Implemented using search: ");
        String searchPage = calcWikiSearchPage(lc.getSearchTypeId());
        bldr.append("[" + lc.getSearchTypeId() + "|" + searchPage + "#"
                + lc.getSearchTypeId() + "]");
        List<CommonLookupParam> configuredParameters = filterConfiguredParams(
                lc.getParams());
        if (configuredParameters.size() > 0) {
            bldr.append("<br>");
            bldr.append("\n");
            bldr.append(" where ");
            and = "";
            for (CommonLookupParam param : configuredParameters) {
                bldr.append(and);
                and = " and ";
                bldr.append(param.getName());
                bldr.append("=");
                if (param.getDefaultValueString() != null) {
                    bldr.append(param.getDefaultValueString());
                    continue;
                }
                if (param.getDefaultValueList() != null) {
                    String comma = "";
                    for (String defValue : param.getDefaultValueList()) {
                        bldr.append(comma);
                        comma = ", ";
                        bldr.append(defValue);
                    }
                }
            }
        }
        return bldr.toString();
    }

    private String calcForceUpperValidCharsMinMax(AttributeDefinition ad) {
        StringBuilder bldr = new StringBuilder();
        String brk = "";
        String forceUpper = calcForceUpperCase(ad);
        if (!forceUpper.trim().isEmpty()) {
            bldr.append(forceUpper);
            brk = "<BR>";
        }

        String validChars = calcValidChars(ad);
        if (!validChars.trim().isEmpty()) {
            bldr.append(brk);
            brk = "<BR>";
            bldr.append(validChars);
        }

        String minMax = calcMinMax(ad);
        if (!minMax.trim().isEmpty()) {
            bldr.append(brk);
            brk = "<BR>";
            bldr.append(minMax);
        }

        return bldr.toString();
    }

    private String calcMinMax(AttributeDefinition ad) {
        if (ad.getExclusiveMin() == null) {
            if (ad.getInclusiveMax() == null) {
                return " ";
            }
            return "Must be <= " + ad.getInclusiveMax();
        }
        if (ad.getInclusiveMax() == null) {
            return "Must be > " + ad.getExclusiveMin();
        }
        return "Must be > " + ad.getExclusiveMin() + " and < "
                + ad.getInclusiveMax();
    }
    private static final String PAGE_PREFIX = "Formatted View of ";
    private static final String PAGE_SUFFIX = " Searches";

    private String calcWikiSearchPage(String searchType) {
        return PAGE_PREFIX + calcWikigPageAbbrev(searchType) + PAGE_SUFFIX;
    }

    private String calcWikigPageAbbrev(String searchType) {
        if (searchType == null) {
            return null;
        }
        if (searchType.equals("enumeration.management.search")) {
            return "EM";
        }
        if (searchType.startsWith("lu.")) {
            return "LU";
        }
        if (searchType.startsWith("cluset.")) {
            return "LU";
        }
        if (searchType.startsWith("lo.")) {
            return "LO";
        }
        if (searchType.startsWith("lrc.")) {
            return "LRC";
        }
        if (searchType.startsWith("comment.")) {
            return "Comment";
        }
        if (searchType.startsWith("org.")) {
            return "Organization";
        }
        if (searchType.startsWith("atp.")) {
            return "ATP";
        }
        throw new IllegalArgumentException("Unknown type of search: " + searchType);
    }

    private List<CommonLookupParam> filterConfiguredParams(
            List<CommonLookupParam> params) {
        List list = new ArrayList();
        if (params == null) {
            return list;
        }
        if (params.size() == 0) {
            return list;
        }
        for (CommonLookupParam param : params) {
            if (param.getDefaultValueString() != null) {
                list.add(param);
                continue;
            }
            if (param.getDefaultValueList() != null) {
                list.add(param);
            }
        }
        return list;
    }

    private String calcLength(AttributeDefinition ad) {
        if (ad.getMaxLength() != null) {
            if (ad.getMinLength() != null && ad.getMinLength() != 0) {
                if (ad.getMaxLength() == ad.getMinLength()) {
                    return ("must be " + ad.getMaxLength());
                }
                return ad.getMinLength() + " to " + ad.getMaxLength();
            }
            return "up to " + ad.getMaxLength();
        }
        if (ad.getMinLength() != null) {
            return "at least " + ad.getMinLength();
        }
        return " ";
    }

    private String calcControl(AttributeDefinition ad) {
        Control control = ad.getControlField();
        if (control == null) {
            return " ";
        }
        if (control instanceof TextControl) {
            TextControl textControl = (TextControl) control;
            if (textControl.getDatePicker() != null) {
                return "DateControl";
            }
            if (!textControl.getStyleClassesAsString().isEmpty()) {
                if (textControl.getStyleClassesAsString().contains("amount")) {
                    return "CurrencyControl";
                }
            }
        }
        return control.getClass().getSimpleName();
    }

    private String calcCrossField(AttributeDefinition ad) {
        StringBuilder b = new StringBuilder();
        String semicolon = "";
        String cfr = calcCrossFieldRequire(ad);
        if (cfr != null) {
            b.append(semicolon);
            semicolon = "; ";
            b.append(cfr);
        }
        String cfw = calcCrossFieldWhen(ad);
        if (cfw != null) {
            b.append(semicolon);
            semicolon = "; ";
            b.append(cfw);
        }
        if (b.length() == 0) {
            return " ";
        }
        return b.toString();
    }

    private String calcCrossFieldRequire(AttributeDefinitionBase ad) {
//        if (ad.getRequireConstraint() == null) {
//            return null;
//        }
//        if (ad.getRequireConstraint().size() == 0) {
//            return null;
//        }
        StringBuilder b = new StringBuilder();
//        String comma = "";
//        b.append("if not empty then ");
//        for (RequiredConstraint rc : ad.getRequireConstraint()) {
//            b.append(comma);
//            comma = ", ";
//            b.append(rc.getPropertyName());
//        }
//        if (ad.getRequireConstraint().size() == 1) {
//            b.append(" is");
//        } else {
//            b.append(" are");
//        }
//        b.append(" also required");
        return b.toString();
    }

    private String calcCrossFieldWhen(AttributeDefinition ad) {
        if (ad.getCaseConstraint() == null) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        CaseConstraint cc = ad.getCaseConstraint();
        for (WhenConstraint wc : cc.getWhenConstraint()) {
            b.append("\\\\");
            b.append("\n");
            b.append("when ");
            b.append(cc.getPropertyName());
            b.append(" ");
            if (!cc.isCaseSensitive()) {
                b.append("ignoring case ");
            }
            b.append(cc.getOperator());
            b.append(" ");

            b.append("\\\\");
            b.append("\n");
            String comma = "";
            for (Object value : wc.getValues()) {
                b.append(comma);
                comma = " or ";
                b.append(asString(value));
            }
            b.append("\\\\");
            b.append("\n");
            b.append("then override constraint:"
                    + calcOverride(ad, (BaseConstraint) wc.getConstraint()));
        }
        return b.toString();
    }

    private String calcOverride(AttributeDefinition ad, BaseConstraint cons) {
        StringBuilder b = new StringBuilder();
//        b.append(calcOverride("serviceSide", ad.(),
//                cons.getApplyClientSide()));
//        b.append(calcOverride("exclusiveMin", ad.getExclusiveMin(),
//                cons.getExclusiveMin()));
//        b.append(calcOverride("inclusiveMax", ad.getInclusiveMax(),
//                cons.getInclusiveMax()));
//        String minOccursMessage = calcOverride("minOccurs", ad.getMinimumNumberOfElements(),
//                cons.getMinimumNumberOfElements());
//        if (!minOccursMessage.trim().equals("")) {
//            if (cons.getMinimumNumberOfElements() != null && cons.getMinimumNumberOfElements() == 1) {
//                minOccursMessage = " REQUIRED";
//            }
//        }
//        b.append(minOccursMessage);
//        b.append(calcOverride("validchars", ad.getValidCharactersConstraint(),
//                cons.getValidCharactersConstraint()));
//        b.append(calcOverride("lookup", ad.getLookupDefinition(),
//                cons.getLookupDefinition()));
        //TODO: other more complex constraints
        return b.toString();
    }

    private String calcOverride(String attribute, LookupConstraint val1,
            LookupConstraint val2) {
        if (val1 == val2) {
            return "";
        }
        if (val1 == null && val2 != null) {
            return " add lookup " + this.calcLookup(val2);
        }
        if (val1 != null && val2 == null) {
            return " remove lookup constraint";
        }
        return " change lookup to " + calcLookup(val2);
    }

    private String calcOverride(String attribute, ValidCharactersConstraint val1,
            ValidCharactersConstraint val2) {
        if (val1 == val2) {
            return "";
        }
        if (val1 == null && val2 != null) {
            return " add validchars " + calcValidChars(val2);
        }
        if (val1 != null && val2 == null) {
            return " remove validchars constraint";
        }
        return " change validchars to " + calcValidChars(val2);
    }

    private String calcOverride(String attribute, boolean val1, boolean val2) {
        if (val1 == val2) {
            return "";
        }
        return " " + attribute + "=" + val2;
    }

    private String calcOverride(String attribute, String val1, String val2) {
        if (val1 == null && val2 == null) {
            return "";
        }
        if (val1 == val2) {
            return "";
        }
        if (val1 == null) {
            return " " + attribute + "=" + val2;
        }
        if (val1.equals(val2)) {
            return "";
        }
        return " " + attribute + "=" + val2;
    }

    private String calcOverride(String attribute, Object val1, Object val2) {
        if (val1 == null && val2 == null) {
            return "";
        }
        if (val1 == val2) {
            return "";
        }
        if (val1 == null) {
            return " " + attribute + "=" + val2;
        }
        if (val1.equals(val2)) {
            return "";
        }
        return " " + attribute + "=" + asString(val2);
    }

    private String asString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    private String nbsp(String str) {
        if (str == null) {
            return "&nbsp;";
        }
        if (str.trim().isEmpty()) {
            return "&nbsp;";
        }
        return str;
    }

    public static void writeTag(PrintStream out, String tag, String value) {
        writeTag(out, tag, null, value);
    }

    public static void writeTag(PrintStream out, String tag, String modifiers, String value) {
        if (value == null) {
            return;
        }
        if (value.equals("")) {
            return;
        }
        out.print("<" + tag);
        if (modifiers != null && !modifiers.isEmpty()) {
            out.print(" " + modifiers);
        }
        out.print(">");
        out.print(escapeXML(value));
        out.print("</" + tag + ">");
        out.println("");
    }

    public static String escapeXML(String s) {
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            // http://www.hdfgroup.org/HDF5/XML/xml_escape_chars.htm
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                //case ' ': sb.append("&nbsp;");break;\
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    private void writeLink(PrintStream out, String url, String value) {
        out.print("<a href=\"" + url + "\">" + value + "</a>");
    }
}
