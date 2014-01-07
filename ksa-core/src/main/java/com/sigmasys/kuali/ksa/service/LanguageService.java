package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.Language;

import java.util.List;

/**
 * Language Service
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface LanguageService {

    /**
     * Returns Language by ID
     *
     * @param id Language ID
     * @return Language instance
     */
    Language getLanguage(Long id);

    /**
     * Returns Language by Locale string, for instance "fr_FR" or "en_US"
     *
     * @param locale Locale string
     * @return Locale instance
     */
    Language getLanguage(String locale);

    /**
     * Returns all languages sorted by Locale in the ascending order
     *
     * @return List of languages
     */
    List<Language> getLanguages();

    /**
     * Persists the language in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param language Language instance
     * @return Language ID
     */
    Long persistLanguage(Language language);

    /**
     * Removes the language from the database.
     *
     * @param id Language ID
     * @return true if the Language entity has been deleted
     */
    boolean deleteLanguage(Long id);

}
