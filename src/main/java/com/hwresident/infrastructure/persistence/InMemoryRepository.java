package com.hwresident.infrastructure.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Base de los repositorios en memoria. Recibe el mapa donde guardar y un aviso
 * "onChange": así el mismo repositorio sirve solo en memoria (aviso vacío) o
 * respaldado en archivo (el aviso escribe el archivo, ver {@link FileDatabase}).
 */
abstract class InMemoryRepository<T> {
    private final Map<UUID, T> store;
    private final Runnable onChange;

    protected InMemoryRepository() {
        this(new LinkedHashMap<>(), () -> { });
    }

    protected InMemoryRepository(Map<UUID, T> store, Runnable onChange) {
        this.store = store;
        this.onChange = onChange;
    }

    protected <E extends T> E put(UUID id, E entity) {
        store.put(id, entity);
        onChange.run();
        return entity;
    }

    protected Optional<T> get(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    protected List<T> values() {
        return new ArrayList<>(store.values());
    }
}
