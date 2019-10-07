package com.hxx.mq.ttl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

public class Producer {


    /**
     * 过期时间 ttl
     * 设置过期时间有两种方式
     * 1.针对某一条消息设置过期时间
     * 2.针对某一个队列属性设置
     * 如果两种都设置 就以较小的时间为准
     *
     * 如果 时间设置为0 则表示这个消失不进入队列 直接进入消费者
     */
    public static void main(String[] args) throws Exception {


        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("106.12.33.235");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String test_ttl_exchange = "test_ttl_exchange";
      //  String routingKeyError = "abc.save";

        String msg = "Hello RabbitMQ Return Message";
        final HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("x-message-ttl", 10000);

        //声明队列
        channel.exchangeDeclare(test_ttl_exchange, "direct", true, false, null);

	    //声明队列
        channel.queueDeclare("test_ttl_exchange_queue", true, false, false, stringObjectHashMap);

        //绑定队列
        channel.queueBind("test_ttl_exchange_queue","test_ttl_exchange","test_ttl_exchange");


        //如果没有这个队列 或者这个队列没有消费者 就进入备份器
        channel.basicPublish("test_ttl_exchange", "test_ttl_exchange", null, msg.getBytes());




    }
}
