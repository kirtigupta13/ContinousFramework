package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.RequestCategoryDAO;
import com.cerner.devcenter.education.models.RequestCategory;
import com.cerner.devcenter.education.models.UserRequestRelation;

/**
 * Manages the insertion, deletion and retrieval of RequestCategory and
 * UserRequestRelation.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
@Service("requestCategoryManager")
public class RequestCategoryManager {

    private static final String NULL_REQUEST_CATEGORY_ERROR_MESSAGE = "RequestCategory object is null.";
    private static final String INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE = "RequestCategory id is zero/negative.";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "UserId is null/empty/blank.";

    private static final String REQUEST_CATEGORY_DB_INSERT_ERROR_MESSAGE = "Error while adding the requested category to the database.";
    private static final String REQUEST_CATEGORY_DB_DELETE_ERROR_MESSAGE = "Error while deleting the requested category from the database.";
    private static final String REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE = "Error while retrieving the requested category from the database.";

    @Autowired
    private RequestCategoryDAO requestCategoryDAO;

    /**
     * Adds the given {@link RequestCategory} and associates it to the user with
     * the given id.
     *
     * @param requestCategory
     *            a requested category which will be added. Cannot be
     *            <code>null</code>
     * @param userId
     *            a user requesting the category which will be added. Cannot be
     *            <code>null</code>/empty/blank
     * @throws ManagerException
     *             when there is an error while trying to add the requested
     *             category or add the user requesting the category
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li><code>requestCategory</code> is <code>null</code></li>
     *             <li>{@link RequestCategory#getName()} returns
     *             <code>null</code></li>
     *             <li><code>userId</code> is <code>null</code>/empty/blank</li>
     *             </ul>
     */
    public void addRequestCategory(final RequestCategory requestCategory, final String userId) throws ManagerException {
        checkArgument(requestCategory != null, NULL_REQUEST_CATEGORY_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        try {
            requestCategoryDAO.addRequestCategory(requestCategory, userId);
        } catch (final DAOException daoException) {
            throw new ManagerException(REQUEST_CATEGORY_DB_INSERT_ERROR_MESSAGE, daoException);
        }
    }

    /**
     * Deletes the {@link UserRequestRelation} with the given requestCategoryId
     * and userId.
     *
     * @param requestCategoryId
     *            a requested category identifier to be deleted. Cannot be
     *            zero/negative
     * @param userId
     *            a user requesting the requested category to be deleted. Cannot
     *            be <code>null</code>/empty/blank
     * @throws ManagerException
     *             when there is an error while trying to delete the requested
     *             category with the given requestCategoryId and userId
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li><code>requestCategoryId</code> is zero/negative</li>
     *             <li><code>userId</code> is <code>null</code>/empty/blank</li>
     *             </ul>
     */
    public void deleteRequestCategory(final int requestCategoryId, final String userId) throws ManagerException {
        checkArgument(requestCategoryId > 0, INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        try {
            requestCategoryDAO.deleteRequestCategory(requestCategoryId, userId);
        } catch (final DAOException daoException) {
            throw new ManagerException(REQUEST_CATEGORY_DB_DELETE_ERROR_MESSAGE, daoException);
        }
    }

    /**
     * Retrieves a list of all {@link RequestCategory requested categories}.
     *
     * @return a list of all requested categories. Cannot be <code>null</code>,
     *         might be empty
     * @throws ManagerException
     *             when there is an error while trying to get the requested
     *             categories list
     */
    public List<RequestCategory> getAllRequestCategories() throws ManagerException {
        try {
            return requestCategoryDAO.getAllRequestCategories();
        } catch (final DAOException daoException) {
            throw new ManagerException(REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE, daoException);
        }
    }

    /**
     * Retrieves a list of all approved {@link RequestCategory requested
     * categories} if <code>isApproved</code> is <code>true</code> otherwise
     * retrieves unapproved requested categories.
     *
     * @param isApproved
     *            <code>True</code> to retrieve only approved requested
     *            categories, <code>False</code> to retrieve only unapproved
     *            requested categories
     * @return a list of all requested categories for the
     *         <code>isApproved</code> value. Cannot be <code>null</code>, might
     *         be empty
     * @throws ManagerException
     *             when there is an error while trying to get the requested
     *             categories list
     */
    public List<RequestCategory> getAllRequestCategories(final boolean isApproved) throws ManagerException {
        try {
            return requestCategoryDAO.getAllRequestCategories(isApproved);
        } catch (final DAOException daoException) {
            throw new ManagerException(REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE, daoException);
        }
    }
}