package com.rabbitmq.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.spring.entity.Order;
import com.rabbitmq.spring.entity.Packaged;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqSpringApplicationTests {


    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testAdmin() throws Exception {
        rabbitAdmin.declareExchange(new DirectExchange("spring.direct", false, false));
        rabbitAdmin.declareQueue(new Queue("srping.direct.queue", false));
        rabbitAdmin.declareBinding(
                new Binding("srping.direct.queue", Binding.DestinationType.QUEUE,
                        "spring.direct", "direct.key", new HashMap<>()));
        //另一种绑定
//		rabbitAdmin.declareBinding(
//				BindingBuilder
//						.bind(new Queue("spring.two.queues",false))
//				.to(new DirectExchange("spring.two.direct",false,false))
//				.with("spring.tw.key"));
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage() {
        final MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "描述");
        messageProperties.getHeaders().put("type", "类型--");
        final Message message = new Message("hello rabbit!".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("topic001", "spring.amqp,", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(final Message message) throws AmqpException {
                System.out.println("----添加额外设置-----");
                message.getMessageProperties().getHeaders().put("desc", "ddddd");
                return message;
            }
        });
    }

    @Test
    public void testSendMessage2() {
        final MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "描述");
        messageProperties.getHeaders().put("type", "类型--");
        messageProperties.setContentType("text/plain");
        final Message message = new Message("hello rabbit!".getBytes(), messageProperties);
        rabbitTemplate.send("topic001","spring.abc",message);

        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hxx01" );
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hxx02" );

    }

    @Test
    public void testSendMessage4Text() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.send("topic002", "rabbit.abc", message);
    }


    @Test
    public void testSendJsonMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }

    @Test
    public void testSendJavaMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "com.rabbitmq.spring.entity.Order");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }


    @Test
    public void testSendMappingMessage() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");

        String json1 = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json1);

        MessageProperties messageProperties1 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties1.setContentType("application/json");
        messageProperties1.getHeaders().put("__TypeId__", "order");
        Message message1 = new Message(json1.getBytes(), messageProperties1);
        rabbitTemplate.send("topic001", "spring.order", message1);

        Packaged pack = new Packaged();
        pack.setId("002");
        pack.setName("包裹消息");
        pack.setDescription("包裹描述信息");

        String json2 = mapper.writeValueAsString(pack);
        System.err.println("pack 4 json: " + json2);

        MessageProperties messageProperties2 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties2.setContentType("application/json");
        messageProperties2.getHeaders().put("__TypeId__", "packaged");
        Message message2 = new Message(json2.getBytes(), messageProperties2);
        rabbitTemplate.send("topic001", "spring.pack", message2);
    }
}
