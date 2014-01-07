package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.service.xliff.Xliff;
import com.sigmasys.kuali.ksa.service.xliff.XliffParser;
import com.sigmasys.kuali.ksa.util.CommonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class XliffParserTest extends AbstractServiceTest {

    @Autowired
    private XliffParser xliffParser;

    @Test
    public void parseXliff() throws Exception {

        String content = CommonUtils.getResourceAsString("xliff/us-jp-xliff.xml");
        Xliff xliff = xliffParser.parse(content);

        Assert.notNull(xliff);
        Assert.notNull(xliff.getTransUnits());
        Assert.notEmpty(xliff.getTransUnits());

        Assert.notNull(xliff.getSourceLanguage());
        Assert.notNull(xliff.getSourceCountry());

        Assert.notNull(xliff.getTargetLanguage());
        Assert.notNull(xliff.getTargetCountry());

    }


}
