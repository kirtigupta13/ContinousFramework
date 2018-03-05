package com.cerner.devcenter.education.admin;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;

/**
 * Unit test cases for {@link CategoryResourceRelationDAOImpl} class.
 *
 * @author Piyush Bandil (PB042879)
 * @author James Kellerman (JK042311)
 * @author Laasya Kuppam (LK043600)
 * @author Santosh Kumar (SK051343)
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryResourceRelationDAOImplTest {

    @InjectMocks
    private CategoryResourceRelationDAOImpl categoryResourceDAOImpl;
    @Mock
    private ResourceDAO resourceDAO;
    @Mock
    private CategoryDAO categoryDAO;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessEx;
    private Resource resource;
    private Category category;
    private URL url;
    private final static Integer GET_ID_RESOURCE = 1;
    private final static Integer GET_ID_CATEGORY = 1;
    private final static Integer NEGATIVE_ID = -1;
    private final static Integer ZERO_ID = 0;
    private static final String INSERT_CATEGORY_RESOURCE_QUERY = "INSERT INTO category_resource_reltn (category_id, resource_id) VALUES(?,?)";
    private final static String DELETE_CATEGORY_RESOURCE_QUERY = "DELETE FROM category_resource_reltn WHERE resource_id=?";

    /**
     * Sets the up.
     * 
     * @throws DAOException
     */
    @Before
    public void setUp() throws MalformedURLException, DAOException {

        url = new URL("https://www.google.com");
        resource = new Resource(1, url, "This is a test resource");
        category = new Category(1, "Test", "This is a test category");
        when(jdbcTemplate.update(INSERT_CATEGORY_RESOURCE_QUERY, new Object[] { GET_ID_CATEGORY, GET_ID_RESOURCE }))
                .thenReturn(1);
        when(resourceDAO.getById(GET_ID_RESOURCE)).thenReturn(resource);
        when(categoryDAO.getById(GET_ID_CATEGORY)).thenReturn(category);
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        resourceDAO = null;
        categoryDAO = null;
        jdbcTemplate = null;
        dataAccessEx = null;
        resource = null;
        category = null;
    }

    /**
     * Test that {@link JdbcTemplate#update(String, Object...)} is called during
     * a call to
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}.
     * 
     * @throws DAOException
     */
    @Test
    public void testAddMappingResourceToDB() throws DAOException {
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
        verify(jdbcTemplate).update(INSERT_CATEGORY_RESOURCE_QUERY, new Object[] { GET_ID_CATEGORY, GET_ID_RESOURCE });
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link NullPointerException} when
     * {@link Resource} object is passed as null.
     * 
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testAddMappingResourceToDBResourceIsNull() throws DAOException {
        categoryResourceDAOImpl.addMappingsToDB(null, category);
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link NullPointerException} when
     * {@link Category} object is passed as null.
     * 
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testAddMappingResourceToDBCategoryIsNull() throws DAOException {
        categoryResourceDAOImpl.addMappingsToDB(resource, null);
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link IllegalArgumentException} when
     * resource_id returns negative from database.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddMappingResourceToDBResourceIdIsNegative() throws DAOException {
        resource = new Resource(NEGATIVE_ID, url, "This is a test resource");
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link IllegalArgumentException} when
     * resource_id returns zero from database.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddMappingReourceToDBResourceIdIsZero() throws DAOException {
        resource = new Resource(ZERO_ID, url, "This is a test resource");
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link NullPointerException} when
     * {@link ResourceDAO} getByID function return null instead of
     * {@link Resource}.
     * 
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testAddMappingResourceToDBResourceDAOGetIdIsNull() throws DAOException {
        when(resourceDAO.getById(GET_ID_RESOURCE)).thenReturn(null);
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link IllegalArgumentException} when
     * category_id returns zero from database.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddMappingResourceToDBCategoryIdIsZero() throws DAOException {
        category = new Category(ZERO_ID, "Test", "This is a test category");
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link IllegalArgumentException} when
     * category_id returns negative from database.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddMappingResourceToDBCategoryIdIsNegative() throws DAOException {
        category = new Category(NEGATIVE_ID, "Test", "This is a test category");
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
    }

    /**
     * This method test
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link NullPointerException} when
     * {@link CategoryDAO} getByID function return null instead of
     * {@link Category}.
     * 
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testAddMappingResourceToDBCategoryDAOGetIdIdIsNull() throws DAOException {
        when(categoryDAO.getById(GET_ID_CATEGORY)).thenReturn(null);
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
    }

    /**
     * This method checking
     * {@link CategoryResourceRelationDAOImpl#addMappingsToDB(Resource, Category)}
     * functionality and expecting {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link org.springframework.dao.DataAccessException}
     * 
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testAddMappingResourceToDBThrowsDAOException() throws DAOException {
        when(jdbcTemplate.update(INSERT_CATEGORY_RESOURCE_QUERY, GET_ID_CATEGORY, GET_ID_RESOURCE))
                .thenThrow(dataAccessEx);
        categoryResourceDAOImpl.addMappingsToDB(resource, category);
    }

    /**
     * Test that {@link JdbcTemplate#update(String, Object...)} is called during
     * a call to {@link CategoryResourceRelationDAOImpl#deleteById(int)}.
     * 
     * @throws DAOException
     */
    @Test
    public void testDeleteById() throws DAOException {
        categoryResourceDAOImpl.deleteById(1);
        verify(jdbcTemplate).update(DELETE_CATEGORY_RESOURCE_QUERY, GET_ID_RESOURCE);
    }

    /**
     * Test that an exception is thrown when
     * {@link CategoryResourceRelationDAOImpl#deleteById(int)} is called with an
     * invalid {@link Resource} id.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByIdInvalidResourceId() throws DAOException {
        categoryResourceDAOImpl.deleteById(NEGATIVE_ID);
    }

    /**
     * Test verifies that a {@link DAOException} is thrown when there is an
     * error in accessing the database.
     * 
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testDeleteByIdDAOException() throws DAOException {
        when(jdbcTemplate.update(DELETE_CATEGORY_RESOURCE_QUERY, GET_ID_RESOURCE)).thenThrow(dataAccessEx);
        categoryResourceDAOImpl.deleteById(GET_ID_RESOURCE);
    }
}
