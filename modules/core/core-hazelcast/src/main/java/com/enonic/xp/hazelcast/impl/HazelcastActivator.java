package com.enonic.xp.hazelcast.impl;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Component(immediate = true, configurationPid = "com.enonic.xp.hazelcast")
public class HazelcastActivator
{

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        ClassLoader appClassLoader = getClass().getClassLoader();
        Config config = new Config();
        config.setClassLoader( appClassLoader );
        HazelcastInstance instance = Hazelcast.newHazelcastInstance( config );

        System.out.println( "Hazelcast instance: " + instance.getName() );

        context.registerService( HazelcastInstance.class, instance, new Hashtable<>() );
    }
}
