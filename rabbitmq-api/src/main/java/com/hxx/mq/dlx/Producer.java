package com.hxx.mq.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

public class Producer {

	/**
	 * 死信队列
	 * 当一个消息在队列中变成死信 就会被发送到另一个交换器中
	 *  变成死信队列的情况：
	 *   消息被拒绝  并且设置requeue参数为false
	 *   消息过期
	 *   队列达到最大长度
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

		String test_ttl_exchange = "test_ttl_exchange.dlx";
		//  String routingKeyError = "abc.save";

		String msg = "Hello RabbitMQ Return Message";
		final HashMap<String, Object> stringObjectHashMap = new HashMap<>();
		stringObjectHashMap.put("x-message-ttl", 10000);
		stringObjectHashMap.put("x-dead-letter-exchange", "dlx");
		stringObjectHashMap.put("x-dead-letter-routing-key", "dlx.key");

		//交换器
		channel.exchangeDeclare(test_ttl_exchange, "direct", true, false, null);
		channel.exchangeDeclare("dlx", "direct", true, false, null);


		//声明队列
		channel.queueDeclare("test_ttl_exchange_queue_dlx", true, false, false, stringObjectHashMap);
		channel.queueDeclare("dlx", true, false, false, null);

		//绑定队列
		channel.queueBind("test_ttl_exchange_queue_dlx",test_ttl_exchange,"test_ttl_dlx_exchange");
		channel.queueBind("dlx","dlx","dlx.key");



		channel.basicPublish("test_ttl_exchange.dlx", "test_ttl_dlx_exchange", null, msg.getBytes());


	}
}
