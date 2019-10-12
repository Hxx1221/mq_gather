package com.rocketmq.api.rocketmqapi.quickstart;

import com.rocketmq.api.rocketmqapi.constants.Constants;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;

public class Producer {

    public static void main(String[] args)
            throws MQClientException, RemotingException,
            InterruptedException, MQBrokerException {

        DefaultMQProducer defaultMQProducer =
                new DefaultMQProducer("test_quickstart");
        defaultMQProducer.setNamesrvAddr(Constants.NAMESRV_ADDR);
        defaultMQProducer.start();
        for (int i = 0; i < 50; i++) {
            String tag= i%2==0?"TagA":"TagB";

            Message message = new Message("test_quick_topic",//主题
                    tag,//标签
                    "keyA",//用户自定义的key 唯一的标识
                    ("hxxlakjsf" + i).getBytes());//消息内容实体
            //1.同步发送消息
//            SendResult send = defaultMQProducer.send(message);
            message.setDelayTimeLevel(1);
            //2.异步发送消息
            defaultMQProducer.send(message,
//                    new MessageQueueSelector() {
//                @Override
//                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                    Integer s= (Integer) arg;
//                //    System.out.println(s);
//                    MessageQueue messageQueue = mqs.get(s);
//                    return messageQueue;
//                }
//            },2,
                    new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
//                    生成者发送消息返回的状态
//                            SEND_OK,  发送成功
//                    FLUSH_DISK_TIMEOUT, 刷盘超时
//                    FLUSH_SLAVE_TIMEOUT, 从节点同步超时
//                    SLAVE_NOT_AVAILABLE,  从节点不可用
                    System.out.println("msgId:" + sendResult.getMsgId() + " status:" + sendResult.getSendStatus());
                }

                @Override
                public void onException(Throwable throwable) {
                    System.err.println("发送失败");
                }
            });


        }
        //defaultMQProducer.shutdown();

    }
}