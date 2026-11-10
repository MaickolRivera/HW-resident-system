package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.SearchAccessHistoryUseCase;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** Historial de accesos filtrable por documento, por apartamento y por día. */
public class HistoryPresenter {
    private final HistoryView view;
    private final ListApartmentsUseCase listApartments;
    private final SearchAccessHistoryUseCase search;
    private final Clock clock;

    public HistoryPresenter(HistoryView view, ListApartmentsUseCase listApartments,
                            SearchAccessHistoryUseCase search, Clock clock) {
        this.view = view;
        this.listApartments = listApartments;
        this.search = search;
        this.clock = clock;
    }

    public void onShow() {
        var options = new ArrayList<ApartmentOption>();
        options.add(ApartmentOption.ALL);
        listApartments.execute().stream().map(Labels::option).forEach(options::add);
        view.showApartmentOptions(options);
        onSearch();
    }

    public void onSearch() {
        var now = LocalDateTime.now(clock);
        var apartment = view.apartmentFilter().map(ApartmentOption::id).orElse(null);
        var rows = search.execute(view.documentFilter(), apartment, view.onlyToday() ? now.toLocalDate() : null)
                .stream()
                .map(r -> RecordRow.of(r, now))
                .toList();
        view.showRecords(rows);
        view.showSummary(rows.size() + (rows.size() == 1 ? " registro" : " registros"));
    }

    public void onClear() {
        view.clearFilters();
        onSearch();
    }
}
