package be.alexandre01.eloriamc.server.events.factories.eventloaders;

import be.alexandre01.eloriamc.server.events.factories.CustomEventLoader;
import org.apache.commons.lang.Validate;
import org.bukkit.Warning;
import org.bukkit.event.*;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.spigotmc.CustomTimingsHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class SpigotEventLoader extends CustomEventLoader {
    public static final CustomTimingsHandler pluginParentTimer = new CustomTimingsHandler("** Plugins");
    public Map<Class<? extends Event>, Set<RegisteredListener>> createCustomRegisteredListeners(Listener listener, Plugin plugin, EventPriority ep) {
        Set<Method> methods;
        Validate.notNull(plugin, "Plugin can not be null");
        Validate.notNull(listener, "Listener can not be null");
        this.plugin.getServer().getPluginManager().useTimings();
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            Method[] privateMethods = listener.getClass().getDeclaredMethods();
            methods = new HashSet<Method>(publicMethods.length + privateMethods.length, 1.0F);
            Method[] arrayOfMethod1;
            int i;
            byte b;
            for (i = (arrayOfMethod1 = publicMethods).length, b = 0; b < i; ) {
                final Method method = arrayOfMethod1[b];
                methods.add(method);
                b++;
            }
            for (i = (arrayOfMethod1 = privateMethods).length, b = 0; b < i; ) {
                final Method method = arrayOfMethod1[b];
                methods.add(method);
                b++;
            }
        } catch (NoClassDefFoundError e) {
            plugin.getLogger().severe("Plugin " + plugin.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return ret;
        }
        for (Method method : methods) {
            EventHandler eh = method.<EventHandler>getAnnotation(EventHandler.class);
            if (eh == null)
                continue;
            if (method.isBridge() || method.isSynthetic())
                continue;
            Class<?> checkClass;
            if ((method.getParameterTypes()).length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                plugin.getLogger().severe(String.valueOf(plugin.getDescription().getFullName()) + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<RegisteredListener>();
                ret.put(eventClass, eventSet);
            }
            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    Warning warning = clazz.<Warning>getAnnotation(Warning.class);
                    Warning.WarningState warningState = this.plugin.getServer().getWarningState();
                    if (!warningState.printFor(warning))
                        break;
                    plugin.getLogger().log(
                            Level.WARNING,
                            String.format(
                                    "\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated. \"%s\"; please notify the authors %s.", new Object[] { plugin.getDescription().getFullName(),
                                            clazz.getName(),
                                            method.toGenericString(), (
                                            warning != null && warning.reason().length() != 0) ? warning.reason() : "Server performance will be affected",
                                            Arrays.toString(plugin.getDescription().getAuthors().toArray()) }), (warningState == Warning.WarningState.ON) ? (Throwable)new AuthorNagException(null) : null);
                    break;
                }
            }
            EventExecutor executor;
            //ADD compatibility for Aikar (Paper) and Old Spigot

                final CustomTimingsHandler timings = new CustomTimingsHandler("Plugin: " + plugin.getDescription().getFullName() + " Event: " + listener.getClass().getName() + "::" + method.getName() + "(" + eventClass.getSimpleName() + ")", pluginParentTimer);
                executor = new EventExecutor() {
                    public void execute(Listener listener, Event event) throws EventException {
                        try {
                            if (!eventClass.isAssignableFrom(event.getClass()))
                                return;
                            boolean isAsync = event.isAsynchronous();
                            if (!isAsync)
                                timings.startTiming();
                            method.invoke(listener, new Object[] { event });
                            if (!isAsync)
                                timings.stopTiming();
                        } catch (InvocationTargetException ex) {
                            throw new EventException(ex.getCause());
                        } catch (Throwable t) {
                            throw new EventException(t);
                        }
                    }
                };

            eventSet.add(new RegisteredListener(listener, executor, ep, plugin, eh.ignoreCancelled()));
        }
        return ret;
    }
}
