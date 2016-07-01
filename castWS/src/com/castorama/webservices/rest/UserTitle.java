package com.castorama.webservices.rest;

/**
 * <p><tt>UserTitle</tt> is the enumeration of the formal titles of persons.
 * There titles are used in the messages sent/received by the
 * <tt>{@link com.castorama.webservices.rest.GetUserInfoServlet}</tt> and
 * <tt>{@link com.castorama.webservices.rest.UpdateUserInfoServlet}</tt> web service servlets.</p>
 * 
 * <p>In addition, each item in the enumeration provides its equivalent used in the user profile repository
 * (see <tt>{@link #repositoryCode()}</tt>). Reverse conversion is performed by
 * <tt>{@link #fromRepositoryCode(String)}</tt>.</p>
 */
enum UserTitle {
    /** The title of a man. */
    MONSIEUR("mr"),
    /** The title of a married woman. */
    MADAME("mrs"),
    /** The title of an unmarried woman. */
    MLLE("miss"),
    /** The title that indicates the user account represents an organisation. */
    SOCIETE("organization");
    
    private final String repositoryCode;

    private UserTitle(final String repositoryCode) {
        this.repositoryCode = repositoryCode;
    }
    
    /**
     * Returns the user profile repository equivalent for this user title.
     *  
     * @return the user profile repository equivalent for this user title.
     */
    public String repositoryCode() {
        return repositoryCode;
    }
    
    /**
     * Converts the user profile repository title into a user title.
     * 
     * @param repositoryCode the user profile repository title code.
     * 
     * @return the user title that is an equivalent to the given user profile
     * repository title.
     * 
     * @throws IllegalArgumentException if <i>repositoryCode</i> is incorrect.
     */
    public static UserTitle fromRepositoryCode(final String repositoryCode) {
        for (UserTitle title : values()) {
            if (title.repositoryCode.equals(repositoryCode)) {
                return title;
            }
        }
        throw new IllegalArgumentException(String.valueOf(repositoryCode));
    }
}
