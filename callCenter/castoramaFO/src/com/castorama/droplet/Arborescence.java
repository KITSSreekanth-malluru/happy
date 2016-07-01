package com.castorama.droplet;

import java.io.*;
import atg.commerce.catalog.*;
import atg.repository.*;
import javax.servlet.*;
import atg.servlet.*;
import atg.nucleus.naming.ParameterName;

/**
 * Cette servlet utilise le template d'affichage <i>output</i> pour acc�der au param�tre de sortie 
 * dont le nom est stoqu� dans le param� d'entr� <i>elementName</i> qui va 
 * contenir le chemin complets des skus dont l'id du product est pass� dans le param�tre d'entr� <i>idProduct</i>
 * 
 * <p>La description compl�te des param�tres de la droplet Arborescence est:
 *
 * <dl>
 *
 * <dt>elementName
 * <dd>Le param�tre facultatif qui doit �tre employ� comme nom pour le vector dans le 
 * template de sortie output.
 *
 * <dt>ID
 * <dd>Le param�tre qui d�fini le product dont on veut l'arborescence.
 *
 * </dl>
 *
 * @author Sylvain Delettr�
 * @version $Revision: 1.1 $ $Date: 2006/06/30 17:31:46 $ $Author: groupinfra\pereirag $
 */


public class Arborescence extends DynamoServlet
{
	public static final String CLASS_VERSION = "$Id: Arborescence.java,v 1.1 2006/06/30 17:31:46 user Exp $";


	public final static String ELEMENT = "element";
	public final static ParameterName OUTPUT = ParameterName.getParameterName("output");
	// for backward compatibility
	public final static ParameterName ELEMENT_NAME = ParameterName.getParameterName("elementName");
	public final static ParameterName ID = ParameterName.getParameterName("productId");

	private CatalogTools m_CatalogTools;
	private Repository m_CatalogueRepository;
	
	
/**
* M�thode d'initialisation du CatalogTools.
* @param CatalogTools - Le CatalogTools.
* @return none.
* @throws none.
*/
	public void setCatalogTools(CatalogTools a_CatalogTools)
	{
	  	m_CatalogTools = a_CatalogTools;
	}
	
/**
* M�thode de r�cup�ration du CatalogTools.
* @param none.
* @return CatalogTools - Le CatalogTools.
* @throws none.
*/
	public CatalogTools getCatalogTools()
	{
	  	return m_CatalogTools;
	}
		
/**
* M�thode d'initialisation du repository contenant l'item product.
* @param Repository - Le repository contenant l'item product.
* @return none.
* @throws none.
*/
	public void setCatalogueRepository(Repository a_CatalogueRepository)
	{
	  	m_CatalogueRepository = a_CatalogueRepository;
	}
	
/**
* M�thode de r�cup�ration du repository contenant l'item product.
* @param none.
* @return Repository - Le repository contenant l'item product.
* @throws none.
*/
	public Repository getCatalogueRepository()
	{
	  	return m_CatalogueRepository;
	}
	
	
	
/**
* Charge dans la request un template d'affichage "output" pour acc�der � l'arborescence du product
* @param DynamoHttpServletRequest - La request � trait�.
* @param DynamoHttpServletResponse - L'objet reponse de cette request.
* @return none.
* @throws javax.servlet.ServletException - Si une erreur sp�cifique d'application est 
* arriv�e traitant cette demande.
* @throws java.io.IOException - Si une erreur est arriv�e lisant des donn�es de 
* la demande ou �crivant des donn�es � la r�ponse.
*/
	public void service(DynamoHttpServletRequest a_Request,
	                    DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException
	{
		//Trace.logOpen(this,".service");
		try{
			String l_strElement;
			
			l_strElement = a_Request.getParameter(ELEMENT_NAME);
			
			if (l_strElement == null || l_strElement.equals(""))
			{
				l_strElement = ELEMENT;
			}
				
			String l_ProductId;
			l_ProductId = a_Request.getParameter(ID);
			if (l_ProductId != null && !l_ProductId.equals(""))
			{

				a_Request.setParameter(l_strElement,getArborescence(l_ProductId));
				a_Request.serviceParameter(OUTPUT,a_Request,a_Response);
			}
			else
			{
				a_Request.setParameter(l_strElement,new String());
				a_Request.serviceParameter(OUTPUT,a_Request,a_Response);
			}
		}catch(Exception e){
 			//Trace.logError(this,e,".service Exception : "+e.toString());
		}finally{
			//Trace.logClose(this,".service");
		}
	}
	
	
	
/**
* Recup�re une chaine de caract�re contenant l'arborescence du product.
* @param String a_ProductId - L'id du product.
* @return String - La chaine de caract�re contenant l'arborescence du product.
* @throws atg.repository.RepositoryException - S'il y avait une erreur d'obtention d'item descriptor.
*/
	private String getArborescence(String a_ProductId) throws RepositoryException
	{
		//Trace.logOpen(this,".getArborescence");
		RepositoryItem l_Product;
		RepositoryItem l_SubCategory;
		RepositoryItem l_Category;
		String l_resultat="";
		String l_CategoryName="";
		String l_SubCategoryName ="";
		String l_ProductName = "";
		
		try{
		
			l_Product = getCatalogueRepository().getItem(a_ProductId, "product");
			l_ProductName = (String) l_Product.getPropertyValue("displayName");
		
			l_SubCategory = (RepositoryItem) l_Product.getPropertyValue("parentCategory");
			l_SubCategoryName = (String) l_SubCategory.getPropertyValue("displayName");
		
			l_Category = (RepositoryItem) l_SubCategory.getPropertyValue("parentCategory");
			l_CategoryName = (String) l_Category.getPropertyValue("displayName");
		
			l_resultat = l_CategoryName+" > "+ l_SubCategoryName+" > "+l_ProductName;
		}catch(RepositoryException e){
 			//Trace.logError(this,e,".getArborescence RepositoryException : "+e.toString());
			l_resultat = l_ProductName ;
			throw(e);
		}catch(Exception e){
 			//Trace.logError(this,e,".getArborescence Exception : "+e.toString());
		}finally{
			//Trace.logClose(this,".getArborescence");
		}
		return l_resultat;
	}
	
}
