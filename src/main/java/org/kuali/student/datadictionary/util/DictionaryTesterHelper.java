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
package org.kuali.student.datadictionary.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DictionaryTesterHelper {

	private static final Logger log = LoggerFactory.getLogger(DictionaryTesterHelper.class);
	
	private String outputDir;
	private Collection<String> inputFiles;
	private List<String> supportFiles;
	
	private Map<String, List<String>>inputFileToBeanNameMap = new LinkedHashMap<String, List<String>>();

	private Set<String> missingDictionaryFiles;

	private Set<String> invalidDictionaryFiles;
	
	

	public DictionaryTesterHelper(String outputDir, Collection<String> inputFiles,
			List<String> supportFiles) {
		this.outputDir = outputDir;
		this.inputFiles = inputFiles;
		this.supportFiles = supportFiles;
	}

	// public void loadApplicationContext() {
	// System.out.println
	// ("DictionaryTesterHelper: begin loading application context");
	//
	// ac = new PathValidatingClassPathXmlApplicationContext();
	//
	// for (String candidateConfigFile : this.configFiles) {
	// ac.addContextFile(candidateConfigFile);
	// }
	//
	// ac.buildApplicationContext();
	// System.out.println
	// ("DictionaryTesterHelper: end loading application context");
	// }
	//
	// public Map<String, DataObjectEntry> getDataObjectEntryBeans () {
	// return (Map<String, DataObjectEntry>)
	// ac.getBeansOfType(DataObjectEntry.class);
	// }

	public void doTest(String projectVersion, String formattedDate) {

		missingDictionaryFiles = new LinkedHashSet<String>();
		
		invalidDictionaryFiles = new LinkedHashSet<String>();
		
		for (String inputFile : inputFiles) {

			List<String> contextFiles = new ArrayList<String>();

			contextFiles.add(inputFile);
			contextFiles.addAll(supportFiles);

			ClassPathXmlApplicationContext ac;
			try {
				log.info("Starting on inputFile: " + inputFile);
				
				ac = new ClassPathXmlApplicationContext(
						contextFiles.toArray(new String[0]));
			} catch (Exception e) {
				log.warn ("FAILED to valildate file: " + inputFile, e);
				
				Throwable cause = e.getCause();
				if (cause != null && cause instanceof FileNotFoundException) {
					missingDictionaryFiles.add(inputFile);
				}
				else {
					// In this case we should create a place holder page 
					writePlaceholderPage(inputFile, cause);
					invalidDictionaryFiles.add(inputFile);
				}
				continue; // skip over this file.
			}

			ArrayList<String> associatedBeanNameList  = new ArrayList<String>();
			
			Map<String, DataObjectEntry> beansOfType = ac
					.getBeansOfType(DataObjectEntry.class);

			for (String beanId : beansOfType.keySet()) {
				
				try {
				DataObjectEntry doe = beansOfType.get(beanId);
				if ("org.kuali.rice.krad.bo.AttributeReferenceDummy".equals(doe
						.getFullClassName())) {
					continue;
				}
				
				associatedBeanNameList.add(beanId);
				
				log.info("Processing data object entry: "
						+ doe.getDataObjectClass().getName());
				DictionaryValidator validator = new DictionaryValidator(doe,
						new HashSet<DataObjectEntry>());
				List<String> errors = validator.validate();
				if (errors != null) {
					if (!errors.isEmpty()) {
						throw new IllegalArgumentException(
								"Errors validating bean " + beanId + "\n"
										+ this.formatAsString(errors));
					}
				}
				String outputFileName = beanId + ".html";
				String fullOutputFileName = this.outputDir + "/"
						+ outputFileName;
				DictionaryFormatter formatter = new DictionaryFormatter(doe,
						beansOfType, beanId, fullOutputFileName);
				
					formatter.formatForHtml(projectVersion, formattedDate);
					
					
				} catch (Exception e) {
					
					log.warn("FAILED to format dictionary page for: " + beanId, e);
					invalidDictionaryFiles.add(inputFile);
					continue;
				}
				
			}
			
			if (associatedBeanNameList.size() > 0) {
				inputFileToBeanNameMap.put(inputFile, associatedBeanNameList);
			}
			
			log.info("Finished processing inputFile: " + inputFile);
		}
	}

	/*
	 * In some cases like where spring fails to parse the input file we will want to write a place holder with the cause.
	 * 
	 */
	private void writePlaceholderPage(String inputFile, Throwable cause) {
		
		String[] parts = inputFile.split ("-");
		
		String beanId = parts[1];
		
			String outputFileName = beanId + ".html";
			String fullOutputFileName = this.outputDir + "/"
					+ outputFileName;
		
			try {
				FileUtils.writeStringToFile(new File (fullOutputFileName), "<html><body><h1>Failed To Generate Page due to Error in Dictionary File: "+inputFile+"</h1>" +ExceptionUtils.getFullStackTrace(cause) + "</body></html>", false);
			} catch (IOException e) {
				log.warn("failed to write placeholder page: " + inputFile, e);
		}
	}

	private String formatAsString(List<String> errors) {
		int i = 0;
		StringBuilder builder = new StringBuilder();
		for (String error : errors) {
			i++;
			builder.append(i).append(". ").append(error).append("\n");
		}
		return builder.toString();
	}

	/**
	 * @return the inputFileToBeanNameMap
	 */
	public Map<String, List<String>> getInputFileToBeanNameMap() {
		return inputFileToBeanNameMap;
	}

	/**
	 * @return the missingDictionaryFiles
	 */
	public Set<String> getMissingDictionaryFiles() {
		return missingDictionaryFiles;
	}

	/**
	 * @return the invalidDictionaryFiles
	 */
	public Set<String> getInvalidDictionaryFiles() {
		return invalidDictionaryFiles;
	}
	
	
	
}
