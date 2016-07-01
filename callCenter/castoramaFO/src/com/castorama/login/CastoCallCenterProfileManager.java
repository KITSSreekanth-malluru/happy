package com.castorama.login;



import javax.transaction.TransactionManager;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;





public class CastoCallCenterProfileManager extends ApplicationLoggingImpl
{
    /*
     * ------------------------------------------------------------------------ [
     * Constants
     * ------------------------------------------------------------------------
     */

    public static final String PROFIL_TYPE_ADRESSE_LIVRAISON = "1";

    public static final String PROFIL_TYPE_ADRESSE_FACTURATION = "2";

    private static final String CRITERE_RECHERCHE_LOGIN = "login = ?0";

    private static final String PROPRIETE_BLOQUE = "bloque";

    /*
     * ------------------------------------------------------------------------ [
     * Attributes
     * ------------------------------------------------------------------------
     */

    private TransactionManager m_transactionManager;

    private Repository m_userRepository;

    private Repository m_codePostalRepository;

 
    public TransactionManager getTransactionManager()
    {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager a_transactionManager)
    {
        m_transactionManager = a_transactionManager;
    }

    public Repository getUserRepository()
    {
        return m_userRepository;
    }

    public void setUserRepository(Repository a_userRepository)
    {
        m_userRepository = a_userRepository;
    }

    public Repository getCodePostalRepository()
    {
        return m_codePostalRepository;
    }

    public void setCodePostalRepository(Repository a_codePostalRepository)
    {
        m_codePostalRepository = a_codePostalRepository;
    }

    /*
     * ------------------------------------------------------------------------ [
     * Methods
     * ------------------------------------------------------------------------
     */

   
    public RepositoryItem getProfile(String a_login) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(this.getClass() 
                    + "com.castorama.profil.CastoProfilManager.getPasswordUtilisateur().");
        }

        RepositoryItem l_profile = null;

        Repository l_userRepository = getUserRepository();

        try
        {
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView("user");
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { a_login });

            if (null != l_userList && 0 < l_userList.length)
            {
                if (null != l_userList[0].getPropertyValue("password"))
                {
                    l_profile = l_userList[0];
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
            logDebug(this.getClass() 
                    + "com.castorama.profil.CastoProfilManager.getPasswordUtilisateur().");
        }

        return l_profile;
    }

 
}
