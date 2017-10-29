package com.example.shiro.netCore;

import android.content.pm.ServiceInfo;

import com.cjsavage.java.net.discovery.ServiceFinder;



/**
 * Created by shiro on 17.10.17.
 */


public class BroadcastSearch
{
    private static final String SERVICE_ID = "e9ababe5872f24caf1a504f1d675470c";
    private String ip = null;

    public String initFinder() {
        // Receive responses via TCP port 1234
        // ServiceFinder finder = new ServiceFinder(1234);
        // Receive response via multicast
        ServiceFinder finder = new ServiceFinder();
        finder.addListener(mListener);
        finder.startListening();
        finder.findServers(SERVICE_ID, 0);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {

        }


        finder.stopListening();

        return ip;

    }

    private ServiceFinder.Listener mListener = new ServiceFinder.Listener() {


        @Override
        public void serverFound(com.cjsavage.java.net.discovery.ServiceInfo serviceInfo, int i, ServiceFinder serviceFinder) {
            System.out.println("\r\nFound service provider named " +
                    serviceInfo.getServerName() + " at " + serviceInfo.getServiceHost() + ":" +
                    + serviceInfo.getServicePort() + "\r\nCommand: ");
            ip = serviceInfo.getServiceHost();
        }

        public void listenStateChanged(ServiceFinder finder, boolean listening) {
            if (listening) {
                System.out.println("ServiceFinder is listening for responses.");
            } else {
                System.out.println("ServiceFinder has shut down.");
            }
        }
    };


}
