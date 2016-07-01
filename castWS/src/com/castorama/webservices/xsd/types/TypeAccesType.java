/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id$
 */

package com.castorama.webservices.xsd.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Class TypeAccesType.
 * 
 * @version $Revision$ $Date$
 */
public class TypeAccesType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The web type
     */
    public static final int WEB_TYPE = 0;

    /**
     * The instance of the web type
     */
    public static final TypeAccesType WEB = new TypeAccesType(WEB_TYPE, "web");

    /**
     * The magasin type
     */
    public static final int MAGASIN_TYPE = 1;

    /**
     * The instance of the magasin type
     */
    public static final TypeAccesType MAGASIN = new TypeAccesType(MAGASIN_TYPE, "magasin");

    /**
     * Field _memberTable.
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type.
     */
    private final int type;

    /**
     * Field stringValue.
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private TypeAccesType(final int type, final java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate.Returns an enumeration of all possible
     * instances of TypeAccesType
     * 
     * @return an Enumeration over all possible instances of
     * TypeAccesType
     */
    public static java.util.Enumeration enumerate(
    ) {
        return _memberTable.elements();
    }

    /**
     * Method getType.Returns the type of this TypeAccesType
     * 
     * @return the type of this TypeAccesType
     */
    public int getType(
    ) {
        return this.type;
    }

    /**
     * Method init.
     * 
     * @return the initialized Hashtable for the member table
     */
    private static java.util.Hashtable init(
    ) {
        Hashtable members = new Hashtable();
        members.put("web", WEB);
        members.put("magasin", MAGASIN);
        return members;
    }

    /**
     * Method readResolve. will be called during deserialization to
     * replace the deserialized object with the correct constant
     * instance.
     * 
     * @return this deserialized object
     */
    private java.lang.Object readResolve(
    ) {
        return valueOf(this.stringValue);
    }

    /**
     * Method toString.Returns the String representation of this
     * TypeAccesType
     * 
     * @return the String representation of this TypeAccesType
     */
    public java.lang.String toString(
    ) {
        return this.stringValue;
    }

    /**
     * Method valueOf.Returns a new TypeAccesType based on the
     * given String value.
     * 
     * @param string
     * @return the TypeAccesType value of parameter 'string'
     */
    public static com.castorama.webservices.xsd.types.TypeAccesType valueOf(
            final java.lang.String string) {
        java.lang.Object obj = null;
        if (string != null) {
            obj = _memberTable.get(string);
        }
        if (obj == null) {
            String err = "" + string + " is not a valid TypeAccesType";
            throw new IllegalArgumentException(err);
        }
        return (TypeAccesType) obj;
    }

}
