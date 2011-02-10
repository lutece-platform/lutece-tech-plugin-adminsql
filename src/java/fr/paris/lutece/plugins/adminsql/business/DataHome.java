/*
 * DataHome.java
 *
 * Created on 24 octobre 2008, 14:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.adminsql.business;

import fr.paris.lutece.portal.service.database.PluginConnectionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


import java.util.List;


/**
 * The class calling the business objects
 */
public class DataHome
{
    private static IDataDAO _dao = (IDataDAO) SpringContextService.getPluginBean( "adminsql", "dataDAO" );

    /**
     * Creates a new instance of DataHome
     * @param strTableName Name of the table
     * @param connectionService Plugin connectionService
     * @return List of all data in a table
     */
    public static List<List<String>> findAll( String strTableName, PluginConnectionService connectionService )
    {
        return _dao.selectDataList( strTableName, connectionService );
    }

    /**
     * List of data of user request
     * @param strPoolName Name of the pool
     * @param strUserRequest User Request
     * @param connectionService PluginConnectionService
     * @return List of data of user request
     */
    public static List<List<String>> findAllInUserRequest( String strPoolName, String strUserRequest,
        PluginConnectionService connectionService )
    {
        return _dao.selectDataListUserRequest( strPoolName, strUserRequest, connectionService );
    }

    /**
     * Create an instance of data
     * @param strPoolName the name of the current pool
     * @param strTableName the name of the table
     * @param listData data to insert
     * @param listFieldsNames fields of a table
     * @param connectionService PluginConnectionService
     * @return list of data
     */
    public static List<String> create( String strPoolName, String strTableName, List<String> listData,
        List<String> listFieldsNames, PluginConnectionService connectionService )
    {
        _dao.insert( strPoolName, strTableName, listData, listFieldsNames, connectionService );

        return listData;
    }

    /**
     * Create an instance of data with user request
     * @param strUserRequest user request
     * @param connectionService PluginConnctionSerice object
     * @param strPoolName the name of the pool
     */
    public static void createWithUserRequest( String strUserRequest, PluginConnectionService connectionService,
        String strPoolName )
    {
        _dao.insertWithUserRequest( strUserRequest, connectionService, strPoolName );
    }

    /**
     * find a row of a table
     * @param strTableName the name of the table
     * @param strContatFieldNameAndData the concatenation of the field name and the data
     * @param listFields list of field table
     * @param connectionService PluginConnectionService object
     * @return a row of a table
     */
    public static List<List<String>> findByPrimaryKey( String strTableName, String strContatFieldNameAndData,
        List<Field> listFields, PluginConnectionService connectionService )
    {
        return _dao.load( strTableName, strContatFieldNameAndData, listFields, connectionService );
    }

    /**
     * Result of a select request type
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @return the result of the user request
     */
    public static List<List<String>> userRequest( String strUserRequest, PluginConnectionService connectionService )
    {
        return _dao.findResultOfUserRequest( strUserRequest, connectionService );
    }

    /**
     * Find fields names of a table for user request
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @param strPoolName the name of the pool
     * @return list of fields names
     */
    public static List<String> findFieldsNames( String strUserRequest, PluginConnectionService connectionService,
        String strPoolName )
    {
        return _dao.selectColumnNames( strUserRequest, connectionService, strPoolName );
    }

    /**
     * Update a row in a table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listFieldValues list of table fields
     * @param connectionService PluginConnectionService object
     */
    public static void update( String strPoolName, String strTableName, List<FieldValues> listFieldValues,
        PluginConnectionService connectionService )
    {
        _dao.store( strPoolName, strTableName, listFieldValues, connectionService );
    }

    /**
     * Delete a row
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listConcatFieldNameAndData list of concatenation of field name and data
     * @param connectionService PluginConnectionService object
     */
    public static void remove( String strPoolName, String strTableName, List<String> listConcatFieldNameAndData,
        PluginConnectionService connectionService )
    {
        _dao.delete( strPoolName, strTableName, listConcatFieldNameAndData, connectionService );
    }
}
