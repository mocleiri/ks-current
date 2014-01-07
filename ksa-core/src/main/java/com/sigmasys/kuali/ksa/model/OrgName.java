package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Organization Name model.
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(NameType.ORGANIZATION_CODE)
public class OrgName extends Name {

    /**
     * Organization Name
     */
    private String name;

    /**
     * Contact information
     */
    private PersonName contact;


    @Column(name = "ORG_NAME", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_NAME_ID_FK")
    public PersonName getContact() {
        return contact;
    }

    public void setContact(PersonName contact) {
        this.contact = contact;
    }

}
