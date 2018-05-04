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
package fr.paris.lutece.plugins.adminsql.business;


/**
 * This is the business class for the object StructureTable
 */
public class StructureTable
{
    // Variables declarations 
    private String _strFieldValue;
    private String _strTypeValue;
    private String _strNullValue;
    private String _strKeyValue;
    private String _strDefaultValue;
    private String _strExtraValue;

    /**
     * Returns the FieldValue
     * @return The FieldValue
     */
    public String getFieldValue(  )
    {
        return _strFieldValue;
    }

    /**
     * Sets the FieldValue
     * @param strFieldValue The FieldValue
     */
    public void setFieldValue( String strFieldValue )
    {
        _strFieldValue = strFieldValue;
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
     * Returns the NullValue
     * @return The NullValue
     */
    public String getNullValue(  )
    {
        return _strNullValue;
    }

    /**
     * Sets the NullValue
     * @param strNullValue The NullValue
     */
    public void setNullValue( String strNullValue )
    {
        _strNullValue = strNullValue;
    }

    /**
     * Returns the KeyValue
     * @return The KeyValue
     */
    public String getKeyValue(  )
    {
        return _strKeyValue;
    }

    /**
     * Sets the KeyValue
     * @param strKeyValue The KeyValue
     */
    public void setKeyValue( String strKeyValue )
    {
        _strKeyValue = strKeyValue;
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
     * @param strDefaultValue The DefaultValue
     */
    public void setDefaultValue( String strDefaultValue )
    {
        _strDefaultValue = strDefaultValue;
    }

    /**
     * Returns the ExtraValue
     * @return The ExtraValue
     */
    public String getExtraValue(  )
    {
        return _strExtraValue;
    }

    /**
     * Sets the ExtraValue
     * @param strExtraValue The ExtraValue
     */
    public void setExtraValue( String strExtraValue )
    {
        _strExtraValue = strExtraValue;
    }
}
