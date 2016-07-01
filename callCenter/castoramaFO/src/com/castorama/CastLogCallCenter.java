package com.castorama;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CastLogCallCenter extends DynamoServlet {

	private Repository orderRepository;
	private Repository profileRepository;
	private MutableRepository journalisationRepository;

	public void service(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
			throws ServletException, IOException {
		try {
			MutableRepository l_mutRep = getJournalisationRepository();
			MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
			l_journalisation.setPropertyValue("dateAction", new java.util.Date());
			l_journalisation.setPropertyValue("action", "validation_CALL_CENTER_PAYBOX");
			l_journalisation.setPropertyValue("userId", a_Request.getParameter("userId"));
			l_journalisation.setPropertyValue("login", a_Request.getParameter("userLogin"));
			l_journalisation.setPropertyValue("orderId", a_Request.getParameter("orderId"));
			l_mutRep.addItem(l_journalisation);
		} catch (Exception l_e) {
			logError(l_e);
		}

		a_Request.setParameter("element", "true");
		a_Request.serviceParameter("OUTPUT", a_Request, a_Response);

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

	public MutableRepository getJournalisationRepository() {
		return journalisationRepository;
	}

	public void setJournalisationRepository(MutableRepository journalisationRepository) {
		this.journalisationRepository = journalisationRepository;
	}

}
