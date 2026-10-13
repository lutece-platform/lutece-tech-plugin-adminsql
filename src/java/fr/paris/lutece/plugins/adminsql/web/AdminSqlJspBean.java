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
package fr.paris.lutece.plugins.adminsql.web;

import fr.paris.lutece.plugins.adminsql.business.DataHome;
import fr.paris.lutece.plugins.adminsql.business.Field;
import fr.paris.lutece.plugins.adminsql.business.FieldHome;
import fr.paris.lutece.plugins.adminsql.business.FieldValues;
import fr.paris.lutece.plugins.adminsql.business.Table;
import fr.paris.lutece.plugins.adminsql.business.TableHome;
import fr.paris.lutece.plugins.adminsql.service.AdminSqlConnectionService;
import fr.paris.lutece.plugins.adminsql.service.AdminSqlExportService;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.IntStream;


/**
 * Management of the sql server
 */
@RequestScoped
@Named( "adminSqlJspBean" )
@Controller( controllerJsp = AdminSqlJspBean.CONTROLLER_JSP, controllerPath = AdminSqlJspBean.CONTROLLER_PATH, right = AdminSqlJspBean.RIGHT_MANAGE_ADMIN_SQL, securityTokenEnabled = true )
public class AdminSqlJspBean extends MVCAdminJspBean
{
    /** The right of the feature */
    public static final String RIGHT_MANAGE_ADMIN_SQL = "ADMIN_SQL_MANAGEMENT";

    /** The controller JSP */
    public static final String CONTROLLER_JSP = "ManageDatabases.jsp";

    /** The controller path */
    public static final String CONTROLLER_PATH = "jsp/admin/plugins/adminsql/";

    private static final long serialVersionUID = 6523104187125405131L;

    private static final String MESSAGE_ERROR_FIELD_NAME = "adminsql.create_field.errorFieldName";
    private static final String MESSAGE_ERROR_FIELD_TYPE_LENGTH = "adminsql.create_field.errorFieldTypeLength";
    private static final String MESSAGE_ERROR_TABLE_NAME = "adminsql.create_table.errorTableName";
    private static final String MESSAGE_ERROR_NULL_AND_PRIMARY_KEY = "adminsql.create_field.errorNullAndPrimaryKey";
    private static final String MESSAGE_SQL_ERROR_IN_QUERY = "adminsql.message.sqlErrorInQuery";
    private static final String MESSAGE_UNKNOWN_POOL = "adminsql.message.unknownPool";
    private static final String MESSAGE_UNKNOWN_TABLE = "adminsql.message.unknownTable";
    private static final String MESSAGE_UNKNOWN_FIELD = "adminsql.message.unknownField";
    private static final String MESSAGE_CONFIRM_DELETE_FIELD = "adminsql.delete_field.confirm_delete_field";
    private static final String MESSAGE_CONFIRM_DELETE_TABLE = "adminsql.delete_table.confirm_delete_table";
    private static final String MESSAGE_CONFIRM_DELETE_DATA = "adminsql.delete_data.confirm_delete_data";

    private static final String PROPERTY_PAGE_TITLE_POOL_LIST = "adminsql.manage_databases.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_TABLE_LIST = "adminsql.manage_tables.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FIELDS_STRUCTURE = "adminsql.manage_fields_structure.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_DATA_TABLE = "adminsql.manage_data.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DATA = "adminsql.create_data.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DATA = "adminsql.modify_data.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FIELD_STRUCTURE = "adminsql.modify_field_structure.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FIELD = "adminsql.create_field.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TABLE = "adminsql.create_table.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TABLE_NAME = "adminsql.modify_table_name.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_FONCTIONNALITIES_LIST = "adminsql.choose_fonctionnality.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_USER_REQUEST = "adminsql.user_request.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RESULT_USER_REQUEST = "adminsql.result_of_user_request.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_EXPORT = "adminsql.parameter_to_export.pageTitle";

    private static final String VIEW_MANAGE_DATABASES = "manageDatabases";
    private static final String VIEW_MANAGE_FONCTIONNALITIES_LIST = "manageFonctionnalitiesList";
    private static final String VIEW_MANAGE_TABLES = "manageTables";
    private static final String VIEW_CREATE_TABLE = "createTable";
    private static final String VIEW_MODIFY_TABLE_NAME = "modifyTableName";
    private static final String VIEW_CONFIRM_REMOVE_TABLE = "confirmRemoveTable";
    private static final String VIEW_MANAGE_FIELDS_STRUCTURE = "manageFieldsStructure";
    private static final String VIEW_CREATE_FIELD_STRUCTURE = "createFieldStructure";
    private static final String VIEW_MODIFY_FIELD_STRUCTURE = "modifyFieldStructure";
    private static final String VIEW_CONFIRM_REMOVE_FIELD_STRUCTURE = "confirmRemoveFieldStructure";
    private static final String VIEW_MANAGE_DATA = "manageData";
    private static final String VIEW_CREATE_DATA = "createData";
    private static final String VIEW_MODIFY_DATA = "modifyData";
    private static final String VIEW_CONFIRM_REMOVE_DATA = "confirmRemoveData";
    private static final String VIEW_USER_REQUEST = "userRequest";
    private static final String VIEW_EXPORT_DATABASE = "exportDatabase";

    private static final String ACTION_CREATE_TABLE = "createTable";
    private static final String ACTION_MODIFY_TABLE_NAME = "modifyTableName";
    private static final String ACTION_REMOVE_TABLE = "removeTable";
    private static final String ACTION_CREATE_FIELD_STRUCTURE = "createFieldStructure";
    private static final String ACTION_MODIFY_FIELD_STRUCTURE = "modifyFieldStructure";
    private static final String ACTION_REMOVE_FIELD_STRUCTURE = "removeFieldStructure";
    private static final String ACTION_CREATE_DATA = "createData";
    private static final String ACTION_MODIFY_DATA = "modifyData";
    private static final String ACTION_REMOVE_DATA = "removeData";
    private static final String ACTION_USER_REQUEST = "userRequest";
    private static final String ACTION_EXPORT_DATABASE = "exportDatabase";

    private static final String PARAMETER_POOL_NAME = "pool_name";
    private static final String PARAMETER_TABLE_NAME = "table_name";
    private static final String PARAMETER_TABLE_NAME_TO_MODIFY = "tableNameToModify";
    private static final String PARAMETER_TABLE_COMMENT = "table_comment";
    private static final String PARAMETER_FIELD_NAME = "field_name";
    private static final String PARAMETER_FIELD_LENGTH_TYPE = "field_length_type";
    private static final String PARAMETER_FIELD_DEFAULT_VALUE = "field_default_value";
    private static final String PARAMETER_FIELD_TO_MODIFY = "fieldToModify";
    private static final String PARAMETER_ID_OLD_FIELD_KEY = "idOldFieldKey";
    private static final String PARAMETER_ID_FIELD_TYPE = "field_type_id";
    private static final String PARAMETER_ID_FIELD_NULL = "field_null_id";
    private static final String PARAMETER_ID_FIELD_KEY = "field_key_id";
    private static final String PARAMETER_PLACE_OF_FIELD = "place_of_new_field";
    private static final String PARAMETER_AFTER_THE_FIELD = "choose_fields";
    private static final String PARAMETER_USER_REQUEST = "user_request";
    private static final String PARAMETER_CHECKBOX_STRUCTURE = "checkbox_structure";
    private static final String PARAMETER_CHECKBOX_DATA = "checkbox_data";
    private static final String PARAMETER_CHECKBOX_DATABASE = "checkbox_database";
    private static final String PARAMETER_PREFIX_COLUMN = "col_";
    private static final String PARAMETER_PREFIX_NEW_VALUE = "new_";

    private static final String MARK_POOL_LIST = "pool_list";
    private static final String MARK_TABLE_LIST = "table_list";
    private static final String MARK_DATA_LIST = "data_list";
    private static final String MARK_FIELDS_TABLE = "field_list";
    private static final String MARK_POOL_NAME = "pool_name";
    private static final String MARK_TABLE = "sql_table";
    private static final String MARK_FIELD = "field";
    private static final String MARK_FIELD_TYPE_REFERENCE_LIST = "field_type_list";
    private static final String MARK_FIELD_KEY_REFERENCE_LIST = "field_key_list";
    private static final String MARK_FIELD_NULL_REFERENCE_LIST = "field_null_list";
    private static final String MARK_FIELD_NAME_REFERENCE_LIST = "field_name_list";
    private static final String MARK_NEW_FIELD = "place_of_new_field";
    private static final String MARK_AFTER_THE_FIELD = "choose_fields";
    private static final String MARK_USER_REQUEST = "user_request";
    private static final String MARK_FIELDS_NAMES = "list_fields_names";
    private static final String MARK_DUMP_COMMANDS = "dump_commands";
    private static final String MARK_APPEXCEPTION_ERROR = "error_sql";

    private static final String TEMPLATE_MANAGE_POOL_SQL = "admin/plugins/adminsql/manage_databases.html";
    private static final String TEMPLATE_MANAGE_TABLE_SQL = "admin/plugins/adminsql/manage_tables.html";
    private static final String TEMPLATE_MANAGE_DATA_SQL = "admin/plugins/adminsql/manage_data.html";
    private static final String TEMPLATE_CREATE_DATA = "admin/plugins/adminsql/create_data.html";
    private static final String TEMPLATE_MANAGE_STRUCTURE_SQL = "admin/plugins/adminsql/manage_fields_structure.html";
    private static final String TEMPLATE_MODIFY_DATA = "admin/plugins/adminsql/modify_data.html";
    private static final String TEMPLATE_MODIFY_STRUCTURE = "admin/plugins/adminsql/modify_field_structure.html";
    private static final String TEMPLATE_CREATE_STRUCTURE = "admin/plugins/adminsql/create_field.html";
    private static final String TEMPLATE_CREATE_TABLE = "admin/plugins/adminsql/create_table.html";
    private static final String TEMPLATE_MODIFY_TABLE_NAME = "admin/plugins/adminsql/modify_table_name.html";
    private static final String TEMPLATE_CHOOSE_FONCTIONNALITIES_LIST = "admin/plugins/adminsql/fonctionnalities_list_on_database.html";
    private static final String TEMPLATE_USER_REQUEST = "admin/plugins/adminsql/user_request.html";
    private static final String TEMPLATE_RESULT_OF_USER_REQUEST = "admin/plugins/adminsql/result_of_user_request.html";
    private static final String TEMPLATE_PARAMETER_TO_EXPORT_DATABASE = "admin/plugins/adminsql/parameter_to_export_db.html";
    private static final String TEMPLATE_DUMP_DATABASE = "admin/plugins/adminsql/dump_database.html";
    private static final String TEMPLATE_USER_REQUEST_ERROR = "admin/plugins/adminsql/error_user_request.html";

    private static final String CHECKBOX_ON = "on";
    private static final String SQL_SELECT = "select";
    private static final int ID_NULL_NO = 1;
    private static final int ID_NULL_YES = 2;
    private static final int ID_KEY_PRIMARY = 2;
    private static final int PLACE_END_OF_TABLE = 1;
    private static final Set<Integer> TYPES_WITHOUT_LENGTH = Set.of( 3, 4, 12, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23 );

    private static final String PROPERTY_ROWS_PER_PAGE = "adminsql.paginator.rows.itemsPerPage";

    @Inject
    private AdminSqlExportService _exportService;

    @Inject
    @Pager( name = "adminsql.rows", listBookmark = MARK_DATA_LIST, defaultItemsPerPage = PROPERTY_ROWS_PER_PAGE, baseUrl = CONTROLLER_PATH + CONTROLLER_JSP )
    private IPager<Integer, DataRow> _pager;

    /**
     * Returns the plugin of this feature
     *
     * @return the adminsql plugin
     */
    @Override
    public Plugin getPlugin(  )
    {
        return PluginService.getPlugin( AdminSqlConnectionService.PLUGIN_NAME );
    }

    /**
     * List of databases
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return the list of databases page
     */
    @View( value = VIEW_MANAGE_DATABASES, defaultView = true )
    public String getManageDatabases( HttpServletRequest request, Models model )
    {
        ReferenceList listPools = new ReferenceList(  );
        AppConnectionService.getPoolList( listPools );
        model.put( MARK_POOL_LIST, listPools );

        return getPage( PROPERTY_PAGE_TITLE_POOL_LIST, TEMPLATE_MANAGE_POOL_SQL, model );
    }

    /**
     * List of fonctionnalities on the pool
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return list of fonctionnalities
     */
    @View( VIEW_MANAGE_FONCTIONNALITIES_LIST )
    public String getManageFonctionnalitiesList( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        model.put( MARK_POOL_NAME, strPoolName );

        return getPage( PROPERTY_PAGE_TITLE_FONCTIONNALITIES_LIST, TEMPLATE_CHOOSE_FONCTIONNALITIES_LIST, model );
    }

    /**
     * List all tables of a database
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return List of all tables of the databases
     */
    @View( VIEW_MANAGE_TABLES )
    public String getManageTables( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        model.put( MARK_TABLE_LIST, TableHome.findAllByPool( strPoolName, getPlugin(  ) ) );
        model.put( MARK_POOL_NAME, strPoolName );

        return getPage( PROPERTY_PAGE_TITLE_TABLE_LIST, TEMPLATE_MANAGE_TABLE_SQL, model );
    }

    /**
     * User can type his request
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return user request page
     */
    @View( VIEW_USER_REQUEST )
    public String getUserRequest( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        model.put( MARK_POOL_NAME, strPoolName );

        return getPage( PROPERTY_PAGE_TITLE_USER_REQUEST, TEMPLATE_USER_REQUEST, model );
    }

    /**
     * Runs the request of the user and shows its result
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return result of the request
     */
    @Action( ACTION_USER_REQUEST )
    public String doUserRequest( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        String strUserRequest = StringUtils.trimToEmpty( getRawParameter( request, PARAMETER_USER_REQUEST ) );
        List<List<String>> listData = new ArrayList<>(  );
        List<String> listFieldsNames = new ArrayList<>(  );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_USER_REQUEST, strUserRequest );

        try
        {
            if ( strUserRequest.toLowerCase(  ).startsWith( SQL_SELECT ) )
            {
                listData = DataHome.findAllInUserRequest( strPoolName, strUserRequest,
                        AdminSqlConnectionService.getConnectionService( strPoolName ) );
                listFieldsNames = DataHome.findFieldsNames( strUserRequest,
                        AdminSqlConnectionService.getConnectionService( strPoolName ), strPoolName );
            }
            else
            {
                DataHome.createWithUserRequest( strUserRequest, AdminSqlConnectionService.getConnectionService( strPoolName ),
                    strPoolName );
            }
        }
        catch ( AppException e )
        {
            model.put( MARK_APPEXCEPTION_ERROR, getErrorMessage( e ) );

            return getPage( PROPERTY_PAGE_TITLE_USER_REQUEST, TEMPLATE_USER_REQUEST_ERROR, model );
        }

        model.put( MARK_DATA_LIST, listData );
        model.put( MARK_FIELDS_NAMES, listFieldsNames );

        return getPage( PROPERTY_PAGE_TITLE_RESULT_USER_REQUEST, TEMPLATE_RESULT_OF_USER_REQUEST, model );
    }

    /**
     * User can choose the options of the dump
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return the dump option page
     */
    @View( VIEW_EXPORT_DATABASE )
    public String getExportDatabase( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        model.put( MARK_POOL_NAME, strPoolName );

        return getPage( PROPERTY_PAGE_TITLE_EXPORT, TEMPLATE_PARAMETER_TO_EXPORT_DATABASE, model );
    }

    /**
     * User can see the dump of a database on a page
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return dump database page
     */
    @Action( ACTION_EXPORT_DATABASE )
    public String doExportDatabase( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        String strDump = _exportService.exportDatabase( strPoolName, isChecked( request, PARAMETER_CHECKBOX_DATABASE ),
                isChecked( request, PARAMETER_CHECKBOX_STRUCTURE ), isChecked( request, PARAMETER_CHECKBOX_DATA ), getPlugin(  ) );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_DUMP_COMMANDS, strDump );

        return getPage( PROPERTY_PAGE_TITLE_EXPORT, TEMPLATE_DUMP_DATABASE, model );
    }

    /**
     * Creation of a table in a database
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return table creation page
     */
    @View( VIEW_CREATE_TABLE )
    public String getCreateTable( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_FIELD_TYPE_REFERENCE_LIST, FieldHome.findFieldType( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_KEY_REFERENCE_LIST, FieldHome.findFieldKey( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_NULL_REFERENCE_LIST, FieldHome.findFieldNull( strPoolName, getPlugin(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TABLE, TEMPLATE_CREATE_TABLE, model );
    }

    /**
     * Process the creation of the table
     *
     * @param request HttpServletRequest
     * @return the manage fields page of the new table
     */
    @Action( ACTION_CREATE_TABLE )
    public String doCreateTable( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strFieldName = getRawParameter( request, PARAMETER_FIELD_NAME );
        String strFieldLengthType = getRawParameter( request, PARAMETER_FIELD_LENGTH_TYPE );

        if ( !isPool( strPoolName ) )
        {
            return redirectStop( request, MESSAGE_UNKNOWN_POOL );
        }

        if ( StringUtils.isEmpty( strTableName ) )
        {
            return redirectStop( request, MESSAGE_ERROR_TABLE_NAME );
        }

        if ( StringUtils.isEmpty( strFieldName ) )
        {
            return redirectStop( request, MESSAGE_ERROR_FIELD_NAME );
        }

        if ( StringUtils.isEmpty( strFieldLengthType ) )
        {
            return redirectStop( request, MESSAGE_ERROR_FIELD_TYPE_LENGTH );
        }

        Table table = new Table(  );
        table.setTableName( strTableName );
        table.setPrimaryKey( strFieldName );
        table.setComment( getRawParameter( request, PARAMETER_TABLE_COMMENT ) );

        Field field = new Field(  );
        field.setFieldName( strFieldName );
        field.setIdTypeValue( getInt( request, PARAMETER_ID_FIELD_TYPE ) );
        field.setLengthTypeValue( strFieldLengthType );
        field.setIdNullValue( ID_NULL_NO );
        field.setIdKeyValue( getInt( request, PARAMETER_ID_FIELD_KEY ) );
        field.setDefaultValue( StringUtils.defaultString( getRawParameter( request, PARAMETER_FIELD_DEFAULT_VALUE ) ) );

        try
        {
            TableHome.create( strPoolName, table, field, getPlugin(  ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_CREATE_TABLE, Map.of( PARAMETER_POOL_NAME, strPoolName ) );
        }

        return redirect( request, VIEW_MANAGE_FIELDS_STRUCTURE, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
    }

    /**
     * Modification of a table name of a database
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return the modification of the table name page
     */
    @View( VIEW_MODIFY_TABLE_NAME )
    public String getModifyTableName( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TABLE_NAME, TEMPLATE_MODIFY_TABLE_NAME, model );
    }

    /**
     * Process the modification of the table name
     *
     * @param request HttpServletRequest
     * @return the manage tables page
     */
    @Action( ACTION_MODIFY_TABLE_NAME )
    public String doModifyTableName( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableNameToModify = getRawParameter( request, PARAMETER_TABLE_NAME_TO_MODIFY );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableNameToModify );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        if ( StringUtils.isEmpty( strTableName ) )
        {
            return redirectStop( request, MESSAGE_ERROR_TABLE_NAME );
        }

        Table table = new Table(  );
        table.setTableName( strTableName );

        try
        {
            TableHome.update( strPoolName, strTableNameToModify, table );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_MODIFY_TABLE_NAME,
                Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableNameToModify ) );
        }

        return redirect( request, VIEW_MANAGE_TABLES, Map.of( PARAMETER_POOL_NAME, strPoolName ) );
    }

    /**
     * Confirmation of the table deleting
     *
     * @param request HttpServletRequest
     * @return a confirm message to delete
     */
    @View( value = VIEW_CONFIRM_REMOVE_TABLE, securityTokenAction = ACTION_REMOVE_TABLE )
    public String getConfirmRemoveTable( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TABLE ) );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        return redirect( request,
            AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_TABLE, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Process the deleting of the table on a database
     *
     * @param request HttpServletRequest
     * @return the management of the tables
     */
    @Action( ACTION_REMOVE_TABLE )
    public String doRemoveTable( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        try
        {
            TableHome.remove( strTableName, AdminSqlConnectionService.usePool( strPoolName ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_MANAGE_TABLES, Map.of( PARAMETER_POOL_NAME, strPoolName ) );
        }

        return redirect( request, VIEW_MANAGE_TABLES, Map.of( PARAMETER_POOL_NAME, strPoolName ) );
    }

    /**
     * Management of the fields structure of a table
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return the manage fields structure page
     */
    @View( VIEW_MANAGE_FIELDS_STRUCTURE )
    public String getManageFieldsStructure( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        ReferenceList listFieldNames = new ReferenceList(  );
        listFields.forEach( field -> listFieldNames.addItem( field.getFieldName(  ), field.getFieldName(  ) ) );
        model.put( MARK_FIELDS_TABLE, listFields );
        model.put( MARK_FIELD_NAME_REFERENCE_LIST, listFieldNames );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_FIELDS_STRUCTURE, TEMPLATE_MANAGE_STRUCTURE_SQL, model );
    }

    /**
     * Creation of a field in a table
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return the field creating page
     */
    @View( VIEW_CREATE_FIELD_STRUCTURE )
    public String getCreateFieldStructure( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ) );
        model.put( MARK_FIELD_TYPE_REFERENCE_LIST, FieldHome.findFieldType( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_KEY_REFERENCE_LIST, FieldHome.findFieldKey( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_NULL_REFERENCE_LIST, FieldHome.findFieldNull( strPoolName, getPlugin(  ) ) );
        model.put( MARK_NEW_FIELD, StringUtils.defaultIfEmpty( request.getParameter( PARAMETER_PLACE_OF_FIELD ), String.valueOf( PLACE_END_OF_TABLE ) ) );
        model.put( MARK_AFTER_THE_FIELD, StringUtils.defaultString( getRawParameter( request, PARAMETER_AFTER_THE_FIELD ) ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_FIELD, TEMPLATE_CREATE_STRUCTURE, model );
    }

    /**
     * Process the creation of the field
     *
     * @param request HttpServletRequest
     * @return the manage fields structure page
     */
    @Action( ACTION_CREATE_FIELD_STRUCTURE )
    public String doCreateFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strFieldName = getRawParameter( request, PARAMETER_FIELD_NAME );
        String strPlaceOfField = StringUtils.defaultIfEmpty( request.getParameter( PARAMETER_PLACE_OF_FIELD ), String.valueOf( PLACE_END_OF_TABLE ) );
        String strAfterTheField = StringUtils.defaultString( getRawParameter( request, PARAMETER_AFTER_THE_FIELD ) );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        if ( StringUtils.isEmpty( strFieldName ) )
        {
            return redirectStop( request, MESSAGE_ERROR_FIELD_NAME );
        }

        int nIdFieldType = getInt( request, PARAMETER_ID_FIELD_TYPE );
        int nIdFieldNull = getInt( request, PARAMETER_ID_FIELD_NULL );
        int nIdFieldKey = getInt( request, PARAMETER_ID_FIELD_KEY );
        String strFieldLengthType = getLength( request, nIdFieldType );

        if ( strFieldLengthType == null )
        {
            return redirectStop( request, MESSAGE_ERROR_FIELD_TYPE_LENGTH );
        }

        if ( ( nIdFieldNull == ID_NULL_YES ) && ( nIdFieldKey == ID_KEY_PRIMARY ) )
        {
            return redirectStop( request, MESSAGE_ERROR_NULL_AND_PRIMARY_KEY );
        }

        Field field = new Field(  );
        field.setFieldName( strFieldName );
        field.setIdTypeValue( nIdFieldType );
        field.setLengthTypeValue( strFieldLengthType );
        field.setIdNullValue( nIdFieldNull );
        field.setIdKeyValue( nIdFieldKey );
        field.setDefaultValue( StringUtils.defaultString( getRawParameter( request, PARAMETER_FIELD_DEFAULT_VALUE ) ) );
        field.setPlaceOfField( StringUtils.isNumeric( strPlaceOfField ) ? Integer.parseInt( strPlaceOfField ) : PLACE_END_OF_TABLE );
        field.setAfterTheField( strAfterTheField );

        try
        {
            FieldHome.create( strPoolName, strTableName, field, getPlugin(  ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_CREATE_FIELD_STRUCTURE, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME,
                    strTableName, PARAMETER_PLACE_OF_FIELD, strPlaceOfField, PARAMETER_AFTER_THE_FIELD, strAfterTheField ) );
        }

        return redirect( request, VIEW_MANAGE_FIELDS_STRUCTURE, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
    }

    /**
     * Modification of a field structure
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return the field modification page
     */
    @View( VIEW_MODIFY_FIELD_STRUCTURE )
    public String getModifyFieldStructure( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strFieldName = getRawParameter( request, PARAMETER_FIELD_NAME );
        String strError = checkField( strPoolName, strTableName, strFieldName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ) );
        model.put( MARK_FIELD, FieldHome.findByPrimaryKey( strPoolName, strTableName, strFieldName, getPlugin(  ) ) );
        model.put( MARK_FIELD_TYPE_REFERENCE_LIST, FieldHome.findFieldType( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_KEY_REFERENCE_LIST, FieldHome.findFieldKey( strPoolName, getPlugin(  ) ) );
        model.put( MARK_FIELD_NULL_REFERENCE_LIST, FieldHome.findFieldNull( strPoolName, getPlugin(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FIELD_STRUCTURE, TEMPLATE_MODIFY_STRUCTURE, model );
    }

    /**
     * Process the modification of the field structure
     *
     * @param request HttpServletRequest
     * @return the manage fields structure page
     */
    @Action( ACTION_MODIFY_FIELD_STRUCTURE )
    public String doModifyFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strFieldName = getRawParameter( request, PARAMETER_FIELD_NAME );
        String strFieldNameToModify = getRawParameter( request, PARAMETER_FIELD_TO_MODIFY );
        String strError = checkField( strPoolName, strTableName, strFieldNameToModify );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        if ( StringUtils.isEmpty( strFieldName ) )
        {
            return redirectStop( request, MESSAGE_ERROR_FIELD_NAME );
        }

        int nIdFieldType = getInt( request, PARAMETER_ID_FIELD_TYPE );
        int nIdFieldNull = getInt( request, PARAMETER_ID_FIELD_NULL );
        int nIdFieldKey = getInt( request, PARAMETER_ID_FIELD_KEY );
        String strFieldLengthType = getLength( request, nIdFieldType );

        if ( strFieldLengthType == null )
        {
            return redirectStop( request, MESSAGE_ERROR_FIELD_TYPE_LENGTH );
        }

        if ( ( nIdFieldNull == ID_NULL_YES ) && ( nIdFieldKey == ID_KEY_PRIMARY ) )
        {
            return redirectStop( request, MESSAGE_ERROR_NULL_AND_PRIMARY_KEY );
        }

        Field field = new Field(  );
        field.setFieldName( strFieldName );
        field.setIdTypeValue( nIdFieldType );
        field.setLengthTypeValue( strFieldLengthType );
        field.setIdKeyValue( nIdFieldKey );
        field.setIdNullValue( nIdFieldNull );
        field.setDefaultValue( StringUtils.defaultString( getRawParameter( request, PARAMETER_FIELD_DEFAULT_VALUE ) ) );

        try
        {
            FieldHome.update( strPoolName, strTableName, field, strFieldNameToModify, getInt( request, PARAMETER_ID_OLD_FIELD_KEY ), getPlugin(  ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_MODIFY_FIELD_STRUCTURE,
                Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName, PARAMETER_FIELD_NAME, strFieldNameToModify ) );
        }

        return redirect( request, VIEW_MANAGE_FIELDS_STRUCTURE, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
    }

    /**
     * Asks the confirmation of the removal of a field
     *
     * @param request HttpServletRequest
     * @return the confirmation message to delete the field
     */
    @View( value = VIEW_CONFIRM_REMOVE_FIELD_STRUCTURE, securityTokenAction = ACTION_REMOVE_FIELD_STRUCTURE )
    public String getConfirmRemoveFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strFieldName = getRawParameter( request, PARAMETER_FIELD_NAME );
        String strError = checkField( strPoolName, strTableName, strFieldName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FIELD_STRUCTURE ) );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );
        url.addParameter( PARAMETER_FIELD_NAME, strFieldName );

        return redirect( request,
            AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_FIELD, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Removes a field from a table
     *
     * @param request HttpServletRequest
     * @return the manage fields structure page
     */
    @Action( ACTION_REMOVE_FIELD_STRUCTURE )
    public String doRemoveFieldStructure( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strFieldName = getRawParameter( request, PARAMETER_FIELD_NAME );
        String strError = checkField( strPoolName, strTableName, strFieldName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        try
        {
            FieldHome.remove( strPoolName, strTableName, strFieldName, getPlugin(  ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_MANAGE_FIELDS_STRUCTURE,
                Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
        }

        return redirect( request, VIEW_MANAGE_FIELDS_STRUCTURE, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
    }

    /**
     * List all data's table of a database
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return List of all data's table of a databases
     */
    @View( VIEW_MANAGE_DATA )
    public String getManageDataTable( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        int nRows = DataHome.countRows( strTableName, AdminSqlConnectionService.getConnectionService( strPoolName ) );

        UrlItem urlPager = new UrlItem( CONTROLLER_PATH + CONTROLLER_JSP );
        urlPager.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_DATA );
        urlPager.addParameter( PARAMETER_POOL_NAME, strPoolName );
        urlPager.addParameter( PARAMETER_TABLE_NAME, strTableName );
        _pager.withBaseUrl( urlPager.getUrl(  ) ).withListItem( IntStream.range( 0, nRows ).boxed(  ).toList(  ) )
            .populateModels( request, model, listIndexes -> getRows( strPoolName, strTableName, listFields, listIndexes ), getLocale(  ) );
        model.put( MARK_FIELDS_TABLE, listFields );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_DATA_TABLE, TEMPLATE_MANAGE_DATA_SQL, model );
    }

    /**
     * Data creation page of a table
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return data creation page of a table
     */
    @View( VIEW_CREATE_DATA )
    public String getCreateData( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        model.put( MARK_FIELDS_TABLE, FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) ) );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DATA, TEMPLATE_CREATE_DATA, model );
    }

    /**
     * Process the creation of data
     *
     * @param request HttpServletRequest
     * @return the manage data page
     */
    @Action( ACTION_CREATE_DATA )
    public String doCreateData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        List<String> listFieldsNames = new ArrayList<>(  );
        List<String> listData = new ArrayList<>(  );

        for ( Field field : FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) ) )
        {
            listFieldsNames.add( field.getFieldName(  ) );
            listData.add( StringUtils.defaultString( getRawParameter( request, PARAMETER_PREFIX_COLUMN + field.getFieldName(  ) ) ) );
        }

        try
        {
            DataHome.create( strPoolName, strTableName, listData, listFieldsNames, AdminSqlConnectionService.getConnectionService( strPoolName ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_CREATE_DATA, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
        }

        return redirect( request, VIEW_MANAGE_DATA, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
    }

    /**
     * Modification of a data in a table
     *
     * @param request HttpServletRequest
     * @param model the model
     * @return the modification data page
     */
    @View( VIEW_MODIFY_DATA )
    public String getModifyData( HttpServletRequest request, Models model )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) );
        List<String> listData = new ArrayList<>(  );

        for ( Field field : listFields )
        {
            listData.add( StringUtils.defaultString( getRawParameter( request, PARAMETER_PREFIX_COLUMN + field.getFieldName(  ) ) ) );
        }

        model.put( MARK_DATA_LIST, listData );
        model.put( MARK_FIELDS_TABLE, listFields );
        model.put( MARK_POOL_NAME, strPoolName );
        model.put( MARK_TABLE, TableHome.findByPrimaryKey( strPoolName, strTableName, getPlugin(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DATA, TEMPLATE_MODIFY_DATA, model );
    }

    /**
     * Process the modification of a data in a table
     *
     * @param request HttpServletRequest
     * @return the manage data page
     */
    @Action( ACTION_MODIFY_DATA )
    public String doModifyData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        List<FieldValues> listFieldValues = new ArrayList<>(  );

        for ( Field field : FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) ) )
        {
            FieldValues fieldValues = new FieldValues(  );
            fieldValues.setFieldName( field.getFieldName(  ) );
            fieldValues.setOldValue( StringUtils.defaultString( getRawParameter( request, PARAMETER_PREFIX_COLUMN + field.getFieldName(  ) ) ) );
            fieldValues.setNewValue( StringUtils.defaultString( getRawParameter( request, PARAMETER_PREFIX_NEW_VALUE + field.getFieldName(  ) ) ) );
            listFieldValues.add( fieldValues );
        }

        try
        {
            DataHome.update( strPoolName, strTableName, listFieldValues, AdminSqlConnectionService.getConnectionService( strPoolName ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_MANAGE_DATA, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
        }

        return redirect( request, VIEW_MANAGE_DATA, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
    }

    /**
     * Asks the confirmation of the removal of a row
     *
     * @param request HttpServletRequest
     * @return the confirmation message to delete a row
     */
    @View( value = VIEW_CONFIRM_REMOVE_DATA, securityTokenAction = ACTION_REMOVE_DATA )
    public String getConfirmRemoveData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DATA ) );
        url.addParameter( PARAMETER_POOL_NAME, strPoolName );
        url.addParameter( PARAMETER_TABLE_NAME, strTableName );

        for ( Field field : FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) ) )
        {
            String strParameter = PARAMETER_PREFIX_COLUMN + field.getFieldName(  );
            url.addParameter( strParameter, StringUtils.defaultString( getRawParameter( request, strParameter ) ) );
        }

        return redirect( request,
            AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_DATA, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Process the deleting of a data
     *
     * @param request HttpServletRequest
     * @return the manage data page
     */
    @Action( ACTION_REMOVE_DATA )
    public String doRemoveData( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );
        String strTableName = getRawParameter( request, PARAMETER_TABLE_NAME );
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return redirectStop( request, strError );
        }

        List<FieldValues> listFieldValues = new ArrayList<>(  );

        for ( Field field : FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) ) )
        {
            FieldValues fieldValues = new FieldValues(  );
            fieldValues.setFieldName( field.getFieldName(  ) );
            fieldValues.setOldValue( StringUtils.defaultString( getRawParameter( request, PARAMETER_PREFIX_COLUMN + field.getFieldName(  ) ) ) );
            listFieldValues.add( fieldValues );
        }

        try
        {
            DataHome.remove( strPoolName, strTableName, listFieldValues, AdminSqlConnectionService.getConnectionService( strPoolName ) );
        }
        catch ( AppException e )
        {
            return redirectSqlError( request, e, VIEW_MANAGE_DATA, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
        }

        return redirect( request, VIEW_MANAGE_DATA, Map.of( PARAMETER_POOL_NAME, strPoolName, PARAMETER_TABLE_NAME, strTableName ) );
    }

    /**
     * Loads the rows of a page of a table
     *
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listFields the fields of the table
     * @param listIndexes the indexes of the rows of the page
     * @return the rows, with the parameters that name each of them
     */
    private static List<DataRow> getRows( String strPoolName, String strTableName, List<Field> listFields, List<Integer> listIndexes )
    {
        List<DataRow> listRows = new ArrayList<>(  );

        if ( listIndexes.isEmpty(  ) )
        {
            return listRows;
        }

        for ( List<String> listValues : DataHome.findPage( strTableName, listIndexes.get( 0 ), listIndexes.size(  ),
                AdminSqlConnectionService.getConnectionService( strPoolName ) ) )
        {
            StringJoiner sjParameters = new StringJoiner( "&" );

            for ( int i = 0; ( i < listFields.size(  ) ) && ( i < listValues.size(  ) ); i++ )
            {
                sjParameters.add( URLEncoder.encode( PARAMETER_PREFIX_COLUMN + listFields.get( i ).getFieldName(  ), StandardCharsets.UTF_8 ) + "=" +
                    URLEncoder.encode( listValues.get( i ), StandardCharsets.UTF_8 ) );
            }

            listRows.add( new DataRow( listValues, sjParameters.toString(  ) ) );
        }

        return listRows;
    }

    /**
     * Tells whether a pool is declared
     *
     * @param strPoolName the name of the pool
     * @return true when the pool is declared in db.properties
     */
    private static boolean isPool( String strPoolName )
    {
        ReferenceList listPools = new ReferenceList(  );
        AppConnectionService.getPoolList( listPools );

        return ( strPoolName != null ) && listPools.stream(  ).anyMatch( item -> strPoolName.equals( item.getCode(  ) ) );
    }

    /**
     * Checks that a table exists in a pool
     *
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @return the key of the error message, null when the table exists
     */
    private String checkTable( String strPoolName, String strTableName )
    {
        if ( !isPool( strPoolName ) )
        {
            return MESSAGE_UNKNOWN_POOL;
        }

        if ( ( strTableName == null ) ||
                TableHome.findAllByPool( strPoolName, getPlugin(  ) ).stream(  ).noneMatch( table -> strTableName.equals( table.getTableName(  ) ) ) )
        {
            return MESSAGE_UNKNOWN_TABLE;
        }

        return null;
    }

    /**
     * Checks that a field exists in a table
     *
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param strFieldName the name of the field
     * @return the key of the error message, null when the field exists
     */
    private String checkField( String strPoolName, String strTableName, String strFieldName )
    {
        String strError = checkTable( strPoolName, strTableName );

        if ( strError != null )
        {
            return strError;
        }

        if ( ( strFieldName == null ) ||
                FieldHome.findAll( strPoolName, strTableName, getPlugin(  ) ).stream(  ).noneMatch( field -> strFieldName.equals( field.getFieldName(  ) ) ) )
        {
            return MESSAGE_UNKNOWN_FIELD;
        }

        return null;
    }

    /**
     * Returns the length typed for a field type
     *
     * @param request HttpServletRequest
     * @param nIdFieldType the id of the field type
     * @return the length, empty for a type without length, null when a length is missing
     */
    private static String getLength( HttpServletRequest request, int nIdFieldType )
    {
        if ( TYPES_WITHOUT_LENGTH.contains( nIdFieldType ) )
        {
            return "";
        }

        String strFieldLengthType = getRawParameter( request, PARAMETER_FIELD_LENGTH_TYPE );

        return StringUtils.isEmpty( strFieldLengthType ) ? null : strFieldLengthType;
    }

    /**
     * Returns a parameter as typed by the user
     *
     * @param request HttpServletRequest
     * @param strParameter the name of the parameter
     * @return the value, null when absent
     */
    private static String getRawParameter( HttpServletRequest request, String strParameter )
    {
        return StringEscapeUtils.unescapeHtml4( request.getParameter( strParameter ) );
    }

    /**
     * Returns a numeric parameter
     *
     * @param request HttpServletRequest
     * @param strParameter the name of the parameter
     * @return the value, 0 when absent or not numeric
     */
    private static int getInt( HttpServletRequest request, String strParameter )
    {
        String strValue = request.getParameter( strParameter );

        return StringUtils.isNumeric( strValue ) ? Integer.parseInt( strValue ) : 0;
    }

    /**
     * Tells whether a checkbox is checked
     *
     * @param request HttpServletRequest
     * @param strParameter the name of the checkbox
     * @return true when checked
     */
    private static boolean isChecked( HttpServletRequest request, String strParameter )
    {
        return CHECKBOX_ON.equals( request.getParameter( strParameter ) );
    }

    /**
     * Returns the message of a SQL error
     *
     * @param e the exception
     * @return the message of its cause, else its own message
     */
    private static String getErrorMessage( AppException e )
    {
        return ( e.getCause(  ) != null ) ? e.getCause(  ).getMessage(  ) : StringUtils.defaultString( e.getMessage(  ) );
    }

    /**
     * Redirects to a stop message
     *
     * @param request HttpServletRequest
     * @param strMessageKey the key of the message
     * @return the redirection
     */
    private String redirectStop( HttpServletRequest request, String strMessageKey )
    {
        return redirect( request, AdminMessageService.getMessageUrl( request, strMessageKey, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Redirects to the message of a SQL error, which goes back to a view
     *
     * @param request HttpServletRequest
     * @param e the exception
     * @param strView the view to go back to
     * @param mapParameters the parameters of the view
     * @return the redirection
     */
    private String redirectSqlError( HttpServletRequest request, AppException e, String strView, Map<String, String> mapParameters )
    {
        UrlItem url = new UrlItem( getViewUrl( strView ) );
        mapParameters.forEach( url::addParameter );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_SQL_ERROR_IN_QUERY, new Object[] { getErrorMessage( e ) },
                url.getUrl(  ), AdminMessage.TYPE_STOP ) );
    }
}
