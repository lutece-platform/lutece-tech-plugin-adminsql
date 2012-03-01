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

import fr.paris.lutece.plugins.adminsql.service.AdminSqlConnectionService;
import fr.paris.lutece.portal.service.database.PluginConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;


import java.util.ArrayList;
import java.util.List;


/**
 * Creates a new instance of TableDAO
 */
public class TableDAO implements ITableDAO
{
    //Constantes
    private static final String NULL_VALUE = "NULL";
    private static final String NOT = "NOT ";
    private static final String CHANGE = "CHANGE";
    private static final String ADD = " ADD ";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String FOREIGN_KEY = "FOREIGN";
    private static final String INDEX_KEY = "INDEX";
    private static final String UNIQUE_KEY = "UNIQUE";
    private static final String DROP = " DROP ";
    private static final String RENAME_TO = " RENAME TO ";
    private static final String WHERE = " WHERE ";
    private static final String SPACE = " ";
    private static final String USER = "-u";
    private static final String PASSWORD = "-p";

    //Requêtes
    private static final String SQL_QUERY_SELECTALL_FIELD_TYPE = " SELECT id_field_type, label_field_type FROM adminsql_field_type";
    private static final String SQL_QUERY_SELECTALL_FIELD_KEY = "SELECT id_field_key, label_field_key FROM adminsql_field_key";
    private static final String SQL_QUERY_SELECTALL_FIELD_NULL = "SELECT id_field_null, label_field_null FROM adminsql_field_null";
    private static final String SQL_QUERY_FIND_FIELD_TYPE_ID = " SELECT id_field_type FROM adminsql_field_type WHERE label_field_type= ?";
    private static final String SQL_QUERY_FIND_FIELD_TYPE_LABEL_BY_ID = " SELECT label_field_type FROM adminsql_field_type WHERE id_field_type= ?";
    private static final String SQL_QUERY_FIND_FIELD_NULL_ID = " SELECT id_field_null FROM adminsql_field_null WHERE label_field_null= ?";
    private static final String SQL_QUERY_FIND_FIELD_NULL_LABEL_BY_ID = " SELECT label_field_null FROM adminsql_field_null WHERE id_field_null= ?";
    private static final String SQL_QUERY_FIND_FIELD_KEY_ID = " SELECT id_field_key FROM adminsql_field_key WHERE label_field_key= ?";
    private static final String SQL_QUERY_FIND_FIELD_KEY_LABEL_BY_ID = " SELECT label_field_key FROM adminsql_field_key WHERE id_field_key= ?";
    private static final String SQL_QUERY_SELECT = "SELECT ";
    private static final String SQL_QUERY_SHOW_DATABASE_TABLES = "SHOW TABLES ";
    private static final String SQL_QUERY_SHOW_DATABASE_TABLES_BY_POOL = "SHOW TABLES FROM ";
    private static final String SQL_QUERY_SELECT_A_TABLE_ON_DATABASE = "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_KEY FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = ? "; //and column_key = 'PRI' ";
    private static final String SQL_QUERY_CREATE_TABLE = " CREATE TABLE ";
    private static final String SQL_QUERY_DELETE_TABLE = " DROP TABLE ";
    private static final String SQL_QUERY_MODIFY_TABLE_NAME = " ALTER TABLE ";
	private static final String SQL_QUERY_SELECT_A_TABLE_ON_DATABASE_1 = "DESC ";

    /**
     * Load a table from a database
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param plugin Plugin adminsql
     * @return the table objet
     */
    public Table load( String strPoolName, String strTableName, Plugin plugin )
    {
        Table table = new Table(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_A_TABLE_ON_DATABASE_1 + strTableName, plugin );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            table.setTableName( strTableName );
            table.setFieldName( daoUtil.getString( 2 ) );
            table.setFieldNull( daoUtil.getString( 3 ) );
            table.setPrimaryKey( daoUtil.getString( 4 ) );
        }

        daoUtil.free(  );

         return table;
    }

    /**
     * Insert a table into a database
     * @param strPoolName the name of the pool
     * @param table the name of the table
     * @param field the name of the field
     * @param plugin Plugin adminsql
     */
    public void insert( String strPoolName, Table table, Field field, Plugin plugin )
    {
	//
        String strFieldName = field.getFieldName(  );
        field.setLabelTypeValue( findFieldTypeLabelbyId( field.getIdTypeValue(  ), plugin, strPoolName ) );

        String strLabelType = field.getLabelTypeValue(  );
        String strFieldLabelType = strLabelType + "(" + field.getLengthTypeValue(  ) + ")";
        field.setLabelNullValue( findFieldNullLabelbyId( field.getIdNullValue(  ), plugin, strPoolName ) );

        String strLabelNullValue = field.getLabelNullValue(  );
        String strFieldLabelNullValue = "";

        if ( strLabelNullValue.equals( "YES" ) )
        {
            strFieldLabelNullValue = NULL_VALUE;
        }
        else
        {
            strFieldLabelNullValue = NOT + NULL_VALUE;
        }

        int nIdKeyValue = field.getIdKeyValue(  );
        field.setLabelKeyValue( findFieldKeyLabelbyId( nIdKeyValue, plugin, strPoolName ) );

        String strFieldLabelKeyValue = field.getLabelKeyValue(  );
        String strKey;

        if ( nIdKeyValue == 1 )
        {
            strKey = "";
        }
        else
        {
            strKey = PRIMARY_KEY;
        }

        String strFieldLabelDefaultValue = field.getDefaultValue(  );
        String strDEFAULT = "";

        if ( strFieldLabelDefaultValue.equals( "" ) )
        {
            strDEFAULT = "";
        }
        else
        {
            strDEFAULT = "DEFAULT";
            strFieldLabelDefaultValue = " '" + strFieldLabelDefaultValue + "' ";
        }
		 String strTableName = table.getTableName(  );
System.out.println(SQL_QUERY_CREATE_TABLE + strTableName + "(" + strFieldName + " " +
                strFieldLabelType + " " + strKey + " " + strFieldLabelNullValue + " " + strDEFAULT +
                strFieldLabelDefaultValue + ")");
       
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CREATE_TABLE + strTableName + "(" + strFieldName + " " +
                strFieldLabelType + " " + strKey + " " + strFieldLabelNullValue + " " + strDEFAULT +
                strFieldLabelDefaultValue + ")", plugin );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Store a table into a database
     * @param strTableNameToModify the name of the table to modify
     * @param table Table object
     */
    public void store( String strTableNameToModify, Table table )
    {
        String strTableName = table.getTableName(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MODIFY_TABLE_NAME + strTableName + RENAME_TO + strTableNameToModify );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a table from a database
     * @param strTableName the name of the table
     * @param plugin Plugin adminsql
     */
    public void delete( String strTableName, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_TABLE + strTableName, plugin );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Find the list of the tables on a database
     * @param plugin Plugin adminsql
     * @return the list of the tables on a database
     */
    public List<Table> selectTableList( Plugin plugin )
    {
        List<Table> tableList = new ArrayList<Table>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SHOW_DATABASE_TABLES, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Table table = new Table(  );
            table.setTableName( daoUtil.getString( 1 ) );
            tableList.add( table );
        }

        daoUtil.free(  );

        return tableList;
    }

    /**
     * Find the list of the tables on a database
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsql
     * @return the list of the tables on a database
     */
    public List<Table> selectTableListByPool( String strPoolName, Plugin plugin )
    {
        plugin.setConnectionService( AdminSqlConnectionService.getInstance(  ).getConnectionService( strPoolName ) );

        List<Table> tableList = new ArrayList<Table>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SHOW_DATABASE_TABLES, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Table table = new Table(  );
            table.setTableName( daoUtil.getString( 1 ) );
            tableList.add( table );
        }

        daoUtil.free(  );

        return tableList;
    }

    /**
     * lists the field list possiblilities to create or modify fields form
     * @return Field list
     * @param plugin plugin adminsql
     */
    public ReferenceList selectFieldTypeList( Plugin plugin )
    {
        ReferenceList fieldtypeList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FIELD_TYPE, plugin );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ReferenceItem item = new ReferenceItem(  );
            item.setCode( daoUtil.getString( 1 ) );
            item.setName( daoUtil.getString( 2 ) );
            fieldtypeList.add( item );
        }

        daoUtil.free(  );

        return fieldtypeList;
    }

    /**
     * lists the field list possiblilities to create or modify fields form
     *
     * @return the keys choice adminsql database
     * @param plugin Plugin adminsql
     */
    public ReferenceList selectFieldKeyList( Plugin plugin )
    {
        ReferenceList fieldkeyList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FIELD_KEY, plugin );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ReferenceItem item = new ReferenceItem(  );
            item.setCode( daoUtil.getString( 1 ) );
            item.setName( daoUtil.getString( 2 ) );
            fieldkeyList.add( item );
        }

        daoUtil.free(  );

        return fieldkeyList;
    }

    /**
     * lists the field list possiblilities to create or modify fields form
     *
     * @return the null value choice adminsql database
     * @param plugin Plugin adminsql
     */
    public ReferenceList selectFieldNullList( Plugin plugin )
    {
        ReferenceList fieldnullList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FIELD_NULL, plugin );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ReferenceItem item = new ReferenceItem(  );
            item.setCode( daoUtil.getString( 1 ) );
            item.setName( daoUtil.getString( 2 ) );
            fieldnullList.add( item );
        }

        daoUtil.free(  );

        return fieldnullList;
    }

    /**
     * Find the field id type by label type from adminsql database
     * @param strLabelType the name of the type that user choose
     * @param plugin Plugin adminsql
     * @param strPoolName the name of the pool
     * @return id type from label type
     */
    public int findFieldTypeIdbyLabel( String strLabelType, Plugin plugin, String strPoolName )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_FIELD_TYPE_ID, getDefaultPool( plugin ) );
        daoUtil.setString( 1, strLabelType );
        daoUtil.executeQuery(  );

        int nIdFieldType = 0;

        if ( daoUtil.next(  ) )
        {
            nIdFieldType = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return nIdFieldType;
    }

    /**
     * Find the field label type by id type from adminsql database
     * @param nId the id of type label
     * @param plugin Plugin adminsql
     * @param strPoolName name of the pool
     * @return the label of the type by id
     */
    public String findFieldTypeLabelbyId( int nId, Plugin plugin, String strPoolName )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_FIELD_TYPE_LABEL_BY_ID, getDefaultPool( plugin ) );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        String strFieldTypeLabel = "";

        if ( daoUtil.next(  ) )
        {
            strFieldTypeLabel = daoUtil.getString( 1 );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return strFieldTypeLabel;
    }

    /**
     * Find the field id null by label null from adminsql database
     * @param strLabelNull the label of the null value
     * @param plugin Plugin adminsql
     * @param strPoolName the name of the pool
     * @return id null from null type
     */
    public int findFieldNullIdbyLabel( String strLabelNull, Plugin plugin, String strPoolName )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_FIELD_NULL_ID, getDefaultPool( plugin ) );
        daoUtil.setString( 1, strLabelNull );
        daoUtil.executeQuery(  );

        int nIdFieldNull = 0;

        if ( daoUtil.next(  ) )
        {
            nIdFieldNull = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return nIdFieldNull;
    }

    /**
     * Find the field id null by label null from adminsql database
     * @param nId the id of the null value
     * @param plugin Plugin adminsql
     * @param strPoolName the name of the pool
     * @return the label of null value by id
     */
    public String findFieldNullLabelbyId( int nId, Plugin plugin, String strPoolName )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_FIELD_NULL_LABEL_BY_ID, getDefaultPool( plugin ) );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        String strFieldNullLabel = "";

        if ( daoUtil.next(  ) )
        {
            strFieldNullLabel = daoUtil.getString( 1 );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return strFieldNullLabel;
    }

    /**
     * Find the field id key by label key from adminsql database
     * @param strLabelKey the label key of the field
     * @param plugin Plugin adminsql
     * @param strPoolName the name of the pool
     * @return id key from label key
     */
    public int findFieldKeyIdbyLabel( String strLabelKey, Plugin plugin, String strPoolName )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_FIELD_KEY_ID, getDefaultPool( plugin ) );
        daoUtil.setString( 1, strLabelKey );
        daoUtil.executeQuery(  );

        int nIdFieldKey = 0;

        if ( daoUtil.next(  ) )
        {
            nIdFieldKey = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return nIdFieldKey;
    }

    /**
     * Find the field id key by label key from adminsql database
     * @param nId the id of the key
     * @param plugin Plugin adminsql
     * @param strPoolName the name of the pool
     * @return the label of key by id
     */
    public String findFieldKeyLabelbyId( int nId, Plugin plugin, String strPoolName )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_FIELD_KEY_LABEL_BY_ID, getDefaultPool( plugin ) );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        String strFieldKeyLabel = "";

        if ( daoUtil.next(  ) )
        {
            strFieldKeyLabel = daoUtil.getString( 1 );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return strFieldKeyLabel;
    }

    private Plugin changePool( String strPoolName )
    {
        Plugin plugin = PluginService.getPlugin( "adminsql" );

        // plugin.setPoolName(strPoolName );
        PluginConnectionService connectionService = new PluginConnectionService( strPoolName );
        connectionService.setPool( strPoolName );
        plugin.setConnectionService( connectionService );

        return plugin;
    }

    private Plugin getDefaultPool( Plugin plugin )
    {
        PluginConnectionService connectionService = new PluginConnectionService( plugin.getDbPoolName(  ) );
        connectionService.setPool( "adminsql" );
        plugin.setConnectionService( connectionService );

        return plugin; //TODO in properties
    }
}
