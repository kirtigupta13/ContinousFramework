package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.CategoryDAO;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.models.Category;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the functionalities of {@link CategoryManager}
 *
 * @author NP046332
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Asim Mohammed (AM045300)
 * @author Wuchen Wang (WW044343)
 * @author Jacob Zimmermann (JZ022690)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryManagerTest {

    @InjectMocks
    private CategoryManager categoryManager;
    @Mock
    private CategoryDAO categoryDAO;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final int VALID_CATEGORY_ID = 5;
    private static final String VALID_CATEGORY_NAME = "Software";
    private static final String VALID_CATEGORY_DESCRIPTION = "All that related to build software";
    private static final int NEGATIVE_CATEGORY_ID = -1;
    private static final String VALID_USER_ID = "TestId";
    private static final String BLANK_STRING = "         ";
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = null;
    private static final String EXCEPTION_MESSAGE = "test exception message";

    private static final String ERROR_ADDING_CATEGORY = "Error encountered while adding category";
    private static final String ERROR_RETRIEVING_CATEGORY_BY_NAME = "Error retrieving category with the provided category name";
    private static final String ERROR_RETRIEVING_CATEGORY_BY_ID = "Error retrieving category by its id";
    private static final String ERROR_RETRIEVING_ALL_CATEGORIES = "Error retrieving all categories from the data source";
    private static final String ERROR_RETRIEVING_SEARCH_CATEGORIES = "Error retrieving searched categories from the database";
    private static final String ERROR_DELETING_CATEGORY = "Error deleting category from the data source using its id";

    private static final String INVALID_CATEGORY_MESSAGE = "Category cannot be null.";
    private static final String INVALID_ID_MESSAGE = "Category ID cannot be negative or 0.";
    private static final String INVALID_SEARCH_MESSAGE = "Search cannot be null, empty, or blank.";
    private static final String INVALID_CATEGORY_NAME_MESSAGE = "Category name cannot be null, empty, or blank.";
    private static final String INVALID_CATEGORY_DESCRIPTION = "Category description cannot be null/blank/empty";
    private static final String INVALID_DIFFICULTY_LEVEL = "difficultyLevel must be on a scale of 1-5";

    private static final String EXPECTED_PRESENT = "Expected CategoryManager.isCategoryAlreadyPresent() to return true";

    private Category category;
    private List<Category> listCategory;

    @Before
    public void setUp() {
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        listCategory = new ArrayList<Category>();
        listCategory.add(category);
    }

    /**
     * Tests {@link CategoryManager#addCategory(Category)} returns a
     * {@link Category} when successful
     */
    @Test
    public void testAddCategory() throws DAOException {
        category.setDifficultyLevel(2);
        when(categoryDAO.addCategory(category)).thenReturn(category);
        Category newCategory = categoryManager.addCategory(category);
        assertEquals(category.getName(), newCategory.getName());
        assertEquals(category.getDescription(), newCategory.getDescription());
        assertEquals(category.getId(), newCategory.getId());
    }

    /***
     * Tests {@link CategoryManager#addCategory(Category)} functionality,
     * expects {@link ManagerException} when
     * {@link CategoryDAO#addCategoryToDB(Category)} throws {@link DAOException}
     */
    @Test
    public void testAddCategoryThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_ADDING_CATEGORY);
        category.setDifficultyLevel(2);
        when(categoryDAO.addCategory(category)).thenThrow(new DAOException(EXCEPTION_MESSAGE, null));
        categoryManager.addCategory(category);
    }

    /**
     * Tests {@link CategoryManager#addCategory(Category)} functionality,
     * expects {@link IllegalArgumentException} when {@link Category} passed in
     * is null.
     */
    @Test
    public void testAddCategoryWhenCategoryIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_CATEGORY_MESSAGE);
        categoryManager.addCategory(null);
    }

    /**
     * Tests {@link CategoryManager#addCategory(Category)} when difficulty level
     * is 0
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddCategoryWhenDifficultyLevelIsZero() {
        try {
            categoryManager.addCategory(category);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DIFFICULTY_LEVEL, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests {@link CategoryManager#getCategoryById(int)} functionality, expects
     * {@link IllegalArgumentException} when id is negative
     */
    @Test
    public void testGetCategoryByIdWhenIdIsNegative() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_ID_MESSAGE);
        categoryManager.getCategoryById(NEGATIVE_CATEGORY_ID);
    }

    /**
     * Tests {@link CategoryManager#getCategoryById(int)} functionality, expects
     * {@link IllegalArgumentException} when id is zero
     */
    @Test
    public void testGetCategoryByIdWhenIdIsZero() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_ID_MESSAGE);
        categoryManager.getCategoryById(0);
    }

    /**
     * Tests {@link CategoryManager#getCategoryById(int)} functionality, expects
     * {@link ManagerException} when {@link CategoryDAO#getById(int)} throws
     * {@link DAOException}
     */
    @Test
    public void testGetCategoryByIdThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_RETRIEVING_CATEGORY_BY_ID);
        doThrow(DAOException.class).when(categoryDAO).getById(VALID_CATEGORY_ID);
        categoryManager.getCategoryById(VALID_CATEGORY_ID);
    }

    /**
     * Tests {@link CategoryManager#getCategoryById(int)} returns a valid
     * {@link Category}.
     */
    @Test
    public void testGetCategoryById() throws DAOException {
        when(categoryDAO.getById(VALID_CATEGORY_ID)).thenReturn(category);
        Category actual = categoryManager.getCategoryById(VALID_CATEGORY_ID);
        assertEquals(VALID_CATEGORY_NAME, actual.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, actual.getDescription());
        assertEquals(VALID_CATEGORY_ID, actual.getId());
    }

    /**
     * Tests {@link CategoryManager#getAllCategories()} functionality, expects
     * {@link ManagerException} when {@link CategoryDAO#getAllCategoriesListFromDB()}
     * throws {@link DAOException}
     */
    @Test
    public void testGetAllCategoriesThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_RETRIEVING_ALL_CATEGORIES);
        when(categoryDAO.getAllCategoryList()).thenThrow(new DAOException());
        categoryManager.getAllCategories();
    }

    /**
     * Tests {@link CategoryManager#getAllCategories()} functionality, expects
     * {@link ManagerException} and verifies log message when
     * {@link CategoryDAO#getAllCategoriesListFromDB()} throws {@link DAOException}
     */
    @Test
    public void testGetAllCategoriesThrowsManagerExceptionVerifyLogMsg() throws DAOException {
        doThrow(new DAOException()).when(categoryDAO).getAllCategoryList();
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage("Error retrieving all categories from the data source");
        TestLogger logger = TestLoggerFactory.getTestLogger(CategoryManager.class);
        try {
            categoryManager.getAllCategories();
        } catch (ManagerException managerException) {
            List<LoggingEvent> listevents = logger.getLoggingEvents();
            assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
            assertEquals("Error retrieving all categories from the data source", listevents.get(0).getMessage());
            throw new ManagerException(managerException.getMessage(), new DAOException());
        }
    }

    /**
     * Tests {@link CategoryManager#getAllCategories()} returns a valid list of
     * {@link Category} when successful
     */
    @Test
    public void testGetAllCategories() throws DAOException {
        when(categoryDAO.getAllCategoryList()).thenReturn(listCategory);
        Category newCategory = categoryManager.getAllCategories().get(0);
        assertEquals(VALID_CATEGORY_NAME, newCategory.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newCategory.getDescription());
        assertEquals(VALID_CATEGORY_ID, newCategory.getId());
    }

    /**
     * Tests {@link CategoryManager#deleteCategoryById(int)} functionality,
     * expects {@link IllegalArgumentException} when id is negative
     */
    @Test
    public void testDeleteCategoryByIdWhenIdIsNegative() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_ID_MESSAGE);
        categoryManager.deleteCategoryById(NEGATIVE_CATEGORY_ID);
    }

    /**
     * Tests {@link CategoryManager#deleteCategoryById(int)} functionality,
     * expects {@link IllegalArgumentException} when id is zero
     */
    @Test
    public void testDeleteCategoryByIdWhenIdIsZero() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_ID_MESSAGE);
        categoryManager.deleteCategoryById(0);
    }

    /**
     * Tests {@link CategoryManager#deleteCategoryById(int)} functionality,
     * expects {@link ManagerException} when
     * {@link CategoryDAO#deleteCategoryFromDB(int)} throws {@link DAOException}
     */
    @Test
    public void testDeleteCategoryByIdThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_DELETING_CATEGORY);
        doThrow(DAOException.class).when(categoryDAO).deleteCategory(VALID_CATEGORY_ID);
        categoryManager.deleteCategoryById(VALID_CATEGORY_ID);
    }

    /**
     * Tests {@link CategoryManager#deleteCategoryById(int)} calls
     * {@link CategoryDAO#deleteCategoriesBasedOnCategoryId(int)}
     */
    @Test
    public void testDeleteCategory() throws DAOException {
        categoryManager.deleteCategoryById(VALID_CATEGORY_ID);
        verify(categoryDAO).deleteCategory(VALID_CATEGORY_ID);
    }

    /**
     * Tests {@link CategoryManager#nonchosenCategories(String, String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is empty given valid userId
     */
    @Test
    public void testGetNonChosenCategoriesForEmptyString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_SEARCH_MESSAGE);
        categoryManager.nonchosenCategories(EMPTY_STRING, VALID_USER_ID);
    }

    /**
     * Tests {@link CategoryManager#nonchosenCategories(String, String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is null given valid userId
     */
    @Test
    public void testGetNonChosenCategoriesForNullString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_SEARCH_MESSAGE);
        categoryManager.nonchosenCategories(NULL_STRING, VALID_USER_ID);
    }

    /**
     * Tests {@link CategoryManager#nonchosenCategories(String, String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is blank given valid userId
     */
    @Test
    public void testGetNonChosendCategoriesForBlankString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_SEARCH_MESSAGE);
        categoryManager.nonchosenCategories(BLANK_STRING, VALID_USER_ID);
    }

    /**
     * Tests {@link CategoryManager#nonchosenCategories(String, String)} and
     * checks {@link List} size is 0 when search returns empty results.
     */
    @Test
    public void testGetNonChosenCategoriesReturnsEmptyList() throws DAOException {
        when(categoryDAO.nonchosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID))
                .thenReturn(Collections.<Category>emptyList());
        assertEquals(0, categoryManager.nonchosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID).size());
    }

    /***
     * Tests {@link CategoryManager#nonchosenCategories(String, String)} throws
     * a {@link ManagerException} when
     * {@link CategoryDAO#nonchosenCategories(String, String)}} throws a
     * {@link DAOException}
     */
    @Test
    public void testGetNonChosenCategoriesThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_RETRIEVING_SEARCH_CATEGORIES);
        when(categoryDAO.nonchosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID)).thenThrow(new DAOException());
        categoryManager.nonchosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID);
    }

    /**
     * Tests {@link CategoryManager#chosenCategories(String, String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is empty given valid userId
     */
    @Test
    public void testGetChosenCategoriesForEmptyString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_SEARCH_MESSAGE);
        categoryManager.chosenCategories(EMPTY_STRING, VALID_USER_ID);
    }

    /**
     * Tests {@link CategoryManager#chosenCategories(String, String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is null given valid userId
     */
    @Test
    public void testGetChosenCategoriesForNullString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_SEARCH_MESSAGE);
        categoryManager.chosenCategories(NULL_STRING, VALID_USER_ID);
    }

    /**
     * Tests {@link CategoryManager#chosenCategories(String, String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is blank given valid userId
     */
    @Test
    public void testGetChosenCategoriesForBlankString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_SEARCH_MESSAGE);
        categoryManager.chosenCategories(BLANK_STRING, VALID_USER_ID);
    }

    /**
     * <<<<<<< HEAD Tests
     * {@link CategoryManager#chosenCategories(String, String)} and checks
     * {@link List} size is 0 when search returns empty results.
     */
    @Test
    public void testGetChosenCategoriesReturnsEmptyList() throws DAOException {
        when(categoryDAO.chosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID)).thenReturn(Collections.<Category>emptyList());
        assertEquals(0, categoryManager.chosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID).size());
    }

    /***
     * Tests {@link CategoryManager#chosenCategories(String, String)} throws a
     * {@link ManagerException} when
     * {@link CategoryDAO#chosenCategories(String, String)}} throws a
     * {@link DAOException}
     */
    @Test
    public void testGetChosenCategoriesThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_RETRIEVING_SEARCH_CATEGORIES);
        when(categoryDAO.chosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID)).thenThrow(new DAOException());
        categoryManager.chosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID);
    }

    /**
     * Tests {@link CategoryManager#isCategoryAlreadyPresent(String)} expects
     * {@link IllegalArgumentException} when category name is null
     */
    @Test
    public void testIsCategoryAlreadyPresentWhenNameIsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_CATEGORY_NAME_MESSAGE);
        categoryManager.isCategoryAlreadyPresent(null);
    }

    /**
     * Tests {@link CategoryManager#isCategoryAlreadyPresent(String)} expects
     * {@link IllegalArgumentException} when category name is empty
     */
    @Test
    public void testIsCategoryAlreadyPresentWhenNameIsEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_CATEGORY_NAME_MESSAGE);
        categoryManager.isCategoryAlreadyPresent(EMPTY_STRING);
    }

    /**
     * Tests {@link CategoryManager#isCategoryAlreadyPresent(String)} expects
     * {@link IllegalArgumentException} when category name is blank
     */
    @Test
    public void testIsCategoryAlreadyPresentWhenNameIsBlank() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_CATEGORY_NAME_MESSAGE);
        categoryManager.isCategoryAlreadyPresent(BLANK_STRING);
    }

    /**
     * Tests {@link CategoryManager#isCategoryAlreadyPresent(String)} expects
     * {@link DAOException} when category name cannot be retrieved from the
     * database
     */
    @Test
    public void testIsCategoryAlreadyPresentThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_RETRIEVING_CATEGORY_BY_NAME);
        when(categoryDAO.isCategoryAlreadyPresent(VALID_CATEGORY_NAME)).thenThrow(new DAOException(EXCEPTION_MESSAGE));
        categoryManager.isCategoryAlreadyPresent(VALID_CATEGORY_NAME);
    }

    /**
     * Tests {@link CategoryManager#isCategoryAlreadyPresent(String)} expects
     * true if category is already present in the database
     */
    @Test
    public void testIsCategoryAlreadyPresentWhenNameIsPresent() throws DAOException {
        when(categoryDAO.isCategoryAlreadyPresent(VALID_CATEGORY_NAME)).thenReturn(true);
        assertTrue(EXPECTED_PRESENT, categoryManager.isCategoryAlreadyPresent(VALID_CATEGORY_NAME));
    }
}
