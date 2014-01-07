package com.sigmasys.kuali.ksa.krad.controller;

import java.util.List;

import com.sigmasys.kuali.ksa.krad.util.KradTypeEntityKeyValuesFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sigmasys.kuali.ksa.service.ServiceTestSuite;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class AccountManagementControllerTest {

	
	@Test
	public void testCreateKRADOptionFinders() throws Exception {
		// NameType option finder:
		KeyValuesFinder finder = new KradTypeEntityKeyValuesFinder<EntityNameTypeBo>(EntityNameTypeBo.class);
		List<KeyValue> options = finder.getKeyValues();
		
		// AddressType option finder:
		finder = new KradTypeEntityKeyValuesFinder<EntityAddressTypeBo>(EntityAddressTypeBo.class);
		options = finder.getKeyValues();
		
		// EmailType option finder:
		finder = new KradTypeEntityKeyValuesFinder<EntityEmailTypeBo>(EntityEmailTypeBo.class);
		options = finder.getKeyValues();
		
		// PhoneType option finder:
		finder = new KradTypeEntityKeyValuesFinder<EntityPhoneTypeBo>(EntityPhoneTypeBo.class);
		options = finder.getKeyValues();
		
		finder = finder;
	}

}
