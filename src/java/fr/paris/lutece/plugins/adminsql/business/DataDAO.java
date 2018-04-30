/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.adminsql.business;

import fr.paris.lutece.portal.service.database.PluginConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Creates a new instance of DataDAO
 */
public class DataDAO implements IDataDAO
{
    //Constants
    private static final String WHERE = " WHERE ";
    private static final String VALUES = " VALUES ";
    private static final String SELECT = " SELECT ";
    private static final String FROM = " FROM ";
    private static final String AND = " AND ";
    private static final String UPDATE = "UPDATE ";
    private static final String SET = " SET ";
    private static final String STR_SQL_ERROR = "SQL Error executing command : ";
    private static final String DELETE_FROM = "DELETE FROM ";

    //Requêtes
    private static final String SQL_QUERY_SELECT_ALL_DATA = "SELECT * FROM ";
    private static final String SQL_QUERY_SELECT_A_ROW = "SELECT * FROM ";
    private static final String SQL_QUERY_INSERT_VALUES = "INSERT INTO ";

    /**
     * Selects and loads into the Data object all the data from the database
     * @param strTableName the name of the table
     * @param connectionService PluginConnectionService Object
     * @return list of data
     */
    public List<List<String>> selectDataList( String strTableName, PluginConnectionService connectionService )
    {
        List<List<String>> listData = new ArrayList<List<String>>(  );

        try
        {
            listData = selectSqlQuery( SQL_QUERY_SELECT_ALL_DATA + strTableName, connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return listData;
    }

    /**
     * Selects and loads into the Data object all the data from the database in user request
     * @param strPoolName the name of the pool
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService Object
     * @return list of data
     */
    public List<List<String>> selectDataListUserRequest( String strPoolName, String strUserRequest,
        PluginConnectionService connectionService )
    {
        List<List<String>> listData = new ArrayList<List<String>>(  );
        connectionService = new PluginConnectionService( strPoolName );
        connectionService.setPool( strPoolName );

        try
        {
            listData = selectSqlQuery( strUserRequest, connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return listData;
    }

    /**
     * Insert data in table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listData the list of data
     * @param listFieldsNames list of fields names
     * @param connectionService PluginConnectionService object
     */
    public void insert( String strPoolName, String strTableName, List<String> listData, List<String> listFieldsNames,
        PluginConnectionService connectionService )
    {
        String strFieldsNames = "";
        String strDataValues = "";

        for ( String strFieldName : listFieldsNames )
        {
            strFieldsNames = strFieldsNames + strFieldName + ", ";
        }

        strFieldsNames = strFieldsNames.substring( 0, strFieldsNames.length(  ) - 2 );

        String strFieldsNameFinal = " (" + strFieldsNames + ") ";

        for ( String strDataValue : listData )
        {
            strDataValues += ( "'" + strDataValue + "'" + ", " );
        }

        strDataValues = strDataValues.substring( 0, strDataValues.length(  ) - 2 );

        String strDataValuesFinal = " (" + strDataValues + ") ";
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_VALUES + strTableName + " " + strFieldsNameFinal + VALUES +
                strDataValuesFinal, changePool( strPoolName ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Store the data in tha table in a database
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listFieldValues the list fields values
     * @param connectionService PluginConnectionService object
     */
    public void store( String strPoolName, String strTableName, List<FieldValues> listFieldValues,
        PluginConnectionService connectionService )
    {
        String strAllDataToModify = "";
        String strAllDataModified = "";

        for ( FieldValues fieldValues : listFieldValues )
        {
            strAllDataModified = strAllDataModified + "," + fieldValues.getFieldName(  ) + "=" + "'" +
                fieldValues.getNewValue(  ) + "'";
            strAllDataToModify = strAllDataToModify + AND + fieldValues.getFieldName(  ) + "=" + "'" +
                fieldValues.getOldValue(  ) + "'";
        }

        strAllDataModified = strAllDataModified.substring( 1 );
        strAllDataToModify = strAllDataToModify.substring( 4 );

        DAOUtil daoUtil = new DAOUtil( UPDATE + strTableName + SET + strAllDataModified + WHERE + strAllDataToModify,
                changePool( strPoolName ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a row in a table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listConcatFieldNameAndData the list of concatenation of fields name and data
     * @param connectionService PluginConnectionService object
     */
    public void delete( String strPoolName, String strTableName, List<String> listConcatFieldNameAndData,
        PluginConnectionService connectionService )
    {
        String strAllDataToDelete = "";

        for ( String strConcatFieldNameAndData : listConcatFieldNameAndData )
        {
            strAllDataToDelete = strAllDataToDelete + AND + strConcatFieldNameAndData;
        }

        strAllDataToDelete = strAllDataToDelete.substring( 4 );

        DAOUtil daoUtil = new DAOUtil( DELETE_FROM + strTableName + WHERE + strAllDataToDelete,
                changePool( strPoolName ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Insert the data with user request
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @param strPoolName the name of the pool
     */
    public void insertWithUserRequest( String strUserRequest, PluginConnectionService connectionService,
        String strPoolName )
    {
        DAOUtil daoUtil = new DAOUtil( strUserRequest, changePool( strPoolName ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Find the list of data of a table
     * @param strTableName the name of the table
     * @param strContatFieldNameAndData the list of concatenation of fields name and data
     * @param listFields list of fields
     * @param connectionService PluginConnectionService object
     * @return list of the data
     */
    public List<List<String>> load( String strTableName, String strContatFieldNameAndData, List<Field> listFields,
        PluginConnectionService connectionService )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_A_ROW + strTableName + WHERE + strContatFieldNameAndData );
        daoUtil.executeQuery(  );
        List<List<String>> listDataValueSelectedByPK = new ArrayList(  );
        return listDataValueSelectedByPK;
    }

    /**
     * Find the answer of the user query
     * @param strRequest the request
     * @param connectionService PluginConnectionService object
     * @return list of the data
     */
    private List<List<String>> selectSqlQuery( String strRequest, PluginConnectionService connectionService )
        throws SQLException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = connectionService.getConnection(  );
            statement = connection.prepareStatement( strRequest );

            ResultSet resultSet = statement.executeQuery(  );
            ResultSetMetaData rsmd = resultSet.getMetaData(  );

            ArrayList listRow = new ArrayList(  );

            while ( resultSet.next(  ) )
            {
                String strValue = null;
                ArrayList listLine = new ArrayList(  );

                for ( int i = 1; i <= rsmd.getColumnCount(  ); i++ )
                {
                    if ( resultSet.getObject( rsmd.getColumnName( i ) ) != null )
                    {
                        strValue = resultSet.getObject( rsmd.getColumnName( i ) ).toString(  );
                    }
                    else
                    {
                        strValue = " ";
                    }

                    listLine.add( strValue );
                }

                listRow.add( listLine );
            }

            statement.close(  );
            statement = null;

            return listRow;
        }
        finally
        {
            try
            {
                if ( statement != null )
                {
                    statement.close(  );
                }
            }
            catch ( SQLException e )
            {
                throw new AppException( "SQL Error executing command : " + e.toString(  ) );
            }

            connectionService.freeConnection( connection );
        }
    }

    /**
     * Result of user request
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @return  result of user request
     */
    public List<List<String>> findResultOfUserRequest( String strUserRequest, PluginConnectionService connectionService )
    {
        List<List<String>> listData = new ArrayList<List<String>>(  );
        List<String> listFields = new ArrayList<String>(  );

        try
        {
            listData = selectSqlQuery( strUserRequest, connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return listData;
    }

    /**
     * Find the names of all columns
     * @param strUserRequest user request
     * @param connectionService PluginConnectionService object
     * @param strPoolName the name of the pool
     * @return list of all names of the columns
     */
    public List<String> selectColumnNames( String strUserRequest, PluginConnectionService connectionService,
        String strPoolName )
    {
        List<String> listFields = new ArrayList(  );
        DAOUtil daoUtil = new DAOUtil( strUserRequest, changePool( strPoolName ) );
        daoUtil.executeQuery(  );

        for ( int i = 1; i <= getColumnCount( daoUtil ); i++ )
        {
            String columnName = getColumnName( i, daoUtil );
            listFields.add( columnName );
        }

        daoUtil.free(  );

        return listFields;
    }

    private String getColumnName( int nColumn, DAOUtil daoUtil )
    {
        try
        {
            ResultSetMetaData rsmd = daoUtil.getResultSet(  ).getMetaData(  );

            return rsmd.getColumnName( nColumn );
        }
        catch ( SQLException e )
        {
            daoUtil.free(  );
            throw new AppException( STR_SQL_ERROR + e.toString(  ) );
        }
    }

    private int getColumnCount( DAOUtil daoUtil )
    {
        try
        {
            ResultSetMetaData rsmd = daoUtil.getResultSet(  ).getMetaData(  );

            return rsmd.getColumnCount(  );
        }
        catch ( SQLException e )
        {
            daoUtil.free(  );
            throw new AppException( STR_SQL_ERROR + e.toString(  ) );
        }
    }

    private Plugin changePool( String strPoolName )
    {
        Plugin plugin = PluginService.getPlugin( "adminsql" );
        PluginConnectionService connectionService = new PluginConnectionService( strPoolName );
        connectionService.setPool( strPoolName );
        plugin.setConnectionService( connectionService );

        return plugin;
    }
}
