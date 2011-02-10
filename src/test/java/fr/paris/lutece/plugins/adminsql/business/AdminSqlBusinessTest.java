/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.paris.lutece.plugins.adminsql.business;


import fr.paris.lutece.portal.service.database.PluginConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author coudercx
 */
public class AdminSqlBusinessTest extends LuteceTestCase {

    private final static String POOL_PORTAL = "portal";
    private final static String TABLE_NAME_MODIFY = "bibibibi";
    private final static String FIELD_NAME_1 = "FIELD_NAME_1";
    private final static String FIELD_NAME_2 = "FIELD_NAME_2";
    private final static String DATA_1 = "1";
    private final static String DATA_1_updated = "2";
    private final static String DATA_2 = "2";
    private final static String DATA_2_updated = "3";
    private static final int ID_FIELD_TYPE = 7;
    private final static String LENGTH_FIELD_TYPE = "8";
    private static final int ID_KEY_VALUE = 1;
    //private static final int ID_KEY_VALUE_2 = 1;
    private static final int ID_NULL_VALUE = 1;
    private static final String DEFAULT_VALUE = "";

    //Constants for the test of the modification of a field
    private static final String MODIFY_FIELD_NAME = "TestFieldName";
    private static final int MODIFY_ID_FIELD_TYPE = 7;
    private static final String MODIFY_LENGTH_TYPE_VALUE = "7";
    private static final int MODIFY_ID_KEY_VALUE = 1;
    private static final int MODIFY_ID_NULL_VALUE = 1;
    private static final String MODIFY_DEFAULT_VALUE = "1";

    //Request
    private static String REQUEST = "SELECT * from ";
    
    /*@Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }*/

    public void testAdminSqlBusiness( )
    {
        Plugin plugin = PluginService.getPlugin( "adminsql" );
        plugin.setPoolName(POOL_PORTAL);

        PluginConnectionService connectionService = new PluginConnectionService( plugin.getDbPoolName(  ) );


        if ( ( plugin != null ) && plugin.isInstalled(  ) )
        {
            Table table = new Table(  );
            table.setTableName("bibi");

            Field field = new Field();
            field.setFieldName( FIELD_NAME_1 );
            field.setIdTypeValue( ID_FIELD_TYPE );
            field.setLengthTypeValue( LENGTH_FIELD_TYPE );
            field.setIdKeyValue( ID_KEY_VALUE );
            field.setIdNullValue( ID_NULL_VALUE );
            field.setDefaultValue( DEFAULT_VALUE );

            String strTableName = table.getTableName(  );

            TableHome.create( POOL_PORTAL , table, field, plugin);

            Table tableStored = TableHome.findByPrimaryKey(POOL_PORTAL, strTableName, plugin);
            Field fieldStored = FieldHome.findByPrimaryKey(POOL_PORTAL, strTableName, FIELD_NAME_1, plugin );

            assertEquals( table.getTableName( ), tableStored.getTableName( ) );
            assertEquals( field.getFieldName( ), fieldStored.getFieldName( ) );
            assertEquals( field.getIdTypeValue(), fieldStored.getIdTypeValue( ) );
            assertEquals( field.getLengthTypeValue( ), fieldStored.getLengthTypeValue( ) );
            assertEquals( field.getIdKeyValue( ), fieldStored.getIdKeyValue( ) );
            assertEquals( field.getIdNullValue( ), fieldStored.getIdNullValue( ) );
            assertEquals( field.getDefaultValue( ), fieldStored.getDefaultValue( ) );

            Field field_2 = new Field();
            field_2.setFieldName( FIELD_NAME_2 );
            field_2.setIdTypeValue( ID_FIELD_TYPE );
            field_2.setLengthTypeValue( LENGTH_FIELD_TYPE );
            field_2.setIdKeyValue( ID_KEY_VALUE );
            field_2.setIdNullValue( ID_NULL_VALUE );
            field_2.setDefaultValue( DEFAULT_VALUE );

            FieldHome.create(POOL_PORTAL, strTableName, field_2, plugin);

            System.out.println("insert data");
            List<String> listData = new ArrayList( );
            listData.add(DATA_1);
            listData.add(DATA_2);
            List<String> listFields = new ArrayList( );
            listFields.add( FIELD_NAME_1 );
            listFields.add( FIELD_NAME_2 );

            DataHome.create(POOL_PORTAL, strTableName, listData, listFields, connectionService);

            System.out.println("update Table");
            Table table_1 = new Table( );
            table_1.setTableName(strTableName);
            TableHome.update(TABLE_NAME_MODIFY, table);
            tableStored = TableHome.findByPrimaryKey(POOL_PORTAL, TABLE_NAME_MODIFY, plugin);
            assertEquals( TABLE_NAME_MODIFY, tableStored.getTableName( ) );

            System.out.println("update Field");
            Field fieldToModify = FieldHome.findByPrimaryKey( POOL_PORTAL, TABLE_NAME_MODIFY, FIELD_NAME_2, plugin );
            fieldToModify.setFieldName( MODIFY_FIELD_NAME );
            fieldToModify.setIdTypeValue( MODIFY_ID_FIELD_TYPE );
            fieldToModify.setLengthTypeValue( MODIFY_LENGTH_TYPE_VALUE );
            fieldToModify.setIdKeyValue( MODIFY_ID_KEY_VALUE );
            fieldToModify.setIdNullValue( MODIFY_ID_NULL_VALUE );
            fieldToModify.setDefaultValue( MODIFY_DEFAULT_VALUE );
            FieldHome.update( POOL_PORTAL, TABLE_NAME_MODIFY, fieldToModify, FIELD_NAME_2, ID_KEY_VALUE, plugin );
            assertEquals( fieldToModify.getFieldName( ), MODIFY_FIELD_NAME );

            /*System.out.println("update Data");
            List<FieldValues> listFieldsValues = new ArrayList<FieldValues>( );
            String strFieldNameUpdate = FIELD_NAME_1;
            String strDataUpdate = DATA_1_updated;
            String strFieldName = ;
            String strData = request.getParameter( strFieldName );*/

            /*FieldValues fieldValues = new FieldValues(  );
            fieldValues.setFieldName( strFieldName );
            fieldValues.setOldValue( strData );
            fieldValues.setNewValue( strDataUpdate );
            listFieldsValues.add( MODIFY_FIELD_NAME );
            listFieldsValues.add( FIELD_NAME_2 );*/

            System.out.println("find all in user request");
            REQUEST += TABLE_NAME_MODIFY;
            assertNotNull( DataHome.findAllInUserRequest( POOL_PORTAL, REQUEST, connectionService ) );

            /*System.out.println("delete Data");
            List<String> listConcatFieldNameAndData = new ArrayList(  );
            listConcatFieldNameAndData.add( FIELD_NAME_1 + "='" + DATA_1_updated + "'");
            listConcatFieldNameAndData.add( MODIFY_FIELD_NAME + "='" + DATA_2_updated + "'");
            DataHome.remove( POOL_PORTAL, TABLE_NAME_MODIFY, listConcatFieldNameAndData, connectionService );*/
            System.out.println("delete Field");
            FieldHome.remove( TABLE_NAME_MODIFY, MODIFY_FIELD_NAME, plugin );

            System.out.println("delete Table");
            TableHome.remove( TABLE_NAME_MODIFY, plugin );
        }
    }
}
