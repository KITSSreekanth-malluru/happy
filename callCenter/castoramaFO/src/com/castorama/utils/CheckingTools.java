package com.castorama.utils;

import java.lang.*;
import java.util.*;
import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;

/**
* CheckingTools : Castorama 2002<br>
* Class comportant les m�thodes utiles pour la v�rification de champs de formulaires
* @version 1.0  
* @author Damien DURIEZ - INTERNENCE (Decembre 2001) 
*/
public class CheckingTools{
	
	
	/**
	* V�rification : l'objet pass� en param�tre est-il null.<br>
	* G�n�re une DropletException en cas d'erreur
	* @param Object				a_Object			: Objet � v�rifier.
	* @param String				a_strExceptionName	: Nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkNull(Object a_Object, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = (a_Object!=null);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	
	/**
	* V�rification d'une chaine de caract�res
	* @param String	a_strString				: String � v�rifier.
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkString(String a_strString){     
		return !(a_strString==null || a_strString.length()<=0);
	}
	
	
	/**
	* V�rification d'une chaine de caract�res.<br>
	* G�n�re une DropletException en cas d'erreur
	* @param String				a_strString			: String � v�rifier.
	* @param String				a_strExceptionName	: Nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkString(String a_strString, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkString(a_strString);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	
	/**
	* V�rification du format d'une adresse email
	* @param String	a_strEmail			: Email � v�rifier.
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkEmail(String a_strEmail){     
		boolean l_bOk = true;
		if(a_strEmail!=null){
			int indexOfAt = a_strEmail.lastIndexOf("@");
			int indexOfPt = a_strEmail.lastIndexOf(".");
			int taille    = a_strEmail.length();
			l_bOk = ((0 < indexOfAt) && (indexOfAt < indexOfPt) && (5 < taille));
		}else{
			l_bOk = false ;
		}
		return l_bOk;    
	}
	
	
	/**
	* V�rification du format d'une adresse email.<br>
	* G�n�re une DropletException en cas d'erreur
	* @param String				a_strEmail			: Email � v�rifier.
	* @param String				a_strExceptionName	: nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkEmail(String a_strEmail, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkEmail(a_strEmail);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	
	
	/**
	* V�rification d' un Integer. est-il positif ?
	* Retourne false si l'Integer pass� en param�tre est null ou si la valeur num�rique<=0
	* @param		Integer		a_Value	: Integer � v�rifier.
	* @return		boolean		erreur True/False
	* @exception	none
	*/
	public static boolean checkIntegerPositif(Integer a_Value){  
	
		return (a_Value !=null && a_Value.intValue()>0) ;

	}
	
	/**
	* V�rification d' un Integer. est-il positif ?
	* Retourne false si l'Integer pass� en param�tre est null ou si la valeur num�rique<=0
	* @param		Integer				a_Value	: Integer � v�rifier.
	* @param		String				a_strExceptionName	: nom de l'exception g�n�r�e en cas d'erreur
	* @param		GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return		boolean				erreur True/False
	* @exception	none
	*/
	public static boolean checkIntegerPositif(Integer a_Value,String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkIntegerPositif(a_Value);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	
	/**
	* V�rification d'un Integer
	* Retourne false si l'Integer pass� en param�tre est null
	* @param String	a_Integer	: Integer � v�rifier.
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkInteger(Integer a_Integer){     
		return (a_Integer!=null);
	}
	
	/**
	* V�rification d'un Integer
	* Retourne false si l'Integer pass� en param�tre est null
	* @param String	a_Integer	: Integer � v�rifier.
	* @param String				a_strExceptionName	: nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkInteger(Integer a_Integer,String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkInteger(a_Integer);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	/**
	* V�rification d'un Double
	* Retourne false si le Double pass� en param�tre est null
	* @param String	a_Double	: Double � v�rifier.
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkDouble(Double a_Double){     
		boolean l_bOk = true;
		if(a_Double==null) 
            {
            l_bOk = false;
            }
		return l_bOk;
	}

	/**
	* V�rification d'un Double
	* @param Double		a_Double					: Double � v�rifier.
	* @param String		a_strExceptionName		: nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkDouble(Double a_Double, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkDouble(a_Double);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	
	/**
	* V�rification d'un Float
	* Retourne false si le Float pass� en param�tre est null
	* @param String	a_Float	: Double � v�rifier.
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkFloat(Float a_Float){     
		boolean l_bOk = true;
		if(a_Float==null)
            {
            l_bOk = false;
            }
		return l_bOk;
	}

	/**
	* V�rification d'un Float
	* @param Double		a_Float					: Float � v�rifier.
	* @param String		a_strExceptionName		: nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkFloat(Float a_Float, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkFloat(a_Float);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	/**
	* V�rification du format d'un code postal
	* @param String a_strCodePostal
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkCodePostal(String a_strCodePostal){
		if(a_strCodePostal==null)		
            {
            return false;
            }
		if(a_strCodePostal.length()!=5)	
            {
            return false;
            }
		try{
		  int l_nIntValue = Integer.parseInt(a_strCodePostal);
		}catch(NumberFormatException e){
		  	return false;
		}
		return true;
	}
	
	
	/**
	* V�rification du format d'un code postal
	* @param String				a_strCodePostal		: Code postal � v�rifier
	* @param String				a_strExceptionName	: Nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkCodePostal(String a_strCodePostal, String a_strExceptionName, GenericFormHandler a_FormHandler){
		boolean l_bOk = checkCodePostal(a_strCodePostal);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;
	}
	
	
	
	/**
	* V�rification d'un Boolean
	* @param Boolean	a_Boolean			: Boolean � v�rifier.
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkBoolean(Boolean a_Boolean){     
		boolean l_bOk = true;
		if(a_Boolean==null) 
            {
            l_bOk = false;
            }
		return l_bOk;
	}

	/**
	* V�rification d'un Boolean
	* @param Boolean	a_Boolean				: Boolean � v�rifier.
	* @param String		a_strExceptionName		: nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkBoolean(Boolean a_Boolean, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkBoolean(a_Boolean);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	/**
	* V�rification : la chaine est-elle num�rique
	* @param String a_strString : chaine � tester
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkLong(String a_strString){     
		boolean l_bOk = true;
		try{
			Long.parseLong(a_strString);
		}catch(NumberFormatException e){
			l_bOk=false;
		}		
		return l_bOk ;    
	}

	/**
	* V�rification : la chaine est-elle num�rique
	* @param String a_strString : chaine � tester
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkLong(String a_strString, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkLong(a_strString);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	/**
	* V�rification de la date de validite de la carte l'Atout : <br>
	* La date doit �tre post�rieure � la date courante
	* @param Date a_Date : Date � v�rifier.
	* @param String a_strExceptionName : nom de l'exception g�n�r�e en cas d'erreur
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkDateValid(Date a_Date){     
		boolean l_bOk = true;
		if(a_Date==null || a_Date.before(new Date()) )
            {
            l_bOk = false;
            }
		return l_bOk;
	}
	
	/**
	* V�rification de la date de validite de la carte l'Atout : <br>
	* La date doit �tre post�rieure � la date courante
	* @param Date a_Date : Date � v�rifier.
	* @param String a_strExceptionName : nom de l'exception g�n�r�e en cas d'erreur
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkDateValid(Date a_Date, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkDateValid(a_Date);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	/**
	* V�rification de la date de validite de la carte l'Atout : <br>
	* La date doit �tre post�rieure � la date courante
	* @param Date a_Date : Date � v�rifier.
	* @param String a_strExceptionName : nom de l'exception g�n�r�e en cas d'erreur
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkDate(String a_strJour,String a_strMois,String a_strAnnee){     
		boolean l_bOk = true;
		try{
			if ((!a_strJour.equals("")) && (!a_strMois.equals("")) && (!a_strAnnee.equals("")))
			{
				int num1=Integer.parseInt(a_strJour);
				int num2=Integer.parseInt(a_strMois);
				int num3=Integer.parseInt(a_strAnnee);
						
				if (num1<1 || num1>31 ||num2 <1 || num2>12 || (num3/1000<1)){
					l_bOk=false;
				}
						
			}else{
				l_bOk=false;
			}
		}catch(Exception e){
			l_bOk=false;
		}
		return l_bOk;
	}
	
	/**
	* V�rification de la date de validite de la carte l'Atout : <br>
	* La date doit �tre post�rieure � la date courante
	* @param Date a_Date : Date � v�rifier.
	* @param String a_strExceptionName : nom de l'exception g�n�r�e en cas d'erreur
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkDate(String a_strJour,String a_strMois,String a_strAnnee, String a_strExceptionName, GenericFormHandler a_FormHandler){     
		boolean l_bOk = checkDate(a_strJour,a_strMois,a_strAnnee);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;    
	}
	
	
	
	/**
	* V�rification du format d'un num�ro de t�l�phone fran�ais (10 chiffres)
	* @param		String
	* @return		boolean erreur True/False
	* @exception	none
	*/
	public static boolean checkTelephone(String a_strTelephone){
		if(a_strTelephone==null)	{
            return false;
        }
		if(a_strTelephone.length()!=10)	
            {
            return false;
            }
		try{
		  long l_lLongValue = Long.parseLong(a_strTelephone);
		}catch(NumberFormatException e){
		  	return false;
		}
		return true;
	}

	
	/**
	* V�rification du format d'un num�ro de t�l�phone fran�ais (10 chiffres)
	* @param String				a_strTelephone		: Num�ro de t�l�phone � v�rifier
	* @param String				a_strExceptionName	: Nom de l'exception g�n�r�e en cas d'erreur
	* @param GenericFormHandler	a_FormHandler		: Le FormHandler qui recevra l'exception
	* @return boolean erreur True/False
	* @exception none
	*/
	public static boolean checkTelephone(String a_strTelephone, String a_strExceptionName, GenericFormHandler a_FormHandler){
		boolean l_bOk = checkTelephone(a_strTelephone);
		if(!l_bOk){
			a_FormHandler.addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
		}
		return l_bOk;
	}
}
