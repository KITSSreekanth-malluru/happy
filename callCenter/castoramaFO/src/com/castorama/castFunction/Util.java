package com.castorama.castFunction;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;

import atg.core.util.StringUtils;

/**
 * Util methods to use like function in jsp. (see
 * WEB-INF\tags\cast\castFunction.tld)
 *
 * @author Alena Karpenkava
 * @author Vasili_Ivus
 */
public class Util {
    /**
     * Checks when collection contaions the object.
     *
     * @param  coll collection
     * @param  o    object
     *
     * @return true when contaions, false otherwise
     */
    public static boolean contains(Collection<?> coll, Object o) {
        return coll.contains(o);
    }

    /**
     * Removes object from collection.
     *
     * @param coll collection
     * @param o    object
     */
    public static void remove(Collection<?> coll, Object o) {
        coll.remove(o);
    }

    /**
     * Adds item to collection.
     *
     * @param coll collection
     * @param o    object
     */
    public static void add(Collection coll, Object o) {
        coll.add(o);
    }

    /**
     * Reverses the order of the objects in list.
     *
     * @param list list
     */
    public static void reverse(List list) {
        Collections.reverse(list);
    }

    /**
     * Reverses the order of the objects in array.
     *
     * @param arrayObject array
     */
    public static void reverseArray(Object[] arrayObject) {
        Collections.reverse(Arrays.asList(arrayObject));
    }

    /**
     * Checks when map contains object.
     *
     * @param  map map
     * @param  o   object
     *
     * @return true when contains, false otherwise
     */
    public static boolean contains(Map map, Object o) {
        return map.keySet().contains(o);
    }

    /**
     * Removes object from map.
     *
     * @param map map
     * @param o   object
     */
    public static void remove(Map map, Object o) {
        map.remove(o);
    }

    /**
     * Adds key value pair to map.
     *
     * @param map   map
     * @param o     key
     * @param value value
     */
    public static void add(Map map, Object o, Object value) {
        map.put(o, value);
    }
    
    /**
     * Adds map content to map.
     *
     * @param map   map
     * @param value value
     */
    public static void putAll(Map mapTo, Map mapFrom) {
        mapTo.putAll(mapFrom);
    }

    /**
     * Gest collection as list.
     *
     * @param  coll collection
     *
     * @return result list
     */
    public static List list(Collection<?> coll) {
        if (coll != null && coll.size() != 0)
        return new ArrayList(coll);
        return new ArrayList();
    }

    /**
     * Gets empty list
     *
     * @return result list
     */
    public static List column() {
        return new ArrayList();
    }

    /**
     * Translates a string into UTF-8.
     *
     * @param  url string to encode
     *
     * @return encoded url
     *
     * @throws JspTagException when unable to encode
     */
    public static String encode(String url) throws JspTagException {
        if (!StringUtils.isBlank(url)) {
            try {
                return java.net.URLEncoder.encode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            throw new JspTagException("Unable to encode URL");
        } else {
            return "";
        }
    }

    /**
     * Decodes a string using a UTF-8 specific encoding scheme.
     *
     * @param  url
     *
     * @return
     *
     * @throws JspTagException when unable to decode
     */
    public static String decode(String url) throws JspTagException {
        if (!StringUtils.isBlank(url)) {
            try {
                return java.net.URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            throw new JspTagException("Unable to decode URL");
        } else {
            return "";
        }
    }

    /**
     * Remove extension from filename like string.
     *
     * @param  value filename like string
     *
     * @return filename like string without extension
     */
    public static String cutExtention(String value) {
        String result = null;
        if (null != value) {
            int index = value.lastIndexOf(".");
            if (-1 == index) {
                result = value;
            } else {
                result = value.substring(0, index);
            }
        }
        return (null == result) ? value : result;
    }

    /**
     * Returns a view of the portion of this list between the specified
     * fromIndex, inclusive, and toIndex, exclusive.
     *
     * @param  listObject map map
     * @param  fromIndex  start index
     * @param  toIndex    end index
     *
     * @return portion of this list between the specified fromIndex, inclusive,
     *         and toIndex, exclusive.
     */
    public static List subList(Object listObject, Integer fromIndex, Integer toIndex) {
        List list = new ArrayList();
        if (listObject instanceof Map) {
            list = new ArrayList(((Map) listObject).values());
        } else if (listObject instanceof List) {
            list = (List) listObject;
        }

        return list.subList(fromIndex, toIndex);
    }
    
    /**
     * Gets array of the string
     *
     * @return result array
     */
    public static String[] split(String str){
    	return str.split(";");
    }
    
    /**
     * If the string ends with the specific suffics.
     *
     * @return result true or false
     */
    public static boolean endsWith(String str){
    	return str.endsWith("flv");
    }
    
    /**
     * Gets empty stringBuilder
     *
     * @return result stringBuilder
     */
    public static StringBuilder sb() {
        return new StringBuilder();
    }
    
    /**
     * appends the specified string
     */
    public static void string(StringBuilder sb, String str, Boolean comma) {
        sb.append(str);
        if(comma){
        	sb.append(",").append(" ");
        }
        else{
        	sb.append(" ");
        }
    }
    
    /**
     * stringBuilder to string
     *
     * @return string result
     */
    public static String toString(StringBuilder sb) {
        return sb.toString();
    }

    /**
     * Replaces each substring of str string that matches the regex with the given replacement.
     *
     * @return replaced string
     */
    public static String replaceAll(String str, String regex, String replacement) {
        return str.replaceAll(regex, replacement);
    }

}
