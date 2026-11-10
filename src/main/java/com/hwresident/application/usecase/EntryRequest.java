package com.hwresident.application.usecase;

import com.hwresident.domain.model.ServiceKind;
import com.hwresident.domain.model.VehicleType;
import java.util.UUID;

/**
 * Datos que el guarda llena en la minuta para registrar un ingreso.
 *
 * @param guardDocument   guarda de turno que registra el movimiento
 * @param documentNumber  documento de quien llega
 * @param fullName        solo si la persona no está registrada: se registra en ese momento
 * @param serviceKind     solo si no está registrada: null = visitante; si no, tipo de prestador
 * @param company         empresa (solo prestadores nuevos)
 * @param labor           labor (solo prestadores nuevos)
 * @param destinationId   apartamento destino (para residentes se usa el suyo)
 * @param reason          motivo de la visita (opcional)
 * @param vehiclePlate    placa (opcional; vacía = sin vehículo)
 * @param vehicleType     tipo de vehículo (si hay placa)
 * @param correspondence  correspondencia o paquete que trae/recibe (opcional)
 */
public record EntryRequest(
        String guardDocument,
        String documentNumber,
        String fullName,
        ServiceKind serviceKind,
        String company,
        String labor,
        UUID destinationId,
        String reason,
        String vehiclePlate,
        VehicleType vehicleType,
        String correspondence) {

    /** Ingreso de alguien ya registrado, sin vehículo ni correspondencia. */
    public static EntryRequest of(String guardDocument, String documentNumber, UUID destinationId, String reason) {
        return new EntryRequest(guardDocument, documentNumber, null, null, null, null,
                destinationId, reason, null, null, null);
    }
}
