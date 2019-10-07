package com.rabbitmq.springboot.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.springboot.entity.Order;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Created by boss on 2019/10/8 5:57
 */
@Component
public class RabbitReceiver {


//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "queue-1", durable = "true"),
//            exchange = @Exchange(value = "exchange-1",
//                    durable = "true",
//                    type = "topic",
//                    ignoreDeclarationExceptions = "true"),
//            key = "springboot.*"
//    ))
//    @RabbitHandler
//    public void onMessage(Message message, Channel channel) throws IOException {
//        System.out.println("========");
//        System.out.println("消息Payload:" + message.getPayload());
//        final Long o = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
//        channel.basicAck(o, false);
//    }




    @RabbitListener(queues = "queue-1")
    @RabbitHandler
    public void onOrderMessage(@Payload com.rabbitmq.springboot.entity.Order  order, Channel channel, @Headers Map<String,Object> headers) throws IOException {
        System.out.println("========");
        System.out.println("消息order:" + order);
        final Long o = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(o, false);
    }
}
