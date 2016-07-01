package com.castorama.utils;

import javax.mail.Message;
import javax.mail.MessagingException;

import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.EmailListener;
import atg.service.email.MimeMessageUtils;

import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;

/**
 * @author Florte Jeremy (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe d'utilitaires pour l'envoi de mail.
 */
public class MailUtils extends /*Code Review*/ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Gestionnaire pour l'envoi de mail.
     */
    private EmailListener m_listener;

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Retourne l'EmailListener attache a ce composant.
     * 
     * @return EmailListener L'EmailListener attache e ce composant.
     */
    public EmailListener getListener()
    {
        return m_listener;
    }

    /**
     * Fixe l'EmailListener attache e ce composant.
     * 
     * @param a_listener
     *            l'EmailListener attache a ce composant.
     */
    public void setListener(EmailListener a_listener)
    {
        this.m_listener = a_listener;
    }

    /*
     * ------------------------------------------------------------------------ [
     * Methodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Methode pour l'envoi d'un mail au format HTML.
     * 
     * @param a_from
     *            L'expediteur du mail.
     * @param a_subject
     *            Le sujet du mail.
     * @param a_to
     *            Le destinataire du mail.
     * @param a_message
     *            Le contenu du mail au format HTML.
     * 
     * @throws Exception
     *             Si une erreur survient.
     */
    public void sendMailHTML(String a_from, String a_subject, String a_to, String a_message) throws Exception
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.utils.MailUtils.sendMailHTML().");
        }

        EmailEvent l_emailEvent = null;

        try
        {
            // Creation de l'objet mail
            Message l_msg = MimeMessageUtils.createMessage(a_from, a_subject);

            // On fixe le destinataire du mail
            MimeMessageUtils.setRecipient(l_msg, Message.RecipientType.TO, a_to);

            // On fixe le message
            ContentPart[] l_content =
            { new ContentPart(a_message, "text/html"), };

            MimeMessageUtils.setContent(l_msg, l_content);

            // Envoi du mail
            l_emailEvent = new EmailEvent(l_msg);
            l_emailEvent.setCharSet("UTF-8");

            getListener().sendEmailEvent(l_emailEvent);
        }
        catch (MessagingException l_exception)
        {
            logError("Impossible d'envoyer le mail : " + a_subject + ", " + l_exception.toString());

            throw l_exception;
        }
        catch (EmailException l_emailException)
        {
            logError("Impossible d'envoyer le mail : " + a_subject + ", " + l_emailException.toString());

            throw l_emailException;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.utils.MailUtils.sendMailHTML().");
        }
    }

    /**
     * M?thode qui renvoie "cher" ou "ch?re" en fonction de la civilit?.
     * 
     * @param a_civilite
     *            La civilit?.
     * 
     * @return String "cher" ou "ch?re" en fonction de la civilit?.
     */
    public String getCiviliteCher(String a_civilite)
    {
        String l_cher;

        if ("Monsieur".equals(a_civilite))
        {
            l_cher = ResourceUtils.getUserMsgResource("commande.email.confirmation.cher.homme",
                    CastoConstantesCommande.BUNDLE_COMMANDES_MAILS, null);
        }
        else
        {
            l_cher = ResourceUtils.getUserMsgResource("commande.email.confirmation.cher.femme",
                    CastoConstantesCommande.BUNDLE_COMMANDES_MAILS, null);
        }
        return l_cher;
    }
}