package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.dao.SkillOptionsDAOImpl.SkillRowMapper;
import com.cerner.devcenter.education.models.SkillOptions;

/**
 * This class contains the unit test cases to test {@link SkillOptionsDAOImpl}
 * 
 * @author Sreelakshmi Chintha (SC043016)
 */
public class SkillOptionsDAOImplTest {

	@InjectMocks
	private SkillOptionsDAOImpl skillOptionsDAOImpl;
	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private DataAccessException dataAccessException;
	@Mock
	private ResultSet resultSet;

	private static final int SKILL_VALUE = 1;
	private static final String SKILL_DESCRIPTION = "Test Skill Description";
	private static List<SkillOptions> skillList;
	private static Map<Integer, String> skillOptionsMap;
	private SkillOptionsDAOImpl.SkillRowMapper skillRowMapper;

	@Before
	public void setup() throws SQLException {
		initMocks(this);
		skillList = new ArrayList<>();
		skillOptionsMap = new TreeMap<>();
		skillList.add(new SkillOptions(SKILL_VALUE, SKILL_DESCRIPTION));
		skillRowMapper = skillOptionsDAOImpl.new SkillRowMapper();
		when(jdbcTemplate.query(anyString(), any(SkillRowMapper.class))).thenReturn(skillList);
		when(resultSet.getInt("value")).thenReturn(SKILL_VALUE);
		when(resultSet.getString("description")).thenReturn(SKILL_DESCRIPTION);
	}

	/**
	 * This function tests {@link SkillOptionsDAOImpl#getSkillOptions()}
	 * functionality and expects {@link NullPointerException} when
	 * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper)}
	 * returns null results from database.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = NullPointerException.class)
	public void testGetSkillOptionsWithNullDBResults() throws SQLException, DAOException {
		when(jdbcTemplate.query(anyString(), any(SkillRowMapper.class))).thenReturn(null);
		skillOptionsDAOImpl.getSkillOptions();
	}

	/**
	 * This function tests {@link SkillOptionsDAOImpl#getSkillOptions()}
	 * functionality and expects {@link DAOException} when
	 * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper)}
	 * throws {@link DataAccessException}
	 * 
	 * @throws DAOException
	 */
	@Test(expected = DAOException.class)
	public void testGetSkillOptionsWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
		when(jdbcTemplate.query(anyString(), any(SkillRowMapper.class))).thenThrow(dataAccessException);
		skillOptionsDAOImpl.getSkillOptions();
	}

	/**
	 * This function tests {@link SkillOptionsDAOImpl#getSkillOptions()} with
	 * valid inputs
	 * 
	 * @throws SQLException
	 * @throws DAOException
	 */
	@Test
	public void testGetSkillOptionsWithValidInputs() throws SQLException, DAOException {
		skillOptionsMap = skillOptionsDAOImpl.getSkillOptions();
		assertTrue(skillOptionsMap.containsKey(SKILL_VALUE));
		assertEquals(SKILL_DESCRIPTION, skillOptionsMap.get(SKILL_VALUE));
	}

	/**
	 * This function tests {@link SkillRowMapper#mapRow(ResultSet, int)}
	 * functionality
	 */
	@Test
	public void testMapRowValidResultSet() throws SQLException {
		SkillOptions skillOption = skillRowMapper.mapRow(resultSet, 1);
		assertEquals(SKILL_VALUE, skillOption.getValue());
		assertEquals(SKILL_DESCRIPTION, skillOption.getDescription());
	}
}
