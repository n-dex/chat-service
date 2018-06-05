package com.ndex;

import java.io.IOException;

/**
 * Created by grishberg on 20.05.18.
 */
public interface MessageSender {
    void sendMessage(byte[] message, String userId, String corrId) throws IOException;
}
