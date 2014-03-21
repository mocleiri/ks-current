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
package org.kuali.student.contract.writer.search;

import org.kuali.student.contract.model.SearchResultColumn;
import org.kuali.student.contract.writer.XmlWriter;

import java.io.PrintStream;

/**
 * Writes out a constraint in XML format.
 * @author nwright
 */
public class SearchResultColumnWriter extends XmlWriter {

    private SearchResultColumn column;

    public SearchResultColumnWriter(PrintStream out, int indent,
            SearchResultColumn column) {
        super(out, indent);
        this.column = column;
    }

    public void write() {
        println("");
        indentPrint("<search:resultColumn");
        //TODO: not sure what to put in the key attribute
        incrementIndent();
        writeAttribute("id", column.getKey());
        println(">");

        // write out comments
        writeComment(column.getComments());

        writeTag("search:name", column.getName());
        writeTag("search:desc", column.getDescription());
        writeTag("search:dataType", column.getDataType());

        indentPrintln("</search:resultColumn>");
        decrementIndent();
    }
}
