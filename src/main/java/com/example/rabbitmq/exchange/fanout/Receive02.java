package com.example.rabbitmq.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 8:40
 */
public class Receive02 {
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
             * 由于Fanout类型的交换机的消息时类似于广播的模式，它不需要绑定RoutingKey
             * 而又可能会有很多个消费来接收这个交换机中的数据，因此我们创建队列时要创建
             * 一个随机的队列名称
             *
             * 没有参数的queueDeclare方法会创建一个名字为随机的一个队列
             * 这个队列的数据时非持久化
             * 是排外的（同时最多只允许有一个消费者监听当前队列）
             * 自动删除的 当没有任何消费者监听队列时这个队列会自动删除
             *
             * getQueue() 方法用于获取这个随机的队列名
             */

            String queueName = channel.queueDeclare().getQueue();
            channel.exchangeDeclare("fanoutExchange", "fanout", true);
            //将这个随机的队列绑定到交换机中， 由于是fanout类型的交换机因此不需指定RoutingKey进行绑定
            channel.queueBind(queueName, "fanoutExchange", "");
            /**
             * 监听某个队列并获取队列中的数据
             * 注意：
             *   当前被讲定的队列必须已经存在并正确的绑定到了某个交换机中
             */
            channel.basicConsume(queueName, true, "", new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body);
                    System.out.println("Receive01消费者 ---" + message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
