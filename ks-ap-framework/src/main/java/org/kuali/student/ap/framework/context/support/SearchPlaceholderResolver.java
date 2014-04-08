package org.kuali.student.ap.framework.context.support;

import java.util.Collections;
import java.util.Set;

import org.kuali.student.ap.academicplan.infc.Placeholder;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.CourseHelper;
import org.kuali.student.ap.framework.context.PlaceholderResolver;
import org.kuali.student.ap.framework.context.PlanConstants;
import org.kuali.student.r2.lum.course.service.CourseService;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.MissingParameterException;

/**
 * 
 * @author glsimpso
 * @version 1.0
 */
public class SearchPlaceholderResolver implements PlaceholderResolver {

	@Override
	public Set<String> resolve(Placeholder ph) throws IllegalStateException, MissingParameterException {
		/* 	Parm1: institution
	 	Parm2: subject criteria
		Parm3: catalog nbr criteria */
		
		throw new UnsupportedOperationException("TODO: Grant is still working on this.");
		/*
		//Make sure parms 1-3 are not null
		if(ph.getParm1() == null || ph.getParm2() == null || ph.getParm3() == null){
			throw new MissingParameterException("parms 1, 2, and 3 for course Placeholder can't be null");
		}
		
		//Make sure we have a null parm4
		if(ph.getParm4() != null || ph.getTypeKey() != PlanConstants.PLACEHOLDER_SEARCH){
			throw new IllegalStateException(this.getClass() + " called but Placeholder doesn't represent a search.");
		}
	
		CourseService cs = KsapFrameworkServiceLocator.getCourseService();

		//cs.searchForCourseIds(criteria, KsapFrameworkServiceLocator.getContext().getContextInfo());
		return null;
		*/
	}
}
