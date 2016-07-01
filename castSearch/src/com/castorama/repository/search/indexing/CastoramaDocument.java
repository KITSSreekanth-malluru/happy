package com.castorama.repository.search.indexing;

import java.io.Serializable;

import java.util.ArrayList;

import atg.search.routing.command.indexing.Metadata;

/**
 * Represents CastoramaDocument object for indexing
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastoramaDocument implements Serializable {
    /** relativeURL property */
    private String mRelativeURL;

    /** categoryId property */
    private String mCategoryId;

    /** repositoryId property */
    private String mRepositoryId;

    /** file property */
    private byte[] mFile;

    /** mDocumentMetadataList property */
    private ArrayList<Metadata> mDocumentMetadataList = new ArrayList<Metadata>();

    /**
     * Constructor
     */
    public CastoramaDocument() {
        super();
    }

    /**
     * Returns relativeURL property
     *
     * @return relativeURL property
     */
    public String getRelativeURL() {
        return mRelativeURL;
    }

    /**
     * Sets the value of the relativeURL property.
     *
     * @param pRelativeURL parameter to set.
     */
    public void setRelativeURL(String pRelativeURL) {
        mRelativeURL = pRelativeURL;
    }

    /**
     * Returns repositoryId property.
     *
     * @return repositoryId property.
     */
    public String getRepositoryId() {
        return mRepositoryId;
    }

    /**
     * Sets the value of the repositoryId property.
     *
     * @param pRepositoryId parameter to set.
     */
    public void setRepositoryId(String pRepositoryId) {
        mRepositoryId = pRepositoryId;
    }

    /**
     * Returns categoryId property.
     *
     * @return categoryId property.
     */
    public String getCategoryId() {
        return mCategoryId;
    }

    /**
     * Sets the value of the categoryId property.
     *
     * @param pCategoryId parameter to set.
     */
    public void setCategoryId(String pCategoryId) {
        mCategoryId = pCategoryId;
    }

    /**
     * Returns file property.
     *
     * @return file property.
     */
    public byte[] getFile() {
        return mFile;
    }

    /**
     * Sets the value of the file property.
     *
     * @param pFile parameter to set.
     */
    public void setFile(byte[] pFile) {
        mFile = pFile;
    }

    /**
     * Returns documentMetadataList property.
     *
     * @return documentMetadataList property.
     */
    public ArrayList<Metadata> getDocumentMetadataList() {
        return mDocumentMetadataList;
    }

    /**
     * Sets the value of the documentMetadataList property.
     *
     * @param pDocumentMetadataList parameter to set.
     */
    public void setDocumentMetadataList(ArrayList<Metadata> pDocumentMetadataList) {
        mDocumentMetadataList = pDocumentMetadataList;
    }



}
