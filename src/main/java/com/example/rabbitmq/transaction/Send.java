package com.example.rabbitmq.transaction;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 11:34
 */
public class Send {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = null;
        Channel channel = null;


        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("transactionQueue", true, false, false, null);

            channel.exchangeDeclare("directTransactionExchange", "direct", true);
            channel.queueBind("transactionQueue", "directTransactionExchange", "transactionRoutingKey");
            String message = "事务的测试消息";
            //启动一个事物，启动事物以后所有写入到队列中的消息
            //必须显示的调用txCommit()提交事物或txRollback()回滚事物
            channel.txSelect();
            channel.basicPublish("directTransactionExchange","transactionRoutingKey",null,message.getBytes("UTF-8"));
            //提交事物，如果我们调用txSelect()方法启动了事物，那么必须显示调用事物的提交
            //否则消息不会真正的写入到队列，提交时以后会将内存中的消息写入队列并释放内存
            channel.txCommit();
            System.out.println("消息发送成功");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    //回滚事务，放弃当前事务中所有没有提交的消息，释放内存
                    channel.txRollback();
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
