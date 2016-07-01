package com.castorama.core;

import java.lang.reflect.Method;

import atg.repository.Repository;

/**
 */
public class RepositoryMethodInvocationPolicy implements MethodInvocationPolicy {

    private String mMethodName;
    private Object mObject;
    private String[] mItemTypes;
    private Repository mRepository;
    private boolean mActive = true;

    @Override
    public Method getMethod() throws SecurityException, NoSuchMethodException {
        if (getMethodName() != null) {
            return getObject().getClass().getMethod(getMethodName(), (Class[]) null);
        }
        return null;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return mMethodName;
    }

    /**
     * @param pMethodName
     *          the methodName to set
     */
    public void setMethodName(String pMethodName) {
        mMethodName = pMethodName;
    }

    /**
     * @return the object
     */
    @Override
    public Object getObject() {
        return mObject;
    }

    /**
     * @param pObject
     *          the object to set
     */
    public void setObject(Object pObject) {
        mObject = pObject;
    }

    /**
     * @return the itemTypes
     */
    @Override
    public String[] getItemTypes() {
        return mItemTypes;
    }

    /**
     * @param pItemTypes
     *          the itemTypes to set
     */
    public void setItemTypes(String[] pItemTypes) {
        mItemTypes = pItemTypes;
    }

    /**
     * @return the repository
     */
    @Override
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * @param pRepository
     *          the repository to set
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * @return the active
     */
    @Override
    public boolean isActive() {
        return mActive;
    }

    /**
     * @param pActive
     *          the active to set
     */
    public void setActive(boolean pActive) {
        mActive = pActive;
    }

}
