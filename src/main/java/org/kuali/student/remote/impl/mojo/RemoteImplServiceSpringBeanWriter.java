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
package org.kuali.student.remote.impl.mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.kuali.student.contract.model.Service;

import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.writer.XmlWriter;

/**
 *
 * @author nwright
 */
public class RemoteImplServiceSpringBeanWriter {
    
    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private XmlWriter out;
    
    public RemoteImplServiceSpringBeanWriter(ServiceContractModel model,
            String directory,
            String rootPackage) {
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
    }

    /**
     * Write out the entire file
     *
     * @param out
     */
    public void write() {
        initXmlWriter();
        for (Service service : model.getServices()) {
            writeService(service);
        }
    }
    
    private void writeService(Service service) {
        out.println("");
        
        out.indentPrintln("<bean id=\"" + service.getKey().toLowerCase() + "Service\" class=\"" + RemoteImplServiceWriter.calcPackage(service.getKey(), rootPackage) + "." + RemoteImplServiceWriter.calcClassName(service.getKey()) + "\">");
        out.incrementIndent();
        out.indentPrintln("<property name=\"hostUrl\" value=\"http://env2.ks.kuali.org\" />");
        out.decrementIndent();
        out.indentPrintln("</bean>");
        out.println("");
        
        out.indentPrintln("<bean id=\"ks.exp." + service.getKey().toLowerCase() + "Service\" class=\"org.kuali.rice.ksb.api.bus.support.ServiceBusExporter\">");
        out.incrementIndent();
        out.indentPrintln("<property name=\"serviceDefinition\">");
        out.incrementIndent();
        out.indentPrintln("<bean class=\"org.kuali.rice.ksb.api.bus.support.SoapServiceDefinition\">");
        out.incrementIndent();
        out.indentPrintln("<property name=\"jaxWsService\" value=\"true\" />");
        out.indentPrintln("<property name=\"service\" ref=\"" + service.getKey().toLowerCase() + "Service\" />");
        out.indentPrintln("<property name=\"serviceInterface\" value=\"" + service.getImplProject() + "." + service.getName() + "\" />");
        out.indentPrintln("<property name=\"localServiceName\" value=\"" + service.getName() + "\" />");
        out.indentPrintln("<property name=\"serviceNameSpaceURI\" value=\"http://student.kuali.org/wsdl/" + service.getKey().toLowerCase () + "\" />");
        out.indentPrintln("<property name=\"busSecurity\" value=\"false\" />");
        out.decrementIndent();
        out.indentPrintln("</bean>");
        out.decrementIndent();
        out.indentPrintln("</property>");
        out.decrementIndent();
        out.indentPrintln("</bean>");
    }
    
    private void initXmlWriter() {
        String fileName = "ksb-remote-impl-generated.xml";
        
        File dir = new File(this.directory + "/main/resources");
        
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("Could not create directory "
                        + this.directory + "/main/resources");
            }
        }
        
        String dirStr = this.directory  + "/main/resources";
        File dirFile = new File(dirStr);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                throw new IllegalStateException(
                        "Could not create directory " + dirStr);
            }
        }
        try {
            PrintStream out = new PrintStream(new FileOutputStream(
                    dirStr + "/"
                    + fileName, false));
            this.out = new XmlWriter(out, 0);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
