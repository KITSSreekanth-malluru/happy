package com.castorama.payment;

import atg.nucleus.GenericService;

public class PayboxParametersConfiguration extends GenericService {

    private String pbx_site;
    private String pbx_rang;
    private String pbx_identifiant;
    private String pbx_devise;
    private String pbx_retour;
    private String pbx_paybox;
    private String pbx_backup1;
    private String pbx_repondre_a;
    private String pbx_annule;
    private String pbx_effectue;
    private String pbx_refuse;
    private String pbx_nbcarteskdo;
    private String pbx_secret_key;
    private String pbx_langue;
    private String pbx_source;
    private String pbx_choix;
    private String pbx_paybox_test;
    private boolean testMode;
    private String publicKey;
    private String directPlusKey;
    private boolean accessibleForPaybox;

    public String getPublicKey() {
        return publicKey;
    }

    public void setOpenKey(String openKey) {
        this.publicKey = openKey;
    }

    public boolean getTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public String getPbx_secret_key() {
        return pbx_secret_key;
    }

    public void setPbx_secret_key(String pbx_secret_key) {
        this.pbx_secret_key = pbx_secret_key;
    }

    public String getPbx_annule() {
        return pbx_annule;
    }

    public void setPbx_annule(String pbx_annule) {
        this.pbx_annule = pbx_annule;
    }

    public String getPbx_effectue() {
        return pbx_effectue;
    }

    public void setPbx_effectue(String pbx_effectue) {
        this.pbx_effectue = pbx_effectue;
    }

    public String getPbx_nbcarteskdo() {
        return pbx_nbcarteskdo;
    }

    public void setPbx_nbcarteskdo(String pbx_nbcarteskdo) {
        this.pbx_nbcarteskdo = pbx_nbcarteskdo;
    }

    public String getPbx_refuse() {
        return pbx_refuse;
    }

    public void setPbx_refuse(String pbx_refuse) {
        this.pbx_refuse = pbx_refuse;
    }

    public String getPbx_repondre_a() {
        return pbx_repondre_a;
    }

    public void setPbx_repondre_a(String pbx_repondre_a) {
        this.pbx_repondre_a = pbx_repondre_a;
    }

    public String getPbx_backup1() {
        return pbx_backup1;
    }

    public void setPbx_backup1(String pbx_backup1) {
        this.pbx_backup1 = pbx_backup1;
    }

    public String getPbx_devise() {
        return pbx_devise;
    }

    public void setPbx_devise(String pbx_devise) {
        this.pbx_devise = pbx_devise;
    }

    public String getPbx_identifiant() {
        return pbx_identifiant;
    }

    public void setPbx_identifiant(String pbx_identifiant) {
        this.pbx_identifiant = pbx_identifiant;
    }

    public String getPbx_paybox() {
        return pbx_paybox;
    }

    public void setPbx_paybox(String pbx_paybox) {
        this.pbx_paybox = pbx_paybox;
    }

    public String getPbx_rang() {
        return pbx_rang;
    }

    public void setPbx_rang(String pbx_rang) {
        this.pbx_rang = pbx_rang;
    }

    public String getPbx_retour() {
        return pbx_retour;
    }

    public void setPbx_retour(String pbx_retour) {
        this.pbx_retour = pbx_retour;
    }

    public String getPbx_site() {
        return pbx_site;
    }

    public void setPbx_site(String pbx_site) {
        this.pbx_site = pbx_site;
    }

    public String getPbx_langue() {
        return pbx_langue;
    }

    public void setPbx_langue(String pbx_langue) {
        this.pbx_langue = pbx_langue;
    }

    public String getPbx_source() {
        return pbx_source;
    }

    public void setPbx_source(String pbx_source) {
        this.pbx_source = pbx_source;
    }

    public String getPbx_choix() {
        return pbx_choix;
    }

    public void setPbx_choix(String pbx_choix) {
        this.pbx_choix = pbx_choix;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPbx_paybox_test() {
        return pbx_paybox_test;
    }

    public void setPbx_paybox_test(String pbx_paybox_test) {
        this.pbx_paybox_test = pbx_paybox_test;
    }

    public String getDirectPlusKey() {
        return directPlusKey;
    }

    public void setDirectPlusKey(String directPlusKey) {
        this.directPlusKey = directPlusKey;
    }

    public boolean isAccessibleForPaybox() {
        return accessibleForPaybox;
    }

    public void setAccessibleForPaybox(boolean accessibleForPaybox) {
        this.accessibleForPaybox = accessibleForPaybox;
    }
}
