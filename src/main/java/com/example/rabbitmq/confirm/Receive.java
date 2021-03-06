package com.example.rabbitmq.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月09日 9:49
 */
public class Receive {
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
            //启动事物
            channel.txSelect();
            /**
             * 接收消息
             * 参数 2 为消息的确认机制，true表示自动消息确认，确认以后消息会从队列中被移除 ，当读取完消息以后就会自动确认
             *       如果为false 表示手动确认消息
             *  注意：
             *     1、如果我们只是接收的消息但是还没有来得处理，当前应用就崩溃或在进行处理的时候例如像数据库中
             *     写数据但是数据库这时不可用，那么由于消息是自动确认的那么这个消息就会在接收完成以后自动从队列中
             *     被删除，这就会丢失消息
             */
            channel.basicConsume("confirmQueue", false, "", new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //获取当前当前消息是否被接收过一次如果返回值为false表示消息之前没有被接收过，如果返回值为true
                    //则表示之前这个消息被接收过，可能也处理完成，因此我们要进行消息的防重复处理
                    Boolean isRedeliver=envelope.isRedeliver();
                    //获取当前内部类中的通道
                    Channel c=this.getChannel();

                    if (!isRedeliver){
                        String message=new String(body);
                        System.out.println("消费者 处理了消息 ---"+message);
                        //获取消息的编号，我们需要根据消息的编号来确认消息
                        long tag=envelope.getDeliveryTag();
                        //手动确认消息，确认以后表示当前消息已经成功处理了，需要从队列中移除掉
                        //这个方法应该在当前消息的处理程序全部完成以后执行
                        //参数1为消息的序号
                        //参数2 为是否确认多个，如果为true则表示需要确认小等于当前编号的所有信息，false就是单个确认值确认当前消息
//                        c.basicAck(tag,true);

                    }else {
                        //程序到了这里表示这个消息之前已经被接收过需要进行防重复处理
                        //例如查询数据库中是否已经添加了记录或已经修改过了记录
                        //如果经过判断这条没有被处理完成则需要重新处理消息然后确认掉这条消息
                        //如果已经处理过了则直接确认消息即可不需要进行其他处理操作
//                        c.basicAck(tag,true);
                    }
                    //注意：如果启动了事务，而消息消费者确认模式为手动确认那么必须要提交事务否则即使调用了确认方法
                    //那么消息也不会从队列中被移除掉
//                    c.txCommit();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
