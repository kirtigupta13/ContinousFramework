package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceTypeDAO;
import com.cerner.devcenter.education.exceptions.DuplicateResourceTypeFoundException;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * Tests the functionalities of {@link ResourceTypeManager} class
 * 
 * @author Gunjan Kaphle (GK045931)
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceTypeManagerTest {
    @InjectMocks
    private ResourceTypeManager resourceTypeManager;
    @Mock
    private ResourceTypeDAO mockResourceTypeDAO;
    @Mock
    private ResourceType mockResourceType;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ResourceType resourceType;
    private List<ResourceType> listOfResourceTypes;

    public static final int VALID_RESOURCE_TYPE_ID = 5;
    public static final String VALID_RESOURCE_TYPE_NAME = "Ebook";
    public static final int TYPE_ID_SMALLER_THAN_ZERO = -8;

    @Before
    public void setup() throws DAOException {
        MockitoAnnotations.initMocks(this);
        resourceType = new ResourceType(VALID_RESOURCE_TYPE_ID, VALID_RESOURCE_TYPE_NAME);
        listOfResourceTypes = new ArrayList<ResourceType>();
        listOfResourceTypes.add(resourceType);

        // Mock ResourceType
        when(mockResourceType.getResourceTypeId()).thenReturn(VALID_RESOURCE_TYPE_ID);
        when(mockResourceType.getResourceType()).thenReturn(VALID_RESOURCE_TYPE_NAME);
        // Mock ResourceTypeDAO
        when(mockResourceTypeDAO.getById(VALID_RESOURCE_TYPE_ID)).thenReturn(resourceType);
        when(mockResourceTypeDAO.addResourceType(resourceType.getResourceType())).thenReturn(mockResourceType);
    }

    /**
     * Tests {@link ResourceTypeManager#addResourceType(String)}
     * functionality
     */
    @Test
    public void testAddResourceType() {
        assertEquals(mockResourceType.getResourceType(),
                resourceTypeManager.addResourceType(VALID_RESOURCE_TYPE_NAME).getResourceType());
    }

    /**
     * Tests {@link ResourceTypeManager#addResourceType(String)} functionality,
     * the return resourceType should be null when
     * {@link ResourceTypeDAO#addResourceType(String)} returns null
     * 
     * @throws DAOException
     */
    @Test
    public void testAddResourceTypeWhenAddResourceTypeToDBReturnNull() throws DAOException {
        when(mockResourceTypeDAO.addResourceType(resourceType.getResourceType())).thenReturn(null);
        assertNull(resourceTypeManager.addResourceType(VALID_RESOURCE_TYPE_NAME));
    }

    /**
     * Tests {@link ResourceTypeManager#addResourceType(String)}
     * functionality, expects {@link IllegalArgumentException} when
     * {@link ResourceType} is null
     */
    @Test
    public void testAddResourceTypeWhenResourceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Resource Type cannot be empty or null.");
        resourceTypeManager.addResourceType(null);
    }

    /**
     * Tests {@link ResourceTypeManager#addResourceType(String)} functionality,
     * expects {@link ManagerException} when
     * {@link ResourceTypeDAO#addResourceType(String)} throws
     * {@link DAOException}
     * 
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testAddResourceTypeThrowsManagerException() throws DAOException {
        when(mockResourceTypeDAO.addResourceType(resourceType.getResourceType())).thenThrow(new DAOException());
        resourceTypeManager.addResourceType(VALID_RESOURCE_TYPE_NAME);
    }

    /**
     * Test {@link ResourceTypeManager#addResourceType(String)} functionality
     * when the resource type already exists in the database
     * 
     * @throws DAOException
     */
    @Test(expected = DuplicateResourceTypeFoundException.class)
    public void testAddResourceTypeThatAlreadyExists() throws DAOException {
        when(mockResourceTypeDAO.getAllResourceTypes()).thenReturn(listOfResourceTypes);
        resourceTypeManager.addResourceType(VALID_RESOURCE_TYPE_NAME);
    }

    /**
     * Tests that @{link ResourceTypeManager#getResourceTypeById(int)} throws an
     * {@link IllegalArgumentException} when getResourceTypeById is negative id.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourceTypeWhenIdIsNegative() {
        resourceTypeManager.getResourceTypeById(TYPE_ID_SMALLER_THAN_ZERO);
    }

    /**
     * Tests that @{link ResourceTypeManager#getResourceTypeById(int)} throws an
     * {@link IllegalArgumentException} when getResourceTypeById is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourceTypeWhenIdIsZero() {
        resourceTypeManager.getResourceTypeById(0);
    }

    /**
     * Tests {@link ResourceTypeManager#getResourceTypeById(int)} functionality,
     * expects {@link ManagerException} when
     * {@link ResourceTypeDAO#getById(int)} throws {@link DAOException}
     * 
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testGetResourceTypeByIDThrowsManagerException() throws DAOException {
        doThrow(DAOException.class).when(mockResourceTypeDAO).getById(VALID_RESOURCE_TYPE_ID);
        resourceTypeManager.getResourceTypeById(VALID_RESOURCE_TYPE_ID);
    }

    /**
     * Tests {@link ResourceTypeManager#getResourceTypeById(int)} functionality is
     * called by ResourceTypeManager and getResourceTypeById() returns a valid
     * resource.
     */
    @Test
    public void testGetResourceTypeFromDB() {
        ResourceType newResourceType = resourceTypeManager.getResourceTypeById(VALID_RESOURCE_TYPE_ID);
        assertEquals(resourceType.getResourceTypeId(), newResourceType.getResourceTypeId());
        assertEquals(resourceType.getResourceType(), newResourceType.getResourceType());
    }

    /**
     * Tests {@link ResourceTypeManager#getResourceTypeDAO()} functionality
     */
    @Test
    public void testResourceTypeDAOGetter() {
        assertSame(mockResourceTypeDAO, resourceTypeManager.getResourceTypeDAO());
    }

    /**
     * Tests {@link ResourceTypeManager#getAllResourceTypes()} functionality,
     * expects {@link ManagerException} when
     * {@link ResourceTypeDAO#getAllResourceTypes()} throws {@link DAOException}
     * 
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testGetAllResourceTypesThrowsManagerException() throws DAOException {
        when(mockResourceTypeDAO.getAllResourceTypes()).thenThrow(new DAOException());
        resourceTypeManager.getAllResourceTypes();
    }

    /**
     * Tests {@link ResourceTypeManager#getAllResourceTypes()} functionality is
     * called by ResourceTypeManager
     * 
     * @throws DAOException
     */
    @Test
    public void testGetAllResourceTypes() throws DAOException {
        when(mockResourceTypeDAO.getAllResourceTypes()).thenReturn(listOfResourceTypes);
        ResourceType newResourceType = resourceTypeManager.getAllResourceTypes().get(0);
        assertEquals(VALID_RESOURCE_TYPE_ID, newResourceType.getResourceTypeId());
        assertEquals(VALID_RESOURCE_TYPE_NAME, newResourceType.getResourceType());
    }

    /**
     * Test {@link ResourceTypeManager#checkIfResourceTypeAlreadyExists(String)}
     * functionality
     * 
     * @throws DAOException
     */
    @Test
    public void testCheckIfResourceTypeAlreadyExists() throws DAOException {
        when(mockResourceTypeDAO.getAllResourceTypes()).thenReturn(listOfResourceTypes);
        ResourceType newResourceType = resourceTypeManager.getAllResourceTypes().get(0);
        assertTrue(resourceTypeManager.checkIfResourceTypeAlreadyExists(newResourceType.getResourceType()));
    }

}
