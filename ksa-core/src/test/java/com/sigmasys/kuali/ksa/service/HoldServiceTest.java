package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.service.hold.HoldService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.RichTextInfo;
import org.kuali.student.r2.core.constants.HoldServiceConstants;
import org.kuali.student.r2.core.hold.dto.AppliedHoldInfo;
import org.kuali.student.r2.core.hold.dto.HoldIssueInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * KS Hold service tests.
 *
 * @author Michael Ivanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class HoldServiceTest extends AbstractServiceTest {

    private static final String[] holdIssueTypes = {
            HoldServiceConstants.FINANCIAL_ISSUE_TYPE_KEY,
            HoldServiceConstants.DISCIPLINE_ISSUE_TYPE_KEY,
            Constants.HOLD_ISSUE_TYPE_FINANCIAL_OVERDUE_PERIOD1,
            Constants.HOLD_ISSUE_TYPE_FINANCIAL_OVERDUE_PERIOD2,
            Constants.HOLD_ISSUE_TYPE_FINANCIAL_OVERDUE_PERIOD3
    };

    @Autowired
    private HoldService holdService;

    @Autowired
    private AccountService accountService;

    private ContextInfo contextInfo;

    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
        contextInfo = new ContextInfo();
        contextInfo.setAuthenticatedPrincipalId(userId);
        contextInfo.setPrincipalId(userId);
    }


    @Test
    public void getHoldIssuesByType1() throws Exception {

        for (String holdIssueType : holdIssueTypes) {

            List<String> holdIssueIds = holdService.getHoldIssueIdsByType(holdIssueType, contextInfo);

            Assert.notNull(holdIssueIds);
            Assert.notEmpty(holdIssueIds);

            List<HoldIssueInfo> holdIssues = holdService.getHoldIssuesByIds(holdIssueIds, contextInfo);

            Assert.notNull(holdIssues);
            Assert.notEmpty(holdIssues);

            for (HoldIssueInfo holdIssue : holdIssues) {
                Assert.notNull(holdIssue);
                Assert.notNull(holdIssue.getId());
                Assert.notNull(holdIssue.getName());
                Assert.notNull(holdIssue.getTypeKey());
                Assert.notNull(holdIssue.getStateKey());
                Assert.notNull(holdIssue.getOrganizationId());
                Assert.notNull(holdIssue.getDescr());
                Assert.notNull(holdIssue.getDescr().getPlain());
                Assert.notNull(holdIssue.getMeta());
                Assert.notNull(holdIssue.getMeta().getCreateTime());
            }

        }

    }

    @Test
    public void getHoldIssuesByType2() throws Exception {

        for (String holdIssueType : holdIssueTypes) {

            List<String> holdIssueIds = holdService.getHoldIssueIdsByType(holdIssueType, contextInfo);

            Assert.notNull(holdIssueIds);
            Assert.notEmpty(holdIssueIds);

            for (String holdIssueId : holdIssueIds) {
                HoldIssueInfo holdIssue = holdService.getHoldIssue(holdIssueId, contextInfo);
                Assert.notNull(holdIssue);
                Assert.notNull(holdIssue.getId());
                Assert.notNull(holdIssue.getName());
                Assert.notNull(holdIssue.getTypeKey());
                Assert.notNull(holdIssue.getStateKey());
                Assert.notNull(holdIssue.getOrganizationId());
                Assert.notNull(holdIssue.getDescr());
                Assert.notNull(holdIssue.getDescr().getPlain());
                Assert.notNull(holdIssue.getMeta());
                Assert.notNull(holdIssue.getMeta().getCreateTime());
            }

        }

    }

    @Test
    public void getHoldIssuesByOrgId() throws Exception {

        final String[] organizationIds = {"1", "94"};

        for (String organizationId : organizationIds) {

            List<HoldIssueInfo> holdIssues = holdService.getHoldIssuesByOrg(organizationId, contextInfo);

            Assert.notNull(holdIssues);
            Assert.notEmpty(holdIssues);

            for (HoldIssueInfo holdIssue : holdIssues) {
                Assert.notNull(holdIssue);
                Assert.notNull(holdIssue.getId());
                Assert.notNull(holdIssue.getName());
                Assert.notNull(holdIssue.getTypeKey());
                Assert.notNull(holdIssue.getStateKey());
                Assert.notNull(holdIssue.getOrganizationId());
                Assert.notNull(holdIssue.getDescr());
                Assert.notNull(holdIssue.getDescr().getPlain());
                Assert.notNull(holdIssue.getMeta());
                Assert.notNull(holdIssue.getMeta().getCreateTime());
            }

        }

    }

    @Test
    public void createHoldIssue() throws Exception {

        for (String holdIssueType : holdIssueTypes) {

            HoldIssueInfo holdIssue = new HoldIssueInfo();
            holdIssue.setTypeKey(holdIssueType);
            holdIssue.setStateKey(HoldServiceConstants.ISSUE_ACTIVE_STATE_KEY);
            holdIssue.setName("Test issue");
            holdIssue.setOrganizationId("2");
            holdIssue.setDescr(new RichTextInfo("Plain description", "Formatted description"));

            holdService.createHoldIssue(holdIssueType, holdIssue, contextInfo);
        }

    }

    private List<String> createAppliedHoldsInternal() throws Exception {

        List<String> holdIds = new LinkedList<String>();

        for (String holdIssueType : holdIssueTypes) {

            List<String> holdIssueIds = holdService.getHoldIssueIdsByType(holdIssueType, contextInfo);

            Assert.notNull(holdIssueIds);
            Assert.notEmpty(holdIssueIds);

            for (String holdIssueId : holdIssueIds) {

                HoldIssueInfo holdIssue = holdService.getHoldIssue(holdIssueId, contextInfo);

                AppliedHoldInfo hold = new AppliedHoldInfo();
                hold.setHoldIssueId(holdIssueId);
                hold.setPersonId("admin");
                hold.setTypeKey(holdIssue.getTypeKey());
                hold.setStateKey(HoldServiceConstants.HOLD_ACTIVE_STATE_KEY);
                hold.setName("Test applied hold " + holdIssueId);
                hold.setEffectiveDate(new Date());
                hold.setReleasedDate(new Date(new Date().getTime() + 50 * 1000));
                hold.setDescr(new RichTextInfo("Plain description " + holdIssueId, "Formatted description " + holdIssueId));

                hold = holdService.createAppliedHold(hold.getPersonId(), hold.getHoldIssueId(),
                        hold.getTypeKey(), hold, contextInfo);

                Assert.notNull(hold);
                Assert.notNull(hold.getId());
                Assert.notNull(hold.getHoldIssueId());
                Assert.notNull(hold.getPersonId());
                Assert.notNull(hold.getName());
                Assert.notNull(hold.getMeta());
                Assert.notNull(hold.getMeta().getCreateTime());
                Assert.notNull(hold.getMeta().getCreateId());
                Assert.notNull(hold.getEffectiveDate());
                Assert.notNull(hold.getDescr());

                Assert.isTrue("admin".equals(hold.getPersonId()));
                Assert.isTrue(("Test applied hold " + holdIssueId).equals(hold.getName()));
                Assert.isTrue(("Plain description " + holdIssueId).equals(hold.getDescr().getPlain()));
                Assert.isTrue(("Formatted description " + holdIssueId).equals(hold.getDescr().getFormatted()));

                holdIds.add(hold.getId());

            }
        }

        return holdIds;
    }

    @Test
    public void createAppliedHolds() throws Exception {
        createAppliedHoldsInternal();
    }

    @Test
    public void getActiveAppliedHoldsByPerson() throws Exception {

        createAppliedHoldsInternal();

        List<AppliedHoldInfo> appliedHolds = holdService.getActiveAppliedHoldsByPerson("admin", contextInfo);

        Assert.notNull(appliedHolds);
        Assert.notEmpty(appliedHolds);

        for (AppliedHoldInfo hold : appliedHolds) {

            Assert.notNull(hold);
            Assert.notNull(hold.getId());
            Assert.notNull(hold.getHoldIssueId());
            Assert.notNull(hold.getPersonId());
            Assert.notNull(hold.getName());
            Assert.notNull(hold.getMeta());
            Assert.notNull(hold.getMeta().getCreateTime());
            Assert.notNull(hold.getMeta().getCreateId());
            Assert.notNull(hold.getEffectiveDate());
            Assert.notNull(hold.getDescr());

            Assert.isTrue("admin".equals(hold.getPersonId()));

        }

    }

    @Test
    public void getAppliedHoldsByIds() throws Exception {

        List<String> holdIds = createAppliedHoldsInternal();

        Assert.notNull(holdIds);
        Assert.notEmpty(holdIds);

        List<AppliedHoldInfo> appliedHolds = holdService.getAppliedHoldsByIds(holdIds, contextInfo);

        Assert.notNull(appliedHolds);
        Assert.notEmpty(appliedHolds);

        for (AppliedHoldInfo hold : appliedHolds) {

            Assert.notNull(hold);
            Assert.notNull(hold.getId());
            Assert.notNull(hold.getHoldIssueId());
            Assert.notNull(hold.getPersonId());
            Assert.notNull(hold.getName());
            Assert.notNull(hold.getMeta());
            Assert.notNull(hold.getMeta().getCreateTime());
            Assert.notNull(hold.getMeta().getCreateId());
            Assert.notNull(hold.getEffectiveDate());
            Assert.notNull(hold.getDescr());

            Assert.isTrue("admin".equals(hold.getPersonId()));

        }

    }

    @Test
    public void getAppliedHoldsByPerson() throws Exception {

        createAppliedHoldsInternal();

        List<AppliedHoldInfo> appliedHolds = holdService.getAppliedHoldsByPerson("admin", contextInfo);

        Assert.notNull(appliedHolds);
        Assert.notEmpty(appliedHolds);

        for (AppliedHoldInfo hold : appliedHolds) {

            Assert.notNull(hold);
            Assert.notNull(hold.getId());
            Assert.notNull(hold.getHoldIssueId());
            Assert.notNull(hold.getPersonId());
            Assert.notNull(hold.getName());
            Assert.notNull(hold.getMeta());
            Assert.notNull(hold.getMeta().getCreateTime());
            Assert.notNull(hold.getMeta().getCreateId());
            Assert.notNull(hold.getEffectiveDate());
            Assert.notNull(hold.getDescr());

            Assert.isTrue("admin".equals(hold.getPersonId()));

        }

    }


}
