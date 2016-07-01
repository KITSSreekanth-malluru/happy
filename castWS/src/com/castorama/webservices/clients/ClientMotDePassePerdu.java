package com.castorama.webservices.clients;

import java.rmi.RemoteException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import com.atg.www.webservices.MotDePassePerduSEI;
import com.atg.www.webservices.MotDePassePerduSEIService;
import com.atg.www.webservices.MotDePassePerduSEIServiceLocator;

public class ClientMotDePassePerdu {

	public static void main(String[] args) {
		System.out.println("Appel au webservice --> motDePassePerdu " + Arrays.asList(args));
		System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");

		try {
			String l_result = motDePassePerdu(args[0]);
			System.out.println("Retour du webservice");
			System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
			System.out.println(l_result);
		} catch (Exception e) {
			System.err.println("Exception levée : " + e.getMessage());
		}
	}

	public static String motDePassePerdu(String requestXML) throws ServiceException, ServletException, RemoteException {
		MotDePassePerduSEIService webService = new MotDePassePerduSEIServiceLocator();
		MotDePassePerduSEI stub = webService.getMotDePassePerduSEIPort();
		return stub.motDePassePerdu(requestXML);
	}

	public static String motDePassePerdu(String requestXML, HttpServletRequest request) throws ServiceException, ServletException, RemoteException {
		String mUrl = request.getRequestURL().toString();
		mUrl = mUrl.substring(0, mUrl.indexOf("test.jsp")) + "motDePassePerdu/motDePassePerdu";
		MotDePassePerduSEIService webService = new MotDePassePerduSEIServiceLocator();
		((MotDePassePerduSEIServiceLocator) webService).setMotDePassePerduSEIPortEndpointAddress(mUrl);
		MotDePassePerduSEI stub = webService.getMotDePassePerduSEIPort();
		return stub.motDePassePerdu(requestXML);
	}

}
