package com.cerner.devcenter.education.admin;

import static com.cerner.devcenter.education.utils.QueryExpanderUtil.expandPlaceholders;
import static com.cerner.devcenter.education.utils.TagNameVerifier.verifyTagNameArgument;
import static com.cerner.devcenter.education.utils.TagNameVerifier.verifyTagNameCollectionArgument;
import static com.google.common.base.Preconditions.checkArgument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Responsible for performing database operations for {@link Tag} objects on a
 * table named tag.
 *
 * @author Abhi Purella (AP045635)
 * @author Amos Bailey (AB032627)
 */
@Repository("tagDAO")
public class TagDAOImpl implements TagDAO {

    private static final String GET_ALL_TAGS_QUERY = "SELECT * FROM tag";
    private static final Logger LOGGER = LoggerFactory.getLogger(TagDAOImpl.class);
    private static final TagRowMapper rowMapper = new TagRowMapper();
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";
    private static final String GET_SEARCHED_TAGS_QUERY = "SELECT tag_id, tag_name FROM tag where lower(tag_name) LIKE ? ORDER BY tag_name";
    private static final String GET_TAG_BY_ID_QUERY = "Select tag_id, tag_name FROM tag WHERE tag_id = ?";
    private static final String INSERT_TAG_QUERY = "INSERT INTO tag(tag_name) VALUES(?)";
    private static final String GET_TAGS_SAME_NAME_QUERY = "SELECT tag_id, tag_name FROM tag WHERE lower(tag_name) = ?";
    private static final String GET_TAGS_NAME_IN_LIST_QUERY = "SELECT tag_id, tag_name FROM tag WHERE lower(tag_name) in (%s)";
    private static final String GET_TAG_BY_NAME_RETURNED_DUPLICATES = "More than one tag was returned when querying for a tag with the name %s.";
    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Tag> getAllTags() throws DAOException {
        try {
            return jdbcTemplate.<Tag>query(GET_ALL_TAGS_QUERY, rowMapper);
        } catch (EmptyResultDataAccessException emptyResultEx) {
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link Tag} object.
     */
    static class TagRowMapper implements RowMapper<Tag> {
        /**
         * @throws SQLException
         *             when SQL Exception is encountered using column values
         */
        @Override
        public Tag mapRow(ResultSet row, int rowNum) throws SQLException {
            int tagId = row.getInt("tag_id");
            String tagName = row.getString("tag_name");
            return new Tag(tagId, tagName);
        }
    }

    @Override
    public List<Tag> getSearchedTags(String search) throws DAOException {
        checkArgument(StringUtils.isNotBlank(search), i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        try {
            return jdbcTemplate.<Tag>query(GET_SEARCHED_TAGS_QUERY, rowMapper, "%" + search.toLowerCase() + "%");
        } catch (EmptyResultDataAccessException emptyResultEx) {
            LOGGER.error(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
        }
    }

    @Override
    public Tag getTagByID(final int id) throws DAOException {
        checkArgument(id > 0, Constants.TAG_ID_MUST_BE_POSITIVE);
        try {
            return jdbcTemplate.queryForObject(GET_TAG_BY_ID_QUERY, rowMapper, id);
        } catch (DataAccessException daoException) {
            throw new DAOException(String.format(Constants.ERROR_GETTING_TAG_BY_ID, id), daoException);
        }
    }

    @Override
    public void addTagToDB(final String tagName) throws DAOException {
        verifyTagNameArgument(tagName);
        try {
            jdbcTemplate.update(INSERT_TAG_QUERY, new Object[] { tagName.trim() });
        } catch (DataAccessException dataAccessException) {
            throw new DAOException(Constants.ERROR_ADDING_TAG_TO_DB, dataAccessException);
        }
    }

    @Override
    public void batchAddTags(final Collection<String> tagNames) throws DAOException {
        verifyTagNameCollectionArgument(tagNames);

        List<Object[]> queryArgs = new ArrayList<Object[]>();
        for (String tagName : tagNames) {
            queryArgs.add(new Object[] { tagName.trim() });
        }

        try {
            jdbcTemplate.batchUpdate(INSERT_TAG_QUERY, queryArgs);
        } catch (DataAccessException dataAccessException) {
            throw new DAOException(Constants.ERROR_ADDING_TAG_TO_DB, dataAccessException);
        }
    }

    @Override
    public Tag getTagByName(final String tagName) throws DAOException {
        verifyTagNameArgument(tagName);

        List<Tag> returnedTags;
        try {
            returnedTags = jdbcTemplate.<Tag>query(GET_TAGS_SAME_NAME_QUERY, rowMapper, tagName.toLowerCase().trim());
        } catch (DataAccessException dataAccessException) {
            throw new DAOException(Constants.ERROR_FINDING_TAGS_BY_NAME, dataAccessException);
        }

        if (returnedTags.isEmpty()) {
            return null;
        } else {
            if (returnedTags.size() > 1) {
                LOGGER.debug(String.format(GET_TAG_BY_NAME_RETURNED_DUPLICATES, tagName));
            }
            return returnedTags.get(0);
        }
    }

    @Override
    public List<Tag> getTagsWithNameInCollection(final Collection<String> tagNames) throws DAOException {
        verifyTagNameCollectionArgument(tagNames);

        List<Object> searchParameters = new ArrayList<Object>();
        for (String tagName : tagNames) {
            searchParameters.add(tagName.toLowerCase().trim());
        }
        try {
            return jdbcTemplate.<Tag>query(
                    String.format(GET_TAGS_NAME_IN_LIST_QUERY, expandPlaceholders(searchParameters.size())), rowMapper,
                    searchParameters.toArray());
        } catch (DataAccessException dataAccessException) {
            throw new DAOException(Constants.ERROR_FINDING_TAGS_BY_NAME, dataAccessException);
        }
    }
}
