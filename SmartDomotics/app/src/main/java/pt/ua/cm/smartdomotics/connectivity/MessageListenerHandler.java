package pt.ua.cm.smartdomotics.connectivity;

/**
 * Created by alvaro on 05-10-2017.
 */
import android.os.Handler;
import android.os.Message;

import pt.ua.cm.smartdomotics.connectivity.IMessageListener;

public class MessageListenerHandler extends Handler {
    private final IMessageListener messageListener;
    private final String payloadKey;

    public MessageListenerHandler(IMessageListener messageListener, String payloadKey) {
        this.messageListener = messageListener;
        this.payloadKey = payloadKey;
    }

    @Override
    public void handleMessage(Message msg) {
        messageListener.messageReceived(msg.getData().getString(payloadKey));
    }
}