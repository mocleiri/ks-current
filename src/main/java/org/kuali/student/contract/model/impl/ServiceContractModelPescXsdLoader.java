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
package org.kuali.student.contract.model.impl;

import com.sun.xml.xsom.*;
import com.sun.xml.xsom.parser.AnnotationContext;
import com.sun.xml.xsom.parser.AnnotationParser;
import com.sun.xml.xsom.parser.AnnotationParserFactory;
import com.sun.xml.xsom.parser.SchemaDocument;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodReturnValue;
import org.kuali.student.contract.model.XmlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author nwright
 */
public class ServiceContractModelPescXsdLoader implements
        ServiceContractModel {

    private static Logger log = LoggerFactory.getLogger(ServiceContractModelPescXsdLoader.class);
    
    private List<String> xsdFileNames;
    private List<Service> services = null;
    private List<ServiceMethod> serviceMethods = null;
    private Map<String, XmlType> xmlTypeMap = null;
    private List<MessageStructure> messageStructures;

    public ServiceContractModelPescXsdLoader(List<String> xsdFileNames) {
        this.xsdFileNames = xsdFileNames;
    }

    @Override
    public List<ServiceMethod> getServiceMethods() {
        if (this.serviceMethods == null) {
            this.parse();
        }
        return this.serviceMethods;
    }

    @Override
    public List<String> getSourceNames() {
        List<String> list = new ArrayList();
        list.addAll(this.xsdFileNames);
        return list;
    }

    @Override
    public List<Service> getServices() {
        if (services == null) {
            this.parse();
        }
        return services;
    }

    @Override
    public List<XmlType> getXmlTypes() {
        if (xmlTypeMap == null) {
            this.parse();
        }
        return new ArrayList(xmlTypeMap.values());
    }

    @Override
    public List<MessageStructure> getMessageStructures() {
        if (messageStructures == null) {
            this.parse();
        }
        return this.messageStructures;
    }

    private void parse() {
        log.info("ServiceContractModelQDoxLoader: Starting parse");
        services = new ArrayList();
        Service service = new Service();
        services.add(service);
        service.setKey("Pesc");
        service.setName("PescService");
        service.setComments("Derived from pesc CoreMain");
        serviceMethods = new ArrayList();
        ServiceMethod method = new ServiceMethod();
        serviceMethods.add(method);
        method.setName("dummy");
        method.setDescription("Dummy method so validation won't fail");
        method.setService("Pesc");
        method.setParameters(new ArrayList());
        ServiceMethodReturnValue rv = new ServiceMethodReturnValue();
        rv.setType("void");
        rv.setDescription("returns nothing");
        method.setReturnValue(rv);
        xmlTypeMap = new LinkedHashMap();
        messageStructures = new ArrayList();

        XSOMParser parser = new XSOMParser(); 
        parser.setAnnotationParser(new DomAnnotationParserFactory());
//        parser.setAnnotationParser(new XsdAnnotationParserFactory());        
        try {
            for (String xsdFileName : this.xsdFileNames) {
//                System.out.println("Parsing " + xsdFileName);
                parser.parse(new File(xsdFileName));
            }
        } catch (SAXException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        XSSchemaSet schemas;
        try {
            schemas = parser.getResult();
        } catch (SAXException ex) {
            throw new IllegalArgumentException(ex);
        }
//        for (XSSchema schema : schemas.getSchemas()) {
//            this.processSchema(schema);
//        }
        Set<SchemaDocument> schemaDocuments = parser.getDocuments();
        for (SchemaDocument doc : schemaDocuments) {
            XSSchema schema = doc.getSchema();
            this.processSchema(schema);
        }
    }

    private void processSchema(XSSchema schema) {
        for (XSElementDecl element : schema.getElementDecls().values()) {
            this.addElementDecl(element);
        }
        for (XSSimpleType st : schema.getSimpleTypes().values()) {
//            System.out.println("SimpleType =" + st.getName() + " namespace=" + st.getTargetNamespace());
            addSimpleType(st);
        }
        for (XSComplexType ct : schema.getComplexTypes().values()) {
            if (!shouldInclude(ct)) {
//                System.out.println("Skipping ComplexType =" + ct.getName() + " namespace=" + ct.getTargetNamespace());
                continue;
            }
//            System.out.println("ComplexType =" + ct.getName() + " namespace=" + ct.getTargetNamespace());
            addComplexType(ct);
        }
    }

    private boolean shouldInclude(XSComplexType ct) {
//        if (ct.getTargetNamespace().equals("urn:org:pesc:core:CoreMain:v1.8.0")) {
        return true;
//        }
//        return false;
    }

    private void addSimpleType(XSSimpleType simpleType) {
        XmlType xmlType = xmlTypeMap.get(simpleType.getName());
        if (xmlType != null) {
//            System.out.println("Already processed simple Type="
//                    + simpleType.getName());
            return;
        }
        xmlType = new XmlType();
        xmlTypeMap.put(simpleType.getName(), xmlType);
        xmlType.setName(simpleType.getName());
        xmlType.setDesc(calcMissing(calcDesc(simpleType.getAnnotation())));
        xmlType.setComments("???");
        xmlType.setExamples("???");
        xmlType.setService("Pesc");
        xmlType.setPrimitive("Primitive");
    }

    private void addComplexType(XSComplexType complexType) {
        addComplexType(complexType, complexType.getName());
    }

    private void addElementDecl(XSElementDecl element) {
        String name = element.getName();
        XmlType xmlType = xmlTypeMap.get(name);
        if (xmlType != null) {
//            System.out.println("Already processed element name=" + name);
            return;
        }
//        System.out.println("processing element=" + name);
        xmlType = new XmlType();
        xmlTypeMap.put(name, xmlType);
        xmlType.setName(name);
        xmlType.setDesc(calcMissing(calcDesc(element.getAnnotation())));
        xmlType.setComments("???");
        xmlType.setExamples("???");
        xmlType.setService("Pesc");
        xmlType.setPrimitive(XmlType.COMPLEX);
        addMessageStructure(xmlType.getName(), element);
    }

    private void addComplexType(XSComplexType complexType, String name) {
        XmlType xmlType = xmlTypeMap.get(name);
        if (xmlType != null) {
//            System.out.println("Already processed complex Type=" + name);
            return;
        }
//        System.out.println("processing complex type=" + name);
        xmlType = new XmlType();
        xmlTypeMap.put(name, xmlType);
        xmlType.setName(name);
        xmlType.setDesc(calcMissing(calcDesc(complexType.getAnnotation())));
        xmlType.setComments("???");
        xmlType.setExamples("???");
        xmlType.setService("Pesc");
        xmlType.setPrimitive(XmlType.COMPLEX);

        boolean found = false;
        XSContentType contentType = complexType.getContentType();
        XSParticle particle = contentType.asParticle();
        if (particle != null) {
            XSTerm term = particle.getTerm();
            if (term.isModelGroup()) {
                XSModelGroup xsModelGroup = term.asModelGroup();
                XSParticle[] particles = xsModelGroup.getChildren();
                for (XSParticle p : particles) {
                    XSTerm pterm = p.getTerm();
                    if (pterm.isElementDecl()) { //xs:element inside complex type
                        XSElementDecl element = pterm.asElementDecl();
                        addMessageStructure(xmlType.getName(), element);
                        found = true;
                    }
                }
            }
        }
//        if (!found) {
//            System.out.println("*** WARNING *** Complex Type, " + xmlType.getName() + ", has no message structure fields");
//        }
    }

    private String calcMissing(String str) {
        if (str == null) {
            return "???";
        }
        if (str.trim().isEmpty()) {
            return "???";
        }
        return str;
    }

    private String calcDesc(XSAnnotation annotation) {
        if (annotation == null) {
            return null;
        }
        if (annotation.getAnnotation() == null) {
            return null;
        }
        return annotation.getAnnotation().toString();
    }

    private void addMessageStructure(String xmlObject, XSElementDecl element) {
        MessageStructure ms = new MessageStructure();
        this.messageStructures.add(ms);
        ms.setXmlObject(xmlObject);
        ms.setShortName(element.getName());
        ms.setName("???");
        ms.setId(xmlObject + "." + ms.getShortName());
        ms.setType(calcType(element, xmlObject + "" + ms.getShortName()));
        ms.setDescription(calcMissing(calcDesc(element.getAnnotation())));
//        System.out.println("Element " + ms.getId() + " " + ms.getType());
        ms.setRequired(calcRequired(element));
        ms.setCardinality(calcCardinality(element));
    }

    private String calcType(XSElementDecl element, String inLinePrefix) {
        String type = calcType(element.getType(), inLinePrefix);
        if (type != null) {
            return type;
        }
        return type;
    }

    private String calcType(XSType xsType, String inLinePrefix) {
        if (xsType.isSimpleType()) {
            XSSimpleType st = xsType.asSimpleType();
            return st.getBaseType().getName();
//   if (st.isRestriction ())
//   {
//    XSRestrictionSimpleType res = st.asRestriction ();
//    return res.getBaseType ().getName ();
//   }
        }
        String type = xsType.getName();
        if (type != null) {
            return type;
        }
        if ((xsType.isComplexType())) {
            XSComplexType ct = xsType.asComplexType();
            String baseType = ct.getBaseType().getName();
            if (baseType.equals("anyType")) {
                baseType = "";
            }
            String inlineTypeName = inLinePrefix + baseType;
            addComplexType(ct, inlineTypeName);
            return inlineTypeName;
        }
        throw new IllegalArgumentException("cannot calculate the type of the field " + inLinePrefix);
    }

    private String calcRequired(XSElementDecl element) {
        // TODO: isNillable seems to ALWAYS be true so... figure out another way        
//        if (element.isNillable()) {
//            return null;
//        }
//        return "Required";
        // TODO: facets only hold min/maxLength not min/maxOccurs find out where min/maxOccurs is parsed into        
//        String minOccurs = this.getFacetValue(element, "minOccurs");
//        if (minOccurs == null) {
//            return "No";
//        }
//        if (minOccurs.equals("0")) {
//            return "No";
//        }
//        System.out.println(element.getName() + " has a minOccurs that is " + minOccurs);
//        return "Required";
        return "???";
    }

    private String calcCardinality(XSElementDecl element) {
//        if (element.getName().equals ("NoteMessage")) {
//            System.out.println ("start debugging because NoteMessage has maxOccurs=unbounded");
//        }
        if (this.getIsRepeated(element)) {
            return "Many";
        }
        // TODO: facets only hold min/maxLength not min/maxOccurs find out where min/maxOccurs is parsed into
//        String maxOccurs = this.getFacetValue(element, "maxOccurs");
//        if (maxOccurs == null) {
//            return "One";
//        }
//        if (maxOccurs.equals("unbounded")) {
//            return "Many";
//        }
//        System.out.println(element.getName() + " has a maxOccurs that is " + maxOccurs);
//        return "Many";
        return null;
    }
    
    private boolean getIsRepeated (XSElementDecl element) {
        XSType type = element.getType();
        if (type == null) {
            return false;
        }
        if (type.isComplexType()) {
            XSContentType contentType = type.asComplexType().getContentType();
            if (contentType == null) {
                return false;
            }
            XSParticle particle = contentType.asParticle();
            if (particle == null) {
                return false;
            }
            particle.isRepeated();
        }
//        if (type.isSimpleType())
        return false;
    }

    private String getFacetValue(XSElementDecl element, String name) {
        XSType type = element.getType();
        if (type == null) {
            return null;
        }
        if (type.isSimpleType()) {
            XSFacet facet = type.asSimpleType().getFacet(name);
            if (facet == null) {
                return null;
            }
            return facet.getValue().toString();
        }
        if (type.isComplexType()) {
            XSContentType contentType = type.asComplexType().getContentType();
            if (contentType == null) {
                return null;
            }
            XSSimpleType simpleType = contentType.asSimpleType();
            if (simpleType == null) {
                return null;
            }
            XSFacet facet = simpleType.getFacet(name);
            if (facet == null) {
                return null;
            }
            return facet.getValue().toString();
        }
        return null;
    }

    /**
     * helper class
     */
    private static class XsdAnnotationParserFactory implements AnnotationParserFactory {

        @Override
        public AnnotationParser create() {
            return new XsdAnnotationParser();
        }
    }

    /** 
     * helper class
     */
    private static class XsdAnnotationParser extends AnnotationParser {

        private StringBuilder documentation = new StringBuilder();

        @Override
        public ContentHandler getContentHandler(AnnotationContext context,
                String parentElementName,
                ErrorHandler handler,
                EntityResolver resolver) {
            return new XsdContentHandler(documentation);
        }

        @Override
        public Object getResult(Object existing) {
            return documentation.toString().trim();
        }
    }

    /**
     * helper class
     */
    private static class XsdContentHandler implements ContentHandler {

        private StringBuilder documentation;

        public XsdContentHandler(StringBuilder documentation) {
            this.documentation = documentation;
        }
        private boolean parsingDocumentation = false;

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (parsingDocumentation) {
                documentation.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            if (localName.equals("documentation")) {
                parsingDocumentation = false;
            }
        }

        @Override
        public void startElement(String uri, String localName, String name,
                Attributes atts) throws SAXException {
            if (localName.equals("documentation")) {
                parsingDocumentation = true;
            }
        }

        @Override
        public void endDocument() throws SAXException {
            // do nothing
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            // do nothing
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            // do nothing
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            // do nothing
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            // do nothing
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            // do nothing
        }

        @Override
        public void startDocument() throws SAXException {
            // do nothing
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            // do nothing
        }
    }

    @Override
    public String toString() {
        return "ServiceContractModelPescXsdLoader{" +
                "xsdFileNames=" + xsdFileNames +
                ", services=" + services +
                ", serviceMethods=" + serviceMethods +
                ", xmlTypeMap=" + xmlTypeMap +
                ", messageStructures=" + messageStructures +
                '}';
    }
}
