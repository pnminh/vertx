package io.vertx.book.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HelloMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.get("/").handler(this::helloRoutes);
        router.get("/:name").handler(this::helloRoutes);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    private void helloRoutes(RoutingContext rc) {
        String message = "Hello";
        if (rc.pathParam("name") != null) {
            message += " " + rc.pathParam("name");
        }
        JsonObject json = new JsonObject().put("message", message);
        rc.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(json.encode());
    }

}
