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


/**
 * Creates a new instance of Table
 */
public class Table
{
    /** Creates a new instance of Table */

    // Variables declarations 
    private String _strTableName;
    private String _strComment;
    private String _strPrimaryKey;
    private String _strIndex;
    private int _nNumberOfFields;

    /**
     * Gets the name of the table
     * @return the name of the table
     */
    public String getTableName(  )
    {
        return _strTableName;
    }

    /**
     * Sets the name of the table
     * @param strTableName the name of the table
     */
    public void setTableName( String strTableName )
    {
        _strTableName = strTableName;
    }

    public void setFieldName( String strFieldName )
    {
        
    }

    public void setFieldNull( String strFieldName )
    {

    }
    /**
     * Gets some comments
     * @return comments on table
     */
    public String getComment(  )
    {
        return _strComment;
    }

    /**
     * Sets comments on a table
     * @param strComment comments on a table
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    /**
     * Returns the PrimaryKey
     * @return The PrimaryKey
     */
    public String getPrimaryKey(  )
    {
        return _strPrimaryKey;
    }

    /**
     * Sets the PrimaryKey
     * @param strPrimaryKey The PrimaryKey
     */
    public void setPrimaryKey( String strPrimaryKey )
    {
        _strPrimaryKey = strPrimaryKey;
    }

    /**
     * Returns the Index
     * @return The Index
     */
    public String getIndex(  )
    {
        return _strIndex;
    }

    /**
     * Sets the Index
     * @param strIndex The Index
     */
    public void setIndex( String strIndex )
    {
        _strIndex = strIndex;
    }

    /**
     * Gets number of fields of the table
     * @return the number of fields
     */
    public int getNumberOfFields(  )
    {
        return _nNumberOfFields;
    }

    /**
     * Sets the number of fields
     * @param nNumberOfFields the number of fields
     */
    public void setNumberOfFields( int nNumberOfFields )
    {
        _nNumberOfFields = nNumberOfFields;
    }
}
