package be.alexandre01.eloriamc.server.events;

import be.alexandre01.eloriamc.server.events.factories.EventsFactory;
import be.alexandre01.eloriamc.server.events.players.ListenerPlayerManager;
import be.alexandre01.eloriamc.server.events.players.RegisterPlayerEvent;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class EventLib {

    @Getter
    private static EventLib instance;
    @Getter
    Plugin plugin;
    @Getter
    private final EventsFactory eventsFactory;
    @Getter
    private final ListenerPlayerManager listenerPlayerManager;

    public EventLib(Plugin plugin){
        instance = this;
        this.plugin = plugin;
        this.eventsFactory = new EventsFactory(plugin);
        this.listenerPlayerManager = new ListenerPlayerManager(eventsFactory);
    }

}
