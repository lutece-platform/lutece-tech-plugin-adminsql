/*
 * IDataDAO.java
 *
 * Created on 24 octobre 2008, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.adminsql.business;

import fr.paris.lutece.plugins.adminsql.business.Field;
import fr.paris.lutece.portal.service.database.PluginConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.sql.SQLException;

import java.util.List;


/**
 * Lists all methods which are in DataDAO
 */
public interface IDataDAO
{
    /**
     * Selects and loads into the Data object all the data from the database
     * @param strTableName the name of the table
     * @param connectionService PluginConnectionService object
     * @return the list of data
     */
    List<List<String>> selectDataList( String strTableName, PluginConnectionService connectionService );

    /**
     * Creates a new instance of IDataDAO
     * @param strPoolName the name of the pool
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @return the list of data from user request
     */
    List<List<String>> selectDataListUserRequest( String strPoolName, String strUserRequest,
        PluginConnectionService connectionService );

    /**
     * Insert into a table of a database
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listData the list of data
     * @param listFieldsNames the list of field names
     * @param connectionService PluginConnectionService object
     */
    void insert( String strPoolName, String strTableName, List<String> listData, List<String> listFieldsNames,
        PluginConnectionService connectionService );

    /**
     * Insert into a table of a database with user request
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @param strPoolName the name of the pool
     */
    void insertWithUserRequest( String strUserRequest, PluginConnectionService connectionService, String strPoolName );

    /*List<String> load ( String strPoolName, String strTableName, PluginConnectionService connectionService );*/
    /**
     * Find all data in a table
     * @param strTableName the name of the table
     * @param strContatFieldNameAndData the concatenation of the field name and the data
     * @param listFields the list of the fields
     * @param connectionService PluginConnectionService object
     * @return the list of all data in a table
     */
    public List<List<String>> load( String strTableName, String strContatFieldNameAndData, List<Field> listFields,
        PluginConnectionService connectionService );

    /**
     * Find all data from user request
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @return list of data from user request
     */
    List<List<String>> findResultOfUserRequest( String strUserRequest, PluginConnectionService connectionService );

    /**
     * Find the names of the columns
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @param strPoolName the name of the pool
     * @return all names of the columns
     */
    public List<String> selectColumnNames( String strUserRequest, PluginConnectionService connectionService,
        String strPoolName );

    /**
     * Store the row modified
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listFieldValues list of the fields values
     * @param connectionService PluginConnectionService object
     */
    void store( String strPoolName, String strTableName, List<FieldValues> listFieldValues,
        PluginConnectionService connectionService );

    /**
     * Delete a row
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listConcatFieldNameAndData the concatenation of the field name and the data
     * @param connectionService PluginConnectionService object
     */
    void delete( String strPoolName, String strTableName, List<String> listConcatFieldNameAndData,
        PluginConnectionService connectionService );
}
