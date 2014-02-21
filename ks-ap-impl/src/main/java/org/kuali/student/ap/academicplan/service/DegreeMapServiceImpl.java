package org.kuali.student.ap.academicplan.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.ap.academicplan.dao.DegreeMapDao;
import org.kuali.student.ap.academicplan.dao.DegreeMapRequirementDao;
import org.kuali.student.ap.academicplan.dao.PlaceholderDao;
import org.kuali.student.ap.academicplan.dao.PlaceholderInstanceDao;
import org.kuali.student.ap.academicplan.dto.DegreeMapInfo;
import org.kuali.student.ap.academicplan.dto.DegreeMapRequirementInfo;
import org.kuali.student.ap.academicplan.dto.PlaceholderInfo;
import org.kuali.student.ap.academicplan.dto.PlaceholderInstanceInfo;
import org.kuali.student.ap.academicplan.model.DegreeMapRequirementEntity;
import org.kuali.student.ap.academicplan.model.PlaceholderEntity;
import org.kuali.student.ap.academicplan.model.PlaceholderInstanceEntity;
import org.kuali.student.common.util.UUIDHelper;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Degree Map Service Implementation.
 */
@Transactional(readOnly = true, noRollbackFor = { AlreadyExistsException.class, DoesNotExistException.class }, rollbackFor = { Throwable.class })
public class DegreeMapServiceImpl implements DegreeMapService {

	private DegreeMapDao degreeMapDao; // OR MAYBE THIS ONE I DON'T HAVE IN HERE BECUASE WE'LL USE A LEARNING PLAN DAO????
	private DegreeMapRequirementDao degreeMapRequirementDao;
	private PlaceholderDao placeholderDao;
	private PlaceholderInstanceDao placeholderInstanceDao;

	public DegreeMapDao getDegreeMapDao() {
		return degreeMapDao;
	}

	public void setDegreeMapDao(DegreeMapDao degreeMapDao) {
		this.degreeMapDao = degreeMapDao;
	}

	public DegreeMapRequirementDao getDegreeMapRequirementDao() {
		return degreeMapRequirementDao;
	}

	public void setDegreeMapRequirementDao(
			DegreeMapRequirementDao degreeMapRequirementDao) {
		this.degreeMapRequirementDao = degreeMapRequirementDao;
	}
	

	public PlaceholderDao getPlaceholderDao() {
		return placeholderDao;
	}

	public void setPlaceholderDao(PlaceholderDao placeholderDao) {
		this.placeholderDao = placeholderDao;
	}

	public PlaceholderInstanceDao getPlaceholderInstanceDao() {
		return placeholderInstanceDao;
	}

	public void setPlaceholderInstanceDao(
			PlaceholderInstanceDao placeholderInstanceDao) {
		this.placeholderInstanceDao = placeholderInstanceDao;
	}
	

	@Override
	public DegreeMapInfo getDegreeMap(String degreeMapId, Date effdt,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public List<DegreeMapInfo> search(QueryByCriteria criteria,
			ContextInfo contextInfo) throws InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public List<DegreeMapRequirementInfo> getRequirements(String degreeMapId,
			Date effdt, ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException {

		if (degreeMapId == null || effdt == null ){
			throw new MissingParameterException("degreeMapId and effdt must not be null");
		}
		
		// TODO: further validation	
		// degreeMapID and effdt must exist in degree map table (or plan table?)
		// need to figure out how to check that. 
		// maybe Mark can tell me how I would go about doing that?
		
		List<DegreeMapRequirementEntity> requirementEntities = degreeMapRequirementDao
				.getDegreeMapRequirementsByDegreeMapIdEffdt(degreeMapId, effdt);

		List<DegreeMapRequirementInfo> requirementInfos = new ArrayList<DegreeMapRequirementInfo>();

		if (null == requirementEntities) {
			throw new DoesNotExistException(
					String.format(
							"There are no requirements for degreeMapId %s and effectiveDate %d ",
							degreeMapId, effdt));
		} else {
			for (DegreeMapRequirementEntity requirementEntity : requirementEntities) {
				requirementInfos.add(requirementEntity.toDto());
			}
		}
		return requirementInfos;
	}

	
	@Override
	public DegreeMapRequirementInfo getRequirement(String requirementId,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException {
		
		DegreeMapRequirementEntity requirement = degreeMapRequirementDao.find(requirementId);
		if (null == requirement) {
			throw new DoesNotExistException(String.format("Requirement with id Id [%s] does not exist", requirementId));
		}
		
		return requirement.toDto();
	}

	
	@Override
	public DegreeMapRequirementInfo createRequirement(
			DegreeMapRequirementInfo requirement, ContextInfo context)
			throws AlreadyExistsException, DataValidationErrorException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {

		DegreeMapRequirementEntity dme = new DegreeMapRequirementEntity();
		String requirementId = UUIDHelper.genStringUUID();	
		
		dme.setId(requirementId);

		// required
		
		if (requirement.getDegreeMapId() == null ){		
			throw new DataValidationErrorException("requirement degreeMapId  must not be null.");
		}
		dme.setDegreeMapId(requirement.getDegreeMapId());
		
		if (requirement.getDegreeMapEffectiveDate() == null  ){		
			throw new DataValidationErrorException("requirement degreeMap effective date must not be null.");
		}
		dme.setDegreeMapEffectiveDate(requirement.getDegreeMapEffectiveDate());
		
		if (requirement.getRefObjectTypeKey() == null){		
			throw new DataValidationErrorException("requirement refOjbectTypeKey must not be null.");
		}
		dme.setRefObjectTypeKey(requirement.getRefObjectTypeKey());
		
		if (requirement.getRefObjectId() == null  ) {		
			throw new DataValidationErrorException("requirement refOjbectId must not be null.");
		}
		dme.setRefObjectId(requirement.getRefObjectId());
		
	    //required?
		dme.setDisplayTermId(requirement.getDisplayTermId());
		dme.setItemSeq(requirement.getItemSeq());

		// not required
		dme.setCredit(requirement.getCredit());
		dme.setDescr(requirement.getDescr());
		dme.setMininumGrade(requirement.getMinimumGrade());
		dme.setNotes(requirement.getNotes());
		dme.setRequiredTermId(requirement.getRequiredTermId());
		dme.setSuggestedTermId(requirement.getSuggestedTermId());
		dme.setSeqKey(requirement.getSeqKey());
		dme.setSeqNo(requirement.getSeqNo());
		
		//TODO 
		// what do we do about the flags? Do we set them to N when not Y?
		dme.setMilestone(requirement.isMilestone());
		dme.setCritical(requirement.isCritical());
		
		degreeMapRequirementDao.persist(dme);

		return degreeMapRequirementDao.find(requirementId).toDto();
	
	}


	//TODO:
	// This will do nothing to the degree map meta
	// is that going to be OK?
	
	@Override
	public DegreeMapRequirementInfo updateRequirement(String requirementId,
			DegreeMapRequirementInfo requirement, ContextInfo context)
			throws DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException, DoesNotExistException {
		
		DegreeMapRequirementEntity requirementEntity = degreeMapRequirementDao.find(requirementId);
		if (requirementEntity == null ){
			throw new DoesNotExistException(requirementId);
		}
		
		if (requirement == null){
			throw new MissingParameterException("requirement is null.");
		}
				
		requirementEntity.setCredit(requirement.getCredit());
		requirementEntity.setCritical(requirement.isCritical());
		requirementEntity.setDegreeMapEffectiveDate(requirement.getDegreeMapEffectiveDate());
		requirementEntity.setDegreeMapId(requirement.getDegreeMapId());
		requirementEntity.setDescr(requirement.getDescr());
		requirementEntity.setDisplayTermId(requirement.getDisplayTermId());
		requirementEntity.setItemSeq(requirement.getItemSeq());
		requirementEntity.setMilestone(requirement.isMilestone());
		requirementEntity.setMininumGrade(requirement.getMinimumGrade());
		requirementEntity.setNotes(requirement.getNotes());
		requirementEntity.setRefObjectTypeKey(requirement.getRefObjectTypeKey());
		requirementEntity.setRefObjectId(requirement.getRefObjectId());
		requirementEntity.setRequiredTermId(requirement.getRequiredTermId());
		requirementEntity.setSuggestedTermId(requirement.getSuggestedTermId());
		requirementEntity.setSeqKey(requirement.getSeqKey());
		requirementEntity.setSeqNo(requirement.getSeqNo());
		
		degreeMapRequirementDao.update(requirementEntity);
		return degreeMapRequirementDao.find(requirementId).toDto();
		
	}
	
	
	

	@Override
	public StatusInfo deleteRequirement(String requirementId,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {

		StatusInfo status = new StatusInfo();
		status.setSuccess(true);
		
		DegreeMapRequirementEntity dme = degreeMapRequirementDao.find(requirementId);
		if (dme == null ) {
			throw new DoesNotExistException(String.format("Requirement with id Id [%s] does not exist", requirementId));
		}

		degreeMapRequirementDao.remove(dme);
		return status;

	}
	

	// THESE WILL NEED TO BE MOVED TO ACADEMIC PLAN SERVICE
	
	@Override
	public PlaceholderInfo getPlaceholder(String placeholderId,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException {
		
		PlaceholderEntity placeholder = placeholderDao.find(placeholderId);
		if (null == placeholder) {
			throw new DoesNotExistException(String.format("Placeholder with id Id [%s] does not exist", placeholderId));
		}
		
		return placeholder.toDto();
	}
	

	@Override
	public PlaceholderInfo createPlaceholder(PlaceholderInfo placeholder,
			ContextInfo context) throws AlreadyExistsException,
			DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException {

		PlaceholderEntity pe = new PlaceholderEntity();
		String placeholderId = UUIDHelper.genStringUUID();	
		
		pe.setId(placeholderId);

		pe.setTypeKey(placeholder.getTypeKey());
		pe.setParm1(placeholder.getParm1());
		pe.setParm2(placeholder.getParm2());
		pe.setParm3(placeholder.getParm3());
	
		placeholderDao.persist(pe);
		return placeholderDao.find(placeholderId).toDto();
		
	}
	

	
	@Override
	public PlaceholderInfo updatePlaceholder(String placeholderId,
			PlaceholderInfo placeholder, ContextInfo context)
			throws DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException, DoesNotExistException {

		PlaceholderEntity placeholderEntity = placeholderDao.find(placeholderId);
		if (placeholderEntity == null ){
			throw new DoesNotExistException(placeholderId);
		}
		
		if (placeholder == null){
			throw new MissingParameterException("placeholder is null.");
		}
		
		placeholderEntity.setTypeKey(placeholder.getTypeKey());
		placeholderEntity.setParm1(placeholder.getParm1());
		placeholderEntity.setParm2(placeholder.getParm2());
		placeholderEntity.setParm3(placeholder.getParm3());				
		
		placeholderDao.update(placeholderEntity);
		return placeholderDao.find(placeholderId).toDto();
		
	}
		

	@Override
	public StatusInfo deletePlaceholder(String placeholderId,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {
		
		StatusInfo status = new StatusInfo();
		status.setSuccess(true);
		
		PlaceholderEntity pe = placeholderDao.find(placeholderId);
		if (pe == null ) {
			throw new DoesNotExistException(String.format("Placeholder with id Id [%s] does not exist", placeholderId));
		}

		placeholderDao.remove(pe);
		return status;
	}


	// PLACEHOLDER INSTANCE
	
	@Override
	public PlaceholderInstanceInfo getPlaceholderInstance(String placeholderInstanceId,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException {
		
		PlaceholderInstanceEntity placeholderInstance = placeholderInstanceDao.find(placeholderInstanceId);
		if (null == placeholderInstance) {
			throw new DoesNotExistException(String.format("PlaceholderInstance with id Id [%s] does not exist", placeholderInstanceId));
		}
		
		return placeholderInstance.toDto();
	}
	

	@Override
	public PlaceholderInstanceInfo createPlaceholderInstance(
			PlaceholderInstanceInfo placeholderInstance, ContextInfo context)
			throws AlreadyExistsException, DataValidationErrorException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {

		PlaceholderInstanceEntity pie = new PlaceholderInstanceEntity();
		String placeholderInstanceId = UUIDHelper.genStringUUID();

		pie.setId(placeholderInstanceId);
		pie.setPlaceholderId(placeholderInstance.getPlaceholderId());
		pie.setRefObjectId(placeholderInstance.getRefObjectId());
		pie.setRefObjectTypeKey(placeholderInstance.getRefObjectTypeKey());
		pie.setAdvisorId(placeholderInstance.getAdvisorId());
		pie.setAdvisorOK(placeholderInstance.isAdvisorOK());
		pie.setStudentOK(placeholderInstance.isStudentOK());

		placeholderInstanceDao.persist(pie);
		return placeholderInstanceDao.find(placeholderInstanceId).toDto();

	}	

	
	@Override
	public PlaceholderInstanceInfo updatePlaceholderInstance(String placeholderInstanceId,
			PlaceholderInstanceInfo placeholderInstance, ContextInfo context)
			throws DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException, DoesNotExistException {

		PlaceholderInstanceEntity placeholderInstanceEntity = placeholderInstanceDao.find(placeholderInstanceId);
		if (placeholderInstanceEntity == null ){
			throw new DoesNotExistException(placeholderInstanceId);
		}
		
		if (placeholderInstance == null){
			throw new MissingParameterException("placeholderInstance is null.");
		}
		
		placeholderInstanceEntity.setId(placeholderInstanceId);
		placeholderInstanceEntity.setPlaceholderId(placeholderInstance.getPlaceholderId());
		placeholderInstanceEntity.setRefObjectId(placeholderInstance.getRefObjectId());
		placeholderInstanceEntity.setRefObjectTypeKey(placeholderInstance.getRefObjectTypeKey());
		placeholderInstanceEntity.setAdvisorId(placeholderInstance.getAdvisorId());
		placeholderInstanceEntity.setAdvisorOK(placeholderInstance.isAdvisorOK());
		placeholderInstanceEntity.setStudentOK(placeholderInstance.isStudentOK());			
		
		placeholderInstanceDao.update(placeholderInstanceEntity);
		return placeholderInstanceDao.find(placeholderInstanceId).toDto();
		
	}
		

	@Override
	public StatusInfo deletePlaceholderInstance(String placeholderInstanceId,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {
		
		StatusInfo status = new StatusInfo();
		status.setSuccess(true);
		
		PlaceholderInstanceEntity pe = placeholderInstanceDao.find(placeholderInstanceId);
		if (pe == null ) {
			throw new DoesNotExistException(String.format("PlaceholderInstance with id Id [%s] does not exist", placeholderInstanceId));
		}

		placeholderInstanceDao.remove(pe);
		return status;
	}

	
	

}
