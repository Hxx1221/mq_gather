package com.hxx.mq.dlx;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class Consumer {

	
	public static void main(String[] args) throws Exception {
		
		
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("106.12.33.235");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		connectionFactory.setUsername("root");
		connectionFactory.setPassword("root");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		// 这就是一个普通的交换机 和 队列 以及路由
		String exchangeName = "test_dlx_exchange";
		String routingKey = "dlx.#";
		String queueName = "test_dlx_queue";

		
		channel.basicConsume("dlx", true, new MyConsumer(channel));
		
		
	}
}
