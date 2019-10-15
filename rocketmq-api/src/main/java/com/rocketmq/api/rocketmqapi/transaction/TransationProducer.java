package com.rocketmq.api.rocketmqapi.transaction;

import com.rocketmq.api.rocketmqapi.constants.Constants;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransationProducer {

    public static void main(String[] args) throws MQClientException {
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer("text-tx");


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 100,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("text-tx" + "-check-thread");
                return thread;
            }
        });

        transactionMQProducer.setNamesrvAddr(Constants.NAMESRV_ADDR);

        transactionMQProducer.setExecutorService(threadPoolExecutor);
        TransactionListener transactionListener = new TransactionListenerImpl();
        transactionMQProducer.setTransactionListener(transactionListener);
        transactionMQProducer.start();

        Message message = new Message("test_tx",//主题
                "tagA",//标签
                "keyA",//用户自定义的key 唯一的标识
                ("hxxlakjsf").getBytes());//消息内容实体

        transactionMQProducer.sendMessageInTransaction(message,"我是回调的参数！");



    }

}