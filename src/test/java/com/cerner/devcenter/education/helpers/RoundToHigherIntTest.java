package com.cerner.devcenter.education.helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ResourceDAO;
import com.cerner.devcenter.education.managers.ResourceManager;

public class RoundToHigherIntTest {

	@InjectMocks
	private RoundToHigherIntHelper roundToHigherInt;
	@Mock
	private ResourceDAO mockResourceDAO;
	@Mock
	private ResourceManager resourceManager;
	private static final int RESOURCES_PER_PAGE = 10;
	private static final int RESOURCE_COUNT = 38;

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Verifies {@link RoundToHigherIntHelper#roundToHigherInt(int, int)} produces
	 * correct output with valid input.
	 */
	@Test
	public void testroundToHigherIntWithValidData() throws DAOException {
		assertEquals(4, RoundToHigherIntHelper.roundToHigherInt(RESOURCE_COUNT, RESOURCES_PER_PAGE));
	}

	/**
	 * Verifies {@link RoundToHigherIntHelper#roundToHigherInt(int, int)} produces
	 * correct output when count Is multiple of maximum resources per page.
	 */
	@Test
	public void testroundToHigherIntWhenCountIsMultipleOfResourcesPerPage() throws DAOException {
		assertEquals(4, RoundToHigherIntHelper.roundToHigherInt(40, RESOURCES_PER_PAGE));
	}

}
