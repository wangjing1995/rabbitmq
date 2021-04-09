package com.example.rabbitmq.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月08日 16:49
 */
public class Receive {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection=null;
        Channel channel=null;
        try {
            connection=factory.newConnection();
            channel=connection.createChannel();
            String message="你好111";
            channel.queueDeclare("myDirectQueue",true,false,false,null);
            String exchangeName = "myExchange";
            //指定Exchange的类型
            //参数1为 交换机名称
            //参数2为交换机类型取值为 direct、queue、topic、headers
            //参数3 为是否为持久化消息 true表示持久化消息 false表示非持久化
            channel.exchangeDeclare(exchangeName, "direct", true);
            channel.queueDeclare("myDirectQueue", true, false, false, null);
            boolean autoAck=true;
            String consumerTag="";
            channel.basicConsume("myDirectQueue",autoAck,consumerTag,new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String bodyStr = new String(body);
                    System.out.println("消费者"+bodyStr);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        //            channel.close();
        //            conn.close();
    }
}
