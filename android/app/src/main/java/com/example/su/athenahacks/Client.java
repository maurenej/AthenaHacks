package com.example.su.athenahacks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

public class Client extends AsyncTask<Object, Object, String> {

    String dstAddress, dstMessage;
    int dstPort;
    String response = "";
    TextView textResponse;

    Client(String addr, int port, String message, TextView textResponse) {
        dstAddress = addr;
        dstMessage = message;
        dstPort = port;
        this.textResponse = textResponse;
    }

    @Override
    protected String doInBackground(Object... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;

            InputStream inputStream = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            output.write(dstMessage.getBytes());
            Log.i("Info", "Sent");
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            response ="Received by the server + " + timeStamp;
//          while ((bytesRead = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8");
//            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        textResponse.setText(response);
        super.onPostExecute(result);
    }

}