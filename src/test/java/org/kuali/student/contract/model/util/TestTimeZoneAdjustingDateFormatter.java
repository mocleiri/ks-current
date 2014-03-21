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
/**
 * 
 */
package org.kuali.student.contract.model.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.tz.DateTimeZoneBuilder;

import junit.framework.TestCase;

/**
 * @author ocleirig
 * 
 * This tests that the DateUtility class works properly.
 *
 */
public class TestTimeZoneAdjustingDateFormatter extends TestCase {

	/**
	 * 
	 */
	public TestTimeZoneAdjustingDateFormatter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public TestTimeZoneAdjustingDateFormatter(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public void testConvertUTCToEastern() {
		
		DateTime utcTime = new DateTime(DateTimeZone.UTC).withDate(2012, 8, 15).withTime(13, 0, 0, 0);
		
		String formatted = DateUtility.asYMDHMInEasternTimeZone(utcTime);
		
		// 1 pm UTC should be 9 AM EDT
		assertEquals("2012-08-15 09:00 EDT", formatted);
		
		DateTime pacificTime = utcTime.withZoneRetainFields(DateTimeZone.forID("America/Los_Angeles"));
	
		formatted = DateUtility.asYMDHMInEasternTimeZone(pacificTime);
		
		// 1 pm pacific time should be 4 pm Eastern time
		assertEquals("2012-08-15 16:00 EDT", formatted);
				
				
		
	}

}
