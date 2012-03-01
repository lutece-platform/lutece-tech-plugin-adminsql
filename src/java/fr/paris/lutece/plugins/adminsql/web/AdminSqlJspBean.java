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
package fr.paris.lutece.plugins.adminsql.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.adminsql.business.DataHome;
import fr.paris.lutece.plugins.adminsql.business.Field;
import fr.paris.lutece.plugins.adminsql.business.FieldHome;
import fr.paris.lutece.plugins.adminsql.business.FieldValues;
import fr.paris.lutece.plugins.adminsql.business.Table;
import fr.paris.lutece.plugins.adminsql.business.TableHome;
import fr.paris.lutece.plugins.adminsql.service.AdminSqlConnectionService;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 * Management of the sql server
 */
public class AdminSqlJspBean extends PluginAdminPageJspBean
{
    //Rights
    public static final String RIGHT_MANAGE_ADMIN_SQL = "ADMIN_SQL_MANAGEMENT";

    //Messages
    private static final String MESSAGE_TITLE_INVALID_CHARACTERS = "portal.site.message.invalidCharactersInTitleName";
    private static final String MESSAGE_ERROR_FIELD_NAME = "adminsql.create_field.errorFieldName";
    private static final String MESSAGE_ERROR_FIELD_TYPE_LENGTH = "adminsql.create_field.errorFieldTypeLength";
    private static final String MESSAGE_ERROR_TABLE_NAME = "adminsql.create_table.errorTableName";
    private static final String MESSAGE_ERROR_NULL_AND_PRIMARY_KEY = "adminsql.create_field.errorNullAndPrimaryKey";

    //Properties
    private static final String PROPERTY_PAGE_TITLE_POOL_LIST = "adminsql.manage_databases.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_TABLE_LIST = "adminsql.manage_tables.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FIELDS_STRUCTURE = "adminsql.manage_fields_structure.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_DATA_TABLE = "adminsql.manage_data.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DATA = "adminsql.create_data.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DATA = "adminsql.modify_data.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FIELD_STRUCTURE = "adminsql.modify_field_structure.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FIELD = "adminsql.create_field.pageTitle";
    private static final String PROPERTY_CONFIRM_DELETE_FIELD = "adminsql.delete_field.confirm_delete_field";
    private static final String PROPERTY_CONFIRM_MODIFY_FIELD = "adminsql.modify_field.confirm_modify_field";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TABLE = "adminsql.create_table.pageTitle";
    private static final String PROPERTY_CONFIRM_DELETE_TABLE = "adminsql.delete_table.confirm_delete_table";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TABLE_NAME = "adminsql.modify_table_name.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_FONCTIONNALITIES_LIST = "adminsql.choose_fonctionnality.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_USER_REQUEST = "adminsql.user_request.pageTitle";
    private static final String PROPERTY_CONFIRM_DELETE_DATA = "adminsql.delete_data.confirm_delete_data";

    //Parameters
    private static final String PARAMETER_POOL_NAME = "pool_name";
    private static final String PARAMETER_TABLE_NAME = "table_name";
    private static final String PARAMETER_TABLE_NAME_TO_MODIFY = "tableNameToModify";
    private static final String PARAMETER_TABLE_COMMENT = "table_comment";
    private static final String PARAMETER_DATA_ID = "data_id";
    private static final String PARAMETER_FIELD_NAME = "field_name";
    private static final String PARAMETER_LABEL_TYPE_VALUE = "type_value_id";
    private static final String PARAMETER_NULL_VALUE = "null_value";
    private static final String PARAMETER_FIELD_LABEL_TYPE = "field_label_type";
    private static final String PARAMETER_FIELD_LENGTH_TYPE = "field_length_type";
    private static final String PARAMETER_FIELD_DEFAULT_VALUE = "field_default_value";
    private static final String PARAMETER_FIELD_NULL = "field_null_value";
    private static final String PARAMETER_FIELD_COMMENT = "field_comment_value";
    private static final String PARAMETER_FIELD_KEY = "field_key_value";
    private static final String PARAMETER_FIELD_TO_MODIFY = "fieldToModify";
    private static final String PARAMETER_ID_OLD_FIELD_KEY = "idOldFieldKey";
    private static final String PARAMETER_ID_FIELD_TYPE = "field_type_id";
    private static final String PARAMETER_ID_FIELD_NULL = "field_null_id";
    private static final String PARAMETER_ID_FIELD_KEY = "field_key_id";
    private static final String PARAMETER_FIELDS_NUMBER_ADDING = "fields_number_adding";
    private static final String PARAMETER_PLACE_OF_FIELD = "place_of_new_field";
    private static final String PARAMETER_AFTER_THE_FIELD = "choose_fields";
    private static final String PARAMETER_NUMBER_FIELDS_TABLE = "number_fields_table";
    private static final String PARAMETER_DATA = "data";
    private static final String PARAMETER_CONCAT_FIELDNAME_DATA = "concat_fielname_data";
    private static final String PARAMETER_USER_REQUEST = "user_request";
    private static final String PARAMETER_DB_TYPE_NAME = "db_type_name";
    private static final String PARAMETER_IDENTIFIANT_USER = "identifiant";
    private static final String PARAMETER_PASSWORD_USER = "password";
    private static final String PARAMETER_ROW_TO_MODIFY = "row_to_modify";
    private static final String PARAMETER_CHECKBOX_STRUCTURE = "checkbox_structure";
    private static final String PARAMETER_CHECKBOX_DATA = "checkbox_data";
    private static final String PARAMETER_CHECKBOX_DATABASE = "checkbox_database";
    private static final String PARAMETER_ERROR_MESSAGE = "error_mess";

    //Markers
    private static final String MARK_POOL_LIST = "pool_list";
    private static final String MARK_TABLE_LIST = "table_list";
    private static final String MARK_DATA_LIST = "data_list";
    private static final String MARK_FIELDS_TABLE = "field_list";
    private static final String MARK_DATA_LIST_TO_MODIFY = "data_to_modify_list";
    private static final String MARK_PRIMARY_KEY_LIST = "primary_key_list";
    private static final String MARK_POOL_NAME = "pool_name";
    private static final String MARK_TABLE = "table";
    private static final String MARK_FIELD = "field";
    private static final String MARK_FIELD_TYPE_REFERENCE_LIST = "field_type_list";
    private static final String MARK_FIELD_KEY_REFERENCE_LIST = "field_key_list";
    private static final String MARK_FIELD_NULL_REFERENCE_LIST = "field_null_list";
    private static final String MARK_FIELD_NAME_REFERENCE_LIST = "field_name_list";
    private static final String MARK_NEW_FIELD = "place_of_new_field";
    private static final String MARK_AFTER_THE_FIELD = "choose_fields";
    private static final String MARK_NUMBER_FIELDS_TABLE = "number_fields_table";
    private static final String MARK_CONCAT_PRIMARY_KEY_AND_VALUE = "concat_primary_key_and_value";
    private static final String MARK_CONCAT_FIELDNAME_DATA = "concat_fielname_data";
    private static final String MARK_USER_REQUEST = "user_request";
    private static final String MARK_FIELDS_NAMES = "list_fields_names";
    private static final String MARK_DATABASE_TYPE_LIST = "list_database_type";
    private static final String MARK_DB_TYPE_NAME = "db_type_name";
    private static final String MARK_SQL_ERROR = "sql_error";
    private static final String MARK_DUMP_SCRIPT = "dump_script";
    private static final String MARK_INPUT_CONCAT_FIELD_NAME_AND_DATA = "input_concat_field_name_data";
    private static final String MARK_DUMP_COMMANDS = "dump_commands";
    private static final String MARK_APPEXCEPTION_ERROR = "error_sql";

    //Error messages
    private static final String MESSAGE_SQL_ERROR_IN_QUERY = "adminsql.message.sqlErrorInQuery";

    //Jsps
    private static final String JSP_URL_MANAGE_FIELDS_STRUCTURES = "ManageFieldsStructure.jsp";
    private static final String JSP_URL_MANAGE_TABLES = "ManageTables.jsp";
    private static final String JSP_DO_DELETE_FIELD_STRUCTURE = "jsp/admin/plugins/adminsql/DoDeleteFieldStructure.jsp";
    private static final String JSP_DO_DELETE_TABLE = "jsp/admin/plugins/adminsql/DoDeleteTable.jsp";
    private static final String JSP_DO_MODIFY_FIELD_STRUCTURE = "jsp/admin/plugins/adminsql/DoMofifyFieldStructure.jsp";
    private static final String JSP_URL_MANAGE_DATA_TABLE = "ManageData.jsp";
    private static final String JSP_URL_MANAGE_DATA_TABLE_AFTER_MODIF = "ManageData.jsp";
    private static final String JSP_DO_DELETE_DATA = "jsp/admin/plugins/adminsql/DoDeleteData.jsp";
    private static final String JSP_CREATE_TABLE = "jsp/admin/plugins/adminsql/CreateTable.jsp";

    // Properties file definition
    private static final String PROPERTY_DATABASE_TYPE_LIST = "adminsql.type.database.list";

    //Urls
    private static final String URL_CREATION_FIELD = "jsp/admin/plugins/adminsql/CreateFieldStructure.jsp";
    private static final String URL_MODIFY_FIELD = "jsp/admin/plugins/adminsql/ModifyFieldStructure.jsp";
    private static final String URL_CREATE_DATA = "jsp/admin/plugins/adminsql/CreateData.jsp";
    private static final String URL_MODIFY_DATA = "jsp/admin/plugins/adminsql/ModifyData.jsp";
    private static final String URL_USER_REQUEST = "jsp/admin/plugins/adminsql/UserRequest.jsp";
    private static final String URL_DO_USER_REQUEST_ERROR = "jsp/admin/plugins/adminsql/DoUserRequestError.jsp";


    //Templates
    private String TEMPLATE_MANAGE_POOL_SQL = "admin/plugins/adminsql/manage_databases.html";
    private String TEMPLATE_MANAGE_TABLE_SQL = "admin/plugins/adminsql/manage_tables.html";
    private String TEMPLATE_MANAGE_DATA_SQL = "admin/plugins/adminsql/manage_data.html";
    private String TEMPLATE_CREATE_DATA = "admin/plugins/adminsql/create_data.html";
    private String TEMPLATE_MANAGE_STRUCTURE_SQL = "admin/plugins/adminsql/manage_fields_structure.html";
    private String TEMPLATE_MODIFY_DATA = "admin/plugins/adminsql/modify_data.html";
    private String TEMPLATE_MODIFY_STRUCTURE = "admin/plugins/adminsql/modify_field_structure.html";
    private String TEMPLATE_CREATE_STRUCTURE = "admin/plugins/adminsql/create_field.html";
    private String TEMPLATE_CREATE_TABLE = "admin/plugins/adminsql/create_table.html";
    private String TEMPLATE_MODIFY_TABLE_NAME = "admin/plugins/adminsql/modify_table_name.html";
    private String TEMPLATE_CHOOSE_FONCTIONNALITIES_LIST = "admin/plugins/adminsql/fonctionnalities_list_on_database.html";
    private String TEMPLATE_USER_REQUEST = "admin/plugins/adminsql/user_request.html";
    private String TEMPLATE_RESULT_OF_USER_REQUEST = "admin/plugins/adminsql/result_of_user_request.html";
    private String TEMPLATE_EXPORT_DATABASE = "admin/plugins/adminsql/export_database.html";
    private String TEMPLATE_SQL_ERROR_PAGE = "admin/plugins/adminsql/sql_error_page.html";
    private String TEMPLATE_PARAMETER_TO_EXPORT_DATABASE = "admin/plugins/adminsql/parameter_to_export_db.html";
    private String TEMPLATE_DUMP_DATABASE = "admin/plugins/adminsql/dump_database.html";
    private String TEMPLATE_USER_REQUEST_ERROR = "admin/plugins/adminsql/error_user_request.html";

    /**
     * List of databases
     * @param request HttpServletRequest
     * @return the list of databases page
     */
    public String getManageDatabases( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_POOL_LIST );

        ReferenceList listPools = new ReferenceList(  );
        AppConnectionService.getPoolList( listPools );

        HashMap model = new HashMap(  );
        model.put( MARK_POOL_LIST, listPools );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_POOL_SQL, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * List of fonctionnalities on the pool
     * @param request HttpServletRequest
     * @return list of fonctionnalities
     */
    public String getManageFonctionnalitiesList( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_FONCTIONNALITIES_LIST );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        HashMap model = new HashMap(  );
        model.put( MARK_POOL_NAME, strPoolName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CHOOSE_FONCTIONNALITIES_LIST, getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * List all tables of a database
     * @param request HttpServletRequest
     * @return List of all tables of the databases
     */
    public String getManageTables( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_TABLE_LIST );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        List<Table> listTables = TableHome.findAllByPool( strPoolName, getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_TABLE_LIST, listTables );
        model.put( MARK_POOL_NAME, strPoolName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_TABLE_SQL, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * User can stamp his request
     * @param request HttpServletRequest
     * @return user request page
     */
    public String getUserRequest( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_USER_REQUEST );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_POOL_NAME, strPoolName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_USER_REQUEST, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * User can see the result of his request
     * @param request HttpServletRequest
     * @return result of the request
     */
    public String doUserRequest( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strUserRequest = request.getParameter( PARAMETER_USER_REQUEST );
        List<List<String>> listData = new ArrayList<List<String>>(  );
        List<String> listFieldsNames = new ArrayList<String>(  );
        strUserRequest = strUserRequest.trim(  );

        String strSqlRequest = strUserRequest;
        strSqlRequest = strUserRequest.toLowerCase(  );

        if ( strSqlRequest.contains( "select" ) )
        {
            strSqlRequest = strSqlRequest.substring( 0, 6 );

            if ( strSqlRequest.contains( "select" ) )
            {
                try
                {
                    listData = DataHome.findAllInUserRequest( strPoolName, strUserRequest,
                            AdminSqlConnectionService.getConnectionService( strPoolName ) );
                    listFieldsNames = DataHome.findFieldsNames( strUserRequest,
                            AdminSqlConnectionService.getConnectionService( strPoolName ), strPoolName );
                }
                catch ( AppException e )
                {
                    String strErrorMessage = e.getInitialException(  ).getMessage(  );
                    Map<String, Object> model = new HashMap<String, Object>(  );
                    model.put( MARK_POOL_NAME, strPoolName );
                    model.put( MARK_APPEXCEPTION_ERROR, strErrorMessage );
                    model.put( MARK_USER_REQUEST, strUserRequest );

                    HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_USER_REQUEST_ERROR, getLocale(  ),
                            model );

                    return getAdminPage( template.getHtml(  ) );
                }
            }
        }
        else
        {
            try
            {
                DataHome.createWithUserRequest( strUserRequest,
                    AdminSqlConnectionService.getConnectionService( strPoolName ), strPoolName );
            }
            catch ( AppException e )
            {
                String strErrorMessage = e.getInitialException(  ).getMessage(  );
                Map<String, Object> model = new HashMap<String, Object>(  );
                model.put( MARK_POOL_NAME, strPoolName );
                model.put( MARK_APPEXCEPTION_ERROR, strErrorMessage );
                model.put( MARK_USER_REQUEST, strUserRequest );

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_USER_REQUEST_ERROR, getLocale(  ),
                        model );

                return getAdminPage( template.getHtml(  ) );
            }
        }

        if ( listData.equals( null ) )
        {
            UrlItem url = new UrlItem( JSP_URL_MANAGE_TABLES );
            url.addParameter( PARAMETER_POOL_NAME, strPoolName );

            return url.getUrl(  );
        }
        else
        {
            HashMap<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_POOL_NAME, strPoolName );
            model.put( MARK_USER_REQUEST, strUserRequest );
            model.put( MARK_DATA_LIST, listData );
            model.put( MARK_FIELDS_NAMES, listFieldsNames );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RESULT_OF_USER_REQUEST, getLocale(  ),
                    model );

            return getAdminPage( template.getHtml(  ) );
        }
    }

    public String getUserRequestError( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );

        UrlItem url = new UrlItem( URL_USER_REQUEST );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );

        return AdminMessageService.getMessageUrl( request, MESSAGE_SQL_ERROR_IN_QUERY, strErrorMessage, url.getUrl(  ),
            AdminMessage.TYPE_STOP );
    }

    /**
     * User can choose the options of the dump
     * @param request HttpServletRequest
     * @return the dump option page
     */
    public String getExportDatabase( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_POOL_NAME, strPoolName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PARAMETER_TO_EXPORT_DATABASE, getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * User can see the dump of a database on a page
     * @param request HttpServletRequest
     * @return dump database page
     */
    public String doExportDatabase( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strCheckboxStructure = request.getParameter( PARAMETER_CHECKBOX_STRUCTURE );
        String strCheckboxData = request.getParameter( PARAMETER_CHECKBOX_DATA );
        String strCheckboxDatabase = request.getParameter( PARAMETER_CHECKBOX_DATABASE );
        String strFieldNameList = "";
        String strPKFieldName = "";
        String strKey = "";
        List<String> DumpCommandslist = new ArrayList(  );
        List<String> listRows = new ArrayList(  );
        StringBuffer stDumpCommands = new StringBuffer(  );
        List<Table> tableList = new ArrayList<Table>(  );
        List<Field> fieldList = new ArrayList<Field>(  );
        List<List<String>> listData = new ArrayList<List<String>>(  );
        tableList = TableHome.findAllByPool( strPoolName, getPlugin(  ) );
        stDumpCommands.append( "--\n-- Base de donn�es : " + "`" + strPoolName + "`" + "\n--\n\n" );
        stDumpCommands.append( "-- -------------------------------\n\n" );

        if ( ( strCheckboxDatabase != null ) && strCheckboxDatabase.equals( "on" ) )
        {
            stDumpCommands.append( "DROP DATABASE IF EXISTS " + strPoolName + ";\n" + "CREATE DATABASE " + strPoolName +
                ";\n" + "USE " + strPoolName + ";\n\n" );
        }

        for ( Table table : tableList )
        {
            String strTableName = table.getTableName(  );
            String strDataRow = "";
            String strInserDataLine = "";
            String strStructureLine = "";
            String strFieldsStructuresLines = "";

            if ( ( strCheckboxStructure != null ) && strCheckboxStructure.equals( "on" ) )
            {
                stDumpCommands.append( "--\n-- Structure de la table " + "`" + strTableName + "`" + "\n--\n\n" );
                stDumpCommands.append( "DROP TABLE IF EXISTS " + "`" + strTableName + "`;\n" );
                stDumpCommands.append( "CREATE TABLE " + "`" + strTableName + "`(\n" );
                fieldList = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );

                for ( Field field : fieldList )
                {
                    String strFieldName = field.getFieldName(  );
                    strFieldNameList += ( "`" + strFieldName + "`," );

                    String strType = field.getTypeValue(  );
                    String strNull = field.getLabelNullValue(  );

                    if ( strNull.equals( "NO" ) )
                    {
                        strNull = "NOT NULL";
                    }
                    else
                    {
                        strNull = "";
                    }

                    String strDefault = field.getDefaultValue(  );

                    if ( strDefault.equals( "" ) )
                    {
                        strDefault = "";
                    }
                    else
                    {
                        if ( field.getTypeValue(  ).equals( "text" ) )
                        {
                            strDefault = "";
                        }
                        else if ( field.getDefaultValue(  ).equals( "NULL" ) ||
                                field.getDefaultValue(  ).equals( "CURRENT_TIMESTAMP" ) )
                        {
                            strDefault = "default " + field.getDefaultValue(  );
                        }
                        else
                        {
                            strDefault = "default " + "'" + field.getDefaultValue(  ) + "'";
                        }
                    }

                    strKey = field.getLabelKeyValue(  );

                    if ( strKey.equals( "PRI" ) )
                    {
                        strPKFieldName += ( "`" + field.getFieldName(  ) + "`" + "," );
                    }

                    strStructureLine = "`" + strFieldName + "` " + strType + " " + strNull + " " + strDefault + ",";
                    strFieldsStructuresLines += ( strStructureLine + "\n" );
                }

                if ( strPKFieldName != "" )
                {
                    strPKFieldName = strPKFieldName.substring( 0, strPKFieldName.length(  ) - 1 );
                    strFieldsStructuresLines += ( "PRIMARY KEY (" + strPKFieldName + ")\n);" );
                }
                else
                {
                    strFieldsStructuresLines = strFieldsStructuresLines.substring( 0,
                            strFieldsStructuresLines.length(  ) - 2 ) + ");";
                }

                strPKFieldName = "";
                stDumpCommands.append( strFieldsStructuresLines + "\n\n" );
                strFieldNameList = "(" + strFieldNameList.substring( 0, strFieldNameList.length(  ) - 1 ) + ")";
            }

            if ( ( strCheckboxData != null ) && strCheckboxData.equals( "on" ) )
            {
                if ( !( ( strCheckboxStructure != null ) && strCheckboxStructure.equals( "on" ) ) )
                {
                    fieldList = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );

                    for ( Field field : fieldList )
                    {
                        String strFieldName = field.getFieldName(  );
                        strFieldNameList += ( "`" + strFieldName + "`," );
                    }

                    strFieldNameList = "(" + strFieldNameList.substring( 0, strFieldNameList.length(  ) - 1 ) + ")";
                }

                listData = DataHome.findAll( strTableName, AdminSqlConnectionService.getConnectionService( strPoolName ) );

                if ( listData.isEmpty(  ) )
                {
                    stDumpCommands.append( "--\n-- Pas de donnees dans " + "`" + strTableName + "`" + "\n--\n\n" ); //
                }
                else
                {
                    stDumpCommands.append( "--\n-- Contenu de la table " + "`" + strTableName + "`" + "\n--\n\n" );

                    for ( List<String> listRow : listData )
                    {
                        for ( String strData : listRow )
                        {
                            if ( strData.contains( "'" ) )
                            {
                                strData = strData.replaceAll( "'", "''" );
                            }

                            strDataRow += ( "'" + strData + "'" + "," );
                        }

                        strInserDataLine = "(" + strDataRow.substring( 0, strDataRow.length(  ) - 1 ) + ");";
                        stDumpCommands.append( "INSERT INTO " + "`" + strTableName + "`" + strFieldNameList +
                            " VALUES " + strInserDataLine + "\n" );
                        strDataRow = "";
                    }

                    stDumpCommands.append( "\n" );
                }

                strFieldNameList = "";
            }
        }

        HashMap model = new HashMap(  );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_DUMP_COMMANDS, stDumpCommands );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DUMP_DATABASE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Creation of a table in a database
     * @param request HttpServletRequest
     * @return table creation page
     */
    public String getCreateTable( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_TABLE );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        HashMap model = new HashMap(  );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_FIELD_TYPE_REFERENCE_LIST, FieldHome.findFieldType( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_KEY_REFERENCE_LIST, FieldHome.findFieldKey( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_NULL_REFERENCE_LIST, FieldHome.findFieldNull( strPoolName, getPlugin(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_TABLE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the creation of the table
     * @param request HttpServletRequest
     * @return the manage tables page
     */
    public String doCreateTable( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strTableComment = request.getParameter( PARAMETER_TABLE_COMMENT );

        String strFieldName = request.getParameter( PARAMETER_FIELD_NAME );
        String strFieldLengthType = request.getParameter( PARAMETER_FIELD_LENGTH_TYPE );
        String strFieldDefault = request.getParameter( PARAMETER_FIELD_DEFAULT_VALUE );

        if ( strTableName.equals( null ) || strTableName.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_TABLE_NAME, AdminMessage.TYPE_STOP );
        }

        if ( strFieldName.equals( null ) || strFieldName.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FIELD_NAME, AdminMessage.TYPE_STOP );
        }

        if ( ( strFieldLengthType == null ) || strFieldLengthType.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FIELD_TYPE_LENGTH, AdminMessage.TYPE_STOP );
        }

        //Combos
        int nIdFieldType = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_TYPE ) );
        int nIdFieldNull = 1;
        int nIdFieldKey = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_KEY ) );

        Table table = new Table(  );
        table.setTableName( strTableName );
        table.setPrimaryKey( strFieldName );
        table.setComment( strTableComment );

        Field field = new Field(  );
        field.setFieldName( strFieldName );
        field.setIdTypeValue( nIdFieldType );
        field.setLengthTypeValue( strFieldLengthType );
        field.setIdNullValue( nIdFieldNull );
        field.setIdKeyValue( nIdFieldKey );
        field.setDefaultValue( strFieldDefault );

        try
        {
            TableHome.create( strPoolName, table, field, getPlugin(  ) );
        }
        catch ( AppException e )
        {
            UrlItem url = new UrlItem( JSP_CREATE_TABLE );

            url.addParameter( PARAMETER_POOL_NAME, strPoolName );
            url.addParameter( PARAMETER_TABLE_NAME, strTableName );

            Object[] messageArgs = { e.getInitialException(  ).getMessage(  ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_SQL_ERROR_IN_QUERY, messageArgs, url.getUrl(  ),
                AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_URL_MANAGE_FIELDS_STRUCTURES );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return url.getUrl(  );
    }

    /**
     * Modification of a table name of a database
     * @param request HttpServletRequest
     * @return the modification of the table name page
     */
    public String getModifyTableName( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_TABLE_NAME );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );

        Table table = TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) );

        HashMap model = new HashMap(  );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, table );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_TABLE_NAME, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the modification of the table name
     * @param request HttpServletRequest
     * @return the manage tables page
     */
    public String doModifyTableName( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableNameToModify = request.getParameter( PARAMETER_TABLE_NAME_TO_MODIFY );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );

        if ( strTableName.equals( null ) || strTableName.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_TABLE_NAME, AdminMessage.TYPE_STOP );
        }

        Table table = new Table(  );
        table.setTableName( strTableName );

        TableHome.update( strTableNameToModify, table );

        UrlItem url = new UrlItem( JSP_URL_MANAGE_TABLES );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );

        return url.getUrl(  );
    }

    /**
     * Confirmation of the table deleting
     * @param request HttpServletRequest
     * @return a confirm message to delete
     */
    public String getDeleteTable( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strUrl = JSP_DO_DELETE_TABLE + "?" + PARAMETER_POOL_NAME + "=" + strPoolName + "&" +
            PARAMETER_TABLE_NAME + "=" + strTableName;
        String strMessageKey = PROPERTY_CONFIRM_DELETE_TABLE;
        String strAdminMessageUrl = AdminMessageService.getMessageUrl( request, strMessageKey, strUrl, "",
                AdminMessage.TYPE_CONFIRMATION );

        return strAdminMessageUrl;
    }

    /**
     * Process the deleting of the table on a database
     * @param request HttpServletRequest
     * @return the management of the tables
     */
    public String doDeleteTable( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );

        TableHome.remove( strTableName, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE_TABLES );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );

        return url.getUrl(  );
    }

    /**
     * Management of the fields structure of a table
     * @param request HttpServletRequest
     * @return the manage fields structure page
     */
    public String getManageFieldsStructure( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_FIELDS_STRUCTURE );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strPlaceOfField = request.getParameter( PARAMETER_PLACE_OF_FIELD );
        String strAfterTheField = request.getParameter( PARAMETER_AFTER_THE_FIELD );

        Table table = TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ); //TODO COnnection service
        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );

        HashMap model = new HashMap(  );
        model.put( MARK_FIELDS_TABLE, listFields );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, table );
        model.put( MARK_NEW_FIELD, strPlaceOfField );
        model.put( MARK_AFTER_THE_FIELD, strAfterTheField );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_STRUCTURE_SQL, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Creation of a field in a table
     * @param request HttpServletRequest
     * @return the field creating page
     */
    public String getCreateFieldStructure( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_FIELD );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strPlaceOfField = request.getParameter( PARAMETER_PLACE_OF_FIELD );
        String strAfterTheField = request.getParameter( PARAMETER_AFTER_THE_FIELD );
        Table table = TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) );
        HashMap model = new HashMap(  );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, table );
        model.put( MARK_FIELD_TYPE_REFERENCE_LIST, FieldHome.findFieldType( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_KEY_REFERENCE_LIST, FieldHome.findFieldKey( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_NULL_REFERENCE_LIST, FieldHome.findFieldNull( strPoolName, getPlugin(  ) ) );
        model.put( MARK_NEW_FIELD, strPlaceOfField );
        model.put( MARK_AFTER_THE_FIELD, strAfterTheField );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_STRUCTURE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the creation of the field
     * @param request HttpServletRequest
     * @return the mana
     */
    public String doCreateFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strFieldName = request.getParameter( PARAMETER_FIELD_NAME );
        String strFieldLengthType = request.getParameter( PARAMETER_FIELD_LENGTH_TYPE );
        String strFieldDefault = request.getParameter( PARAMETER_FIELD_DEFAULT_VALUE );
        String strPlaceOfField = request.getParameter( PARAMETER_PLACE_OF_FIELD );
        String strAfterTheField = request.getParameter( PARAMETER_AFTER_THE_FIELD );

        if ( strFieldName.equals( null ) || strFieldName.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_TABLE_NAME, AdminMessage.TYPE_STOP );
        }

        //Combos
        int nIdFieldType = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_TYPE ) );
        int nIdFieldNull = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_NULL ) );
        int nIdFieldKey = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_KEY ) );

        if ( ( nIdFieldType == 3 ) || ( nIdFieldType == 4 ) || ( nIdFieldType == 12 ) || ( nIdFieldType == 13 ) ||
                ( nIdFieldType == 14 ) || ( nIdFieldType == 15 ) || ( nIdFieldType == 17 ) || ( nIdFieldType == 18 ) ||
                ( nIdFieldType == 19 ) || ( nIdFieldType == 20 ) || ( nIdFieldType == 21 ) || ( nIdFieldType == 22 ) ||
                ( nIdFieldType == 23 ) )
        {
            strFieldLengthType = "";
        }
        else if ( ( strFieldLengthType == null ) || strFieldLengthType.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FIELD_TYPE_LENGTH, AdminMessage.TYPE_STOP );
        }

        int nPlaceOfField = Integer.parseInt( strPlaceOfField );

        if ( ( nIdFieldNull == 2 ) && ( nIdFieldKey == 2 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NULL_AND_PRIMARY_KEY,
                AdminMessage.TYPE_STOP );
        }

        Field field = new Field(  );
        field.setFieldName( strFieldName );
        field.setIdTypeValue( nIdFieldType );
        field.setLengthTypeValue( strFieldLengthType );
        field.setIdNullValue( nIdFieldNull );
        field.setIdKeyValue( nIdFieldKey );
        field.setDefaultValue( strFieldDefault );
        field.setPlaceOfField( nPlaceOfField );
        field.setAfterTheField( strAfterTheField );

        try
        {
            FieldHome.create( strPoolName, strTableName, field, getPlugin(  ) );
        }

        catch ( AppException e )
        {
            UrlItem url = new UrlItem( URL_CREATION_FIELD );

            url.addParameter( PARAMETER_POOL_NAME, strPoolName );
            url.addParameter( PARAMETER_TABLE_NAME, strTableName );
            url.addParameter( PARAMETER_PLACE_OF_FIELD, strPlaceOfField );
            url.addParameter( PARAMETER_AFTER_THE_FIELD, strAfterTheField );

            Object[] messageArgs = { e.getInitialException(  ).getMessage(  ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_SQL_ERROR_IN_QUERY, messageArgs, url.getUrl(  ),
                AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_URL_MANAGE_FIELDS_STRUCTURES );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return url.getUrl(  );
    }

    /**
     * Modification of a field structure
     * @param request HttpServletRequest
     * @return the field modification page
     */
    public String getModifyFieldStructure( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_FIELD_STRUCTURE );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strFieldName = request.getParameter( PARAMETER_FIELD_NAME );
        Table table = TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) );
        Field field = FieldHome.findByPrimaryKey( strPoolName, strTableName, strFieldName, getPlugin(  ) );

        HashMap model = new HashMap(  );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, table );
        model.put( MARK_FIELD, field );
        model.put( MARK_FIELD_TYPE_REFERENCE_LIST, FieldHome.findFieldType( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_KEY_REFERENCE_LIST, FieldHome.findFieldKey( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_NULL_REFERENCE_LIST, FieldHome.findFieldNull( strPoolName, getPlugin(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_STRUCTURE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the modification of the field structure
     * @param request HttpServletRequest
     * @return the manage fields structure page
     */
    public String doModifyFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strFieldName = request.getParameter( PARAMETER_FIELD_NAME );
        String strFieldLengthType = request.getParameter( PARAMETER_FIELD_LENGTH_TYPE );
        String strFieldDefault = request.getParameter( PARAMETER_FIELD_DEFAULT_VALUE );
        String strFieldNameToModify = request.getParameter( PARAMETER_FIELD_TO_MODIFY );
        String strIdOldFieldKey = request.getParameter( PARAMETER_ID_OLD_FIELD_KEY );

        int nIdOldFieldKey = Integer.parseInt( strIdOldFieldKey );

        if ( strFieldName.equals( null ) || strFieldName.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FIELD_NAME, AdminMessage.TYPE_STOP );
        }

        //Combos
        int nIdFieldType = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_TYPE ) );
        int nIdFieldNull = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_NULL ) );
        int nIdFieldKey = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD_KEY ) );

        if ( ( nIdFieldType == 3 ) || ( nIdFieldType == 4 ) || ( nIdFieldType == 12 ) || ( nIdFieldType == 13 ) ||
                ( nIdFieldType == 14 ) || ( nIdFieldType == 15 ) || ( nIdFieldType == 17 ) || ( nIdFieldType == 18 ) ||
                ( nIdFieldType == 19 ) || ( nIdFieldType == 20 ) || ( nIdFieldType == 21 ) || ( nIdFieldType == 22 ) ||
                ( nIdFieldType == 23 ) )
        {
            strFieldLengthType = "";
        }
        else if ( ( strFieldLengthType == null ) || strFieldLengthType.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FIELD_TYPE_LENGTH, AdminMessage.TYPE_STOP );
        }

        if ( strFieldDefault == null )
        {
            strFieldDefault = "";
        }

        if ( ( nIdFieldNull == 2 ) && ( nIdFieldKey == 2 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NULL_AND_PRIMARY_KEY,
                AdminMessage.TYPE_STOP );
        }

        Field field = new Field(  );

        field.setFieldName( strFieldName );
        field.setIdTypeValue( nIdFieldType );
        field.setLengthTypeValue( strFieldLengthType );
        field.setIdKeyValue( nIdFieldKey );
        field.setIdNullValue( nIdFieldNull );
        field.setDefaultValue( strFieldDefault );

        try
        {
            FieldHome.update( strPoolName, strTableName, field, strFieldNameToModify, nIdOldFieldKey, getPlugin(  ) );
        }

        catch ( AppException e )
        {
            UrlItem url = new UrlItem( URL_MODIFY_FIELD );

            url.addParameter( PARAMETER_POOL_NAME, strPoolName );
            url.addParameter( PARAMETER_TABLE_NAME, strTableName );
            url.addParameter( PARAMETER_FIELD_NAME, strFieldName );

            Object[] messageArgs = { e.getInitialException(  ).getMessage(  ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_SQL_ERROR_IN_QUERY, messageArgs, url.getUrl(  ),
                AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_URL_MANAGE_FIELDS_STRUCTURES );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return url.getUrl(  );
    }

    /**
     * Delete a field structure of a database
     * @param request HttpServletRequest
     * @return the confirmation message to delete the field
     */
    public String getDeleteFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strFieldName = request.getParameter( PARAMETER_FIELD_NAME );
        String strUrl = JSP_DO_DELETE_FIELD_STRUCTURE + "?" + PARAMETER_POOL_NAME + "=" + strPoolName + "&" +
            PARAMETER_TABLE_NAME + "=" + strTableName + "&" + PARAMETER_FIELD_NAME + "=" + strFieldName;
        String strMessageKey = PROPERTY_CONFIRM_DELETE_FIELD;

        String strAdminMessageUrl = AdminMessageService.getMessageUrl( request, strMessageKey, strUrl, "",
                AdminMessage.TYPE_CONFIRMATION );

        return strAdminMessageUrl;
    }

    /**
     * Confirmation of a deling field structure
     * @param request HttpServletRequest
     * @return the manage fields structure page
     */
    public String doDeleteFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        String strFieldName = request.getParameter( PARAMETER_FIELD_NAME );
        FieldHome.remove( strTableName, strFieldName, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE_FIELDS_STRUCTURES );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return url.getUrl(  );
    }

    /**
     * List all data's table of a database
     * @return List of all data's table of a databases
     * @param request HttpServetRequest
     */
    public String getManageDataTable( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_DATA_TABLE );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        Table table = TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) );
        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        List<List<String>> listData = DataHome.findAll( strTableName,
                AdminSqlConnectionService.getConnectionService( strPoolName ) );
        List<List<String>> listNewData = new ArrayList(  );
        List<String> listRows = new ArrayList(  );
        List<String> listInputs = new ArrayList(  );

        int nNumberLine = listData.size(  );
        String strData = "";
        int nNumberFields = listFields.size(  );
        String strConcatFieldNameAndData = "";
        int nNumberData = 0;
        int nNumberOfRow = 0;
        int n;
        String strInputConcatFieldNameAndData = "";

        for ( n = 0; n < nNumberLine; n++ )
        {
            strConcatFieldNameAndData = "";
            listRows = listData.get( n );

            for ( Field field : listFields )
            {
                String strFieldName = field.getFieldName(  );
                strData = listRows.get( listFields.indexOf( field ) );
                strConcatFieldNameAndData = strConcatFieldNameAndData + "&" + strFieldName + "=" + strData;
                strInputConcatFieldNameAndData = "<input name=" + field.getFieldName(  ) + " type=hidden value=" +
                    strData + ">";
            }

            listInputs.add( strInputConcatFieldNameAndData );
            listRows.add( strConcatFieldNameAndData );
            listNewData.add( listRows );
        }

        HashMap model = new HashMap(  );
        model.put( MARK_FIELDS_TABLE, listFields );
        model.put( MARK_CONCAT_FIELDNAME_DATA, strConcatFieldNameAndData );
        model.put( MARK_INPUT_CONCAT_FIELD_NAME_AND_DATA, strInputConcatFieldNameAndData );
        model.put( MARK_DATA_LIST, listData );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, table );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DATA_SQL, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Data creation page of a table
     * @param request HttpServetRequest
     * @return data creation page of a table
     */
    public String getCreateData( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_DATA );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        Table table = TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) );
        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        HashMap model = new HashMap(  );
        model.put( MARK_FIELDS_TABLE, listFields );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, table );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DATA, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the creation of data
     * @param request HttpServletRequest
     * @return the manage data page
     */
    public String doCreateData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        List<String> listFieldsNames = new ArrayList(  );
        List<String> listData = new ArrayList(  );
        int nIndexOfData=0;
        String strDefaultValue="";
        String strData = "";

        for ( Field field : listFields )
        {
            listFieldsNames.add( field.getFieldName(  ) );
            strData = request.getParameter( field.getFieldName(  ) );

            if ( strData != null && strData.length( )>0  )
                
            {
                nIndexOfData = listData.indexOf(strData) + 1;
                Field verifFieldDefault = listFields.get(nIndexOfData);
                strDefaultValue=verifFieldDefault.getDefaultValue();
                 if ( strDefaultValue != null && strDefaultValue.length( )>0)
                 {
                     strData = "";
                 }
                 else
                 {
                    strData = strDefaultValue;
                 }
            }
            if ( field.getIdTypeValue(  ) == 1 )
            {
                strData = "\"" + strData + "\"";
            }
            listData.add( strData );
        }
        try
        {
            DataHome.create( strPoolName, strTableName, listData, listFieldsNames,
                AdminSqlConnectionService.getConnectionService( strPoolName ) );
        }
        catch ( AppException e )
        {
            UrlItem url = new UrlItem( URL_CREATE_DATA );

            url.addParameter( PARAMETER_POOL_NAME, strPoolName );
            url.addParameter( PARAMETER_TABLE_NAME, strTableName );

            Object[] messageArgs = { e.getInitialException(  ).getMessage(  ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_SQL_ERROR_IN_QUERY, messageArgs, url.getUrl(  ),
                AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_URL_MANAGE_DATA_TABLE );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return url.getUrl(  );
    }

    /**
     * Modification of a data in a table
     * @param request HttpServletRequest
     * @return the modification data page
     */
    public String getModifyData( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_DATA );

        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        Table table = TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) );
        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        List<String> listData = new ArrayList(  );
        String strData = "";

        for ( Field field : listFields )
        {
            strData = request.getParameter( field.getFieldName(  ) );
            listData.add( strData );
        }

        HashMap model = new HashMap(  );
        model.put( MARK_DATA_LIST, listData );
        model.put( MARK_FIELDS_TABLE, listFields );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, table );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DATA, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the modification of a data in a table
     * @param request HttpServletRequest
     * @return the manage data page
     */
    public String doModifyData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );

        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        List<String> listConcatFieldNameAndData = new ArrayList(  );
        List<String> listConcatFieldNameAndOldData = new ArrayList(  );
        List<FieldValues> listFieldValues = new ArrayList<FieldValues>(  );

        for ( Field field : listFields )
        {
            String strFieldNameUpdate = field.getFieldName(  ) + "_update";
            String strDataUpdate = request.getParameter( strFieldNameUpdate );
            String strFieldName = field.getFieldName(  );
            String strData = request.getParameter( strFieldName );

            FieldValues fieldValues = new FieldValues(  );
            fieldValues.setFieldName( strFieldName );
            fieldValues.setOldValue( strData );
            fieldValues.setNewValue( strDataUpdate );

            listFieldValues.add( fieldValues );
        }

        try
        {
            DataHome.update( strPoolName, strTableName, listFieldValues,
                AdminSqlConnectionService.getConnectionService( strPoolName ) );
        }
        catch ( AppException e )
        {
            UrlItem url = new UrlItem( JSP_URL_MANAGE_DATA_TABLE );
            url.addParameter( PARAMETER_POOL_NAME, strPoolName );
            url.addParameter( PARAMETER_TABLE_NAME, strTableName );

            Object[] messageArgs = { e.getInitialException(  ).getMessage(  ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_SQL_ERROR_IN_QUERY, messageArgs, url.getUrl(  ),
                AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_URL_MANAGE_DATA_TABLE_AFTER_MODIF );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return url.getUrl(  );
    }

    /**
     * Delete a data in a table
     * @param request HttpServletRequest
     * @return the confirmation message to deleting a row
     */
    public String getDeleteData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        String strData = "";
        String strConcatFieldNameAndData = "";
        String strFieldName = "";

        for ( Field field : listFields )
        {
            strFieldName = field.getFieldName(  );
            strData = request.getParameter( field.getFieldName(  ) );
            strConcatFieldNameAndData = strConcatFieldNameAndData + "&" + strFieldName + "=" + strData;
        }

        strConcatFieldNameAndData = strConcatFieldNameAndData.substring( 1 );

        String strUrl = JSP_DO_DELETE_DATA + "?" + PARAMETER_POOL_NAME + "=" + strPoolName + "&" +
            PARAMETER_TABLE_NAME + "=" + strTableName + "&" + strConcatFieldNameAndData;
        String strMessageKey = PROPERTY_CONFIRM_DELETE_DATA;

        String strAdminMessageUrl = AdminMessageService.getMessageUrl( request, strMessageKey, strUrl, "",
                AdminMessage.TYPE_CONFIRMATION );

        return strAdminMessageUrl;
    }

    /**
     * Process the deleting of a data
     * @param request HttpServletRequest
     * @return th e manage data page
     */
    public String doDeleteData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = request.getParameter( PARAMETER_TABLE_NAME );
        List<String> listFieldsNames = new ArrayList(  );
        List<String> listConcatFieldNameAndData = new ArrayList(  );
        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        String strData = "";
        String strConcatFieldNameAndData = "";
        String strFieldName = "";

        for ( Field field : listFields )
        {
            strFieldName = field.getFieldName(  );
            strData = request.getParameter( field.getFieldName(  ) );
            listConcatFieldNameAndData.add( strFieldName + "='" + strData + "'" );
        }

        DataHome.remove( strPoolName, strTableName, listConcatFieldNameAndData,
            AdminSqlConnectionService.getConnectionService( strPoolName ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE_DATA_TABLE );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return url.getUrl(  );
    }
}
