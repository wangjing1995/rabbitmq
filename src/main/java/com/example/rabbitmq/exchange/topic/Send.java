package com.example.rabbitmq.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 8:48
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

            /**
             * 由于使用Fanout类型的交换机，因此消息的接收方肯能会有多个因此不建议在消息发送时来创建队列
             * 以及绑定交换机，建议在消费者中创建队列并绑定交换机
             * 但是发送消息时至少应该确保交换机时存在
             */
            channel.exchangeDeclare("topicExchange","topic",true);
            String message="topic的测试消息";
            /**
             * 发送消息到指定的队列
             * 参数 1 为交换机名称
             * 参数 2 为消息的RoutingKey 如果这个消息的RoutingKey和某个队列与交换机绑定的RoutingKey一致那么
             *       这个消息就会发送的指定的队列中
             * 注意：
             *   1、发送消息时必须确保交换机已经创建并且确保已经正确的绑定到了某个队列
             */
            channel.basicPublish("topicExchange","aa.bb.cc",null,message.getBytes("UTF-8"));
            System.out.println("消息发送成功");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
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
