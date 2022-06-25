package be.alexandre01.eloriamc.server.events.nms;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.lang.reflect.Method;

public class EventUtils {
    protected HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList", new Class[0]);
            method.setAccessible(true);
            return (HandlerList)method.invoke(null, new Object[0]);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }
    protected Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList", new Class[0]);
            return clazz;
        } catch (NoSuchMethodException noSuchMethodException) {
            if (clazz.getSuperclass() != null &&
                    !clazz.getSuperclass().equals(Event.class) &&
                    Event.class.isAssignableFrom(clazz.getSuperclass()))
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
        }
    }
}
