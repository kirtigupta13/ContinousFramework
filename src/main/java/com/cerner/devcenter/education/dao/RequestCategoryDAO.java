package com.cerner.devcenter.education.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.RequestCategory;
import com.cerner.devcenter.education.models.UserRequestRelation;

/**
 * Performs database operations for RequestCategory and UserRequestRelation.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
@Repository("requestCategoryDAO")
public interface RequestCategoryDAO {

    /**
     * Adds the given {@link RequestCategory} and {@link UserRequestRelation}.
     *
     * @param requestCategory
     *            a requested category which will be added. Cannot be
     *            <code>null</code>
     * @param userId
     *            a user requesting the category which will be added. Cannot be
     *            <code>null</code>/empty/blank
     * @throws DAOException
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
    void addRequestCategory(final RequestCategory requestCategory, final String userId) throws DAOException;

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
     * @throws DAOException
     *             when there is an error while trying to delete the requested
     *             category with the given requestCategoryId and userId
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li><code>requestCategoryId</code> is zero/negative</li>
     *             <li><code>userId</code> is <code>null</code>/empty/blank</li>
     *             </ul>
     */
    void deleteRequestCategory(final int requestCategoryId, final String userId) throws DAOException;

    /**
     * Retrieves a list of all {@link RequestCategory requested categories}.
     *
     * @return a list of all requested categories. Cannot be <code>null</code>,
     *         might be empty
     * @throws DAOException
     *             when there is an error while trying to get the requested
     *             categories list
     */
    List<RequestCategory> getAllRequestCategories() throws DAOException;

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
     * @throws DAOException
     *             when there is an error while trying to get the requested
     *             categories list
     */
    List<RequestCategory> getAllRequestCategories(final boolean isApproved) throws DAOException;
}