package br.com.dio.sudoku.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifierService {

    private final Map<EventEnum, List<EventListener>> listener = new HashMap<>() {{
        put(EventEnum.CLEAR_SPACE, new ArrayList<>());
    }};

    public void subscriber(final EventEnum eventType, final EventListener listener) {
        var selectedListeners = this.listener.get(eventType);
        selectedListeners.add(listener);
    }

    public void notify(final EventEnum eventType) {
        this.listener.get(eventType).forEach(listener -> listener.update(eventType));
    }
}
