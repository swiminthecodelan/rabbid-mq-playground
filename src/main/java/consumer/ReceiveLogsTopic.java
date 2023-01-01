package consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsTopic {
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection  = factory.newConnection();
        Channel channel = connection.createChannel();
        // 频道内定义一个 exchange
        channel.exchangeDeclare(EXCHANGE_NAME,"topic");
        // 频道内创建一个队列并得到名字
        String queueName = channel.queueDeclare().getQueue();
        if (args.length < 1) {
            System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
            System.exit(1);
        }
        for (String bindingKey : args) {
            // 将 channel 内定义好的 exchange 和 队列绑定
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
        }
        DeliverCallback deliverCallback = (consumerTag ,delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        // 消费队列
        channel.basicConsume(queueName, true,deliverCallback,  consumerTag -> {});
    }
}
