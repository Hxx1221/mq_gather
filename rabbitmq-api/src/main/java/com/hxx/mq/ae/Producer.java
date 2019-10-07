package com.hxx.mq.ae;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Producer {


    /**
     * 备份交换机
     * 如果mandatory设置为false  那么消息在未被路由的情况下 就会丢失消息
     * 如果设置true  那么又要添加returnListener
     * 如果不想这么麻烦  可以使用备份交换器
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

        String ae_exchange = "test_ae_exchange";
      //  String routingKeyError = "abc.save";

        String msg = "Hello RabbitMQ Return Message";
        final HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("alternate-exchange", ae_exchange);
        //声明队列
        channel.exchangeDeclare("test_a_exchange", "direct", true, false, stringObjectHashMap);
        //声明备份器
        channel.exchangeDeclare(ae_exchange, "fanout", true, false, null);
	    //声明队列
        channel.queueDeclare("test_a_exhange_queue", true, false, false, null);
        channel.queueDeclare("test_ae_exhange_queue", true, false, false, null);

        //绑定队列
        channel.queueBind("test_a_exhange_queue","test_a_exchange","test_a_exchange_key");
        channel.queueBind("test_ae_exhange_queue",ae_exchange,"");

        //如果没有这个队列 或者这个队列没有消费者 就进入备份器
        channel.basicPublish("test_a_exchange", "test_a_exchange_keys", true,null, msg.getBytes());




    }
}
