package rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if(n == 0) return 0;
        if(n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

    channel.queuePurge(RPC_QUEUE_NAME);

    channel.basicQos(1);

    System.out.println(" [x] Awaiting RPC requests");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
              .correlationId(delivery.getProperties().getCorrelationId())
              .build();
      String response = "";
      try {
          String message =  new String(delivery.getBody(), "UTF-8");
          System.out.println(" [.] fib(" + message + ")");
          response += fib(Integer.parseInt(message));
      } catch (Exception e) {
          e.printStackTrace();
      } finally {
          channel.basicPublish("", delivery.getProperties().getReplyTo(), properties , response.getBytes("UTF-8"));
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      }
    };
    channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, consumerTag -> {});
    }
}
