package com.castorama.webservices.clients;

import java.rmi.RemoteException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import com.atg.www.webservices.LoginUserSEI;
import com.atg.www.webservices.LoginUserSEIService;
import com.atg.www.webservices.LoginUserSEIServiceLocator;

public class ClientLoginUser {

	public static String loginUser(String requestXML) throws ServiceException, ServletException, RemoteException {
		LoginUserSEIService webService = new LoginUserSEIServiceLocator();
		LoginUserSEI stub = webService.getLoginUserSEIPort();
		return stub.loginUser(requestXML);
	}

	public static String loginUser(String requestXML, HttpServletRequest request) throws ServiceException, ServletException, RemoteException {
		String mUrl = request.getRequestURL().toString();
		mUrl = mUrl.substring(0, mUrl.indexOf("test.jsp")) + "loginUser/loginUser" ;
		LoginUserSEIService webService = new LoginUserSEIServiceLocator();
		((LoginUserSEIServiceLocator)webService).setLoginUserSEIPortEndpointAddress(mUrl);
		LoginUserSEI stub = webService.getLoginUserSEIPort();
		return stub.loginUser(requestXML);
	}

}
