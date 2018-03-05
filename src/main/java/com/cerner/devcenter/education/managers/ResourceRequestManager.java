package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.ResourceRequestDAO;
import com.cerner.devcenter.education.models.ResourceRequest;

/**
 * This class manages the insertion of {@link ResourceRequest} objects.
 *
 * @author Navya Rangeneni (NR046827)
 * @author Vatsal Kesarwani (VK049896)
 */
@Service("resourceRequestManager")
public class ResourceRequestManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceRequestManager.class);
    private static final String INVALID_REQUEST_IDS = "Request IDs array can't be null or empty.";
    private static final String ERROR_DELETING = "Error: unable to delete the resource request.";
    private static final String USER_ID_NULL_ERROR = "UserId cannot be null";
    private static final String RESOURCE_REQUEST_DB_READ_ERROR_LOGGER_MESSAGE = "Error retrieving requested resources from the database";
    private static final String RESOURCE_REQUEST_DB_READ_ERROR = "Error retrieving all resource requests from the database";

    @Autowired
    private ResourceRequestDAO resourceRequestDAO;

    /**
     * Add new resource request
     *
     * @param resourceRequest
     *            a {@link ResourceRequest} object to be added. Can't be null
     *
     * @throws IllegalArgumentException
     *             when {@link ResourceRequest} object is null
     * @throws ManagerException
     *             when an error occurs while adding the object
     */
    public void addResourceRequest(final ResourceRequest resourceRequest) throws ManagerException {
        checkArgument(resourceRequest != null, "Can't add a null ResourceRequest");
        try {
            resourceRequestDAO.addResourceRequest(resourceRequest);
        } catch (final DAOException dAOException) {
            LOGGER.error("Error encountered while adding resource request1", dAOException);
            throw new ManagerException("Error encountered while adding resource request ", dAOException);
        }
    }

    /**
     * Deletes {@link ResourceRequest} for a particular requestIds.
     *
     * @param requestIds
     *            Array that contains requestIds (can't be null or empty).
     * @throws IllegalArgumentException
     *             when Array of requestIds is empty or null
     * @throws ManagerException
     *             if there is an error deleting {@link ResourceRequest}.
     */
    public void deleteResourceRequestsInBatch(final int[] requestIds) throws ManagerException {
        checkArgument(ArrayUtils.isNotEmpty(requestIds), INVALID_REQUEST_IDS);
        try {
            resourceRequestDAO.deleteResourceRequests(requestIds);
        } catch (final DAOException daoException) {
            LOGGER.error(ERROR_DELETING, daoException);
            throw new ManagerException(ERROR_DELETING, daoException);
        }
    }

    /**
     * This method retrieves a list of all resource requests for the given user.
     *
     * @param userId
     *            userId of the current user.
     * @return a {@link List} of all {@link ResourceRequest}.
     * @throws ManagerException
     *             when a manager is unable to access data through
     *             {@link ResourceRequestDAO}.
     */
    public List<ResourceRequest> getAllResourceRequestsOfUser(final String userId) throws ManagerException {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_NULL_ERROR);
        try {
            return resourceRequestDAO.getAllResourceRequests(userId);
        } catch (final DAOException daoException) {
            LOGGER.error(RESOURCE_REQUEST_DB_READ_ERROR_LOGGER_MESSAGE, daoException);
            throw new ManagerException(RESOURCE_REQUEST_DB_READ_ERROR, daoException);
        }
    }
}