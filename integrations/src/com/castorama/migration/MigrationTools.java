package com.castorama.migration;

import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;

/**
 * Tools class for use with migration process.
 * 
 * @author Vasili_Ivus
 *
 */
public class MigrationTools extends GenericService {

	private final static int CHUNK_SIZE = 10000;
	
	private final static String PROFILE_PROPERTY_NAME_HASHED = "passwordHashed";

	/** FILTER_NOT_HASHED constant. */
	public static final String FILTER_NOT_HASHED = PROFILE_PROPERTY_NAME_HASHED + " = ?0";

	/** FILTER_NOT_HASHED_RANGED constant. */
    public static final String FILTER_NOT_HASHED_RANGED = FILTER_NOT_HASHED + " RANGE ?1+?2";

	/**
	 * Profile tools
	 */
	private ProfileTools mProfileTools;
	
	private boolean isClimb;

	/**
	 * Non-argument constructor.
	 */
	public MigrationTools() {}
	
	public void stopHashPasswords() {
		isClimb = false;
	}
	
	
	/**
	 * Apply hashing about all passwords. 
	 * Needs when all users after import have open passwords.
	 */
	public void hashPasswords() {
		int index = 0;
		try {
			PropertyManager propertyManager = getPropertyManager();
			ProfileTools profileTools =  getProfileTools();
			int count = getProfileItemsCount(profileTools);
			if ( 0 < count) {
				if ( isLoggingInfo() ) {
					logInfo("Profiles needs to hash: " + count);
				}
				int start = 0;
				int end = CHUNK_SIZE;
				RepositoryItem[] profiles = null;
				isClimb = true;
				do {
					if ( isLoggingDebug() ) {
						logDebug("Try to hash user passwords: " + index + " - " + (index + end));
					}
					profiles = getProfileItems(profileTools, start, end);
					if ( null != profiles ) {
						for (int i = 0; i < profiles.length; i++) {
							if ( isClimb ) {
								String login = profileTools.getLogin(profiles[i]);
								String password = profileTools.getPassword(profiles[i]);
								String newPassword = propertyManager.generatePassword(login, password);
								MutableRepositoryItem profile = profileTools.getMutableItem(profiles[i]);
								profile.setPropertyValue(propertyManager.getPasswordPropertyName(), newPassword);
								profile.setPropertyValue(PROFILE_PROPERTY_NAME_HASHED, Boolean.TRUE);
								((MutableRepository) getProfileRepository()).updateItem(profile);
								index++;
							} else {
								break;
							}
						}
					}
					if ( isLoggingDebug() ) {
						if ( isClimb ) {
							logDebug("OK.");
						} else {
							logDebug("Canceled.");
						}
					}
				} while( isClimb && null != profiles && CHUNK_SIZE == profiles.length );
			} else {
				if ( isLoggingInfo() ) {
					logInfo("Nothing to hash.");
				}
			}
		} catch (Exception e) {
			if ( isLoggingError() ) {
				logError(e);
			}
		}
		if ( isLoggingInfo() ) {
			logInfo("Hash finished: " + index);
		}
	}

	/**
	 * Returns the array of repository items of profiles with not hashed passwords.
	 * @param profileTools the profile tools. 
	 * @param start the start inedx.
	 * @param end the end index.
	 * @return the array of repository items of profiles with not hashed passwords. 
	 */
	private RepositoryItem[] getProfileItems(ProfileTools profileTools, Integer start, Integer end) {
		RepositoryItem[] result = null;
		try {
            RqlStatement findNotHashed = RqlStatement.parseRqlStatement(FILTER_NOT_HASHED_RANGED);
            RepositoryView usersView = getProfileRepository().getView(profileTools.getDefaultProfileType());
            result = findNotHashed.executeQuery(usersView, new Object[] {Boolean.FALSE, start, end});
		} catch (Exception e) {
			if ( isLoggingError() ) {
				logError(e);
			}
		}
		return result; 
	}

	/**
	 * Returns count of repository items of profiles with not hashed passwords.
	 * @param profileTools the profile tools. 
	 * @return count of repository items of profiles with not hashed passwords. 
	 */
	private int getProfileItemsCount(ProfileTools profileTools) {
		int result = 0;
		try {
            RqlStatement findNotHashed = RqlStatement.parseRqlStatement(FILTER_NOT_HASHED);
            RepositoryView usersView = getProfileRepository().getView(profileTools.getDefaultProfileType());
            result = findNotHashed.executeCountQuery(usersView, new Object[] {Boolean.FALSE});
		} catch (Exception e) {
			if ( isLoggingError() ) {
				logError(e);
			}
		}
		return result; 
	}
	
	/**
	 * Returns profile property manager
	 * @return profile property manager
	 */
	private PropertyManager getPropertyManager() {
		return ( null == mProfileTools ) ? null : mProfileTools.getPropertyManager();   
	}
	
	/**
	 * Rerurms user repository.
	 * @return the userRepository.
	 */
	public Repository getProfileRepository() {
		return ( null == mProfileTools ) ? null : mProfileTools.getProfileRepository();
	}
	
	/**
	 * Returns profile tools.
	 * @return Profile tools.
	 */
	public ProfileTools getProfileTools() {
	  return mProfileTools;
	}

	/**
	 * Sets profile tools.
	 * @param pProfileTools the profile tools 
	 */
	public void setProfileTools(ProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

}
