package com.sigmasys.kuali.ksa.model;

/**
 * This enumeration represents possible values of KeyPair class and subclass types. 
 * According to the current design, "K" for KeyPairs and "P" for PeriodKeyPairs.
 * 
 * @author Sergey
 *
 */
public enum KeyPairType {

	KEY_PAIR(KeyPairType.KEY_PAIR_CODE),
	PERIOD_KEY_PAIR(KeyPairType.PERIOD_KEY_PAIR_CODE);
	
	public static final String KEY_PAIR_CODE = "K";
	public static final String PERIOD_KEY_PAIR_CODE = "P";
	
	private String code;
	
	private KeyPairType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
