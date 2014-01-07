package com.sigmasys.kuali.ksa.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Information Access Level model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_ACCESS_LEVEL",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"CODE"}), @UniqueConstraint(columnNames = {"NAME"})})
@AttributeOverrides({
        @AttributeOverride(name = "code", column = @Column(name = "CODE", length = 45, nullable = false, unique = true)),
        @AttributeOverride(name = "name", column = @Column(name = "NAME", length = 45, nullable = false, unique = true))
})
public class InformationAccessLevel extends AuditableEntity<Long> {


    /**
     * Permission that is required to be able to create this level of information.
     */
    private String createPermission;

    /**
     * Permission that is required to be able to read this level of information.
     */
    private String readPermission;

    /**
     * Permission that is required to be able to update this level of information.
     */
    private String updatePermission;

    /**
     * Permission that is required to be able to delete this level of information.
     */
    private String deletePermission;

    /**
     * Permission that is required to be able to expire this level of information.
     */
    private String expirePermission;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "CREATE_PERMISSION", length = 45)
    public String getCreatePermission() {
        return createPermission;
    }

    public void setCreatePermission(String createPermission) {
        this.createPermission = createPermission;
    }

    @Column(name = "READ_PERMISSION", length = 45)
    public String getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(String readPermission) {
        this.readPermission = readPermission;
    }

    @Column(name = "UPDATE_PERMISSION", length = 45)
    public String getUpdatePermission() {
        return updatePermission;
    }

    public void setUpdatePermission(String updatePermission) {
        this.updatePermission = updatePermission;
    }

    @Column(name = "DELETE_PERMISSION", length = 45)
    public String getDeletePermission() {
        return deletePermission;
    }

    public void setDeletePermission(String deletePermission) {
        this.deletePermission = deletePermission;
    }

    @Column(name = "EXPIRE_PERMISSION", length = 45)
    public String getExpirePermission() {
        return expirePermission;
    }

    public void setExpirePermission(String expirePermission) {
        this.expirePermission = expirePermission;
    }
}
