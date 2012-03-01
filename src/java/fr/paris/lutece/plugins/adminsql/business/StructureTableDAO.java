/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class provides Data Access methods for StructureTable objects
 */
public final class StructureTableDAO implements IStructureTableDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( fieldValue ) FROM table_name";
    private static final String SQL_QUERY_SELECT = "SELECT fieldValue, typeValue, nullValue, keyValue, defaultValue, extraValue FROM table_name WHERE fieldValue = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO table_name ( fieldValue, typeValue, nullValue, keyValue, defaultValue, extraValue ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM table_name WHERE fieldValue = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE table_name SET fieldValue = ?, typeValue = ?, nullValue = ?, keyValue = ?, defaultValue = ?, extraValue = ? WHERE fieldValue = ?";

    //Requests
    private static final String SQL_QUERY_SHOW_TABLES = "SHOW TABLES ";
    private static final String SQL_QUERY_SHOW_COLUMNS = "SHOW COLUMNS FROM ";
    private static final String SQL_QUERY_SELECT_DATA = "SELECT * FROM ";
    private static final String SQL_QUERY_SELECT_FIELDS = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.`COLUMNS` C where c.table_name='";
    private static final String SQL_QUERY_SELECT_FIELD = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '";

    public List<List<String>> selectAllTables( PluginConnectionService connectionService )
    {
        List<List<String>> list = new ArrayList<List<String>>(  );

        try
        {
            list = selectSqlQuery( SQL_QUERY_SHOW_TABLES, connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return list;
    }

    public List<List<String>> selectAllColumns( PluginConnectionService connectionService, String strTableName )
    {
        List<List<String>> list = new ArrayList<List<String>>(  );

        try
        {
            list = selectSqlQuery( SQL_QUERY_SHOW_COLUMNS + strTableName, connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return list;
    }

    public List<List<String>> selectSqlQuery( String strRequest, PluginConnectionService connectionService )
        throws SQLException
    {
        Connection connection = null;
        PreparedStatement statement = null;
        String strSQL = strRequest;

        try
        {
            connection = connectionService.getConnection(  );
            statement = connection.prepareStatement( strSQL );

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

    public List<List<String>> selectAllData( PluginConnectionService connectionService, String strTableName )
    {
        List<List<String>> listData = new ArrayList<List<String>>(  );

        try
        {
            listData = selectSqlQuery( SQL_QUERY_SELECT_DATA + strTableName, connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return listData;
    }

    public List<List<String>> selectAllFields( PluginConnectionService connectionService, String strTableName )
    {
        List<List<String>> listFields = new ArrayList<List<String>>(  );

        try
        {
            listFields = selectSqlQuery( SQL_QUERY_SELECT_FIELDS + strTableName + "'", connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return listFields;
    }

    public List<List<String>> selectAField( PluginConnectionService connectionService, String strTableName,
        String strFieldName )
    {
        List<List<String>> listField = new ArrayList<List<String>>(  );

        try
        {
            listField = selectSqlQuery( SQL_QUERY_SELECT_FIELD + "'" + strTableName + "AND COLUMN_NAME = " +
                    strFieldName + "'", connectionService );
        }
        catch ( SQLException sql )
        {
            AppLogService.error( sql.getMessage(  ), sql );
        }

        return listField;
    }
}
