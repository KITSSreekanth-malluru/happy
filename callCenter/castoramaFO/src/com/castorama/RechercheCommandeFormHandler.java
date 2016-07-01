package com.castorama;

import com.castorama.utils.CheckingTools;
import java.io.*;

import javax.servlet.*;

import atg.servlet.*;
import atg.userprofiling.Profile;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import java.util.Vector;
import atg.droplet.GenericFormHandler;
import atg.repository.rql.RqlStatement;
import atg.repository.servlet.RepositoryFormHandler;
import atg.repository.RepositoryException;
import atg.commerce.catalog.*;
import atg.repository.*;

/**
 * RechercheUserFormHandler : Castorama 2001. Ce composant est utilisï¿½ pour la
 * recherche d'une commande dans le call_center
 * 
 * @author Sylvain Delettrï¿½ - INTERNENCE (Mai 2001)
 */

public class RechercheCommandeFormHandler extends RepositoryFormHandler
{
    String m_strSubmitSuccessURL; // URL de redirection en cas de succes du
                                    // form

    String m_strSubmitErrorURL; // URL de redirection en cas de probleme du form

    Profile m_Profile; // Profil

    String m_strProfileId;

    /*
     * Projet Castorama - La Défense *** Logica ajout de la possibilite de
     * rehcercher les commandes par magasin
     */
    String m_magasinId = "";

    CatalogTools m_CatalogTools;

    public void setCatalogTools(CatalogTools a_CatalogTools)
    {

        m_CatalogTools = a_CatalogTools;

    }

    public CatalogTools getCatalogTools()
    {

        return m_CatalogTools;
    }

    /* Projet Castorama - La Défense *** Logica */
    public void setMagasinId(String a_magasinId)
    {

        m_magasinId = a_magasinId;

    }

    public String getMagasinId()
    {

        return m_magasinId;
    }

    /* Fin Projet Castorama - La Défense *** Logica */

    /**
     * Rï¿½cupï¿½ration du SubmitSuccessURL
     * 
     * @param none
     * @return String SubmitSuccessURL
     * @throws none
     */
    public String getSubmitSuccessURL()
    {

        return m_strSubmitSuccessURL;
    }

    /**
     * Modification du SubmitSuccessURL
     * 
     * @param String
     *            SubmitSuccessURL
     * @return none
     * @throws none
     */
    public void setSubmitSuccessURL(String a_strSubmitSuccessURL)
    {

        m_strSubmitSuccessURL = a_strSubmitSuccessURL;

    }

    /**
     * Rï¿½cupï¿½ration du SubmitErrorURL
     * 
     * @param none
     * @return String SubmitErrorURL
     * @throws none
     */
    public String getSubmitErrorURL()
    {

        return m_strSubmitErrorURL;
    }

    /**
     * Modification du SubmitErrorURL
     * 
     * @param String
     *            SubmitErrorURL
     * @return none
     * @throws none
     */
    public void setSubmitErrorURL(String a_strSubmitErrorURL)
    {

        m_strSubmitErrorURL = a_strSubmitErrorURL;

    }

    /**
     * Rï¿½cupï¿½ration du profil
     * 
     * @param none
     * @return Profil user
     * @throws none
     */
    public Profile getProfile()
    {

        return m_Profile;
    }

    /**
     * Modification du profil
     * 
     * @param Profil
     *            user
     * @return none
     * @throws none
     */
    public void setProfile(Profile a_Profile)
    {

        m_Profile = a_Profile;

    }

    /**
     * Rï¿½cupï¿½ration du ProfileId
     * 
     * @param none
     * @return String ProfileId
     * @throws none
     */
    public String getProfileId()
    {

        return m_strProfileId;
    }

    /**
     * Modification du ProfileId
     * 
     * @param String
     *            ProfileId
     * @return none
     * @throws none
     */
    public void setProfileId(String a_strProfileId)
    {

        m_strProfileId = a_strProfileId;

    }

    private RepositoryItem[] m_ResultatRecherche;

    public RepositoryItem[] getResultatRecherche()
    {

        return m_ResultatRecherche;
    }

    public boolean rechercheCommande(String a_strProfileId)
    {
        // trace.logOpen(this,".rechercheCommande");
        boolean l_bOk = true;
        RepositoryItem[] items = null;
        try
        {
            RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("order");
            RepositoryView l_ItemView = l_Item.getRepositoryView();
            QueryBuilder l_Builder = l_ItemView.getQueryBuilder();

            Query[] l_TabPieces;
            Vector l_VPieces = new Vector();

            // ProfileId
            if (a_strProfileId.compareTo("") != 0)
            {
                // trace.logDebug(this,".rechercheCommande a_strProfileId : " +
                // a_strProfileId);
                QueryExpression l_Query1 = l_Builder.createPropertyQueryExpression("profileId");

                QueryExpression l_Query2 = l_Builder.createConstantQueryExpression(a_strProfileId);

                Query l_Query = l_Builder.createComparisonQuery(l_Query1, l_Query2, QueryBuilder.EQUALS);

                l_VPieces.add(l_Query);
            }
            l_TabPieces = new Query[l_VPieces.size()];
            l_VPieces.copyInto(l_TabPieces);
            SortDirectives l_SortDirectives = new SortDirectives();
            l_SortDirectives.addDirective(new SortDirective("submittedDate", SortDirective.DIR_DESCENDING));

            Query l_AndQuery = l_Builder.createAndQuery(l_TabPieces);
            items = l_ItemView.executeQuery(l_AndQuery, l_SortDirectives);
        }
        catch (RepositoryException e)
        {
            l_bOk = false;
            items = new RepositoryItem[0];
            // trace.logError(this,e,".rechercheCommande RepositoryException :
            // "+e.toString());
        }
        catch (Exception e)
        {
            l_bOk = false;
            items = new RepositoryItem[0];
            // trace.logError(this,e,".rechercheCommande Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".rechercheCommande");
        }
        m_ResultatRecherche = items;
        return l_bOk;
    }

    public boolean handleSubmit(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws IOException, ServletException
    {

        // trace.logOpen(this,".handleSubmit");
        RepositoryItem[] items = null;
        boolean l_bOk = true;
        try
        {
            // recup v3 param
            m_strProfileId = a_Request.getParameter("profileClient");
            l_bOk &= CheckingTools.checkString(m_strProfileId, "ProfileIdMissing", (GenericFormHandler) this);
            if (l_bOk)
            {
                l_bOk = rechercheCommande(m_strProfileId);
                if (l_bOk)
                {
                    a_Response.sendLocalRedirect(getSubmitSuccessURL() + "?profileId=" + m_strProfileId, a_Request);
                }
                else
                {
                    a_Response.sendLocalRedirect(getSubmitErrorURL(), a_Request);
                }
            }
        }
        catch (Exception e)
        {
            a_Response.sendLocalRedirect(getSubmitErrorURL(), a_Request);
            // trace.logError(this,e,".handleSubmit Exception : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleSubmit");
        }
        return true;
    }

    /* Projet Castorama - La Défense *** Logica */
    /**
     * methode permettant le recherche d'une commande par magasin.
     * 
     * @param a_Request
     *            DynamoHttpServletRequest.
     * @param a_Response
     *            DynamoHttpServletResponse.
     * @return boolean oui si la recherche s'est bien passée non sinon.
     * @throws IOException
     *             Si une erreur survient.
     * @throws ServletException
     *             Si une erreur survient.
     * @throws RepositoryException
     *             Si une erreur survient.
     */
    public boolean handleRechercheParMagasin(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws IOException, ServletException, RepositoryException
    {

        String l_magasin = "";
        if (getMagasinId() != null)
        {
            l_magasin = getMagasinId();
        }

        if (l_magasin != null && !"".equals(l_magasin))
        {
            MutableRepository l_oMutRepos = (MutableRepository) getRepository();
            RepositoryView l_ItemView = l_oMutRepos.getView("order");
            
            QueryBuilder l_Builder = l_ItemView.getQueryBuilder();

            Query[] l_TabPieces;
            Vector l_VPieces = new Vector();
            RepositoryItem[] l_commandes = null;

            // on recupere les commandes dont le origineMagasin correspond à
            // celui choisi par l'utilisateur et on trie les commandes par date
            // avec la date non null
            QueryExpression l_Query1 = l_Builder.createPropertyQueryExpression("origineMagasin");
            QueryExpression l_Query2 = l_Builder.createConstantQueryExpression(l_magasin);
            Query l_Query = l_Builder.createComparisonQuery(l_Query1, l_Query2, QueryBuilder.EQUALS);
            l_VPieces.add(l_Query);
            
            QueryExpression l_Query3 = l_Builder.createPropertyQueryExpression("submittedDate");
            Query l_QueryNull = l_Builder.createIsNullQuery(l_Query3);
            QueryExpression l_QueryNotNull = l_Builder.createNotQuery(l_QueryNull);          
            l_VPieces.add(l_QueryNotNull);
            
            l_TabPieces = new Query[l_VPieces.size()];
            l_VPieces.copyInto(l_TabPieces);
            SortDirectives l_SortDirectives = new SortDirectives();
            l_SortDirectives.addDirective(new SortDirective("submittedDate", SortDirective.DIR_DESCENDING));

            Query l_AndQuery = l_Builder.createAndQuery(l_TabPieces);
            l_commandes = l_ItemView.executeQuery(l_AndQuery, l_SortDirectives);

             if (l_commandes == null)
            {
                l_commandes = new RepositoryItem[0];
            }
            m_ResultatRecherche = l_commandes;
           
            // on redirige vers la page de succes
            a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);

        }
        return true;

    }
    /* Fin Projet Castorama - La Défense *** Logica */

}
