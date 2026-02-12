package com.stockcontrol.service;

import com.stockcontrol.entity.User;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StartupInitializer {

    private static final Logger LOG = Logger.getLogger(StartupInitializer.class);

    @Inject
    AuthService authService;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (User.count() == 0) {
            User admin = new User();
            admin.username = "admin";
            admin.password = authService.hashPassword("admin123");
            admin.role = "user";
            admin.persist();
            LOG.info("✅ Usuário padrão criado: admin / admin123");
        }
    }

    // Make hashPassword accessible - we need it public in AuthService
}
