package com.hxx.mq.exchange.exchangeBind;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer4DirectExchange {

	
	public static void main(String[] args) throws Exception {
		
		//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("106.12.33.235");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		connectionFactory.setUsername("root");
		connectionFactory.setPassword("root");
		
		//2 创建Connection
		Connection connection = connectionFactory.newConnection();
		//3 创建Channel
		Channel channel = connection.createChannel();  
		//4 声明
		String exchangeOneName = "test_direct_exchange_one";
		String exchangeTwoName = "test_direct_exchange_two";
		String routingKeyOne = "test.directOne";
		String routingKeyTwo = "test.directTwo";
		//声明第一个交换器
		channel.exchangeDeclare(exchangeOneName,"direct",false,true,null);
		channel.exchangeDeclare(exchangeTwoName,"fanout",false,true,null);
		channel.exchangeBind(exchangeTwoName,exchangeOneName,"exchangeBind");


		//5 发送
		
		String msg = "Hello World RabbitMQ 4  Direct Exchange Message 111 ... ";
		channel.basicPublish(exchangeOneName, "exchangeBind" , null , msg.getBytes());
		
	}
	
}
