package io.vertx.book.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class HelloMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().<String>consumer("hello", stringMessage -> {
            JsonObject jsonObject = new JsonObject().put("served_by", this.toString());
            if (stringMessage.body().isEmpty()) {
                stringMessage.reply(jsonObject.put("message", "hello"));
            } else {
                stringMessage.reply(jsonObject.put("message", "hello " + stringMessage.body()));
            }
        });
    }

}
