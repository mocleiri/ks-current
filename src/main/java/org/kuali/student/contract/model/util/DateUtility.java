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
package org.kuali.student.contract.model.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.kuali.student.contract.exception.DictionaryExecutionException;

/**
 *
 * @author nwright
 */
public class DateUtility {

    public static String asYMD(String date)
            throws ParseException {
        if (date == null) {
            return null;
        }
        return asYMD(asDate(date));
    }

    public static Date asDate(String date)
            throws ParseException {
        if (date == null) {
            return null;
        }
        String[] formats = {
            "yyyy-MM-dd",
            "MM/dd/yyyy"
        };
        ParseException pe = null;
        for (int i = 0; i < formats.length; i++) {
            DateFormat df = new SimpleDateFormat(formats[i]);
            try {
                return df.parse(date);
            } catch (ParseException e) {
                pe = e;
            }
        }
        throw pe;
    }

    public static String asYMD(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
    
    private static DateTimeFormatter adjustToEasternTimeZoneFormatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm zzz");
    
    public static String asYMDHMInEasternTimeZone (DateTime date) {
    	if (date == null)
    		return null;
    	
    	DateTime adjustedDate = date.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Toronto")));
    	
    	String formattedDate = adjustToEasternTimeZoneFormatter.print(adjustedDate);
    	
//    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz", Locale.);

    	return formattedDate;
    }
}
