package org.idle.easy.fibers;

import io.vertx.core.Vertx;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Log4j2
public class AppLauncher {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(MainVerticle.class.getName(), h -> {
            if (h.succeeded()) {
                log.info("Success: {}", h.result());
            } else {
                log.error("Something went wrong: {}", h.cause());
            }
        });
    }
}
