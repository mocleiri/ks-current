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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.student.datadictionary.util;

import static org.junit.Assert.assertEquals;

import java.util.Stack;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.student.contract.model.test.source.AtpInfo;
import org.kuali.student.r2.core.exemption.dto.ExemptionInfo;
import org.kuali.student.validation.decorator.mojo.ValidationDecoratorWriterForOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class Bean2DictionaryConverterTest {
    
    private static Logger log = LoggerFactory.getLogger(Bean2DictionaryConverterTest.class);
    

    public Bean2DictionaryConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of convert method, of class Bean2DictionaryConverter.
     */
    @Test
    public void testConvert() {
        log.info("convert");
        Class<?> clazz = null;
        Stack<DataDictionaryDefinitionBase> parentFields = null;
        Stack<Class<?>> parentClasses = null;
        Bean2DictionaryConverter instance = null;
        DataObjectEntry result = null;
        
        clazz = AtpInfo.class;
        parentFields = new Stack<DataDictionaryDefinitionBase>();
        parentClasses = new Stack<Class<?>>();        
        instance = new Bean2DictionaryConverter(clazz, parentFields, parentClasses);
        result = instance.convert();
        assertEquals(13, result.getAttributes().size());

        clazz = ExemptionInfo.class;
        parentFields = new Stack<DataDictionaryDefinitionBase>();
        parentClasses = new Stack<Class<?>>();        
        instance = new Bean2DictionaryConverter(clazz, parentFields, parentClasses);
        result = instance.convert();
        assertEquals(22, result.getAttributes().size());        
    }
}
