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
package org.kuali.student.contract.model.validation;

import org.kuali.student.contract.model.Constraint;
import org.kuali.student.contract.model.Dictionary;
import org.kuali.student.contract.model.DictionaryModel;
import org.kuali.student.contract.model.Field;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.util.ModelFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates the entire spreadsheet model
 * @author nwright
 */
public class DictionaryModelValidator implements ModelValidator {

    private DictionaryModel model;
    private ModelFinder finder;

    public DictionaryModelValidator(DictionaryModel model) {
        this.model = model;
        this.finder = new ModelFinder(model);
    }
    List<String> errors;

    @Override
    public Collection<String> validate() {
        errors = new ArrayList();
        validateConstraints();
        validateFields();
        validateDefaultDictionary();
        validateStateOverrideDictionary();
        checkForDuplicateDictionaryEntries();
        return errors;
    }

    private void validateConstraints() {
        if (model.getConstraints().size() == 0) {
            addError("No constraints found");
        }
        for (Constraint cons : model.getConstraints()) {
            ConstraintValidator cv = new ConstraintValidator(cons);
            errors.addAll(cv.validate());
        }
    }

    private void validateFields() {
        if (model.getFields().size() == 0) {
            addError("No fields found");
        }
        for (Field field : model.getFields()) {
            FieldValidator fv = new FieldValidator(field, model);
            errors.addAll(fv.validate());
        }
    }

    private void validateDefaultDictionary() {
        if (finder.findDefaultDictionary().size() == 0) {
            addError("No dictionary entries for the (default) state found");
        }
        for (Dictionary dict : finder.findDefaultDictionary()) {
            DictionaryValidator dv = new DictionaryValidator(dict, model);
            errors.addAll(dv.validate());
        }
    }

    private void validateStateOverrideDictionary() {

        if (finder.findStateOverrideDictionary().size() == 0) {
            addError("No dictionary entries that override for the (default) state found");
        }
        for (Dictionary dict : finder.findStateOverrideDictionary()) {
            DictionaryValidator dv = new DictionaryValidator(dict, model);
            errors.addAll(dv.validate());
        }
    }

    private void checkForDuplicateDictionaryEntries() {
        Set dups = new HashSet();
        for (Dictionary dict : finder.findDefaultDictionary()) {
            if (!dups.add(dict.getId())) {
                addError("Duplicate ID's found in dictionary: " + dict.getId());
            }
        }
        for (Dictionary dict : finder.findStateOverrideDictionary()) {
            if (!dups.add(dict.getId())) {
                addError("Duplicate ID's found in dictionary: " + dict.getId());
            }
        }
    }

    private void addError(String msg) {
        String error = "Error in overall spreadsheet: " + msg;
        if (!errors.contains(error)) {
            errors.add(error);
        }
    }
}
