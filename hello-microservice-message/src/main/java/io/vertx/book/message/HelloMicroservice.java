package io.vertx.book.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class HelloMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().<String>consumer("hello", stringMessage -> {
            double chaos = Math.random();
            if(chaos <0.3) {
                JsonObject jsonObject = new JsonObject().put("served_by", this.toString());
                if (stringMessage.body().isEmpty()) {
                    stringMessage.reply(jsonObject.put("message", "hello"));
                } else {
                    stringMessage.reply(jsonObject.put("message", "hello " + stringMessage.body()));
                }
            }else if(chaos < .9){
                System.out.println("Returning error");
                stringMessage.fail(500,"message processing failed");
            }else{
                System.out.println("Will not return reply");
            }
        });
    }

}
