package com.example.rabbitmq.confirm.waitForConfirms;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 9:31
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
            channel.queueDeclare("confirmQueue", true, false, false, null);
            channel.exchangeDeclare("directConfirmExchange", "direct", true);
            channel.queueBind("confirmQueue", "directConfirmExchange", "confirmRoutingKey");
            String message = "普通发送者确认模式测试消息！";
            //启动发送者确认模式
            channel.confirmSelect();
            channel.basicPublish("directConfirmExchange", "confirmRoutingKey", null, message.getBytes("UTF-8"));
            //阻塞线程等待服务返回响应 ，用于是否消费发送成功，如果服务确认消费已经发送完成则返回true 否则返回false
            //可以为这个方法指定一个毫秒用于确定我们的需要等待服务确认的超时时间，
            //如果超过了指定的时间以后则会抛出异常InterruptedException 表示服务器出现问题了需要补发消息或
            //将消息缓存到Redis中稍后利用定时任务补发
            //无论是返回false还是抛出异常消息都有可能发送成功有可能没有发送成功
            //如果我们要求这个消息一定要发送到队列例如订单数据，那怎么我们可以采用消息补发
            //所谓补发就是重新发送一次消息，可以使用递归或利用Redis+定时任务来完成补发

            boolean flag = channel.waitForConfirms();
            System.out.println("消息发送成功"+flag);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if(channel!=null){
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
