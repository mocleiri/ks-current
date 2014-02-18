package org.kuali.student.ap.framework.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.framework.config.module.ModuleConfigurer;

/**
 * Common Rice module configurer for setting up KS services to use with KSAP.
 */
public abstract class AbstractKsapModuleConfigurer extends ModuleConfigurer {

	public AbstractKsapModuleConfigurer(String moduleName) {
		super(moduleName);
		setValidRunModes(Arrays.asList(RunMode.LOCAL, RunMode.EMBEDDED, RunMode.REMOTE));
	}

	protected String getDefaultConfigPackagePath() {
		return KSAPConstants.KSAP_PACKAGE_CONFIG_PATH;
	}

	private String getKsModuleName() {
		String mn = getModuleName().toLowerCase();
		if (mn.startsWith("ks"))
			return mn.substring(2);
		else
			return mn;
	}

	@Override
	public List<String> getPrimarySpringFiles() {
		List<String> springFileLocations = new ArrayList<String>();
		springFileLocations.add(getDefaultConfigPackagePath() + "ks-"
				+ getKsModuleName().toLowerCase() + "-"
				+ getRunMode().name().toLowerCase() + ".xml");
		return springFileLocations;
	}

}
