package com.castorama.checkout;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author EPAM team
 */
public class PaymentTabs {
    /** slitter constant. */
    private String mSlitter = ",";

    /** tabs constant. */
    private List<Tab> mTabs = new ArrayList<Tab>();

    /**
     * Returns slitter property.
     *
     * @return slitter property.
     */
    public String getSlitter() {
        return (null == mSlitter) ? "," : mSlitter;
    }

    /**
     * Sets the value of the slitter property.
     *
     * @param pSlitter parameter to set.
     */
    public void setSlitter(String pSlitter) {
        this.mSlitter = pSlitter;
    }

    /**
     * Sets the value of the tabs property.
     *
     * @param tabs parameter to set.
     */
    public void setTabs(String tabs) {
        String[] values = tabs.split(getSlitter());
        for (int i = 0; (i + 1) < values.length; i += 2) {
            this.mTabs.add(new Tab(values[i], values[i + 1]));
        }

    }

    /**
     * Returns allTabs property.
     *
     * @return allTabs property.
     */
    public List<Tab> getAllTabs() {
        return mTabs;
    }

    /**
     * Sets the value of the allTabs property.
     *
     * @param tabs parameter to set.
     */
    public void setAllTabs(List<Tab> tabs) {
        this.mTabs = tabs;
    }

    /**
     *
     * @author EPAM team
     */
    public class Tab {
        /** id constant. */
        private String id;

        /** jspPage constant. */
        private String jspPage;

        /**
         * Creates a new Tab object.
         *
         * @param id      parameter
         * @param jspPage parameter
         */
        public Tab(String id, String jspPage) {
            super();
            this.id = id;
            this.jspPage = jspPage;
        }

        /**
         * Returns id property.
         *
         * @return id property.
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         *
         * @param id parameter to set.
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Returns jspPage property.
         *
         * @return jspPage property.
         */
        public String getJspPage() {
            return jspPage;
        }

        /**
         * Sets the value of the jspPage property.
         *
         * @param jspPage parameter to set.
         */
        public void setJspPage(String jspPage) {
            this.jspPage = jspPage;
        }

    }
}
