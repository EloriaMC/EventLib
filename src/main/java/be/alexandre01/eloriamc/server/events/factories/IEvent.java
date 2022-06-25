package be.alexandre01.eloriamc.server.events.factories;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.lang.reflect.ParameterizedType;

public abstract class IEvent<T extends Event> {
    @Getter
    private Class<T> eventClass;

    @Getter @Setter private String playerCall = null;
    @Getter @Setter private String cancelCall = null;


    public IEvent(){
        eventClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
    public  abstract void onEvent(T event);


}
