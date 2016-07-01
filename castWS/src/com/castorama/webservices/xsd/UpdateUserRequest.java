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
 * Class UpdateUserRequest.
 * 
 * @version $Revision$ $Date$
 */
public class UpdateUserRequest implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _typeAcces.
     */
    private com.castorama.webservices.xsd.types.TypeAccesType _typeAcces;

    /**
     * Field _updatedUser.
     */
    private com.castorama.webservices.xsd.UpdatedUser _updatedUser;


      //----------------/
     //- Constructors -/
    //----------------/

    public UpdateUserRequest() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'typeAcces'.
     * 
     * @return the value of field 'TypeAcces'.
     */
    public com.castorama.webservices.xsd.types.TypeAccesType getTypeAcces(
    ) {
        return this._typeAcces;
    }

    /**
     * Returns the value of field 'updatedUser'.
     * 
     * @return the value of field 'UpdatedUser'.
     */
    public com.castorama.webservices.xsd.UpdatedUser getUpdatedUser(
    ) {
        return this._updatedUser;
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
     * Sets the value of field 'typeAcces'.
     * 
     * @param typeAcces the value of field 'typeAcces'.
     */
    public void setTypeAcces(
            final com.castorama.webservices.xsd.types.TypeAccesType typeAcces) {
        this._typeAcces = typeAcces;
    }

    /**
     * Sets the value of field 'updatedUser'.
     * 
     * @param updatedUser the value of field 'updatedUser'.
     */
    public void setUpdatedUser(
            final com.castorama.webservices.xsd.UpdatedUser updatedUser) {
        this._updatedUser = updatedUser;
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
     * com.castorama.webservices.xsd.UpdateUserRequest
     */
    public static com.castorama.webservices.xsd.UpdateUserRequest unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.castorama.webservices.xsd.UpdateUserRequest) Unmarshaller.unmarshal(com.castorama.webservices.xsd.UpdateUserRequest.class, reader);
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
