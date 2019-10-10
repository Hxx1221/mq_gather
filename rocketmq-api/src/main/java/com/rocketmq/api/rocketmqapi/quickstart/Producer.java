package com.rocketmq.api.rocketmqapi.quickstart;

import com.rocketmq.api.rocketmqapi.constants.Constants;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class Producer {

    public static void main(String[] args)
            throws MQClientException, RemotingException,
            InterruptedException, MQBrokerException {

        DefaultMQProducer defaultMQProducer =
                new DefaultMQProducer("test_quickstart");
        defaultMQProducer.setNamesrvAddr(Constants.NAMESRV_ADDR);
        defaultMQProducer.start();
        for (int i = 0; i <5 ; i++) {
            Message message = new Message("test_quick_topic",//主题
                    "TagA",//标签
                    "keyA"+i,//用户自定义的key 唯一的标识
                    ("hxxlakjsf"+i).getBytes());//消息内容实体
            SendResult send = defaultMQProducer.send(message);
            System.out.println("消息发出："+send);
        }
        defaultMQProducer.shutdown();

    }
}