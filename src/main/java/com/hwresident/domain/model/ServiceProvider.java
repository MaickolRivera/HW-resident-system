package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.util.UUID;

/** Domiciliario, contratista o personal de servicio: externo que trabaja para una empresa. */
public class ServiceProvider extends ExternalPerson {
    private static final long serialVersionUID = 1L;

    private final ServiceKind kind;
    private final String company;
    private final String labor;

    public ServiceProvider(UUID id, String fullName, String documentNumber,
                           ServiceKind kind, String company, String labor) {
        super(id, fullName, documentNumber);
        if (kind == null) throw new DomainException("El tipo de servicio es obligatorio");
        if (company == null || company.isBlank()) throw new DomainException("La empresa es obligatoria");
        if (labor == null || labor.isBlank()) throw new DomainException("La labor es obligatoria");
        this.kind = kind;
        this.company = company.trim();
        this.labor = labor.trim();
    }

    public static ServiceProvider create(String fullName, String documentNumber,
                                         ServiceKind kind, String company, String labor) {
        return new ServiceProvider(UUID.randomUUID(), fullName, documentNumber, kind, company, labor);
    }

    public ServiceKind kind() { return kind; }
    public String company() { return company; }
    public String labor() { return labor; }

    @Override
    public String typeLabel() { return kind.label(); }
}
