package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class LanguageServiceTest extends AbstractServiceTest {


    @Autowired
    private LanguageService languageService;

    private Long languageId;

    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        createLanguage(Locale.getDefault().toString());
    }

    @Test
    public void getLanguage() throws Exception {

        Language language = languageService.getLanguage(languageId);

        Assert.notNull(language);
        Assert.notNull(language.getId());

        Assert.isTrue(language.getId().equals(languageId));

        Assert.notNull(language.getDescription());
        Assert.isTrue("Default language".equals(language.getDescription()));

        Assert.notNull(language.getLocale());
        Assert.isTrue(Locale.getDefault().toString().equals(language.getLocale()));

    }

    @Test
    public void getLanguageByLocale() throws Exception {

        Language language = languageService.getLanguage(Locale.getDefault().toString());

        Assert.notNull(language);
        Assert.notNull(language.getId());

        Assert.isTrue(language.getId().equals(languageId));

        Assert.notNull(language.getDescription());
        Assert.isTrue("Default language".equals(language.getDescription()));

    }

    @Test
    public void getLanguages() throws Exception {

        List<Language> languages = languageService.getLanguages();

        Assert.notNull(languages);

        Assert.isTrue(!languages.isEmpty());

        for (Language language : languages) {
            Assert.notNull(language.getId());
            Assert.notNull(language.getLocale());
            Assert.notNull(language.getDescription());
        }

    }

    @Test
    public void updateLanguage() {

        Language language = languageService.getLanguage(Locale.getDefault().toString());

        Assert.notNull(language);
        Assert.notNull(language.getId());

        language.setDescription("Updated Description");

        Long id = languageService.persistLanguage(language);
        Assert.notNull(id);

        language = languageService.getLanguage(Locale.getDefault().toString());

        Assert.notNull(language);
        Assert.notNull(language.getId());

        Assert.isTrue(Locale.getDefault().toString().equals(language.getLocale()));
        Assert.isTrue("Updated Description".equals(language.getDescription()));

    }

    @Test
    public void createLanguage() {
        createLanguage("rr_RR");
    }


    private void createLanguage(String locale) {
        Language language = new Language();
        language.setLocale(locale);
        language.setDescription("Default language");
        languageId = languageService.persistLanguage(language);
        Assert.notNull(languageId);
    }


}
