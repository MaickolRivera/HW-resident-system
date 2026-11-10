package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.GuiUseCases;
import com.hwresident.presentation.gui.presenter.AccessCheckPresenter;
import com.hwresident.presentation.gui.presenter.ApartmentsPresenter;
import com.hwresident.presentation.gui.presenter.GuardsPresenter;
import com.hwresident.presentation.gui.presenter.HistoryPresenter;
import com.hwresident.presentation.gui.presenter.InsidePresenter;
import com.hwresident.presentation.gui.presenter.LogbookPresenter;
import com.hwresident.presentation.gui.presenter.PermissionsPresenter;
import com.hwresident.presentation.gui.presenter.ResidentsPresenter;
import com.hwresident.presentation.gui.presenter.ShiftPresenter;
import com.hwresident.presentation.gui.presenter.VisitorsPresenter;
import com.hwresident.presentation.gui.swing.MainFrame.Tab;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada de la GUI: crea vistas, les conecta sus presentadores y abre la ventana. */
public final class SwingApp {
    private SwingApp() {}

    public static void launch(GuiUseCases uc) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // se usa el look and feel por defecto
            }
            var shift = new ShiftBar();
            shift.bind(new ShiftPresenter(shift, uc.listGuards()));

            var logbook = new LogbookPanel(shift);
            logbook.bind(new LogbookPresenter(logbook, uc.findPerson(), uc.listApartments(), uc.listResidents(),
                    uc.registerEntry(), uc.resolveAuthorization(), uc.registerExit(), uc.searchHistory(), uc.clock()));

            var inside = new InsidePanel(shift);
            inside.bind(new InsidePresenter(inside, uc.listPeopleInside(), uc.registerExit(), uc.clock()));

            var history = new HistoryPanel();
            history.bind(new HistoryPresenter(history, uc.listApartments(), uc.searchHistory(), uc.clock()));

            var permissions = new PermissionsPanel();
            permissions.bind(new PermissionsPresenter(permissions, uc.listResidents(), uc.listExternals(),
                    uc.listApartments(), uc.grantPermission(), uc.revokePermission(), uc.listPermissions(), uc.clock()));

            var residents = new ResidentsPanel();
            residents.bind(new ResidentsPresenter(residents, uc.registerResident(), uc.listResidents(), uc.listApartments()));

            var apartments = new ApartmentsPanel();
            apartments.bind(new ApartmentsPresenter(apartments, uc.registerApartment(), uc.listApartments(), uc.listResidents()));

            var visitors = new VisitorsPanel();
            visitors.bind(new VisitorsPresenter(visitors, uc.registerVisitor(), uc.registerServiceProvider(), uc.listExternals()));

            var guards = new GuardsPanel();
            guards.bind(new GuardsPresenter(guards, uc.registerGuard(), uc.listGuards()));

            var accessCheck = new AccessCheckPanel();
            accessCheck.bind(new AccessCheckPresenter(accessCheck, uc.checkAccess()));

            new MainFrame(shift, List.of(
                    new Tab("Minuta", logbook, logbook::onShow),
                    new Tab("Dentro del conjunto", inside, inside::onShow),
                    new Tab("Historial", history, history::onShow),
                    new Tab("Permisos", permissions, permissions::onShow),
                    new Tab("Residentes", residents, residents::onShow),
                    new Tab("Inmuebles", apartments, apartments::onShow),
                    new Tab("Externos", visitors, visitors::onShow),
                    new Tab("Guardas", guards, guards::onShow),
                    new Tab("Consultar permiso", accessCheck, () -> { })))
                    .setVisible(true);
        });
    }
}
