package com.cerner.devcenter.education.models;

import java.util.Map;
import java.util.List;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserRating;

/**
 * This interface is responsible for obtaining education {@link Resource} links
 * and descriptions.
 */
public interface ResourceExtractor {
	/**
	 * This method will extract the description and link for the
	 * {@link Resource}'s
	 * 
	 * @param userCategoryRatings
	 *            Collection of {@link UserRating}
	 * @param maxCategories
	 *            Number of categories for which we need to extract resources.
	 *            If value is 0, then all categories should be considered.
	 * @return Map that contains {@link Category} ids as the key and another Map
	 *         as the value, which contains the description and link for a
	 *         {@link Resource}
	 * @throws DAOException when there is an error while trying to get resources
	 * @throws IllegalArgumentException
	 *             when value of maxCategories is negative or userCategoryRating
	 *             is null
	 */
	public Map<Integer, List<Resource>> getResources(List<UserRating> userCategoryRatings, int maxCategories) throws DAOException;
}
