package io.vertx.book.message;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Single;

import java.util.concurrent.TimeUnit;

public class HelloConsumerMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(req -> {
            EventBus bus = vertx.eventBus();
            Single<JsonObject> lukeSingle = bus.<JsonObject>rxSend("hello", "Luke")
                    .subscribeOn(RxHelper.scheduler(vertx)).timeout(3, TimeUnit.SECONDS).retry(3).map(Message::body);
            Single<JsonObject> leiaSingle = bus.<JsonObject>rxSend("hello", "Leia")
                    .subscribeOn(RxHelper.scheduler(vertx)).timeout(3, TimeUnit.SECONDS).retry(3).map(Message::body);
            Single.zip(lukeSingle, leiaSingle, (luke, leia) -> new JsonObject()
                    .put("Luke", luke.getString("message") + " from "
                            + luke.getString("served_by")).put("Leia", leia.getString("message") + " from "
                            + leia.getString("served_by")))
                    .subscribe(result -> req.response().setStatusCode(200).end(result.encode()), error -> {
                        error.printStackTrace();
                        req.response().setStatusCode(500).end(error.getMessage());
                    });
        }).rxListen(8080).subscribe(result -> System.out.println("done"), Throwable::printStackTrace);
    }

}
