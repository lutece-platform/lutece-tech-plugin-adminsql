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

import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.database.PluginConnectionService;

import java.util.Hashtable;


/**
 * This class provides AdminSqlConnectionService object
 */
public final class AdminSqlConnectionService
{
    //Constants
    private static AdminSqlConnectionService _singleton = new AdminSqlConnectionService(  );
    private static Hashtable _htConnectionServices = new Hashtable(  );

    /**
     * Creates a new AdminSqlConnectionService object.
     */
    private AdminSqlConnectionService(  )
    {
    }

    /**
     * Get a connection service corresponding to the given poolname. If the pool
     * name is empty, the default connection service of the current plugin is returned
     *
     * @return A Connection Service
     * @param strPoolName The Poolname
     */
    public static PluginConnectionService getConnectionService( String strPoolName )
    {
        PluginConnectionService connectionService = null;

        if ( ( strPoolName != null ) && ( !strPoolName.equals( "" ) ) )
        {
            if ( _htConnectionServices.contains( strPoolName ) )
            {
                connectionService = (PluginConnectionService) _htConnectionServices.get( strPoolName );
            }
            else
            {
                connectionService = new PluginConnectionService( strPoolName );
                _htConnectionServices.put( strPoolName, connectionService );
            }
        }
        else
        {
            connectionService = AppConnectionService.getDefaultConnectionService(  );
        }

        return connectionService;
    }

    /**
     * Provides singleton to connection service
     * @return Returns an instance of the connection service
     */
    public static AdminSqlConnectionService getInstance(  )
    {
        return _singleton;
    }
}
