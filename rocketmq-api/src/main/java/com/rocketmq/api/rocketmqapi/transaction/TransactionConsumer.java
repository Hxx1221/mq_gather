package com.rocketmq.api.rocketmqapi.transaction;

import com.rocketmq.api.rocketmqapi.constants.Constants;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

public class TransactionConsumer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer("test-tx-consumer");
        defaultMQPushConsumer.setConsumeThreadMin(10);
        defaultMQPushConsumer.setConsumeThreadMax(20);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        defaultMQPushConsumer.setNamesrvAddr(Constants.NAMESRV_ADDR);

        defaultMQPushConsumer.subscribe("test_tx","*");

        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> lists, ConsumeConcurrentlyContext context) {
                {
                    MessageExt me = lists.get(0);
                    try {

                        String topicc = me.getTopic();
                        String tags = me.getTags();
                        byte[] body = me.getBody();
                        String s = new String(body, RemotingHelper.DEFAULT_CHARSET);
                        String keys = me.getKeys();
                        int queueId = me.getQueueId();
                        long queueOffset = me.getQueueOffset();
                        long commitLogOffset = me.getCommitLogOffset();
//                    if (keys.equals("keyA1")) {
                        System.out.println("queueOffset:"+queueOffset+" queueId:"+queueId+"  topic:" + topicc + "," + "tags:" + tags + "," + "keys:" + keys + "," + "body:" + s);
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
            }
        });
        defaultMQPushConsumer.start();
    }
}