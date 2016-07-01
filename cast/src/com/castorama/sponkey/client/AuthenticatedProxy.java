package com.castorama.sponkey.client;

import java.net.Proxy;
import java.net.SocketAddress;


/**
 * @author Andrei_Raichonak
 *
 */
public class AuthenticatedProxy extends Proxy {

	private String m_userName = "";
	private String m_password = "";
	
	public AuthenticatedProxy(Type type, SocketAddress sa, String userName, String password) {
		super(type, sa);
		this.m_userName = userName;
		this.m_password = password;
	}

	public String getUserName() {
		return m_userName;
	}

	public String getPassword() {
		return m_password;
	}
}
