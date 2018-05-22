import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class AmqpClient {
    static final String EXCHANGE_NAME = "messenger.topic";
    private static final String QUEUE_NAME = "services";
    private final String name;
    private Connection connection = null;
    private MessageSender messageSender = new MessageSenderStub();
    private final MessageHandler messageHandler;

    public AmqpClient(String name, MessageHandler messageHandler) {
        this.name = name;
        this.messageHandler = messageHandler;
    }

    public static void main(String[] argv) {

        ChatManager chatManager = new ChatManager();
        MessageHandler messageHandler = new MessageHandler(chatManager);
        AmqpClient client = new AmqpClient("service", messageHandler);
        client.connect();

        new Scanner(System.in).nextLine();
        System.out.println("End.");

        client.disconnect();
    }

    public void connect() {
        ConnectionFactory factory = new ConnectionFactory();
        new Thread(() -> doConnect(factory)).start();
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doConnect(ConnectionFactory factory) {
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();

            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            messageSender = new MessageSenderImpl(channel);

            channel.queueDeclare(QUEUE_NAME, true, true, true, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME);
            channel.basicQos(1);

            MessageConsumer consumer = new MessageConsumer(channel);

            channel.basicConsume(QUEUE_NAME, true, consumer);

            consumer.setListener(messageHandler);
            messageHandler.setMessageSender(messageSender);
            System.out.println("setup ok");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static class MessageSenderStub implements MessageSender {
        @Override
        public void sendMessage(byte[] message, String userId, String corrId)
                throws IOException {/* stub */}
    }
}