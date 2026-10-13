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
package fr.paris.lutece.plugins.adminsql.service;

import fr.paris.lutece.plugins.adminsql.business.DataHome;
import fr.paris.lutece.plugins.adminsql.business.Field;
import fr.paris.lutece.plugins.adminsql.business.FieldHome;
import fr.paris.lutece.plugins.adminsql.business.Table;
import fr.paris.lutece.plugins.adminsql.business.TableHome;
import fr.paris.lutece.portal.service.plugin.Plugin;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;


/**
 * Builds the SQL script of a database
 */
@ApplicationScoped
public class AdminSqlExportService
{
    private static final String NULL_NO = "NO";
    private static final String KEY_PRIMARY = "PRI";
    private static final String TYPE_TEXT = "text";
    private static final String DEFAULT_NULL = "NULL";
    private static final String DEFAULT_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";

    /**
     * Builds the SQL script of the tables of a pool
     *
     * @param strPoolName the name of the pool
     * @param bDatabase true to drop and create the database first
     * @param bStructure true to write the structure of the tables
     * @param bData true to write the rows of the tables
     * @param plugin the adminsql plugin
     * @return the SQL script
     */
    public String exportDatabase( String strPoolName, boolean bDatabase, boolean bStructure, boolean bData, Plugin plugin )
    {
        StringBuilder sbDump = new StringBuilder(  );
        sbDump.append( "--\n-- Base de données : `" ).append( strPoolName ).append( "`\n--\n\n" );
        sbDump.append( "-- -------------------------------\n\n" );

        if ( bDatabase )
        {
            sbDump.append( "DROP DATABASE IF EXISTS " ).append( strPoolName ).append( ";\nCREATE DATABASE " )
                  .append( strPoolName ).append( ";\nUSE " ).append( strPoolName ).append( ";\n\n" );
        }

        for ( Table table : TableHome.findAllByPool( strPoolName, plugin ) )
        {
            String strTableName = table.getTableName(  );
            List<Field> listFields = FieldHome.findAll( strPoolName, strTableName, plugin );

            if ( bStructure )
            {
                appendStructure( sbDump, strTableName, listFields );
            }

            if ( bData )
            {
                appendData( sbDump, strPoolName, strTableName, listFields );
            }
        }

        return sbDump.toString(  );
    }

    /**
     * Appends the creation script of a table
     *
     * @param sbDump the script
     * @param strTableName the name of the table
     * @param listFields the fields of the table
     */
    private static void appendStructure( StringBuilder sbDump, String strTableName, List<Field> listFields )
    {
        StringBuilder sbLines = new StringBuilder(  );
        StringBuilder sbPrimaryKey = new StringBuilder(  );

        sbDump.append( "--\n-- Structure de la table `" ).append( strTableName ).append( "`\n--\n\n" );
        sbDump.append( "DROP TABLE IF EXISTS `" ).append( strTableName ).append( "`;\n" );
        sbDump.append( "CREATE TABLE `" ).append( strTableName ).append( "`(\n" );

        for ( Field field : listFields )
        {
            String strNull = NULL_NO.equals( field.getLabelNullValue(  ) ) ? "NOT NULL" : "";

            if ( KEY_PRIMARY.equals( field.getLabelKeyValue(  ) ) )
            {
                sbPrimaryKey.append( '`' ).append( field.getFieldName(  ) ).append( "`," );
            }

            sbLines.append( '`' ).append( field.getFieldName(  ) ).append( "` " ).append( field.getTypeValue(  ) )
                   .append( ' ' ).append( strNull ).append( ' ' ).append( getDefault( field ) ).append( ",\n" );
        }

        String strLines = sbLines.toString(  );

        if ( sbPrimaryKey.length(  ) > 0 )
        {
            strLines += ( "PRIMARY KEY (" + sbPrimaryKey.substring( 0, sbPrimaryKey.length(  ) - 1 ) + ")\n);" );
        }
        else
        {
            strLines = strLines.substring( 0, strLines.length(  ) - 2 ) + ");";
        }

        sbDump.append( strLines ).append( "\n\n" );
    }

    /**
     * Returns the default clause of a field
     *
     * @param field the field
     * @return the default clause, empty when the field has none
     */
    private static String getDefault( Field field )
    {
        String strDefault = field.getDefaultValue(  );

        if ( strDefault.isEmpty(  ) || TYPE_TEXT.equals( field.getTypeValue(  ) ) )
        {
            return "";
        }

        if ( DEFAULT_NULL.equals( strDefault ) || DEFAULT_CURRENT_TIMESTAMP.equals( strDefault ) )
        {
            return "default " + strDefault;
        }

        return "default '" + strDefault + "'";
    }

    /**
     * Appends the insertion script of the rows of a table
     *
     * @param sbDump the script
     * @param strPoolName the name of the pool
     * @param strTableName the name of the table
     * @param listFields the fields of the table
     */
    private static void appendData( StringBuilder sbDump, String strPoolName, String strTableName, List<Field> listFields )
    {
        StringBuilder sbFieldNames = new StringBuilder(  );

        for ( Field field : listFields )
        {
            sbFieldNames.append( sbFieldNames.length(  ) == 0 ? "(" : "," ).append( '`' ).append( field.getFieldName(  ) ).append( '`' );
        }

        sbFieldNames.append( ')' );

        List<List<String>> listRows = DataHome.findAll( strTableName, AdminSqlConnectionService.getConnectionService( strPoolName ) );

        if ( listRows.isEmpty(  ) )
        {
            sbDump.append( "--\n-- Pas de donnees dans `" ).append( strTableName ).append( "`\n--\n\n" );

            return;
        }

        sbDump.append( "--\n-- Contenu de la table `" ).append( strTableName ).append( "`\n--\n\n" );

        for ( List<String> listRow : listRows )
        {
            StringBuilder sbValues = new StringBuilder(  );

            for ( String strData : listRow )
            {
                sbValues.append( sbValues.length(  ) == 0 ? "(" : "," ).append( '\'' ).append( strData.replace( "'", "''" ) ).append( '\'' );
            }

            sbDump.append( "INSERT INTO `" ).append( strTableName ).append( '`' ).append( sbFieldNames ).append( " VALUES " )
                  .append( sbValues ).append( ");\n" );
        }

        sbDump.append( '\n' );
    }
}
