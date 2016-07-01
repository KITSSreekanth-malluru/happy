package com.castorama.integration.infokiosk;

import java.util.List;
import java.util.Map;

public class InfoKioskCategory {

    private String name;
    private String id;
    private String type;
    private String typeNav;
    private Map<String, InfoKioskCategory> subcategories;
    private Map<Integer, List<Integer>> skus;

    public InfoKioskCategory() {
    }
    
    public InfoKioskCategory(String name, String type, String typeNav) {
        this.name = name;
        this.type = type;
        this.typeNav = typeNav;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeNav() {
        return typeNav;
    }

    public void setTypeNav(String typeNav) {
        this.typeNav = typeNav;
    }

    public Map<String, InfoKioskCategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(Map<String, InfoKioskCategory> subcategories) {
        this.subcategories = subcategories;
    }

    public Map<Integer, List<Integer>> getSkus() {
        return skus;
    }

    public void setSkus(Map<Integer, List<Integer>> skus) {
        this.skus = skus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InfoKioskCategory)) {
            return false;
        }
        InfoKioskCategory other = (InfoKioskCategory) object;
        if ((this.name == null && other.name != null)
                || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
