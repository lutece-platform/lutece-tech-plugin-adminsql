/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;


import java.util.List;


/**
 * The class calling the business objects
 */
public final class TableHome
{
    private static ITableDAO _dao = (ITableDAO) SpringContextService.getPluginBean( "adminsql", "tableDAO" );

    /**
     * Find the tables list of a pool
     * @param plugin Plugin adminsq
     * @return list of tables
     */
    public static List<Table> findAll( Plugin plugin )
    {
        return _dao.selectTableList( plugin );
    }

    /**
     * Find the tables list of a pool
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsq
     * @return list of tables
     */
    public static List<Table> findAllByPool( String strPoolName, Plugin plugin )
    {
        return _dao.selectTableListByPool( strPoolName, plugin );
    }

    /**
     * Find table in tables list
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param plugin Plugin adminsql
     * @return table in tables list
     */
    public static Table findByPrimaryKey( String strPoolName, String strTableName, Plugin plugin )
    {
         return  _dao.load( strPoolName, strTableName, plugin );
    }

    /**
     * Create an intance of a field
     * @param strPoolName the name of the pool
     * @param table Table object
     * @param field field object
     * @param plugin Plugin adminsql
     * @return list of the tables
     */
    public static void create( String strPoolName, Table table, Field field, Plugin plugin )//throws CreationException 1.Field null
    {
         _dao.insert( strPoolName, table, field, plugin );
    }

    /**
     * Delete a table
     * @param strTableName the name of the table
     * @param plugin Plugin adminsql
     */
    public static void remove( String strTableName, Plugin plugin )
    {
        _dao.delete( strTableName, plugin );
    }

    /**
     * Update a table on a database
     * @param strTableNameToModify
     * @param table Table object
     */
    public static void update( String strTableNameToModify, Table table )
    {
        _dao.store( strTableNameToModify, table );
    }
}
