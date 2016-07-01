package com.castorama.commerce.pricing;

import atg.nucleus.GenericService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CastVATManager extends GenericService {

    /**
     * Current VAT value
     */
    private double mCurrentVATValue;

    /**
     * New VAT value
     */
    private double mNewVATValue;

    /**
     * Date of VAT switching
     */
    private String mDateOfVATSwitch;

    /**
     * Switching date input format
     */
    private String mDateFormat;

    public double getCurrentVATValue() {
        return mCurrentVATValue;
    }

    public void setCurrentVATValue(double mCurrentVATValue) {
        this.mCurrentVATValue = mCurrentVATValue;
    }

    public double getNewVATValue() {
        return mNewVATValue;
    }

    public void setNewVATValue(double mNewVATValue) {
        this.mNewVATValue = mNewVATValue;
    }

    public String getDateOfVATSwitch() {
        return mDateOfVATSwitch;
    }

    public void setDateOfVATSwitch(String mDateOfVATSwitch) {
        this.mDateOfVATSwitch = mDateOfVATSwitch;
    }

    public double getVATValue() {
        Date currentDate = new Date();
        return getVATValue(currentDate);
    }

    public String getDateFormat() {
        return mDateFormat;
    }

    public void setDateFormat(String mDateFormat) {
        this.mDateFormat = mDateFormat;
    }

    public double getVATValue(final Date date) {
        double vatResult = mCurrentVATValue;
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat);
        Date dateOfVATSwitch;
        try {
            dateOfVATSwitch = dateFormat.parse(mDateOfVATSwitch);
            if (date != null) {
                if (mDateOfVATSwitch != null) {
                    if (date.after(dateOfVATSwitch)) {
                        vatResult = mNewVATValue;
                    }
                }
            }
        } catch (ParseException e) {
            if (isLoggingError()) {
                vlogError("Unable to parse string {0} using date format {1}", mDateOfVATSwitch, mDateFormat, e);
            }
        }
        return vatResult;
    }
}
