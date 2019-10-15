package com.rocketmq.api.rocketmqapi.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

public class TransactionListenerImpl implements TransactionListener {

    //执行本地事务
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        System.out.println("=======执行本地事务=========");
        System.out.println("msg:" + msg);
        System.out.println("arg:" + arg);


        return LocalTransactionState.UNKNOW;
    }

    //
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        System.out.println("=======回调检查消息=========");
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}