package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String SERVER_DOMAIN = "se2-isys.aau.at";
    private static final int SERVER_PORT = 53212;
    private static final String MATRIKEL = "01461562";

    private PrintWriter mOutputBuffer;
    private BufferedReader mInputBuffer;

    EditText mInputMatrikel;
    TextView mServerAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputMatrikel = (EditText) findViewById(R.id.editTextNumber);
        mServerAnswer = (TextView) findViewById(R.id.Result);

    }


    public void messageReceived(String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Log.d("lalala", "Print answer " + message);
                mServerAnswer.setText(message);
            }
        });
    }

    public void sendNumber(View view) {
        Editable matrikel = mInputMatrikel.getText();
        if (matrikel.length() == 8) {
            sendOverTCP(matrikel.toString());
        }
    }

    private void sendOverTCP(String matrikelNumber) {
        new Thread(new Runnable() {
            public void run() {
                try {

                    InetAddress adress = InetAddress.getByName(SERVER_DOMAIN);
                    Log.d("lalala", "connecting...");
                    Socket soc = new Socket(adress, SERVER_PORT);


                    mOutputBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);
                    mInputBuffer = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                    if (mOutputBuffer != null) {
                        Log.d("lalala", " sending: " + matrikelNumber);
                        mOutputBuffer.println(matrikelNumber);
                        mOutputBuffer.flush();
                    }

                    while (true) {
                        String received = mInputBuffer.readLine();
                        if (received != null) {
                            messageReceived(received);
                            Log.d("lalala", "receiving: '" + received + "'");
                            soc.close();
                            return;
                        }
                    }

                } catch (Exception e) {
                    Log.e("lalala", "C: Error", e);
                }
            }
        }).start();
    }


    public void calculate(View view) {
        Editable matrikel = mInputMatrikel.getText();
            if(matrikel.toString().equals(MATRIKEL)) {

                List<Integer> numbers = new ArrayList<Integer>();

                for(int i = 0; i<MATRIKEL.length(); i++){
                    int current = Integer.parseInt(String.valueOf(MATRIKEL.charAt(i)));
                    if(!(current == 2 || current == 3 || current == 5 || current == 7)){
                        numbers.add(current);
                    }
                }
                Collections.sort(numbers);
                String sortedString = "";
                for(int current : numbers){
                    sortedString += current;
                }
                mServerAnswer.setText(sortedString);
            }

        }
    }
