import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import models.Chat;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class AmqpClient {
    static final String EXCHANGE_NAME = "messenger.topic";
    static final String SERVICE_ROUTING_KEY = "services";
    private final String name;
    private Connection connection = null;
    private MessageSender messageSender = new MessageSenderStub();
    private final MessageHandler messageHandler;

    public AmqpClient(String name, MessageHandler messageHandler) {
        this.name = name;
        this.messageHandler = messageHandler;
    }

    public static void main(String[] argv) {

        UserManager userManager = new UserManager();
        ChatManager chatManager = new ChatManager(userManager);
        addFakeData(userManager, chatManager);
        MessageHandler messageHandler = new MessageHandler(chatManager, userManager);
        AmqpClient client = new AmqpClient("service", messageHandler);
        client.connect();

        new Scanner(System.in).nextLine();
        System.out.println("End.");

        client.disconnect();
    }

    public void connect() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("85.143.222.18");
        factory.setPassword("msgr");
        factory.setUsername("rabbitmq");
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
        try {
            connection = factory.newConnection();

            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            messageSender = new MessageSenderImpl(channel);

            channel.queueDeclare(SERVICE_ROUTING_KEY, true, true, true, null);
            channel.queueBind(SERVICE_ROUTING_KEY, EXCHANGE_NAME, SERVICE_ROUTING_KEY);
            channel.basicQos(1);

            MessageConsumer consumer = new MessageConsumer(channel);

            channel.basicConsume(SERVICE_ROUTING_KEY, true, consumer);

            consumer.setListener(messageHandler);
            messageHandler.setMessageSender(messageSender);
            onConnected();
            System.out.println("setup ok");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void onConnected() {

    }

    private static class MessageSenderStub implements MessageSender {
        @Override
        public void sendMessage(byte[] message, String userId, String corrId)
                throws IOException {/* stub */}
    }

    /**
     * Add fake data there.
     */
    private static void addFakeData(UserManager userManager, ChatManager chatManager) {
        userManager.addNewUserIfNotExists("b81c268cdba3a902");
        chatManager.createChat("b81c268cdba3a902", "Test Chat");

        userManager.addNewUserIfNotExists("11111111111");
        Chat chat = chatManager.createChat("11111111111", "Test Chat for 111");
        chatManager.inviteToChat("b81c268cdba3a902", chat.getId(), "11111111111");
    }
}