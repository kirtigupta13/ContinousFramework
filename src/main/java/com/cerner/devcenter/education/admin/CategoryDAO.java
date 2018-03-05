package com.cerner.devcenter.education.admin;

import java.util.List;

import com.cerner.devcenter.education.models.Category;

/**
 * This interface is responsible for performing database operations for
 * {@link Category} objects.
 *
 * @author Nathaniel Owen (NO032013)
 * @author Piyush Bandil (PB042879)
 * @author Chaitali Kharangate (CK042502)
 * @author Nikhil Agrawal (NA044293)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vatsal Kesarwani (VK049896)
 * @author Mani Teja Kurapati (MK051340)
 * @author Santosh Kumar (SK051343)
 */
public interface CategoryDAO {

    /**
     * Returns a {@link Category} from a database that has the same id as the
     * passed in value.
     *
     * @param id
     *            an Integer that represents the primary key of the category we
     *            wish to retrieve from the database
     * @return Category that was in the database that has the same primary key
     *         as the passed in id, if no Category with this key can be found,
     *         <code>null</code> is returned
     * @throws IllegalArgumentException
     *             when id is a non-negative value
     * @throws DAOException
     *             when there is an error while trying to get category by id
     */
    Category getById(int id) throws DAOException;

    /**
     * Returns a {@link Category} from a database that has the same name as the
     * passed in value.
     *
     * @param name
     *            a String that represents the unique name of the category we
     *            wish to retrieve from the database. Cannot be
     *            <code>null</code> or empty
     * @return Category that was in the database that has the same name as the
     *         passed in name
     * @throws DAOException
     *             when there is an error while trying to get category by name
     *             from the database
     * @throws IllegalArgumentException
     *             when the name is <code>null</code> or empty
     *
     */
    Category getByName(String name) throws DAOException;

    /**
     * Returns a {@link Category} from a database that has the same name and
     * description as the passed in value.
     *
     * @param category
     *            a Category object which we will add to the database. Following
     *            attribute of Category object cannot be <code>null</code>: name
     *            and description
     * @return Category by adding value to id field of input Category object
     * @throws DAOException
     *             when there is an error while trying to add category to
     *             database
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when category name is null/blank/empty</li>
     *             <li>when category description is null/blank/empty</li>
     *             <li>when difficulty level is not in range 1-5</li>
     *             </ul>
     */
    Category addCategory(Category category) throws DAOException;

    /**
     * Updates the given {@link Category}.
     *
     * @param category
     *            which will be added. Cannot be <code>null</code>.
     * @throws DAOException
     *             when there is an error while trying to update category
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>category is <code>null</code></li>
     *             <li>category.getId returns zero</li>
     *             <li>category.getName returns <code>null</code></li>
     *             <li>category.getDescription returns <code>null</code></li>
     *             </ul>
     */
    void updateCategory(Category category) throws DAOException;

    /**
     * Delete a {@link Category} from the database corresponding to passed
     * category_id.
     *
     * @param id
     *            a positive Integer is passed to the method to delete
     *            corresponding Category from database
     * @throws DAOException
     *             when there is an error while trying to delete category from
     *             database
     * @throws IllegalArgumentException
     *             when id is passed as a non-positive
     */
    void deleteCategory(int id) throws DAOException;

    /**
     * Returns a list of all categories from database.
     *
     * @return a List of all {@link Category} from database
     * @throws DAOException
     *             when there is an error while trying to get the category list
     *             from database
     */
    List<Category> getAllCategoryList() throws DAOException;

    /**
     * Returns a list of categories corresponding to passed category Id's from
     * database.
     *
     * @param categoryIdsList
     *            array of category id's for which {@link Category} objects are
     *            needed
     *
     * @return a List of {@link Category} from database
     * @throws DAOException
     *             when there is an error while trying to get the category list
     *             from database
     */
    List<Category> getCategoryListByIds(List<Integer> categoryIdsList) throws DAOException;
    

    /**
     * Performs a query on data source that will return a {@link List} of
     * {@link Category} objects that are not added in user interested
     * categories.
     * 
     * @param search
     *            {@link String} input given by user in order to search(cannot
     *            be empty, blank or null).
     * @param userId
     *            {@link String} input given by user in order to search(cannot
     *            be empty, blank or null).
     * @return {@link List} of unAdded {@link Category} which are not available
     *         in the list of user interested categories. Can be empty, but not
     *         null.
     * @throws DAOException
     *             when there is an error while trying to get selected
     *             categories from data source.
     */
    List<Category> nonchosenCategories(String search, String userId) throws DAOException;

    /**
     * Performs a query on data source that will return a {@link List} of
     * {@link Category} objects that are currently available in user interested
     * categories.
     * 
     * @param search
     *            {@link String} input given by user in order to search(cannot
     *            be empty, blank or null).
     * @param userId
     *            {@link String} input given by user in order to search(cannot
     *            be empty, blank or null).
     * @return {@link List} of matched {@link Category} which are already
     *         available in user interested category list. Can be empty, but not
     *         null.
     * @throws DAOException
     *             when there is an error while trying to get selected
     *             categories from data source.
     */
    List<Category> chosenCategories(String search, String userId) throws DAOException;

    /**
     * Performs query to check if provided category name is present in the
     * database.
     *
     * @param categoryName
     *            a {@link String} that represents the category name to search.
     * @return true if the category name is already present, false if category
     *         name is not found.
     * @throws IllegalArgumentException
     *             When the categoryName parameter is empty, blank or null.
     * @throws DAOException
     *             when there is an error while trying to get category info with
     *             the given category name
     */
    boolean isCategoryAlreadyPresent(String categoryName) throws DAOException;
    
}