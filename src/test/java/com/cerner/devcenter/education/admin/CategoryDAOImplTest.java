package com.cerner.devcenter.education.admin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.cerner.devcenter.education.admin.CategoryDAOImpl.CategoriesByIdsResultExtractor;
import com.cerner.devcenter.education.admin.CategoryDAOImpl.CategoryRowMapper;
import com.cerner.devcenter.education.models.Category;

/**
 * Tests the {@link CategoryDAOImpl} class
 *
 * @author Piyush Bandil ("PB042879")
 * @author Nikhil Agrawal (NA044293)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vatsal Kesarwani (VK049896)
 * @author Mani Teja Kurapati (MK051340)
 */
public class CategoryDAOImplTest {

    private static final String GET_MAX_ID_QUERY = "SELECT MAX(id) FROM category";
    private static final String DELETE_CATEGORY_QUERY = "DELETE FROM category WHERE id=?";
    private static final String INSERT_CATEGORY_QUERY = "INSERT INTO category (name, description, difficulty_level) VALUES(?,?,?)";
    private static final String QUERY_UPDATE_CATEGORY = "UPDATE category SET name = ?, description = ? WHERE id = ?";

    private static final int VALID_CATEGORY_ID = 5;
    private static final String VALID_CATEGORY_NAME = "testingname";
    private static final String VALID_CATEGORY_DESCRIPTION = "testingdescription";
    private static final int VALID_DIFFICULTY_LEVEL = 2;

    private static final int NEGATIVE_CATEGORY_ID = -1;
    private static final int ZERO_CATEGORY_ID = 0;
    private static final String EMPTY_STRING = "";
    private static final String ERROR_GETTING_BY_NAME = "Error while getting the category by name";
    private static final String DB_EXPECTED_MSG_UPDATE_CATEGORY = "Error while updating the category";

    private static final String EXPECTED_MSG_NULL_CATEGORY = "Category cannot be null";
    private static final String EXPECTED_MSG_ZERO_CATEGORY_ID = "Category id cannot be zero";
    private static final String EXPECTED_MSG_NULL_CATEGORY_NAME = "Category name cannot be empty";
    private static final String EXPECTED_MSG_NULL_CATEGORY_DESCRIPTION = "Category description cannot be empty";
    private static final String EXPECTED_MSG_INVALID_DIFFICULTY_LEVEL = "difficultyLevel must be on a scale of 1-5";

    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_DESCRIPTION = "description";
    private static final String CATEGORY_NAME = "name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private CategoryDAOImpl categoryDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private Category mockCategory;
    @Mock
    private MapSqlParameterSource mockParameter;
    @Mock
    private ResultSet resultSet;
    private Category category;
    private CategoryDAOImpl.CategoryRowMapper categoryRowMapper;
    private Category newCategory;
    private List<Category> categories;
    private List<Integer> categoryIds;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        categories = new ArrayList<Category>();
        categories.add(category);
        categoryIds = new ArrayList<>();
        categoryRowMapper = categoryDAOImpl.new CategoryRowMapper();
        categoryDAOImpl.new CategoriesByIdsResultExtractor();
        when(jdbcTemplate.queryForObject(GET_MAX_ID_QUERY, Integer.class)).thenReturn(VALID_CATEGORY_ID);
        when(jdbcTemplate.update(INSERT_CATEGORY_QUERY, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION,
                VALID_DIFFICULTY_LEVEL)).thenReturn(1);
        when(resultSet.getInt(CATEGORY_ID)).thenReturn(VALID_CATEGORY_ID);
        when(resultSet.getString(CATEGORY_DESCRIPTION)).thenReturn(VALID_CATEGORY_DESCRIPTION);
        when(resultSet.getString(CATEGORY_NAME)).thenReturn(VALID_CATEGORY_NAME);
    }

    /**
     * This function tests {@link CategoryDAOImpl#getById(int)} functionality
     * and expects {@link IllegalArgumentException} when id is negative
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryByIDWhenIdIsNegative() throws DAOException {
        categoryDAOImpl.getById(NEGATIVE_CATEGORY_ID);
    }

    /**
     * This function tests {@link CategoryDAOImpl#getById(int)} functionality
     * and expects {@link IllegalArgumentException} when id is zero
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryByIDWhenIdIsZero() throws DAOException {
        categoryDAOImpl.getById(ZERO_CATEGORY_ID);
    }

    /**
     * This function verifies {@link CategoryRowMapper#mapRow(ResultSet, int)}
     * functionality
     *
     * @throws SQLException
     */
    @Test
    public void testMapRowValidResultSet() throws SQLException {
        newCategory = categoryRowMapper.mapRow(resultSet, 1);
        assertEquals(VALID_CATEGORY_ID, newCategory.getId());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newCategory.getDescription());
        assertEquals(VALID_CATEGORY_NAME, newCategory.getName());
    }

    /**
     * This function tests {@link CategoryDAOImpl#getById(int)} functionality
     * and expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws
     * {@link DataAccessException} .
     *
     * @throws DAOException
     */

    @Test(expected = DAOException.class)
    public void testGetCategoryByIDWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(CategoryRowMapper.class), anyInt()))
                .thenThrow(dataAccessException);
        categoryDAOImpl.getById(VALID_CATEGORY_ID);
    }

    /**
     * This function verifies {@link CategoryDAOImpl#getById(int)} functionality
     *
     * @throws DAOException
     */
    @Test
    public void testGetCategoryByIDValid() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(CategoryRowMapper.class), anyInt())).thenReturn(category);
        newCategory = categoryDAOImpl.getById(VALID_CATEGORY_ID);
        assertEquals(VALID_CATEGORY_NAME, newCategory.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newCategory.getDescription());
        assertEquals(VALID_CATEGORY_ID, newCategory.getId());
    }

    /**
     * This function tests {@link CategoryDAOImpl#getByName(String)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws
     * {@link DataAccessException} .
     *
     * @throws DAOException
     */
    @Test
    public void testGetCategoryByNameWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(ERROR_GETTING_BY_NAME);
        when(jdbcTemplate.queryForObject(anyString(), any(CategoryRowMapper.class), anyInt()))
                .thenThrow(dataAccessException);
        categoryDAOImpl.getByName(VALID_CATEGORY_NAME);
    }

    /**
     * This function verifies {@link CategoryDAOImpl#getByName(String)}
     * functionality success
     *
     * @throws DAOException
     */
    @Test
    public void testGetCategoryByNameValid() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(CategoryRowMapper.class), anyInt())).thenReturn(category);
        newCategory = categoryDAOImpl.getByName(VALID_CATEGORY_NAME);
        assertEquals(VALID_CATEGORY_NAME, newCategory.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newCategory.getDescription());
        assertEquals(VALID_CATEGORY_ID, newCategory.getId());
    }

    /**
     * This function verifies {@link CategoryDAOImpl#getByName(String)}
     * functionality with empty name
     *
     * @throws DAOException
     */
    @Test
    public void testGetCategoryByNameEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(EXPECTED_MSG_NULL_CATEGORY_NAME);
        newCategory = categoryDAOImpl.getByName(EMPTY_STRING);
    }

    /**
     * This function tests {@link CategoryDAOImpl#addCategory(Category)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class)} throws
     * {@link DataAccessException}.
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testAddCategoryWhenJdbcTemplateOfPrivateMethodThrowsDataAccessException() throws DAOException {
        category.setDifficultyLevel(VALID_DIFFICULTY_LEVEL);
        when(jdbcTemplate.queryForObject(GET_MAX_ID_QUERY, Integer.class)).thenThrow(dataAccessException);
        categoryDAOImpl.addCategory(category);
    }

    /**
     * This function tests {@link CategoryDAOImpl#addCategory(Category)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testAddCategoryWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        category.setDifficultyLevel(VALID_DIFFICULTY_LEVEL);
        when(jdbcTemplate.update(INSERT_CATEGORY_QUERY, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION,
                VALID_DIFFICULTY_LEVEL))
                .thenThrow(dataAccessException);
        categoryDAOImpl.addCategory(category);
    }

    /**
     * This function verifies {@link CategoryDAOImpl#addCategory(Category)}
     * functionality
     *
     * @throws DAOException
     */
    @Test
    public void testAddCategoryValid() throws DAOException {
        category.setDifficultyLevel(VALID_DIFFICULTY_LEVEL);
        newCategory = categoryDAOImpl.addCategory(category);
        assertEquals(VALID_CATEGORY_NAME, newCategory.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newCategory.getDescription());
        assertEquals(VALID_CATEGORY_ID, newCategory.getId());
    }

    /**
     * Tests{@link CategoryDAOImpl#addCategory(Category)} when category is null
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddCategoryWhenCategoryIsNull() throws DAOException {
        try {
            categoryDAOImpl.addCategory(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_NULL_CATEGORY, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests{@link CategoryDAOImpl#addCategory(Category)} when difficulty level
     * is 0
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddCategoryWhenDifficultyLevelIsZero() throws DAOException {
        try {
            categoryDAOImpl.addCategory(category);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_INVALID_DIFFICULTY_LEVEL, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests{@link CategoryDAOImpl#addCategory(Category)} when category name is
     * not valid
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddCategoryWhenCategoryNameInvalid() throws DAOException {
        when(mockCategory.getName()).thenReturn(EMPTY_STRING);
        try {
            categoryDAOImpl.addCategory(mockCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_NULL_CATEGORY_NAME, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests{@link CategoryDAOImpl#addCategory(Category)} when category
     * description is not valid
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddCategoryWhenCategoryDescriptionInvalid() throws DAOException {
        when(mockCategory.getName()).thenReturn(VALID_CATEGORY_NAME);
        when(mockCategory.getDescription()).thenReturn(EMPTY_STRING);
        try {
            categoryDAOImpl.addCategory(mockCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_NULL_CATEGORY_DESCRIPTION, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link CategoryDAOImpl#updateCategory(Category)} to throw
     * {@link IllegalArgumentException} when Category is <code>null</code>.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCategory_NullCategory() throws DAOException {
        try {
            categoryDAOImpl.updateCategory(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_NULL_CATEGORY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link CategoryDAOImpl#updateCategory(Category)} to throw
     * {@link IllegalArgumentException} when id is zero.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCategory_ZeroID() throws DAOException {
        when(mockCategory.getId()).thenReturn(ZERO_CATEGORY_ID);
        try {
            categoryDAOImpl.updateCategory(mockCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_ZERO_CATEGORY_ID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link CategoryDAOImpl#updateCategory(Category)} to throw
     * {@link IllegalArgumentException} when name is <code>null</code>.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCategory_NullName() throws DAOException {
        when(mockCategory.getId()).thenReturn(VALID_CATEGORY_ID);
        when(mockCategory.getName()).thenReturn(null);
        try {
            categoryDAOImpl.updateCategory(mockCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_NULL_CATEGORY_NAME, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link CategoryDAOImpl#updateCategory(Category)} to throw
     * {@link IllegalArgumentException} when description is <code>null</code>.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCategory_NullCategoryDescription() throws DAOException {
        when(mockCategory.getId()).thenReturn(VALID_CATEGORY_ID);
        when(mockCategory.getName()).thenReturn(VALID_CATEGORY_NAME);
        when(mockCategory.getDescription()).thenReturn(null);
        try {
            categoryDAOImpl.updateCategory(mockCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(EXPECTED_MSG_NULL_CATEGORY_DESCRIPTION, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link CategoryDAOImpl#updateCategory(Category)} to throw
     * {@link DAOException} when {@link JdbcTemplate#update(String, Object...)}
     * throws {@link DataAccessException}.
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testUpdateCategory_JdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.update(QUERY_UPDATE_CATEGORY, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION,
                VALID_CATEGORY_ID)).thenThrow(dataAccessException);
        try {
            categoryDAOImpl.updateCategory(category);
        } catch (final DAOException e) {
            assertEquals(DB_EXPECTED_MSG_UPDATE_CATEGORY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link CategoryDAOImpl#updateCategory(Category)} to run
     * successfully when no exception is thrown.
     *
     * @throws DAOException
     */
    @Test
    public void testUpdateCategory() throws DAOException {
        when(jdbcTemplate.update(QUERY_UPDATE_CATEGORY, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION,
                VALID_CATEGORY_ID)).thenReturn(1);
        categoryDAOImpl.updateCategory(category);
        verify(jdbcTemplate, times(1)).update(QUERY_UPDATE_CATEGORY, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION,
                VALID_CATEGORY_ID);
    }

    /**
     * This function tests {@link CategoryDAOImpl#deleteCategory(int)}
     * functionality and expects {@link IllegalArgumentException} when id is
     * negative.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)

    public void testDeleteCategoryWhenIDIsNegative() throws DAOException {
        categoryDAOImpl.deleteCategory(NEGATIVE_CATEGORY_ID);
    }

    /**
     * This function tests {@link CategoryDAOImpl#deleteCategory(int)}
     * functionality and expects {@link IllegalArgumentException} when id is
     * zero.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteCategoryWhenIdIsZero() throws DAOException {
        categoryDAOImpl.deleteCategory(ZERO_CATEGORY_ID);
    }

    /**
     * This function tests {@link CategoryDAOImpl#deleteCategory(int)}
     * functionality and expect {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException} .
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testDeleteCategoryWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.update(DELETE_CATEGORY_QUERY, VALID_CATEGORY_ID)).thenThrow(dataAccessException);
        categoryDAOImpl.deleteCategory(VALID_CATEGORY_ID);
    }

    /**
     * This function verifies {@link CategoryDAOImpl#deleteCategory(int)}
     * functionality
     *
     * @throws DAOException
     */
    @Test
    public void testDeleteCategory() throws DAOException {
        categoryDAOImpl.deleteCategory(VALID_CATEGORY_ID);
        verify(jdbcTemplate).update(DELETE_CATEGORY_QUERY, VALID_CATEGORY_ID);
    }

    /**
     * This function tests {@link CategoryDAOImpl#getAllCategoryList()}
     * functionality and expect {@link DAOException} when
     * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper)}
     * throws {@link DataAccessException} .
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testgetAllCategoryListWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(CategoryRowMapper.class))).thenThrow(dataAccessException);
        categoryDAOImpl.getAllCategoryList();
    }

    /**
     * This function verifies {@link CategoryDAOImpl#getAllCategoryList()}
     * functionality
     *
     * @throws DAOException
     */
    @Test
    public void testGetAllCategoryList() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(CategoryRowMapper.class))).thenReturn(categories);
        newCategory = categoryDAOImpl.getAllCategoryList().get(0);
        assertEquals(VALID_CATEGORY_ID, newCategory.getId());
        assertEquals(VALID_CATEGORY_NAME, newCategory.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newCategory.getDescription());
    }

    /**
     * This function tests
     * {@link CategoryDAOImpl#getCategoryListByIds(List categoryIds)}
     * functionality and expect {@link DAOException} when
     * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.ResultSetExtractor)}
     * throws {@link DataAccessException} .
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testGetCategoryListByIdsWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(namedParameterJdbcTemplate.query(anyString(), any(MapSqlParameterSource.class),
                any(CategoriesByIdsResultExtractor.class))).thenThrow(dataAccessException);
        categoryDAOImpl.getCategoryListByIds(categoryIds);
    }

    /**
     * This function verifies
     * {@link CategoryDAOImpl#getCategoryListByIds(List categoryIds)}
     * functionality
     *
     * @throws DAOException
     */
    @Test
    public void testGetCategoryListByIds() throws DAOException {
        when(namedParameterJdbcTemplate.query(anyString(), any(MapSqlParameterSource.class),
                any(CategoriesByIdsResultExtractor.class))).thenReturn(categories);
        final List<Category> actualCategories = categoryDAOImpl.getCategoryListByIds(categoryIds);
        newCategory = actualCategories.get(0);
        assertEquals(1, actualCategories.size());
        assertEquals(VALID_CATEGORY_ID, newCategory.getId());
        assertEquals(VALID_CATEGORY_NAME, newCategory.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newCategory.getDescription());
    }
}