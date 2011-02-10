/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * The class calling the business objects
 */
public final class FieldHome
{
    private static IFieldDAO _dao = (IFieldDAO) SpringContextService.getPluginBean( "adminsql", "fieldDAO" );

    /**
     * Find the list of all fields of a table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param plugin Plugin objet
     * @return list of all the fields
     */
    public static List<Field> findAll( String strPoolName, String strTableName, Plugin plugin )
    {
        return _dao.selectFieldList( strPoolName, strTableName, plugin );
    }

    /**
     * Find the structure of a field
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param strFieldName the name of the field
     * @param plugin Plugin object
     * @return the structure of a field
     */
    public static Field findByPrimaryKey( String strPoolName, String strTableName, String strFieldName, Plugin plugin )
    {
        return _dao.load( strPoolName, strTableName, strFieldName, plugin );
    }

    /**
     * Find the type label of the field in adminsql database
     * @return the type label of the field in adminsql database
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsq
     */
    public static ReferenceList findFieldType( String strPoolName, Plugin plugin )
    {
        return _dao.selectFieldTypeList( strPoolName, plugin );
    }

    /**
     * Find the key of the field in adminsql database if exists
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsq
     * @return the key of the field in adminsql database if exists
     */
    public static ReferenceList findFieldKey( String strPoolName, Plugin plugin )
    {
        return _dao.selectFieldKeyList( strPoolName, plugin );
    }

    /**
     * Find the null value of the field in adminsql database if exists
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsq
     * @return the null value of the field in adminsql database if exists
     */
    public static ReferenceList findFieldNull( String strPoolName, Plugin plugin )
    {
        return _dao.selectFieldNullList( strPoolName, plugin );
    }

    /**
     * Create an intance of a field
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param field Field object
     * @param plugin Plugin adminsql
     * @return list of fields
     */
    public static Field create( String strPoolName, String strTableName, Field field, Plugin plugin )
    {
        _dao.insert( strPoolName, strTableName, field, plugin );

        return field;
    }

    /**
     * Delete a field in a table
     * @param strTableName the name of the table
     * @param strFieldName the name of the field
     * @param plugin Plugin adminsql
     */
    public static void remove( String strTableName, String strFieldName, Plugin plugin )
    {
        _dao.delete( strTableName, strFieldName, plugin );
    }

    /**
     * Update a field structure
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param field Field object
     * @param strFieldNameToModify the name of the field before modification for the request
     * @param nIdOldFieldKey keep the key of the field
     * @param plugin Plugin adminsql
     * @return list of fields
     */
    public static Field update( String strPoolName, String strTableName, Field field, String strFieldNameToModify,
        int nIdOldFieldKey, Plugin plugin )
    {
        _dao.store( strPoolName, strTableName, field, strFieldNameToModify, nIdOldFieldKey, plugin );

        return field;
    }
}
