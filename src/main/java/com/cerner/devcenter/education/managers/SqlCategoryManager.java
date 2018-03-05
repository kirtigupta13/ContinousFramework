package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.models.Category;

/**
 * This implementation takes category names and descriptions from a SQL
 * database.
 * 
 */
@Service("sqlCategoryManager")
public class SqlCategoryManager {
	private static final Logger LOGGER = Logger.getLogger(SqlCategoryManager.class);
	private static final String SELECTCATEGORY = "SELECT * FROM category";
	private List<Category> categories;
	@Autowired
	private DataSource dataSource;

    /**
     * This method retrieves a list of all categories.
     *
     * @return a {@link List} of all {@link Category}
     */
	public List<Category> getCategories() {
		categories = new ArrayList<Category>();

		Connection connection = null;
		Statement statement = null;
		ResultSet results = null;

		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			results = statement.executeQuery(SELECTCATEGORY);

			checkNotNull(results);

			while (results.next()) {
				int id = Integer.parseInt(results.getString("category_id"));
				String name = results.getString("name");
				String description = results.getString("description");
				categories.add(new Category(id, name, description));
			}
		} catch (SQLException sqlEx) {
			LOGGER.error("Database exception while obtaining category list.", sqlEx);
		} finally {
			try {
				results.close();
				statement.close();
				connection.close();
			} catch (Exception ex) {
				LOGGER.error("Database exception while closing connection", ex);
			}
		}
		return categories;
	}
}
