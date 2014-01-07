package com.sigmasys.kuali.ksa.krad.controller;


import com.sigmasys.kuali.ksa.service.AbstractServiceTest;
import com.sigmasys.kuali.ksa.service.ServiceTestSuite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class BatchRefundControllerTest extends AbstractServiceTest {


    @Autowired
    private BatchRefundController batchRefundController;

    @Before
    public void setUpWithinTransaction() {

    }

    @Test
    public void getCurrencyList() throws Exception {
//        // All potential refunds should be Credit transactions
//
//        // Passing request parameters needed to perform get() method
//        MockHttpServletRequest request = getRequest();
//
//        RefundForm form = batchRefundController.createInitialForm(request);
//
//        form.setNewAccount("user1");
//        ModelAndView modelAndView = batchRefundController.filterAccounts(form);
//
//        List<PotentialRefundModel> potentialModels = form.getPotentialRefundModels();
//
//        // Checking assertions
//        Assert.notNull(modelAndView);
//        Assert.notNull(form);
//
//        Assert.notNull(potentialModels);
//        Assert.isTrue(potentialModels.size() > 0);
//        for(PotentialRefundModel model : potentialModels) {
//            Assert.isTrue(model.getParentTransaction() instanceof Credit);
//        }
    }


}
