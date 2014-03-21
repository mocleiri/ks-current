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

import java.io.PrintStream;

import org.kuali.student.contract.writer.HtmlWriter;

/**
 * @author ocleirig
 *
 */
public class VersionLinesUtility {

	/**
	 * 
	 */
	public VersionLinesUtility() {
		// TODO Auto-generated constructor stub
	}

	public static void writeVersionTag(HtmlWriter writer, String homeLink, String otherHomeLink, String projectVersion, String formattedDate) {
		writeVersionTag(writer.getOut(), homeLink, otherHomeLink, projectVersion, formattedDate);
		
	}
	
	public static void writeVersionTag(PrintStream writer, String homeLink, String otherHomeLink, String projectVersion, String formattedDate) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("<style type=\"text/css\">");
		
		builder.append("ul.horizontal-list { display: inline; list-style-type: none; margin: 0; }");
		builder.append("ul.horizontal-list li { display: inline; list-style-type: none; padding-right: 1em; }");

	
		builder.append("</style>");
		
		builder.append("<ul class=\"horizontal-list\">");
		
		builder.append("<li>");
		builder.append(homeLink);
		builder.append("</li>");
		
		builder.append("<li>");
		builder.append(otherHomeLink);
		builder.append("</li>");
		
		builder.append("<li><b>Maven Release: </b>");
		builder.append(projectVersion);
		
		builder.append("</li><li><b>Contract Docs Plugin Version: </b>");
		
		String contractDocMavenVersion = VersionLinesUtility.class.getPackage().getImplementationVersion();
		
        builder.append(contractDocMavenVersion);
        
		builder.append("</li><li><b>Page Generated: </b>");
		builder.append(formattedDate);
		
		builder.append("</li></ul>");
		
		writer.println(builder.toString());
	}
}
