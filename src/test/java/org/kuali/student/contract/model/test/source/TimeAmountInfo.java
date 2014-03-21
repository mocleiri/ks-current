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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.student.contract.model.test.source.ModelBuilder;
import org.kuali.student.contract.model.test.source.TimeAmount;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeAmountInfo", propOrder = {"atpDurationTypeKey", "timeQuantity", "_futureElements"})
public class TimeAmountInfo implements TimeAmount, Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlElement
	private final String atpDurationTypeKey; 
	
	@XmlElement
	private final Integer timeQuantity; 

    @XmlAnyElement
    private final List<Element> _futureElements;    
	
	private TimeAmountInfo() {
		atpDurationTypeKey = null; 
		timeQuantity = null;
		_futureElements = null;
	}
	
	private TimeAmountInfo(TimeAmount builder) {
		this.atpDurationTypeKey = builder.getAtpDurationTypeKey();
		this.timeQuantity = builder.getTimeQuantity();
		this._futureElements = null;
	}
	
	public String getAtpDurationTypeKey(){
		return atpDurationTypeKey;
	}
	
	public Integer getTimeQuantity(){
		return timeQuantity;
	}
	
	public static class Builder implements ModelBuilder<TimeAmountInfo>, TimeAmount {
		private String atpDurationTypeKey;
		private Integer timeQuantity;

		public Builder() {}
		
		public Builder(TimeAmount taInfo) {
			this.atpDurationTypeKey = taInfo.getAtpDurationTypeKey();
			this.timeQuantity = taInfo.getTimeQuantity();
		}
		
		public TimeAmountInfo build() {
			return new TimeAmountInfo(this);
		}

        public String getAtpDurationTypeKey() {
            return atpDurationTypeKey;
        }

        public void setAtpDurationTypeKey(String atpDurationTypeKey) {
            this.atpDurationTypeKey = atpDurationTypeKey;
        }

        public Integer getTimeQuantity() {
            return timeQuantity;
        }

        public void setTimeQuantity(Integer timeQuantity) {
            this.timeQuantity = timeQuantity;
        }
	}
}
