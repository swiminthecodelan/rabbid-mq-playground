package consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs {
    private static final String EXCHANGE_NAME = "logs";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel =  connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 队列名随机生成，可以生成一个空的、新的、使用完即自动删除的队列。
        String queueName = channel.queueDeclare().getQueue();
        // 绑定队列和 exchange
        for(String severity : args){
            channel.queueBind(queueName, EXCHANGE_NAME, severity);
        }
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(queueName,  true, deliverCallback, consumerTag -> { });
    }
}
