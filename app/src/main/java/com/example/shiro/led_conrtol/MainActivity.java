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
import com.example.shiro.netCore.DataHelper;
import com.example.shiro.netCore.JSONEntity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private SeekBar mRedSeekBar, mGreenSeekBar, mBlueSeekBar;
    private Switch mPowerSwitch;
    private TextView mText;
    private DataHelper serverCon = null;
    private BroadcastSearch search = new BroadcastSearch();
    private String ip = null;
    private static final String LOG_TAG = "Data Service";
    JSONEntity jsonEntity = new JSONEntity();



    Base64Converter converter = new Base64Converter();


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

        mPowerSwitch = (Switch) findViewById(R.id.power_switch);
        if (mPowerSwitch != null)
        {
            mPowerSwitch.setOnCheckedChangeListener(this);

        }

        mText = (TextView) findViewById(R.id.text);

        mRedSeekBar = (SeekBar) findViewById(R.id.seekBar_Red);
        mGreenSeekBar = (SeekBar) findViewById(R.id.seekBar_Green);
        mBlueSeekBar = (SeekBar) findViewById(R.id.seekBar_Blue);

        mRedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mBlueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mGreenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mText.setText("");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                   ip = search.initFinder();

                    Log.e(LOG_TAG, ip);
                    if ( ip != null) {
                        serverCon = new DataHelper(ip, 8000);
                        Log.e(LOG_TAG, "connect");
                        serverCon.openConnection();
                        Thread.sleep(200);
                        Log.e(LOG_TAG, "connected");
                        Thread.sleep(200);
                        Log.e(LOG_TAG, "send message");
                    }
                    else
                    {
                        Log.e(LOG_TAG, "Can't get ip address ");
                        serverCon.closeConnection();
                    }

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        }).start();


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (isChecked == true)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        serverCon.sendData(Base64Converter.encode("ON"));
                    }
                    catch (Exception e)
                    {
                        Log.e(LOG_TAG, e.getMessage());
                    }

                }
            }).start();
        }
        else
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            serverCon.sendData(Base64Converter.encode("OfF"));
                        }
                        catch (Exception e)
                        {
                            Log.e(LOG_TAG, e.getMessage());
                        }

                    }
                }).start();
            }

    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Log.e(LOG_TAG, "sending data");
            if (serverCon == null) {
                Log.e(LOG_TAG, "server not exist");
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonEntity.setJSON(0, mRedSeekBar.getProgress(),mGreenSeekBar.getProgress(),mBlueSeekBar.getProgress());



                        serverCon.sendData( converter.encodeRGB(jsonEntity));

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
    serverCon.closeConnection();
}








    }

