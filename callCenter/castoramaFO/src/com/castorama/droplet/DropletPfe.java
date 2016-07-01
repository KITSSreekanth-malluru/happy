package com.castorama.droplet;

import java.io.*;
import java.text.DecimalFormat;
import javax.servlet.*;
import atg.servlet.*;
import atg.repository.*;

import com.castorama.*;


/**
 * Cette servlet utilise le template d'affichage <i>ouput</i>
 * pour indiquer la partitipation au frais de port pour un poids pass� en parametre.
 * 
 * <p>La description compl�te des param�tres de la droplet Contains est:
 *
 * <dl>
 *
 * <dt>poids
 * <dd>Le poids d'un article.
 * 
 * 
 * <dt>output
 * <dd>Le template d'affichage de sortie
 *
 * <dt>pfe
 * <dd>Le param�tre de soprtie contenant la pfe
 *
 * </dl>
 *
 * @author Damien DURIEZ
 */

public class DropletPfe extends DynamoServlet{
	

	public final static String POIDS		= "poids";
	public final static String QUANTITE		= "quantite";
	public final static String OUTPUT		= "output";
	public final static String URL_PICTO	= "urlPicto";
	public final static String POIDS_EN_KG	= "poidsEnKg";
	
	
	/**
	* Service.
	* @param DynamoHttpServletRequest - La request � trait�.
	* @param DynamoHttpServletResponse - L'objet reponse de cette request.
	* @return none.
	* @throws javax.servlet.ServletException - Si une erreur sp�cifique d'application est 
	* arriv�e traitant cette demande.
	* @throws java.io.IOException - Si une erreur est arriv�e lisant des donn�es de 
	* la demande ou �crivant des donn�es � la r�ponse.
	*/
	public void service(	DynamoHttpServletRequest a_Request,
							DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException {
		
		try{
			
			double	l_dPfe		= 0.00 ;
			
			Integer	l_Poids		= (Integer)	a_Request.getObjectParameter(POIDS);
			Long	l_Quantite	= (Long)	a_Request.getObjectParameter(QUANTITE);
			if(l_Poids!=null){
				int	l_nPoids	= l_Poids!=null ? l_Poids.intValue() : 0;
				int	l_nQuantite	= l_Quantite!=null ? l_Quantite.intValue() : 0;	
				a_Request.setParameter(POIDS_EN_KG,formatPoids(l_nPoids*l_nQuantite));
			}

			a_Request.serviceLocalParameter(OUTPUT, a_Request, a_Response);
		}catch(Exception e){
			//Trace.logError(this,e,".service : "+e.toString());	
		}

	}
	
	
	/**
	* Formatage d'un poids en Kg
	* @param	int		poids
	* @return	String
	* @throws	Exception
	*/
	public static String formatPoids(int a_nPoids) throws Exception {
		DecimalFormat l_DecimalFormat = new DecimalFormat("0.###");
		l_DecimalFormat.setGroupingUsed(false);		
		return l_DecimalFormat.format((double)a_nPoids/1000);
	}
}