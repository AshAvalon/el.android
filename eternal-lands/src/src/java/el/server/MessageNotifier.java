package el.server;

import el.protocol.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageNotifier {
    private List<MessageReceiveListener> messageReceiveListeners = new ArrayList<>();
    private List<MessageSendListener> messageSendListeners = new ArrayList<>();
    
    public void addListener(MessageReceiveListener listener) {
        messageReceiveListeners.add(listener);
    }

    public void addListener(MessageSendListener listener) {
        messageSendListeners.add(listener);
    }

    public void notifyReceive(Message message) {
        for (MessageReceiveListener listener : messageReceiveListeners) {
            listener.onMessageReceived(message);
        }
    }

    public void notifySend(Message message) {
        for (MessageSendListener listener : messageSendListeners) {
            listener.onMessageSend(message);
        }
    }
}
