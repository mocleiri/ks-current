package org.kuali.student.cm.course.form;

import org.kuali.student.r2.lum.course.dto.LoDisplayInfo;

public class LoDisplayInfoWrapper extends LoDisplayInfo {

    private static final long serialVersionUID = 8232176748014317444L;

    private String searchBy;

    private String code;

    private String title;

    private String typeName;

    private String orgName;

    private String orgType;

    private String courseNumber;

    private int indentLevel = 0;

    private boolean selected;

    public LoDisplayInfoWrapper() {
    }

    public LoDisplayInfoWrapper(LoDisplayInfo info) {
        super(info);
    }

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public void indent() {
        indentLevel++;
    }

    public void outdent() {
        indentLevel--;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Used by the UI to determine if the indent widget should be active.
     * @return True if the widget should be active. Otherwise, false.
     */
    public boolean isIndentable() {
        return true;
    }

    /**
     * Used by the UI to determine if the unindent widget should be active.
     * @return True if the widget should be active. Otherwise, false.
     */
    public boolean isOutdentable() {
        return true;
    }

    /**
     * Used by the UI to determine if the "move up" widget should be active.
     * @return True if the widget should be active. Otherwise, false.
     */
    public boolean isMoveUpable() {
        return true;
    }

    /**
     * Used by the UI to determine if the "move down" widget should be active.
     * @return True if the widget should be active. Otherwise, false.
     */
    public boolean isMoveDownable() {
        return true;
    }

    /**
     * Used by the UI to determine if the indent widget should be active.
     * @return True if the widget should be active. Otherwise, false.
     */
    public boolean isDeleteable() {
        return true;
    }
}
