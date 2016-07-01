package com.castorama.utils;

import atg.servlet.DynamoHttpServletRequest;

/**
* ServletTools : Castorama 2002<br>
* Class comportant les m�thodes utiles pour la manipulation des requetes
* @version 1.0  
* @author Damien DURIEZ - INTERNENCE (Decembre 2001) 
*/
public class ServletTools{
	
	/**
	* R�cup�ration de l'URI avec queryString, sans le param�tre _requestId g�n�r� par dynamo
	* @param DynamoHttpServletRequest a_Request.
	* @return String
	* @exception none
	*/
	public static String getURIWithQueryStringWithoutRequestId(DynamoHttpServletRequest a_Request){     
		String l_strURI = a_Request.getRequestURIWithQueryString();
		String l_strResultat = l_strURI;//AP add
		String l_strResultatTmp1;
		String l_strResultatTmp2;
		/* AP comment
		try
		{
			gnu.regexp.RE l_RE = new gnu.regexp.RE("\\?_requestid=[0-9]+\\&");
			l_strResultatTmp1=l_RE.substituteAll(l_strURI,"?");
			l_RE = new gnu.regexp.RE("&_requestid=[0-9]+");
			l_strResultatTmp2=l_RE.substituteAll(l_strResultatTmp1,"");
			l_RE = new gnu.regexp.RE("\\?_requestid=[0-9]+");
			l_strResultat=l_RE.substituteAll(l_strResultatTmp2,"");		
		}
		catch(gnu.regexp.REException e)
		{
			l_strResultat=l_strURI;
		}
		*/
		return l_strResultat;
		
	}
	
	
	/**
	* Ajout de query strings � une URL
	* @param		String	URL
	* @param		String	Nom du param�tre � ajouter
	* @param		String	Valeur du param�tre � ajouter
	* @return		String	Nouvelle valeur de l'URL
	* @exception	none
	*/
	public static String addQueryString(String a_strURL, String a_strParamKey, String a_strParamValue){     
		
		String l_strURL = a_strURL;
		if(l_strURL.indexOf("?")<=0)
            {
            l_strURL = l_strURL + "?" + a_strParamKey + "=" + a_strParamValue;
            }
		else					
            {
            l_strURL = l_strURL + "&" + a_strParamKey + "=" + a_strParamValue;
            }
		return l_strURL;
		
	}
	
}// fin de class
