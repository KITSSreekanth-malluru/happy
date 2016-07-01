package com.castorama.stock.mchannel.cache;

/**
 * Stock Visualization.
 *
 * @author  EPAM team
 */
public interface Constants {
  
  /** STATUS_PARAM property */
  String STATUS = "STATUS";

  /** STOCK_PARAM property */
  int STATUS_AVAILABLE = 1;

  /** STATUS_LIMITED_AVAILABILITY property */
  int STATUS_LIMITED_AVAILABILITY = 2;

  /** STATUS_PRODUCT_NOT_FOUND property */
  int STATUS_PRODUCT_NOT_FOUND = 11;

  /** STATUS_STORE_OR_POSTAL_CODE_NOT_FOUND property */
  int STATUS_STORE_OR_POSTAL_CODE_NOT_FOUND = 12;

  /** STATUS_NOT_AVAILABLE property */
  int STATUS_NOT_AVAILABLE = 15;
  
  /** STATUS_OTHER_CASES property */
  int STATUS_OTHER_CASES = 17;
  
  /** STATUS_WRONG_REQUEST_PARAMETERS property */
  int STATUS_WRONG_REQUEST_PARAMETERS = 18;

  /** STATUS_INTERNAL_ERROR property */
  int STATUS_INTERNAL_ERROR = 98;
  
  /** STATUS_REDUCE_SUCCESS property */
  int STATUS_REDUCE_SUCCESS = 101;

  /** POSTAL_CODES_NODE_NAME property */
  String POSTAL_CODES_NODE_NAME = "PostalCodes";

  /** STORE_AREAS_NODE_NAME property */
  String STORE_AREAS_NODE_NAME = "StoreAreas";
}
