package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.GuiUseCases;
import com.hwresident.presentation.gui.presenter.AccessCheckPresenter;
import com.hwresident.presentation.gui.presenter.PermissionsPresenter;
import com.hwresident.presentation.gui.presenter.ResidentsPresenter;
import com.hwresident.presentation.gui.presenter.VisitorsPresenter;
import java.time.Clock;
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
            var residents = new ResidentsPanel();
            residents.bind(new ResidentsPresenter(residents, uc.registerResident(), uc.listResidents()));

            var visitors = new VisitorsPanel();
            visitors.bind(new VisitorsPresenter(visitors, uc.registerVisitor(), uc.listVisitors()));

            var permissions = new PermissionsPanel();
            permissions.bind(new PermissionsPresenter(permissions, uc.listResidents(), uc.listVisitors(),
                    uc.grantPermission(), uc.revokePermission(), uc.listPermissions(), Clock.systemDefaultZone()));

            var accessCheck = new AccessCheckPanel();
            accessCheck.bind(new AccessCheckPresenter(accessCheck, uc.checkAccess()));

            new MainFrame(residents, visitors, permissions, accessCheck).setVisible(true);
        });
    }
}
