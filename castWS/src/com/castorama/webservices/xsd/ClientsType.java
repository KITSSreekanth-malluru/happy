/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.castorama.webservices.xsd;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class ClientsType.
 * 
 * @version $Revision$ $Date$
 */
public class ClientsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _clientList.
     */
    private java.util.Vector _clientList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ClientsType() {
        super();
        this._clientList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vClient
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addClient(
            final com.castorama.webservices.xsd.Client vClient)
    throws java.lang.IndexOutOfBoundsException {
        this._clientList.addElement(vClient);
    }

    /**
     * 
     * 
     * @param index
     * @param vClient
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addClient(
            final int index,
            final com.castorama.webservices.xsd.Client vClient)
    throws java.lang.IndexOutOfBoundsException {
        this._clientList.add(index, vClient);
    }

    /**
     * Method enumerateClient.
     * 
     * @return an Enumeration over all
     * com.castorama.webservices.xsd.Client elements
     */
    public java.util.Enumeration enumerateClient(
    ) {
        return this._clientList.elements();
    }

    /**
     * Method getClient.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * com.castorama.webservices.xsd.Client at the given index
     */
    public com.castorama.webservices.xsd.Client getClient(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._clientList.size()) {
            throw new IndexOutOfBoundsException("getClient: Index value '" + index + "' not in range [0.." + (this._clientList.size() - 1) + "]");
        }
        
        return (com.castorama.webservices.xsd.Client) _clientList.get(index);
    }

    /**
     * Method getClient.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public com.castorama.webservices.xsd.Client[] getClient(
    ) {
        com.castorama.webservices.xsd.Client[] array = new com.castorama.webservices.xsd.Client[0];
        return (com.castorama.webservices.xsd.Client[]) this._clientList.toArray(array);
    }

    /**
     * Method getClientCount.
     * 
     * @return the size of this collection
     */
    public int getClientCount(
    ) {
        return this._clientList.size();
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        Marshaller.marshal(this, handler);
    }

    /**
     */
    public void removeAllClient(
    ) {
        this._clientList.clear();
    }

    /**
     * Method removeClient.
     * 
     * @param vClient
     * @return true if the object was removed from the collection.
     */
    public boolean removeClient(
            final com.castorama.webservices.xsd.Client vClient) {
        boolean removed = _clientList.remove(vClient);
        return removed;
    }

    /**
     * Method removeClientAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public com.castorama.webservices.xsd.Client removeClientAt(
            final int index) {
        java.lang.Object obj = this._clientList.remove(index);
        return (com.castorama.webservices.xsd.Client) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vClient
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setClient(
            final int index,
            final com.castorama.webservices.xsd.Client vClient)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._clientList.size()) {
            throw new IndexOutOfBoundsException("setClient: Index value '" + index + "' not in range [0.." + (this._clientList.size() - 1) + "]");
        }
        
        this._clientList.set(index, vClient);
    }

    /**
     * 
     * 
     * @param vClientArray
     */
    public void setClient(
            final com.castorama.webservices.xsd.Client[] vClientArray) {
        //-- copy array
        _clientList.clear();
        
        for (int i = 0; i < vClientArray.length; i++) {
                this._clientList.add(vClientArray[i]);
        }
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * com.castorama.webservices.xsd.ClientsType
     */
    public static com.castorama.webservices.xsd.ClientsType unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.castorama.webservices.xsd.ClientsType) Unmarshaller.unmarshal(com.castorama.webservices.xsd.ClientsType.class, reader);
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
