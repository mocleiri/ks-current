package com.sigmasys.kuali.ksa.model.rule;

import com.sigmasys.kuali.ksa.model.Constants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * Knowledge base (Rule Set) JPA entity.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_RULE_SET")
public class RuleSet extends AbstractRuleEntity {

    /**
     * A set of rules belonging to this RuleSet
     */
    private Set<Rule> rules;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_RULE_SET_RULE",
            joinColumns = {
                    @JoinColumn(name = "RULE_SET_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "RULE_ID_FK")
            }
    )
    public Set<Rule> getRules() {
        return rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }
}
