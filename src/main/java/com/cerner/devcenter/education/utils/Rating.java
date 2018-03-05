/**
 * 
 */
package com.cerner.devcenter.education.utils;

/**
 * Tracks a resource rating by a user
 * 
 * @author Asim Mohammed (AM045300)
 *
 */
public enum Rating {
	DISLIKE(0), LIKE(1);

	private int rating;

	Rating(int ratingValue) {
		rating = ratingValue;
	}

	public int getRatingValue() {
		return rating;
	}
}
