package org.kuali.student.ap.academicplan.service;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.ap.academicplan.dto.DegreeMapInfo;
import org.kuali.student.ap.academicplan.dto.DegreeMapRequirementInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;

/**
 *
 * @version 1.0 (Dev)
 * @Author mguilla
 * Date: 1/27/14
 */
@WebService(name = DegreeMapServiceConstants.SERVICE_NAME, serviceName = DegreeMapServiceConstants.SERVICE_NAME, portName = DegreeMapServiceConstants.SERVICE_NAME, targetNamespace = DegreeMapServiceConstants.NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DegreeMapService {

    public DegreeMapInfo getDegreeMap(@WebParam(name = "degreeMapId") String degreeMapId, @WebParam(name = "effdt") Date effdt, @WebParam(name = "context") ContextInfo context) 
    		throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException;

    public List<DegreeMapRequirementInfo> getRequirements(@WebParam(name = "degreeMapId") String degreeMapId, @WebParam(name = "effdt") Date effdt,  @WebParam(name = "context") ContextInfo context) 
    		throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException;

    public DegreeMapRequirementInfo getRequirement(@WebParam(name = "requirementId") String requirementId,  @WebParam(name = "context") ContextInfo context) 
    		throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException;
    
    public DegreeMapRequirementInfo createRequirement(@WebParam(name = "requirementId") DegreeMapRequirementInfo requirementId,
    		@WebParam(name = "context") ContextInfo context) throws AlreadyExistsException, DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException;

    public DegreeMapRequirementInfo updateRequirement(@WebParam(name = "requirementId") String requirementId, @WebParam(name = "requirementId") DegreeMapRequirementInfo Requirement, @WebParam(name = "context") ContextInfo context) throws DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException;


    public StatusInfo deleteRequirement(@WebParam(name = "requirementId") String requirementId, @WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException;

    public List<DegreeMapInfo> search(@WebParam(name = "criteria") QueryByCriteria criteria, @WebParam(name = "contextInfo") ContextInfo contextInfo) throws InvalidParameterException,
    	MissingParameterException, OperationFailedException, PermissionDeniedException;

    
    //  public List<DegreeMapRequirementInfo> search(QueryByCriteria criteria)
    
    //    public DegreeMapInfo createDegreeMap(@WebParam(name = "degreeMap") DegreeMapInfo degreeMap,
//    		@WebParam(name = "context") ContextInfo context) throws AlreadyExistsException, DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException;
//    
//    public DegreeMapInfo updateDegreeMap(@WebParam(name = "degreeMapId") String degreeMapId, @WebParam(name = "DegreeMap") DegreeMapInfo DegreeMap, @WebParam(name = "context") ContextInfo context) throws DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException;
//
//
//    public StatusInfo deleteDegreeMap(@WebParam(name = "degreeMapId") String degreeMapId, @WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException;
//
//
//    public List<DegreeMapInfo> getDegreeMapsForProgram(@WebParam(name = "credentialProgramId") String credentialProgramId, @WebParam(name = "context") ContextInfo context) 
//    		throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException;

}


