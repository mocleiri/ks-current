package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.service.atp.AtpService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.RichTextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * KS ATP service tests.
 *
 * @author Michael Ivanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class AtpServiceTest extends AbstractServiceTest {


    @Autowired
    private AtpService atpService;

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
    public void getAtpsByDates() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date startDate = dateFormat.parse("01/01/1970");
        Date endDate = dateFormat.parse("01/01/2020");

        List<AtpInfo> atpInfos = atpService.getAtpsByDates(startDate, endDate, contextInfo);

        Assert.notNull(atpInfos);
        Assert.notEmpty(atpInfos);
    }

    @Test
    public void getAtpsByIds() throws Exception {

        List<String> atpIds = Arrays.asList("19871", "19872", "19873", "19874");

        List<AtpInfo> atpInfos = atpService.getAtpsByIds(atpIds, contextInfo);

        Assert.notNull(atpInfos);
        Assert.notEmpty(atpInfos);
        Assert.isTrue(atpInfos.size() == atpIds.size());
    }

    @Test
    public void getAtpsByDateAndType() throws Exception {

        Date startDate = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse("03/31/1985");

        String atpType = "kuali.atp.type.Winter";

        List<AtpInfo> atpInfos = atpService.getAtpsByDateAndType(startDate, atpType, contextInfo);

        Assert.notNull(atpInfos);
        Assert.notEmpty(atpInfos);
    }

    @Test
    public void createAtp() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date startDate = dateFormat.parse("06/01/2012");
        Date endDate = dateFormat.parse("08/31/2012");

        String atpType = "kuali.atp.type.Summer_2012";

        AtpInfo atpInfo = new AtpInfo();
        atpInfo.setId("666-666-666");
        atpInfo.setCode("NEW ATP CODE #1");
        atpInfo.setName("Summer 2012");

        RichTextInfo textInfo = new RichTextInfo();
        textInfo.setPlain("New ATP Summer 2012");

        atpInfo.setStateKey("kuali.atp.state.Official");
        atpInfo.setDescr(textInfo);
        atpInfo.setStartDate(startDate);
        atpInfo.setEndDate(endDate);
        atpInfo.setAdminOrgId("admin");

        atpInfo = atpService.createAtp(atpType, atpInfo, contextInfo);

        Assert.notNull(atpInfo);
        Assert.notNull(atpInfo.getId());
        Assert.notNull(atpInfo.getCode());
        Assert.isTrue(atpInfo.getCode().equals("NEW ATP CODE #1"));

    }


}
