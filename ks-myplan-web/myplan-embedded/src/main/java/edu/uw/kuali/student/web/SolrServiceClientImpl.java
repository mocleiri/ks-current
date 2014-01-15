package edu.uw.kuali.student.web;

import edu.uw.kuali.student.lib.client.studentservice.ServiceException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.ext.net.HttpClientHelper;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hemanthg
 * Date: 5/15/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolrServiceClientImpl implements SolrSeviceClient {

    private String solrBaseUrl;
    private SolrServer server;

    public String getSolrBaseUrl() {
        return solrBaseUrl;
    }

    public void setSolrBaseUrl(String solrBaseUrl) {
        this.solrBaseUrl = solrBaseUrl;
    }

    public SolrServiceClientImpl(String baseUrl) {
        setSolrBaseUrl(baseUrl);
        setServer(new HttpSolrServer(baseUrl));
    }

    public SolrServer getServer() {
        return server;
    }

    public void setServer(SolrServer server) {
        this.server = server;
    }

    /**
     * url: https://uwksdev01.cac.washington.edu/solr/section/select?q=section.id:"2013:2:CHEM:152:A"&sort=section.id%20asc&fl=section.data&wt=xml&indent=true&rows=9999
     *
     * @param id
     * @return
     */
    public String getSectionById(String id) throws ServiceException {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "section.id:\"" + id + "\"");
        params.set("fl", "section.data");
        params.set("sort", "section.id asc");
        params.set("rows", "9999");
        List<SolrDocument> documents = sendQuery(params);
        if (!CollectionUtils.isEmpty(documents)) {
            return documents.get(0).getFieldValue("section.data").toString();
        }
        return null;
    }

    /**
     * Syllabus fetched for given Id
     * url: http://localhost:9090/solr/section/select?q=section.id:%222013:4:ENGL:108:C%22%20AND%20section.syllabus:[1+TO+*]&fl=section.syllabus&wt=xml&indent=true
     *
     * @param id
     * @return
     * @throws ServiceException
     */
    @Override
    public String getSyllabusById(String id) throws ServiceException {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "section.id:\"" + id + "\"" + "AND section.syllabus:[1 TO *]");
        params.set("fl", "section.syllabus");
        params.set("sort", "section.id asc");
        params.set("rows", "9999");
        List<SolrDocument> documents = sendQuery(params);
        if (!CollectionUtils.isEmpty(documents)) {
            return documents.get(0).getFieldValue("section.syllabus").toString();
        }
        return null;
    }

    /**
     * eg: https://uwksdev01.cac.washington.edu/solr/section/select?q=section.year:2013%20AND%20section.term:spring%20AND%20section.curriculum.abbreviation:CHEM%20AND%20section.course.number:152%20AND%20section.primary:true&sort=section.id%20asc&fl=section.data&wt=xml&indent=true&rows=9999
     * returns xml response which has the
     *
     * @param year
     * @param term
     * @param curriculumAbbreviation
     * @param courseNumber
     * @return
     */
    public List<String> getPrimarySections(String year, String term, String curriculumAbbreviation, String courseNumber) throws ServiceException {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "section.year:" + year + " AND section.term:" + term + " AND section.curriculum.abbreviation:\"" + curriculumAbbreviation + "\" AND section.course.number:" + courseNumber + " AND section.primary:true NOT section.delete.flag:withdrawn");
        params.set("fl", "section.data");
        params.set("sort", "section.id asc");
        params.set("rows", "9999");
        List<SolrDocument> documents = sendQuery(params);
        List<String> sectionDataList = new ArrayList<String>();
        for (SolrDocument document : documents) {
            sectionDataList.add(document.getFieldValue("section.data").toString());
        }
        return sectionDataList;
    }

    /**
     * returns only the secondary sections excluding the primary section using the primarySectionId
     *
     * @param primarySectionId
     * @return
     * @throws ServiceException
     */
    public List<String> getSecondarySections(String primarySectionId) throws ServiceException {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "section.primary.id:\"" + primarySectionId + "\" AND section.primary:false");
        params.set("fl", "section.data");
        params.set("sort", "section.id asc");
        params.set("rows", "9999");
        List<SolrDocument> documents = sendQuery(params);
        List<String> sectionDataList = new ArrayList<String>();
        for (SolrDocument document : documents) {
            sectionDataList.add(document.getFieldValue("section.data").toString());
        }
        return sectionDataList;

    }

    /**
     * returns both primary and secondary sections by PrimarySectionId
     *
     * @param primarySectionId
     * @return
     * @throws ServiceException
     */
    public List<String> getPrimaryAndSecondarySections(String primarySectionId) throws ServiceException {
        /*TODO: Add a check to get only suspended and active sections*/
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "section.id:\"" + primarySectionId + "\" OR section.primary.id:\"" + primarySectionId + "\" NOT section.delete.flag:withdrawn");
        params.set("fl", "section.data");
        params.set("sort", "section.id asc");
        params.set("rows", "9999");
        List<SolrDocument> documents = sendQuery(params);
        List<String> sectionDataList = new ArrayList<String>();
        for (SolrDocument document : documents) {
            sectionDataList.add(document.getFieldValue("section.data").toString());
        }
        return sectionDataList;

    }

    /**
     * returns all the activityIds for given params
     *
     * @param year
     * @param term
     * @param curriculumAbbreviation
     * @return
     * @throws ServiceException
     */
    public List<String> getActivityIds(String year, String term, String curriculumAbbreviation) throws ServiceException {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "section.year:" + year + " AND section.term:" + term + " AND section.curriculum.abbreviation:\"" + curriculumAbbreviation + "\" AND section.primary:true NOT section.delete.flag:withdrawn");
        params.set("fl", "section.id");
        params.set("sort", "section.id asc");
        params.set("rows", "9999");
        List<SolrDocument> documents = sendQuery(params);
        List<String> activityIds = new ArrayList<String>();
        for (SolrDocument document : documents) {
            activityIds.add(document.getFieldValue("section.id").toString());
        }
        return activityIds;
    }

    /**
     * @param params
     * @return
     * @throws ServiceException
     */
    public List<SolrDocument> sendQuery(ModifiableSolrParams params) throws ServiceException {
        List<SolrDocument> documents = new ArrayList<SolrDocument>();
        try {
            server.ping();
            QueryResponse queryResponse = server.query(params);
            documents = queryResponse.getResults();

        } catch (Exception e) {
            throw new ServiceException("Could not read solr response.", e);
        }
        return documents;
    }


}