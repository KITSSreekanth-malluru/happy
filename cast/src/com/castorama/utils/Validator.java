package com.castorama.utils;

import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.core.util.ResourceUtils;

import atg.service.dynamo.LangLicense;

/**
 * Helper class for input validation.
 *
 * @author Katsiaryna_Dmitrievich
 */
public class Validator {
	private enum RegexChoice {
		NAME, FIELD;
	}
	
    /** Maximum length for email. */
    private static final int EMAIL_LENGTH = 50;

    /** Length for French phone. */
    private static final int PHONE_LENGTH_FR = 10;
    
    /** Maximum length for not French phone. */
    private static final int MAX_PHONE_LENGTH = 17;
    
    /** Minimum length for not French phone. */
    private static final int MIN_PHONE_LENGTH = 4;

    /** Resource Bundle Name. */
    private static final String RESOURCE_BUNDLE = "com.castorama.utils.ValidatorResource";

    /** Resource Bundle. */
    private static ResourceBundle sResourceBundle =
        ResourceUtils.getBundle(RESOURCE_BUNDLE, LangLicense.getLicensedDefault());

    /** Email regular expression. */
    private static final String EMAIL_REGEX = "emailRegex";

    /** Password regular expression. */
    private static final String PASSWORD_REGEX = "passwordRegex";

    /** Alpha Numeric regular expression. */
    private static final String ALPHA_NUMERIC_REGEX = "alphaNumericRegex";

    /** Numeric regular expression. */
    private static final String NUMERIC_REGEX = "numericRegex";

    /** City regular expression. */
    private static final String CITY_REGEX = "cityRegex";

    /** Date regular expression. */
    private static final String DATE_REGEX = "dateRegex";

    /** Alpha regular expression. */
    private static final String ALPHA_REGEX = "alphaRegex";
    
    /** French phone regular expression. */
    private static final String PHONE_REGEX_FR = "phoneRegexFR";
    
    /** No French phone regular expression. */
    private static final String PHONE_REGEX = "phoneRegex";
    
    /**
     * !!!!!!
     */
    private static final String FIELD_REGEX = "fieldRegex";
    
    private static final String NAME_REGEX = "nameRegex";
    
    private static final String CITY_REGEX_2 = "secondCityRegex"; 
    
    private static final String FORBIDDEN_SYMBOLS_FOR_CITY_REGEX = "forbiddenSymbolsForCityRegex";
    
    private static final String FORBIDDEN_SYMBOLS_FOR_NAME_REGEX = "forbiddenSymbolsForNameRegex";
    
    private static final String FORBIDDEN_SYMBOLS_REGEX = "forbiddenSymbolsRegex";

    /**
     * Validate <code>value</code> using <code>regex</code>
     *
     * @param  value
     * @param  regex
     *
     * @return <code>true</code> if <code>value</code> is matches <code>
     *         regex</code>
     */
    private static boolean validate(final String value, final String regex) {
        boolean result = true;
        String trimmedValue = value.trim();
        if (regex != null) {
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(trimmedValue);
            if (!matcher.matches()) {
                result = false;
            }
        }
        return result;
    }
    
     /**
     * Validate Email: characters (uppercase or lowercase) followed by @
     * followed by a text (uppercase or lowercase) followed by a dot followed by
     * a text (fr, com, uk, etc.).
     *
     * @param  email
     *
     * @return <code>true</code> if email is correct
     */
    public static boolean validateEmail(final String email) {
        if ((email != null) && (email.length() <= EMAIL_LENGTH)) {
            return validate(email, sResourceBundle.getString(EMAIL_REGEX));
        } else {
            return false;
        }
    }

    /**
     * Validate password: Alphanumeric. No space allowed.
     *
     * @param  password
     *
     * @return <code>true</code> if password is correct
     */
    public static boolean validatePassword(final String password) {
        return validate(password, sResourceBundle.getString(PASSWORD_REGEX));
    }

    /**
     * Validate AlphaNumeric: AlphaNumeric + space.
     *
     * @param  value
     *
     * @return <code>true</code> if value is correct
     */
    public static boolean validateAlphaNumeric(final String value) {
        return validate(value, sResourceBundle.getString(ALPHA_NUMERIC_REGEX));
    }

    /**
     * Validate Alpha: only letters.
     *
     * @param  value
     *
     * @return <code>true</code> if value is correct
     */
    public static boolean validateAlpha(final String value) {
        return validate(value, sResourceBundle.getString(ALPHA_REGEX));
    }

    /**
     * Validate Numeric: only numeral.
     *
     * @param  value
     *
     * @return <code>true</code> if value is correct
     */
    public static boolean validateNumeric(final String value) {
        return validate(value, sResourceBundle.getString(NUMERIC_REGEX));
    }

    /**
     * Validate City: Alphabetic, Hyphen -, Apostrophe ’.
     *
     * @param  value
     *
     * @return <code>true</code> if value is correct
     */
    public static boolean validateCity(final String value) {
        return validate(value, sResourceBundle.getString(CITY_REGEX));
    }

    /**
     * Validate Phone: 10 digits when country is "France",
     * 4-16 digits or 4-15 digits including character "+" at the beginning
     * for the other countries
     * 
     * @param  phone
     * @param  isFrance
     * 
     * @return <code>true</code> if phone is correct
     */
    public static boolean validatePhone(final String phone, final boolean isFrance) {
    	if(isFrance) {
    		if (phone.length() == PHONE_LENGTH_FR) {
    			return validate(phone, sResourceBundle.getString(PHONE_REGEX_FR));
    		} else {
    			return false;
    		}
    	} else {
    		if ((phone.length() >= MIN_PHONE_LENGTH) && (phone.length() <= MAX_PHONE_LENGTH)) {
    			return validate(phone, sResourceBundle.getString(PHONE_REGEX));
    		} else {
    			return false;
    		}
    	}
    }

    /**
     * Validate Date: should be date in past.
     *
     * @param  date
     *
     * @return <code>true</code> if date is in past
     */
    public static boolean validateDateBefore(final Date date) {
        boolean result = true;
        Date today = new Date();
        if (today.before(date)) {
            result = false;
        }
        return result;
    }

    /**
     * Validate Date: ddmmyyyy format
     *
     * @param  date
     *
     * @return <code>true</code> if date is correct
     */
    public static boolean validateDate(final String date) {
        return validate(date, sResourceBundle.getString(DATE_REGEX));
    }
    
    /*public static char[] validateField(final String field) {
    	
    }*/
    
    /**
     * Validates name. Using for validation first name and last name (Prenom and nom) according next rule:
     * first name and last name shouldn't include characters % *  \ /  | [ ]  (  )  ,  .  ;     {  } +  =  #  ~  & �
     * @param name		name to validate 
     * @return			true, if name doesn't contain illegal symbols
     */
    public static boolean validateName(final String name) {
    	return validate(name, sResourceBundle.getString(NAME_REGEX));
    }
    
    /**
     * Validates address fields such as '№, voie', 'Etage, appartment', 'Batiment',
     * 'Lieu-dit'. 
     * @param field		field to validate
     * @return			true, if filed doesn't contain % *  \   |  [ ]  (  )    {  } +  =  #  ~  &  
     */
    public static boolean validateField(final String field) {
    	return validate(field, sResourceBundle.getString(FIELD_REGEX));
    }
    
    /**
     * Validates zip code. Zip code must only consist of digits
     * @param zipCode
     * @return
     */
    /*public static boolean validateZipCode(final String zipCode) {
    	return validateNumeric(zipCode);
    } */
    
    /*public static boolean validateCity2( final String city) {
    	return validate(city, sResourceBundle.getString(CITY_REGEX_2));
    } */
    
    /**
     * Finds symbols which matches regular expression in tested string
     * @param testedString		string for symbols search
     * @param regexType
     * @return					string with all symbols form regular expression which was found in tested string
     */
    public static String getIncludedSymbols(String testedString, String regexType) {
    	String includedSymbols = "";
    	String regex = getForbiddenRegex(regexType);
    	final Pattern pattern = Pattern.compile( sResourceBundle.getString(regex));
        final Matcher matcher = pattern.matcher(testedString);
        
        while (matcher.find()) {
        	String foundSymbol = matcher.group();
          	if ( !includedSymbols.contains(foundSymbol)) {
                //foundSymbol = foundSymbol.replaceAll("(\\s)+", "(space)");
                includedSymbols = includedSymbols.concat(" " + foundSymbol);
        	}
        }
        if (includedSymbols.equals("")) {
        	return null;
        }
    	return includedSymbols;
     }
    
    private static String getForbiddenRegex(String type) {
    	RegexChoice sign = RegexChoice.valueOf(type.toUpperCase());
		switch (sign) {
			case NAME:
				return FORBIDDEN_SYMBOLS_FOR_NAME_REGEX;
			case FIELD:
				return FORBIDDEN_SYMBOLS_REGEX;
			default: throw new EnumConstantNotPresentException(RegexChoice.class, sign.name());
			}
    }
}
