import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * Created by grishberg on 20.05.18.
 */
public class MessageSenderImpl implements MessageSender {
    private final Channel channel;

    MessageSenderImpl(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(byte[] message, String userId, String corrId) throws IOException {
        if (userId == null) {
            System.out.println("userId is null");
            return;
        }
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .build();

        channel.basicPublish(AmqpClient.EXCHANGE_NAME, userId, replyProps, message);
    }
}
