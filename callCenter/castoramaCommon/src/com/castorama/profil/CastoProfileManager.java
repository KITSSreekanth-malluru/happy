package com.castorama.profil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.transaction.TransactionManager;

import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.Profile;

import com.castorama.commande.CastoOrder;
import com.castorama.commande.CastoRepositoryContactInfo;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesDefense;
import com.castorama.constantes.CastoConstantesIdentification;

/**
 * @author Florte J�r�my (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Composant pour la gestion des profils utilisateur.
 */
public class CastoProfileManager extends /*Code Review*/ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */

    /**
     * Désigne une adresse de type livraison.
     */
    public static final String PROFIL_TYPE_ADRESSE_LIVRAISON = "1";

    /**
     * Désigne une adresse de type facturation.
     */
    public static final String PROFIL_TYPE_ADRESSE_FACTURATION = "2";

    /**
     * Constante pour la recherche par recherche login utilisateur.
     */
    private static final String CRITERE_RECHERCHE_LOGIN = "login = ?0";

    /**
     * Désigne la propriété "bloque" de l'item descripteur "user".
     */
    private static final String PROPRIETE_BLOQUE = "bloque";

    private static final String PASSWORD = "password";

    private static final String CONTENU = "contenu";

    private static final String MES_SELECTIONS = "mesSelections";

    private static final String DELIMITER = " : =================================================================";
    
    private static final String NEW_LINE = "\n";
    
    private static final String NEWSLETTER = "abonnementNewsletter";
    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */

    /**
     * Référence vers le TransactionManager.
     */
    private TransactionManager m_transactionManager;

    /**
     * Référence vers le user repository.
     */
    private Repository m_userRepository;
    
    /**
     * Référence vers le newsletter repository.
     */
    private Repository m_newsletterRepository;

    /**
     * Référence vers le CodePostalRepository.
     */
    private Repository m_codePostalRepository;
    
    /**
     * SCENARII
     * Référence vers le OrderRepository.
     */
    private Repository m_orderRepository;

    
    private int m_nbClientMax;
    
    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */

    public int getNbClientMax()
    {
        return m_nbClientMax;
    }

    public void setNbClientMax(int a_nbClientMax)
    {
        m_nbClientMax = a_nbClientMax;
    }

    public Repository getOrderRepository()
    {
        return m_orderRepository;
    }

    public void setOrderRepository(Repository a_orderRepository)
    {
        m_orderRepository = a_orderRepository;
    }
    
    public Repository getNewsletterRepository()
    {
        return m_newsletterRepository;
    }

    public void setNewsletterRepository(Repository a_newsletterRepository)
    {
        m_newsletterRepository = a_newsletterRepository;
    }

    /**
     * Retourne l'objet TransactionManager à employer pour les accès au
     * Repository.
     * 
     * @return TransactionManager L'objet TransactionManager à employer pour les
     *         accès au Repository.
     */
    public TransactionManager getTransactionManager()
    {
        return m_transactionManager;
    }

    /**
     * Fixe l'objet TransactionManager à employer pour les accès au Repository.
     * 
     * @param a_transactionManager
     *            L'objet TransactionManager à employer pour les accès au
     *            Repository.
     */
    public void setTransactionManager(TransactionManager a_transactionManager)
    {
        m_transactionManager = a_transactionManager;
    }

    /**
     * Retourne le Repository correspondant au descripteur "user".
     * 
     * @return Repository Le Repository correspondant au descripteur "user".
     */
    public Repository getUserRepository()
    {
        return m_userRepository;
    }

    /**
     * Fixe le Repository correspondant au descripteur "user".
     * 
     * @param a_userRepository
     *            Le Repository correspondant au descripteur "user".
     */
    public void setUserRepository(Repository a_userRepository)
    {
        m_userRepository = a_userRepository;
    }

    /**
     * Renvoit une r�f�rence vers le CodePostalRepository.
     * 
     * @return Repository Une r�f�rence vers le CodePostalRepository.
     */
    public Repository getCodePostalRepository()
    {
        return m_codePostalRepository;
    }

    /**
     * Fixe la r�f�rence vers le CodePostalRepository.
     * 
     * @param a_codePostalRepository
     *            La nouvelle r�f�rence vers le CodePostalRepository.
     */
    public void setCodePostalRepository(Repository a_codePostalRepository)
    {
        m_codePostalRepository = a_codePostalRepository;
    }

    /*
     * ------------------------------------------------------------------------
     * Méthodes
     * ------------------------------------------------------------------------
     */

    /**
     * Fixe l'action utilisateur.
     * 
     * @param a_profile
     *            Le profil utilisateur concern�.
     * @param a_action
     *            L'action.
     */
    public void setActionUtilisateur(Profile a_profile, String a_action)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.profil.CastoProfilManager.setActionUtilisateur().");
        }

        a_profile.setPropertyValue(CastoConstantes.PROPRIETE_ACTION_UTILISATEUR, a_action);

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.profil.CastoProfilManager.setActionUtilisateur().");
        }
    }

    /**
     * Retourne l'action utilisateur du profil demand�.
     * 
     * @param a_profile
     *            Le profil concern�.
     * 
     * @return String L'action utilisateur.
     */
    public String getActionUtilisateur(Profile a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.profil.CastoProfilManager.getActionUtilisateur().");
        }

        String l_action = (String) a_profile.getPropertyValue(CastoConstantes.PROPRIETE_ACTION_UTILISATEUR);

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.profil.CastoProfilManager.getActionUtilisateur().");
        }

        return l_action;
    }

    /**
     * Retourne le nombre de tentatives d'identification d'un internaute.
     * 
     * @param a_profile
     *            Le profil utilisateur concern�e
     * 
     * @return int Le nombre de tentatives d'identification d'un internaute.
     */
    public int getNbTentativesIdentification(Profile a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfileManager.getNbTentativesIdentification().");
        }

        int l_nbTentativesIdentification = 0;

        if (null != a_profile.getPropertyValue(CastoConstantesIdentification.PROFILE_NB_IDENTIFICATION))
        {
            l_nbTentativesIdentification = ((Integer) a_profile
                    .getPropertyValue(CastoConstantesIdentification.PROFILE_NB_IDENTIFICATION)).intValue();
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfileManager.getNbTentativesIdentification().");
        }

        return l_nbTentativesIdentification;
    }

    /**
     * Fixe le nombre de tentatives d'identification du prodil demand�e.
     * 
     * @param a_profile
     *            Le profil concern�
     * @param a_nbTentativesIdentification
     *            Le nombre de tentatives d'identification
     */
    public void setNbTentativesIdentification(Profile a_profile, int a_nbTentativesIdentification)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfileManager.setNbTentativesIdentification().");
        }

        a_profile.setPropertyValue(CastoConstantesIdentification.PROFILE_NB_IDENTIFICATION, new Integer(
                a_nbTentativesIdentification));

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfileManager.setNbTentativesIdentification().");
        }
    }

    /**
     * D�sactive un compte utilisateur.
     * 
     * @param a_login
     *            Le login du compte � d�sactiver.
     * 
     * @throws RepositoryException
     *             Si une exception survient.
     */
    public void desactiverCompteUtilisateur(String a_login) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.desactiverCompteUtilisateur().");
        }

        // Obtention du Repository pour update
        MutableRepository l_userRepository = (MutableRepository) getUserRepository();

        try
        {
            // Obtention de l'item qui doit �tre mis � jour
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { a_login });

            if (null != l_userList && 0 < l_userList.length)
            {
                // Obtention de l'item en mise � jour
                MutableRepositoryItem l_user = l_userRepository.getItemForUpdate(l_userList[0].getRepositoryId(),
                        CastoConstantes.DESCRIPTEUR_UTILISATEUR);

                if (null != l_user)
                {
                    l_user.setPropertyValue(PROPRIETE_BLOQUE, Boolean.valueOf(true));
                    l_userRepository.updateItem(l_user);
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
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.desactiverCompteUtilisateur().");
        }
    }

    /**
     * V�rifie si un compte utilisateur est bloqu� ou non.
     * 
     * @param a_login
     *            Le login du compte � v�rifier.
     * 
     * @return boolean True si le compte est bloqu�, false sinon.
     * 
     * @throws RepositoryException
     *             Si une exception survient.
     */
    public boolean isCompteUtilisateurBloque(String a_login) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.IsCompteUtilisateurBloque().");
        }

        boolean l_ret = false;

        // Obtention du Repository
        Repository l_userRepository = getUserRepository();

        try
        {
            // Obtention de l'item
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { a_login });

            if (null != l_userList && 0 < l_userList.length)
            {
                if (null != l_userList[0].getPropertyValue(PROPRIETE_BLOQUE))
                {
                    l_ret = ((Boolean) l_userList[0].getPropertyValue(PROPRIETE_BLOQUE)).booleanValue();
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
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.IsCompteUtilisateurBloque().");
        }

        return l_ret;
    }

    /**
     * Retourne le mot de passe de l'utilisateur dont le login est pass� en
     * param�tre.
     * 
     * @param a_login
     *            Le login de l'utilisateur.
     * 
     * @return String Le mot de passe de l'utilisateur dont le login est
     *         psLogin.
     * 
     * @throws RepositoryException
     *             Si une exception survient lors de la recherche dans le
     *             Repository.
     */
    public RepositoryItem getProfile(String a_login) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.getPasswordUtilisateur().");
        }

        RepositoryItem l_profile = null;

        // Obtention du Repository
        Repository l_userRepository = getUserRepository();

        try
        {
            // Obtention de l'item
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
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
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.getPasswordUtilisateur().");
        }

        return l_profile;
    }

    /**
     * . J.R. Méthode qui vérifie si un utilisateur existe deja avec a_login et
     * a_password.
     * 
     * @param a_login
     *            le login.
     * @param a_password
     *            le password.
     * @return le user s'il existe.
     * @throws RepositoryException
     *             Si une erreur survient.
     */
    public RepositoryItem getProfile(String a_login, String a_password) throws RepositoryException
    {
        RepositoryItem l_profile = getProfile(a_login);

        if (null != l_profile)
        {
            if (isLoggingDebug())
            {
                logDebug("le password correspondant au login entre : " + l_profile.getPropertyValue(PASSWORD));
                logDebug("le login entre : " + a_password);
            }

            if (l_profile.getPropertyValue(PASSWORD).equals(a_password))
            {
                return l_profile;
            }
            else
            {
                l_profile = null;
            }
        }

        return l_profile;
    }

    /**
     * M�thode qui copie une adresse de facturation ou de livraison vers une
     * adresse principale (facturation ou livraison) du profil.
     * 
     * @param a_profile
     *            Le profil � mettre � jour.
     * @param a_type
     *            Indique s'il s'agit d'un type facturation ou livraison.
     * @param a_newAddress
     *            La nouvelle adresse.
     * 
     * @throws RepositoryException
     *             Si une exception survient.
     */
    public void copierAdresse(Profile a_profile, String a_type, CastoRepositoryContactInfo a_newAddress)
            throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.profil.CastoProfilManager.copierAdresse().");
        }

        String l_propertyAddress = (PROFIL_TYPE_ADRESSE_LIVRAISON.equals(a_type)) ? "shippingAddress"
                : "billingAddress";

        /*
         * On teste si le profil a d�j� une adresse ou si cette adresse est vide
         */
        if (null == a_profile.getPropertyValue(l_propertyAddress)
                || (null != a_profile.getPropertyValue(l_propertyAddress) && null == ((RepositoryItem) a_profile
                        .getPropertyValue(l_propertyAddress)).getPropertyValue("lastName")))
        {
            MutableRepository l_mutableRepo = (MutableRepository) getUserRepository();

            /*
             * Cr�ation d'un repository item de type contactInfo
             */
            MutableRepositoryItem l_item = l_mutableRepo.createItem("contactInfo");

            /*
             * Affectation de ses propri�t�s
             */
            l_item.setPropertyValue("civilite", a_newAddress.getCivilite());
            l_item.setPropertyValue("lastName", a_newAddress.getLastName());
            l_item.setPropertyValue("firstName", a_newAddress.getFirstName());
            l_item.setPropertyValue("societe", a_newAddress.getCompanyName());
            l_item.setPropertyValue("address1", a_newAddress.getAddress1());
            l_item.setPropertyValue("address2", a_newAddress.getAddress2());
            l_item.setPropertyValue("address3", a_newAddress.getAddress3());
            l_item.setPropertyValue("country", a_newAddress.getCountry());
            l_item.setPropertyValue("postalCode", a_newAddress.getPostalCode());
            l_item.setPropertyValue("city", a_newAddress.getCity());
            l_item.setPropertyValue("phoneNumber", a_newAddress.getPhoneNumber());
            l_item.setPropertyValue("telephonePro", a_newAddress.getTelephonePro());
            l_item.setPropertyValue("gsm", a_newAddress.getTelephonePortable());
            l_item.setPropertyValue("faxNumber", a_newAddress.getFaxNumber());
            l_item.setPropertyValue("eMail", a_newAddress.getEmail());
            l_item.setPropertyValue("numTva", a_newAddress.getTVAIntracommunautaire());
            l_item.setPropertyValue("adresseActive", new Integer(1));

            /*
             * Enregistrement en base
             */
            l_mutableRepo.addItem(l_item);

            /*
             * Modification des donn�es du profil
             */
            MutableRepositoryItem l_profil = l_mutableRepo.getItemForUpdate(a_profile.getRepositoryId(), "user");

            l_profil.setPropertyValue(l_propertyAddress, l_item);

            l_mutableRepo.updateItem(l_profil);

            /*
             * Modification du profil en m�moire
             */
            a_profile.setPropertyValue(l_propertyAddress, l_item);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.profil.CastoProfilManager.copierAdresse().");
        }
    }

    /**
     * M�thode qui renvoit pour un code postal donn� la ville correspondante.
     * 
     * @param a_codePostal
     *            Le code postal.
     * 
     * @return Strin La ville correspondante au code postal pass� en param�tre.
     * 
     * @throws RepositoryException
     *             Si une erreur survient.
     */
    public List getVilleParCodePostal(String a_codePostal) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.getVilleParCodePostal().");
        }

        List l_villes = new ArrayList();

        // Obtention du Repository
        Repository l_userRepository = getCodePostalRepository();

        try
        {
            if (isLoggingDebug())
            {
                logDebug("|--> code postal : " + a_codePostal);
            }

            // Obtention de l'item
            RqlStatement l_findVille = RqlStatement.parseRqlStatement("code_postal = ?0");
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_CODE_POSTAL);
            RepositoryItem[] l_villeList = l_findVille.executeQuery(l_view, new Object[]
            { a_codePostal });

            if (null != l_villeList)
            {
                int l_size = l_villeList.length;

                for (int l_i = 0; l_i < l_size; l_i++)
                {
                    if (isLoggingDebug())
                    {
                        logDebug("|--> ville : " + l_villeList[l_i].getPropertyValue("ville").toString());
                    }

                    l_villes.add(l_villeList[l_i].getPropertyValue("ville").toString());
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
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.getVilleParCodePostal().");
        }
        return l_villes;
    }

    /**
     * Active/d�sactive la connexion automatique d'un profil.
     * 
     * @param a_login
     *            Le login du compte � d�sactiver.
     * @param a_autoLogin
     *            Indique si le profil se connecte automatiquement.
     * 
     * @throws RepositoryException
     *             Si une exception survient.
     */
    public void setAutoLogin(String a_login, boolean a_autoLogin) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.profil.CastoProfilManager.setAutoLogin().");
        }

        // Obtention du Repository pour update
        MutableRepository l_userRepository = (MutableRepository) getUserRepository();

        try
        {
            // Obtention de l'item qui doit �tre mis � jour
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { a_login });

            if (null != l_userList && 0 < l_userList.length)
            {
                // Obtention de l'item en mise � jour
                MutableRepositoryItem l_user = l_userRepository.getItemForUpdate(l_userList[0].getRepositoryId(),
                        CastoConstantes.DESCRIPTEUR_UTILISATEUR);

                if (null != l_user)
                {
                    l_user.setPropertyValue("autoLogin", Boolean.valueOf(a_autoLogin));
                    l_userRepository.updateItem(l_user);
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
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.profil.CastoProfilManager.setAutoLogin().");
        }
    }

    /**
     * M�thode qui sauvegarde dans les s�lections de l'internaute la liste des
     * articles du panier en cours.
     * 
     * @param a_order
     *            La commande en cours.
     * @param a_profile
     *            Le profil de l'utilisateur.
     * 
     * @return boolean True si la sauvegarde s'est bien pass�e, false sinon.
     */
    public boolean sauvegarderSelection(Order a_order, Profile a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.profil.CastoProfilManager.sauvegarderSelection().");
        }

        boolean l_ret = true;

        // Obtention du Repository pour update
        MutableRepository l_userRepository = (MutableRepository) getUserRepository();

        try
        {
            // R�cup�ration des donn�es de l'utilisateur
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { (String) a_profile.getPropertyValue("login") });

            if (null != l_userList && 0 != l_userList.length)
            {
                // Obtention de l'item en mise � jour
                MutableRepositoryItem l_user = l_userRepository.getItemForUpdate(l_userList[0].getRepositoryId(),
                        CastoConstantes.DESCRIPTEUR_UTILISATEUR);

                if (null != l_user)
                {
                    // On cr�e un item s�lection
                    MutableRepositoryItem l_selection = l_userRepository.createItem("selection");

                    if (null != l_selection)
                    {
                        // On fixe les propri�t�s de la s�lection
                        l_selection.setPropertyValue("libelle", "Mon panier");
                        l_selection.setPropertyValue("creationDate", new Date());

                        /*
                         * On ajoute la liste des skus.
                         */
                        Set l_articles = new HashSet();
                        ShippingGroup l_shippingGroup = null;
                        CommerceItemRelationship l_relationShip = null;

                        // On parcourt la liste des articles de la commande
                        for (Iterator l_shippingGroups = a_order.getShippingGroups().iterator(); l_shippingGroups
                                .hasNext();)
                        {
                            l_shippingGroup = (ShippingGroup) l_shippingGroups.next();

                            for (Iterator l_relationsShips = l_shippingGroup.getCommerceItemRelationships().iterator(); l_relationsShips
                                    .hasNext();)
                            {
                                l_relationShip = (CommerceItemRelationship) l_relationsShips.next();

                                /*
                                 * Pour chaque article, on cr�e un item de type
                                 * "contenu".
                                 */
                                MutableRepositoryItem l_contenu = l_userRepository.createItem(CONTENU);

                                l_contenu.setPropertyValue("quantite", new Integer((int) l_relationShip
                                        .getCommerceItem().getQuantity()));
                                l_contenu.setPropertyValue("sku", l_relationShip.getCommerceItem().getAuxiliaryData()
                                        .getCatalogRef());

                                l_articles.add(l_contenu);

                                // On sauvegarde le contenu
                                l_userRepository.addItem(l_contenu);
                            }
                        }

                        // On met � jour la propri�t� "contenu" de la s�lection
                        l_selection.setPropertyValue("contenu", l_articles);

                        // On ins�re la s�lection
                        l_userRepository.addItem(l_selection);

                        // On met � jour la propri�t� "selection" du user
                        Set l_selectionsUser = (Set) l_user.getPropertyValue(MES_SELECTIONS);

                        l_selectionsUser.add(l_selection);

                        l_user.setPropertyValue("mesSelections", l_selectionsUser);

                        // On sauvegarde l'utilisateur
                        l_userRepository.updateItem(l_user);
                    }
                }
            }
        }
        catch (RepositoryException l_repositoryException)
        {
            logError("Impossible de sauvegarder la s�lection : " + l_repositoryException.toString());

            l_ret = false;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.profil.CastoProfilManager.sauvegarderSelection().");
        }
        return l_ret;
    }

    /**
     * Méthode qui sauvegarde dans les listes de courses de l'internaute la
     * liste des articles du panier en cours.
     * 
     * @param a_order
     *            La commande en cours.
     * @param a_profile
     *            Le profil de l'utilisateur.
     * 
     * @return boolean True si la sauvegarde s'est bien pass�e, false sinon.
     */
    public boolean sauvegarderListeDeCourses(Order a_order, Profile a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.sauvegarderListeDeCourses().");
        }

        boolean l_ret = true;

        // Obtention du Repository pour update
        MutableRepository l_userRepository = (MutableRepository) getUserRepository();

        try
        {
            // Récupération des données de l'utilisateur
            RqlStatement l_findUser = RqlStatement.parseRqlStatement(CRITERE_RECHERCHE_LOGIN);
            RepositoryView l_view = l_userRepository.getView(CastoConstantes.DESCRIPTEUR_UTILISATEUR);
            RepositoryItem[] l_userList = l_findUser.executeQuery(l_view, new Object[]
            { (String) a_profile.getPropertyValue("login") });

            if (null != l_userList && 0 != l_userList.length)
            {
                // Obtention de l'item en mise à jour
                MutableRepositoryItem l_user = l_userRepository.getItemForUpdate(l_userList[0].getRepositoryId(),
                        CastoConstantes.DESCRIPTEUR_UTILISATEUR);

                if (null != l_user)
                {
                    // On crée un item s�lection
                    MutableRepositoryItem l_selection = l_userRepository.createItem("selection");

                    if (null != l_selection)
                    {
                        // On fixe les propriétés de la sélection
                        l_selection.setPropertyValue("libelle", "Mon panier");
                        l_selection.setPropertyValue("creationDate", new Date());

                        /*
                         * On ajoute la liste des skus.
                         */
                        Set l_articles = new HashSet();
                        ShippingGroup l_shippingGroup = null;
                        CommerceItemRelationship l_relationShip = null;

                        // On parcourt la liste des articles de la commande
                        for (Iterator l_shippingGroups = a_order.getShippingGroups().iterator(); l_shippingGroups
                                .hasNext();)
                        {
                            l_shippingGroup = (ShippingGroup) l_shippingGroups.next();

                            for (Iterator l_relationsShips = l_shippingGroup.getCommerceItemRelationships().iterator(); l_relationsShips
                                    .hasNext();)
                            {
                                l_relationShip = (CommerceItemRelationship) l_relationsShips.next();

                                /*
                                 * Pour chaque article, on crée un item de type
                                 * "contenu".
                                 */
                                MutableRepositoryItem l_contenu = l_userRepository.createItem(CONTENU);

                                l_contenu.setPropertyValue("quantite", new Integer((int) l_relationShip
                                        .getCommerceItem().getQuantity()));
                                l_contenu.setPropertyValue("sku", l_relationShip.getCommerceItem().getAuxiliaryData()
                                        .getCatalogRef());

                                l_articles.add(l_contenu);

                                // On sauvegarde le contenu
                                l_userRepository.addItem(l_contenu);
                            }
                        }

                        // On met à jour la propriété "contenu" de la liste de
                        // courses
                        l_selection.setPropertyValue(CONTENU, l_articles);

                        // On insère la sélection
                        l_userRepository.addItem(l_selection);

                        // On met à jour la propriété "liste de courses" du user
                        Set l_selectionsUser = (Set) l_user.getPropertyValue(MES_SELECTIONS);

                        l_selectionsUser.add(l_selection);

                        l_user.setPropertyValue(MES_SELECTIONS, l_selectionsUser);

                        // On sauvegarde l'utilisateur
                        l_userRepository.updateItem(l_user);
                    }
                }
            }
        }
        catch (RepositoryException l_repositoryException)
        {
            logError("Impossible de sauvegarder la s�lection : " + l_repositoryException.toString());

            l_ret = false;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.sauvegarderListeDeCourses().");
        }
        return l_ret;
    }

    /**
     * Méthode qui indique si un profil donné est en session Castorama direct ou
     * non.
     * 
     * @param a_profile
     *            Le profil de l'internaute.
     * 
     * @return true si le profil est session Castorama direct, false sinon ou en
     *         cas d'échec.
     */
    public boolean isSessionCastoramaDirect(Profile a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.isSessionCastoramaDirect(Profile).");
        }

        boolean l_ret;

        if (null != a_profile)
        {
            Object l_prop = a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);

            if (null != l_prop)
            {
                l_ret = CastoConstantesDefense.CASTORAMA_DIRECT.equals(((RepositoryItem) l_prop).getRepositoryId());
            }
            else
            {
                logError("CastoProfilManager.isSessionCastoramaDirect(Profile) : propriété non renseignée.");

                l_ret = false;
            }
        }
        else
        {
            logError("CastoProfilManager.isSessionCastoramaDirect(Profile) : objet Profile est null.");

            l_ret = false;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.isSessionCastoramaDirect(Profile).");
        }

        return l_ret;
    }

    /**
     * Méthode qui indique si un profil donné est en session Castorama direct ou
     * non.
     * 
     * @param a_profile
     *            Le profil de l'internaute.
     * 
     * @return true si le profil est session Castorama direct, false sinon ou en
     *         cas d'échec.
     */
    public boolean isSessionCastoramaDirect(RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.isSessionCastoramaDirect(RepositoryItem).");
        }

        boolean l_ret;

        if (null != a_profile)
        {
            Object l_prop = a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);

            if (null != l_prop)
            {
                l_ret = CastoConstantesDefense.CASTORAMA_DIRECT.equals(((RepositoryItem) l_prop).getRepositoryId());
            }
            else
            {
                logError("CastoProfilManager.isSessionCastoramaDirect(RepositoryItem) : propriété non renseignée.");

                l_ret = false;
            }
        }
        else
        {
            logError("CastoProfilManager.isSessionCastoramaDirect(RepositoryItem) : objet Profile est null.");

            l_ret = false;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.isSessionCastoramaDirect(RepositoryItem).");
        }

        return l_ret;
    }

    /**
     * Méthode qui indique si une commande est une commande Castorama Direct ou
     * PLD.
     * 
     * @param a_order
     *            La commande à tester.
     * 
     * @return True si la commande est une commande Castorama Direct, false
     *         sinon.
     */
    public boolean isSessionCastoramaDirect(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.profil.CastoProfilManager.isSessionCastoramaDirect(CastoOrder).");
        }

        boolean l_ret;

        if (null == a_order)
        {
            logError("CastoProfilManager.isSessionCastoramaDirect(CastoOrder) : objet Order est null.");

            l_ret = false;
        }
        else
        {

                RepositoryItem l_magasin = a_order.getOrigineMagasin();

                if (null == l_magasin)
                {
                    logError("CastoProfilManager.isSessionCastoramaDirect(CastoOrder) : propriété origine magasin nulle.");
                    
                    l_ret = true;
                }
                else
                {
                    l_ret = CastoConstantesDefense.CASTORAMA_DIRECT.equals(l_magasin.getRepositoryId());
                }

        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.profil.CastoProfilManager.isSessionCastoramaDirect(CastoOrder).");
        }

        return l_ret;
    }
    
    /**
     * SCENARII
     * Cete méthode initialise la date de dernière commande des profiles.
     * Logiquement cette méthode ne sera utilisée qu'une fois au lancement du scénario 1.2 du site v3.2
     */
    public void initializeDateDerniereCommandeProfile(String a_phase)
    {
        String l_maClasse = this.getClass().toString();
        
        if (isLoggingDebug())
        {
            StringBuffer l_buffer = new StringBuffer();
            
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(" : == INITIALISATION DE LA DATE DE DERNIERE COMMANDE DES PROFILS  ==").append(NEW_LINE);
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(" : début de la méthode initializeDateDerniereCommandeProfile.");

            logDebug(l_buffer.toString());
        }
        
        int l_borneInf=0;
        Integer l_nbClientsMax = new Integer(getNbClientMax()); 
        String  l_token;
        /*
         * On découpe en tokens les arguments de la ligne de commandes pour en extraire la borne d'extration 
         * inférieure et le nombre de skus à extraire à partir de cette borne
         */
        if(null != a_phase && !"".equals(a_phase))
        {
            StringTokenizer l_tokenizer = new StringTokenizer(a_phase, "!"); 
            if(null!=l_tokenizer && l_tokenizer.hasMoreTokens())
            {
                l_token = l_tokenizer.nextToken(); 
            }
            if(null!=l_tokenizer && l_tokenizer.hasMoreTokens())
            {
                l_token = l_tokenizer.nextToken(); 
                l_borneInf = Integer.parseInt(l_token);
            }
        }
        if (isLoggingInfo())
        {
            logInfo("borneInf= " + l_borneInf);
            logInfo("nbClientsPris= " + l_nbClientsMax);
        }
        
        
        
        try
        {
            // Récupération de tous les profils utilisateurs 
            RepositoryItemDescriptor l_itemDescriptor = getUserRepository().getItemDescriptor("user");
            RepositoryView l_repositoryView = l_itemDescriptor.getRepositoryView();
            RqlStatement l_statement = RqlStatement.parseRqlStatement("ALL RANGE order by id ?0+?1");
            Object[] l_profils = l_statement.executeQuery(l_repositoryView, new Object[]{new Integer(l_borneInf), l_nbClientsMax});
            
            int l_nombreProfils = ( null != l_profils ) ? l_profils.length : 0;
            
            // parcours des profils et mise à jour de la date de dernière commande
            for (int l_i = 0 ; l_i < l_nombreProfils ; l_i++)
            {
                MutableRepository l_mutUserRepo = (MutableRepository)getUserRepository();
                MutableRepositoryItem l_mutUser = (MutableRepositoryItem)l_profils[l_i];
                
                RepositoryItemDescriptor l_itemDescriptorOrder = getOrderRepository().getItemDescriptor("order");
                RepositoryView l_repositoryViewOrder = l_itemDescriptorOrder.getRepositoryView();
                RqlStatement l_statementOrder = RqlStatement.parseRqlStatement("profileId = ?0 order by creationDate desc");
                Object[] l_orders = l_statementOrder.executeQuery(l_repositoryViewOrder, new Object[]{l_mutUser.getRepositoryId()});
                
                if (null != l_orders && l_orders.length != 0)
                {
                    RepositoryItem l_orderRescent = (RepositoryItem)l_orders[0];
                    l_mutUser.setPropertyValue("dateDerniereCommande", l_orderRescent.getPropertyValue("creationDate"));
                    
                    if (isLoggingInfo())
                    {
                        logInfo(l_maClasse + " : mise à jour du profil " + l_mutUser.getRepositoryId() + " : " + l_i);    
                    }
                    
                    l_mutUserRepo.updateItem(l_mutUser);
                }
            }
        }
        catch (RepositoryException l_re)
        {
            logError(l_maClasse + " : -ERREUR 1- : " + l_re.toString());
        }
                
        if (isLoggingDebug())
        {
            StringBuffer l_buffer = new StringBuffer();
            
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(" : == FIN INITIALISATION DE LA DATE DE DERNIERE COMMANDE DES PROFILS").append(NEW_LINE);
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(DELIMITER).append(NEW_LINE);
            l_buffer.append(l_maClasse).append(" : fin de la méthode initializeDateDerniereCommandeProfile.");

            logDebug(l_buffer.toString());
        }
    }
    
    /**
     * Méthode permettant de créer un abonnement à la newsletter.
     * @param a_profile Le Profil
     */
    public void createAbonnementNewsletter(RepositoryItem a_profile)
    {
        try{
            MutableRepository       l_MutableRepository = (MutableRepository) getUserRepository();
            MutableRepository       l_MutableRepositoryNewsletter = (MutableRepository) getNewsletterRepository();
            MutableRepositoryItem   l_MutableUser       = l_MutableRepository.getItemForUpdate(a_profile.getPropertyValue("id").toString(),"user");
                       
            if (l_MutableUser!=null)
            {
                
                MutableRepositoryItem l_oNewsletter = l_MutableRepositoryNewsletter.createItem(
                        l_MutableUser.getPropertyValue("login").toString(),NEWSLETTER);               

                l_oNewsletter.setPropertyValue("isValid", Boolean.TRUE);

                l_oNewsletter.setPropertyValue("dateInscription", new Date());

                l_MutableRepositoryNewsletter.updateItem(l_oNewsletter);

                RepositoryItem l_abo = l_MutableRepositoryNewsletter.getItem(
                        l_MutableUser.getPropertyValue("login").toString(), NEWSLETTER);

                l_MutableUser.setPropertyValue(NEWSLETTER,l_abo);
                l_MutableRepository.updateItem(l_MutableUser);
            }
        }
        catch(RepositoryException l_re)
        {
            logError(l_re.toString());
        }
    }
}