package com.cerner.devcenter.education.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.ResourceRequest;

/**
 * Performs database operations for ResourceRequests.
 *
 * @author Navya Rangeneni (NR046827)
 * @author Vatsal Kesarwani (VK049896)
 */
@Repository("resourceRequestDAO")
public interface ResourceRequestDAO {

    /**
     * Adds the given {@link ResourceRequest}.
     *
     * @param resourceRequest
     *            a resource request which will be added. Cannot be null
     * @throws DAOException
     *             when there is an error while trying to add resource request
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>resourceRequest is null</li>
     *             <li>resourceRequest.getUserId returns null</li>
     *             <li>resourceRequest.getCategoryName returns null</li>
     *             <li>resourceRequest.getResourceName returns null</li>
     *             </ul>
     */
    void addResourceRequest(final ResourceRequest resourceRequest) throws DAOException;

    /**
     * Retrieves a list of all {@link ResourceRequest}.
     *
     * @return a list of all resource requests. Cannot be null, might be empty
     * @throws DAOException
     *             when there is an error while trying to get the resource
     *             requests list
     */
    List<ResourceRequest> getAllResourceRequests() throws DAOException;

    /**
     * Retrieves a list of all {@link ResourceRequest resource requests} for the
     * given userId.
     *
     * @param userId
     *            a user identifier of the current user. Cannot be null
     * @return a list of all resource requests of the user. Cannot be null,
     *         might be empty
     * @throws DAOException
     *             when there is an error while trying to get the resource
     *             requests list
     * @throws IllegalArgumentException
     *             when userId is null/empty/blank
     */
    List<ResourceRequest> getAllResourceRequests(final String userId) throws DAOException;

    /**
     * Retrieves a list of approved {@link ResourceRequest resource requests} if
     * isApproved is <code>true</code> otherwise returns unapproved resource
     * requests.
     *
     * @param isApproved
     *            <code>True</code> if this request is approved,
     *            <code>False</code> otherwise
     * @return a list of all resource requests for the isApproved value. Cannot
     *         be null, might be empty
     * @throws DAOException
     *             when there is an error while trying to get the resource
     *             requests list
     */
    List<ResourceRequest> getAllResourceRequests(final boolean isApproved) throws DAOException;

    /**
     * Deletes the {@link ResourceRequest resource requests} with the given
     * id's.
     *
     * @param ids
     *            array of resource request identifiers. Cannot be null/empty
     * @throws DAOException
     *             when there is an error while trying to delete the resource
     *             requests
     * @throws IllegalArgumentException
     *             when ids is null/empty
     */
    void deleteResourceRequests(final int[] ids) throws DAOException;
}