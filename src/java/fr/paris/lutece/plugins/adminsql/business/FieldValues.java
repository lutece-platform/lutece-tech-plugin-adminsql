/*
 * FieldValues.java
 *
 * Created on 26 décembre 2008, 15:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.adminsql.business;


/**
 *
 * @author coudercx
 */
public class FieldValues
{
    private String strFieldName;
    private String strOldValue;
    private String strNewValue;

    /** Creates a new instance of FieldValues */
    public FieldValues(  )
    {
    }

    /**
     *
     * @return
     */
    public String getFieldName(  )
    {
        return strFieldName;
    }

    /**
     *
     * @param strFieldName
     */
    public void setFieldName( String strFieldName )
    {
        this.strFieldName = strFieldName;
    }

    /**
     *
     * @return
     */
    public String getOldValue(  )
    {
        return strOldValue;
    }

    /**
     *
     * @param strOldValue
     */
    public void setOldValue( String strOldValue )
    {
        this.strOldValue = strOldValue;
    }

    /**
     *
     * @return
     */
    public String getNewValue(  )
    {
        return strNewValue;
    }

    /**
     *
     * @param strNewValue
     */
    public void setNewValue( String strNewValue )
    {
        this.strNewValue = strNewValue;
    }
}
