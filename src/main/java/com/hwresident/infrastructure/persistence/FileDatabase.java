package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.model.Person;
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.AccessRecordRepository;
import com.hwresident.domain.repository.ApartmentRepository;
import com.hwresident.domain.repository.PersonRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Almacenamiento local en un archivo (serialización de Java).
 *
 * <p>Todos los datos viven en un único objeto {@link Data}; los repositorios en memoria
 * trabajan sobre sus mapas y, cada vez que guardan algo, este objeto se escribe
 * completo en disco. Se escribe primero un archivo temporal y luego se reemplaza,
 * para no dejar el archivo a medias si la aplicación se cierra mientras guarda.
 * Se guarda todo en un solo archivo para conservar las referencias entre objetos
 * (p. ej. un registro de la minuta apunta a la misma persona que está en el repositorio).
 */
public final class FileDatabase {

    private static final class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        final LinkedHashMap<UUID, Person> persons = new LinkedHashMap<>();
        final LinkedHashMap<UUID, Apartment> apartments = new LinkedHashMap<>();
        final LinkedHashMap<UUID, AccessPermission> permissions = new LinkedHashMap<>();
        final LinkedHashMap<UUID, AccessRecord> records = new LinkedHashMap<>();
    }

    private final Path file;
    private final Data data;
    private final PersonRepository persons;
    private final ApartmentRepository apartments;
    private final AccessPermissionRepository permissions;
    private final AccessRecordRepository records;

    private FileDatabase(Path file, Data data) {
        this.file = file;
        this.data = data;
        this.persons = new InMemoryPersonRepository(data.persons, this::flush);
        this.apartments = new InMemoryApartmentRepository(data.apartments, this::flush);
        this.permissions = new InMemoryAccessPermissionRepository(data.permissions, this::flush);
        this.records = new InMemoryAccessRecordRepository(data.records, this::flush);
    }

    /** Abre el archivo si existe; si no, empieza vacío. Un archivo ilegible se renombra y se empieza vacío. */
    public static FileDatabase open(Path file) {
        if (!Files.exists(file)) {
            return new FileDatabase(file, new Data());
        }
        try (InputStream in = Files.newInputStream(file); var objects = new ObjectInputStream(in)) {
            return new FileDatabase(file, (Data) objects.readObject());
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            var broken = file.resolveSibling(file.getFileName() + ".corrupto");
            try {
                Files.move(file, broken, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
                // si ni siquiera se puede mover, igual se arranca vacío
            }
            System.err.println("No se pudo leer " + file + " (" + e.getMessage() + "). Se guardó como "
                    + broken.getFileName() + " y se inicia con datos vacíos.");
            return new FileDatabase(file, new Data());
        }
    }

    public boolean isEmpty() {
        return data.persons.isEmpty() && data.apartments.isEmpty();
    }

    public Path file() { return file; }
    public PersonRepository persons() { return persons; }
    public ApartmentRepository apartments() { return apartments; }
    public AccessPermissionRepository permissions() { return permissions; }
    public AccessRecordRepository records() { return records; }

    private synchronized void flush() {
        try {
            var dir = file.toAbsolutePath().getParent();
            if (dir != null) Files.createDirectories(dir);
            var tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (OutputStream out = Files.newOutputStream(tmp); var objects = new ObjectOutputStream(out)) {
                objects.writeObject(data);
            }
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar en " + file, e);
        }
    }
}
