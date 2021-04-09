package com.example.rabbitmq.confirm.addConfirmListener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 9:14
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
            connection=factory.newConnection();
            channel=connection.createChannel();

            channel.queueDeclare("confirmQueue",true,false,false,null);
            channel.exchangeDeclare("directConfirmExchange","direct",true);
            channel.queueBind("confirmQueue","directConfirmExchange","confirmRoutingKey");
            String message="普通发送者确认模式测试消息！";
            //启动发送者确认模式
            channel.confirmSelect();

            /**
             * 异步消息确认监听器，需要在发送前启动
             * */
            channel.addConfirmListener(new ConfirmListener() {
                //消息确认以后的回调方法
                //参数 1 为被确认的消息的编号 从 1 开始自动递增用于标记当前是第几个消息
                //参数 2 为当前消息是否同时确认了多个
                //注意：如果参数 2 为true 则表示本次确认同时确认了多条消息，消息等于当前参数1 （消息编号）的所有消息
                //     全部被确认 如果为false 则表示只确认多了当前编号的消息
                @Override
                public void handleAck(long l, boolean b) throws IOException {
                    System.out.println("消息被确认了 --- 消息编号："+l+"    是否确认了多条："+b);
                }

                //消息没有确认的回调方法
                //如果这个方法被执行表示当前的消息没有被确认 需要进行消息补发
                //参数 1 为没有被确认的消息的编号 从 1 开始自动递增用于标记当前是第几个消息
                //参数 2 为当前消息是否同时没有确认多个
                //注意： 如果参数2 为true 则表示小于当前编号的所有的消息可能都没有发送成功需要进行消息的补发
                //      如果参数2 为false则表示当前编号的消息没法发送成功需要进行补发
                @Override
                public void handleNack(long l, boolean b) throws IOException {
                    System.out.println("消息没有被确认-----消息编号："+l+"    是否没有确认多条："+b);
                }
            });

            for (int i=0;i<10000;i++){
                channel.basicPublish("directConfirmExchange","confirmRoutingKey",null,message.getBytes("UTF-8"));
                System.out.println("消息发送成功");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
