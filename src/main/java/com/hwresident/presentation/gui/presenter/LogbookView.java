package com.hwresident.presentation.gui.presenter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Contrato de la pantalla "Minuta" (portería: ingresos, autorizaciones y salidas). */
public interface LogbookView {
    // --- lo que el guarda llena
    String guardDocument();
    String documentNumber();
    String fullName();
    String personType();
    String company();
    String labor();
    Optional<ApartmentOption> selectedDestination();
    String reason();
    String vehiclePlate();
    String vehicleType();
    String correspondence();
    Optional<RecordRow> selectedRecord();

    // --- lo que el presentador le muestra
    void showApartmentOptions(List<ApartmentOption> options);
    void showPersonTypes(List<String> types);
    void showVehicleTypes(List<String> types);
    /** Resultado de buscar el documento. registered=false habilita los campos para registrar a la persona. */
    void showPersonLookup(String message, boolean registered);
    /** Fija el destino (residentes) y si el guarda puede cambiarlo. */
    void setDestination(UUID apartmentId, boolean editable);
    void showRecords(List<RecordRow> rows);
    /** "¿El residente autoriza?": true = sí, false = no, vacío = decidir después (queda en espera). */
    Optional<Boolean> askResidentAuthorization(String question);
    void clearForm();
    void showSuccess(String message);
    void showError(String message);
}
