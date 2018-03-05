package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test class for: {@link SqlOptionManager}
 * 
 */
public class SqlOptionManagerTest 
{
	@InjectMocks
	private SqlOptionManager testManager;
	@Mock
	private DataSource testSource;
	@Mock
	private ResultSet dummyResults;
	@Mock
	private Connection testConnect;
	@Mock
	private Statement testStatement;
	
	private static final String SELECTSKILLOPTIONS = "SELECT * FROM skill_options";
	private static final String SELECTRELOPTIONS = "SELECT * FROM relevance_options";
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@After
	public void tearDown(){
		testManager = null;
		testSource = null;
		dummyResults = null;
		testConnect =null;
		testStatement =null;
	}
	
	/**
	 * Invoke getSkillOptions function.
	 */
	@Test
	public void testGetSkillOptionsWithValidOptions() throws SQLException
	{
		when(testSource.getConnection()).thenReturn(testConnect);
		when(testConnect.createStatement()).thenReturn(testStatement);
		when(testStatement.executeQuery(SELECTSKILLOPTIONS)).thenReturn(dummyResults);
		when(dummyResults.next()).thenReturn(true).thenReturn(false);
		when(dummyResults.getString("value")).thenReturn("1");
		when(dummyResults.getString("description")).thenReturn("test");
		assertTrue(testManager.getSkillOptions().containsValue("test"));
	}
	
	/**
	 * Invoke {@link SqlOptionManager#getSkillOptions()} function with null database results.
	 * @throws NullPointerException.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetSkillOptionsWithNullDBResults() throws SQLException
	{
		when(testSource.getConnection()).thenReturn(testConnect);
		when(testConnect.createStatement()).thenReturn(testStatement);
		when(testStatement.executeQuery(SELECTSKILLOPTIONS)).thenReturn(null);
		assertTrue(testManager.getSkillOptions().containsValue("test"));
	}
	
    /**
     * Invoke {@link SqlOptionManager#getRelevanceOptions()} function with valid
     * input.
     */
	@Test
	public void testGetRelevanceOptionsWithValidOptions() throws SQLException
	{
		when(testSource.getConnection()).thenReturn(testConnect);
		when(testConnect.createStatement()).thenReturn(testStatement);
		when(testStatement.executeQuery(SELECTRELOPTIONS)).thenReturn(dummyResults);
		when(dummyResults.next()).thenReturn(true).thenReturn(false);
		when(dummyResults.getString("value")).thenReturn("1");
		when(dummyResults.getString("description")).thenReturn("test");
		assertTrue(testManager.getRelevanceOptions().containsValue("test"));
	}
	
    /**
     * Invoke {@link SqlOptionManager#getRelevanceOptions()} function with
     * <code>null</code> database results.
     * 
     * @throws NullPointerException
     *             when database results are <code>null</code>
     */
	@Test(expected = NullPointerException.class)
	public void testGetRelevanceOptionsWithNullDBResults() throws SQLException
	{
		when(testSource.getConnection()).thenReturn(testConnect);
		when(testConnect.createStatement()).thenReturn(testStatement);
		when(testStatement.executeQuery(SELECTRELOPTIONS)).thenReturn(null);
		assertTrue(testManager.getRelevanceOptions().containsValue("test"));
	}		
}
