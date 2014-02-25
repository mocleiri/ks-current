package org.kuali.student.ap.academicplan.infc;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.student.ap.academicplan.service.AcademicPlanServiceConstants;
import org.kuali.student.r2.common.infc.HasId;
import org.kuali.student.r2.common.infc.RichText;
import org.kuali.student.r2.common.infc.TypeStateEntity;

/**
 * A single plan item. Plan items can link to different entities in the system based on the refObjectType.
 *
 * @Author Kamal
 */
public interface PlanItem extends HasId, TypeStateEntity, TypedObjectReference {

    /**
     * A description of the Learning Plan Item
     * @name Description
     */
    public RichText getDescr();

    /**
     * Time Periods for which the item is planned for
     * @name  Time Periods ATPIds
     */
    public List<String> getPlanPeriods();

    /**
     * Containing learning plan
     * @name Learning Plan Id
     */
    public String getLearningPlanId();

    /**
    * The plan credits
    * @name The number of credits
    */
    public BigDecimal getCredit();

    /**
     * Category of item:  BOOKMARKED, BACKUP, PLANNED, CART,...
     * @return
     */
    public AcademicPlanServiceConstants.ItemCategory getCategory();

}
