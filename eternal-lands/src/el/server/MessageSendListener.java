package el.server;

import el.protocol.Message;

public interface MessageSendListener {
    public void onMessageSend(Message message);
}
