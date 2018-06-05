package com.ndex;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by grishberg on 20.05.18.
 */
public class MessageConsumer extends DefaultConsumer {
    private OnNewMessageListener listener = new ListenerStub();
    private Channel channel;

    MessageConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    void setListener(OnNewMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        listener.onMessageReceived(body,
                properties.getCorrelationId(),
                properties.getReplyTo());
    }

    private static class ListenerStub implements OnNewMessageListener {
        @NotNull
        @Override
        public void onMessageReceived(@NotNull byte[] message,
                                      @NotNull String corrId,
                                      @NotNull String rk) {
           /* stub */
        }
    }
}
