package com.sigmasys.kuali.ksa.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * XmlSchemaValidator
 *
 * @author Michael Ivanov
 */
public class XmlSchemaValidator {

    private static final Log logger = LogFactory.getLog(XmlSchemaValidator.class);

    private static final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private static final ResourcePatternResolver resourcePatternResolver =
            ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader());

    private final Schema schema;

    public XmlSchemaValidator(String... schemaLocations) {
        schema = getSchema(schemaLocations);
    }

    private List<Source> getSchemaSources(String... schemaLocations) throws IOException {
        List<Source> schemaSources = new LinkedList<Source>();
        for (String schemaLocation : schemaLocations) {
            Resource[] resources = resourcePatternResolver.getResources(schemaLocation);
            if (resources != null) {
                for (Resource resource : resources) {
                    schemaSources.add(new StreamSource(resource.getURL().toExternalForm()));
                }
            }
        }
        return schemaSources;
    }

    private Schema getSchema(String... schemaLocations) {
        try {
            List<Source> schemaSources = getSchemaSources(schemaLocations);
            if (!schemaSources.isEmpty()) {
                return schemaFactory.newSchema(schemaSources.toArray(new Source[schemaSources.size()]));
            }
            HashSet<String> locations = new HashSet<String>(Arrays.asList(schemaLocations));
            String errMsg = "Cannot retrieve the XML schema located at " + locations;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean validateXml(String xml) {
        return validateXml(new StringReader(xml));
    }

    public boolean validateXml(Reader reader) {
        return validateXmlAndGetErrorMessage(reader) == null;
    }

    public String validateXmlAndGetErrorMessage(String xml) {
        return validateXmlAndGetErrorMessage(new StringReader(xml));
    }

    public String validateXmlAndGetErrorMessage(Reader reader) {

        // Get a validator from the schema.
        Validator validator = schema.newValidator();

        // Parse the document
        Source source = new StreamSource(reader);

        // Validate the document against the schema
        try {
            validator.validate(source);
            logger.info("XML is valid");
            return null;
        } catch (Throwable t) {
            logger.info("XML is invalid: " + t.getMessage(), t);
            return StringUtils.isNotBlank(t.getMessage()) ? t.getMessage() : "XML is invalid";
        }

    }


}