package com.sigmasys.kuali.ksa.service.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a base class that is used as a search criteria in the search of JPA entities.
 *
 * @author Michael Ivanov
 *         Date: 4/8/12
 *         Time: 10:46 PM
 */
public class SearchCriteria extends AbstractSearchCriteria {

    private HashMap<String, Serializable> map;

    public SearchCriteria() {
        map = new HashMap<String, Serializable>();
    }

    public Set<Map.Entry<String, Serializable>> entrySet() {
        return map.entrySet();
    }

    public Serializable get(String key) {
        return map.get(key);
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public void remove(String key) {
        map.remove(key);
    }

    public void put(String key, Serializable value) {
        map.put(key, value);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public void putAll(SearchCriteria sc) {
        map.putAll(sc.map);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Serializable> entity : entrySet()) {
            sb.append(entity.toString());
            sb.append(";");
        }
        return sb.toString();
    }

    public SearchCriteria copy() {
        SearchCriteria sc = super.copyTo(new SearchCriteria());
        sc.putAll(this);
        return sc;
    }
}
