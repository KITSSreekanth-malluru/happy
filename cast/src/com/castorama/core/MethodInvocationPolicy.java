package com.castorama.core;

import java.lang.reflect.Method;

import atg.repository.Repository;

/**
 */
public interface MethodInvocationPolicy {

    public abstract Object getObject();

    public abstract Method getMethod() throws SecurityException, NoSuchMethodException;

    public abstract String[] getItemTypes();

    public abstract Repository getRepository();

    public abstract boolean isActive();

}
