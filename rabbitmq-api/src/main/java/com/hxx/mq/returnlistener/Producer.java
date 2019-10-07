package com.hxx.mq.returnlistener;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Producer {



	/**
	 * boolean mandatory true
	 *   当生产者发送消息 未发现有合适的队列或者
	 * boolean immediate  true
	 *  发现该队列没有一个消费者 则rabbitmq就通过return命令返回给消费者
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
		
		String exchange = "test_return_exchange";
		String routingKey = "return.save";
		String routingKeyError = "abc.save";
		
		String msg = "Hello RabbitMQ Return Message";
		
		
		channel.addReturnListener(new ReturnListener() {
			@Override
			public void handleReturn(int replyCode, String replyText, String exchange,
					String routingKey, BasicProperties properties, byte[] body) throws IOException {
				
				System.err.println("---------handle  return----------");
				System.err.println("replyCode: " + replyCode);
				System.err.println("replyText: " + replyText);
				System.err.println("exchange: " + exchange);
				System.err.println("routingKey: " + routingKey);
				System.err.println("properties: " + properties);
				System.err.println("body: " + new String(body));
			}
		});
		
		
		channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
		
		//channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
		
		
		
		
	}
}
