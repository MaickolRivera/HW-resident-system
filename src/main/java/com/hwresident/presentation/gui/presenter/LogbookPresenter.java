package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.EntryRequest;
import com.hwresident.application.usecase.FindPersonUseCase;
import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterEntryUseCase;
import com.hwresident.application.usecase.RegisterExitUseCase;
import com.hwresident.application.usecase.ResolveAuthorizationUseCase;
import com.hwresident.application.usecase.SearchAccessHistoryUseCase;
import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.model.Guard;
import com.hwresident.domain.model.Resident;
import com.hwresident.domain.model.ServiceProvider;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Minuta digital. Implementa el flujo de portería:
 * documento -> ¿residente? entra directo : (destino -> ¿permiso previo? entra : ¿el residente autoriza? -> entra / rechazado).
 */
public class LogbookPresenter {
    private final LogbookView view;
    private final FindPersonUseCase findPerson;
    private final ListApartmentsUseCase listApartments;
    private final ListResidentsUseCase listResidents;
    private final RegisterEntryUseCase registerEntry;
    private final ResolveAuthorizationUseCase resolveAuthorization;
    private final RegisterExitUseCase registerExit;
    private final SearchAccessHistoryUseCase history;
    private final Clock clock;

    public LogbookPresenter(LogbookView view, FindPersonUseCase findPerson, ListApartmentsUseCase listApartments,
                            ListResidentsUseCase listResidents, RegisterEntryUseCase registerEntry,
                            ResolveAuthorizationUseCase resolveAuthorization, RegisterExitUseCase registerExit,
                            SearchAccessHistoryUseCase history, Clock clock) {
        this.view = view;
        this.findPerson = findPerson;
        this.listApartments = listApartments;
        this.listResidents = listResidents;
        this.registerEntry = registerEntry;
        this.resolveAuthorization = resolveAuthorization;
        this.registerExit = registerExit;
        this.history = history;
        this.clock = clock;
        view.showPersonTypes(Labels.externalTypes());
        view.showVehicleTypes(Labels.vehicleTypes());
    }

    public void onShow() {
        view.showApartmentOptions(listApartments.execute().stream().map(Labels::option).toList());
        refreshTable();
    }

    /** El guarda digitó un documento: se le dice quién es y qué falta llenar. */
    public void onLookup() {
        var document = view.documentNumber().trim();
        if (document.isEmpty()) {
            view.showError("Ingrese el documento de quien llega");
            return;
        }
        var person = findPerson.execute(document);
        if (person.isEmpty()) {
            view.setDestination(null, true);
            view.showPersonLookup("No registrado: complete nombre y tipo; se registrará al dar ingreso", false);
            return;
        }
        switch (person.get()) {
            case Resident r -> {
                view.setDestination(r.apartment().id(), false);
                view.showPersonLookup("Residente: " + r.fullName() + " - " + r.apartment().label()
                        + ". Ingresa sin autorización.", true);
            }
            case ServiceProvider p -> {
                view.setDestination(null, true);
                view.showPersonLookup(p.typeLabel() + ": " + p.fullName() + " (" + p.company() + ", "
                        + p.labor() + "). Seleccione el destino.", true);
            }
            case Guard g -> view.showError("El documento pertenece al guarda " + g.fullName());
            default -> {
                view.setDestination(null, true);
                view.showPersonLookup(person.get().typeLabel() + ": " + person.get().fullName()
                        + ". Seleccione el destino.", true);
            }
        }
    }

    public void onRegisterEntry() {
        try {
            var destination = view.selectedDestination().map(ApartmentOption::id).orElse(null);
            var result = registerEntry.execute(new EntryRequest(
                    view.guardDocument(),
                    view.documentNumber(),
                    view.fullName(),
                    Labels.serviceKindOf(view.personType()),
                    view.company(),
                    view.labor(),
                    destination,
                    view.reason(),
                    view.vehiclePlate(),
                    Labels.vehicleTypeOf(view.vehicleType()),
                    view.correspondence()));
            var record = result.record();
            view.clearForm();
            switch (result.outcome()) {
                case RESIDENT_ENTERED -> view.showSuccess("Ingreso registrado: " + record.person().fullName()
                        + " (residente) - " + record.destination().label());
                case PRE_AUTHORIZED -> view.showSuccess("Ingreso registrado con permiso previo: "
                        + record.person().fullName() + " - " + record.destination().label());
                case AWAITING_AUTHORIZATION -> askAuthorization(record);
            }
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
        refreshTable();
    }

    /** Autoriza un registro que quedó en espera (seleccionado en la tabla). */
    public void onAuthorize() {
        resolveSelected(true);
    }

    public void onReject() {
        resolveSelected(false);
    }

    public void onExit() {
        var selected = view.selectedRecord();
        if (selected.isEmpty()) {
            view.showError("Seleccione un registro de la tabla");
            return;
        }
        try {
            var record = registerExit.execute(selected.get().id(), view.guardDocument());
            view.showSuccess("Salida registrada: " + record.person().fullName());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
        refreshTable();
    }

    private void askAuthorization(AccessRecord record) {
        var residents = listResidents.execute().stream()
                .filter(r -> r.apartment().id().equals(record.destination().id()))
                .map(Resident::fullName)
                .collect(Collectors.joining(", "));
        var question = record.person().typeLabel() + " " + record.person().displayName()
                + " solicita ingresar a " + record.destination().label() + ".\n"
                + "Residentes: " + (residents.isEmpty() ? "(sin residentes registrados)" : residents) + "\n\n"
                + "¿El residente autoriza el ingreso?";
        var answer = view.askResidentAuthorization(question);
        if (answer.isEmpty()) {
            view.showSuccess("Solicitud EN ESPERA de autorización: " + record.person().fullName());
            return;
        }
        resolve(record, answer.get());
    }

    private void resolveSelected(boolean authorized) {
        var selected = view.selectedRecord();
        if (selected.isEmpty()) {
            view.showError("Seleccione un registro en espera de la tabla");
            return;
        }
        try {
            var record = resolveAuthorization.execute(selected.get().id(), authorized);
            showResolution(record, authorized);
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
        refreshTable();
    }

    private void resolve(AccessRecord record, boolean authorized) {
        resolveAuthorization.execute(record.id(), authorized);
        showResolution(record, authorized);
    }

    private void showResolution(AccessRecord record, boolean authorized) {
        if (authorized) {
            view.showSuccess("Ingreso autorizado: " + record.person().fullName() + " - " + record.destination().label());
        } else {
            view.showError("Ingreso RECHAZADO por el residente: " + record.person().fullName());
        }
    }

    /** Movimientos de hoy más los que siguen abiertos de días anteriores. */
    private void refreshTable() {
        var now = LocalDateTime.now(clock);
        var today = now.toLocalDate();
        view.showRecords(history.execute(null, null, null).stream()
                .filter(r -> r.isOpen() || r.requestedAt().toLocalDate().equals(today))
                .map(r -> RecordRow.of(r, now))
                .toList());
    }
}
