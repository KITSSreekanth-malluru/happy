package com.castorama.xml;



import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.xml.sax.InputSource ;


import atg.nucleus.GenericContext;
import atg.xml.tools.XMLToolsFactory;
import atg.xml.tools.XSLProcessor;


/**
* La classe Transformer fournie les m�thodes charg�es de mixer des donn�s avec un template xsl.
* Les donn�es xml doivent �tre repr�sent�s par un XmlDocument
*/
public class Transformer{
	
    
    protected XMLToolsFactory	m_XmlToolsFactory;
    protected GenericContext	m_GenericContext;
      
    
    /**
	* R�cup�ration du GenericContext
	* @param none
	* @return GenericContext GenericContext
	* @throws none
	*/
    public GenericContext getGenericContext(){
        return m_GenericContext;
    }
    
    /**
	* Modification du GenericContext
	* @param GenericContext - GenericContext
	* @return none
	* @throws Exception
	*/
    public void setGenericContext(GenericContext a_GenericContext){
        m_GenericContext = a_GenericContext;
    }
    
    
    /**
	* R�cup�ration du XMLToolsFactory
	* @param none
	* @return XMLToolsFactory XMLToolsFactory
	* @throws none
	*/
    public XMLToolsFactory getXmlToolsFactory(){
        return m_XmlToolsFactory;
    }
    
    /**
	* Modification du XMLToolsFactory
	* @param XMLToolsFactory - XMLToolsFactory
	* @return none
	* @throws Exception
	*/
    public void setXmlToolsFactory(XMLToolsFactory a_XmlToolsFactory){
        m_XmlToolsFactory = a_XmlToolsFactory;
    }
    
    
    /**
	* Procces du template xsl pass� en param�tre avec les donn�es pass�es sous forme de XmlDocument
	* @param	XmlDocument	repr�sentation du xml
	* @param	String	url du template xsl
	* @return none
	* @throws Exception
	*/
    public String processXSLTemplate(XmlDocument a_XmlRepresentation, String a_strXSLTemplateURL) throws Exception{
     	String			l_strXSLTemplatePath	= m_GenericContext.getRealPath(a_strXSLTemplateURL);
        XSLProcessor	l_XslProcessor			= m_XmlToolsFactory.createXSLProcessor();
        InputSource		l_XMLInputSource		= getInputSource(a_XmlRepresentation);
        InputSource		l_XSLInputSource		= new InputSource(new FileInputStream(l_strXSLTemplatePath));
        StringWriter	l_Result			= new StringWriter();
        PrintWriter		l_ResultWriter			= new PrintWriter(l_Result);
        StringWriter	l_Diagnostic			= new StringWriter();
        PrintWriter		l_DiagnosticWriter		= new PrintWriter(l_Diagnostic);
        l_XSLInputSource.setSystemId(l_strXSLTemplatePath);
        
        l_XslProcessor.process(l_XMLInputSource, l_XSLInputSource, l_ResultWriter, l_DiagnosticWriter);
        
        String			l_strDiagnostic			= l_Diagnostic.toString();
       
        
        return l_Result.getBuffer().toString();
    }
    
    
    /**
	* Construction d'un org.xml.sax.InputSource � partir d'un XmlDocument
	* @param	XmlDocument	repr�sentation du xml
	* @return	InputSource
	* @throws none
	*/
    public InputSource getInputSource(XmlDocument a_XmlRepresentation){
    	StringBuffer 	l_Xml	= a_XmlRepresentation.getBuffer();
        return new InputSource(new StringReader(l_Xml.toString()));
    }
    
    
	
}