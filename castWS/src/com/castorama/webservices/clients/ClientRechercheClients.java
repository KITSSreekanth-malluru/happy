package com.castorama.webservices.clients;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import com.atg.www.webservices.RechercheClientsSEI;
import com.atg.www.webservices.RechercheClientsSEIService;
import com.atg.www.webservices.RechercheClientsSEIServiceLocator;

public class ClientRechercheClients {

	public static void main(String[] args) {
		System.out.println("Appel au webservice --> rechercheClients " + Arrays.asList(args));
		System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");

		try {
			String l_result = rechercheClients(args[0]);
			System.out.println("Retour du webservice");
			System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
			System.out.println(l_result);

			FileWriter fstream = new FileWriter("rechercheClients.xml");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(l_result);
			out.close();

		} catch (Exception e) {
			System.err.println("Exception levée : " + e.getMessage());
		}
	}

	public static String rechercheClients(String requestXML) throws ServiceException, ServletException, RemoteException {
		RechercheClientsSEIService webService = new RechercheClientsSEIServiceLocator();
		RechercheClientsSEI stub = webService.getRechercheClientsSEIPort();
		return stub.rechercheClients(requestXML);
	}

	public static String rechercheClients(String requestXML, HttpServletRequest request) throws ServiceException, ServletException, RemoteException {
		String mUrl = request.getRequestURL().toString();
		mUrl = mUrl.substring(0, mUrl.indexOf("test.jsp")) + "rechercheClients/rechercheClients";
		RechercheClientsSEIService webService = new RechercheClientsSEIServiceLocator();
		((RechercheClientsSEIServiceLocator) webService).setRechercheClientsSEIPortEndpointAddress(mUrl);
		RechercheClientsSEI stub = webService.getRechercheClientsSEIPort();
		return stub.rechercheClients(requestXML);
	}
}
