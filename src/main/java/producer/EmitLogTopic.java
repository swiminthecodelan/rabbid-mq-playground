package producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogTopic {
    public static final String EXCHANGE_NAME = "topic_logs";
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            String message = "This is a topic log";
            // 定义一个 exchange
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            // publish 到 exchange
            channel.basicPublish(EXCHANGE_NAME,"docker.info" , null, message.getBytes());
        }
    }
}
