package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Tests the functionalities of the CategoryController.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Asim Mohammed (AM045300)
 * @author Jacob Zimmermann (JZ022690)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryControllerTest {

    private static final String PAGE_RESTRICTED_MESSAGE = "restricted_message";
    private static final String MESSAGE = "message";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ALL_CATEGORIES = "categories";
    private static final int VALID_CATEGORY_ID = 2;
    private static final String VALID_USER_ID = "test";
    private static final String VALID_CATEGORY_NAME = "Object Oriented Programming";
    private static final String VALID_CATEGORY_DESCRIPTION = "Learn the fundamentals of OOP.";
    private static final String ADD_CATEGORY = "add_category";
    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String USER_DETAILS = "userDetails";
    private static final String BLANK_STRING = "         ";
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = null;
    private static final String CATEGORY_EXISTS_ERROR_MESSAGE = "addCategoryPage.message.alreadyPresent";
    private static final String CATEGORY_ADD_ERROR_MESSAGE = "com.database.query.fails";
    private static final String BINDING_RESULT_ERROR_MESSAGE = "com.cerner.devcenter.education.controllers.bindingResultError";
    private static final String CATEGORY_NULL_ERROR_MESSAGE = "addCategoryPage.userGuideMessage";
    private static final String CATEGORY_SUCCESS_MESSAGE = "addCategoryPage.successMessage";

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private final CategoryController categoryController = new CategoryController();
    @Mock
    private CategoryManager categoryManager;
    @Mock
    private BindingResult result;
    @Mock
    private AuthenticationStatusUtil status;
    @Mock
    private HttpSession session;
    @Mock
    private UserProfileDetails userInfo;
    @Mock
    private UserManager userManager;

    private final ModelAndView model = new ModelAndView();
    private MockMvc mockMvc;
    private Category category;
    private List<Category> listOfCategories;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(categoryController).build();
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        listOfCategories = new ArrayList<Category>();
        when(status.isLoggedIn()).thenReturn(true);
        when(categoryManager.addCategory(category)).thenReturn(category);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        userInfo = new UserProfileDetails(
                "test first, test last",
                "ADMIN",
                "test",
                "test@cerner.com",
                "Dev Academy",
                "test manager",
                "Education Evaluation");
        when(session.getAttribute(USER_DETAILS)).thenReturn(userInfo);
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
    }

    /**
     * Test
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * when user is not logged in, it redirects to the login page.
     */
    @Test
    public void testShowAddCategoryPageWhenNotLoggedIn() {
        when(status.isLoggedIn()).thenReturn(false);
        final ModelAndView returnedModel = categoryController.showAddCategoryPage(category, model, session);
        assertEquals(LOGIN_REDIRECT, categoryController.showAddCategoryPage(category, model, session).getViewName());
        assertEquals(Constants.USER_NOT_LOGGED_IN, returnedModel.getModel().get(MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * to validate that the message to guide the user is shown.
     */
    @Test
    public void testShowAddCategoryDisplaysUserGuideMessage() {
        assertEquals(
                i18nBundle.getString(CATEGORY_NULL_ERROR_MESSAGE),
                categoryController.showAddCategoryPage(category, model, session).getModel().get(MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * expects {@link IllegalArgumentException} when category parameter is null.
     */
    @Test
    public void testAddCategoryWhenCategoryParameterIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        categoryController.addCategory(null, model, result);
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * expects {@link IllegalArgumentException} when model parameter is null.
     */
    @Test
    public void testAddCategoryWhenModelParameterIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        categoryController.addCategory(category, null, result);
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * expects {@link IllegalArgumentException} when result parameter is null.
     */
    @Test
    public void testAddCategoryWhenResultParameterIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        categoryController.addCategory(category, model, null);
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * when category is already present.
     */
    @Test
    public void testAddCategoryWhenCategoryIsAlreadyPresent() {
        when(categoryManager.isCategoryAlreadyPresent(VALID_CATEGORY_NAME)).thenReturn(true);
        categoryController.addCategory(category, model, result);
        assertEquals(model.getModel().get(SUCCESS_MESSAGE), i18nBundle.getString(CATEGORY_EXISTS_ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * when isCategoryAlreadyPresent related database query fails then error
     * message is thrown .
     */
    @Test
    public void testAddCategoryWhenCategoryIsAlreadyPresentDBQueryFails() {
        when(categoryManager.isCategoryAlreadyPresent(VALID_CATEGORY_NAME)).thenThrow(new ManagerException());
        categoryController.addCategory(category, model, result);
        assertEquals(model.getModel().get(ERROR_MESSAGE), i18nBundle.getString(CATEGORY_ADD_ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * when adding a category related database query fails then error message is
     * thrown .
     */
    @Test
    public void testAddCategoryWhenAddCategoryToDBQueryFails() {
        when(categoryManager.addCategory(category)).thenThrow(new ManagerException());
        categoryController.addCategory(category, model, result);
        assertEquals(model.getModel().get(ERROR_MESSAGE), i18nBundle.getString(CATEGORY_ADD_ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * when getting all categories related database query fails then error
     * message is thrown .
     */
    @Test
    public void testAddCategoryWhenGettingAllCategoriesToDBQueryFails() {
        when(categoryManager.getAllCategories()).thenThrow(new ManagerException());
        categoryController.addCategory(category, model, result);
        assertEquals(model.getModel().get(ERROR_MESSAGE), i18nBundle.getString(CATEGORY_ADD_ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * and validates the view name for "show add category" page when user is
     * logged in.
     *
     * @throws Exception
     */
    @Test
    public void testViewNameForShowAddCategoryPage() throws Exception {
        mockMvc.perform(get("/app/show_add_category").sessionAttr(USER_DETAILS, userInfo)).andExpect(
                view().name(ADD_CATEGORY));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * and validates the view name for "add category" page when user is logged
     * in.
     *
     * @throws Exception
     */
    @Test
    public void testViewNameForAddCategoryPage() throws Exception {
        mockMvc.perform(get("/app/add_category_page")).andExpect(view().name(ADD_CATEGORY));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * to validate that the message to guide the user is shown.
     */
    @Test
    public void testAddCategoryDisplaysUserGuideMessage() {
        assertEquals(
                i18nBundle.getString(CATEGORY_NULL_ERROR_MESSAGE),
                categoryController.addCategory(category, model, result).getModel().get(MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * and validates that user is sent back to login page when they are not
     * logged in.
     */
    @Test
    public void testAddCategoryPageWhenNotLoggedIn() {
        when(status.isLoggedIn()).thenReturn(false);
        final ModelAndView returnedModel = categoryController.addCategory(category, model, result);
        assertEquals(LOGIN_REDIRECT, categoryController.addCategory(category, model, result).getViewName());
        assertEquals(Constants.USER_NOT_LOGGED_IN, returnedModel.getModel().get(MESSAGE));
    }

    /**
     * Test
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * when the result has binding errors after user tries to add category.
     */
    @Test
    public void testAddCategoryWhenBindingErrors() {
        when(result.hasErrors()).thenReturn(true);
        assertEquals(
                i18nBundle.getString(BINDING_RESULT_ERROR_MESSAGE),
                categoryController.addCategory(category, model, result).getModel().get(ERROR_MESSAGE));
    }

    /**
     * Test the returned model after
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * is provided with all valid inputs from the user.
     */
    @Test
    public void testShowAddCategoryPageWhenLoggedIn() {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
        final ModelAndView returnedModel = categoryController.showAddCategoryPage(category, model, session);
        assertEquals(ADD_CATEGORY, returnedModel.getViewName());
        assertEquals(listOfCategories, returnedModel.getModel().get(ALL_CATEGORIES));
        assertEquals(i18nBundle.getString(CATEGORY_NULL_ERROR_MESSAGE), returnedModel.getModel().get(MESSAGE));
    }

    /**
     * Test the returned model of
     * {@link CategoryController#addCategory(Category, ModelAndView, BindingResult)}
     * after the user adds a category.
     */
    @Test
    public void testAddCategoryWhenLoggedIn() {
        final ModelAndView returnedModel = categoryController.addCategory(category, model, result);
        assertEquals(ADD_CATEGORY, returnedModel.getViewName());
        assertEquals(listOfCategories, returnedModel.getModel().get(ALL_CATEGORIES));
        assertEquals(i18nBundle.getString(CATEGORY_SUCCESS_MESSAGE), returnedModel.getModel().get(SUCCESS_MESSAGE));
    }

    /**
     * Tests {@link CategoryController#deleteCategoryBasedOnCategoryId(int)}
     * expects {@link IllegalArgumentException} when the categoryId is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteCategoryBasedOnCategoryIdWithZeroCategoryId() {
        categoryController.deleteCategoryBasedOnCategoryId(0);
    }

    /**
     * Tests {@link CategoryController#deleteCategoryBasedOnCategoryId(int)}
     * expects {@link IllegalArgumentException} when the categoryId is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteCategoryBasedOnCategoryIdWithNegativeCategoryId() {
        categoryController.deleteCategoryBasedOnCategoryId(-1);
    }

    /**
     * Tests {@link CategoryController#deleteCategoryBasedOnCategoryId(int)}
     * with valid categoryId when exception is thrown while deletion.
     */
    @Test
    public void testDeleteCategoryBasedOnCategoryIdWithException() {
        doThrow(ManagerException.class).when(categoryManager).deleteCategoryById(VALID_CATEGORY_ID);
        assertEquals(false, categoryController.deleteCategoryBasedOnCategoryId(VALID_CATEGORY_ID));
    }

    /**
     * Tests {@link CategoryController#deleteCategoryBasedOnCategoryId(int)}
     * with valid categoryId.
     */
    @Test
    public void testDeleteCategoryBasedOnCategoryIdReturnsCorrectValue() {
        assertEquals(true, categoryController.deleteCategoryBasedOnCategoryId(VALID_CATEGORY_ID));
    }

    /**
     * Tests
     * {@link CategoryController#categoryAutocomplete(String, HttpSession)} with
     * empty string
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCategoryAutoCompleteForEmptyString() {
        categoryController.categoryAutocomplete(EMPTY_STRING, session);
    }

    /**
     * Tests
     * {@link CategoryController#categoryAutocomplete(String, HttpSession)} with
     * null string
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCategoryAutoCompleteForNullString() {
        categoryController.categoryAutocomplete(NULL_STRING, session);
    }

    /**
     * Tests
     * {@link CategoryController#categoryAutocomplete(String, HttpSession)} with
     * blank string
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCategoryAutoCompleteForBlankString() {
        categoryController.categoryAutocomplete(BLANK_STRING, session);
    }

    /**
     * Tests
     * {@link CategoryController#categoryAutocomplete(String, HttpSession)} when
     * it returns 0 results
     */
    @Test
    public void testCategoryAutoCompleteSearchWhenReturnsZeroResults() {
        when(categoryManager.nonchosenCategories(VALID_CATEGORY_NAME, VALID_USER_ID))
                .thenReturn(new ArrayList<Category>());
        when(categoryManager.chosenCategories(VALID_CATEGORY_NAME.toLowerCase(), VALID_USER_ID))
                .thenReturn(new ArrayList<Category>());
        assertEquals(0, categoryController.categoryAutocomplete(VALID_CATEGORY_NAME, session).size());
    }

    /**
     * Test helper method for creating a list of {@link Category} objects.
     *
     * @param size
     *            The number of {@link Category} objects. to place in the list.
     *            Cannot be negative.
     * @return A list of unique {@link Category} objects of the specified size.
     * @throws IllegalArgumentException
     *             If size is negative.
     */
    private static List<Category> getTestCategoryList(final int size) {
        final List<Category> categoryList = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            categoryList.add(new Category(i, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION));
        }
        return categoryList;
    }

    /**
     * Tests
     * {@link CategoryController#categoryAutocomplete(String, HttpSession)} when
     * it returns more than 10 results.
     * 
     */
    @Test
    public void testCategoryAutoCompleteSearchWhenReturnsMoreThanTenResults() {
        when(categoryManager.nonchosenCategories(VALID_CATEGORY_NAME.toLowerCase(), VALID_USER_ID))
                .thenReturn(getTestCategoryList(8));
        when(categoryManager.chosenCategories(VALID_CATEGORY_NAME.toLowerCase(), VALID_USER_ID))
                .thenReturn(getTestCategoryList(5));
        assertEquals(
                Constants.AUTOFILL_SIZE,
                categoryController.categoryAutocomplete(VALID_CATEGORY_NAME, session).size());
    }

    /**
     * Tests
     * {@link CategoryController#categoryAutocomplete(String, HttpSession)} when
     * it returns less than 10 results.
     */
    @Test
    public void testCategoryAutoCompleteSearchWhenReturnsLessThanTenResults() {
        when(categoryManager.nonchosenCategories(VALID_CATEGORY_NAME.toLowerCase(), VALID_USER_ID))
                .thenReturn(getTestCategoryList(5));

        when(categoryManager.chosenCategories(VALID_CATEGORY_NAME.toLowerCase(), VALID_USER_ID))
                .thenReturn(getTestCategoryList(4));
        assertEquals(9, categoryController.categoryAutocomplete(VALID_CATEGORY_NAME, session).size());
    }

    /**
     * Tests
     * {@link CategoryController#categoryAutocomplete(String, HttpSession)} when
     * it returns 10 results.
     */
    @Test
    public void testCategoryAutoCompleteSearchWhenReturnsTenResults() {
        when(categoryManager.nonchosenCategories(VALID_CATEGORY_NAME.toLowerCase(), VALID_USER_ID))
                .thenReturn(getTestCategoryList(5));
        when(categoryManager.chosenCategories(VALID_CATEGORY_NAME.toLowerCase(), VALID_USER_ID))
                .thenReturn(getTestCategoryList(5));
        assertEquals(10, categoryController.categoryAutocomplete(VALID_CATEGORY_NAME, session).size());
    }

    /**
     * Test verifies
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * when user is not admin then redirects to access denied page.
     *
     * @throws Exception
     */
    @Test
    public void testShowAddCategoryPageWhenUserNotAdmin() throws Exception {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        when(status.redirectsAccessDenied()).thenCallRealMethod();
        assertEquals(
                PAGE_RESTRICTED_MESSAGE,
                categoryController.showAddCategoryPage(category, model, session).getViewName());
    }

    /**
     * Test verifies
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * when user is admin then redirects to add category page.
     *
     * @throws Exception
     */
    @Test
    public void testShowAddCategoryPageWhenUserIsAdmin() throws Exception {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
        assertEquals(ADD_CATEGORY, categoryController.showAddCategoryPage(category, model, session).getViewName());
    }

    /**
     * Test verifies
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * when {@link Category}is null expects {@link IllegalArgumentException}
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowAddCategoryPageWhenCategoryArgumentIsNull() throws Exception {
        categoryController.showAddCategoryPage(null, model, session);
    }

    /**
     * Test verifies
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * when {@link ModelAndView}is null expects {@link IllegalArgumentException}
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowAddCategoryPageWhenModelArgumentIsNull() throws Exception {
        categoryController.showAddCategoryPage(category, null, session);
    }

    /**
     * Test verifies
     * {@link CategoryController#showAddCategoryPage(Category, ModelAndView, HttpSession)}
     * when session is null expects {@link IllegalArgumentException}.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowAddCategoryPageWhenSessionArgumentIsNull() throws Exception {
        categoryController.showAddCategoryPage(category, model, null);
    }
}
