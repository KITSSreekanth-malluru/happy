package com.castorama.utils;

/**
 * Settings for address fields bounds. Addresses like profile or checkout are different.
 * 
 * @see /com/castorama/util/ContactAddressBounds.properties
 * @see /com/castorama/util/DeliveryAddressBounds.properties
 * 
 * @author Vasili_Ivus
 *
 */

public class AddressesBounds {

    /**
     * Property for field
     * DPS_CONTACT_INFO.PREFIX VARCHAR2 (40)
     * DCSPP_SHIP_ADDR.PREFIX VARCHAR2 (40)
     * default value is minimal size
     */
    private int mPrefixSize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.FIRST_NAME VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.VARCHAR2 (40)
     * default value is minimal size
     */ 
    private int mFirstNameSize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.MIDDLE_NAME VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.MIDDLE_NAME VARCHAR2 (40)
     * default value is minimal size
     */
    private int mMiddleNameSize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.LAST_NAME VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.LAST_NAME VARCHAR2 (40)
     * default value is minimal size
     */
    private int mLastNameSize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.SUFFIX VARCHAR2 (40)
     * DCSPP_SHIP_ADDR.SUFFIX VARCHAR2 (40)
     * default value is minimal size
     */
    private int mSuffixSize = 40;
    
    /**
     * Property for field
     * DPS_CONTACT_INFO.JOB_TITLE VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.JOB_TITLE VARCHAR2 (40)
     * default value is minimal size
     */
    private int mJobTitleSize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.COMPANY_NAME VARCHAR2 (40)
     * DCSPP_SHIP_ADDR.COMPANY_NAME VARCHAR2 (40)
     * default value is minimal size
     */
    private int mCompanyNameSize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.DDRESS1 VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.ADDRESS1 VARCHAR2 (50)
     * default value is minimal size
     */
    private int mAddress1Size = 50;

    /**
     * Property for field
     * DPS_CONTACT_INFO.ADDRESS2 VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.ADDRESS2 VARCHAR2 (50)
     * default value is minimal size
     */
    private int mAddress2Size = 50;

    /**
     * Property for field
     * DPS_CONTACT_INFO.ADDRESS3 VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.ADDRESS3 VARCHAR2 (50)
     * default value is minimal size
     */
    private int mAddress3Size = 50;

    /**
     * Property for field
     * DPS_CONTACT_INFO.CITY VARCHAR2 (100)
     * DCSPP_SHIP_ADDR.CITY VARCHAR2 (40)
     * default value is minimal size
     */
    private int mCitySize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.STATE VARCHAR2 (20)
     * DCSPP_SHIP_ADDR.STATE VARCHAR2 (40)
     * default value is minimal size
     */
    private int mStateSize = 20;

    /**
     * Property for field
     * DPS_CONTACT_INFO.POSTAL_CODE VARCHAR2 (10)
     * DCSPP_SHIP_ADDR.POSTAL_CODE VARCHAR2 (10)
     * default value is minimal size
     */
    private int mPostalCodeSize = 10;

    /**
     * Property for field
     * DPS_CONTACT_INFO.COUNTY VARCHAR2 (40)
     * DCSPP_SHIP_ADDR.COUNTY VARCHAR2 (40)
     * default value is minimal size
     */
    private int mCountySize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.COUNTRY VARCHAR2 (40)
     * DCSPP_SHIP_ADDR.COUNTRY VARCHAR2 (40)
     * default value is minimal size
     */
    private int mCountrySize = 40;

    /**
     * Property for field
     * DPS_CONTACT_INFO.PHONE_NUMBER VARCHAR2 (15)
     * DCSPP_SHIP_ADDR.PHONE_NUMBER VARCHAR2 (40)
     * default value is minimal size
     */
    private int mPhoneNumberSize = 17;

    /**
     * Property for field
     * DPS_CONTACT_INFO.FAX_NUMBER VARCHAR2 (15)
     * DCSPP_SHIP_ADDR.FAX_NUMBER VARCHAR2 (15)
     * default value is minimal size
     */
    private int mFaxNumberSize = 15;

    /**
     * Property for field
     * CASTO_CONTACT_INFO.EMAIL VARCHAR2 (255)
     * DCSPP_SHIP_ADDR.EMAIL VARCHAR2 (255)
     * default value is minimal size
     */
    private int mEmailSize = 50;

    /**
     * Property for field
     * CASTO_CONTACT_INFO.LOCALITY VARCHAR2 (100)
     * ... 
     * default value is minimal size
     */
    private int mLocalitySize = 100;

    /**
     * Property for field
     * CASTO_CONTACT_INFO.PHONE_NUMBER_2 VARCHAR2 (15)
     * ... 
     * default value is minimal size
     */
    private int mPhoneNumber2Size = 17;


    /**
     * Returns first name max size 
     * @return first name max size
     */
    public int getFirstNameSize() {
        return mFirstNameSize;
    }

    /**
     * Sets first name max size 
     * @param firstNameSize first name max size
     */
    public void setFirstNameSize(int firstNameSize) {
        this.mFirstNameSize = firstNameSize;
    }

    /**
     * Returns middle name max size
     * @return middle name max size
     */
    public int getMiddleNameSize() {
        return mMiddleNameSize;
    }

    /**
     * Sets middle name max size
     * @param middleNameSize middle name max size
     */
    public void setMiddleNameSize(int middleNameSize) {
        this.mMiddleNameSize = middleNameSize;
    }

    /**
     * Returns last name max size
     * @return last name max size
     */
    public int getLastNameSize() {
        return mLastNameSize;
    }

    /**
     * Sets last name max size
     * @param lastNameSize last name max size
     */
    public void setLastNameSize(int lastNameSize) {
        this.mLastNameSize = lastNameSize;
    }

    /**
     * Returns address 1 max size
     * @return address 1 max size
     */
    public int getAddress1Size() {
        return mAddress1Size;
    }

    /**
     * Sets address 1 max size
     * @param address1Size address 1 max size
     */
    public void setAddress1Size(int address1Size) {
        this.mAddress1Size = address1Size;
    }

    /**
     * Returns address 2 max size
     * @return address 2 max size
     */
    public int getAddress2Size() {
        return mAddress2Size;
    }

    /**
     * Sets address 2 max size
     * @param address2Size address 2 max size
     */
    public void setAddress2Size(int address2Size) {
        this.mAddress2Size = address2Size;
    }

    /**
     * Returns city max size
     * @return city max size
     */
    public int getCitySize() {
        return mCitySize;
    }

    /**
     * Sets city max size
     * @param citySize city max size
     */
    public void setCitySize(int citySize) {
        this.mCitySize = citySize;
    }

    /**
     * Returns state max size
     * @return state max size
     */
    public int getStateSize() {
        return mStateSize;
    }

    /**
     * Sets state max size
     * @param stateSize state max size
     */
    public void setStateSize(int stateSize) {
        this.mStateSize = stateSize;
    }

    /**
     * Returns postal code max size
     * @return postal code max size
     */
    public int getPostalCodeSize() {
        return mPostalCodeSize;
    }

    /**
     * Sets postal code max size
     * @param postalCodeSize postal code max size
     */
    public void setPostalCodeSize(int postalCodeSize) {
        this.mPostalCodeSize = postalCodeSize;
    }

    /**
     * Returns country max size
     * @return country max size
     */
    public int getCountrySize() {
        return mCountrySize;
    }

    /**
     * Sets country max size
     * @param countrySize country max size
     */
    public void setCountrySize(int countrySize) {
        this.mCountrySize = countrySize;
    }

    /**
     * Returns phone number max size
     * @return phone number max size
     */
    public int getPhoneNumberSize() {
        return mPhoneNumberSize;
    }

    /**
     * Returns phone number max size
     * @param phoneNumberSize phone number max size
     */
    public void setPhoneNumberSize(int phoneNumberSize) {
        this.mPhoneNumberSize = phoneNumberSize;
    }

    /**
     * Returns fax number max size
     * @return fax number max size
     */
    public int getFaxNumberSize() {
        return mFaxNumberSize;
    }

    /**
     * Sets fax number max size
     * @param faxNumberSize fax number max size
     */
    public void setFaxNumberSize(int faxNumberSize) {
        this.mFaxNumberSize = faxNumberSize;
    }

    /**
     * Returns e-mail max size
     * @return e-mail max size
     */
    public int getEmailSize() {
        return mEmailSize;
    }

    /**
     * Sets e-mail max size
     * @param emailSize e-mail max size
     */
    public void setEmailSize(int emailSize) {
        this.mEmailSize = emailSize;
    }

    /**
     * Returns prefix max size
     * @return prefix max size
     */
    public int getPrefixSize() {
        return mPrefixSize;
    }

    /**
     * Sets prefix max size
     * @param prefixSize prefix max size
     */
    public void setPrefixSize(int prefixSize) {
        this.mPrefixSize = prefixSize;
    }

    /**
     * Returns suffix max size
     * @return suffix max size
     */
    public int getSuffixSize() {
        return mSuffixSize;
    }

    /**
     * Sets suffix max size
     * @param suffixSize suffix max size
     */
    public void setSuffixSize(int suffixSize) {
        this.mSuffixSize = suffixSize;
    }

    /**
     * Returns job title max size
     * @return job title max size
     */
    public int getJobTitleSize() {
        return mJobTitleSize;
    }

    /**
     * Sets job title max size
     * @param jobTitleSize job title max size
     */
    public void setJobTitleSize(int jobTitleSize) {
        this.mJobTitleSize = jobTitleSize;
    }

    /**
     * Returns company name max size
     * @return company name max size
     */
    public int getCompanyNameSize() {
        return mCompanyNameSize;
    }

    /**
     * Sets company name max size
     * @param companyNameSize company name max size
     */
    public void setCompanyNameSize(int companyNameSize) {
        this.mCompanyNameSize = companyNameSize;
    }

    /**
     * Returns address 3 max size
     * @return address 3 max size
     */
    public int getAddress3Size() {
        return mAddress3Size;
    }

    /**
     * Sets address 3 max size
     * @param address3Size address 3 max size
     */
    public void setAddress3Size(int address3Size) {
        this.mAddress3Size = address3Size;
    }

    /**
     * Returns county max size
     * @return county max size
     */
    public int getCountySize() {
        return mCountySize;
    }

    /**
     * Sets county max size
     * @param countySize county max size
     */
    public void setCountySize(int countySize) {
        this.mCountySize = countySize;
    }

    /**
     * Returns locality max size
     * @return locality max size
     */
    public int getLocalitySize() {
        return mLocalitySize;
    }

    /**
     * Sets locality max size
     * @param localitySize locality max size
     */
    public void setLocalitySize(int localitySize) {
        this.mLocalitySize = localitySize;
    }

    /**
     * Returns phone number 2 max size
     * @return phone number 2 max size
     */
    public int getPhoneNumber2Size() {
        return mPhoneNumber2Size;
    }

    /**
     * Sets phone number 2 max size
     * @param phoneNumber2Size phone number 2 max size
     */
    public void setPhoneNumber2Size(int phoneNumber2Size) {
        this.mPhoneNumber2Size = phoneNumber2Size;
    }
    
}