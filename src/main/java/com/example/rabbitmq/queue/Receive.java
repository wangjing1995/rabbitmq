package com.example.rabbitmq.queue;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月08日 16:37
 */
public class Receive {
    public static void main(String[] args) {
        ConnectionFactory factory =new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setHost("127.0.0.1");
        Connection connection= null;
        final Channel channel;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("myQueue",true,false,false,null);
            boolean autoAck = true;
            String consumerTag="";
            channel.basicConsume("myQueue",autoAck,consumerTag,new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String bodyStr=new String(body,"UTF-8");
                    System.out.println(bodyStr);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
//            channel.close();
//            connection.close();
        }

    }
}
