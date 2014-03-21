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
package org.kuali.student.datadictionary.mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.joda.time.DateTime;
import org.kuali.student.common.mojo.AbstractKSMojo;
import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.impl.ServiceContractModelCache;
import org.kuali.student.contract.model.impl.ServiceContractModelQDoxLoader;
import org.kuali.student.contract.model.util.DateUtility;
import org.kuali.student.contract.model.util.VersionLinesUtility;
import org.kuali.student.contract.model.validation.ServiceContractModelValidator;
import org.kuali.student.datadictionary.util.DictionaryFormatter;
import org.kuali.student.datadictionary.util.DictionaryTesterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mojo for generating a formatted view of the data dictionary.
 * 
 * <pre>
 * {@code
 * <plugin>
 * 		<groupId>org.kuali.maven.plugins</groupId>
 *      <artifactId>maven-kscontractdoc-plugin</artifactId>
 *      <execution>
 *      	<id>generate-dictionary-documentation</id>
 *          <phase>site</phase>
 *          <goals>
 *          	<goal>ksdictionarydoc</goal>                            
 *          </goals>
 *          <configuration>
 *           <supportFiles>
 *           	<supportFile>commonApplicationContext.xml</supportFile>
 *           </supportFiles>
 *          </configuration>
 *     </execution>
 * </plugin>
 *  }
 * </pre>
 * 
 * We use the QDox model to read the class files present and to enumerate the list of Message Structure objects.  Then for each identified type we build an application context that includes the union of that 
 * file plus any files specified using the <supportFile> configuration parameter.
 * 
 *  Errors with an application context are detected and logged but will not break the plugin's ability to generate the other files.
 * 
 * @goal ksdictionarydoc
 * @phase site
 * @requiresDependencyResolution test
 */
public class KSDictionaryDocMojo extends AbstractKSMojo {

	private static final Logger log = LoggerFactory.getLogger(KSDictionaryDocMojo.class);
	
    /**
     * @parameter property="project"
     * @required
     * @readonly
     */
    private MavenProject project;
    
   
    
    /**
     * The base applicationContext files.  
     * @parameter
     **/
    private List<String> supportFiles = new ArrayList<String>();
    /**
     * @parameter property="htmlDirectory" default-value="${project.build.directory}/site/services/dictionarydocs"
     */
    private File htmlDirectory;

	private String testDictionaryFile;

	private LinkedHashMap<String, String> dictionaryFileToMessageStructureMap  = new LinkedHashMap<String, String>();
   
	public void setHtmlDirectory(File htmlDirectory) {
        this.htmlDirectory = htmlDirectory;
    }

    public File getHtmlDirectory() {
        return htmlDirectory;
    }

    public MavenProject getProject() {
        return project;
    }

    public List<String> getSupportFiles() {
        return supportFiles;
    }

    public void setSupportFiles(List<String> supportFiles) {
    	this.supportFiles.clear();
    	
    	if (supportFiles != null)
    		this.supportFiles.addAll(supportFiles);
    }
    
    @Override
    public void execute()
            throws MojoExecutionException {
    	this.getLog().info("generating dictionary documentation");
        
        if (getPluginContext() != null) {
        	project = (MavenProject) getPluginContext().get("project");
        }
        
        // add the current projects classpath to the plugin so the springbean
        // loader can find the xml files and lasses that it needs to can be run
        // against the current project's files
        if (project != null) {
            this.getLog().info("adding current project's classpath to plugin class loader");
            List<String> runtimeClasspathElements;
            try {
                runtimeClasspathElements = project.getRuntimeClasspathElements();
            } catch (DependencyResolutionRequiredException ex) {
                throw new MojoExecutionException("Failed to get runtime classpath elements.", ex);
            }
            URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
            for (int i = 0; i < runtimeClasspathElements.size(); i++) {
                String element = (String) runtimeClasspathElements.get(i);
                try {
                    runtimeUrls[i] = new File(element).toURI().toURL();
                } catch (MalformedURLException ex) {
                    throw new MojoExecutionException(element, ex);
                }
            }
            URLClassLoader newLoader = new URLClassLoader(runtimeUrls,
                    Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(newLoader);
        }


        if (!htmlDirectory.exists()) {
            if (!htmlDirectory.mkdirs()) {
                throw new IllegalArgumentException("Could not create directory "
                        + this.htmlDirectory.getPath());
            }
        }
        
        Set<String> inpFiles = new LinkedHashSet<String>();
		if (project != null) {
			ServiceContractModel model = this.getModel();
			this.validate(model);
			inpFiles.addAll(extractDictionaryFiles(model));

		} else {
			inpFiles.add(this.testDictionaryFile);
		}
    

        String outputDir = this.htmlDirectory.getAbsolutePath();
        DictionaryTesterHelper tester = new DictionaryTesterHelper(outputDir, inpFiles, this.supportFiles);
        tester.doTest(project.getVersion(), DateUtility.asYMDHMInEasternTimeZone(new DateTime()));

        // write out the index file
        String indexFileName = this.htmlDirectory.getPath() + "/" + "index.html";
        File indexFile = new File(indexFileName);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(indexFile, false);
        } catch (FileNotFoundException ex) {
//            throw new MojoExecutionException(indexFileName, ex);
            throw new IllegalArgumentException(indexFileName, ex);
        }
        
        String formattedDate = DateUtility.asYMDHMInEasternTimeZone(new DateTime());
        
        PrintStream out = new PrintStream(outputStream);
        
        DictionaryFormatter.writeHeader(out, "Data Dictionary Index");
        
        VersionLinesUtility.writeVersionTag(out, "<a href=\"index.html\">Home</a>", "<a href=\"../contractdocs/index.html\">Contract Docs Home</a>", project.getVersion(), formattedDate);
        
        out.println("<h1>Data Dictionary Index</h1>");
        out.println("<blockquote>A Red background indicates that there is a problem with the data dictionary for that type.</blockquote>");
        out.println("<ul>");
        
        Map<String, List<String>> fileToBeanNameMap = tester.getInputFileToBeanNameMap();
        
		for (String inputFile : fileToBeanNameMap.keySet()) {

			boolean containsError = false;
			
			if (tester.getInvalidDictionaryFiles().contains(inputFile)) {
				containsError = true;
			}
			
			List<String> beanIds = fileToBeanNameMap.get(inputFile);

			for (String beanId : beanIds) {

				String outputFileName = beanId + ".html";
				
				if (containsError)
					out.println ("<li class=\"invalid\">");
				else
					out.println ("<li>");
				
				out.println("<a href=\"" + outputFileName + "\">" + beanId
						+ "</a>");
			}
		}
        out.println("</ul>");
        
		
        
		if (tester.getMissingDictionaryFiles().size() > 0) {
			out.println("<h1>Missing Dictionary Files</h1>");
			out.println("<blockquote>The Message structure exists but there is no dictionary file present.</blockquote>");
			out.println("<ul>");
			for (String missingFile : tester.getMissingDictionaryFiles()) {
				out.println("<li><b>" + missingFile + "</b></li>");
			}
			out.println("</ul>");

		}
        
        DictionaryFormatter.writeFooter(out);
        out.flush();
        out.close();
        
        log.info("finished generating dictionary documentation");
    }

	private Collection<String> extractDictionaryFiles(
			ServiceContractModel model) {
		
		Set<String> dictionaryFiles = new LinkedHashSet<String>();
		
		List<MessageStructure> mss = new ArrayList<MessageStructure>(model.getMessageStructures());
		
		// this is needed to remove r1 duplicates
		// we only seem to hold the r2 data but the entry exists multiple times.
		Set<String>mergedMessageStructureNames = new LinkedHashSet<String>();
        
        Iterator<MessageStructure> it = mss.iterator();
        
        while (it.hasNext()) {
        	MessageStructure ms = it.next();
			
        	String messageStructureName = ms.getXmlObject();
        	
        	if (mergedMessageStructureNames.contains(messageStructureName) || !messageStructureName.endsWith("Info")) {
        		
        		// remove duplicates
        		// remove classes that don't end in Info
        		it.remove();
        		
        	}
        	else
        		mergedMessageStructureNames.add(ms.getName());
		}
		
		for (MessageStructure messageStructure : mss) {
			
			String inputFileName = "ks-" + messageStructure.getXmlObject() + "-dictionary.xml";

			dictionaryFiles.add(inputFileName);
			
			// we also track the file name to message structure so we can link the invalid
			dictionaryFileToMessageStructureMap.put(inputFileName, messageStructure.getXmlObject());
			
		}
		
		
		return dictionaryFiles;
	}

	/**
	 * Used for testing to hard code a single dictionary file to use.
	 * @param string
	 */
	public void setTestDictionaryFile(String dictionaryFile) {
		this.testDictionaryFile = dictionaryFile;
		
	}
}
