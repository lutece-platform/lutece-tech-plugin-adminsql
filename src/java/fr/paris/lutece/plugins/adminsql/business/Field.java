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
 * Creates a new instance of Field
 */
public class Field
{
    /** Creates a new instance of Row */

    // Variables declarations 
    private String _strFieldName;
    private String _strTypeValue;
    private String _strLabelTypeValue;
    private String _strLengthTypeValue;
    private String _strLabelNullValue;
    private String _strLabelKeyValue;
    private String _strDefaultValue;
    private String _strCommentValue;
    private String _strAfterTheField;
    private int _nIdFieldName;
    private int _nIdTypeValue;
    private int _nIdNullValue;
    private int _nIdKeyValue;
    private int _nFieldEndOfTable;
    private int _nFieldBeginningOfTable;
    private int _nFieldAfterAField;
    private int _nPlaceOfField;

    /**
     * Returns the idFieldName
     * @return The idFieldName
     */
    public int getIdFieldName(  )
    {
        return _nIdFieldName;
    }

    /**
     * Sets the idFieldName
     * @param nIdFieldName set the id field name
     */
    public void setIdFieldName( int nIdFieldName )
    {
        _nIdFieldName = nIdFieldName;
    }

    /**
     * Get the field name
     * @return the field name
     */
    public String getFieldName(  )
    {
        return _strFieldName;
    }

    /**
     * Sets the field name
     * @param strFieldName the name of the field
     */
    public void setFieldName( String strFieldName )
    {
        _strFieldName = strFieldName;
    }

    /**
     * Returns the TypeValue
     * @return The TypeValue
     */
    public String getTypeValue(  )
    {
        return _strTypeValue;
    }

    /**
     * Sets the TypeValue
     * @param strTypeValue The TypeValue
     */
    public void setTypeValue( String strTypeValue )
    {
        _strTypeValue = strTypeValue;
    }

    /**
        * Returns the TypeValue
        * @return The TypeValue
        */
    public String getLabelTypeValue(  )
    {
        return _strLabelTypeValue;
    }

    /**
     * Sets the TypeValue
     * @param strLabelTypeValue set the label type value
     */
    public void setLabelTypeValue( String strLabelTypeValue )
    {
        _strLabelTypeValue = strLabelTypeValue;
    }

    /**
     * Get the length of the type
     * @return get the length of the type
     */
    public String getLengthTypeValue(  )
    {
        return _strLengthTypeValue;
    }

    /**
     * Sets the TypeValue
     * @param strLengthTypeValue set the length of the type value
     */
    public void setLengthTypeValue( String strLengthTypeValue )
    {
        _strLengthTypeValue = strLengthTypeValue;
    }

    /**
     * Returns the NullValue
     * @return The NullValue
     */
    public String getLabelNullValue(  )
    {
        return _strLabelNullValue;
    }

    /**
     * Sets the NullValue
     * @param strLabelNullValue set the label of the null value
     */
    public void setLabelNullValue( String strLabelNullValue )
    {
        _strLabelNullValue = strLabelNullValue;
    }

    /**
     * Returns the KeyValue
     * @return The KeyValue
     */
    public String getLabelKeyValue(  )
    {
        return _strLabelKeyValue;
    }

    /**
     * Sets the KeyValue
     * @param strLabelKeyValue set the label of the key value
     */
    public void setLabelKeyValue( String strLabelKeyValue )
    {
        _strLabelKeyValue = strLabelKeyValue;
    }

    /**
     * Returns the DefaultValue
     * @return The DefaultValue
     */
    public String getDefaultValue(  )
    {
        return _strDefaultValue;
    }

    /**
     * Sets the DefaultValue
     * @param strDefaultValue the dafault value
     */
    public void setDefaultValue( String strDefaultValue )
    {
        _strDefaultValue = strDefaultValue;
    }

    /**
     * Get id type value
     * @return id type value
     */
    public int getIdTypeValue(  )
    {
        return _nIdTypeValue;
    }

    /**
     * Sets id type value
     * @param nIdTypeValue set the id of the type value
     */
    public void setIdTypeValue( int nIdTypeValue )
    {
        _nIdTypeValue = nIdTypeValue;
    }

    /**
     * Gets id null value
     * @return id null value
     */
    public int getIdNullValue(  )
    {
        return _nIdNullValue;
    }

    /**
     * Sets id null value
     * @param nIdNullValue the id null value
     */
    public void setIdNullValue( int nIdNullValue )
    {
        _nIdNullValue = nIdNullValue;
    }

    /**
     * Gets the id key value
     * @return id key vallue
     */
    public int getIdKeyValue(  )
    {
        return _nIdKeyValue;
    }

    /**
     * Sets the id key value
     * @param nIdKeyValue id key value
     */
    public void setIdKeyValue( int nIdKeyValue )
    {
        _nIdKeyValue = nIdKeyValue;
    }
    
    /**
     * Gets the end of table field
     * @return the end of table field
     */
    public int getFieldEndOfTable(  )
    {
        return _nFieldEndOfTable;
    }

    /**
     * Sets the end of table field
     * @param nFieldEndOfTable the end of table field
     */
    public void setFieldEndOfTable( int nFieldEndOfTable )
    {
        _nFieldEndOfTable = nFieldEndOfTable;
    }

    /**
     * Gets the beginning of table field
     * @return the beginning of table field
     */
    public int getFieldBeginningOfTable(  )
    {
        return _nFieldBeginningOfTable;
    }

    /**
     * Sets the beginning of table field
     * @param nFieldBeginningOfTable the beginning of table field
     */
    public void setFieldBeginningOfTable( int nFieldBeginningOfTable )
    {
        _nFieldBeginningOfTable = nFieldBeginningOfTable;
    }

    /**
     * Gets the field after a field
     * @return a field name
     */
    public int getFieldAfterAField(  )
    {
        return _nFieldAfterAField;
    }

    /**
     * Sets a field after a field
     * @param nFieldAfterAField a field
     */
    public void setFieldAfterAField( int nFieldAfterAField )
    {
        _nFieldAfterAField = nFieldAfterAField;
    }

    /**
     * Gets place of field
     * @return place of field
     */
    public int getPlaceOfField(  )
    {
        return _nPlaceOfField;
    }

    /**
     * Sets place of field
     * @param nPlaceOfField id place of field
     */
    public void setPlaceOfField( int nPlaceOfField )
    {
        _nPlaceOfField = nPlaceOfField;
    }

    /**
     * Gets after the field
     * @return the field before
     */
    public String getAfterTheField(  )
    {
        return _strAfterTheField;
    }

    /**
     * Sets the field before the insertion
     * @param strAfterTheField the field before the insertion
     */
    public void setAfterTheField( String strAfterTheField )
    {
        _strAfterTheField = strAfterTheField;
    }
}
