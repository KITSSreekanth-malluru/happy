package com.castorama;

public class SessionBeanOrder
{
	protected boolean m_bValid=false;
	public void setValid(boolean a_bValid){
	  	m_bValid = a_bValid;
	}
	public boolean getValid(){
	  	return m_bValid;
	}	
	protected boolean m_bErreurNumOrDateAtout=false;
	public void setErreurNumOrDateAtout(boolean a_bErreurNumOrDateAtout){
	  	m_bErreurNumOrDateAtout = a_bErreurNumOrDateAtout;
	}
	public boolean getErreurNumOrDateAtout(){
	  	return m_bErreurNumOrDateAtout;
	}	
}