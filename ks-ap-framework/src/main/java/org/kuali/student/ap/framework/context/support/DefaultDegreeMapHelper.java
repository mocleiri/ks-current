package org.kuali.student.ap.framework.context.support;

import java.util.Date;

import org.kuali.student.ap.academicplan.infc.DegreeMap;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.DegreeMapHelper;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;

/**
 * Default implementation of the DegreeMapHelper
 */

public class DefaultDegreeMapHelper implements DegreeMapHelper {

	@Override
	public DegreeMap getDegreeMap(String id, Date effDt) {
		try {
			return KsapFrameworkServiceLocator.getDegreeMapService()
					.getDegreeMap(
							id,
							effDt,
							KsapFrameworkServiceLocator.getContext()
									.getContextInfo());
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("Invalid degree map " + id
					+ " (" + effDt + ")");
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("Invalid degree map " + id
					+ " (" + effDt + ")");
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("Invalid degree map " + id
					+ " (" + effDt + ")");
		} catch (OperationFailedException e) {
			throw new IllegalStateException("DM lookup error " + id + " ("
					+ effDt + ")");
		}
	}

	@Override
	public String getDisplayTermId() {
		throw new UnsupportedOperationException("TODO: implment this method");
	}

}
