package com.castorama.integration.ticket;

public class TicketMappingObject {

  private String profileId;
  private String homeId;
  private String sofincoAccount;
  private boolean active;
  private String sofincoName1;
  private String sofincoName2;

  

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("TICKET MAPPING[");
    result.append("profileId=").append(profileId);
    result.append(",homeId=").append(homeId);
    result.append(",sofincoAccount=").append(sofincoAccount);
    result.append(",active=").append(active);
    result.append(",sofincoName1=").append(sofincoName1);
    result.append(",sofincoName2=").append(sofincoName2);
    result.append("]");
    return result.toString();
  }

  /**
   * @return the profileId
   */
  public String getProfileId() {
    return profileId;
  }

  /**
   * @param profileId the profileId to set
   */
  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  /**
   * @return the homeId
   */
  public String getHomeId() {
    return homeId;
  }

  /**
   * @param homeId the homeId to set
   */
  public void setHomeId(String homeId) {
    this.homeId = homeId;
  }

  /**
   * @return the sofincoAccount
   */
  public String getSofincoAccount() {
    return sofincoAccount;
  }

  /**
   * @param sofincoAccount the sofincoAccount to set
   */
  public void setSofincoAccount(String sofincoAccount) {
    this.sofincoAccount = sofincoAccount;
  }

  /**
   * @return the active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * @param active the active to set
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * @return the sofincoName1
   */
  public String getSofincoName1() {
    return sofincoName1;
  }

  /**
   * @param sofincoName1 the sofincoName1 to set
   */
  public void setSofincoName1(String sofincoName1) {
    this.sofincoName1 = sofincoName1;
  }

  /**
   * @return the sofincoName2
   */
  public String getSofincoName2() {
    return sofincoName2;
  }

  /**
   * @param sofincoName2 the sofincoName2 to set
   */
  public void setSofincoName2(String sofincoName2) {
    this.sofincoName2 = sofincoName2;
  }

}
