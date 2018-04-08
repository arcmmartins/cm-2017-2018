package pt.ua.cm.smartdomotics.connectivity;

/**
 * Created by alvaro on 05-10-2017.
 */
public interface IMessageListener {
    void messageReceived(String messageBody);
}