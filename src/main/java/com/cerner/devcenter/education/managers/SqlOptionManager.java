package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class takes options that can be selected by the user from a SQL Database.
 * 
 */
@Service("sqlOptionManager")
public class SqlOptionManager implements OptionManager {
    private static final Logger LOGGER = Logger.getLogger(SqlOptionManager.class);
    private static final String SELECTSKILLOPTIONS = "SELECT * FROM skill_options";
    private static final String SELECTRELOPTIONS = "SELECT * FROM relevance_options";
    private TreeMap<Integer, String> skillOptions;
    private TreeMap<Integer, String> relevanceOptions;
    @Autowired
    private DataSource dataSource;
    
   /**
    * Constructor for SqlOptionManager that creates new skillOptions and relevanceOptions TreeMaps.
    */
    public SqlOptionManager() {
        skillOptions = new TreeMap<Integer, String>();
        relevanceOptions = new TreeMap<Integer, String>();
    }

    /**
     * @inheritDoc
     */
    @Override
    public TreeMap<Integer, String> getSkillOptions() {
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            results = statement.executeQuery(SELECTSKILLOPTIONS);
            
            checkNotNull(results);
            
            while(results.next()) {
                int value = Integer.parseInt(results.getString("value"));
                String description = results.getString("description");
                skillOptions.put(value, description);
            }
        }
        catch (SQLException e) {
            LOGGER.error("Database Exception while pulling Skill Options", e);
        }
        
        return skillOptions;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TreeMap<Integer, String> getRelevanceOptions() {
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            
            results = statement.executeQuery(SELECTRELOPTIONS);
            
            checkNotNull(results);        
            
            while(results.next()) {
                int value = Integer.parseInt(results.getString("value"));
                String description = results.getString("description");
                relevanceOptions.put(value, description);
            }
        }
        catch (SQLException sqlEx) {
            LOGGER.error("Database Exception while pulling Relevance Options", sqlEx);
        }
        finally {
            try {
        	    results.close();
         	    statement.close();
        	    connection.close();
            }
            catch (SQLException sqlEx) {
                LOGGER.error("Database exception while closing connection", sqlEx);
            }
        }
        
        return relevanceOptions;
    }
}
