package com.castorama.login;

import java.io.IOException;

import javax.servlet.ServletException;

//import com.atos.sips.payment.web.Request;
import com.castorama.constantes.CastoConstantes;

import atg.commerce.profile.CommerceProfileFormHandler;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.repository.*;
import atg.repository.rql.RqlStatement;


/**
 * @author Geoffrey Yusif
 * 
 */
public class CastoCallCenterProfileFormHandler extends CommerceProfileFormHandler
{
    /*
     * ------------------------------------------------------------------------ [
     * Constants
     * ------------------------------------------------------------------------
     */

     /*
     * property "login" in item description "user".
     */
    private static final String LOGIN_PROPERTY = "login";

    
    /**
     * Constant pour la recherche par recherche login utilisateur.
     */
    private static final String CRITERE_RECHERCHE_LOGIN = "login = ?0";

    
    
    /*
     * ------------------------------------------------------------------------ [
     * Attributes
     * ------------------------------------------------------------------------
     */

    /**
     * {@link CastoCallCenterProfileManager}.
     */
    private CastoCallCenterProfileManager m_manager;

   
    /**
     * repository profile
     */
    private Repository m_repository;
    
    

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs
     * ------------------------------------------------------------------------
     */

    public Repository getRepository()
    {
        return m_repository;
    }

    public void setRepository(Repository a_repository)
    {
        m_repository = a_repository;
    }

    /**
     * Return the CastoProfilManager.
     * 
     * @return CastoProfilManager the CastoProfilManager
     */
    public CastoCallCenterProfileManager getManager()
    {
        return m_manager;
    }

    /**
     * Set the CastoProfilManager.
     * 
     * @param a_manager
     *            The object is type CastoProfilManager.
     */
    public void setManager(CastoCallCenterProfileManager a_manager)
    {
        m_manager = a_manager;
    }

    

  

    /*
     * ------------------------------------------------------------------------ [
     * Methods ]
     * ------------------------------------------------------------------------
     */

    /**
     * @see atg.userprofiling.ProfileFormHandler#preLoginUser(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     * 
     * @param a_request
     *            the request HTTP.
     * @param a_response
     *            the response HTTP.
     * 
     * @throws ServletException
     * @throws IOException
     */
    protected void preLoginUser(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
    throws ServletException, IOException
    {

        if (isLoggingDebug())
        {
            logDebug( this.getClass()+ "preLoginUser().");
        }

        super.preLoginUser(a_request, a_response);

        if (isLoggingDebug())
        {
            logDebug(this.getClass() + "FIN preLoginUser().");
        }

      
    }


    /**
     * 
     * @param a_request
     * 
     * @return boolean True is success.
     */
    private boolean verifierFormulaireIdentification(DynamoHttpServletRequest a_request)
    {
         boolean l_ret = true;
        
        if (isLoggingDebug())
        {
            logDebug(this.getClass()
                    + "com.castorama.profil.CastoProfileFormHandler.verifierFormulaireIdentification().");
        }
        
        String login = a_request.getParameter("login");
        String password = a_request.getParameter("password");
        
        if (login == null 
                || (login != null && login.equals("")))
        {
            l_ret = false;
        }
        else if (password == null
                || (password != null && password.equals("")))
        {
            l_ret = false;
        }
        return l_ret;
                
    }

    /**
     * @see atg.scenario.userprofiling.ScenarioProfileFormHandler#handleLogin(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     * 
     * @param a_request
     * @param a_response
     * 
     * @throws ServletException
      * @throws IOException
     * 
     */
    public boolean handleLogin(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
    throws ServletException, IOException
    {

        if (isLoggingDebug())
        {
            logDebug(this.getClass() + "com.castorama.profil.CastoProfileFormHandler.handleLogin().");
        }

        boolean l_ret = true;

        String l_login = a_request.getPostParameter(LOGIN_PROPERTY);
        
        try
        {
            
                if (verifierFormulaireIdentification(a_request))
                {
                    
                	/*l_ret = checkFormRedirect(this.getLoginSuccessURL(), getLoginErrorURL(), a_request,
                    		a_response);*/
                    
                        if (isFoUser(l_login) && !isBloqueOuSupprime(l_login))
                        {
                            l_ret = super.handleLogin(a_request, a_response);
                        }
                        else
                        {
                            addFormException(new DropletException("N'est pas un utilisateur Front Office"));
                            a_response.sendLocalRedirect(getLoginErrorURL(), a_request);
                        }
                }
                else
                {
                    addFormException(new DropletException("Veuillez renseigner le login ET le mot de passe."));
                    a_response.sendLocalRedirect(getLoginErrorURL(), a_request);
                    l_ret = false;
                }
            
        }
        catch (Exception l_rpe)
        {
            logError(l_rpe);
        }

        if (isLoggingDebug())
        {
            logDebug(this.getClass() + "com.castorama.profil.CastoProfileFormHandler.handleLogin().");
        }

        return l_ret;
    }

    public boolean isFoUser(String a_login) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(this.getClass() + ".isFoUser");
        }

        boolean l_ret = false;

        Repository l_userRepository = getRepository();

        try
        {
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { a_login });

            if (null != l_userList && 0 < l_userList.length)
            {
                if (null != l_userList[0].getPropertyValue("estUtilisateurFo"))
                {
                    l_ret = ((Boolean) l_userList[0].getPropertyValue("estUtilisateurFo")).booleanValue();
                }
            }
        }
        catch (RepositoryException l_rpe)
        {
            logError(l_rpe.toString());

            throw l_rpe;
        }

        if (isLoggingDebug())
        {
            logDebug(this.getClass() + ".isFoUser end");
        }

        return l_ret;
    }
    
    
    public boolean isBloqueOuSupprime(String a_login) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(this.getClass() + ".isBloqueOuSupprime");
        }

        boolean l_ret = false;

        Repository l_userRepository = getRepository();

        try
        {
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { a_login });

            if (null != l_userList && 0 < l_userList.length)
            {
                if (null != l_userList[0].getPropertyValue("bloque") && l_userList[0].getPropertyValue("bloque").toString().equals("true"))
                {
                    l_ret = true;
                }
                
                if (null != l_userList[0].getPropertyValue("estSupprime") && l_userList[0].getPropertyValue("estSupprime").toString().equals("true"))
                {
                    l_ret = true;
                }
            }
            
        }
        catch (RepositoryException l_rpe)
        {
            logError(l_rpe.toString());

            throw l_rpe;
        }

        if (isLoggingDebug())
        {
            logDebug(this.getClass() + ".isFoUser end");
        }

        return l_ret;
    }
}