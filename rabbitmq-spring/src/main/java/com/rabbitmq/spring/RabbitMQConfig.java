package com.rabbitmq.spring;

import com.rabbitmq.client.Channel;
import com.rabbitmq.spring.adapter.MessageDelegate;
import com.rabbitmq.spring.convert.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by boss on 2019/10/7 21:51
 */
@Configuration
@ComponentScan({"com.rabbitmq.*.*"})
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses("106.12.33.235:5672");
        cachingConnectionFactory.setUsername("root");
        cachingConnectionFactory.setPassword("root");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        System.out.println("=============----============");
        return rabbitAdmin;
    }

    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        return new Queue("queue002", true); //队列持久
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        return new Queue("queue003", true); //队列持久
    }

    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        return new Queue("image_queue", true); //队列持久
    }

    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue", true); //队列持久
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        final SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        simpleMessageListenerContainer.setDefaultRequeueRejected(false);
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });
//        simpleMessageListenerContainer.setMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(final Message message, final Channel channel) throws Exception {
//                final String string = new String(message.getBody());
//                System.out.println("========消费者:" + string);
//            }
//        });
        //1 适配器方式. 默认是有自己的方法名字的：handleMessage
        // 可以自己指定一个方法的名字: consumeMessage
        // 也可以添加一个转换器: 从字节数组转换为String
//        MessageListenerAdapter adapter=new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(new TextMessageConverter());

        //2 适配器方式  我们的队列名称和方法名称也可以一一对应的匹配
//        MessageListenerAdapter adapter=new MessageListenerAdapter(new MessageDelegate());
//        final HashMap<String, String> stringStringHashMap = new HashMap<>();
//        stringStringHashMap.put("queue001","method1");
//        stringStringHashMap.put("queue002","method2");
//        adapter.setMessageConverter(new TextMessageConverter());
//        adapter.setQueueOrTagToMethodName(stringStringHashMap);

        //////////转换器///////////
        //1支持json格式的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);

        // 2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
//         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//         adapter.setDefaultListenerMethod("consumeMessage");
//         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//         adapter.setMessageConverter(jackson2JsonMessageConverter);


        //3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");
         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

         Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
         idClassMapping.put("order", com.rabbitmq.spring.entity.Order.class);
         idClassMapping.put("packaged", com.rabbitmq.spring.entity.Packaged.class);

         javaTypeMapper.setIdClassMapping(idClassMapping);

         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
         adapter.setMessageConverter(jackson2JsonMessageConverter);


        simpleMessageListenerContainer.setMessageListener(adapter);
        return simpleMessageListenerContainer;
    }

}
