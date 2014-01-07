package com.sigmasys.kuali.ksa.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple event multicaster that dispatches <code>java.util.EventObject</code> events
 * and manages the internal registry of <code>EventListener</code> listeners. Singleton.
 *
 * @author Michael Ivanov
 */
public class EventMulticaster {

    private static final EventMulticaster eventMulticaster = new EventMulticaster();

    private final List<EventListener<? extends EventObject>> eventListeners =
            Collections.synchronizedList(new LinkedList<EventListener<? extends EventObject>>());

    public static EventMulticaster getInstance() {
        return eventMulticaster;
    }

    private EventMulticaster() {
    }

    public void addListener(EventListener<? extends EventObject> eventListener) {
        eventListeners.add(eventListener);
    }

    public void removeListener(EventListener<? extends EventObject> eventListener) {
        eventListeners.remove(eventListener);
    }

    @SuppressWarnings("unchecked")
    public void fireEvent(EventObject eventObject) {
        for (EventListener eventListener : eventListeners) {
            Class eventClass = getEventClass(eventListener);
            if (eventClass == null || eventClass.isInstance(eventObject)) {
                eventListener.handleEvent(eventObject);
            }
        }
    }

    protected Class getEventClass(EventListener eventListener) throws ClassCastException {
        Class<?> listenerClass = eventListener.getClass();
        do {
            Type[] genericInterfaces = listenerClass.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType interfaceType = (ParameterizedType) genericInterface;
                    if (EventListener.class.equals(interfaceType.getRawType())) {
                        Type[] genericTypes = interfaceType.getActualTypeArguments();
                        if (genericTypes != null && genericTypes.length > 0) {
                            return (Class<?>) genericTypes[0];
                        }
                    }
                }
            }
            listenerClass = listenerClass.getSuperclass();
        } while (listenerClass != null);
        return null;
    }

}
