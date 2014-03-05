package com.sigmasys.kuali.ksa.model.fm;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BigDecimalType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * A java.lang.Number extension used in Fee Management to store the number of units.
 *
 * @author Michael Ivanov
 */
public class UnitNumber extends Number implements UserType, Comparable<UnitNumber> {

    public static final UnitNumber ZERO = new UnitNumber(BigDecimal.ZERO);

    private BigDecimal unitNumber;

    public UnitNumber() {
        this(BigDecimal.ZERO);
    }

    public UnitNumber(double unitNumber) {
        this(new BigDecimal(unitNumber));
    }

    public UnitNumber(int unitNumber) {
        this(new BigDecimal(unitNumber));
    }

    public UnitNumber(BigDecimal unitNumber) {
        if (unitNumber != null) {
            this.unitNumber = unitNumber.setScale(2, RoundingMode.HALF_DOWN);
        }
    }

    public BigDecimal getValue() {
        return unitNumber;
    }

    public void setValue(BigDecimal unitNumber) {
        this.unitNumber = unitNumber;
    }

    public UnitNumber add(UnitNumber unitNumber) {
        return new UnitNumber(getValue().add(unitNumber.getValue()));
    }

    public UnitNumber subtract(UnitNumber unitNumber) {
        return new UnitNumber(getValue().subtract(unitNumber.getValue()));
    }

    public UnitNumber multiply(UnitNumber unitNumber) {
        return new UnitNumber(getValue().multiply(unitNumber.getValue()).setScale(2, RoundingMode.HALF_DOWN));
    }

    public UnitNumber divide(UnitNumber unitNumber) {
        return new UnitNumber(getValue().divide(unitNumber.getValue(), RoundingMode.HALF_DOWN));
    }

    @Override
    public int intValue() {
        return (getValue() != null) ? getValue().intValue() : 0;
    }

    @Override
    public long longValue() {
        return (getValue() != null) ? getValue().longValue() : 0;
    }

    @Override
    public float floatValue() {
        return (getValue() != null) ? getValue().floatValue() : 0;
    }

    @Override
    public double doubleValue() {
        return (getValue() != null) ? getValue().doubleValue() : 0;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.DECIMAL};
    }

    @Override
    public Class returnedClass() {
        return UnitNumber.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null && y == null) {
            return true;
        } else if (x != null) {
            return x.equals(y);
        } else {
            return y.equals(x);
        }
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        BigDecimal value = BigDecimalType.INSTANCE.nullSafeGet(rs, names[0], session);
        return new UnitNumber(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            BigDecimalType.INSTANCE.set(st, ((UnitNumber) value).getValue(), index, session);
        } else {
            BigDecimalType.INSTANCE.set(st, null, index, session);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (value instanceof Serializable) ? (Serializable) value : null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        BigDecimal objectValue = ((UnitNumber) object).getValue();

        if (getValue() != null && objectValue != null) {
            return getValue().compareTo(objectValue) == 0;
        } else if (getValue() == null && objectValue == null) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (getValue() != null) ? getValue().hashCode() : 0;
    }

    @Override
    public int compareTo(UnitNumber object) {

        if (getValue() == null && object.getValue() == null) {
            return 0;
        } else if (getValue() != null && object.getValue() != null) {
            return getValue().compareTo(object.getValue());
        }

        return (getValue() != null) ? 1 : -1;
    }

    @Override
    public String toString() {
        return (unitNumber != null) ? unitNumber.toString() : null;
    }
}
