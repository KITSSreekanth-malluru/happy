package com.castorama;

import java.util.*;
import atg.servlet.*;

/**
* Cette classe est la classe SUCookieManager du site Systeme-U.
* Elle permet de r�cup�rer les informations �crites dans les cookies
* du site Systeme-U mais �galement d'�crire dans ces cookies.
* Elle permet de s�curiser les cookies gr�ce � un cryptage �volu�.
* @version 1.0  
* @author Thomas PINTO - D�veloppeur INTERNENCE.COM (Juillet 2001) 
*/

public class CookieManager {
/**
* Constructeur vide.
* @param none 
* @throws none
*/
	
	public CookieManager () {
	}

//-------------------------------------------------------------------------------------------------
	public int getIntCookie(String ParameterName,DynamoHttpServletRequest req,int DefaultInt){
 		//trace.logOpen(this,".getIntCookie");
		int entier=-1;
		try{
			String Chaine;
			javax.servlet.http.Cookie[] cookies = req.getCookies();
			if (cookies != null){
				for (int i=0; i<cookies.length; i++){
					if (cookies[i].getName().equals(ParameterName) ){
						Chaine = cookies[i].getValue();
		 				entier = Integer.valueOf(Chaine.trim()).intValue();
		 				continue;
					}
				}
			}
		}catch (NumberFormatException e){
			//trace.logError(this,e,".getIntCookie Exception : "+e.toString());
			entier=-1;
		}catch(Exception e){
			//trace.logError(this,e,".getIntCookie Exception : "+e.toString());
			entier=-1;
		}finally{
			//trace.logClose(this,".getIntCookie");
		}
		return entier;
	}

//-------------------------------------------------------------------------------------------------
	public String getStringCookie(String ParameterName,DynamoHttpServletRequest req,String DefaultString){
 		//trace.logOpen(this,".getStringCookie");
		String chaine=DefaultString;
		try{
			javax.servlet.http.Cookie[] cookies = req.getCookies();
			if (cookies != null){
				for (int i=0; i<cookies.length; i++){
					if (cookies[i].getName().equals(ParameterName) ){
						chaine = cookies[i].getValue();
						return chaine;
					}
				}
			}
		}catch(Exception e){
			//trace.logError(this,e,".getStringCookie Exception : "+e.toString());
			chaine=DefaultString;
		}finally{
			//trace.logClose(this,".getStringCookie");
		}
		return DefaultString;
	}
	
//-------------------------------------------------------------------------------------------------
	public void addStringCookie(DynamoHttpServletResponse res,String ParameterName,String ParameterValue,String CookieDate){
  		//trace.logOpen(this,".addStringCookie");
		try{
			String cookString = ParameterName;
			cookString = cookString.concat("=");
			cookString = cookString.concat(ParameterValue);
			cookString = cookString.concat("; path=/;expires=");
			cookString = cookString.concat(CookieDate);
			res.addHeader("Set-Cookie",cookString);
		}catch(Exception e){
			//trace.logError(this,e,".addStringCookie Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".addStringCookie");
		}
	}
//-------------------------------------------------------------------------------------------------
	public void setStringCookie(DynamoHttpServletResponse res,String ParameterName,String ParameterValue,String CookieDate){
   		//trace.logOpen(this,".setStringCookie");
		try{
			String cookString = ParameterName;
			cookString = cookString.concat("=");
			cookString = cookString.concat(ParameterValue);
			cookString = cookString.concat("; path=/;expires=");
			cookString = cookString.concat(CookieDate);
			res.setHeader("Set-Cookie",cookString);
		}catch(Exception e){
			//trace.logError(this,e,".setStringCookie Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".setStringCookie");
		}
	}
	
//-------------------------------------------------------------------------------------------------
	public void setIntCookie(DynamoHttpServletResponse res,String ParameterName,int ParameterValue,String CookieDate){
  		//trace.logOpen(this,".setIntCookie");
		try{
			String cookString = ParameterName;
			cookString = cookString.concat("=");
			cookString += ParameterValue;
			cookString = cookString.concat("; path=/;expires=");
			cookString = cookString.concat(CookieDate);
			res.setHeader("Set-Cookie",cookString);
 		}catch(Exception e){
			//trace.logError(this,e,".setIntCookie Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".setIntCookie");
		}
	}
	
/**
* M�thode qui vide le cookie
* @param HttpServletResponse - la reponse Http
*        String - le nom du cookie
* @return none
* @throws none
*/
	
	public void clearCookie(DynamoHttpServletResponse response, String cookieName) {
  		//trace.logOpen(this,".clearCookie");
		try{
			String cookString = new String();
			String cookieValue = new String();
			Calendar aday=Calendar.getInstance();
			setStringCookie(response,cookieName,cookieValue,aday.getTime().toString());
 		}catch(Exception e){
			//trace.logError(this,e,".clearCookie Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".clearCookie");
		}
	}
	
//-------------------------------------------------------------------------------------------------
	public int getKey(int key1, int key2){
 		//trace.logOpen(this,".getKey");
		int key = 0;
		try{
			
            int tKey1 = key1;
            int tKey2 = key2;
            
            if (tKey1 < tKey2) {
				key = tKey1;
                tKey1 = tKey2;
                tKey2 = key;
			}
			key = tKey1;
			key *= 2;
			key %= tKey2;
			key += (tKey1/tKey2);
			key += 3;
 		}catch(Exception e){
			//trace.logError(this,e,".getKey Exception : "+e.toString());
			key = 0;
		}finally{
			//trace.logClose(this,".getKey");
		}
		return key;
	}
	
//-------------------------------------------------------------------------------------------------
	public String cryptString(String chaine){
  		//trace.logOpen(this,".cryptString");
		String cryptChaine = "";
		try{
			Random rand = new Random();
			int key1 = (rand.nextInt(30)+35);
			int key2 = (rand.nextInt(20)+35);
			int key = getKey(key1,key2);
			char tmpChar = ' ';
			int tmpCharValue = 0;
			cryptChaine += (char)key1;
			for (int i=0; i<chaine.length(); i++) {
				tmpChar = chaine.charAt(i);
				tmpCharValue = (int)tmpChar;
				tmpCharValue += key;
				tmpChar = (char)tmpCharValue;
				cryptChaine += tmpChar;
			}
			cryptChaine += (char)key2;
	/*
			System.out.println("\n ---------- Crypt ---------- \n");
			System.out.println("key1 : "+key1+" - charKey1 : "+(char)key1+"\n");
			System.out.println("key2 : "+key2+" - charKey2 : "+(char)key2+"\n");
			System.out.println(" --------------------------- \n");
	*/
		}catch(Exception e){
			//trace.logError(this,e,".cryptString Exception : "+e.toString());
			cryptChaine = "";
		}finally{
			//trace.logClose(this,".cryptString");
		}
		return cryptChaine;
	}
	
//-------------------------------------------------------------------------------------------------
	public String decryptString(String chaine){
  		//trace.logOpen(this,".decryptString");
		String decryptChaine = "";
		try{
			if (chaine==null || chaine.length()<3){
				 decryptChaine= "";
			}else{
				int key1 = (int)(chaine.charAt(0));
				int key2 = (int)(chaine.charAt(chaine.length()-1));
				
                String chaine2 = chaine;
                chaine2 = chaine.substring(1,chaine.length()-1);
                
				int key = getKey(key1,key2);
				char tmpChar = ' ';
				int tmpCharValue = 0;
				for (int i=0; i<chaine2.length(); i++) {
					tmpChar = chaine2.charAt(i);
					tmpCharValue = (int)tmpChar;
					tmpCharValue -= key;
					tmpChar = (char)tmpCharValue;
					decryptChaine += ""+tmpChar;
				}
			}
	/*
			System.out.println("\n ---------- DeCrypt ---------- \n");
			System.out.println("key1 : "+key1+" - charKey1 : "+(char)key1+"\n");
			System.out.println("key2 : "+key2+" - charKey2 : "+(char)key2+"\n");
			System.out.println(" ----------------------------- \n");
	*/
 		}catch(Exception e){
			//trace.logError(this,e,".decryptString Exception : "+e.toString());
			decryptChaine = "";
		}finally{
			//trace.logClose(this,".decryptString");
		}
		return decryptChaine;
	}
	
//---------------------------------------------------------------------
// Fin de classe	
//---------------------------------------------------------------------
}