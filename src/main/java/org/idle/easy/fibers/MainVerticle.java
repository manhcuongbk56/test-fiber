package org.idle.easy.fibers;

import co.paralleluniverse.fibers.Suspendable;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.providers.GoogleAuth;
import io.vertx.ext.sync.Sync;
import io.vertx.ext.sync.SyncVerticle;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.*;
//import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisClient;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.vertx.ext.sync.Sync.awaitResult;

@Log4j2
public class MainVerticle extends SyncVerticle {

    private static final String COLLECTION_NAME = "Entities";
    private WebClient webClient;
    HikariDataSource ds;

    @Override
    @Suspendable
    public void start(Future<Void> startFuture) throws Exception {
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.user", "loplop");
        props.setProperty("dataSource.password", "1");
        props.setProperty("dataSource.databaseName", "loplop");
        props.put("dataSource.logWriter", new PrintWriter(System.out));

        HikariConfig config = new HikariConfig(props);
        ds = new HikariDataSource(config);
        super.start(startFuture);
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Content-Type"));
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());

        router.route().failureHandler(ErrorHandler.create());
        router.get("/authors").handler(Sync.fiberHandler(this::getAllAuthors));
        server.requestHandler(router::accept).listen(8080);
        webClient = WebClient.create(vertx, new WebClientOptions().setSsl(true));
    }



    @Suspendable
    private void getAllAuthors(RoutingContext routingContext) {
        try {
            Connection con = ds.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM authors where id = 1");
            ResultSet rs = pst.executeQuery();
            var result  = new JsonObject();
            while (rs.next()) {
                result.put("id", rs.getInt(1));
                result.put("name", rs.getString(2));
            }
            routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(result.toString());
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    

}
