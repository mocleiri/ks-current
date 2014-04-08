package org.kuali.student.ap.framework.context.support;

import java.util.Collections;
import java.util.Set;

import org.kuali.student.ap.academicplan.infc.Placeholder;
import org.kuali.student.ap.framework.context.PlaceholderResolver;

public class CoursePlaceholderResolver implements PlaceholderResolver {

	@Override
	public Set<String> resolve(Placeholder ph) throws IllegalStateException, IllegalArgumentException {
		//If any of parms 2-4 are not null, throw an exception
		if(ph.getParm2() != null || ph.getParm3() != null || ph.getParm4() != null){
			throw new IllegalStateException(this.getClass() + " called but Placeholder doesn't represent a course.");
		}
		
		//Make sure we have a non-null parm1
		if(ph.getParm1() == null){
			throw new IllegalArgumentException("parm1 for course Placeholder can't be null");
		}
		
		//parm1 is already a KS course ID, so we can pass it along
		return Collections.singleton(ph.getParm1());
	}
}
