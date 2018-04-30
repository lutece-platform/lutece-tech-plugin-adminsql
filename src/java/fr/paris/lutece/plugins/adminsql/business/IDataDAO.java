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
