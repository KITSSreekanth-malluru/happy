package com.castorama.droplet;

import java.text.BreakIterator;
import java.util.Locale;

/**
 * @author EPAM
 * @author philippemouawad
 * 
 */
public class ProductDescriptionDisplayerProcessor {

    private static final String MORE_CONTENT = "...";

    /** OPEN_TAG_BR constant. */
    private static final String OPEN_TAG_BR = "&bull";
    
    /** CLOSE_TAG_BR constant. */
    private static final String CLOSE_TAG_BR = "<br/>";
    
    /** OPEN_TAG_LI constant. */
    private static final String OPEN_TAG_LI = "<li>";
    
    /** CLOSE_TAG_LI constant. */
    private static final String CLOSE_TAG_LI = "</li>";

    /** maxTextLength property. */
    private int maxTextLength;
        
    public ProductDescriptionDisplayerProcessor(int maxTextLength) {

        this.maxTextLength = maxTextLength;
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }
    
    public String compute(String descr) {
        String cutDescr = descr.substring(0, Math.min(getMaxTextLength(), descr.length()));
        String cutDescrInLowerCase = cutDescr.toLowerCase();
        String result = null;
        if (cutDescrInLowerCase.lastIndexOf(OPEN_TAG_BR) >= 0) {
            result = parseDescription(cutDescr, descr, OPEN_TAG_BR, CLOSE_TAG_BR);
        } else if (cutDescrInLowerCase.lastIndexOf(OPEN_TAG_LI) >= 0) {
            result = parseDescription(cutDescr, descr, OPEN_TAG_LI, CLOSE_TAG_LI);
        } 
        if(result == null) {
            if (descr.length() > getMaxTextLength()) {
                result = cutAtBoundary(descr);
                if(result.length()<=getMaxTextLength()) {
                    return result;
                } else {
                    result = result.substring(0,getMaxTextLength()-3).concat(ProductDescriptionDisplayerProcessor.MORE_CONTENT); //$NON-NLS-1$
                }
            } else {
                result = descr;
            }
        }        
        return result;
    } 
    
    /**
     * Parses description
     * @param cutDescription cut description at MaxLength
     * @param descr complete description used to cut at boundary
     * @param openTag tags &bull or <li> supposed to be in cutDescription
     * @param closeTag </br> or </li>
     * @return cut description as per business rules
     */
    private String parseDescription(String cutDescription, String descr, String openTag, String closeTag){  
        
        String result = null;
        String cutDescrInLowerCase = cutDescription.toLowerCase();
        
        int lastIndexOfOpenTag = cutDescrInLowerCase.lastIndexOf(openTag);
        if(lastIndexOfOpenTag== -1) {
            throw new IllegalArgumentException(openTag+" is not in "+cutDescrInLowerCase+" as expected by this method");
        }
        
        if (cutDescrInLowerCase.lastIndexOf(closeTag) > lastIndexOfOpenTag) {
            if (cutDescrInLowerCase.endsWith(closeTag)) {
                result = cutDescription;
            } else {
                if(cutDescription.length()==getMaxTextLength()) {
                    result = cutAtBoundary(descr);
                    if(result.length()>getMaxTextLength()) {
                        result = result.substring(0,getMaxTextLength()-3).concat(ProductDescriptionDisplayerProcessor.MORE_CONTENT); //$NON-NLS-1$
                    }
                } else {
                    return cutDescription;
                }
            }
        } else if (lastIndexOfOpenTag > 0){ 
            result = cutDescription.substring(0, lastIndexOfOpenTag);
        } else {
            // No openTag in
            result = null;
        }
        return result;
    }

    /**
     * @param descr Description
     * @return String returns cut description if > MaxLenght or all if not
     */
    private String cutAtBoundary(String descr) {
        String result = null;
        BreakIterator boundary = BreakIterator.getWordInstance(Locale.FRENCH);
        boundary.setText(descr);
        int index = boundary.following(getMaxTextLength());
        if(index>=0) {  
            result = descr.substring(0, index);
        } else {
            result = descr;
        }
        return result;
    } 
}
