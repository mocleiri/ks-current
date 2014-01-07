package com.sigmasys.kuali.ksa.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * JaxbUtils contains reusable JAXB helper methods
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
public class JaxbUtils {

    private static final Log logger = LogFactory.getLog(JaxbUtils.class);


    private JaxbUtils() {
    }

    /**
     * Parses the incoming XML specified by StringReader and converts it into a Java object using JAXB.
     *
     * @param xml String representation of XML
     * @return NeedAnalysisInput instance
     */
    public static <T> T fromXml(String xml, Class<T> type) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Takes an object instance and serializes it to XML.
     *
     * @param object a Java bean instance to be serialized to XML
     * @return String representation of XML
     */
    public static <T> String toXml(T object) {
        try {
            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
