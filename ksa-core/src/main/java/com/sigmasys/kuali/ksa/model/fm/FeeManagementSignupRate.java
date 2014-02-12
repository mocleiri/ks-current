package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.Identifiable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Fee management signup rate model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_FM_SIGNUP_RATE")
public class FeeManagementSignupRate implements Identifiable {

    private Long id;

    private FeeManagementSignup signup;

    private Rate rate;

    private Boolean isComplete;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FM_SIGNUP_ID_FK")
    public FeeManagementSignup getSignup() {
        return signup;
    }

    public void setSignup(FeeManagementSignup signup) {
        this.signup = signup;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_ID_FK")
    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_COMPLETE")
    public Boolean isComplete() {
        return isComplete != null ? isComplete : false;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }
}
