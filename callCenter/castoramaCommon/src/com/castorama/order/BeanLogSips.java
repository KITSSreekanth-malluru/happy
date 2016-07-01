package com.castorama.order;

import java.util.Calendar;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;

import com.castorama.constantes.CastoConstantes;

/**
 * BeanLogSips : Castorama 2001 Ce composant est utilise les logs de paiement
 * SIPS.
 * 
 * @version 1.0
 * 
 * @author Sebastien Deman - INTERNENCE (mai 2001)
 */
public class BeanLogSips extends /* Code Review */ApplicationLoggingImpl/* Code Review */
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Code retour SIPS : paiement accept�.
     */
    public static final int CODE_RETOUR_PAIEMENT_ACCEPTE = 0;

    /**
     * Code retour SIPS : paiement refus�.
     */
    public static final int CODE_RETOUR_PAIEMENT_REFUSE = 2;

    /**
     * Code retour SIPS : identifiant commer�ant invalide.
     */
    public static final int CODE_RETOUR_MERCHANT_ID_INVALIDE = 3;

    /**
     * Code retour SIPS : refus de paiement.
     */
    public static final int CODE_RETOUR_REFUS_PAIEMENT = 5;

    /**
     * Code retour SIPS : transaction invalide.
     */
    public static final int CODE_RETOUR_TRANSACTION_INVALIDE = 12;

    /**
     * Code retour SIPS : montant invalide.
     */
    public static final int CODE_RETOUR_MONTANT_INVALIDE = 13;

    /**
     * Code retour SIPS : identifiant client invalide.
     */
    public static final int CODE_RETOUR_ID_CLIENT_INVALIDE = 14;

    /**
     * Code retour SIPS : annulation client.
     */
    public static final int CODE_RETOUR_ANNULATION_CLIENT = 17;

    /**
     * Code retour SIPS : erreur de format.
     */
    public static final int CODE_RETOUR_ERREUR_FORMAT = 30;

    /**
     * Code retour SIPS : r�gles de s�curit� non respect�e.
     */
    public static final int CODE_RETOUR_ERREUR_SECURITE = 63;

    /**
     * Code retour SIPS : nombre maximum d'essais atteint.
     */
    public static final int CODE_RETOUR_NB_ESSAIS_MAX = 75;

    /**
     * Code retour SIPS : service indisponible.
     */
    public static final int CODE_RETOUR_SERVICE_INDISPONIBLE = 90;

    /**
     * Code retour SIPS : transaction d�j� trait�e.
     */
    public static final int CODE_RETOUR_TRANSACTION_DEJA_TRAITEE = 94;

    /**
     * Libell� retour SIPS : paiement accept�.
     */
    private static final String LIBELLE_PAIEMENT_ACCEPTE = "accept�";

    /**
     * Libell� retour SIPS : paiement refus�.
     */
    private static final String LIBELLE_PAIEMENT_REFUSE = "refus de paiement";

    /**
     * Libell� retour SIPS : identifiant commer�ant invalide.
     */
    private static final String LIBELLE_MERCHANT_ID_INVALIDE = "MERCHANT_ID invalide";

    /**
     * Libell� retour SIPS : refus de paiement.
     */
    private static final String LIBELLE_REFUS_PAIEMENT = "refus du paiement";

    /**
     * Libell� retour SIPS : transaction invalide.
     */
    private static final String LIBELLE_TRANSACTION_INVALIDE = "transaction invalide";

    /**
     * Libell� retour SIPS : montant invalide.
     */
    private static final String LIBELLE_MONTANT_INVALIDE = "montant invalide";

    /**
     * Libell� retour SIPS : identifiant client invalide.
     */
    private static final String LIBELLE_ID_CLIENT_INVALIDE = "CUSTOMER_ID invalide";

    /**
     * Libell� retour SIPS : annulation client.
     */
    private static final String LIBELLE_ANNULATION_CLIENT = "annulation de l'internaute";

    /**
     * Libell� retour SIPS : erreur de format.
     */
    private static final String LIBELLE_ERREUR_FORMAT = "erreur de format";

    /**
     * Libell� retour SIPS : r�gles de s�curit� non respect�e.
     */
    private static final String LIBELLE_ERREUR_SECURITE = "r�gles de s�curit� non respect�es";

    /**
     * Libell� retour SIPS : nombre maximum d'essais atteint.
     */
    private static final String LIBELLE_NB_ESSAIS_MAX = "nombre maximum essais";

    /**
     * Libell� retour SIPS : service indisponible.
     */
    private static final String LIBELLE_SERVICE_INDISPONIBLE = "service indisponible";

    /**
     * Libell� retour SIPS : transaction d�j� trait�e.
     */
    private static final String LIBELLE_TRANSACTION_DEJA_TRAITEE = "transaction d�j� trait�e";

    /**
     * Repr�sente la fin d'une ann�e dans le substring d'une date.
     */
    private static final int INDEX_FIN_ANNEE = 4;

    /**
     * Repr�sente la fin d'un mois dans le substring d'une date.
     */
    private static final int INDEX_FIN_MOIS = 6;

    /**
     * Repr�sente la fin d'un jour dans le substring d'une date.
     */
    private static final int INDEX_FIN_JOUR = 8;

    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    private Repository m_Repository;

    private int m_n_orderValidity;

    private String m_strOrderId = "";

    private String m_strResponseCode = "";

    private int m_nMontant = 0;

    private String m_strTransactionId = "";

    private String m_strAuthorisationId = "";

    private String m_strPaymentMeans = "";

    private String m_strCardNumber = "";

    private String m_strPaymentCertificate = "";

    private String m_strPaymentTime = "";

    private String m_strPaymentDate = "";

    private String m_strDevise = "";

    private String m_strProfileId = "";

    // private String m_strDateTransExpire = ""; // date limite de validit� de
    // la

    // transaction = date de passage
    // de la transaction +
    // orderValidity

    private String m_strValidTout = "";

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * ??.
     * 
     * @param a_Repository
     *            ??.
     */
    public void setRepository(Repository a_Repository)
    {
        m_Repository = a_Repository;
    }

    /**
     * ??.
     * 
     * @return Repository ??.
     */
    public Repository getRepository()
    {
        return m_Repository;
    }

    /**
     * ORDER VALIDITY (nombre de jours de validit� de la transaction).
     * 
     * @param a_orderValidity
     *            ??.
     */
    public void setOrderValidity(int a_orderValidity)
    {
        m_n_orderValidity = a_orderValidity;
    }

    /**
     * ??.
     * 
     * @return int ??.
     */
    public int getOrderValidity()
    {
        return m_n_orderValidity;
    }

    /**
     * ORDER_ID.
     * 
     * @param a_strOderId
     *            ??.
     */
    public void setOrderId(String a_strOderId)
    {
        m_strOrderId = a_strOderId;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getOrderId()
    {
        return m_strOrderId;
    }

    /**
     * RESPONSE_CODE.
     * 
     * @param a_strResponseCode
     *            ??.
     */
    public void setResponseCode(String a_strResponseCode)
    {
        m_strResponseCode = a_strResponseCode;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getResponseCode()
    {
        return m_strResponseCode;
    }

    /**
     * MONTANT.
     * 
     * @param a_nMontant
     *            ??.
     */
    public void setMontant(int a_nMontant)
    {
        m_nMontant = a_nMontant;
    }

    /**
     * ??.
     * 
     * @return int ??.
     */
    public int getMontant()
    {
        return m_nMontant;
    }

    /**
     * TRANSACTION_ID.
     * 
     * @param a_strTransactionId
     *            ??.
     */
    public void setTransactionId(String a_strTransactionId)
    {
        m_strTransactionId = a_strTransactionId;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getTransactionId()
    {
        return m_strTransactionId;
    }

    /**
     * AUTHORISATION_ID.
     * 
     * @param a_strAuthorisationId
     *            ??.
     */
    public void setAuthorisationId(String a_strAuthorisationId)
    {
        m_strAuthorisationId = a_strAuthorisationId;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getAuthorisationId()
    {
        return m_strAuthorisationId;
    }

    /**
     * PAYMENT_MEANS.
     * 
     * @param a_strPaymentMeans
     *            ??.
     */
    public void setPaymentMeans(String a_strPaymentMeans)
    {
        m_strPaymentMeans = a_strPaymentMeans;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getPaymentMeans()
    {
        return m_strPaymentMeans;
    }

    /**
     * CARD_NUMBER.
     * 
     * @param a_strCardNumber
     *            ??.
     */
    public void setCardNumber(String a_strCardNumber)
    {
        m_strCardNumber = a_strCardNumber;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getCardNumber()
    {
        return m_strCardNumber;
    }

    /**
     * PAYMENT_CERTIFICATE.
     * 
     * @param a_strPaymentCertificate
     *            ??.
     */
    public void setPaymentCertificate(String a_strPaymentCertificate)
    {
        m_strPaymentCertificate = a_strPaymentCertificate;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getPaymentCertificate()
    {
        return m_strPaymentCertificate;
    }

    /**
     * PAYMENT_TIME.
     * 
     * @param a_strPaymentTime
     *            ??.
     */
    public void setPaymentTime(String a_strPaymentTime)
    {
        m_strPaymentTime = a_strPaymentTime;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getPaymentTime()
    {
        return m_strPaymentTime;
    }

    /**
     * PAYMENT_DATE.
     * 
     * @param a_strPaymentDate
     *            ??.
     */
    public void setPaymentDate(String a_strPaymentDate)
    {
        m_strPaymentDate = a_strPaymentDate;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getPaymentDate()
    {
        return m_strPaymentDate;
    }

    /**
     * DEVISE.
     * 
     * @param a_strDevise
     *            ??.
     */
    public void setDevise(String a_strDevise)
    {
        m_strDevise = a_strDevise;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getDevise()
    {
        return m_strDevise;
    }

    /**
     * PROFILE_ID.
     * 
     * @param a_strProfileId
     *            ??.
     */
    public void setProfileId(String a_strProfileId)
    {
        m_strProfileId = a_strProfileId;
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getProfileId()
    {
        return m_strProfileId;
    }

    /**
     * VALID TOUT.
     * 
     * @param a_varBidon
     *            ??.
     */
    public void setValidTout(String a_varBidon)
    {
        createLog();
    }

    /**
     * ??.
     * 
     * @return String ??.
     */
    public String getValidTout()
    {
        return m_strValidTout;
    }

    /*
     * ------------------------------------------------------------------------ [
     * M�thodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * ??.
     */
    public void createLog()
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.order.BeanLogSips.createLog()");
        }

        try
        {
            /*
             * On convertit le code retour SISP en entier, permet d'employer un
             * switch : plus rapide que les if/else if imbriqu�s.
             */
            int l_responseCode = Integer.parseInt(m_strResponseCode);

            if (CODE_RETOUR_PAIEMENT_ACCEPTE == l_responseCode || CODE_RETOUR_PAIEMENT_REFUSE == l_responseCode
                    || CODE_RETOUR_REFUS_PAIEMENT == l_responseCode || CODE_RETOUR_ANNULATION_CLIENT == l_responseCode
                    || CODE_RETOUR_NB_ESSAIS_MAX == l_responseCode)
            {
                String l_strLibelle = getLibelle(l_responseCode);

                /*
                 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                 * calculer la date limite de validit� de la transaction
                 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                 */

                // D�coupage de la date
                String l_strAnneeTransaction = m_strPaymentDate.substring(0, INDEX_FIN_ANNEE);
                String l_strMoisTransaction = m_strPaymentDate.substring(INDEX_FIN_ANNEE, INDEX_FIN_MOIS);
                String l_strJourTransaction = m_strPaymentDate.substring(INDEX_FIN_MOIS, INDEX_FIN_JOUR);

                // D�claration des entiers correspondant au jour, mois, ann�e de
                // la transaction
                int l_nAnneeTransaction = Integer.parseInt(l_strAnneeTransaction);
                int l_nMoisTransaction = Integer.parseInt(l_strMoisTransaction);
                int l_nJourTransaction = Integer.parseInt(l_strJourTransaction);

                // Cr�ation d'une date correspondant � ces valeurs
                Calendar l_cal = Calendar.getInstance();
                l_cal.set(Calendar.DAY_OF_MONTH, l_nJourTransaction);
                l_cal.set(Calendar.MONTH, l_nMoisTransaction - 1);
                l_cal.set(Calendar.YEAR, l_nAnneeTransaction);
                l_cal.add(Calendar.DAY_OF_MONTH, m_n_orderValidity);

                /*
                 * ??
                 */
                String l_strNewMonth = "";
                String l_strNewDay = "";

                int l_nNewMonth = l_cal.get(Calendar.MONTH) + 1;
                int l_nNewDay = l_cal.get(Calendar.DAY_OF_MONTH);

                l_strNewMonth = (l_nNewMonth < CastoConstantes.DIX) ? "0" + l_nNewMonth : "" + l_nNewMonth;
                l_strNewDay = (l_nNewDay < CastoConstantes.DIX) ? "0" + l_nNewDay : "" + l_nNewDay;

                String l_strDateTransExpire = l_cal.get(Calendar.YEAR) + l_strNewMonth + l_strNewDay;

                /*
                 * ??
                 */

                MutableRepository l_MutableRepository = (MutableRepository) getRepository();
                MutableRepositoryItem l_logSips;
                String l_DescriptorName = "logsips";
                l_logSips = l_MutableRepository.createItem(l_DescriptorName);

                l_logSips.setPropertyValue("order_id", m_strOrderId);
                l_logSips.setPropertyValue("response_code", m_strResponseCode);
                l_logSips.setPropertyValue("libelle", l_strLibelle);
                l_logSips.setPropertyValue("montant", new Integer(m_nMontant));
                l_logSips.setPropertyValue("transaction_id", m_strTransactionId);
                l_logSips.setPropertyValue("authorisation_id", m_strAuthorisationId);
                l_logSips.setPropertyValue("payment_means", m_strPaymentMeans);
                l_logSips.setPropertyValue("card_number", m_strCardNumber);
                l_logSips.setPropertyValue("payment_certificate", m_strPaymentCertificate);
                l_logSips.setPropertyValue("payment_time", m_strPaymentTime);
                l_logSips.setPropertyValue("payment_date", m_strPaymentDate);
                l_logSips.setPropertyValue("devise", m_strDevise);
                l_logSips.setPropertyValue("profile_id", m_strProfileId);
                l_logSips.setPropertyValue("date_trans_expire", l_strDateTransExpire);

                l_MutableRepository.updateItem(l_logSips);
                l_logSips = (MutableRepositoryItem) l_MutableRepository.addItem(l_logSips);
            }
            else
            {
                if (isLoggingWarning())
                {
                    logWarning("createLog() : le code est une alarme MGN");
                }
            }
            // Fin du if (CODE_RETOUR_PAIEMENT_ACCEPTE == l_responseCode ||
            // CODE_RETOUR_PAIEMENT_REFUSE == l_responseCode...
        }
        catch (RepositoryException l_repoException)
        {
            logError("createLog() : " + l_repoException.toString());
        }
        catch (Exception l_exception)
        {
            logError("createLog() : " + l_exception.toString());
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.order.BeanLogSips.createLog()");
        }
    }

    /**
     * M�thode qui renvoie le libell� correspondant au code retour SIPS.
     * 
     * @param a_responseCode
     *            Le code retour SIPS
     * 
     * @return String Le libell� correspondant au code retour SIPS.
     */
    private String getLibelle(int a_responseCode)
    {
        String l_strLibelle;

        switch (a_responseCode)
        {
        case CODE_RETOUR_PAIEMENT_ACCEPTE:
        {
            l_strLibelle = LIBELLE_PAIEMENT_ACCEPTE;
            break;
        }
        case CODE_RETOUR_PAIEMENT_REFUSE:
        {
            l_strLibelle = LIBELLE_PAIEMENT_REFUSE;
            break;
        }
        case CODE_RETOUR_MERCHANT_ID_INVALIDE:
        {
            l_strLibelle = LIBELLE_MERCHANT_ID_INVALIDE;
            break;
        }
        case CODE_RETOUR_REFUS_PAIEMENT:
        {
            l_strLibelle = LIBELLE_REFUS_PAIEMENT;
            break;
        }
        case CODE_RETOUR_TRANSACTION_INVALIDE:
        {
            l_strLibelle = LIBELLE_TRANSACTION_INVALIDE;
            break;
        }
        case CODE_RETOUR_MONTANT_INVALIDE:
        {
            l_strLibelle = LIBELLE_MONTANT_INVALIDE;
            break;
        }
        case CODE_RETOUR_ID_CLIENT_INVALIDE:
        {
            l_strLibelle = LIBELLE_ID_CLIENT_INVALIDE;
            break;
        }
        case CODE_RETOUR_ANNULATION_CLIENT:
        {
            l_strLibelle = LIBELLE_ANNULATION_CLIENT;
            break;
        }
        case CODE_RETOUR_ERREUR_FORMAT:
        {
            l_strLibelle = LIBELLE_ERREUR_FORMAT;
            break;
        }
        case CODE_RETOUR_ERREUR_SECURITE:
        {
            l_strLibelle = LIBELLE_ERREUR_SECURITE;
            break;
        }
        case CODE_RETOUR_NB_ESSAIS_MAX:
        {
            l_strLibelle = LIBELLE_NB_ESSAIS_MAX;
            break;
        }
        case CODE_RETOUR_SERVICE_INDISPONIBLE:
        {
            l_strLibelle = LIBELLE_SERVICE_INDISPONIBLE;
            break;
        }
        case CODE_RETOUR_TRANSACTION_DEJA_TRAITEE:
        {
            l_strLibelle = LIBELLE_TRANSACTION_DEJA_TRAITEE;
            break;
        }
        default:
        {
            l_strLibelle = "";
            break;
        }
        }
        return l_strLibelle;
    }

    /**
     * R�cup�ration d'un libelle en fonction d'un code de r�ponse SIPS.
     * 
     * @param a_strResponseCode
     *            Le code retour SIPS.
     * 
     * @return String Le libell� correspondant au code retour SIPS.
     */
    public String getLibelle(String a_strResponseCode)
    {
        String l_libelle;

        try
        {
            int l_responseCode = Integer.parseInt(a_strResponseCode);

            l_libelle = getLibelle(l_responseCode);
        }
        catch (NumberFormatException l_nfe)
        {
            l_libelle = "";
        }

        return l_libelle;
    }
}