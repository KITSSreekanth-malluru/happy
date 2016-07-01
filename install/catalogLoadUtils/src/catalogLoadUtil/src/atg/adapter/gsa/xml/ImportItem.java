package atg.adapter.gsa.xml;

import java.util.HashMap;

public class ImportItem
{
  public static final int M_ACTION_ADD = 0;
  public static final int M_ACTION_UPDATE = 1;
  public static final int M_ACTION_DELETE = 2;
  
  private int mAction;
  
  public int getAction ()
  {
    return (mAction);  
  }
  
  public void setAction (int pAction)
  {
    mAction = pAction;
  }
  
  private String mItemDescriptor = null;
  
  public String getItemDescriptor ()
  {
    return (mItemDescriptor);  
  }
  
  public void setItemDescriptor (String pItemDescriptor)
  {
    mItemDescriptor = pItemDescriptor;
  }
  
  private String mItemId = null;

  public String getItemId ()
  {
    return (mItemId);  
  }
  
  public void setItemId (String pItemId)
  {
    mItemId = pItemId;
  }

  private String mRepositoryName = null;

  public String getRepositoryName ()
  {
    return (mRepositoryName);  
  }
  
  public void setRepositoryName (String pRepositoryName)
  {
    mRepositoryName = pRepositoryName;
  }

  private HashMap<String, String> mProperties;

  public HashMap<String, String> getProperties ()
  {
    return (mProperties);  
  }
  
  public void setProperties (HashMap<String, String> pProperties)
  {
    mProperties = pProperties;
  }

  public ImportItem ()
  {
  }
}
