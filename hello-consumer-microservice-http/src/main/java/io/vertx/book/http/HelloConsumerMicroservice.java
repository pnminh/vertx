package io.vertx.book.http;


import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;

public class HelloConsumerMicroservice extends AbstractVerticle {
    private WebClient client;

    @Override
    public void start() {
        client = WebClient.create(vertx);
        Router router = Router.router(vertx);
        router.get("/").handler(this::rootRoute);
        vertx.createHttpServer().requestHandler(router::accept).listen(8081);

    }

    private void rootRoute(RoutingContext rc) {
        HttpRequest<JsonObject> lukeRequest = client.get(8080, "localhost", "/Luke").as(BodyCodec.jsonObject());
        HttpRequest<JsonObject> leiaRequest = client.get(8080, "localhost", "/Leia").as(BodyCodec.jsonObject());
        Single<JsonObject> lukeSingle = lukeRequest.rxSend().map(HttpResponse::body);
        Single<JsonObject> leiaSingle = leiaRequest.rxSend().map(HttpResponse::body);
        Single.zip(lukeSingle,leiaSingle, (luke,leia) -> new JsonObject().put("Luke", luke.getString("message")).put("Leia", leia.getString("message"))).subscribe(result -> rc.response().end(result.encodePrettily()), error -> {
            error.printStackTrace();
            rc.response().setStatusCode(500).end(error.getMessage());
        });
    }
}
