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
 * Class UserType.
 * 
 * @version $Revision$ $Date$
 */
public class UserType implements java.io.Serializable {


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
     * Field _password.
     */
    private java.lang.String _password;

    /**
     * Field _civilite.
     */
    private java.lang.String _civilite;

    /**
     * Field _nom.
     */
    private java.lang.String _nom;

    /**
     * Field _prenom.
     */
    private java.lang.String _prenom;

    /**
     * Field _societe.
     */
    private java.lang.String _societe;

    /**
     * Field _codePostal.
     */
    private java.lang.String _codePostal;

    /**
     * Field _adresse.
     */
    private java.lang.String _adresse;

    /**
     * Field _complement.
     */
    private java.lang.String _complement;

    /**
     * Field _ville.
     */
    private java.lang.String _ville;

    /**
     * Field _pays.
     */
    private java.lang.String _pays;

    /**
     * Field _dateMAJProfil.
     */
    private java.lang.String _dateMAJProfil;

    /**
     * Field _dateNaissance.
     */
    private java.lang.String _dateNaissance;

    /**
     * Field _magasinReference.
     */
    private java.lang.String _magasinReference;

    /**
     * Field _dateInscription.
     */
    private java.lang.String _dateInscription;

    /**
     * Field _inscriptionNewsletter.
     */
    private boolean _inscriptionNewsletter;

    /**
     * keeps track of state for field: _inscriptionNewsletter
     */
    private boolean _has_inscriptionNewsletter;

    /**
     * Field _inscriptionNewsletterPartenaires.
     */
    private boolean _inscriptionNewsletterPartenaires;

    /**
     * keeps track of state for field:
     * _inscriptionNewsletterPartenaires
     */
    private boolean _has_inscriptionNewsletterPartenaires;

    /**
     * Field _acceptRecontact.
     */
    private int _acceptRecontact;

    /**
     * keeps track of state for field: _acceptRecontact
     */
    private boolean _has_acceptRecontact;


      //----------------/
     //- Constructors -/
    //----------------/

    public UserType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteAcceptRecontact(
    ) {
        this._has_acceptRecontact= false;
    }

    /**
     */
    public void deleteInscriptionNewsletter(
    ) {
        this._has_inscriptionNewsletter= false;
    }

    /**
     */
    public void deleteInscriptionNewsletterPartenaires(
    ) {
        this._has_inscriptionNewsletterPartenaires= false;
    }

    /**
     * Returns the value of field 'acceptRecontact'.
     * 
     * @return the value of field 'AcceptRecontact'.
     */
    public int getAcceptRecontact(
    ) {
        return this._acceptRecontact;
    }

    /**
     * Returns the value of field 'adresse'.
     * 
     * @return the value of field 'Adresse'.
     */
    public java.lang.String getAdresse(
    ) {
        return this._adresse;
    }

    /**
     * Returns the value of field 'civilite'.
     * 
     * @return the value of field 'Civilite'.
     */
    public java.lang.String getCivilite(
    ) {
        return this._civilite;
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
     * Returns the value of field 'complement'.
     * 
     * @return the value of field 'Complement'.
     */
    public java.lang.String getComplement(
    ) {
        return this._complement;
    }

    /**
     * Returns the value of field 'dateInscription'.
     * 
     * @return the value of field 'DateInscription'.
     */
    public java.lang.String getDateInscription(
    ) {
        return this._dateInscription;
    }

    /**
     * Returns the value of field 'dateMAJProfil'.
     * 
     * @return the value of field 'DateMAJProfil'.
     */
    public java.lang.String getDateMAJProfil(
    ) {
        return this._dateMAJProfil;
    }

    /**
     * Returns the value of field 'dateNaissance'.
     * 
     * @return the value of field 'DateNaissance'.
     */
    public java.lang.String getDateNaissance(
    ) {
        return this._dateNaissance;
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
     * Returns the value of field 'inscriptionNewsletter'.
     * 
     * @return the value of field 'InscriptionNewsletter'.
     */
    public boolean getInscriptionNewsletter(
    ) {
        return this._inscriptionNewsletter;
    }

    /**
     * Returns the value of field
     * 'inscriptionNewsletterPartenaires'.
     * 
     * @return the value of field 'InscriptionNewsletterPartenaires'
     */
    public boolean getInscriptionNewsletterPartenaires(
    ) {
        return this._inscriptionNewsletterPartenaires;
    }

    /**
     * Returns the value of field 'magasinReference'.
     * 
     * @return the value of field 'MagasinReference'.
     */
    public java.lang.String getMagasinReference(
    ) {
        return this._magasinReference;
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
     * Returns the value of field 'password'.
     * 
     * @return the value of field 'Password'.
     */
    public java.lang.String getPassword(
    ) {
        return this._password;
    }

    /**
     * Returns the value of field 'pays'.
     * 
     * @return the value of field 'Pays'.
     */
    public java.lang.String getPays(
    ) {
        return this._pays;
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
     * Returns the value of field 'societe'.
     * 
     * @return the value of field 'Societe'.
     */
    public java.lang.String getSociete(
    ) {
        return this._societe;
    }

    /**
     * Returns the value of field 'ville'.
     * 
     * @return the value of field 'Ville'.
     */
    public java.lang.String getVille(
    ) {
        return this._ville;
    }

    /**
     * Method hasAcceptRecontact.
     * 
     * @return true if at least one AcceptRecontact has been added
     */
    public boolean hasAcceptRecontact(
    ) {
        return this._has_acceptRecontact;
    }

    /**
     * Method hasInscriptionNewsletter.
     * 
     * @return true if at least one InscriptionNewsletter has been
     * added
     */
    public boolean hasInscriptionNewsletter(
    ) {
        return this._has_inscriptionNewsletter;
    }

    /**
     * Method hasInscriptionNewsletterPartenaires.
     * 
     * @return true if at least one
     * InscriptionNewsletterPartenaires has been added
     */
    public boolean hasInscriptionNewsletterPartenaires(
    ) {
        return this._has_inscriptionNewsletterPartenaires;
    }

    /**
     * Returns the value of field 'inscriptionNewsletter'.
     * 
     * @return the value of field 'InscriptionNewsletter'.
     */
    public boolean isInscriptionNewsletter(
    ) {
        return this._inscriptionNewsletter;
    }

    /**
     * Returns the value of field
     * 'inscriptionNewsletterPartenaires'.
     * 
     * @return the value of field 'InscriptionNewsletterPartenaires'
     */
    public boolean isInscriptionNewsletterPartenaires(
    ) {
        return this._inscriptionNewsletterPartenaires;
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
     * Sets the value of field 'acceptRecontact'.
     * 
     * @param acceptRecontact the value of field 'acceptRecontact'.
     */
    public void setAcceptRecontact(
            final int acceptRecontact) {
        this._acceptRecontact = acceptRecontact;
        this._has_acceptRecontact = true;
    }

    /**
     * Sets the value of field 'adresse'.
     * 
     * @param adresse the value of field 'adresse'.
     */
    public void setAdresse(
            final java.lang.String adresse) {
        this._adresse = adresse;
    }

    /**
     * Sets the value of field 'civilite'.
     * 
     * @param civilite the value of field 'civilite'.
     */
    public void setCivilite(
            final java.lang.String civilite) {
        this._civilite = civilite;
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
     * Sets the value of field 'complement'.
     * 
     * @param complement the value of field 'complement'.
     */
    public void setComplement(
            final java.lang.String complement) {
        this._complement = complement;
    }

    /**
     * Sets the value of field 'dateInscription'.
     * 
     * @param dateInscription the value of field 'dateInscription'.
     */
    public void setDateInscription(
            final java.lang.String dateInscription) {
        this._dateInscription = dateInscription;
    }

    /**
     * Sets the value of field 'dateMAJProfil'.
     * 
     * @param dateMAJProfil the value of field 'dateMAJProfil'.
     */
    public void setDateMAJProfil(
            final java.lang.String dateMAJProfil) {
        this._dateMAJProfil = dateMAJProfil;
    }

    /**
     * Sets the value of field 'dateNaissance'.
     * 
     * @param dateNaissance the value of field 'dateNaissance'.
     */
    public void setDateNaissance(
            final java.lang.String dateNaissance) {
        this._dateNaissance = dateNaissance;
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
     * Sets the value of field 'inscriptionNewsletter'.
     * 
     * @param inscriptionNewsletter the value of field
     * 'inscriptionNewsletter'.
     */
    public void setInscriptionNewsletter(
            final boolean inscriptionNewsletter) {
        this._inscriptionNewsletter = inscriptionNewsletter;
        this._has_inscriptionNewsletter = true;
    }

    /**
     * Sets the value of field 'inscriptionNewsletterPartenaires'.
     * 
     * @param inscriptionNewsletterPartenaires the value of field
     * 'inscriptionNewsletterPartenaires'.
     */
    public void setInscriptionNewsletterPartenaires(
            final boolean inscriptionNewsletterPartenaires) {
        this._inscriptionNewsletterPartenaires = inscriptionNewsletterPartenaires;
        this._has_inscriptionNewsletterPartenaires = true;
    }

    /**
     * Sets the value of field 'magasinReference'.
     * 
     * @param magasinReference the value of field 'magasinReference'
     */
    public void setMagasinReference(
            final java.lang.String magasinReference) {
        this._magasinReference = magasinReference;
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
     * Sets the value of field 'password'.
     * 
     * @param password the value of field 'password'.
     */
    public void setPassword(
            final java.lang.String password) {
        this._password = password;
    }

    /**
     * Sets the value of field 'pays'.
     * 
     * @param pays the value of field 'pays'.
     */
    public void setPays(
            final java.lang.String pays) {
        this._pays = pays;
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
     * Sets the value of field 'societe'.
     * 
     * @param societe the value of field 'societe'.
     */
    public void setSociete(
            final java.lang.String societe) {
        this._societe = societe;
    }

    /**
     * Sets the value of field 'ville'.
     * 
     * @param ville the value of field 'ville'.
     */
    public void setVille(
            final java.lang.String ville) {
        this._ville = ville;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled com.castorama.webservices.xsd.UserTyp
     */
    public static com.castorama.webservices.xsd.UserType unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (com.castorama.webservices.xsd.UserType) Unmarshaller.unmarshal(com.castorama.webservices.xsd.UserType.class, reader);
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
