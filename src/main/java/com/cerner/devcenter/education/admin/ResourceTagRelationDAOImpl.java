package com.cerner.devcenter.education.admin;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Responsible for performing database operations for relationships between
 * {@link Tag} and {@link Resource}.
 *
 * @author Amos Bailey (AB032627)
 *
 */
@Repository("resourceTagRelationDAO")
public class ResourceTagRelationDAOImpl implements ResourceTagRelationDAO {
    @Autowired
    private TagDAO tagDAO;
    @Autowired
    private ResourceDAO resourceDAO;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static String INSERT_TAG_RESOURCE_QUERY = "INSERT INTO tag_resource_reltn(tag_id, resource_id) VALUES(?,?)";

    @Override
    public void addTagsToResource(final int resourceID, final List<Tag> tagList) throws DAOException {
        checkArgument(tagList != null, Constants.TAG_LIST_NULL);
        checkArgument(!tagList.isEmpty(), Constants.TAG_LIST_EMPTY);
        checkArgument(resourceID > 0, Constants.RESOURCE_ID_MUST_BE_POSITIVE);

        List<Object[]> queryArgs = new ArrayList<Object[]>();
        for (Tag tag : tagList) {
            checkArgument(tag != null, Constants.TAG_LIST_HAS_NULL_TAG);
            checkArgument(tag.getTagId() > 0, Constants.TAG_ID_MUST_BE_POSITIVE);
            checkArgument(tag.getTagName() != null, Constants.TAG_NAME_CANNOT_BE_NULL);
            checkArgument(StringUtils.isNotBlank(tag.getTagName()), Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
            queryArgs.add(new Object[] { tag.getTagId(), resourceID });
        }

        try {
            jdbcTemplate.batchUpdate(INSERT_TAG_RESOURCE_QUERY, queryArgs);
        } catch (DataAccessException dataAccessException) {
            throw new DAOException(Constants.ERROR_ADDING_TAG_RESOURCE_RELTN, dataAccessException);
        }
    }
}
