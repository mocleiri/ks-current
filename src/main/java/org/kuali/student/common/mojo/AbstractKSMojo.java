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
package org.kuali.student.common.mojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.impl.ServiceContractModelCache;
import org.kuali.student.contract.model.impl.ServiceContractModelQDoxLoader;
import org.kuali.student.contract.model.validation.ServiceContractModelValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;

/**
 * 
 * The basic Kuali Student Mojo that defines things like the source lookup path.
 * 
 * @author ocleirig
 * 
 */
public abstract class AbstractKSMojo extends AbstractMojo {

	private static final Logger log = LoggerFactory.getLogger(AbstractKSMojo.class);
	
	/**
	 * @parameter
	 **/
	private List<String> sourceDirs;
	
	/**
	 * @parameter
	 */
	private List<String>contextSourceDirs;

	public AbstractKSMojo() {
		super();
	}

	public List<String> getSourceDirs() {
		return sourceDirs;
	}

	public void setSourceDirs(List<String> sourceDirs) {
		this.sourceDirs = sourceDirs;
	}
	
	
	
	/**
	 * @return the contextSourceDirs
	 */
	public List<String> getContextSourceDirs() {
		return contextSourceDirs;
	}

	/**
	 * @param contextSourceDirs the contextSourceDirs to set
	 */
	public void setContextSourceDirs(List<String> contextSourceDirs) {
		this.contextSourceDirs = contextSourceDirs;
	}



	protected final Set<String>localPackages = new HashSet<String>();
	

	protected final ServiceContractModel getModel() {

		Set<String> modelSourceDirectories = new LinkedHashSet<String>();

		MavenProject project = (MavenProject) getPluginContext().get("project");
		/*
		 * Default to the source directory that the plugin is run from.
		 */

		if (sourceDirs == null) {
			modelSourceDirectories.add(project.getBuild().getSourceDirectory());
			
		} else {
				modelSourceDirectories.addAll(sourceDirs);
		}
		if (modelSourceDirectories.size() == 0)
			throw new RuntimeException("No Source Directories are defined");

		for (String directory : modelSourceDirectories) {
            
			DirectoryScanner scanner = new DirectoryScanner(new File (directory));
			scanner.addFilter(new SuffixFilter(".java"));
			scanner.scan(new FileVisitor() {
				public void visitFile(File currentFile) {
					
					String path = currentFile.getPath();
					
					int startIndex = path.indexOf("org" + File.separator + "kuali");
					
					if (startIndex != -1) {
						
						String subPath = path.substring(startIndex);
						
						String[] parts = subPath.split(StringEscapeUtils.escapeJava(File.separator));
						
						StringBuilder packageBuilder = new StringBuilder();
						
						for (int i = 0; i < parts.length-1; i++) {
							if (i != 0)
								packageBuilder.append(".");
							
							packageBuilder.append(parts[i]);
						}
						
						String pkg = packageBuilder.toString();
						
						localPackages.add(pkg);
					}
				}
			});
		}
		
		if (contextSourceDirs != null)
			modelSourceDirectories.addAll(contextSourceDirs);
		
		ServiceContractModel instance = new ServiceContractModelQDoxLoader(
				new ArrayList<String>(modelSourceDirectories));
		
		return new ServiceContractModelCache(instance);
	}

	protected final boolean validate(ServiceContractModel model) {
		Collection<String> errors = new ServiceContractModelValidator(model)
				.validate();
		if (errors.size() > 0) {

			StringBuilder buf = new StringBuilder();
			buf.append(errors.size()).append(
					" errors found while validating the data.");
			int cnt = 0;
			for (String msg : errors) {
				cnt++;
				buf.append("\n");
				buf.append("*error*").append(cnt).append(":").append(msg);
			}

			buf.append(errors.size()).append(
					" errors found while validating the data.");
			return false;
		}
		return true;
	}

}
