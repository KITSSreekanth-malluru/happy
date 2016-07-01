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
 * Class RechercheClientsRequest.
 * 
 * @version $Revision$ $Date$
 */
public class RechercheClientsRequest implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _email.
     */
    private java.lang.String _email;

    /**
     * Field _nom.
     */
    private java.lang.String _nom;

    /**
     * Field _prenom.
     */
    private java.lang.String _prenom;

    /**
     * Field _codePostal.
     */
    private java.lang.String _codePostal;

    /**
     * Field _pageSize.
     */
    private int _pageSize;

    /**
     * keeps track of state for field: _pageSize
     */
    private boolean _has_pageSize;

    /**
     * Field _pageNumber.
     */
    private int _pageNumber;

    /**
     * keeps track of state for field: _pageNumber
     */
    private boolean _has_pageNumber;


      //----------------/
     //- Constructors -/
    //----------------/

    public RechercheClientsRequest() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deletePageNumber(
    ) {
        this._has_pageNumber= false;
    }

    /**
     */
    public void deletePageSize(
    ) {
        this._has_pageSize= false;
    }

    /**
     * Returns the value of field 'codePostal'.
     * 
     * @return the value of field 'CodePostal'.
     */
    public java.lang.String getCodePostal(
    ) {
        return this._codePostal;
    }

    /**
     * Returns the value of field 'email'.
     * 
     * @return the value of field 'Email'.
     */
    public java.lang.String getEmail(
    ) {
        return this._email;
    }

    /**
     * Returns the value of field 'id'.
     * 
     * @return the value of field 'Id'.
     */
    public java.lang.String getId(
    ) {
        return this._id;
    }

    /**
     * Returns the value of field 'nom'.
     * 
     * @return the value of field 'Nom'.
     */
    public java.lang.String getNom(
    ) {
        return this._nom;
    }

    /**
     * Returns the value of field 'pageNumber'.
     * 
     * @return the value of field 'PageNumber'.
     */
    public int getPageNumber(
    ) {
        return this._pageNumber;
    }

    /**
     * Returns the value of field 'pageSize'.
     * 
     * @return the value of field 'PageSize'.
     */
    public int getPageSize(
    ) {
        return this._pageSize;
    }

    /**
     * Returns the value of field 'prenom'.
     * 
     * @return the value of field 'Prenom'.
     */
    public java.lang.String getPrenom(
    ) {
        return this._prenom;
    }

    /**
     * Method hasPageNumber.
     * 
     * @return true if at least one PageNumber has been added
     */
    public boolean hasPageNumber(
    ) {
        return this._has_pageNumber;
    }

    /**
     * Method hasPageSize.
     * 
     * @return true if at least one PageSize has been added
     */
    public boolean hasPageSize(
    ) {
        return this._has_pageSize;
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
     * Sets the value of field 'codePostal'.
     * 
     * @param codePostal the value of field 'codePostal'.
     */
    public void setCodePostal(
            final java.lang.String codePostal) {
        this._codePostal = codePostal;
    }

    /**
     * Sets the value of field 'email'.
     * 
     * @param email the value of field 'email'.
     */
    public void setEmail(
            final java.lang.String email) {
        this._email = email;
    }

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(
            final java.lang.String id) {
        this._id = id;
    }

    /**
     * Sets the value of field 'nom'.
     * 
     * @param nom the value of field 'nom'.
     */
    public void setNom(
            final java.lang.String nom) {
        this._nom = nom;
    }

    /**
     * Sets the value of field 'pageNumber'.
     * 
     * @param pageNumber the value of field 'pageNumber'.
     */
    public void setPageNumber(
            final int pageNumber) {
        this._pageNumber = pageNumber;
        this._has_pageNumber = true;
    }

    /**
     * Sets the value of field 'pageSize'.
     * 
     * @param pageSize the value of field 'pageSize'.
     */
    public void setPageSize(
            final int pageSize) {
        this._pageSize = pageSize;
        this._has_pageSize = true;
    }

    /**
     * Sets the value of field 'prenom'.
     * 
     * @param prenom the value of field 'prenom'.
     */
    public void setPrenom(
            final java.lang.String prenom) {
        this._prenom = prenom;
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
     * com.castorama.webservices.xsd.RechercheClientsRequest
     */
    public static com.castorama.webservices.xsd.RechercheClientsRequest unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.castorama.webservices.xsd.RechercheClientsRequest) Unmarshaller.unmarshal(com.castorama.webservices.xsd.RechercheClientsRequest.class, reader);
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
