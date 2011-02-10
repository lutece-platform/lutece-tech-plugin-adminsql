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

import fr.paris.lutece.plugins.adminsql.business.Field;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.sql.SQLException;

import java.util.List;


/**
 * Lists all methods which are in FieldDAO
 */
public interface IFieldDAO
{
    /**
     * Find the field list
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param plugin Plugin adminsql
     * @return the field list
     */
    List<Field> selectFieldList( String strPoolName, String strTableName, Plugin plugin );

    /**
     * Load a field from the table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param strFieldName the name of the field
     * @param plugin Plugin adminsql
     * @return a field object
     */
    Field load( String strPoolName, String strTableName, String strFieldName, Plugin plugin );

    /**
     * lists the field list possiblilities to create or modify fields form
     * @param strPoolName the name of the pool
     * @param plugin plugin adminsql
     * @return Field list
     */
    ReferenceList selectFieldTypeList( String strPoolName, Plugin plugin );

    /**
     * lists the field list possiblilities to create or modify fields form
     * @param strPoolName the name of the pool
     * @param plugin plugin adminsql
     * @return Field list
     */
    ReferenceList selectFieldKeyList( String strPoolName, Plugin plugin );

    /**
     * Find the reference list of null list
     * @param strPoolName the name of the pool
     * @param plugin Plugin adminsql
     * @return reference list of null list
     */
    ReferenceList selectFieldNullList( String strPoolName, Plugin plugin );

    /**
     * Insert a field into a table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the pool
     * @param field Field object
     * @param plugin Plugin adminsql
     */
    void insert( String strPoolName, String strTableName, Field field, Plugin plugin );

    /**
     * Delete field from a table
     * @param strTableName the name of the table
     * @param strFieldName the name of the field
     * @param plugin Plugin adminsql
     */
    void delete( String strTableName, String strFieldName, Plugin plugin );

    /**
     * Store a field into a table
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param field Field object
     * @param strFieldNameToModify the name of the field to modify
     * @param nIdOldFieldKey the id of the field key
     * @param plugin Plugin adminsql
     */
    void store( String strPoolName, String strTableName, Field field, String strFieldNameToModify, int nIdOldFieldKey,
        Plugin plugin );
}
