package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.CategoryDAO;
import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.models.Category;

/**
 *
 * This class manages the addition, deletion and retrieval of {@link Category}
 * objects.
 *
 * @author Naga Rishyendar Panguluri(NP046332)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Asim Mohammed(AM045300)
 * @author Wuchen Wang (WW044343)
 * @author Jacob Zimmermann (JZ022690)
 * @author Santosh Kumar (SK051343)
 */
@Service("categoryManager")
public class CategoryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryManager.class);

    private static final String CATEGORY_NULL = "Category cannot be null.";
    private static final String INVALID_CATEGORY_ID = "Category ID cannot be negative or 0.";
    private static final String INVALID_CATEGORY_NAME = "Category name cannot be null, empty, or blank.";
    private static final String INVALID_CATEGORY_DESCRIPTION = "Category description cannot be null/blank/empty";
    private static final String INVALID_DIFFICULTY_LEVEL = "difficultyLevel must be on a scale of 1-5";
    private static final String INVALID_SEARCH = "Search cannot be null, empty, or blank.";

    private static final String ERROR_ADDING_CATEGORY = "Error encountered while adding category";
    private static final String ERROR_RETRIEVING_CATEGORY_BY_ID = "Error retrieving category by its id.";
    private static final String ERROR_RETRIEVING_ALL_CATEGORIES = "Error retrieving all categories from the data source";
    private static final String ERROR_DELETING_CATEGORY = "Error deleting category from the data source using its id";
    private static final String ERROR_RETRIEVING_SEARCH_CATEGORIES = "Error retrieving searched categories from the database";
    private static final String ERROR_RETRIEVING_CATEGORY_BY_NAME = "Error retrieving category with the provided category name";

    private static final int MIN_DIFFICULTY_LEVEL = 1;
    private static final int MAX_DIFFICULTY_LEVEL = 5;

    @Autowired
    private CategoryDAO categoryDAO;

    /**
     * Add new category
     *
     * @param category
     *            a {@link Category} object to be added. Can't be null
     * @return {@link Category} if category is successfully added otherwise
     *         null.
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when category name is null/blank/empty</li>
     *             <li>when category description is null/blank/empty</li>
     *             <li>when difficulty level is not in scale 1-5</li>
     *             </ul>
     */
    public Category addCategory(Category category) {
        checkArgument(category != null, CATEGORY_NULL);
        checkArgument(StringUtils.isNotBlank(category.getName()), INVALID_CATEGORY_NAME);
        checkArgument(StringUtils.isNotBlank(category.getDescription()), INVALID_CATEGORY_DESCRIPTION);
        Range<Integer> desiredDifficultyLevelRange = Range.between(MIN_DIFFICULTY_LEVEL, MAX_DIFFICULTY_LEVEL);
        checkArgument(desiredDifficultyLevelRange.contains(category.getDifficultyLevel()), INVALID_DIFFICULTY_LEVEL);
        try {
            return categoryDAO.addCategory(category);
        } catch (DAOException dAOException) {
            LOGGER.error(ERROR_ADDING_CATEGORY, dAOException);
            throw new ManagerException(ERROR_ADDING_CATEGORY, dAOException);
        }
    }

    /**
     * Retrieves a {@link Category} with provided id
     *
     * @param categoryId
     *            represents the id of the category to be retrieved. Must be a
     *            positive integer.
     * @return {@link Category} for the provided id, if no category with this
     *         key can be found, null is returned
     * @throws IllegalArgumentException
     *             when id is a non-positive value
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     */
    public Category getCategoryById(int categoryId) {
        checkArgument(categoryId > 0, INVALID_CATEGORY_ID);
        try {
            return categoryDAO.getById(categoryId);
        } catch (DAOException dAOException) {
            throw new ManagerException(ERROR_RETRIEVING_CATEGORY_BY_ID, dAOException);
        }
    }

    /**
     * This method retrieves a list of all categories.
     *
     * @return a {@link List} of all {@link Category}
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     */
    public List<Category> getAllCategories() {
        try {
            return categoryDAO.getAllCategoryList();
        } catch (DAOException daoException) {
            LOGGER.error("Error retrieving all categories from the data source", daoException);
            throw new ManagerException(ERROR_RETRIEVING_ALL_CATEGORIES, daoException);
        }
    }

    /**
     * Delete a {@link Category} corresponding to passed categoryId.
     *
     * @param categoryId
     *            represents the id of the category to be retrieved. Must be a
     *            positive integer.
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     * @throws IllegalArgumentException
     *             when id is passed as a non-positive value
     */
    public void deleteCategoryById(int categoryId) {
        checkArgument(categoryId > 0, INVALID_CATEGORY_ID);
        try {
            categoryDAO.deleteCategory(categoryId);
        } catch (DAOException daoException) {
            throw new ManagerException(ERROR_DELETING_CATEGORY, daoException);
        }
    }

    /**
     * This method returns a {@link List} of nonchosen {@link Category}.
     *
     * @param search
     *            input {@link String} given by user (cannot be null, empty or
     *            blank.
     * @param userId
     *            input {@link String}
     * @return {@link List} of unAdded {@link Category} which are not available
     *         in the list of user interested categories. Can be empty, but not
     *         null.
     * @throws ManagerException
     *             When there is an error retrieving searched resources from the
     *             database.
     * @throws IllegalArgumentException
     *             when invalid search keyword is entered.
     */
    public List<Category> nonchosenCategories(String search, String userId) {
        checkArgument(StringUtils.isNotBlank(search), INVALID_SEARCH);
        try {
            return categoryDAO.nonchosenCategories(search, userId);
        } catch (DAOException daoException) {
            LOGGER.error(ERROR_RETRIEVING_SEARCH_CATEGORIES, daoException);
            throw new ManagerException(ERROR_RETRIEVING_SEARCH_CATEGORIES, daoException);
        }
    }

    /**
     * This method returns a {@link List} of chosen {@link Category}.
     *
     * @param search
     *            input {@link String} given by user (cannot be null, empty or
     *            blank.
     * @param userId
     *            input {@link String}
     * @return {@link List} of matched {@link Category} which are already
     *         available in user interested category list. Can be empty, but not
     *         null.
     * @throws ManagerException
     *             When there is an error retrieving searched resources from the
     *             database.
     * @throws IllegalArgumentException
     *             when invalid search keyword is entered.
     */
    public List<Category> chosenCategories(String search, String userId) {
        checkArgument(StringUtils.isNotBlank(search), INVALID_SEARCH);
        try {
            return categoryDAO.chosenCategories(search, userId);
        } catch (DAOException daoException) {
            LOGGER.error(ERROR_RETRIEVING_SEARCH_CATEGORIES, daoException);
            throw new ManagerException(ERROR_RETRIEVING_SEARCH_CATEGORIES, daoException);
        }
    }

    /**
     * Checks if the provided category name is already present.
     *
     * @param categoryName
     *            represents the category name that is being checked.
     * @return true if category name is found, else false.
     * @throws IllegalArgumentException
     *             when category name is empty, blank or null.
     * @throws ManagerException
     *             when an error occurs while retrieving category info.
     */
    public boolean isCategoryAlreadyPresent(String categoryName) {
        checkArgument(StringUtils.isNotBlank(categoryName), INVALID_CATEGORY_NAME);
        try {
            return categoryDAO.isCategoryAlreadyPresent(categoryName);
        } catch (DAOException daoException) {
            throw new ManagerException(ERROR_RETRIEVING_CATEGORY_BY_NAME, daoException);
        }
    }
}