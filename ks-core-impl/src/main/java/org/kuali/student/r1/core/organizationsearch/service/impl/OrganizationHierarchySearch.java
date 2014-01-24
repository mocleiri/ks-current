package org.kuali.student.r1.core.organizationsearch.service.impl;

import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.core.organization.dao.OrgDao;
import org.kuali.student.r2.core.organization.dao.OrgOrgRelationDao;
import org.kuali.student.r2.core.organization.infc.Org;
import org.kuali.student.r2.core.organization.infc.OrgOrgRelation;
import org.kuali.student.r2.core.organization.model.OrgEntity;
import org.kuali.student.r2.core.organization.model.OrgOrgRelationEntity;
import org.kuali.student.r2.core.organization.service.OrganizationService;
import org.kuali.student.r2.core.search.dto.SearchParamInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.dto.SearchResultRowInfo;
import org.kuali.student.r2.core.search.dto.SortDirection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a description of what this class does - pctsw don't forget to fill this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OrganizationHierarchySearch implements OrganizationSearch {

    private OrgDao orgDao;
    private OrgOrgRelationDao orgOrgRelationDao;

    public OrganizationHierarchySearch() {
        super();
    }

    public OrgDao getOrgDao() {
        return orgDao;
    }

    public void setOrgDao(OrgDao orgDao) {
        this.orgDao = orgDao;
    }

    public OrgOrgRelationDao getOrgOrgRelationDao() {
        return orgOrgRelationDao;
    }

    public void setOrgOrgRelationDao(OrgOrgRelationDao orgOrgRelationDao) {
        this.orgOrgRelationDao = orgOrgRelationDao;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.student.r1.core.organizationsearch.service.impl.OrganizationSearch#search(org.kuali.student.r2.core.search.dto.SearchRequestInfo)
     */
    @Override
    public SearchResultInfo search(SearchRequestInfo searchRequest) {

        List<String> relatedOrgIds = null;
        List<String> orgTypes = null;
        String relationTypeKey = null;
        String orgOptionalId = null;
        String sortColumn = searchRequest.getSortColumn();
        SortDirection sortDirection = searchRequest.getSortDirection();

        for (SearchParamInfo param : searchRequest.getParams()) {
            if ("org.queryParam.relatedOrgIds".equals(param.getKey())) {
                relatedOrgIds = (List<String>) param.getValues();
                continue;
            } else if ("org.queryParam.optionalOrgTypeList".equals(param.getKey())) {
                orgTypes = (List<String>) param.getValues();
                continue;
            } else if ("org.queryParam.optionalRelationType".equals(param.getKey())) {
                relationTypeKey = (String) param.getValues().get(0);
                continue;
            } else if ("org.queryParam.relOrgOptionalId".equals(param.getKey())) {
                orgOptionalId = (String) param.getValues().get(0);
                continue;
            }
        }
        try {
            List<Org> orgs = null;
            if (orgOptionalId != null) {
                orgs = new ArrayList<Org>();
                OrgEntity entity = orgDao.find(orgOptionalId);
                if(entity != null) {
                    orgs.add(entity.toDto());
                }
            } else {
                orgs = doOrgHierarchySearch(relatedOrgIds, orgTypes, relationTypeKey);
            }

            // Create a search result for the return value
            SearchResultInfo searchResult = new SearchResultInfo();
            searchResult.setSortColumn(sortColumn);
            searchResult.setSortDirection(sortDirection);
            for (Org org : orgs) {
                SearchResultRowInfo resultRow = new SearchResultRowInfo();

                // Map the result cells
                resultRow.addCell("org.resultColumn.orgId", org.getId());
                resultRow.addCell("org.resultColumn.orgShortName", org.getShortName());
                resultRow.addCell("org.resultColumn.orgOptionalLongName", org.getLongName());
                resultRow.addCell("org.resultColumn.orgType", org.getTypeKey());

                searchResult.getRows().add(resultRow);
            }

            searchResult.sortRows();

            return searchResult;
        } catch (DoesNotExistException e) {
            throw new RuntimeException("Error performing search");
        }

    }

    /**
     * This method ...
     *
     * @param relatedOrgIds
     * @param orgTypes
     * @param relationTypeKey
     * @return
     * @throws org.kuali.student.r2.common.exceptions.MissingParameterException
     * @throws org.kuali.student.r2.common.exceptions.DoesNotExistException
     */
    private List<Org> doOrgHierarchySearch(List<String> relatedOrgIds, List<String> orgTypes, String relationTypeKey) throws DoesNotExistException {

        // Loop thru org types and related org ids to add the ancestors one by one.
        Set<Org> orgs = new HashSet<Org>();
        for (String orgTypeKey : orgTypes) {
            for (String relatedOrgId : relatedOrgIds) {
                Org childOrg = null;
                OrgEntity entity = orgDao.find(relatedOrgId);
                if(entity != null) {
                    childOrg = entity.toDto();
                } else {
                    continue;
                }
                Org parentOrg = findParentOrgForType(childOrg, orgTypeKey, relationTypeKey);

                // Add parent Org to list
                if (parentOrg != null) {
                    orgs.add(parentOrg);
                }
            }
        }

        return new ArrayList<Org>(orgs);
    }

    /**
     * This method recursively looks for a parent organisation of a given type.
     * 
     * @param org
     * @param orgTypeKey
     * @param relationTypeKey
     * @return Org
     */
    private Org findParentOrgForType(Org org, String orgTypeKey, String relationTypeKey) {
        // If the org type is not found, return null
        if (org == null) {
            return null;
        }
        // Return org if type is of given type
        if (org.getTypeKey().equals(orgTypeKey)) {
            return org;
        }
        // Recursive call
        List<OrgOrgRelationEntity> relations = orgOrgRelationDao.getOrgOrgRelationsByTypeAndRelatedOrg(relationTypeKey, org.getId());
        if(relations != null && relations.size() >= 0) {
            for(OrgOrgRelationEntity relation : relations) {
                OrgEntity entity = orgDao.find(relation.getOrgId());
                Org parent = findParentOrgForType(entity.toDto(), orgTypeKey, relationTypeKey);
                if(parent != null) {
                    return parent;
                }
            }

        }
        return null;
    }

}
