package com.sigmasys.kuali.ksa.service.xliff;

import java.io.StringReader;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.sigmasys.kuali.ksa.util.XmlSchemaValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.codehaus.stax2.XMLInputFactory2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation for the &lt;xliff/&gt; tag for the XLIFF 1.2 OASIS specification.
 * An xliff tag contains the whole translation for one language.
 *
 * @author Michael Ivanov
 */
@Service("xliffParser")
@Transactional(readOnly = true)
public class XliffParser {

    private static final Log logger = LogFactory.getLog(XliffParser.class);

    private static final String XLIFF_SCHEMA_LOCATION = "classpath*:/xsd/xliff-core-1.2-transitional.xsd";
    private static final String XML_SCHEMA_LOCATION = "classpath*:/xsd/xml.xsd";

    private static final String XLIFF_NAMESPACE = "urn:oasis:names:tc:xliff:document:1.2";

    private static final String ID = "id";
    private static final String MAX_BYTES = "maxbytes";
    private static final String FILE = new QName(XLIFF_NAMESPACE, "file").toString();
    private static final String TRANS_UNIT = new QName(XLIFF_NAMESPACE, "trans-unit").toString();
    private static final String SOURCE = new QName(XLIFF_NAMESPACE, "source").toString();
    private static final String TARGET = new QName(XLIFF_NAMESPACE, "target").toString();
    private static final String SOURCE_LANGUAGE = "source-language";
    private static final String TARGET_LANGUAGE = "target-language";

    private final XMLInputFactory2 xmlInputFactory;
    private final XmlSchemaValidator schemaValidator;

    public XliffParser() {
        xmlInputFactory = (XMLInputFactory2) XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        xmlInputFactory.configureForSpeed();
        schemaValidator = new XmlSchemaValidator(XML_SCHEMA_LOCATION, XLIFF_SCHEMA_LOCATION);
    }

    /**
     * Parses the xliff reading its content from the Reader
     */
    public Xliff parse(String xliffContent) {

        if (logger.isDebugEnabled()) {
            logger.debug("Parsing XLIFF:\n" + xliffContent);
        }

        try {

            // Validating the content against the XLIFF schema
            if (!schemaValidator.validateXml(xliffContent)) {
                String errMsg = "XLIFF content is invalid.\n\n" + xliffContent;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            Xliff xliff = new Xliff();
            Map<String, TransUnit> transUnits = new HashMap<String, TransUnit>();
            XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(new StringReader(xliffContent));
            while (streamReader.hasNext()) {
                streamReader.next();
                if (XMLEvent.START_ELEMENT == streamReader.getEventType()) {
                    String currentElement = streamReader.getName().toString();
                    logger.info("Processing '" + currentElement + "'...");
                    if (FILE.equals(currentElement)) {
                        String sourceLanguage = null;
                        String targetLanguage = null;
                        for (int i = 0; i < streamReader.getAttributeCount(); i++) {
                            String attrName = streamReader.getAttributeName(i).toString();
                            if (SOURCE_LANGUAGE.equals(attrName)) {
                                logger.info("Processing '" + SOURCE_LANGUAGE + "'...");
                                sourceLanguage = streamReader.getAttributeValue(i);
                            } else if (TARGET_LANGUAGE.equals(attrName)) {
                                logger.info("Processing '" + TARGET_LANGUAGE + "'...");
                                targetLanguage = streamReader.getAttributeValue(i);
                            }
                        }
                        setLanguage(xliff, SOURCE_LANGUAGE, sourceLanguage);
                        setLanguage(xliff, TARGET_LANGUAGE, targetLanguage);
                    } else if (TRANS_UNIT.equals(currentElement)) {
                        TransUnit transUnit = getTransUnit(streamReader);
                        if (transUnit.getId() != null) {
                            transUnits.put(transUnit.getId(), transUnit);
                        }
                    }
                }
            }

            xliff.setTransUnits(transUnits);
            return xliff;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private void setLanguage(Xliff xliff, String attributeName, String language) {
        if (language == null) {
            String errMsg = "'" + attributeName + "' value cannot be null";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
        String[] values = language.split("-");
        if (values.length != 2) {
            String errMsg = "'" + attributeName + "' attribute must have values for both - " +
                    "language and country separated by '-', i.e. 'EN-US' or 'JA-JP'";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
        String languageCode = values[0].toLowerCase();
        String countryCode = values[1].toUpperCase();
        if (SOURCE_LANGUAGE.equals(attributeName)) {
            xliff.setSourceLanguage(languageCode);
            xliff.setSourceCountry(countryCode);
        } else if (TARGET_LANGUAGE.equals(attributeName)) {
            xliff.setTargetLanguage(languageCode);
            xliff.setTargetCountry(countryCode);
        }
    }


    /**
     * Retrieve one &lt;trans-unit/&gt; contents.
     *
     * @param streamReader XML Stream reader
     * @return Trans unit read
     */
    private TransUnit getTransUnit(XMLStreamReader streamReader) {

        String transUnitId = null;
        Integer maxBytes = null;
        for (int i = 0; i < streamReader.getAttributeCount(); i++) {
            String attrName = streamReader.getAttributeName(i).toString();
            String attrValue = streamReader.getAttributeValue(i);
            if (ID.equals(attrName)) {
                logger.info("Processing " + TRANS_UNIT + "'s " + ID + "...");
                transUnitId = attrValue;
            } else if (MAX_BYTES.equals(attrName)) {
                logger.info("Processing " + TRANS_UNIT + "'s " + MAX_BYTES + "...");
                maxBytes = (attrValue != null && !attrValue.isEmpty()) ? Integer.valueOf(attrValue) : null;
            }
        }

        try {

            TransUnit transUnit = new TransUnit();
            transUnit.setId(transUnitId);
            transUnit.setMaxBytes(maxBytes);

            // Read until </trans-unit>
            String currentElement = "";
            int eventType = streamReader.getEventType();
            while (streamReader.hasNext()) {
                if (TRANS_UNIT.equals(currentElement) && eventType == XMLEvent.END_ELEMENT) {
                    logger.info("Processing of " + TRANS_UNIT + "' has been completed");
                    break;
                }
                streamReader.next();
                eventType = streamReader.getEventType();
                if (eventType == XMLEvent.START_ELEMENT || eventType == XMLEvent.END_ELEMENT) {
                    currentElement = streamReader.getName().toString();
                    if (eventType == XMLEvent.START_ELEMENT) {
                        if (SOURCE.equals(currentElement)) {
                            logger.info("Processing '" + SOURCE + "'...");
                            transUnit.setSource(streamReader.getElementText());
                        } else if (TARGET.equals(currentElement)) {
                            logger.info("Processing '" + TARGET + "'...");
                            transUnit.setTarget(streamReader.getElementText());
                        }
                    }
                }
            }

            return transUnit;

        } catch (XMLStreamException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
