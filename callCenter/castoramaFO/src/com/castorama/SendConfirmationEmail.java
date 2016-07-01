package com.castorama;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.utils.MailUtils;
import com.castorama.utils.ServerSetting;

public class SendConfirmationEmail extends DynamoServlet {
	
	private Repository orderRepository;
    private Repository profileRepository;
    private MailUtils mailUtils;

    private String m_urlBaseImg;
    private String m_castoMail;
   
    private ServerSetting serverSetting;
    

	
	public void service(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException {
		String orderId = a_Request.getParameter("orderId");
		
		sendEmail(orderId);
		
		a_Request.setParameter("element", "true");
		a_Request.serviceParameter("OUTPUT",a_Request,a_Response);
		
	}

	public Repository getOrderRepository() {
		return orderRepository;
	}

	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public Repository getProfileRepository() {
		return profileRepository;
	}

	public void setProfileRepository(Repository profileRepository) {
		this.profileRepository = profileRepository;
	}

	public MailUtils getMailUtils() {
		return mailUtils;
	}

	public void setMailUtils(MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}
	
	public ServerSetting getServerSetting() {
		return serverSetting;
	}

	public void setServerSetting(ServerSetting serverSetting) {
		this.serverSetting = serverSetting;
	}

    public String getUrlBaseImg()
    {
        return m_urlBaseImg;
    }
    
    public void setUrlBaseImg(String a_urlBaseImg)
    {
        m_urlBaseImg = a_urlBaseImg;
    }
    
    public String getCastoMail()
    {
        return m_castoMail;
    }
    
    public void setCastoMail(String a_castoMail)
    {
        m_castoMail = a_castoMail;
    }
	
    public void sendEmail(String orderId)
    {
        try
        {
            RepositoryItem order = getOrderRepository().getItem(orderId, "order");
            if (order != null) {
                RepositoryItem user = getProfileRepository().getItem((String) order.getPropertyValue("profileId"), "user");
                if (user != null) {
                    String l_cher = null;
                    String l_strCivilite = (String) user.getPropertyValue("civilite");
                    if (l_strCivilite != null) {
                    	if ("mr".equals(l_strCivilite)) {
                            l_cher = "Cher";
                            l_strCivilite = "monsieur";
                    	} else if ("miss".equals(l_strCivilite)) {
                                l_cher = "Ch&egrave;re";
                                l_strCivilite = "Mademoiselle";
                        } else if ("mrs".equals(l_strCivilite)) {
                                l_cher = "Ch&egrave;re";
                                l_strCivilite = "Madame";
                        } else {
                        	l_cher = "";
                        }
                    	
                    } else {
                    	l_strCivilite = "";
                    }

                    String l_strFirstName = (String) user.getPropertyValue("firstName");
                    l_strFirstName = l_strFirstName.substring(0,1).toUpperCase() + l_strFirstName.substring(1);
                    
                    String l_strLastName = (String) user.getPropertyValue("lastName");
                    l_strLastName = l_strLastName.toUpperCase();
                    
                    String l_codeClient = order.getPropertyValue("profileId").toString();

                    String l_strAverti = (l_strCivilite.equalsIgnoreCase("Madame") || l_strCivilite
                            .equalsIgnoreCase("Mademoiselle")) ? "avertie" : "averti";

                    String l_corpsMessage = ResourceUtils.getUserMsgResource("commande.job.confirmation.reception.texte",
                            CastoConstantesCommande.BUNDLE_COMMANDES, null, new Object[]
                            {
                                order.getRepositoryId(),
                                getServerSetting().getHost() + getUrlBaseImg(),
                                l_codeClient,
                                l_strCivilite,
                                l_strFirstName,
                                l_strLastName,
                                l_cher,
                                l_strAverti,
                                getServerSetting().getHost()
                                });
 
                    String l_subject = ResourceUtils.getUserMsgResource("commande.job.confirmation.reception.titre",
                            CastoConstantesCommande.BUNDLE_COMMANDES, null, new String[]
                            { order.getRepositoryId()});
                    
                    String l_destinataire = (String) user.getPropertyValue("email");
                    
                    
                    if (null != l_destinataire)
                    {
                        getMailUtils().sendMailHTML(getCastoMail(), l_subject, l_destinataire,
                                l_corpsMessage);
                    }
                } else {
                	if (isLoggingWarning()) {
                		logWarning("User not found by ID=" + order.getPropertyValue("profileId"));
                	}
                }
            	
            } else {
            	if (isLoggingWarning()) {
            		logWarning("Order not found by ID=" + orderId);
            	}
            }
        }
        catch (Exception e)
        {
            if(isLoggingError())
            {
                logError("ERREUR LORS DE L'ENVOID U MAIL DE CONFIRMATION DE RECEPTION : "+e);
            }
        }
    }

}
