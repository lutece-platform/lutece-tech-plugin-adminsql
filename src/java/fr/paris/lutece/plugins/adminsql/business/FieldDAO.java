/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.adminsql.util.AdminSqlUtil;
import fr.paris.lutece.portal.service.database.PluginConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;


import java.util.ArrayList;
import java.util.List;


/**
 * Creates a new instance of DataDAO
 */
public class FieldDAO implements IFieldDAO
{
    //Constantes
    private static final String NULL_VALUE = "NULL";
    private static final String NOT = "NOT ";
    private static final String CHANGE = "CHANGE";
    private static final String ADD = " ADD ";
    private static final String ADD_KEY = " ,ADD ";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String FOREIGN_KEY = "FOREIGN KEY";
    private static final String INDEX_KEY = "INDEX";
    private static final String UNIQUE_KEY = "UNIQUE";
    private static final String DROP = " DROP ";
    private static final String DROP_KEY = " ,DROP ";
    private static final String POOL_ADMINSQL = "adminsql";

    //Requêtes
    private static final String SQL_QUERY_SHOW_TABLE_FIELDS_STRUCTURE = " SHOW COLUMNS FROM ";
    private static final String SQL_QUERY_SELECT_A_FIELD_ON_TABLE = "DESC ";
    private static final String SQL_QUERY_SELECTALL_FIELD_TYPE = " SELECT id_field_type, label_field_type FROM adminsql_field_type";
    private static final String SQL_QUERY_SELECTALL_FIELD_KEY = "SELECT id_field_key, label_field_key FROM adminsql_field_key";
    private static final String SQL_QUERY_SELECTALL_FIELD_NULL = "SELECT id_field_null, label_field_null FROM adminsql_field_null";
    private static final String SQL_QUERY_FIND_FIELD_TYPE_ID = " SELECT id_field_type FROM adminsql_field_type WHERE label_field_type= ?";
    private static final String SQL_QUERY_FIND_FIELD_TYPE_LABEL_BY_ID = " SELECT label_field_type FROM adminsql_field_type WHERE id_field_type= ?";
    private static final String SQL_QUERY_FIND_FIELD_NULL_ID = " SELECT id_field_null FROM adminsql_field_null WHERE label_field_null= ?";
    private static final String SQL_QUERY_FIND_FIELD_NULL_LABEL_BY_ID = " SELECT label_field_null FROM adminsql_field_null WHERE id_field_null= ?";
    private static final String SQL_QUERY_FIND_FIELD_KEY_ID = " SELECT id_field_key FROM adminsql_field_key WHERE label_field_key= ?";
    private static final String SQL_QUERY_FIND_FIELD_KEY_LABEL_BY_ID = " SELECT label_field_key FROM adminsql_field_key WHERE id_field_key= ?";
    private static final String SQL_QUERY_UPDATE = " ALTER TABLE ";
    private static final String SQL_QUERY_DELETE_FIELD = " ALTER TABLE ";

    /**
     * Load a field from the table
     * @param strTableName the name of the table
     * @param strFieldName the name of the field
     * @param plugin adminsql plugin
     * @param strPoolName the name of the pool
     * @return a field object
     */
    public Field load( String strPoolName, String strTableName, String strFieldName, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_A_FIELD_ON_TABLE + strTableName + " " + strFieldName, plugin );
        daoUtil.executeQuery(  );

        Field field = new Field(  );
        String[] tabString = { "", "" };

        if ( daoUtil.next(  ) )
        {
            field.setFieldName( daoUtil.getString( 1 ) );
            field.setTypeValue( daoUtil.getString( 2 ) );

            String strFieldType = field.getTypeValue(  );

            if ( strFieldType.equals( "date" ) || strFieldType.equals( "datetime" ) || strFieldType.equals( "time" ) ||
                    strFieldType.equals( "timestamp" ) || strFieldType.equals( "text" ) ||
                    strFieldType.equals( "tinytext" ) || strFieldType.equals( "longtext" ) ||
                    strFieldType.equals( "mediumtext" ) || strFieldType.equals( "blob" ) ||
                    strFieldType.equals( "tinyblob" ) || strFieldType.equals( "longblob" ) ||
                    strFieldType.equals( "mediumblob" ) )
            {
                field.setLabelTypeValue( strFieldType );
                field.setLengthTypeValue( "" );
                field.setIdTypeValue( findFieldTypeIdbyLabel( strFieldType, plugin, strPoolName ) );
            }
            else
            {
                tabString = AdminSqlUtil.getFieldDetails( field.getTypeValue(  ) );
                field.setLabelTypeValue( tabString[0] );
                field.setLengthTypeValue( tabString[1] );
                field.setIdTypeValue( findFieldTypeIdbyLabel( tabString[0], plugin, strPoolName ) );
            }

            field.setLabelNullValue( daoUtil.getString( 3 ) );

            int nFieldIdNull = findFieldNullIdbyLabel( daoUtil.getString( 3 ), plugin, strPoolName );
            field.setIdNullValue( nFieldIdNull );

            String strLabelKeyValue = daoUtil.getString( 4 );

            if ( strLabelKeyValue.equals( "PRI" ) )
            {
                strLabelKeyValue = "PRIMAIRE";
            }

            int nFieldIdKey = findFieldKeyIdbyLabel( strLabelKeyValue, plugin, strPoolName );
            field.setIdKeyValue( nFieldIdKey );

            if ( daoUtil.getString( 5 ) == null )
            {
                field.setDefaultValue( NULL_VALUE );
            }
            else
            {
                field.setDefaultValue( daoUtil.getString( 5 ) );
            }
        }

        daoUtil.free(  );

        return field;
    }

    /**
     * Store a field in the table
     * @param strTableName the name of the table
     * @param strFieldNameToModify the name of the field to modify
     * @param field Field object
     * @param plugin adminsql plugin
     * @param strPoolName the name of the pool
     * @param nIdOldFieldKey find the key of the field if exists
     */
    public void store( String strPoolName, String strTableName, Field field, String strFieldNameToModify,
        int nIdOldFieldKey, Plugin plugin )
    {
        String strFieldName = field.getFieldName(  );
        field.setLabelTypeValue( findFieldTypeLabelbyId( field.getIdTypeValue(  ), plugin, strPoolName ) );

        String strFieldType = field.getLabelTypeValue(  );
        String strFieldLabelType = "";
        String strLengthTypeValue = field.getLengthTypeValue(  );

        if ( strLengthTypeValue.equals( "" ) || ( strLengthTypeValue == null ) )
        {
            strFieldLabelType = strFieldType;
        }
        else if ( strFieldType.equals( "year" ) )
        {
            strFieldLabelType = strFieldType + "(" + 4 + ")";
        }
        else
        {
            strFieldLabelType = strFieldType + "(" + field.getLengthTypeValue(  ) + ")";
        }

        field.setLabelNullValue( findFieldNullLabelbyId( field.getIdNullValue(), plugin, strPoolName ) );

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
        String strKey = "";
        String strNewPrimaryKey = "";

        if ( ( nIdOldFieldKey == 1 ) && ( nIdKeyValue == 2 ) )
        {
            strKey = ADD_KEY + PRIMARY_KEY;
            strNewPrimaryKey = "(" + strFieldName + ")";
        }
        else if ( ( nIdOldFieldKey == 2 ) && ( nIdKeyValue == 1 ) )
        {
            strKey = DROP_KEY + PRIMARY_KEY;
        }

        String strFieldLabelDefaultValue = field.getDefaultValue(  );
        String strDEFAULT;
        String strFieldLabelDefaultValueFinal;

        if ( strFieldLabelDefaultValue.equals( "" ) )
        {
            strDEFAULT = "";
            strFieldLabelDefaultValueFinal = "";
        }
        else
        {
            strDEFAULT = "DEFAULT";
            strFieldLabelDefaultValueFinal = " '" + strFieldLabelDefaultValue + "'";
        }

        String strAlterQuery = SQL_QUERY_UPDATE + " " + strTableName + " " + CHANGE + " " + strFieldNameToModify + " " +
            strFieldName + " " + strFieldLabelType + " " + " " + strFieldLabelNullValue + " " + strDEFAULT +
            strFieldLabelDefaultValueFinal + " " + strKey + strNewPrimaryKey;
        DAOUtil daoUtil = new DAOUtil( strAlterQuery, plugin );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Insert a field in the table
     * @param strPoolName the name of the pool
     * @param field Field object
     * @param strTableName the name of the table
     * @param plugin adminsql plugin
     */
    public void insert( String strPoolName, String strTableName, Field field, Plugin plugin )
    {
        String strFieldName = field.getFieldName(  );
        field.setLabelTypeValue( findFieldTypeLabelbyId( field.getIdTypeValue(  ), plugin, strPoolName ) );

        String strFieldLabelType = "";
        String strFieldType = field.getLabelTypeValue(  );
        String strLengthTypeValue = field.getLengthTypeValue(  );

        if ( strLengthTypeValue.equals( "" ) || ( strLengthTypeValue == null ) )
        {
            strFieldLabelType = strFieldType;
        }
        else if ( strFieldType.equals( "YEAR" ) )
        {
            strFieldLabelType = strFieldType + "(" + 4 + ")";
        }
        else
        {
            strFieldLabelType = strFieldType + "(" + field.getLengthTypeValue(  ) + ")";
        }

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

        int nAddFieldEndOfTable = field.getFieldEndOfTable(  );
        int nAddFieldBeginningOfTable = field.getFieldBeginningOfTable(  );
        int nAddFieldAfterAField = field.getFieldAfterAField(  );
        int nPlaceOfField = field.getPlaceOfField(  );
        String strFIRST = "";

        if ( nPlaceOfField == 2 )
        {
            strFIRST = "FIRST";
        }
        else
        {
            strFIRST = "";
        }

        String strAfterTheField = "";
        String strAFTER = "";

        if ( nPlaceOfField == 3 )
        {
            strAFTER = "AFTER";
            strAfterTheField = field.getAfterTheField(  );
        }
        else
        {
            strAFTER = "";
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE + strTableName + " " + ADD + " " + strFieldName + " " +
                strFieldLabelType + " " + strKey + " " + strFieldLabelNullValue + " " + strDEFAULT +
                strFieldLabelDefaultValue + strFIRST + strAFTER + " " + strAfterTheField, plugin );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a field from the table
     * @param strTableName the name of the table
     * @param strFieldName the name of the field
     * @param plugin adminsql plugin
     */
    public void delete( String strTableName, String strFieldName, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FIELD + strTableName + DROP + strFieldName );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Find the list of fields from a table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param plugin Plugin adminsql
     * @return list of fields
     */
    public List<Field> selectFieldList( String strPoolName, String strTableName, Plugin plugin )
    {
        List<Field> fieldList = new ArrayList<Field>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SHOW_TABLE_FIELDS_STRUCTURE + strTableName, changePool( strPoolName ) );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Field field = new Field(  );
            field.setFieldName( daoUtil.getString( 1 ) );
            field.setTypeValue( daoUtil.getString( 2 ) );
            field.setLabelNullValue( daoUtil.getString( 3 ) );

            int nFieldIdNull = findFieldNullIdbyLabel( daoUtil.getString( 3 ), plugin, strPoolName );
            field.setIdNullValue( nFieldIdNull );
            field.setLabelKeyValue( daoUtil.getString( 4 ) );

            int nFieldIdKey = findFieldKeyIdbyLabel( daoUtil.getString( 4 ), plugin, strPoolName );
            field.setIdKeyValue( nFieldIdKey );

            if ( daoUtil.getString( 5 ) == null )
            {
                field.setDefaultValue( NULL_VALUE );
            }
            else
            {
                field.setDefaultValue( daoUtil.getString( 5 ) );
            }

            fieldList.add( field );
        }

        daoUtil.free(  );

        return fieldList;
    }

    /**
     * lists the field list possiblilities to create or modify fields form
     * @return Field list
     * @param strPoolName the name of the pool
     * @param plugin plugin adminsql
     */
    public ReferenceList selectFieldTypeList( String strPoolName, Plugin plugin )
    {
        ReferenceList fieldtypeList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FIELD_TYPE, getDefaultPool( plugin ) );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ReferenceItem item = new ReferenceItem(  );
            item.setCode( daoUtil.getString( 1 ) );
            item.setName( daoUtil.getString( 2 ) );
            fieldtypeList.add( item );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return fieldtypeList;
    }

    /**
     * lists the field list possiblilities to create or modify fields form
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsql
     * @return the keys choice adminsql database
     */
    public ReferenceList selectFieldKeyList( String strPoolName, Plugin plugin )
    {
        ReferenceList fieldkeyList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FIELD_KEY, getDefaultPool( plugin ) );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ReferenceItem item = new ReferenceItem(  );
            item.setCode( daoUtil.getString( 1 ) );
            item.setName( daoUtil.getString( 2 ) );
            fieldkeyList.add( item );
        }

        daoUtil.free(  );
        plugin = changePool( strPoolName );

        return fieldkeyList;
    }

    /**
     * lists the field list possiblilities to create or modify fields form
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsql
     * @return the null value choice adminsql database
     */
    public ReferenceList selectFieldNullList( String strPoolName, Plugin plugin )
    {
        ReferenceList fieldnullList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FIELD_NULL, getDefaultPool( plugin ) );

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
