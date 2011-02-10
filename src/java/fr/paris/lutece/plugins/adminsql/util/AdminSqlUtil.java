/*
 * AdminSqlUtil.java
 *
 * Created on 4 juillet 2008, 15:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.adminsql.util;


/**
 * This c
 */
public final class AdminSqlUtil
{
    public static String[] getFieldDetails( String strFieldType )
    {
        String[] strResult = { "", "" };
        String str1 = strFieldType.substring( 0, strFieldType.indexOf( ")" ) );
        strResult[0] = str1.substring( 0, strFieldType.indexOf( "(" ) );
        strResult[1] = str1.substring( strFieldType.indexOf( "(" ) + 1 );

        return strResult;
    }
}
