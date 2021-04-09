package com.example.rabbitmq.confirm.waitForConfirmsOrDie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 9:42
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
            channel.queueDeclare("confirmQueue",true,false,false,null);
            channel.exchangeDeclare("directConfirmExchange","direct",true);
            channel.queueBind("confirmQueue","directConfirmExchange","confirmRoutingKey");

            String message="普通发送者确认模式测试消息！";
            //启动发送者确认模式
            channel.confirmSelect();
            channel.basicPublish("directConfirmExchange","confirmRoutingKey",null,message.getBytes("UTF-8"));
            /**
             * waitForConfirmsOrDie 批量消息确认，它会同时向服务中确认之前当前通道中发送的所有的消息是否已经全部成功写入
             * 这个方法没有任何的返回值，如果服务器中有一条消息没有能够成功或向服务器发送确认时服务不可访问都被认定为
             * 消息确认失败，可能有有消息没有发送成功，我们需要进行消费的补发。
             * 如果无法向服务器获取确认信息那么方法就会抛出InterruptedException异常，这时就需要补发消息到队列
             * waitForConfirmsOrDie方法可以指定一个参数timeout 用于等待服务器的确认时间，如果超过这个时间也会
             * 抛出异常，表示确认失败需要补发消息
             *
             * 注意：
             *    批量消息确认的速度比普通的消息确认要快，但是如果一旦出现了消息补发的情况，我们不能确定具体
             *    是哪条消息没有完成发送，需要将本次的发送的所有消息全部进行补发
             *
             */

            channel.waitForConfirmsOrDie();
            System.out.println("消息发送成功");

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
