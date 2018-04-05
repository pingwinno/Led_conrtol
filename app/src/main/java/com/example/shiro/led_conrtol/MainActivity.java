package com.example.shiro.led_conrtol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.shiro.netCore.Base64Converter;
import com.example.shiro.netCore.BroadcastSearch;
import com.example.shiro.netCore.JSONEntity;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private SeekBar mRedSeekBar, mGreenSeekBar, mBlueSeekBar;
    private Switch mPowerSwitch;
    private TextView mText;
    private JSONEntity powerOff = new JSONEntity(0,0,0);
    private Gson gson = new Gson();
    private JSONEntity storedJson = new JSONEntity();

    private BroadcastSearch search = new BroadcastSearch();

    private static final String LOG_TAG = "Data Service";
    JSONEntity jsonEntity = new JSONEntity();

    MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://192.168.1.201:1883";

    String clientId = "ExampleAndroidClient";

    final String topic = "test";
    final String topic2 = "test1";







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.settings:
            {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new PerfFragment())
                        .commit();
            }


                return true;


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPowerSwitch =  findViewById(R.id.power_switch);
        if (mPowerSwitch != null)
        {
            mPowerSwitch.setOnCheckedChangeListener(this);

        }

        mText = findViewById(R.id.text);

        mRedSeekBar =  findViewById(R.id.seekBar_Red);
        mGreenSeekBar =  findViewById(R.id.seekBar_Green);
        mBlueSeekBar =  findViewById(R.id.seekBar_Blue);

        mRedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mBlueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mGreenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mText.setText("");


        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.e(LOG_TAG,"Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic(topic);
                    subscribeToTopic(topic2);
                } else {
                    Log.e(LOG_TAG,"Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(LOG_TAG,"The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String query = new String(message.getPayload());

                Log.e(LOG_TAG,"Incoming message: " + query);

                if (query.contains("r")&& topic.equals(topic2))
                {
                    storedJson = gson.fromJson(query,JSONEntity.class);
                    mRedSeekBar.setProgress(storedJson.getR());
                    mGreenSeekBar.setProgress(storedJson.getG());
                    mBlueSeekBar.setProgress(storedJson.getB());
                }
                else if (query.equals("1"))
                {
                    mPowerSwitch.setChecked(true);
                }
                else if (query.equals("0"))
                {
                    mPowerSwitch.setChecked(false);
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);







        try {
            Log.e(LOG_TAG,"Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic(topic);
                    subscribeToTopic(topic2);
                    publishMessage( "query",topic2);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(LOG_TAG,"Failed to connect to: " + serverUri);
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {

        if(isChecked)
        {
            mRedSeekBar.setEnabled(true);
            mBlueSeekBar.setEnabled(true);
            mGreenSeekBar.setEnabled(true);
            publishMessage( "1",topic2); }
        else
            {
                mRedSeekBar.setEnabled(false);
                mGreenSeekBar.setEnabled(false);
                mBlueSeekBar.setEnabled(false);
                publishMessage( "0",topic2);
        }

    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                       jsonEntity.setJSON( mRedSeekBar.getProgress(), mGreenSeekBar.getProgress(), mBlueSeekBar.getProgress());


                        publishMessage(gson.toJson(jsonEntity), topic);

                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }

                }
            }).start();

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {



        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonEntity.setJSON( mRedSeekBar.getProgress(), mGreenSeekBar.getProgress(), mBlueSeekBar.getProgress());


                        publishMessage(gson.toJson(jsonEntity), topic2);

                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }

                }
            }).start();


        }
    };
@Override
protected void onStart ()
{

    super.onStart();











}
@Override
protected void onDestroy ()
{



    super.onDestroy();

}


    public void subscribeToTopic(String topic){
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(LOG_TAG,"Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(LOG_TAG,"Failed to subscribe");
                }
            });



        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessage(String jsonString, String publishTopic){

        if (mqttAndroidClient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(jsonString.getBytes());
                mqttAndroidClient.publish(publishTopic, message);
                Log.e(LOG_TAG, "Message Published");
                if (!mqttAndroidClient.isConnected()) {
                    Log.e(LOG_TAG, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
                }
            } catch (MqttException e) {
                System.err.println("Error Publishing: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            Log.e(LOG_TAG,"Failed to publishing. Connection not exist");
        }
    }





    }

