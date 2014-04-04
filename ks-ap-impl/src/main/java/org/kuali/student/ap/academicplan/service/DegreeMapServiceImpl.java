package org.kuali.student.ap.academicplan.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.ap.academicplan.dao.DegreeMapDao;
import org.kuali.student.ap.academicplan.dao.DegreeMapRequirementDao;
import org.kuali.student.ap.academicplan.dao.PlaceholderDao;
import org.kuali.student.ap.academicplan.dao.PlaceholderInstanceDao;
import org.kuali.student.ap.academicplan.dao.ReferenceObjectListDao;
import org.kuali.student.ap.academicplan.dto.DegreeMapInfo;
import org.kuali.student.ap.academicplan.dto.DegreeMapRequirementInfo;
import org.kuali.student.ap.academicplan.dto.PlaceholderInfo;
import org.kuali.student.ap.academicplan.dto.PlaceholderInstanceInfo;
import org.kuali.student.ap.academicplan.dto.ReferenceObjectListInfo;
import org.kuali.student.ap.academicplan.model.DegreeMapRequirementEntity;
import org.kuali.student.ap.academicplan.model.PlaceholderEntity;
import org.kuali.student.ap.academicplan.model.PlaceholderInstanceEntity;
import org.kuali.student.ap.academicplan.model.ReferenceObjectListItemEntity;
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
	private ReferenceObjectListDao referenceObjectListDao;

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
	

	public ReferenceObjectListDao getReferenceObjectListDao() {
		return referenceObjectListDao;
	}

	public void setReferenceObjectListDao(
			ReferenceObjectListDao referenceObjectListDao) {
		this.referenceObjectListDao = referenceObjectListDao;
	}

	@Override
	public DegreeMapInfo getDegreeMap(String degreeMapId, Date effdt,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException {
		// TODO Auto-generated method stub
		//IuDegreeMapService.getDegreeMap();
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
		
		if (requirementId == null ) {
			throw new MissingParameterException("Requirement id is null.");
		}

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
		dme.copyFromInfo(requirement);	
		degreeMapRequirementDao.persist(dme);
		return degreeMapRequirementDao.find(requirementId).toDto();	
		
	}

	
	@Override
	public DegreeMapRequirementInfo updateRequirement(String requirementId,
			DegreeMapRequirementInfo dto, ContextInfo context)
			throws DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException, DoesNotExistException {
		
		DegreeMapRequirementEntity requirementEntity = degreeMapRequirementDao.find(requirementId);
		if (requirementEntity == null ){
			throw new DoesNotExistException(requirementId);
		}
		
		if (dto == null){
			throw new MissingParameterException("requirement is null.");
		}
		
		requirementEntity.copyFromInfo(dto);
		
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
		
		if (placeholderId == null ) {
			throw new MissingParameterException("Placeholder id is null.");
		}
		
		PlaceholderEntity placeholder = placeholderDao.find(placeholderId);
		if (null == placeholder) {
			throw new DoesNotExistException(String.format("Placeholder with id Id [%s] does not exist", placeholderId));
		}
		
		return placeholder.toDto();
	}
	

	@Override
	public PlaceholderInfo createPlaceholder(PlaceholderInfo dto,
			ContextInfo context) throws AlreadyExistsException,
			DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException {

		PlaceholderEntity pe = new PlaceholderEntity();
		String placeholderId = UUIDHelper.genStringUUID();		
		pe.setId(placeholderId);		
		pe.copyFromInfo(dto);
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
		
		if (null == placeholderEntity) {
			throw new DoesNotExistException(String.format("Placeholder with id Id [%s] does not exist", placeholderId));
		}

		placeholderEntity.copyFromInfo(placeholder);
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
		
		if (placeholderInstanceId == null ) {
			throw new MissingParameterException("PlaceholderInstance id is null.");
		}

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
		pie.copyFromDto(placeholderInstance);
		placeholderInstanceDao.persist(pie);
		return placeholderInstanceDao.find(placeholderInstanceId).toDto();

	}	

	
	@Override
	public PlaceholderInstanceInfo updatePlaceholderInstance(String placeholderInstanceId,
			PlaceholderInstanceInfo dto, ContextInfo context)
			throws DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException, DoesNotExistException {

		PlaceholderInstanceEntity placeholderInstanceEntity = placeholderInstanceDao.find(placeholderInstanceId);
		if (placeholderInstanceEntity == null ){
			throw new DoesNotExistException(placeholderInstanceId);
		}
		
		if (dto == null){
			throw new MissingParameterException("placeholderInstance is null.");
		}
		
		placeholderInstanceEntity.copyFromDto(dto);		
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

	
	
	// REFERENCE OBJECT LIST
	
	@Override
	public ReferenceObjectListInfo getReferenceObjectList(String id,
			ContextInfo context) throws DoesNotExistException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException {


		if (id == null ){
		throw new MissingParameterException("id must not be null");
	}
	
	List<ReferenceObjectListItemEntity> refObjEntities = referenceObjectListDao.getReferenceOjbectListItems(id);

	if (refObjEntities == null || refObjEntities.isEmpty()){
		throw new DoesNotExistException("list does not exist " + id);
	}
	
	ReferenceObjectListInfo refObjList = new ReferenceObjectListInfo();
	refObjList.setId(id);
	refObjList.setReferences(refObjEntities);
	
	return refObjList;

	}
	
	
	
	@Override
	public ReferenceObjectListInfo createReferenceObjectList(
			ReferenceObjectListInfo referenceObjectList, ContextInfo context)
			throws AlreadyExistsException, DataValidationErrorException,
			InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {
		
		if (referenceObjectList == null){
			throw new InvalidParameterException("referenceOjbectList cannot be null.");
		}
		
		String id = referenceObjectList.getId();
		try {
			getReferenceObjectList(id, context);
		} catch (DoesNotExistException e) {
			//TODO: SAVE HERE 
			doCreate(referenceObjectList, context);
			return null;
		}
		
		throw new AlreadyExistsException("referenceOjbectList with this id already exists: " + id);
	}
	

	private void doCreate(ReferenceObjectListInfo referenceObjectList,
			ContextInfo context) {
	
			// assumes it doesn't exists. checking up to caller.
		
	}

	
	@Override
	public ReferenceObjectListInfo updateReferenceObjectList(String id,
			ReferenceObjectListInfo referenceObjectList, ContextInfo context)
			throws DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException, DoesNotExistException {
		// TODO Auto-generated method stub
		
		
		// delete 
		
		
		// call doCreate.


		
		return null;
	}

	@Override
	public StatusInfo deleteReferenceObjectList(String id, ContextInfo context)
			throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException,
			PermissionDeniedException {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	
	
	
	
//	@Override
//	public List<ReferenceObjectListInfo> getReferenceObjectItems(String listId,
//			ContextInfo context) throws DoesNotExistException,
//			InvalidParameterException, MissingParameterException,
//			OperationFailedException {
//
//
//		if (listId == null ){
//			throw new MissingParameterException("listId must not be null");
//		}
//		
//		List<ReferenceObjectListEntity> refObjEntities = referenceObjectListDao.getReferenceOjbectListItems(listId);
//
//		List<ReferenceObjectListInfo> refObjInfos = new ArrayList<ReferenceObjectListInfo>();
//
//		if (null == refObjEntities) {
//			throw new DoesNotExistException(
//					String.format(
//							"There are no items for listId %s ", listId));
//		} else {
//			for (ReferenceObjectListEntity refObjEntity : refObjEntities) {
//				refObjInfos.add(refObjEntity.toDto());
//			}
//		}
//		return refObjInfos;
//	}
//	
//
//	@Override
//	public ReferenceObjectListInfo getReferenceOjbectItem(
//			String referenceObjectItemId, ContextInfo context)
//			throws DoesNotExistException, InvalidParameterException,
//			MissingParameterException, OperationFailedException {
//
//		if (referenceObjectItemId == null ) {
//			throw new MissingParameterException("referenceObjectItemId is null.");
//		}
//		
//		ReferenceObjectListEntity referenceOjbectListItem = referenceObjectListDao.find(referenceObjectItemId);
//		if (null == referenceOjbectListItem) {
//			throw new DoesNotExistException(String.format("referenceObjectItem with id Id [%s] does not exist", referenceObjectItemId));
//		}
//		
//		return referenceOjbectListItem.toDto();
//	}
//	
//
//	@Override
//	public ReferenceObjectListInfo createReferenceObjectItem(
//			ReferenceObjectListInfo dto, ContextInfo context)
//			throws AlreadyExistsException, DataValidationErrorException,
//			InvalidParameterException, MissingParameterException,
//			OperationFailedException, PermissionDeniedException {
//
//
//		ReferenceObjectListEntity pe = new ReferenceObjectListEntity();
//		String rId = UUIDHelper.genStringUUID();		
//		pe.setId(rId);		
//		pe.copyFromInfo(dto);
//		referenceObjectListDao.persist(pe);
//		return referenceObjectListDao.find(rId).toDto();
//
//	}
//
//	@Override
//	public ReferenceObjectListInfo updateReferenceObjectItem(
//			String referenceObjectItemId,
//			ReferenceObjectListInfo referenceObjectItem, ContextInfo context)
//			throws DataValidationErrorException, InvalidParameterException,
//			MissingParameterException, OperationFailedException,
//			PermissionDeniedException, DoesNotExistException {
//
//		ReferenceObjectListEntity referenceObjectListEntity = referenceObjectListDao.find(referenceObjectItemId);
//		
//		if (null == referenceObjectListEntity) {
//			throw new DoesNotExistException(String.format("referenceObjectList Item with id Id [%s] does not exist", referenceObjectItemId));
//		}
//
//		referenceObjectListEntity.copyFromInfo(referenceObjectItem);
//		referenceObjectListDao.update(referenceObjectListEntity);
//		return referenceObjectListDao.find(referenceObjectItemId).toDto();
//
//	}
//
//	
//	@Override
//	public StatusInfo deleteReferenceObjectItem(String referenceObjectItemId,
//			ContextInfo context) throws DoesNotExistException,
//			InvalidParameterException, MissingParameterException,
//			OperationFailedException, PermissionDeniedException {
//
//		StatusInfo status = new StatusInfo();
//		status.setSuccess(true);
//		
//		ReferenceObjectListEntity pe = referenceObjectListDao.find(referenceObjectItemId);
//		if (pe == null ) {
//			throw new DoesNotExistException(String.format("ReferenceObjectListItem with id Id [%s] does not exist", referenceObjectItemId));
//		}
//
//		referenceObjectListDao.remove(pe);
//		return status;
//		
//	}

	
	

}
