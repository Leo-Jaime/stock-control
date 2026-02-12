package com.stockcontrol.config;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Set;

@ApplicationScoped
public class CorsConfig {

    @ConfigProperty(name = "app.cors.origins", defaultValue = "http://localhost:3000,http://localhost:5173")
    String origins;

    public void init(@Observes Router router) {
        Set<HttpMethod> methods = Set.of(
            HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
            HttpMethod.DELETE, HttpMethod.OPTIONS
        );

        CorsHandler corsHandler = CorsHandler.create()
            .addRelativeOrigin(".*")
            .allowedMethods(methods)
            .allowedHeader("*")  // Allow all headers
            .exposedHeader("Authorization")
            .exposedHeader("Content-Type")
            .allowCredentials(true);

        router.route().order(0).handler(corsHandler);
    }
}
