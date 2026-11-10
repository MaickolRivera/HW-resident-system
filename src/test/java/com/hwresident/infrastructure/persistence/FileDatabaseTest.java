package com.hwresident.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.TestData;
import com.hwresident.application.usecase.EntryRequest;
import com.hwresident.domain.model.AccessStatus;
import com.hwresident.domain.model.Resident;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileDatabaseTest {

    @TempDir
    Path dir;

    private TestData dataIn(FileDatabase db) {
        return new TestData(db.persons(), db.apartments(), db.permissions(), db.records());
    }

    @Test
    void losDatosSobrevivenAlCerrarYAbrir() {
        var file = dir.resolve("datos.dat");
        var db = FileDatabase.open(file);
        assertTrue(db.isEmpty());
        var data = dataIn(db);
        data.grant().execute("123", "987", TestData.NOW, TestData.NOW.plusHours(2));
        var record = data.registerEntry().execute(EntryRequest.of("500", "123", null, "Llega")).record();
        assertTrue(Files.exists(file));

        var reopened = FileDatabase.open(file);

        assertFalse(reopened.isEmpty());
        assertEquals(4, reopened.persons().findAll().size());
        assertEquals(2, reopened.apartments().findAll().size());
        assertEquals(1, reopened.permissions().findAll().size());
        var loaded = reopened.records().findById(record.id()).orElseThrow();
        assertEquals(AccessStatus.INSIDE, loaded.status());
        assertEquals("Llega", loaded.reason().orElseThrow());
        // las referencias se conservan: el registro apunta a la misma residente del repositorio
        var ana = (Resident) reopened.persons().findByDocumentNumber("123").orElseThrow();
        assertSame(ana, loaded.person());
        assertSame(ana.apartment(), loaded.destination());
    }

    @Test
    void losCambiosDeEstadoTambienSeGuardan() {
        var file = dir.resolve("datos.dat");
        var data = dataIn(FileDatabase.open(file));
        var record = data.registerEntry().execute(EntryRequest.of("500", "123", null, null)).record();
        data.registerExit().execute(record.id(), "500");

        var loaded = FileDatabase.open(file).records().findById(record.id()).orElseThrow();
        assertEquals(AccessStatus.EXITED, loaded.status());
    }

    @Test
    void archivoCorruptoSeApartaYSeEmpiezaVacio() throws Exception {
        var file = dir.resolve("datos.dat");
        Files.writeString(file, "esto no es un archivo de datos");

        var db = FileDatabase.open(file);

        assertTrue(db.isEmpty());
        assertTrue(Files.exists(dir.resolve("datos.dat.corrupto")));
    }
}
