package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * This entity represents a "key-value" pair used by RateService
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_KEY_PAIR")
public class KeyPair implements Identifiable {


    /**
     * The unique identifier.
     */
    private Long id;

    /**
     * The name of the key in a pair.
     */
    private String key;

    /**
     * The value of the given key.
     */
    private String value;


    public KeyPair() {
    }

    public KeyPair(String key, String value) {
        this.key = key;
        this.value = value;
    }


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

    @Column(name = "KEY", nullable = false, length = 45)
    public String getKey() {
        return key;
    }

    @Column(name = "VALUE", length = 256)
    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyPair keyPair = (KeyPair) o;

        return !(key != null ? !key.equals(keyPair.key) : keyPair.key != null) && !(value != null ?
                !value.equals(keyPair.value) : keyPair.value != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
