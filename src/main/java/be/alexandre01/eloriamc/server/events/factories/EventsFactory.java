package be.alexandre01.eloriamc.server.events.factories;

import be.alexandre01.eloriamc.server.events.factories.eventloaders.PaperEventLoader;
import be.alexandre01.eloriamc.server.events.factories.eventloaders.SpigotEventLoader;
import be.alexandre01.eloriamc.server.events.nms.EventUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.Map;
import java.util.Set;

public class EventsFactory extends EventUtils {

    @Getter private Multimap<Class<?>, IEvent<?>> eventHashMap;
    @Getter private Plugin plugin;
    @Getter private CustomEventLoader customEventLoader;
    public EventsFactory(Plugin plugin) {
        eventHashMap = ArrayListMultimap.create();
        this.plugin = plugin;
        try {
            Class aikar =  Class.forName("co.aikar.timings.TimedEventExecutor");
            if(aikar != null){
                customEventLoader = new PaperEventLoader();
            }
        } catch (ClassNotFoundException e) {
            customEventLoader = new SpigotEventLoader();
        }
    }

    public <T extends Event> IEvent<T> fastRegisterEvent(Class<T> event, IEvent<T> iEvent, EventPriority eventPriority) {
        if(!eventHashMap.containsKey(iEvent.getEventClass())){
            eventHashMap.put(iEvent.getEventClass(), iEvent);
            Listener registerEvent = new RegisterEvent<>(iEvent.getEventClass(), this);
            for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : this.getCustomEventLoader().createCustomRegisteredListeners(registerEvent, plugin,eventPriority).entrySet())
                getEventListeners(getRegistrationClass(iEvent.getEventClass())).registerAll(entry.getValue());
        }

        eventHashMap.put(iEvent.getEventClass(), iEvent);

        return (IEvent<T>) iEvent;
    }
    public <T extends Event> IEvent<T> fastRegisterEvent(Class<T> event, IEvent<T> iEvent) {
        return fastRegisterEvent(event,iEvent,EventPriority.NORMAL);
    }
    public void unregisterEvent(IEvent<? extends Event> iEvent){
        eventHashMap.remove(iEvent.getEventClass(),iEvent);
    }

    public void  unregisterEvent(Class<? extends Event> event){
        eventHashMap.removeAll(event);
    }
}
