package com.hwresident.application.usecase;

import com.hwresident.domain.model.Visitor;
import com.hwresident.domain.repository.VisitorRepository;
import java.util.List;

public class ListVisitorsUseCase {
    private final VisitorRepository visitors;

    public ListVisitorsUseCase(VisitorRepository visitors) {
        this.visitors = visitors;
    }

    public List<Visitor> execute() {
        return visitors.findAll();
    }
}
