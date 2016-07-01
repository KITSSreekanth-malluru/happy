package com.castorama.integration.bazaarvoice;

class RatingReview {
	
	private String externalId;
	
	private double avgRating;
	
	private int totalReviewCount;

	public void setExternalId(String externalId) {
		this.externalId = externalId;		
	}

	public String getExternalId() {
		return externalId;
	}

	public void setAvgRating(double avgRating) {
		this.avgRating = avgRating;
	}

	public void setTotalReviewCount(int totalReviewCount) {
		this.totalReviewCount = totalReviewCount;
	}

	public double getAvgRating() {
		return avgRating;
	}

	public int getTotalReviewCount() {
		return totalReviewCount;
	}

}
