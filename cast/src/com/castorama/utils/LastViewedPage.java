package com.castorama.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import atg.nucleus.GenericService;

public class LastViewedPage extends GenericService {

	private final static String ILLEGAL_SYMBOLS_REG = "[{\\:/}]";

	private final static String CHARSET_UTF_8 = "UTF-8";
	
	private static final String LESS_THAN = "&lt;";
    private static final String GREAT_THAN = "&gt;";
    private static final String LEFT_BRACKET = "&#40;";
    private static final String RIGHT_BRACKET = "&#41;";
    private static final String QUOT = "&quot;";
    
    private Map<String, String> stringsToReplace;

	private String lastVisitedUrl;

	private String searchQuestion;

	private String buildResultURL() {
		StringBuilder sb = new StringBuilder();
		try {
			lastVisitedUrl = URLDecoder.decode(lastVisitedUrl, CHARSET_UTF_8);
			searchQuestion = URLDecoder.decode(searchQuestion, CHARSET_UTF_8);
			restoreHtmlView();
			Object[] params = removeDuplicateParams(lastVisitedUrl);
			if (searchQuestion != null && !searchQuestion.isEmpty() && params.length != 0) {

				searchQuestion = URLDecoder.decode(searchQuestion, CHARSET_UTF_8);
				sb.append("/store/rechercher4/");
				sb.append(searchQuestion.replaceAll(ILLEGAL_SYMBOLS_REG, "")
						.replace('+', ' '));
				sb.append('?');
				sb.append(StringUtils.join(params, '&'));
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}

	private Object[] removeDuplicateParams(String decodedStr) {
		Set<String> resultParams = new HashSet<String>();
		if (null != decodedStr) {
			int firstMatch = decodedStr.indexOf("?");
			String parameters = new String(
					decodedStr.substring(firstMatch + 1));
			for (String param : parameters.split("&")) {
				resultParams.add(param);
			}
		}
		return resultParams.toArray();
	}
	
	private void restoreHtmlView(){
		for(String key : stringsToReplace.keySet()) {
			lastVisitedUrl = lastVisitedUrl.replaceAll(key, stringsToReplace.get(key));
			searchQuestion = searchQuestion.replaceAll(key, stringsToReplace.get(key));
		}
	}

	public boolean isPrevSearchResults() {
		if (null != lastVisitedUrl && !lastVisitedUrl.isEmpty()) {
			int firstMatch = lastVisitedUrl.indexOf("?");
			String uri = firstMatch == -1 ? null : lastVisitedUrl.substring(0,
					firstMatch);
			if (uri == null) {
				return false;
			}
			lastVisitedUrl = null;
			searchQuestion = null;
			return true;
		}
		return false;
	}

	public String getSearchQuestion() {
		return searchQuestion;
	}

	public void setSearchQuestion(String searchQuestion) {
		this.searchQuestion = searchQuestion;
	}

	public String getLastVisitedUrl() {
		return buildResultURL();
	}

	public void setLastVisitedUrl(String lastVisitedUrl) {
		this.lastVisitedUrl = lastVisitedUrl;
	}

	public Map<String, String> getStringsToReplace() {
		return stringsToReplace;
	}

	public void setStringsToReplace(Map<String, String> stringsToReplace) {
		this.stringsToReplace = stringsToReplace;
	}

}
