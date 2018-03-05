package com.cerner.devcenter.education.dao;

import java.util.Map;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.SkillOptions;

/**
 * This interface is responsible for performing database operations for
 * {@link SkillOptions} objects.
 * 
 * @author SC043016
 */
public interface SkillOptionsDAO {

	/**
	 * Performs a query on the database that will return {@link Map} object
	 * contains mapping of skill value to description.
	 * 
	 * @return A mapping of skill value to skill description for 'skill level'
	 *         field.
	 * @throws DAOException
	 *             when there is an error while trying to get skill options from database
	 */
	Map<Integer, String> getSkillOptions() throws DAOException;

}
