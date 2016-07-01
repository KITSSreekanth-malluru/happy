package com.castorama.webservices;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.castorama.webservices.xsd.RechercheClientsRequest;

/**
 * Utilisée par le webservice rechercheClients.
 * 
 * @author derosiauxs
 * 
 */
public class CastoRechercheClientsManager extends GenericService {

	// ------------------------------------------------------------------------
	// ATTRIBUTS
	// ------------------------------------------------------------------------	
	
	private CastoXMLOutputManager m_XMLManager;

	private Map m_messages;

	private Repository m_profileRepository;
	
	private CastoWebServicesTools m_webServicesTools;
	
	private int m_defaultPageSize;
    
    private int m_totalPagesCount;
	
	// ------------------------------------------------------------------------
	// CONSTANTES
	// ------------------------------------------------------------------------	
	
	private static final String CODE_RETOUR_OK = "0";

	private static final String CODE_RETOUR_NOM_ET_PRENOM_VIDE = "1";

	private static final String CODE_RETOUR_NUMERO_CLIENT_INCORRECT = "2";

	private static final String CODE_RETOUR_EMAIL_INCORRECT = "3";
	
	private static final String CODE_RETOUR_AUCUN_RESULTAT = "4";

	private static final String CODE_RETOUR_INCONNU = "999";



	// ------------------------------------------------------------------------	
	// METHODES
	// ------------------------------------------------------------------------
	
	/**
	 * Méthode appelée par le webservice.
	 *
	 * @param a_requestXML la requête sous format XML envoyé par les clients
	 * @param retourne un XML contenant la réponse
	 */
	public String rechercheClients(String a_requestXML) {
		if (isLoggingDebug()) {
			logDebug(a_requestXML);
		}

		String l_resultXML = null;

		List<String> users = new ArrayList<String>();
		
        setTotalPagesCount(0);

        try {
			RechercheClientsRequest l_request = RechercheClientsRequest.unmarshal(new StringReader(a_requestXML));
			String l_id = l_request.getId();
			String l_email = l_request.getEmail();
			String l_prenom = l_request.getPrenom();
			String l_nom = l_request.getNom();
			String l_codePostal = l_request.getCodePostal();
            int l_pageSize = l_request.getPageSize();
            if (l_pageSize == 0)
                l_pageSize = getDefaultPageSize();
            int l_pageNumber = l_request.getPageNumber();
            if (l_pageNumber == 0)
                l_pageNumber = 1;
            int l_start = l_pageSize * (l_pageNumber - 1);
            int l_end = l_start + l_pageSize;
			
			if (!StringUtils.isBlank(l_id)) {
				
				if (isLoggingDebug()) {
					logDebug("Search by ID " + l_id + " ...");
				}
				
				// ------------------------------------------------------------	
				// REG_CC_5_1
				// ------------------------------------------------------------
				
				if (!getWebServicesTools().isNumericOnly(l_id)) {
					l_resultXML = getXML(CODE_RETOUR_NUMERO_CLIENT_INCORRECT,
							null);
				} else {
					RepositoryItem item = getProfileRepository().getItem(l_id, "user");
					if (item != null) {
						users.add(item.getRepositoryId());
					}
				}

				
			} else if (!StringUtils.isBlank(l_email)) {

				if (isLoggingDebug()) {
					logDebug("Search by email " + l_email + " ...");
				}
				
				// ------------------------------------------------------------	
				// REG_CC_5_2
				// ------------------------------------------------------------
				
				if (!getWebServicesTools().isEmail(l_email))
				{
					l_resultXML = getXML(CODE_RETOUR_EMAIL_INCORRECT, null);
				}
				else
				{
					RepositoryItemDescriptor rid = getProfileRepository().getItemDescriptor("user");
					RepositoryView view = rid.getRepositoryView();
					RqlStatement req = RqlStatement.parseRqlStatement("login = ?0");
					RepositoryItem clients[] = req.executeQuery(view, new Object[] { l_email });
					
	                if (clients != null && clients.length > l_start) {
	                    setTotalPagesCount((int) Math.ceil((double) clients.length / l_pageSize));
	                    l_end = Math.min(l_end, clients.length);
	                    for (int i = l_start; i < l_end; i++) {
	                        users.add(clients[i].getRepositoryId());
	                    }
	                }
				}

				
			} else if (!StringUtils.isBlank(l_prenom) && !StringUtils.isBlank(l_nom)) {

				if (isLoggingDebug()) {
					logDebug("Search by firstName " + l_prenom + " and lastName " + l_nom + " ...");
				}
				
				//AP l_prenom = getWebServicesTools().removeAccentsAndCapitals(l_prenom);
				//AP l_nom = getWebServicesTools().removeAccentsAndCapitals(l_nom);
				
				// ------------------------------------------------------------
				// REG_CC_5_8
				// ------------------------------------------------------------	
				
				// ------------------------------------------------------------
				// On récupére les profils correspondant au nom/prénom sans tenir
				// compte de la casse ni des accents
				// ------------------------------------------------------------
				
				StringBuilder query = new StringBuilder();
				query.append("lastName EQUALS IGNORECASE ?0 AND firstName EQUALS IGNORECASE ?1");
				if (!StringUtils.isBlank(l_codePostal)) {
					if (isLoggingDebug()) {
						logDebug("Search by postalCode " + l_codePostal + " ...");
					}
					
					query.append(" AND billingAddress.postalCode = ?2");
				}
				
				// ------------------------------------------------------------
				// REG_CC_5_7
				// ------------------------------------------------------------				
				query.append(" ORDER BY firstName CASE IGNORECASE, lastName CASE IGNORECASE, billingAddress.postalCode, billingAddress.city CASE IGNORECASE");
				if (isLoggingDebug()) {
					logDebug("RQL:" +  query);
				}

				// ------------------------------------------------------------
				// REG_CC_5_5
				// ------------------------------------------------------------
				RepositoryView view = getProfileRepository().getItemDescriptor("user").getRepositoryView();
				RqlStatement req = RqlStatement.parseRqlStatement(query.toString());
				RepositoryItem clients[] = req.executeQueryUncached(view, new Object[] {l_nom, l_prenom, l_codePostal });

                if (clients != null && clients.length > l_start) {
                    setTotalPagesCount((int) Math.ceil((double) clients.length / l_pageSize));
                    l_end = Math.min(l_end, clients.length);
                    for (int i = l_start; i < l_end; i++) {
						users.add(clients[i].getRepositoryId());
					}
				}
				
				/*ap
				String[] ids = getWebServicesTools().getUserIdsMatchingFirstNameAndLastName(l_prenom, l_nom);
				
				if (ids != null && ids.length > 0)
				{
					if (isLoggingDebug()) {
						logDebug("Users matching firstName and lastName:" + Arrays.asList(ids));
					}
					
					// on construit la requête en commençant par rajouter ces IDs (qui matche firstName et lastName)
					StringBuffer l_requete = new StringBuffer("ID IN { ");
					for (int i = 0; i < ids.length; i++)
					{
						l_requete.append((i > 0 ? "," : "") + "\"" + ids[i] + "\"");
					}
					l_requete.append(" }");
					
					// ------------------------------------------------------------
					// REG_CC_5_4
					// ------------------------------------------------------------				
					if (!StringUtils.isBlank(l_codePostal)) {
						if (isLoggingDebug()) {
							logDebug("Search by postalCode " + l_codePostal + " ...");
						}
						
						l_requete.append(" AND billingAddress.postalCode = ?0");
					}
	
					// ------------------------------------------------------------
					// REG_CC_5_7
					// ------------------------------------------------------------				
					l_requete.append(" ORDER BY firstName CASE IGNORECASE, lastName CASE IGNORECASE, billingAddress.postalCode, billingAddress.city CASE IGNORECASE");
					if (isLoggingDebug()) {
						logDebug("RQL:" +  l_requete);
					}
	
					// ------------------------------------------------------------
					// REG_CC_5_5
					// ------------------------------------------------------------
					RepositoryView view = getProfileRepository().getItemDescriptor("user").getRepositoryView();
					RqlStatement req = RqlStatement.parseRqlStatement(l_requete.toString());
					RepositoryItem clients[] = req.executeQueryUncached(view, new Object[] { l_codePostal });
	
					if (clients != null && clients.length > 0) {
						for (int i = 0; i < clients.length; i++) {
							users.add(clients[i].getPropertyValue("id"));
						}
					}
				
				}
				else
				{
					if (isLoggingDebug()) {
						logDebug("No users matching firstName and lastName");
					}
				}

				*/

            } else if (!StringUtils.isBlank(l_nom)) {
                if (isLoggingDebug()) {
                    logDebug("Search by lastName " + l_nom + " ...");
                }
                
                StringBuilder query = new StringBuilder();
                query.append("lastName EQUALS IGNORECASE ?0");
                if (!StringUtils.isBlank(l_codePostal)) {
                    if (isLoggingDebug()) {
                        logDebug("Search by postalCode " + l_codePostal + " ...");
                    }
                    
                    query.append(" AND billingAddress.postalCode = ?1");
                }
                
                query.append(" ORDER BY firstName CASE IGNORECASE, lastName CASE IGNORECASE, billingAddress.postalCode, billingAddress.city CASE IGNORECASE");
                if (isLoggingDebug()) {
                    logDebug("RQL:" +  query);
                }

                RepositoryView view = getProfileRepository().getItemDescriptor("user").getRepositoryView();
                RqlStatement req = RqlStatement.parseRqlStatement(query.toString());
                RepositoryItem clients[] = req.executeQueryUncached(view, new Object[] {l_nom, l_codePostal });

                if (clients != null && clients.length > l_start) {
                    setTotalPagesCount((int) Math.ceil((double) clients.length / l_pageSize));
                    l_end = Math.min(l_end, clients.length);
                    for (int i = l_start; i < l_end; i++) {
                        users.add(clients[i].getRepositoryId());
                    }
                }

            } else if (!StringUtils.isBlank(l_prenom) && !StringUtils.isBlank(l_codePostal)) {
                if (isLoggingDebug()) {
                    logDebug("Search by firstName " + l_prenom + " and postalCode " + l_codePostal + " ...");
                }
                
                StringBuilder query = new StringBuilder();
                query.append("firstName EQUALS IGNORECASE ?0 AND billingAddress.postalCode = ?1");
                
                query.append(" ORDER BY firstName CASE IGNORECASE, lastName CASE IGNORECASE, billingAddress.postalCode, billingAddress.city CASE IGNORECASE");
                if (isLoggingDebug()) {
                    logDebug("RQL:" +  query);
                }

                RepositoryView view = getProfileRepository().getItemDescriptor("user").getRepositoryView();
                RqlStatement req = RqlStatement.parseRqlStatement(query.toString());
                RepositoryItem clients[] = req.executeQueryUncached(view, new Object[] {l_prenom, l_codePostal });

                if (clients != null && clients.length > l_start) {
                    setTotalPagesCount((int) Math.ceil((double) clients.length / l_pageSize));
                    l_end = Math.min(l_end, clients.length);
                    for (int i = l_start; i < l_end; i++) {
                        users.add(clients[i].getRepositoryId());
                    }
                }

            } else if (!StringUtils.isBlank(l_codePostal)) {
                if (isLoggingDebug()) {
                    logDebug("Search by postalCode " + l_codePostal + " ...");
                }
                
                StringBuilder query = new StringBuilder();
                query.append("billingAddress.postalCode = ?0");
                
                query.append(" ORDER BY firstName CASE IGNORECASE, lastName CASE IGNORECASE, billingAddress.postalCode, billingAddress.city CASE IGNORECASE");
                if (isLoggingDebug()) {
                    logDebug("RQL:" +  query);
                }

                RepositoryView view = getProfileRepository().getItemDescriptor("user").getRepositoryView();
                RqlStatement req = RqlStatement.parseRqlStatement(query.toString());
                RepositoryItem clients[] = req.executeQueryUncached(view, new Object[] {l_codePostal });

                if (clients != null && clients.length > l_start) {
                    setTotalPagesCount((int) Math.ceil((double) clients.length / l_pageSize));
                    l_end = Math.min(l_end, clients.length);
                    for (int i = l_start; i < l_end; i++) {
                        users.add(clients[i].getRepositoryId());
                    }
                }

            } else {
				
				// ------------------------------------------------------------				
				// REG_CC_5_3
				// ------------------------------------------------------------
				
				l_resultXML = getXML(CODE_RETOUR_NOM_ET_PRENOM_VIDE, null);

			}
			
			
			// ------------------------------------------------------------				
			// REG_CC_5_9
			// ------------------------------------------------------------
			
			// si aucune erreur, alors tout s'est bien passé (!). On set le code d'erreur (clients trouvés ou non)
			if (l_resultXML == null)
			{
				/*for (Iterator l_it = users.iterator(); l_it.hasNext();)
				{
					// on vérifie que les ids existent
					String l_idToCheck = (String) l_it.next();
					if (getProfileRepository().getItem(l_idToCheck, "user") == null)
					{
						if (isLoggingDebug()) {
							logDebug("ID " + l_idToCheck + " doesn't exist, removing ...");
						}
						l_it.remove();
					}
				}
				*/
				
				if (isLoggingDebug()) {
					logDebug("Users list:" + users);
				}
				
				if (users.size() > 0)
				{
					l_resultXML = getXML(CODE_RETOUR_OK, users);
				}
				else
				{
					l_resultXML = getXML(CODE_RETOUR_AUCUN_RESULTAT, null);
				}
			}

		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (ValidationException l_ve) {
			if (isLoggingError()) {
				logError(l_ve);
			}
		} catch (RepositoryException l_re) {
			if (isLoggingError()) {
				logError(l_re);
			}
		} catch (Exception l_e) {
			if (isLoggingError()) {
				logError(l_e);
			}
		}

		if (l_resultXML == null) {
			if (isLoggingDebug()) {
				logDebug("Internal error. See ATG logs for more information.");
			}
			l_resultXML = getXML(CODE_RETOUR_INCONNU, users);
		}

		if (isLoggingDebug()) {
			logDebug(l_resultXML);
		}
		return l_resultXML;
	}

	private String getXML(String a_codeRetour, List<String> a_users) {
		return getXMLManager().getRechercheClientsOutput(a_codeRetour,
				(String) getMessages().get(a_codeRetour), a_users);
	}
	
	// ------------------------------------------------------------------------
	// GETTERS/SETTERS
	// ------------------------------------------------------------------------	

	public Map getMessages() {
		return m_messages;
	}

	public void setMessages(Map a_messages) {
		this.m_messages = a_messages;
	}

	public CastoXMLOutputManager getXMLManager() {
		return m_XMLManager;
	}

	public void setXMLManager(CastoXMLOutputManager a_manager) {
		m_XMLManager = a_manager;
	}
	
	public Repository getProfileRepository() {
		return m_profileRepository;
	}

	public void setProfileRepository(Repository repository) {
		m_profileRepository = repository;
	}

	public CastoWebServicesTools getWebServicesTools() {
		return m_webServicesTools;
	}

	public void setWebServicesTools(CastoWebServicesTools servicesTools) {
		m_webServicesTools = servicesTools;
	}
	
    public int getDefaultPageSize() {
        return m_defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        m_defaultPageSize = defaultPageSize;
    }

    public int getTotalPagesCount() {
        return m_totalPagesCount;
    }

    public void setTotalPagesCount(int totalPagesCount) {
        this.m_totalPagesCount = totalPagesCount;
    }
    
}
