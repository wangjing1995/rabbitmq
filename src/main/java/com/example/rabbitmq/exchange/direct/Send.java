package com.example.rabbitmq.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: TODO
 * @author: wangjing
 * @date: 2021年04月08日 16:49
 */
public class Send {
    public static void main(String[] args)  {
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

            channel.queueDeclare("myDirectQueue",true,false,false,null);
            /**
             * 声明一个交换机
             * 参数 1 为交换机的名称取值任意
             * 参数 2 为交换机的类型 取值为 direct、fanout、topic、headers
             * 参数 3 为是否为持久化交换机
             * 注意：
             *    1、声明交换机时如果这个交换机应存在则会放弃声明，如果交换机不存在则声明交换机
             *    2、这个代码是可有可无的但是在使用前必须要确保这个交换机被声明
             */
            channel.exchangeDeclare("directExchange","direct",true);
            /**
             * 将队列绑定到交换机
             * 参数 1 为队列的名称
             * 参数 2 为交换机名称
             * 参数 3 为消息的RoutingKey （就是BindingKey）
             * 注意：
             *   1、在进行队列和交换机绑定时必须要确保交换机和队列已经成功的声明
             */
            channel.queueBind("myDirectQueue","directExchange","directRoutingKey");
            String message="direct的测试消息！";
            /**
             * 发送消息到指定的队列
             * 参数 1 为交换机名称
             * 参数 2 为消息的RoutingKey 如果这个消息的RoutingKey和某个队列与交换机绑定的RoutingKey一致那么
             *       这个消息就会发送的指定的队列中
             * 注意：
             *   1、发送消息时必须确保交换机已经创建并且确保已经正确的绑定到了某个队列
             */
            channel.basicPublish("directExchange","directRoutingKey",null,message.getBytes("utf-8"));
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
