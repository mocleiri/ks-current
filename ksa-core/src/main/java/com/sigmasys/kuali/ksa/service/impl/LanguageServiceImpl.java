package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.model.Language;
import com.sigmasys.kuali.ksa.model.Pair;
import com.sigmasys.kuali.ksa.model.SortOrder;
import com.sigmasys.kuali.ksa.service.LanguageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * Language Service implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("languageService")
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class LanguageServiceImpl extends GenericPersistenceService implements LanguageService {

    /**
     * Returns Language by ID
     *
     * @param id Language ID
     * @return Language instance
     */
    @Override
    public Language getLanguage(Long id) {
        return getEntity(id, Language.class);
    }

    /**
     * Returns Language by Locale string, for instance "fr_FR" or "en_US"
     *
     * @param locale Locale string
     * @return Locale instance
     */
    @Override
    public Language getLanguage(String locale) {
        Query query = em.createQuery("select l from Language l where l.locale = :locale)");
        query.setParameter("locale", locale);
        List<Language> languages = query.getResultList();
        if (languages != null && !languages.isEmpty()) {
            return languages.get(0);
        }
        throw new IllegalArgumentException("Locale '" + locale + "' does not exist");
    }

    /**
     * Returns all languages sorted by Locale in the ascending order
     *
     * @return List of languages
     */
    @Override
    public List<Language> getLanguages() {
        return getEntities(Language.class, new Pair<String, SortOrder>("locale", SortOrder.ASC));
    }

    /**
     * Persists the language in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param language Language instance
     * @return Language ID
     */
    @Override
    @Transactional(readOnly = false)
    public Long persistLanguage(Language language) {
        return persistEntity(language);
    }

    /**
     * Removes the language from the database.
     *
     * @param id Language ID
     * @return true if the Language entity has been deleted
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteLanguage(Long id) {
        return deleteEntity(id, Language.class);
    }

}
