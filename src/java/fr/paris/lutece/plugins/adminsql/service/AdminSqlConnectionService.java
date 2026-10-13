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
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class provides the connection services of the pools browsed by adminsql
 */
public final class AdminSqlConnectionService
{
    /** The name of the plugin */
    public static final String PLUGIN_NAME = "adminsql";
    private static final Map<String, PluginConnectionService> _mapConnectionServices = new ConcurrentHashMap<>(  );

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
        if ( StringUtils.isEmpty( strPoolName ) )
        {
            return AppConnectionService.getDefaultConnectionService(  );
        }

        return _mapConnectionServices.computeIfAbsent( strPoolName, PluginConnectionService::new );
    }

    /**
     * Points the connection service of the adminsql plugin to a pool
     *
     * @param strPoolName The name of the pool
     * @return the adminsql plugin
     */
    public static Plugin usePool( String strPoolName )
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );
        plugin.setConnectionService( new PluginConnectionService( strPoolName ) );

        return plugin;
    }

    /**
     * Points the connection service of the adminsql plugin to the pool holding its own tables
     *
     * @param plugin The adminsql plugin
     * @return the adminsql plugin
     */
    public static Plugin usePluginPool( Plugin plugin )
    {
        String strPoolName = plugin.getDbPoolName(  );

        if ( StringUtils.isEmpty( strPoolName ) || AppConnectionService.NO_POOL_DEFINED.equals( strPoolName ) )
        {
            strPoolName = AppConnectionService.DEFAULT_POOL_NAME;
        }

        plugin.setConnectionService( new PluginConnectionService( strPoolName ) );

        return plugin;
    }
}
