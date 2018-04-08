package pt.ua.cm.smartdomotics.connectivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.zeromq.ZMQ;

/**
 * Created by alvaro on 05-10-2017.
 */

public class ZeroMQMessageTask extends AsyncTask<String, Void, String> {
    private final Handler uiThreadHandler;
    public static final String MESSAGE_PAYLOAD_KEY = "db_command";
    private String ip = "localhost";
    public ZeroMQMessageTask(Handler uiThreadHandler, String ip) {
        this.uiThreadHandler = uiThreadHandler;
        this.ip = ip;
        Log.d("new ip message task", ip);
    }

    @Override
    protected String doInBackground(String... params) {
        if(ip.equals("localhost")){
            return "";
        }
        Log.d("ZeroMQMessageTask", "sending " +params[0]);
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        Log.d("ip", ip);
        socket.connect("tcp://"+ip+":9696");

        socket.send(params[0].getBytes(), 0);
        String result = new String(socket.recv(0));

        socket.close();
        context.term();
        Log.d("ZeroMQMessageTask", "sent " +params[0]);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        uiThreadHandler.sendMessage(bundledMessage(uiThreadHandler, result));
    }

    private Message bundledMessage(Handler uiThreadHandler, String msg) {
        Message m = uiThreadHandler.obtainMessage();
        prepareMessage(m, msg);
        return m;
    };

    private void prepareMessage(Message m, String msg){
        Bundle b = new Bundle();
        b.putString(MESSAGE_PAYLOAD_KEY, msg);
        m.setData(b);
        return ;
    }
}