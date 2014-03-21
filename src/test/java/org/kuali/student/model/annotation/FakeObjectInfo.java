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
package org.kuali.student.model.annotation;

/**
 * @author Kuali Student Team
 */
public class FakeObjectInfo {

	EnumWithAnnotationValue withValue;
	EnumWithoutAnnotationValue withoutValue;
	
	/**
	 * 
	 */
	public FakeObjectInfo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the withValue
	 */
	public EnumWithAnnotationValue getWithValue() {
		return withValue;
	}

	/**
	 * @param withValue the withValue to set
	 */
	public void setWithValue(EnumWithAnnotationValue withValue) {
		this.withValue = withValue;
	}

	/**
	 * @return the withoutValue
	 */
	public EnumWithoutAnnotationValue getWithoutValue() {
		return withoutValue;
	}

	/**
	 * @param withoutValue the withoutValue to set
	 */
	public void setWithoutValue(EnumWithoutAnnotationValue withoutValue) {
		this.withoutValue = withoutValue;
	}

	
}
