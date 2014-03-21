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
package org.kuali.student.contract.model.test.source;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.kuali.student.contract.model.test.source.KeyEntityInfo;

import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AtpInfo", propOrder = { "key", "typeKey", "stateKey", "name",
		"descr", "startDate", "endDate", "meta", "attributes",
		"_futureElements" })
public class AtpInfo extends KeyEntityInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement
	private Date startDate;
	@XmlElement
	private Date endDate;
	@XmlAnyElement
	private List<Element> _futureElements;

	public static AtpInfo newInstance() {
		return new AtpInfo();
	}

	public AtpInfo() {
		startDate = null;
		endDate = null;
		_futureElements = null;
	}


	public Date getStartDate() {
		return startDate != null ? new Date(startDate.getTime()) : null;
	}

	
	public void setStartDate(Date startDate) {
		if (startDate != null)
			this.startDate = new Date(startDate.getTime());
	}

	public Date getEndDate() {
		return endDate != null ? new Date(endDate.getTime()) : null;
	}

	
	public void setEndDate(Date endDate) {
		if (endDate != null)
			this.endDate = new Date(endDate.getTime());
	}
}
