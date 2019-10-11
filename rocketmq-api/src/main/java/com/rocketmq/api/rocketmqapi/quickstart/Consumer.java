package com.rocketmq.api.rocketmqapi.quickstart;

import com.rocketmq.api.rocketmqapi.constants.Constants;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

public class Consumer {
    public static void main(String[] args) throws MQClientException {

        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer("test_quick_consumer");
       // defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);//集群模式
        defaultMQPushConsumer.setMessageModel(MessageModel.BROADCASTING);

        defaultMQPushConsumer.setNamesrvAddr(Constants.NAMESRV_ADDR);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        defaultMQPushConsumer.subscribe("test_quick_topic", "TagA||TagB");

        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt me = list.get(0);
                try {

                    String topic = me.getTopic();
                    String tags = me.getTags();
                    byte[] body = me.getBody();
                    String s = new String(body, RemotingHelper.DEFAULT_CHARSET);
                    String keys = me.getKeys();
                    int queueId = me.getQueueId();
                    long queueOffset = me.getQueueOffset();
                    long commitLogOffset = me.getCommitLogOffset();
//                    if (keys.equals("keyA1")) {
                        System.out.println("queueOffset:"+queueOffset+" queueId:"+queueId+"  topic:" + topic + "," + "tags:" + tags + "," + "keys:" + keys + "," + "body:" + s);
//                        int a = 1 / 0;
//                    }
                } catch (Exception e) {
                    int reconsumeTimes = me.getReconsumeTimes();//重试次数
                    if (reconsumeTimes == 3) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            }
        });
        defaultMQPushConsumer.start();
    }
}