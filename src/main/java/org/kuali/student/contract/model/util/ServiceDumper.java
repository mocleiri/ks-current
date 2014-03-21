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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.student.contract.model.util;

import java.io.PrintStream;

import org.kuali.student.contract.model.Service;

/**
 *
 * @author nwright
 */
public class ServiceDumper {

    private Service service;
    private PrintStream out;

    public ServiceDumper(Service service, PrintStream out) {
        this.service = service;
        this.out = out;
    }

    public void dump() {
        out.println(service.getKey() + "." + service.getName() + "(" + service.getVersion() + ")");
    }

    public void writeTabbedHeader() {
        out.print("Key");
        out.print("\t");
        out.print("Name");
        out.print("\t");
        out.print("Version");
        out.print("\t");
        out.print("url");
        out.print("\t");
        out.print("ImplProject");
        out.print("\t");
        out.print("status");
        out.print("\t");
        out.print("comments");
        out.println("");
    }

    public void writeTabbedData() {
        out.print(service.getKey());
        out.print("\t");
        out.print(service.getName());
        out.print("\t");
        out.print(service.getVersion());
        out.print("\t");
        out.print(service.getUrl());
        out.print("\t");
        out.print(service.getImplProject());
        out.print("\t");
        out.print(service.getStatus());
        out.print("\t");
        out.print(service.getComments());
        out.println("");
    }
}
