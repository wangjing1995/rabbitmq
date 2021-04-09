package com.example.rabbitmq.exchange.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 9:10
 */
public class Receive04 {
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
            /**
             * Topic 类型的交换机也是消息一对多的一种交换机类型，它和fanout都能实现一个消息同时发送给多个队列
             * fanout更适合于使用在一个功能不同的进程来获取数据，例如手机App中的消息推送，一个App可能会还有很
             * 多个用户来进行安装然后他们都会启动一个随机的队列来接收着自己的数据
             * Topic更适合不同的功能模块来接收同一个消息，例如商城下单成功以后需要发送消息到队列中。例如RoutingKey
             * 为 的order.success,物流系统监听订单order.* 发票系统监听order.*
             *
             * Topic可以使用随机的队列名也可以使用一个明确的队列名，但是如果应用在和订单有关的功能中，建议是有个
             * 名取的队列名并且要求为持久化的队列
             */

            channel.queueDeclare("topicQueue04",true,false,false,null);
            channel.exchangeDeclare("topicExchange","topic",true);
            channel.queueBind("topicQueue04","topicExchange","aa.bb.cc");
            channel.basicConsume("topicQueue04",true,"",new DefaultConsumer(channel){
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message=new String(body);
                    System.out.println("Receive04消费者aa.bb.cc ---"+message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
