package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.LocalizedString;
import com.sigmasys.kuali.ksa.model.LocalizedStringId;
import com.sigmasys.kuali.ksa.util.CommonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class LocalizationWebServiceTest extends AbstractServiceTest {

    @Autowired
    @Qualifier("localizationWsClient")
    private LocalizationService localizationService;

    @Test
    public void importResources() {

        String xliff = CommonUtils.getResourceAsString("xliff/us-jp-xliff.xml");

        List<LocalizedString> strings = localizationService.importResources(xliff, LocalizationService.ImportType.FULL);

        Assert.notNull(strings);
        Assert.notEmpty(strings);

        for (LocalizedString string : strings) {

            String id = string.getId().getId();

            System.out.println("Retrieved LocalizedString: id = " + id + ", value = " + string);

            Assert.notNull(string);
            Assert.notNull(string.getId());
            Assert.isTrue(id.equals(string.getId().getId()));
            Assert.isTrue(new LocalizedStringId(id, "ja_JP").equals(string.getId()));
            Assert.notNull(string.getOverridden());
            Assert.notNull(string.getValue());
            Assert.isTrue(!string.getValue().trim().isEmpty());

        }


    }

    // TODO: implement more tests

}
