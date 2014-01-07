package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Transfer Type.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TRANSFER_TYPE")
@AttributeOverride(name = "code", column = @Column(name = "CODE", length = 20, nullable = false, unique = true))
public class TransferType extends AuditableEntity<Long> {


    private GeneralLedgerType generalLedgerType;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GL_TYPE_ID_FK")
    public GeneralLedgerType getGeneralLedgerType() {
        return generalLedgerType;
    }

    public void setGeneralLedgerType(GeneralLedgerType generalLedgerType) {
        this.generalLedgerType = generalLedgerType;
    }
}
