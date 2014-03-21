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

import org.kuali.student.contract.exception.DictionaryExecutionException;
import org.kuali.student.contract.model.*;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.model.validation.DictionaryValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A dictionary expander that expandds the main type
 * @author nwright
 */
public class DictionaryMainTypeExpander implements DictionaryExpander {

    private static Logger log = LoggerFactory.getLogger(DictionaryMainTypeExpander.class);
    
    private List<Dictionary> oldDicts;
    private List<Dictionary> newDicts;
    private DictionaryModel spreadsheet;
    private ModelFinder finder;

    public DictionaryMainTypeExpander(DictionaryModel spreadsheet) {
        this.spreadsheet = spreadsheet;
        this.oldDicts = spreadsheet.getDictionary();
        this.finder = new ModelFinder(this.spreadsheet);
    }

    @Override
    public List<Dictionary> expand() {
        newDicts = new ArrayList(oldDicts.size() * 3);
        expandMainType();
        return newDicts;
    }

    private void expandMainType() {
        for (Dictionary d : oldDicts) {
            Type type = getMainType(d);
            if (type.getStatus().equals(Type.GROUPING)) {
                expandMainType(d, type);
            } else {
                newDicts.add(d);
            }
        }
        return;
    }

    private Type getMainType(Dictionary dict) {
        Dictionary root = finder.findRoot(dict);
        Type type = finder.findType(root.getXmlObject(), dict.getType());
        if (type == null) {
            throw new DictionaryValidationException("Could not find main type for dictionary entry "
                    + dict.getId() + ": " + root.getXmlObject() + "." + dict.getType());
        }
        return type;
    }

    private void expandMainType(Dictionary d, Type type) {
        for (Type t : finder.expandType(type)) {
            try {
                log.info("Expanding dictionary entry " + d.getId()
                        + " with type " + type.getName() + "  to " + t.getName());
                Dictionary clone = (Dictionary) d.clone();
                clone.setType(t.getName());
                newDicts.add(clone);
            } catch (CloneNotSupportedException ex) {
                throw new DictionaryExecutionException(ex);
            }
        }
    }
}
 
 