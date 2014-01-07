package com.sigmasys.kuali.ksa.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.StringUtils;

/**
 * This entity represents a "key-value" pair used to perform tuition Fee Assessment.
 * 
 * @author Sergey
 */
@Entity
@Table(name = "KSSA_KYPR")
@DiscriminatorColumn(name = "TYPE")
@DiscriminatorValue(KeyPairType.KEY_PAIR_CODE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class KeyPair implements Identifiable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The unique identifier.
	 */
	private Long id;
	
	/**
	 * The name of the key in a pair.
	 */
	private String name;
	
	/**
	 * The value of the given key.
	 */
	private String value;
	
	/**
	 * If the key had a value before reassignment, the previous value.
	 */
	private String previousValue;
	

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_KEYPAIR",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "KEYPAIR_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_KEYPAIR")
	@Override
	public Long getId() {
		return id;
	}

    @Column(name="NAME", length=45)
	public String getName() {
		return name;
	}

    @Column(name="VALUE", length=256)
	public String getValue() {
		return value;
	}

    @Column(name="PREV_VALUE", length=256)
	public String getPreviousValue() {
		return previousValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		// Store the old value into the "previousValue" property:
		setPreviousValue(this.value);
		
		// Reassign the "value" property to the new value:
		this.value = value;
	}

	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean equals(Object o) {
		if (o instanceof KeyPair) {
			KeyPair kpAnother = (KeyPair)o;
			
			return (kpAnother.id != null) && (kpAnother.id.equals(this.id)) 
					&& StringUtils.equals(kpAnother.name, this.name)
					&& StringUtils.equals(kpAnother.value, this.value)
					&& StringUtils.equals(kpAnother.previousValue, this.previousValue);
					
		}
		
		return false;
	}
	
	public int hashCode() {
		return 31 * (((id != null) ? id.hashCode() : 0) +
				31 * ((StringUtils.isNotEmpty(this.name) ? name.hashCode() : 0) + 
				31 * (StringUtils.isNotEmpty(this.value) ? value.hashCode() : 0)));
	}
	
}
