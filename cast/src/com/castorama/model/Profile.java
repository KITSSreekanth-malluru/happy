package com.castorama.model;

import com.castorama.commerce.profile.Constants;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Profile Wrapper for Profile repository  and for reservation catalog repository.
 *
 * @author Andrew_Logvinov
 */
public class Profile extends RepositoryItemWrapper {
    /**
     * Creates a new Profile object.
     *
     * @param repositoryItem parameter
     */
    public Profile(RepositoryItem repositoryItem) {
        super(repositoryItem);
    }

    /**
     * Returns instance property.
     *
     * @param  repository     parameter to set.
     * @param  profileId      parameter to set.
     * @param  descriptorName descriptor name in repository
     *
     * @return instance property.
     *
     * @throws RepositoryException - exception
     */
    public static Profile getInstance(Repository repository, String profileId, String descriptorName)
                               throws RepositoryException {
        if (null == profileId) {
            return null;
        }
        RepositoryItem item = repository.getItem(profileId, descriptorName);

        return getInstance(item);
    }

    /**
     * Returns instance property.
     *
     * @param  repositoryItem parameter to set.
     *
     * @return instance property.
     */
    public static Profile getInstance(RepositoryItem repositoryItem) {
        return (null != repositoryItem) ? new Profile(repositoryItem) : null;
    }

    /**
     * Returns prefix property.
     *
     * @return prefix property.
     */
    public String getPrefix() {
    	String prefix = null;
    	try {
			if (repositoryItem.getItemDescriptor().getPropertyDescriptor(Constants.TITLE_PROFILE_PROP) != null) {
				prefix = (String) repositoryItem.getPropertyValue(Constants.TITLE_PROFILE_PROP);
			} else if (repositoryItem.getItemDescriptor().getPropertyDescriptor(Constants.TITLE_RESERVATION_FIELD) != null) {
				prefix = (String) repositoryItem.getPropertyValue(Constants.TITLE_RESERVATION_FIELD);
			}
		} catch (RepositoryException e) {
			prefix = null;
		}
        return prefix;
    }

    /**
     * Sets the value of the prefix property.
     *
     * @param  prefix parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setPrefix(String prefix) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns firstName property.
     *
     * @return firstName property.
     */
    public String getFirstName() {
    	String firstName = null;
    	try {
			if (repositoryItem.getItemDescriptor().getPropertyDescriptor(Constants.FIRST_NAME_PROFILE_PROP) != null) {
				firstName = (String) repositoryItem.getPropertyValue(Constants.FIRST_NAME_PROFILE_PROP);
			} else if (repositoryItem.getItemDescriptor().getPropertyDescriptor(Constants.FIRST_NAME_RESERVATION_FIELD) != null) {
				firstName = (String) repositoryItem.getPropertyValue(Constants.FIRST_NAME_RESERVATION_FIELD);
			}
		} catch (RepositoryException e) {
			firstName = null;
		}
		return firstName;
    }

    /**
     * Sets the value of the firstName property.
     *
     * @param  firstName parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setFirstName(String firstName) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns lastName property.
     *
     * @return lastName property.
     */
    public String getLastName() {
    	String lastName = null;
    	try {
			if (repositoryItem.getItemDescriptor().getPropertyDescriptor(Constants.LAST_NAME_PROFILE_PROP) != null) {
				lastName = (String) repositoryItem.getPropertyValue(Constants.LAST_NAME_PROFILE_PROP);
			} else if (repositoryItem.getItemDescriptor().getPropertyDescriptor(Constants.LAST_NAME_RESERVATION_FIELD) != null) {
				lastName = (String) repositoryItem.getPropertyValue(Constants.LAST_NAME_RESERVATION_FIELD);
			}
		} catch (RepositoryException e) {
			lastName = null;
		}
		return lastName;
    }

    /**
     * Sets the value of the lastName property.
     *
     * @param  lastName parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setLastName(String lastName) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }
}
