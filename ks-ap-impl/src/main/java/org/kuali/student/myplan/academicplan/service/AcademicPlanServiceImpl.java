package org.kuali.student.myplan.academicplan.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebParam;

import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.common.util.UUIDHelper;
import org.kuali.student.myplan.academicplan.dao.LearningPlanDao;
import org.kuali.student.myplan.academicplan.dao.LearningPlanTypeDao;
import org.kuali.student.myplan.academicplan.dao.PlanItemDao;
import org.kuali.student.myplan.academicplan.dao.PlanItemTypeDao;
import org.kuali.student.myplan.academicplan.dto.LearningPlanInfo;
import org.kuali.student.myplan.academicplan.dto.PlanItemInfo;
import org.kuali.student.myplan.academicplan.dto.PlanItemSetInfo;
import org.kuali.student.myplan.academicplan.model.AttributeEntity;
import org.kuali.student.myplan.academicplan.model.LearningPlanAttributeEntity;
import org.kuali.student.myplan.academicplan.model.LearningPlanEntity;
import org.kuali.student.myplan.academicplan.model.LearningPlanRichTextEntity;
import org.kuali.student.myplan.academicplan.model.LearningPlanTypeEntity;
import org.kuali.student.myplan.academicplan.model.PlanItemAttributeEntity;
import org.kuali.student.myplan.academicplan.model.PlanItemEntity;
import org.kuali.student.myplan.academicplan.model.PlanItemRichTextEntity;
import org.kuali.student.myplan.academicplan.model.PlanItemTypeEntity;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.RichTextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.common.infc.HasAttributes;
import org.kuali.student.r2.common.infc.ValidationResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Academic Plan Service Implementation.
 */
@Transactional(readOnly = true, noRollbackFor = { AlreadyExistsException.class, DoesNotExistException.class }, rollbackFor = { Throwable.class })
public class AcademicPlanServiceImpl implements AcademicPlanService {

	private LearningPlanDao learningPlanDao;
	private LearningPlanTypeDao learningPlanTypeDao;
	private PlanItemDao planItemDao;
	private PlanItemTypeDao planItemTypeDao;

	public PlanItemDao getPlanItemDao() {
		return planItemDao;
	}

	public void setPlanItemDao(PlanItemDao planItemDao) {
		this.planItemDao = planItemDao;
	}

	public PlanItemTypeDao getPlanItemTypeDao() {
		return planItemTypeDao;
	}

	public void setPlanItemTypeDao(PlanItemTypeDao planItemTypeDao) {
		this.planItemTypeDao = planItemTypeDao;
	}

	public LearningPlanDao getLearningPlanDao() {
		return learningPlanDao;
	}

	public void setLearningPlanDao(LearningPlanDao learningPlanDao) {
		this.learningPlanDao = learningPlanDao;
	}

	public LearningPlanTypeDao getLearningPlanTypeDao() {
		return learningPlanTypeDao;
	}

	public void setLearningPlanTypeDao(LearningPlanTypeDao learningPlanTypeDao) {
		this.learningPlanTypeDao = learningPlanTypeDao;
	}

	@Override
	public LearningPlanInfo getLearningPlan(@WebParam(name = "learningPlanId") String learningPlanId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {

		LearningPlanEntity lpe = learningPlanDao.find(learningPlanId);
		if (null == lpe) {
			throw new DoesNotExistException(learningPlanId);
		}

		LearningPlanInfo dto = lpe.toDto();
		return dto;
	}

	@Override
	public PlanItemInfo getPlanItem(@WebParam(name = "planItemId") String planItemId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {

		PlanItemEntity planItem = planItemDao.find(planItemId);
		if (null == planItem) {
			throw new DoesNotExistException(String.format("Plan item with Id [%s] does not exist", planItemId));
		}

		return planItem.toDto();
	}

	@Override
	public List<PlanItemInfo> getPlanItemsInPlanByType(@WebParam(name = "learningPlanId") String learningPlanId,
			@WebParam(name = "planItemTypeKey") String planItemTypeKey, @WebParam(name = "context") ContextInfo context)
			throws DoesNotExistException, InvalidParameterException, MissingParameterException,
			OperationFailedException {
		List<PlanItemInfo> planItemInfos = new ArrayList<PlanItemInfo>();
		List<PlanItemEntity> planItemEntities = planItemDao.getLearningPlanItems(learningPlanId, planItemTypeKey);
		if (null == planItemEntities) {
			throw new DoesNotExistException(String.format("Plan item with learning plan Id [%s] does not exist",
					learningPlanId));
		} else {
			for (PlanItemEntity planItemEntity : planItemEntities) {
				planItemInfos.add(planItemEntity.toDto());
			}
		}
		return planItemInfos;
	}

	@Override
	public List<PlanItemInfo> getPlanItemsInPlan(@WebParam(name = "learningPlanId") String learningPlanId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {

		List<PlanItemInfo> dtos = new ArrayList<PlanItemInfo>();

		List<PlanItemEntity> planItems = planItemDao.getLearningPlanItems(learningPlanId);
		for (PlanItemEntity pie : planItems) {
			dtos.add(pie.toDto());
		}
		return dtos;
	}

	@Override
	public List<PlanItemInfo> getPlanItemsInPlanByAtp(@WebParam(name = "learningPlanId") String learningPlanId,
			@WebParam(name = "atpKey") String atpKey, @WebParam(name = "planItemTypeKey") String planItemTypeKey,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {

		List<PlanItemEntity> planItemsList = planItemDao.getLearningPlanItems(learningPlanId, planItemTypeKey);

		List<PlanItemInfo> planItemDtos = new ArrayList<PlanItemInfo>();
		for (PlanItemEntity pie : planItemsList) {
			if (pie.getPlanPeriods().contains(atpKey)) {
				planItemDtos.add(pie.toDto());
			}
		}

		return planItemDtos;
	}

	@Override
	public List<PlanItemInfo> getPlanItemsInPlanByRefObjectIdByRefObjectType(
			@WebParam(name = "learningPlanId") String learningPlanId,
			@WebParam(name = "refObjectId") String refObjectId, @WebParam(name = "refObjectType") String refObjectType,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {

		List<PlanItemEntity> planItemsList = planItemDao.getLearningPlanItemsByRefObjectId(learningPlanId, refObjectId,
				refObjectType);

		List<PlanItemInfo> planItemDtos = new ArrayList<PlanItemInfo>();
		for (PlanItemEntity pie : planItemsList) {
			planItemDtos.add(pie.toDto());
		}

		return planItemDtos;
	}

	@Override
	public PlanItemSetInfo getPlanItemSet(@WebParam(name = "planItemSetId") String planItemSetId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<PlanItemInfo> getPlanItemsInSet(@WebParam(name = "planItemSetId") String planItemSetId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<LearningPlanInfo> getLearningPlansForStudentByType(@WebParam(name = "studentId") String studentId,
			@WebParam(name = "planTypeKey") String planTypeKey, @WebParam(name = "context") ContextInfo context)
			throws DoesNotExistException, InvalidParameterException, MissingParameterException,
			OperationFailedException {

		List<LearningPlanEntity> lpeList = learningPlanDao.getLearningPlansByType(studentId, planTypeKey);

		List<LearningPlanInfo> learningPlanDtos = new ArrayList<LearningPlanInfo>();
		for (LearningPlanEntity lpe : lpeList) {
			learningPlanDtos.add(lpe.toDto());
		}
		return learningPlanDtos;
	}

	@Override
	@Transactional
	public LearningPlanInfo createLearningPlan(@WebParam(name = "learningPlan") LearningPlanInfo learningPlan,
			@WebParam(name = "context") ContextInfo context) throws AlreadyExistsException,
			DataValidationErrorException, InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {

		LearningPlanEntity lpe = new LearningPlanEntity();
		lpe.setId(UUIDHelper.genStringUUID());

		// FIXME: Is this check necessary?
		LearningPlanEntity existing = learningPlanDao.find(lpe.getId());
		if (existing != null) {
			// When generating a new UUID as the key, this should not be possible.
			throw new AlreadyExistsException();
		}

		LearningPlanTypeEntity type = learningPlanTypeDao.find(learningPlan.getTypeKey());
		if (type == null) {
			throw new InvalidParameterException(String.format("Unknown type [%s].", learningPlan.getTypeKey()));
		}
		lpe.setLearningPlanType(type);
		lpe.setStateKey(learningPlan.getStateKey());

		lpe.setStudentId(learningPlan.getStudentId());
		lpe.setDescr(new LearningPlanRichTextEntity(learningPlan.getDescr()));

		//  Item meta
		lpe.setCreateId(context.getPrincipalId());
		lpe.setCreateTime(new Date());
		lpe.setUpdateId(context.getPrincipalId());
		lpe.setUpdateTime(new Date());
		lpe.setShared(learningPlan.getShared());

		// Update attributes.
		Set<LearningPlanAttributeEntity> attributeEntities = new HashSet<LearningPlanAttributeEntity>();
		for (Attribute att : learningPlan.getAttributes())
			attributeEntities.add(new LearningPlanAttributeEntity(att, lpe));
		lpe.setAttributes(attributeEntities);

		learningPlanDao.persist(lpe);

		return learningPlanDao.find(lpe.getId()).toDto();
	}

	@Override
	@Transactional
	public PlanItemInfo createPlanItem(@WebParam(name = "planItem") PlanItemInfo planItem,
			@WebParam(name = "context") ContextInfo context) throws AlreadyExistsException,
			DataValidationErrorException, InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {

		//  FIXME: For a given plan there should be only one planned course item per course id. So, do a lookup to see
		//  if a plan item exists if the type is "planned" and do an update of ATPid instead of creating a new plan item.
		PlanItemEntity pie = new PlanItemEntity();
		String planItemId = UUIDHelper.genStringUUID();
		pie.setId(planItemId);

		pie.setRefObjectId(planItem.getRefObjectId());
		pie.setRefObjectTypeKey(planItem.getRefObjectType());

		PlanItemTypeEntity planItemTypeEntity = planItemTypeDao.find(planItem.getTypeKey());
		if (planItemTypeEntity == null) {
			throw new InvalidParameterException(String.format("Unknown plan item type id [%s].", planItem.getTypeKey()));
		}
		pie.setLearningPlanItemType(planItemTypeEntity);

		//  Convert the List of plan periods to a Set.
		pie.setPlanPeriods(new HashSet<String>(planItem.getPlanPeriods()));

		//  Set attributes.
		pie.setAttributes(new HashSet<PlanItemAttributeEntity>());
		if (planItem.getAttributes() != null) {
			for (Attribute att : planItem.getAttributes()) {
				PlanItemAttributeEntity attEntity = new PlanItemAttributeEntity(att, pie);
				pie.getAttributes().add(attEntity);
			}
		}

		//  Create text entity.
		pie.setDescr(new PlanItemRichTextEntity(planItem.getDescr()));

		//  Set meta data.
		pie.setCreateId(context.getPrincipalId());
		pie.setCreateTime(new Date());
		pie.setUpdateId(context.getPrincipalId());
		pie.setUpdateTime(new Date());

		// Set credits
		if (planItem.getCredit() != null) {
			pie.setCredit(planItem.getCredit());
		}

		//  Set the learning plan.
		String planId = planItem.getLearningPlanId();
		if (planId == null) {
			throw new InvalidParameterException("Learning plan id was null.");
		}
		LearningPlanEntity plan = learningPlanDao.find(planItem.getLearningPlanId());
		if (plan == null) {
			throw new InvalidParameterException(String.format("Unknown learning plan id [%s]",
					planItem.getLearningPlanId()));
		}
		pie.setLearningPlan(plan);

		//  Save the new plan item.
		planItemDao.persist(pie);

		//  Update the metadata (timestamp, updated-by) on the plan.
		plan.setUpdateId(context.getPrincipalId());
		plan.setUpdateTime(new Date());
		learningPlanDao.update(plan);

		return planItemDao.find(planItemId).toDto();
	}

	@Override
	public PlanItemSetInfo createPlanItemSet(@WebParam(name = "planItemSet") PlanItemSetInfo planItemSet,
			@WebParam(name = "context") ContextInfo context) throws AlreadyExistsException,
			DataValidationErrorException, InvalidParameterException, MissingParameterException,
			OperationFailedException, PermissionDeniedException {
		throw new RuntimeException("Not implemented.");
	}

	/**
	 * 
	 * @param createNewPlanItem 
	 * @param attrSource
	 * @param attributeEntities
	 * @return
	 */
	private List<Attribute> mergeAttributes(boolean createNewPlanItem, HasAttributes attrSource,
			Set<? extends AttributeEntity> attributeEntities) {
		if (attrSource.getAttributes() == null)
			return null;

		Map<String, List<Attribute>> attributeMap = new LinkedHashMap<String, List<Attribute>>();
		for (Attribute att : attrSource.getAttributes()) {
			String key = att.getKey();
			List<Attribute> attl = attributeMap.get(key);
			if (attl == null)
				attributeMap.put(key, attl = new LinkedList<Attribute>());
			attl.add(att);
		}

		if (attributeEntities != null) {
			if (createNewPlanItem) {
				attributeEntities.clear();
			} else {
				Iterator<? extends AttributeEntity> ai = attributeEntities.iterator();
				while (ai.hasNext()) {
					AttributeEntity attrEntity = ai.next();
					String key = attrEntity.getKey();
					if (attributeMap.containsKey(key)) {
						List<Attribute> attl = attributeMap.get(key);
						if (attl.isEmpty()) {
							ai.remove();
						} else {
							Iterator<Attribute> atti = attl.iterator();
							Attribute att = null;
							while (att == null && atti.hasNext()) {
								Attribute attc = atti.next();
								if (attc.getId() != null && attc.getId().equals(attrEntity.getId())) {
									att = attc;
									atti.remove();
								}
							}
							if (att == null) {
								att = attl.remove(0);
							}
							attrEntity.setValue(att.getValue());
						}
						if (attl.isEmpty())
							attributeMap.remove(key);
					} else {
						ai.remove();
					}
				}
			}
		}

		List<Attribute> rv = new LinkedList<Attribute>();
		for (List<Attribute> attl : attributeMap.values())
			rv.addAll(attl);
		return rv;
	}

	@Override
	@Transactional
	public LearningPlanInfo updateLearningPlan(@WebParam(name = "learningPlanId") String learningPlanId,
			@WebParam(name = "learningPlan") LearningPlanInfo learningPlan,
			@WebParam(name = "context") ContextInfo context) throws DataValidationErrorException,
			InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException,
			DoesNotExistException {

		LearningPlanEntity lpe = learningPlanDao.find(learningPlanId);
		if (lpe == null) {
			throw new DoesNotExistException(learningPlanId);
		}

		LearningPlanTypeEntity type = learningPlanTypeDao.find(learningPlan.getTypeKey());
		if (type == null) {
			throw new InvalidParameterException(String.format("Unknown type [%s].", learningPlan.getTypeKey()));
		}
		lpe.setLearningPlanType(type);
		lpe.setStateKey(learningPlan.getStateKey());

		lpe.setStudentId(learningPlan.getStudentId());

		//  Update text entity.
		RichTextInfo descrInfo = learningPlan.getDescr();
		if (descrInfo == null) {
			lpe.setDescr(null);
		} else {
			LearningPlanRichTextEntity descr = lpe.getDescr();
			if (descr == null) {
				descr = new LearningPlanRichTextEntity(descrInfo);
			} else {
				descr.setPlain(descrInfo.getPlain());
				descr.setFormatted(descrInfo.getFormatted());
			}
		}

		//  Update attributes.
		List<Attribute> createAttrs = mergeAttributes(false, learningPlan, lpe.getAttributes());
		if (createAttrs != null) {
			Set<LearningPlanAttributeEntity> attributeEntities = lpe.getAttributes();
			if (attributeEntities == null) {
				lpe.setAttributes(attributeEntities = new HashSet<LearningPlanAttributeEntity>());
			}
			for (Attribute att : createAttrs)
				attributeEntities.add(new LearningPlanAttributeEntity(att, lpe));
		}

		//  Plan meta
		lpe.setUpdateId(context.getPrincipalId());
		lpe.setUpdateTime(new Date());
		lpe.setShared(learningPlan.getShared());

		learningPlanDao.merge(lpe);
		return learningPlanDao.find(learningPlanId).toDto();
	}

	@Override
	@Transactional
	public PlanItemInfo updatePlanItem(@WebParam(name = "planItemId") String planItemId,
			@WebParam(name = "planItem") PlanItemInfo planItem, @WebParam(name = "context") ContextInfo context)
			throws DoesNotExistException, DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException, PermissionDeniedException {

		//  See if the plan item exists before trying to update it.
		PlanItemEntity planItemEntity = planItemDao.find(planItemId);

		// If Plan type changes, create a new one and update the old one's state to DELETED
		String updatePlanTypeId = null;

		if (planItemEntity == null) {
			throw new DoesNotExistException(planItemId);
		}

		planItemEntity.setRefObjectId(planItem.getRefObjectId());
		planItemEntity.setRefObjectTypeKey(planItem.getRefObjectType());

		//  Update the plan item type if it has changed.
		boolean createNewPlanItem = false;
		if (!planItemEntity.getLearningPlanItemType().getId().equals(planItem.getTypeKey())
				&& planItemEntity.getLearningPlanItemType().getId()
						.equals(AcademicPlanServiceConstants.LEARNING_PLAN_ITEM_TYPE_WISHLIST)) {
			createNewPlanItem = true;
		}

		if (!planItemEntity.getLearningPlanItemType().getId().equals(planItem.getTypeKey())) {
			PlanItemTypeEntity planItemTypeEntity = planItemTypeDao.find(planItem.getTypeKey());
			if (planItemTypeEntity == null) {
				throw new InvalidParameterException(String.format("Unknown plan item type id [%s].",
						planItem.getTypeKey()));
			}

			// Reset the plan Item
			planItemEntity.setLearningPlanItemType(planItemTypeEntity);
			updatePlanTypeId = planItemEntity.getId();
		}

		//  Update plan periods.
		if (planItem.getPlanPeriods() != null) {
			//  Convert from List to Set.
			planItemEntity.setPlanPeriods(new HashSet<String>(planItem.getPlanPeriods()));
		}

		//  Update attributes.
		List<Attribute> createAttrs = mergeAttributes(createNewPlanItem, planItem, planItemEntity.getAttributes());
		if (createAttrs != null) {
			Set<PlanItemAttributeEntity> attributeEntities = planItemEntity.getAttributes();
			if (attributeEntities == null) {
				planItemEntity.setAttributes(attributeEntities = new HashSet<PlanItemAttributeEntity>());
			}
			for (Attribute att : createAttrs)
				attributeEntities.add(new PlanItemAttributeEntity(att, planItemEntity));
		}

		//  Update text entity.
		RichTextInfo descrInfo = planItem.getDescr();
		if (descrInfo == null) {
			planItemEntity.setDescr(null);
		} else {
			PlanItemRichTextEntity descr = planItemEntity.getDescr();
			if (descr == null) {
				descr = new PlanItemRichTextEntity(planItem.getDescr());
			} else {
				descr.setPlain(descrInfo.getPlain());
				descr.setFormatted(descrInfo.getFormatted());
			}
		}

		//  Update meta data.
		planItemEntity.setUpdateId(context.getPrincipalId());
		planItemEntity.setUpdateTime(new Date());

		//   If the the learning plan has changed update the plan item and update the meta data (update date, user) on the old plan.
		LearningPlanEntity originalPlan = learningPlanDao.find(planItem.getLearningPlanId());
		if (originalPlan == null) {
			throw new InvalidParameterException(String.format("Unknown learning plan id [%s]",
					planItem.getLearningPlanId()));
		}

		LearningPlanEntity newPlan = null;
		if (!planItemEntity.getLearningPlan().getId().equals(planItem.getLearningPlanId())) {
			String planId = planItem.getLearningPlanId();
			if (planId == null) {
				throw new InvalidParameterException("Learning plan id was null.");
			}
			newPlan = learningPlanDao.find(planItem.getLearningPlanId());
			if (newPlan == null) {
				throw new InvalidParameterException(String.format("Unknown learning plan id [%s]",
						planItem.getLearningPlanId()));
			}
			planItemEntity.setLearningPlan(newPlan);
		}

		// If plan type changes create a new one and delete
		String updatePlanItemId = null;
		if (createNewPlanItem) {
			try {
				PlanItemInfo newpiInfo = createPlanItem(planItemEntity.toDto(), context);
				updatePlanItemId = newpiInfo.getId();
			} catch (AlreadyExistsException e) {
				throw new OperationFailedException(e.getMessage());
			}
			deletePlanItem(updatePlanTypeId, context);
		} else {
			updatePlanItemId = planItemEntity.getId();
			planItemDao.merge(planItemEntity);
		}

		//  Update meta data on the original plan.
		originalPlan.setUpdateId(context.getPrincipalId());
		originalPlan.setUpdateTime(new Date());
		learningPlanDao.update(originalPlan);

		//  Update the new plan meta data if the plan changed.
		if (newPlan != null) {
			newPlan.setUpdateId(context.getPrincipalId());
			newPlan.setUpdateTime(new Date());
			learningPlanDao.update(newPlan);
		}

		// update credits
		if (planItem.getCredit() != null) {
			planItemEntity.setCredit(planItem.getCredit());
		}

		return planItemDao.find(updatePlanItemId).toDto();
	}

	@Override
	public PlanItemSetInfo updatePlanItemSet(@WebParam(name = "planItemSetId") String planItemSetId,
			@WebParam(name = "planItemSet") PlanItemSetInfo planItemSet, @WebParam(name = "context") ContextInfo context)
			throws AlreadyExistsException, DataValidationErrorException, InvalidParameterException,
			MissingParameterException, OperationFailedException, PermissionDeniedException {
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	@Transactional
	public StatusInfo deleteLearningPlan(@WebParam(name = "learningPlanId") String learningPlanId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException, PermissionDeniedException {
		StatusInfo status = new StatusInfo();
		status.setSuccess(Boolean.TRUE);

		LearningPlanEntity lpe = learningPlanDao.find(learningPlanId);
		if (lpe == null) {
			throw new DoesNotExistException(learningPlanId);
		}

		//  Delete plan items.
		List<PlanItemEntity> pies = planItemDao.getLearningPlanItems(learningPlanId);
		for (PlanItemEntity pie : pies) {
			planItemDao.remove(pie);
		}

		learningPlanDao.remove(lpe);

		return status;

	}

	@Override
	@Transactional
	public StatusInfo deletePlanItem(@WebParam(name = "planItemId") String planItemId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException, PermissionDeniedException {

		StatusInfo status = new StatusInfo();
		status.setSuccess(true);

		PlanItemEntity pie = planItemDao.find(planItemId);
		if (pie == null) {
			throw new DoesNotExistException(String.format("Unknown plan item id [%s].", planItemId));
		}

		planItemDao.remove(pie);

		return status;
	}

	@Override
	public StatusInfo deletePlanItemSet(@WebParam(name = "planItemSetId") String planItemSetId,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException, PermissionDeniedException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<ValidationResultInfo> validateLearningPlan(@WebParam(name = "validationType") String validationType,
			@WebParam(name = "learningPlanInfo") LearningPlanInfo learningPlanInfo,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {
		return new ArrayList<ValidationResultInfo>();
	}

	@Override
	public List<ValidationResultInfo> validatePlanItem(@WebParam(name = "validationType") String validationType,
			@WebParam(name = "planItemInfo") PlanItemInfo planItemInfo, @WebParam(name = "context") ContextInfo context)
			throws DoesNotExistException, InvalidParameterException, MissingParameterException,
			OperationFailedException, AlreadyExistsException {

		List<ValidationResultInfo> validationResultInfos = new ArrayList<ValidationResultInfo>();

		/*
		 * Validate that the course exists. TODO: Move this validation to the
		 * data dictionary.
		 */
		try {
			if (KsapFrameworkServiceLocator.getCourseHelper().getCourseInfo(planItemInfo.getRefObjectId()) == null) {
				validationResultInfos.add(makeValidationResultInfo(
						String.format("Could not find course with ID [%s].", planItemInfo.getRefObjectId()),
						"refObjectId", ValidationResult.ErrorLevel.ERROR));
			}
		} catch (RuntimeException e) {
			validationResultInfos.add(makeValidationResultInfo(e.getLocalizedMessage(), "refObjectId",
					ValidationResult.ErrorLevel.ERROR));
		}

		//  TODO: This validation should be implemented in the data dictionary when that possibility manifests.
		//  Make sure a plan period exists if type is planned course.
		if (planItemInfo.getTypeKey().equals(AcademicPlanServiceConstants.LEARNING_PLAN_ITEM_TYPE_PLANNED)
				|| planItemInfo.getTypeKey().equals(AcademicPlanServiceConstants.LEARNING_PLAN_ITEM_TYPE_BACKUP)) {
			if (planItemInfo.getPlanPeriods() == null || planItemInfo.getPlanPeriods().size() == 0) {
				validationResultInfos.add(makeValidationResultInfo(
						String.format("Plan Item Type was [%s], but no plan periods were defined.",
								planItemInfo.getTypeKey()), "typeKey", ValidationResult.ErrorLevel.ERROR));
			} else {
				//  Make sure the plan periods are valid. Note: There should never be more than one item in the collection.
				for (String atpId : planItemInfo.getPlanPeriods()) {
					boolean valid = false;
					try {
						valid = isValidTerm(atpId);
						if (!valid) {
							validationResultInfos.add(makeValidationResultInfo(
									String.format("ATP ID [%s] was not valid.", atpId), "atpId",
									ValidationResult.ErrorLevel.ERROR));
						}
					} catch (Exception e) {
						validationResultInfos.add(makeValidationResultInfo("ATP ID lookup failed.", "typeKey",
								ValidationResult.ErrorLevel.ERROR));
					}
				}
			}
		}

		/*
		 * Check for duplicate list items: Make sure a saved courses item with
		 * this course id doesn't already exist in the plan. Make sure a planned
		 * course item with the same ATP id doesn't exist in the plan.
		 * 
		 * Note: This validation is last to insure that all of the other
		 * validations are performed on "update" operations. The duplicate check
		 * throw an AlreadyExistsException on updates.
		 * 
		 * TODO: Maybe there is a better way to deal with validating udpates?
		 * 
		 * TODO: Move these validations to the data dictionary.
		 */
		checkPlanItemDuplicate(planItemInfo);

		return validationResultInfos;
	}

	@Override
	public List<ValidationResultInfo> validatePlanItemSet(@WebParam(name = "validationType") String validationType,
			@WebParam(name = "planItemInfo") PlanItemSetInfo planItemSetInfo,
			@WebParam(name = "context") ContextInfo context) throws DoesNotExistException, InvalidParameterException,
			MissingParameterException, OperationFailedException {
		return new ArrayList<ValidationResultInfo>();
	}

	/**
	 * @throws AlreadyExistsException
	 *             If the plan item is a duplicate.
	 */
	private void checkPlanItemDuplicate(PlanItemInfo planItem) throws AlreadyExistsException {

		String planItemId = planItem.getLearningPlanId();
		String courseId = planItem.getRefObjectId();
		String planItemType = planItem.getTypeKey();

		/**
		 * See if a duplicate item exits in the plan. If the type is wishlist
		 * then only the course id has to match to make it a duplicate. If the
		 * type is planned course then the ATP must match as well.
		 */
		List<PlanItemEntity> planItems = this.planItemDao.getLearningPlanItems(planItemId, planItemType);
		for (PlanItemEntity p : planItems) {
			if (p.getRefObjectId().equals(courseId)) {
				if (planItemType.equals(AcademicPlanServiceConstants.LEARNING_PLAN_ITEM_TYPE_PLANNED)
						|| planItemType.equals(AcademicPlanServiceConstants.LEARNING_PLAN_ITEM_TYPE_BACKUP)
						|| planItemType.equals(AcademicPlanServiceConstants.LEARNING_PLAN_ITEM_TYPE_CART)) {
					for (String atpId : planItem.getPlanPeriods()) {
						if (p.getPlanPeriods().contains(atpId)) {
							throw new AlreadyExistsException(String.format(
									"A plan item for plan [%s], course id [%s], and term [%s] already exists.", p
											.getLearningPlan().getId(), courseId, atpId));
						}
					}
				} else {
					throw new AlreadyExistsException(String.format(
							"A plan item for plan [%s] and course id [%s] already exists.",
							p.getLearningPlan().getId(), courseId));
				}
			}
		}
	}

	private ValidationResultInfo makeValidationResultInfo(String errorMessage, String element,
			ValidationResult.ErrorLevel errorLevel) {
		ValidationResultInfo vri = new ValidationResultInfo();
		vri.setError(errorMessage);
		vri.setElement(element);
		vri.setLevel(errorLevel);
		return vri;
	}

	private boolean isValidTerm(String atpId) {
		try {
			return KsapFrameworkServiceLocator.getTermHelper().getTerm(atpId) != null;
		} catch (Exception e) {
			throw new RuntimeException("Query to ATP service failed.", e);
		}
	}

}
