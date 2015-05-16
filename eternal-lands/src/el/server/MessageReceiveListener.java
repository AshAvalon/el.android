package el.server;

import el.protocol.Message;

public interface MessageReceiveListener {
    void onMessageReceived(Message message);
}
