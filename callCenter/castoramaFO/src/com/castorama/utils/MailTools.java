package com.castorama.utils;
import atg.servlet.DynamoHttpServletRequest;
import atg.service.email.EmailListener;
import javax.mail.Message;
import atg.service.email.EmailEvent;
import atg.service.email.ContentPart;
import atg.service.email.MimeMessageUtils;
import atg.nucleus.Nucleus;

import com.castorama.xml.*;
/**
 * MailTools : Castorama 2002<br>
 * Class comportant les m�thodes utiles pour l'envoi de mails
 * @version 1.0  
 * @author Damien DURIEZ - INTERNENCE (JUIN 2002) 
 */
public class MailTools
{
	protected EmailListener	m_EmailListener;
	protected Transformer	m_Transformer;	// Transformer utlis� pour un template de mail en xsl
	/**
	 * R�cup�ration de l'EmailListener charg� d'envoyer les mails
	 * @param none
	 * @return EmailListener - EmailListener
	 * @throws none
	 */
	public EmailListener getEmailListener()
	{
		return m_EmailListener;
	}
	/**
	 * Modification de l'EmailListener charg� d'envoyer les mails
	 * @param EmailListener - EmailListener
	 * @return none
	 * @throws none
	 */
	public void setEmailListener(EmailListener a_EmailListener)
	{
		m_EmailListener = a_EmailListener;
	}
	/**
	 * R�cup�ration du Transformer
	 * @param none
	 * @return Transformer Transformer
	 * @throws none
	 */
	public Transformer getTransformer()
	{
		return m_Transformer;
	}
	/**
	 * Modification du Transformer
	 * @param Transformer - Transformer
	 * @return none
	 * @throws Exception
	 */
	public void setTransformer(Transformer a_Transformer)
	{
		m_Transformer = a_Transformer;
	}
	/**
	 * M�thode statique permettant de r�cup�rer le composant global /castorama/utils/MailTools
	 * @param	none.
	 * @return	MailTools
	 * @exception none
	 */
	public static MailTools getMailToolsInstance()
	{
		return (MailTools) Nucleus.getGlobalNucleus().resolveName("/castorama/utils/MailTools");
	}
	/**
	 * Envoi d'un mail
	 * @param	String	a_strFrom
	 * @param	String	a_strTo
	 * @param	String	a_strSubject
	 * @param	String	a_strMessage
	 * @return	none
	 * @exception Exception
	 */
	public void send(String a_strFrom, String a_strTo, String a_strSubject, String a_strMessage) throws Exception
	{
		try
		{
			Message l_Message = MimeMessageUtils.createMessage();
			MimeMessageUtils.setFrom(l_Message, a_strFrom);
			l_Message.setSubject(a_strSubject);
			MimeMessageUtils.setRecipient(l_Message, Message.RecipientType.TO, a_strTo);
			ContentPart[] l_Content = { new ContentPart(a_strMessage, "text/html") };
			MimeMessageUtils.setContent(l_Message, l_Content);
			EmailEvent l_EmailEvent = new EmailEvent(l_Message);
			m_EmailListener.sendEmailEvent(l_EmailEvent);
		} catch (Exception e)
		{
			//Trace.logError(this, e, ".sendMail Exception : " + e.toString());
		}
	}
	/**
	 * M�thode statique permettant d'envoyer un email sans r�f�rencer le composant /castorama/utils/MailTools
	 * @param	String	a_strFrom
	 * @param	String	a_strTo
	 * @param	String	a_strSubject
	 * @param	String	a_strMessage
	 * @return	none
	 * @exception Exception
	 */
	public static void sendMail(String a_strFrom, String a_strTo, String a_strSubject, String a_strMessage) throws Exception
	{
		MailTools l_MailTools = getMailToolsInstance();
		l_MailTools.send(a_strFrom, a_strTo, a_strSubject, a_strMessage);
	}
	/**
	 * M�thode statique permettant d'envoyer un email en utilisant un template XSL pour le corps du message
	 * @param	String		a_strFrom
	 * @param	String		a_strTo
	 * @param	String		a_strSubject
	 * @param	String		a_strXSLTemplateURL
	 * @param	XmlDocument	repr�sentation du xml
	 * @return	none
	 * @exception Exception
	 */
	public static void sendXSLMail(String a_strFrom, String a_strTo, String a_strSubject, String a_strXSLTemplateURL, XmlDocument a_XmlRepresentation) throws Exception
	{
		MailTools l_MailTools = getMailToolsInstance();
		String l_strMessage = l_MailTools.getTransformer().processXSLTemplate(a_XmlRepresentation, a_strXSLTemplateURL);
		l_MailTools.send(a_strFrom, a_strTo, a_strSubject, l_strMessage);
	}
}// fin de class
