/**
 * Copyright 2004-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
    private String servKey;

    public RemoteImplServiceSpringBeanWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey) {
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
    }

    private void indentPrintln(String line) {
        out.indentPrintln(line);
    }

    /**
     * Write out the entire file
     *
     * @param out
     */
    public void write() {
        initXmlWriter();
        Service service = finder.findService(servKey);
        writeService(service);
    }

    private void writeService(Service service) {

        indentPrintln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        indentPrintln("<!--");
        indentPrintln(" Copyright 2008-2014 The Kuali Foundation");
        indentPrintln(" ");
        indentPrintln(" Licensed under the Educational Community License, Version 2.0 (the \"License\");");
        indentPrintln(" you may not use this file except in compliance with the License.");
        indentPrintln(" You may obtain a copy of the License at");
        indentPrintln(" ");
        indentPrintln(" http://www.opensource.org/licenses/ecl2.php");
        indentPrintln(" ");
        indentPrintln(" Unless required by applicable law or agreed to in writing, software");
        indentPrintln(" distributed under the License is distributed on an \"AS IS\" BASIS,");
        indentPrintln(" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        indentPrintln(" See the License for the specific language governing permissions and");
        indentPrintln(" limitations under the License.");
        indentPrintln("-->");
        indentPrintln("<beans xmlns=\"http://www.springframework.org/schema/beans\"");
        indentPrintln("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        indentPrintln("       xmlns:util=\"http://www.springframework.org/schema/util\"");
        indentPrintln("       xmlns:p=\"http://www.springframework.org/schema/p\"");
        indentPrintln("       xmlns:aop=\"http://www.springframework.org/schema/aop\"");
        indentPrintln("       xmlns:tx=\"http://www.springframework.org/schema/tx\"");
        indentPrintln("       xsi:schemaLocation=\"http://www.springframework.org/schema/beans");
        indentPrintln("                           http://www.springframework.org/schema/beans/spring-beans-3.0.xsd");
        indentPrintln("                           http://www.springframework.org/schema/tx");
        indentPrintln("                           http://www.springframework.org/schema/tx/spring-tx-3.0.xsd");
        indentPrintln("                           http://www.springframework.org/schema/aop");
        indentPrintln("                           http://www.springframework.org/schema/aop/spring-aop-3.0.xsd");
        indentPrintln("                           http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd\">");
        indentPrintln("");
        indentPrintln("");
        indentPrintln("    <bean id=\"" + service.getKey().toLowerCase() + "ServiceRemoteImpl\" class=\"" 
                + RemoteImplServiceWriter.calcPackage(service.getKey(), rootPackage) + "." + RemoteImplServiceWriter.calcClassName(service.getKey()) + "\">");
        indentPrintln("    <!-- ********************************************************************************************");
        indentPrintln("    The host url or other properties may be configured here BUT the host url should really be set via configuration ");
        indentPrintln("    so that you can have your remote service point to local host during development, your TEST servers during testing, ");
        indentPrintln("    QA for your QA deployment and your PROD servers for production by just changing the config file on that server, for example:");
        indentPrintln("    ");
        indentPrintln("    Add something like this to your config (in userhome/kuali/dev/ks-xxx-config.xml on the machine that is running the application's");
        indentPrintln("    war file:");
        indentPrintln("    ");
        indentPrintln("    Notes: ");
        indentPrintln("      The first parameter provides a default server for all remote services");
        indentPrintln("      The second parameter allows you to override the server on a service by service basis, in this case the " + service.getKey().toLowerCase() + " service");
        indentPrintln("      ");
        indentPrintln("      <config>");
        indentPrintln("        ...  ");
        indentPrintln("            <param name=\"remote.service.host.url\">http://qaserver.myschool.edu/kscm</param>");
        indentPrintln("            <param name=\"remote.service.host.url." + service.getKey().toLowerCase() + "\">http://my" + service.getKey().toLowerCase() + "serviceserver.myschool.edu/kscm</param>");
        indentPrintln("        ...");
        indentPrintln("      </config>");
        indentPrintln("    ");
        indentPrintln("          You can so something like this only if you want to hardwire in as part of the war the host url to a particular server");
        indentPrintln("          perhaps as part of configuring a unit or functional test:");
        indentPrintln("               <property name=\"hostUrl\" value=\"http://localhost/kscm\" />");
        indentPrintln("    **************************************************************************************************** -->");
        indentPrintln("    </bean>");
        indentPrintln("");
        indentPrintln("    <bean id=\"ks.exp." + service.getKey().toLowerCase() + "Service\" class=\"org.kuali.rice.ksb.api.bus.support.ServiceBusExporter\">");
        indentPrintln("        <property name=\"serviceDefinition\">");
        indentPrintln("            <bean class=\"org.kuali.rice.ksb.api.bus.support.SoapServiceDefinition\">");
        indentPrintln("                <property name=\"jaxWsService\" value=\"true\" />");
        indentPrintln("                <property name=\"service\" ref=\"" + service.getKey().toLowerCase() + "ServiceRemoteImpl\" />");
        indentPrintln("                <property name=\"serviceInterface\" value=\"" + service.getImplProject() + "." + service.getName() + "\" />");
        indentPrintln("                <property name=\"localServiceName\" value=\"" + service.getName() + "\" />");
        indentPrintln("                <property name=\"serviceNameSpaceURI\" value=\"http://student.kuali.org/wsdl/" + service.getKey().toLowerCase() + "\" />");
        indentPrintln("                <property name=\"busSecurity\" value=\"false\" />");
        indentPrintln("            </bean>");
        indentPrintln("        </property>");
        indentPrintln("    </bean>");
        indentPrintln("");
        indentPrintln("</beans>");

    }

    private void initXmlWriter() {
        String fileName = "ksb-" + servKey.toLowerCase() + "-service-remote-impl.xml";

        File dir = new File(this.directory + "/main/resources/ksb");

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("Could not create directory "
                        + this.directory + "/main/resources");
            }
        }

        String dirStr = this.directory + "/main/resources/ksb";
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
