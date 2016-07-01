package com.castorama.utils;

import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_CODEPOSTAL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_FIRST_NAME;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_LAST_NAME;



public class CastProfileHelper {
	private enum Fields {
		LASTNAME, FIRSTNAME, POSTALCODE, ADDRESS1, ADDRESS2, ADDRESS3, LOCALITY, CITY;
	}
	
	public static String getEmptyFiledMessage (String fieldName) {
		Fields sign = Fields.valueOf(fieldName.toUpperCase());
		switch (sign) {
			case LASTNAME:
				return MSG_MISSED_LAST_NAME;
			case FIRSTNAME:
				return MSG_MISSED_FIRST_NAME;
			case POSTALCODE:
				return MSG_EMPTY_CODEPOSTAL;
			case ADDRESS1:
				
			default: throw new EnumConstantNotPresentException(Fields.class, sign.name());
			}
		
	}

}
