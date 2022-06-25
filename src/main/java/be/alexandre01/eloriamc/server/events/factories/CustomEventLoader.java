package be.alexandre01.eloriamc.server.events.factories;


import java.util.Map;
import java.util.Set;

import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public abstract class CustomEventLoader {



    protected Plugin plugin;

    public CustomEventLoader(){
        Plugin plugin;
    }

    public abstract Map<Class<? extends Event>, Set<RegisteredListener>> createCustomRegisteredListeners(Listener listener, Plugin plugin, EventPriority eventPriority);


}
