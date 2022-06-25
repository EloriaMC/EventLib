package be.alexandre01.eloriamc.server.events.players;

import be.alexandre01.eloriamc.server.events.EventLib;
import be.alexandre01.eloriamc.server.events.factories.EventsFactory;
import be.alexandre01.eloriamc.server.events.nms.EventUtils;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ListenerPlayerManager extends EventUtils {


    HashMap<Class<? extends Event>, IPlayerEvent> events = new HashMap<>();


    @Getter HashMap<Player, ListenerPerPlayer> listenersPerPlayer = new HashMap<>();

    @Getter private static ListenerPlayerManager instance;

    EventLib eventLib;


    public ListenerPlayerManager(EventsFactory eventsFactory) {
        instance = this;
        this.eventLib = EventLib.getInstance();
    }


    public <T extends Event> IPlayerEvent<T> registerEvent(Class<T> t, String handler, Player player, IPlayerEvent<T> customEvent, EventPriority eventPriority){

        if(!events.containsKey(customEvent.getEventClass())){
            events.put(customEvent.getEventClass(), customEvent);
            Listener registerEvent = new RegisterPlayerEvent(customEvent.getEventClass(), handler, this);
            for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : eventLib.getEventsFactory().getCustomEventLoader().createCustomRegisteredListeners(registerEvent, eventLib.getPlugin(),eventPriority).entrySet())
                getEventListeners(getRegistrationClass(customEvent.getEventClass())).registerAll(entry.getValue());
        }
        ListenerPerPlayer listenerPerPlayer;

        if(!listenersPerPlayer.containsKey(player)){
            listenersPerPlayer.put(player, listenerPerPlayer= new ListenerPerPlayer(player));
        }else {
            listenerPerPlayer = listenersPerPlayer.get(player);
        }
        customEvent.setPlayer(player);
        listenerPerPlayer.getListeners().add(customEvent);
        return customEvent;
    }

    public <T extends Event> IPlayerEvent<T> registerEvent(Class<T> t, String handler, Player player, IPlayerEvent<T> customEvent){
        return registerEvent(t,handler,player,customEvent,EventPriority.NORMAL);
    }


    public void removeEvent(Player player, IPlayerEvent customEvent){
        listenersPerPlayer.get(player).getListeners().remove(customEvent);
    }
}
