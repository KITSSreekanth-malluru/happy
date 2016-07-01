/**
 *
 */
package com.castorama.mobile.droplet;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.core.util.StringUtils;

import atg.droplet.TagAttributeDescriptor;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverter;

import atg.servlet.DynamoHttpServletRequest;

/**
 * Convert phone number.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class PhoneNumberConverter implements TagConverter {
    /** CHARACHTERS_TO_REMOVE constant. */
    private static final String CHARACHTERS_TO_REMOVE = "().";

    /** PATTERN constant. */
    private static final String PATTERN = "\\d{1,2}";

    /** WHITE_SPACE constant. */
    private static final String WHITE_SPACE = " ";

    /** NAME constant. */
    public static final String NAME = "phoneNumber";

    /** VALUES_TO_SUBSTRING constant. */
    public static final String VALUES_TO_SUBSTRING = "valuesToSubstring";

    /** VALUES_IN_GROUP constant. */
    public static final String VALUES_IN_GROUP = "group";

    /** sTagAttributeDescriptors property */
    private static final TagAttributeDescriptor[] sTagAttributeDescriptors = {};

    /**
     * Converts Object to String.
     *
     * @param  pRequest    parameter
     * @param  pValue      parameter
     * @param  pAttributes parameter
     *
     * @return converted to string object
     *
     * @throws TagConversionException exception
     */
    public String convertObjectToString(DynamoHttpServletRequest pRequest, Object pValue, Properties pAttributes)
                                 throws TagConversionException {
        StringBuilder sb = new StringBuilder();
        if (pValue != null) {
            String valueStr = pValue.toString();
            valueStr = StringUtils.removeCharacters(valueStr, CHARACHTERS_TO_REMOVE);
            valueStr = StringUtils.removeWhiteSpace(valueStr);
            if (valueStr.length() > 3) {
                valueStr = valueStr.substring(3);

                Pattern pattern = Pattern.compile(PATTERN);
                Matcher matcher = pattern.matcher(valueStr);

                while (matcher.find()) {
                    String group = matcher.group();
                    sb = sb.append(group);
                    if (!matcher.hitEnd()) {
                        sb = sb.append(WHITE_SPACE);
                    }

                }
            }

        }  // end if

        return sb.toString();
    }

    /**
     * Converts String to Object.
     *
     * @param  pRequest    parameter
     * @param  pValue      parameter
     * @param  pAttributes parameter
     *
     * @return converted to Object
     *
     * @throws TagConversionException exception.
     */
    public Object convertStringToObject(DynamoHttpServletRequest pRequest, String pValue, Properties pAttributes)
                                 throws TagConversionException {
        return pValue;
    }

    /**
     * Returns name property.
     *
     * @return name property.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Returns tagAttributeDescriptors property.
     *
     * @return tagAttributeDescriptors property.
     */
    public TagAttributeDescriptor[] getTagAttributeDescriptors() {
        return sTagAttributeDescriptors;
    }

}
