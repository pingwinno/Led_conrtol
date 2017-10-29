package com.example.shiro.netCore;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Socket Client class
 */

public class DataHelper {
    private static final String LOG_TAG = "Data Service";

    private String hostAddress = "";

    private int serverPort = 0;

    private Socket socket = null;

    public DataHelper(String hostAddress, int serverPort)
    {
        this.hostAddress = hostAddress;
        this.serverPort = serverPort;
    }

    public void openConnection () throws  Exception
    {
        closeConnection();

        try {
            socket = new Socket(hostAddress,serverPort);
        }
        catch (IOException e)
        {
            throw new Exception(" Can't open connection" +e.getMessage());
        }
    }

    public void closeConnection()
    {
    if (socket != null && !socket.isClosed())
    {
        try {
            socket.close();
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "can't close socket" + e.getMessage());
        }
        finally {
            socket=null;
        }
    }
    }

    public void sendData(byte[] data) throws Exception {
        if (socket == null || socket.isClosed()) {
            throw new Exception("socket close or not exist");

        }
        try
        {
            socket.getOutputStream().write(data);
            socket.getOutputStream().flush();
        }
        catch (IOException e)
        {
            throw new Exception("can't send data: " + e.getMessage());
        }

    }


    @Override
    protected void finalize () throws Throwable
    {
        super.finalize();
        closeConnection();
    }

}

