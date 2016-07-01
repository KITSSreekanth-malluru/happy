package com.castorama.integration.bazaarvoice;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import atg.repository.MutableRepository;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

class FeedLoader {
	
	private final Repository repository;

	private static final Set<String> supportedProductTypes = new HashSet<String>();
	
	static {
		supportedProductTypes.add("product");
		supportedProductTypes.add("casto_product");
//		is not supported		
//		supportedTypes.add("casto-pack");
		supportedProductTypes.add("casto-grouped-product");
	}
	
//	private static final Set<String> pivotCategories = new HashSet<String>();
	
	
	private class CategoryProductIterator implements Iterator<CategoryProductFeed> {
		
		private final RepositoryItem[] skuList;
		
		private int current = -1;

		public CategoryProductIterator(RepositoryItem[] skuList) {
			if (null == skuList) {
				this.skuList = new RepositoryItem[]{};
			} else {
				this.skuList = skuList;
			}
		}

		public boolean hasNext() {
			return (current + 1) < skuList.length;
		}

		public CategoryProductFeed next() {
			RepositoryItem el = skuList[++current];
			
			CategoryProductFeed result = 
				new CategoryProductFeed(
						el,
						(String)el.getPropertyValue("id"), 
						(String)el.getPropertyValue("displayName"));
			
			RepositoryItem template = (RepositoryItem) el.getPropertyValue("template");
			if (null != template) {
				result.setCategoryPageUrl((String) template.getPropertyValue("url"));
			} else {
				result.setCategoryPageUrl("");
			}
			
//			pivotCategories.add(result.getExternalId());
			
			return result ;
		}

		public void remove() {
			// 			
		}
		
	}
	
	
	private class ProductIterator implements Iterator<ProductFeed> {

		private final RepositoryItem[] skuList;
		
		private int current = -1;
		
		public ProductIterator(RepositoryItem[] skuList) {
			if (null == skuList) {
				this.skuList = new RepositoryItem[]{};
			} else {
				this.skuList = skuList;
			}
		}

		public boolean hasNext() {
			return (current + 1) < skuList.length;
		}

		@SuppressWarnings("unchecked")
		public ProductFeed next() {
			RepositoryItem el = skuList[++current];
			
			Collection categories = null;
			String categoryId = null;
			try {
				categories = (Collection) el.getPropertyValue("parentCategories");
				if (null != categories) {
					for (Object cat : categories) {
						String cid = getPivotCategoryId((RepositoryItem) cat);
						
						if (null != cid && !"".equals(cid)) {
							categoryId = cid;
							break;
						}
					}
				}
			} catch (Throwable e) {
				e.printStackTrace(System.err);
			}
			
			if (categoryId == null) {
				return null;
			}
			String type = (String) el.getPropertyValue("type");
			
			if (null == type || !supportedProductTypes.contains(type)) {
				return null;
			}
			
			ProductFeed result = 
				new ProductFeed(
						el,
						(String)el.getPropertyValue("id"), 
						(String)el.getPropertyValue("displayName"),
						(String)el.getPropertyValue("description"),
						(String)categoryId);

			RepositoryItem template = (RepositoryItem) el.getPropertyValue("template");
			if (null != template) {
				result.setProductPageUrl((String)template.getPropertyValue("url"));
			}

			RepositoryItem img = (RepositoryItem) el.getPropertyValue("largeImage");
			
			if (null == img) {
				img = (RepositoryItem) el.getPropertyValue("smallImage");
				
				if (null == img) {
					img = (RepositoryItem) el.getPropertyValue("thumbnailImage");
				}			
			}
			
			if (null != img) {
				result.setImageUrl((String) img.getPropertyValue("url"));
			};
			
			try {
				result.setBrand((String) el.getPropertyValue("MarqueCommerciale"));
			} catch (Throwable e) {
				// do nothing
			}
			
			return result ;
		}

		public void remove() {
			// removing is not supported
		}

	}
	
	private class DocumentIterator implements Iterator<ProductFeed> {

		private int current = -1;

		private final RepositoryItem[] docList;
		
		public DocumentIterator(RepositoryItem[] docList) {
			if (null == docList) {
				this.docList = new RepositoryItem[]{};
			} else {
				this.docList = docList;
			}
		}
		
		public boolean hasNext() {
			return (current + 1) < docList.length;
		}

		@SuppressWarnings("unchecked")
		public ProductFeed next() {
			RepositoryItem el = docList[++current];
			
			Collection categories = null;
			String categoryId = null;
			try {
				categories = (Collection) el.getPropertyValue("categories");
				if (null != categories){
					for (Object cat : categories) {
						String cid = getPivotCategoryId((RepositoryItem) cat);
						
						if (null != cid && !"".equals(cid)) {
							categoryId = cid;
							break;
						}
					}
				}
			} catch (Throwable e) {
				e.printStackTrace(System.err);
			}
			
			if (categoryId == null) {
				return null;
			}
			
			ProductFeed result = 
				new ProductFeed(
						el,
						(String)el.getPropertyValue("id"), 
						(String)el.getPropertyValue("title"),
						(String)el.getPropertyValue("description"),
						(String)categoryId);
			result.setDocument(true);

			result.setProductPageUrl((String) el.getPropertyValue("relativeURL"));

			RepositoryItem img = (RepositoryItem) el.getPropertyValue("image");
			if (null != img) {
				result.setImageUrl((String) img.getPropertyValue("url"));
			}
			
			return result ;
		}

		public void remove() {
			// removement is not supported
		}
	}
	
	private final Map<String, String> pivotCategoryId = new HashMap<String, String>();
	
	public FeedLoader(Repository repository) {
		this.repository = repository;
	}

	public Iterator<CategoryProductFeed> categoryIterator() throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
		RepositoryView view = repo.getView("casto_category");
		QueryBuilder qb = view.getQueryBuilder();
		
				
		RepositoryItem[] items = view.executeQuery(
				qb.createComparisonQuery(
				qb.createConstantQueryExpression(Boolean.TRUE), 
				qb.createPropertyQueryExpression("pivot"),
				QueryBuilder.EQUALS));
        
        return new CategoryProductIterator(items);
	}

	public MutableRepository getRepository() {
		return (MutableRepository) repository;
	}

	public Iterator<ProductFeed> productIterator() throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
		RepositoryView view = repo.getView("product");
		RepositoryItem[] items = view.executeQuery(view.getQueryBuilder().createUnconstrainedQuery());
        
        return new ProductIterator(items);
	}

	public Iterator<ProductFeed> documentIterator() throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
		RepositoryView view = repo.getView("castoramaDocument");
		RepositoryItem[] items = view.executeQuery(view.getQueryBuilder().createUnconstrainedQuery());
        
        return new DocumentIterator(items);
	}

	public int countProducts() throws RepositoryException {
		MutableRepository mutableRepos = (MutableRepository) getRepository();
		RepositoryView view = mutableRepos.getView("product");
		return view.executeCountQuery(view.getQueryBuilder().createUnconstrainedQuery());
	}

	public int countDocuments() throws RepositoryException {
		MutableRepository mutableRepos = (MutableRepository) getRepository();
		RepositoryView view = mutableRepos.getView("castoramaDocument");
		return view.executeCountQuery(view.getQueryBuilder().createUnconstrainedQuery());
	}

	public Iterator<ProductFeed> productIterator(int startPosition, int portion) throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
		RepositoryView view = repo.getView("product");

		String rqlQuery = "ALL RANGE ?0+";

		Object[] rqlParams;
		RqlStatement rqlStatement;
		if (portion > 0) {
			rqlParams = new Object[2];
			rqlParams[0] = startPosition;
			rqlParams[1] = portion;
			rqlStatement = RqlStatement.parseRqlStatement(rqlQuery + "?1");
		} else {
			rqlParams = new Object[1];
			rqlParams[0] = startPosition;
			rqlStatement = RqlStatement.parseRqlStatement(rqlQuery);
		}

		RepositoryItem[] items = rqlStatement.executeQuery(view, rqlParams);
        return new ProductIterator(items);
	}

	public Iterator<ProductFeed> documentIterator(int startPosition, int portion) throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
		RepositoryView view = repo.getView("castoramaDocument");
		String rqlQuery = "ALL RANGE ?0+";

		Object[] rqlParams;
		RqlStatement rqlStatement;
		if (portion > 0) {
			rqlParams = new Object[2];
			rqlParams[0] = startPosition;
			rqlParams[1] = portion;
			rqlStatement = RqlStatement.parseRqlStatement(rqlQuery + "?1");
		} else {
			rqlParams = new Object[1];
			rqlParams[0] = startPosition;
			rqlStatement = RqlStatement.parseRqlStatement(rqlQuery);
		}

		RepositoryItem[] items = rqlStatement.executeQueryUncached(view, rqlParams);
        
        return new DocumentIterator(items);
	}

	private String getPivotCategoryId(RepositoryItem category) {
		if (null == category) {
			return null;
		}
		
		/*
		String id = (String)category.getPropertyValue("id");
		
		if (null != id) {
			if (pivotCategories.contains(id)) {
				return id;
			}
		}
		
		return null;		
        */
		
		Boolean pivot = (Boolean) category.getPropertyValue("pivot");
        if (pivot) {
			return (String)category.getPropertyValue("id");
        }
        
        String id = (String) category.getPropertyValue("id");
        String result = pivotCategoryId.get(id);
        
        if (null != result){
        	return result;
        } else {
        	result = getPivotCategoryId((RepositoryItem) category.getPropertyValue("parentCategory"));
        	
        	if (null == result) {
	        	pivotCategoryId.put(id, "");
        	} else {
	        	pivotCategoryId.put(id, result);
        	}
        	
        	return result;
        }
	}
	
}
