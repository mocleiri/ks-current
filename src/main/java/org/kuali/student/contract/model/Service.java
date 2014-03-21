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
package org.kuali.student.contract.model;

import java.util.List;

/**
 *
 * @author nwright
 */
public class Service {

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private String implProject;

    public String getImplProject() {
        return implProject;
    }

    public void setImplProject(String implProject) {
        this.implProject = implProject;
    }
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    private List<String> includedServices;

    /**
     * Comma separated list of services that are included
     *
     * @return
     */
    public List<String> getIncludedServices() {
        return includedServices;
    }

    /**
     * comma separated list of services that are included
     * @param includedServices
     */
    public void setIncludedServices(List<String> includedServices) {
        this.includedServices = includedServices;
    }

    @Override
    public String toString() {
        return "Service{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", implProject='" + implProject + '\'' +
                ", status='" + status + '\'' +
                ", comments='" + comments + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", includedServices=" + includedServices +
                '}';
    }
}
