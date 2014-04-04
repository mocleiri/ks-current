package org.kuali.student.ap.framework.context;

import java.util.Set;

import org.kuali.student.ap.academicplan.infc.Placeholder;

/**
 * @author glsimpso
 * @version 1.0
 */
public interface PlaceholderResolver {

	/**
	 * @since 1.0
	 * @param ph the Placeholder to resolve
	 * @return a set of strings representing IDs of the items the 
	 * placeholder resolves to 
	 */
	public Set<String> resolve(Placeholder ph);
}
