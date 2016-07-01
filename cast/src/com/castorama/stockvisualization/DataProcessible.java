/*<ATGCOPYRIGHT>
 * Copyright (C) 2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package com.castorama.stockvisualization;

import java.io.Serializable;
import java.util.List;

/**
 * Provides ability to process data obtained from web 
 * service in sub-type defined manner.
 *
 * @author Aliaksandr Belakurski
 */
public interface DataProcessible
{
  /**
   * Parses data embedded in provided string and fills 
   * list of {@link StockModel} entities which encapsulate 
   * relevant stock properties
   * 
   * @param pResponseData
   * @return
   * @throws StockVisualizationException
   */
  List<StockModel> processData (String pResponseData) 
  		  throws StockVisualizationException;
}
