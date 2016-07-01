package com.castorama.pageListe;


/**
 * 
 * bean de stockage des tri par prix et marque pageListe.
 *
 */
public class CastoStockDataPageListe
{
    
    private String m_sMarque;
    private String m_sTri;
    private String m_sTriService ;
    
    /** REFERENCEMENT: on sauvegarde aussi la pagination **/
    private int m_iPage; 
    /** !REFERENCEMENT: on sauvegarde aussi la pagination **/
    
    /** 
     * Retient le dernier mode utilisé pour une page liste (CD ou PLD)
     * pour réinitialiser le tri si on est passée sur un nouveau mode.
     */   
    private String m_ancienMode;
    
    /** 
     * Retient la dernière catégorie affichée sur une page liste
     * pour réinitialiser le tri si on est passée sur une nouvelle catégorie.
     */
    private String m_ancienneCategorie;
    
    
    /**
     * getTri.
     * @return String
     */
    public String getTri() 
    {
        return m_sTri;
    }

    /**
     * 
     * setTri.
     * @param a_tri ordre de tri
     */
    public void setTri(String a_tri) 
    {
        // REFERENCEMENT: si on change le tri, on revient en page 1
        setPage(1);
        // !REFERENCEMENT: si on change le tri, on revient en page 1        
        m_sTri = a_tri;
    }

    /**
     * 
     * getMarque.
     * @return String
     */
    public String getMarque() 
    {
        return m_sMarque;
    }

    /**
     * 
     * setMarque.
     * @param a_marque   la marque
     */
    public void setMarque(String a_marque) 
    {
        // REFERENCEMENT: si on change la marque, on revient en page 1
        setPage(1);
        // !REFERENCEMENT: si on change la marque, on revient en page 1        
        m_sMarque = a_marque;
    }

    /**
     * getTriService.
     * @return String
     */
    public String getTriService()
    {
        return m_sTriService;
    }

    
    /**
     * setTriService.
     * @param a_triService String
     */
    public void setTriService(String a_triService)
    {
        // REFERENCEMENT: si on change le tri service, on revient en page 1
        setPage(1);
        // !REFERENCEMENT: si on change le tri service, on revient en page 1      
        m_sTriService = a_triService;
    }

    /**
     * getAncienMode.
     * @return encien mode
     */
    public String getAncienMode()
    {
        return m_ancienMode;
    }

    /**
     * setAncienMode.
     * @param a_ancienMode ancien mode
     */
    public void setAncienMode(String a_ancienMode)
    {
        m_ancienMode = a_ancienMode;
    }

    /**
     * TODO getAncienneCategorie.
     * @return ancienne categorie
     */
    public String getAncienneCategorie()
    {
        return m_ancienneCategorie;
    }

    /**
     * setAncienneCastegorie.
     * @param a_ancienneCategorie ancienne categorie
     */
    public void setAncienneCategorie(String a_ancienneCategorie)
    {
        m_ancienneCategorie = a_ancienneCategorie;
    }

    /** REFERENCEMENT: on sauvegarde aussi la pagination **/
    public int getPage()
    {
        return m_iPage;
    }

    public void setPage(int a_page)
    {       
        m_iPage = a_page;
    }
    /** !REFERENCEMENT: on sauvegarde aussi la pagination **/
    
    
}
