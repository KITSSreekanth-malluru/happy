package com.castorama;

import java.io.IOException;
import javax.servlet.ServletException;
import com.castorama.constantes.CastoConstantes;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.repository.*;
import atg.repository.rql.RqlStatement;

public class CastoJournalisationFormHandler extends CommerceProfileFormHandler
{
    /*
     * ------------------------------------------------------------------------
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    /**
     * repository profile
     */
    private Repository m_repository;
    
    private String m_numeroCommande;
    
    private Object[] m_journalisation;

    private String m_successURL;
    private String m_errorURL;
    private boolean existeJournalisation;
    
    
    
    
    
    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * @return the existeJournalisation
     */
    public boolean isExisteJournalisation()
    {
        return existeJournalisation;
    }

    /**
     * @param a_existeJournalisation the existeJournalisation to set
     */
    public void setExisteJournalisation(boolean a_existeJournalisation)
    {
        existeJournalisation = a_existeJournalisation;
    }

    /**
     * @return the errorURL
     */
    public String getErrorURL()
    {
        return m_errorURL;
    }

    /**
     * @param a_errorURL the errorURL to set
     */
    public void setErrorURL(String a_errorURL)
    {
        m_errorURL = a_errorURL;
    }

    /**
     * @return the successURL
     */
    public String getSuccessURL()
    {
        return m_successURL;
    }

    /**
     * @param a_successURL the successURL to set
     */
    public void setSuccessURL(String a_successURL)
    {
        m_successURL = a_successURL;
    }
    
    
    
    /**
     * @return the journalisation
     */
    public Object[] getJournalisation()
    {
        return m_journalisation;
    }

    /**
     * @param a_journalisation the journalisation to set
     */
    public void setJournalisation(Object[] a_journalisation)
    {
        m_journalisation = a_journalisation;
    }

    /**
     * @return the numeroCommande
     */
    public String getNumeroCommande()
    {
        return m_numeroCommande;
    }

    /**
     * @param a_numeroCommande the numeroCommande to set
     */
    public void setNumeroCommande(String a_numeroCommande)
    {
        m_numeroCommande = a_numeroCommande;
    }

    public Repository getRepository()
    {
        return m_repository;
    }

    public void setRepository(Repository a_repository)
    {
        m_repository = a_repository;
    }


    /*
     * ------------------------------------------------------------------------ [
     * Methodes ]
     * ------------------------------------------------------------------------
     */

    public boolean handleFindJournalisation(DynamoHttpServletRequest a_oRequest,
            DynamoHttpServletResponse a_oResponse) throws IOException
    {
        try
        {
            verificationChamps();
            
            
            if (getFormError())
            {
                a_oResponse.sendLocalRedirect(getErrorURL() + "?error=true", a_oRequest);
                return true;
            }
            else
            {
                findHisto();
                a_oResponse.sendLocalRedirect(getSuccessURL() + "?success=true", a_oRequest);
                return false;
            }
        }
        catch (Exception l_e)
        {
            logError(l_e);
            a_oResponse.sendLocalRedirect(getErrorURL() + "?error=true", a_oRequest);
            return true;
        }
    }
    
    
    
    
    private void findHisto()
    {
        try
        {
            Repository l_mutRepo = getRepository();
            
            Object[] l_journalisations = null;
            RepositoryItemDescriptor l_itemDescriptor = getRepository().getItemDescriptor("journalisationCC");
            if (l_itemDescriptor != null)
            {
                RepositoryView l_repositoryView = l_itemDescriptor.getRepositoryView();
                RqlStatement l_statement = RqlStatement.parseRqlStatement("orderId = ?0");
                l_journalisations = l_statement.executeQuery(l_repositoryView, new Object[]{getNumeroCommande()});
                if (null !=l_journalisations)
                {
                        setJournalisation(l_journalisations);
                        setExisteJournalisation(new Boolean("true").booleanValue());
                }
                else
                {
                    setExisteJournalisation(new Boolean("false").booleanValue());
                }
            }
        }
        catch (RepositoryException l_repoEx)
        {
            logError(l_repoEx);
        }
    }
    
    
    
    private void verificationChamps()
    {
        if (getNumeroCommande() == null || getNumeroCommande().length() == 0 || getNumeroCommande().equals(""))
        {
            addFormException(new DropletException("Veuillez saisir le numéro de la commande."));
        }
    }

   
}