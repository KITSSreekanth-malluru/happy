package com.castorama.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author EPAM team
 */
public class TabsMap {
    /** tabs property. */
    private LinkedHashMap<String, Tab> mTabs =
        new LinkedHashMap<String, Tab>() {
            @Override public Tab get(Object key) {
                Tab result = super.get(key);

                if (null == result) {
                    result = new Tab();

                    put((String) key, result);
                }

                return result;
            }
        };

    /** slitter property. */
    private String mSlitter = ",";

    /**
     * Creates a new TabsMap object.
     */
    public TabsMap() {
    }

    /**
     *
     *
     * @return 
     */
    @Override public String toString() {
        StringBuffer sb = new StringBuffer(256);
        for (Iterator<String> it = getKeys().iterator(); it.hasNext();) {
            String key = it.next();
            sb.append("tab: ").append(key).append("(");
            sb.append(this.mTabs.get(key).toString()).append(")\n");
        }
        return sb.toString();
    }

    /**
     * Returns tabs property.
     *
     * @return tabs property.
     */
    public LinkedHashMap<String, Tab> getTabs() {
        return mTabs;
    }

    /**
     * Sets the value of the tabs property.
     *
     * @param pTabs parameter to set.
     */
    public void setTabs(LinkedHashMap<String, Tab> pTabs) {
        if (null != pTabs) {
            this.mTabs = pTabs;
        } else {
            setClear(true);
        }
    }

    /**
     * Returns keys property.
     *
     * @return keys property.
     */
    public Set<String> getKeys() {
        return this.mTabs.keySet();
    }

    /**
     * Sets the value of the keys property.
     *
     * @param keys parameter to set.
     */
    public void setKeys(Set<String> keys) {
    }

    /**
     * Returns clear property.
     *
     * @return clear property.
     */
    public boolean getClear() {
        return this.mTabs.isEmpty();
    }

    /**
     * Sets the value of the clear property.
     *
     * @param clear parameter to set.
     */
    public void setClear(boolean clear) {
        if (clear) {
            this.mTabs.clear();
        }
    }

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

    /*
    public void setTab(String tab) {
        String[] values =  tab.split(getSlitter());
        for (int i = 0; (i + 1) < values.length; i += 2) {
            this.mTabs.put(values[i], new Tab(values[i + 1]));
        }

    }

    public String getTab() {
        return "";
    }
     */
}
