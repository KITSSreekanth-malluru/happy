package com.castorama.xml;



/**
* La classe XmlDocument est un StringBuffer repr�sentant un document xml.
*/
public class XmlDocument{
	
	private static final String XML_ENTETE		= "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
	private static final String XML_DEBUT_ROOT	= "<root>\n";
    private static final String XML_FIN_ROOT	= "</root>\n";
	
	StringBuffer m_buffer ;
	
	/**
	* Constructeur vide<br>
	* Ajout des ent�tes
	*/
	public XmlDocument(){
		m_buffer = new StringBuffer();
		m_buffer.append(XML_ENTETE);
		m_buffer.append(XML_DEBUT_ROOT);
	}
	
	
	/**
	* R�cup�ration du StringBuffer repr�sentant le document
	* @param	none
	* @return	StringBuffer
	* @throws none
	*/
    public StringBuffer getBuffer(){
    	return m_buffer;
    }
	
	
	/**
	* R�cup�ration du document apres fermeture du root
	* @param	none
	* @return	XmlDocument
	* @throws none
	*/
    public XmlDocument closeDocument(){
    	
    	String	l_strSiteHttpServerName = com.castorama.config.Configuration.getConfiguration().getSiteHttpServerName();
    	int		l_nSiteHttpServerPort = com.castorama.config.Configuration.getConfiguration().getSiteNonSecurePort();
    	addNode("siteHttpServerName",l_strSiteHttpServerName);
    	addNode("siteHttpServerPort",String.valueOf(l_nSiteHttpServerPort));
    	m_buffer.append(XML_FIN_ROOT);
    	
    	return this;
    }
	
	
	/**
	* Ajout d'un noeud au document
	* @param	String	nom du noeud
	* @param	String	Valeur du noeud
	* @return	List
	* @throws none
	*/
    public void addNode(String a_strNomNoeud, String a_strText){
    	if(a_strText!=null){
	    	m_buffer.append("<");
	    	m_buffer.append(a_strNomNoeud);
	    	m_buffer.append(">");
	    	m_buffer.append(substituteXML(a_strText));
	    	m_buffer.append("</");
	    	m_buffer.append(a_strNomNoeud);
	    	m_buffer.append(">");
	 	}
    }
    
    
    /**
	* Ouverture d'un noeud dans document
	* @param	String	nom du noeud
	* @return	List
	* @throws none
	*/
    public void openNode(String a_strNomNoeud){
    	m_buffer.append("<");
    	m_buffer.append(a_strNomNoeud);
    	m_buffer.append(">");
    }
    
    
    /**
	* Fermeture d'un noeud dans document
	* @param	String	nom du noeud
	* @return	List
	* @throws none
	*/
    public void closeNode(String a_strNomNoeud){
    	m_buffer.append("</");
    	m_buffer.append(a_strNomNoeud);
    	m_buffer.append(">");
    }
    
    
    /**
	* Ajout d'un texte dans document
	* @param	String	nom du noeud
	* @return	List
	* @throws none
	*/
    public void addText(String a_strText){
    	if(a_strText!=null)
            {
            m_buffer.append(substituteXML(a_strText));
            }
    }
    
    
    /**
	* Substitution des caract�res non permis par leur version encod�e
	* @param	String
	* @return	String
	* @throws none
	*/
    public static String substituteXML(String l_strInput){
		String l_strOutput=l_strInput;
		try{
			l_strOutput = replaceAll(l_strOutput,"&",	"&amp;");	
			l_strOutput = replaceAll(l_strOutput,"<",	"&lt;");
			l_strOutput = replaceAll(l_strOutput,">",	"&gt;");
			l_strOutput = replaceAll(l_strOutput,"\\\\","&apos;");
		}catch(Exception e){
			System.out.print("castorama.xml.XmlDocument.subsituteXML(String l_strInput) : "+e);	
		}
		return l_strOutput;
	}
	
	
	/**
	* Remplacement d'une String dans une String
	* @param	String
	* @return	String
	* @throws none
	*/
    public static String replaceAll(String l_strInput, String a_strStringToReplace, String a_strStringToPutInstead){
    	String l_strResult = l_strInput;
    	/* my comment
		try
		{
			gnu.regexp.RE l_RE = new gnu.regexp.RE(a_strStringToReplace);
			l_strResult=l_RE.substituteAll(l_strResult,a_strStringToPutInstead);

		}catch(gnu.regexp.REException e)
		{
			
			l_strResult=l_strInput;
		}
		*/
		return l_strResult;
	}
	
}
