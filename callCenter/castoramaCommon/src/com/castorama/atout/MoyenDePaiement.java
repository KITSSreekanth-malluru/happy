package com.castorama.atout;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.i18n.I18nTools;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Composant de scope golbal chargé de la configuration d'un moyen de paiement par carte l'Atout.
 */
public class MoyenDePaiement extends DynamoServlet
{

    /**. */
    public static final String MONTANT  = "montant";

    /**. */
    public static final String DATE     = "date";

    /**. */
    public static final String OUTPUT   = "output";


    protected boolean   m_bEnabled;         // Le mode de paiement est-il activé ?
    protected double    m_dMontantMinimum;  // Montant minimum (en euros) autorisant le mode de paiement 
    protected double    m_dMontantMaximum;  // Montant maximum (en euros) autorisant le mode de paiement
    protected Date      m_DateDebut;        // Date de début d'activation du mode de paiement
    protected Date      m_DateFin;          // Date de fin d'activation du mode de paiement
    protected String    m_strCode;          // code de paiement
    protected String    m_strLibelle;       // Libelle du moyen de paiement
    protected String    m_strFragModalites; // chemin du fragment présentant les modalités de paiement



    /**.
     * ConstructeurVide
     * @param    none

     */
    public MoyenDePaiement()
    {
        m_dMontantMaximum = Double.POSITIVE_INFINITY; // par défaut, le montant max est infini
    }


    /**.
     * Récupération de Enabled
     * @param    none
     * @return   boolean Enabled
     * @throws   none
     */
    public boolean getEnabled()
    {
        return m_bEnabled;
    }


    /**.
     * Modification de Enabled<br>
     * @param    a_bEnabled     boolean Enabled
     */
    public void setEnabled(boolean a_bEnabled)
    {
        m_bEnabled = a_bEnabled;

    }


    /**.
     * Récupération de MontantMinimum
     * Le mode de paiement est autorise pour un montant minimum
     * @param    none
     * @return   double MontantMinimum
     * @throws   none
     */
    public double getMontantMinimum()
    {
        return m_dMontantMinimum;
    }


    /**.
     * Modification de MontantMinimum<br>
     * @param    a_dMontantMinimum      double MontantMinimum
     */
    public void setMontantMinimum(double a_dMontantMinimum)
    {
        m_dMontantMinimum = a_dMontantMinimum;

    }


    /**.
     * Récupération de MontantMaximum
     * Le mode de paiement est autorise pour un montant maximum
     * @param    none
     * @return   double MontantMaximum
     * @throws   none
     */
    public double getMontantMaximum()
    {
        return m_dMontantMaximum;
    }


    /**.
     * Modification de MontantMaximum<br>
     * @param    a_dMontantMaximum      double MontantMaximum
     */
    public void setMontantMaximum(double a_dMontantMaximum)
    {
        m_dMontantMaximum = a_dMontantMaximum;

    }


    /**.
     * Récupération de DateDebut
     * @param    none
     * @return   Date DateDebut
     * @throws   none
     */
    public Date getDateDebut()
    {
        return m_DateDebut;
    }


    /**.
     * Modification de DateDebut<br>
     * @param    a_DateDebut        Date DateDebut
     */
    public void setDateDebut(Date a_DateDebut)
    {
        m_DateDebut = a_DateDebut;

    }


    /**.
     * Récupération de DateFin
     * @param    none
     * @return   Date DateFin
     * @throws   none
     */
    public Date getDateFin()
    {
        return m_DateFin;
    }


    /**.
     * Modification de DateFin<br>
     * @param    a_DateFin      Date DateFin
     */
    public void setDateFin(Date a_DateFin)
    {
        m_DateFin = a_DateFin;

    }


    /**.
     * Récupération du Code
     * @param    none
     * @return   String Code
     * @throws   none
     */
    public String getCode()
    {
        return m_strCode;
    }


    /**.
     * Modification du Code
     * @param    a_strCode      String Code
     */
    public void setCode(String a_strCode)
    {
        m_strCode = a_strCode;
    }


    /**.
     * Récupération du Libelle
     * @param    none
     * @return   String Libelle
     * @throws   none
     */
    public String getLibelle()
    {
        return m_strLibelle;
    }


    /**.
     * Modification du Libelle
     * @param    a_strLibelle   String Libelle
     */
    public void setLibelle(String a_strLibelle)
    {
        m_strLibelle = a_strLibelle;
    }


    /**.
     * Récupération du FragModalites
     * @param    none
     * @return   String FragModalites
     * @throws   none
     */
    public String getFragModalites()
    {
        return m_strFragModalites;
    }


    /**.
     * Modification du FragModalites
     * @param    a_strFragModalites     String FragModalites
     */
    public void setFragModalites(String a_strFragModalites)
    {
        m_strFragModalites = a_strFragModalites;
    }



    /**
     * Le mode de paiement est-il actif aujourd'hui ?
     * Si l'une des dates de début ou de fin n'est pas renseignée, alors le paiement est actif si enabled=true
     * @param    none
     * @return   boolean
     * @throws   none
     */
    public boolean isActif()
    {

        boolean l_bDateOk = false;

        if(m_DateDebut==null || m_DateFin==null)
        {
            l_bDateOk = true;
        }
        else
        {
            Date l_Now = new Date();
            l_bDateOk = l_Now.compareTo(m_DateDebut)>=0 && l_Now.compareTo(m_DateFin)<=0 ;
        }


        return m_bEnabled && l_bDateOk;
    }


    /**
     *  Le mode de paiement est-il autorisé pour ce montant et cette date?
     *  Test si le mode de paiement est actif et si le montant passé en paramètre est dans
     *  la tranche bornée par les montants minimum et maximum et si la date est dans la période bornée
     *  par dateDebut et dateFin
     *
     *  @param    a_dMontant     double
     *  @param    a_Date         Date
     *  @return   boolean
     *  @throws   none
     */
    public boolean estAutorise(double a_dMontant, Date a_Date)
    {

        boolean l_bMontantOk    = a_dMontant>=m_dMontantMinimum && a_dMontant<=m_dMontantMaximum;
        boolean l_bDateOk       = false;

        /*if(m_DateDebut==null || m_DateFin==null) l_bDateOk = true;
        else
        {
            l_bDateOk = a_Date.compareTo(m_DateDebut)>=0 && a_Date.compareTo(m_DateFin)<=0 ;
        }*/
        
        // G.P. Correction si date de fin nulle, il faut quand même tester !
        if (m_DateDebut != null && m_DateDebut.after(a_Date))
        {
            l_bDateOk = false;
        }
        // Si la date de fin non saisie ou passée
        else if (m_DateFin != null && m_DateFin.before(a_Date))
        {
            l_bDateOk = false;
        }
        else
        {
            l_bDateOk = true;
        }


        return m_bEnabled && l_bMontantOk && l_bDateOk;
    }



    /**
     * Service.
     * Le mode de paiement est-il autorisé pour ce montant ?
     * Test si le mode de paiement est actif et si le montant passé en paramètre est supérieur ou égal au montant minimum
     * 
     * @param   a_Request       DynamoHttpServletRequest - La request à traité.
     * @param   a_Response      DynamoHttpServletResponse - L'objet reponse de cette request.
     *
     * @throws  javax.servlet.ServletException - Si une erreur spécifique d'application est 
     *          arrivée traitant cette demande.
     *
     * @throws  java.io.IOException - Si une erreur est arrivée lisant des données de 
     *          la demande ou écrivant des données à la réponse.
     */
    public void service(    DynamoHttpServletRequest a_Request,
            DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {

        try
        {

            Double  l_Montant = null;
            try
            {
                l_Montant = (Double)  a_Request.getObjectParameter(MONTANT) ;
            }
            catch (ClassCastException l_exception)
            {
                String l_sMontant = (String) a_Request.getObjectParameter(MONTANT) ;
                l_Montant = Double.valueOf(l_sMontant);
            }

            Date    l_Date      = (Date)    a_Request.getObjectParameter(DATE) ;

            double  l_dMontant  = l_Montant!=null ? l_Montant.doubleValue() : 0.0 ;
            if(l_Date==null)
            {
                l_Date = new Date(); // LA DATE N'EST PASSEE QUE POUR LE CALL CENTER(DATE DE COMMIT DE LA COMMANDE)
            }

            if(estAutorise(l_dMontant,l_Date))
            {
                a_Request.serviceLocalParameter(OUTPUT, a_Request, a_Response);
            }
        }
        catch(Exception l_oException)
        {
            //if (is)
            if (isLoggingError())
            {
                logError(l_oException);
            }
        }

    }

}