package com.hwresident.presentation.gui.presenter;

import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.model.ServiceKind;
import com.hwresident.domain.model.VehicleType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Traducción entre los textos que muestran los combos y los enums del dominio,
 * para que las vistas Swing no dependan del dominio.
 */
final class Labels {
    static final String VISITOR = "Visitante";

    private Labels() {}

    /** "Visitante", "Domiciliario", "Contratista", "Personal de servicio". */
    static List<String> externalTypes() {
        var labels = new ArrayList<String>();
        labels.add(VISITOR);
        Arrays.stream(ServiceKind.values()).map(ServiceKind::label).forEach(labels::add);
        return labels;
    }

    /** null = visitante. */
    static ServiceKind serviceKindOf(String label) {
        return Arrays.stream(ServiceKind.values())
                .filter(k -> k.label().equals(label))
                .findFirst()
                .orElse(null);
    }

    static List<String> vehicleTypes() {
        return Arrays.stream(VehicleType.values()).map(VehicleType::label).toList();
    }

    static VehicleType vehicleTypeOf(String label) {
        return Arrays.stream(VehicleType.values())
                .filter(t -> t.label().equals(label))
                .findFirst()
                .orElse(VehicleType.CAR);
    }

    static ApartmentOption option(Apartment apartment) {
        return new ApartmentOption(apartment.id(), apartment.label());
    }
}
