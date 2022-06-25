package be.alexandre01.eloriamc.server.events.players;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterPlayerEvent<T extends Event> implements Listener {

    String handler;

    ListenerPlayerManager listenerPlayerManager;




    public <T extends Class<? extends Event>> RegisterPlayerEvent(Class<? extends Event> event, String handler, ListenerPlayerManager listenerPlayerManager) {
        this.handler = handler;
        System.out.println(event.getSimpleName());
        this.listenerPlayerManager = listenerPlayerManager;

    }



    @EventHandler
    public void onRegister(T event) {
        try {
            Object o = event.getClass().getMethod(handler).invoke(event);
            if(o != null) {
                if(o instanceof Player) {
                    Player p = (Player) o;
                    ListenerPerPlayer listener = listenerPlayerManager.getListenersPerPlayer().get(p);
                    List<IPlayerEvent> collected = listener.getListeners().stream().filter(iPlayerEvent -> iPlayerEvent.getEventClass().equals(event.getClass())).collect(Collectors.toList());
                    for(Iterator<IPlayerEvent> iterator = collected.iterator(); iterator.hasNext();) {
                        IPlayerEvent iPlayerEvent = iterator.next();
                        try {
                            iPlayerEvent.onPlayerEvent(event, p);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
