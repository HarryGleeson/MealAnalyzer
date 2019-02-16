package com.example.harry.sqltest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** TakePhoto.java
 * This class allows the user to take a picture using the device's camera and send it
 * as a byte array to the server socket. It also gets the relevant information back from
 * the server socket and displays it.
 */

public class TakePhoto extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    ProgressBar spinner;
    Handler handler = new Handler();
    int port = 4442;
    String IP = "192.168.0.3";
    String input = "";
    int lipaseDosage, tabletDosage;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo);
        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);
        textView = (TextView)findViewById(R.id.textView);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.INVISIBLE);


        textView.setMovementMethod(new ScrollingMovementMethod());

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
        Intent receivedIntent = getIntent();
        lipaseDosage = receivedIntent.getIntExtra("lipase", -1);
        tabletDosage = receivedIntent.getIntExtra("tablet", -1);
        Thread myThread = new Thread(new MyServerThread());
        myThread.start();
    }

    /**
     * Receives each identified food and the total fat contained in the
     * meal back from the server. Calculates total lipase dosage and number of
     * tablets required based on information in user profile.
     */
    class MyServerThread implements Runnable{
        Socket s;
        ServerSocket ss;
        String foodString;
        Handler h = new Handler();
        float fat = 0;

        @Override
        public void run() {
            try {
                ss = new ServerSocket(4444);
                foodString = "";
                while(true){
                    s = ss.accept();
                    ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
                    String[] input = (String[])inStream.readObject();
                    fat = Float.parseFloat(input[input.length-1]);
                    if(fat==0){
                        foodString="No foods were identified from this image";
                    }
                    else{
                        foodString="Identified Food(s):\n";
                        for(int i=0; i<input.length-1; i++){
                            foodString+=input[i]+"\n";
                        }
                    float dosage = fat*lipaseDosage;
                    float tablets = dosage/tabletDosage;
                        foodString+=("\nLipase Dosage: "+dosage);
                        foodString+=("\nTablets Required: "+tablets);
                    }
                    h.post(new Runnable()   {
                        @Override
                        public void run(){
                            spinner.setVisibility(View.GONE);
                            textView.setText(foodString);
                        }
                    });
                }
            }catch (IOException e){

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Takes image taken on devices camera, displays it on the screen and converts it to
     * a Byte array to be sent to the server socket
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);

            byte[] encodedImage = baos.toByteArray();
            SendImageClient sic = new SendImageClient();
            sic.execute(encodedImage);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong when selecting image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends image to the server socket in the form of a Byte array
     */
    public class SendImageClient extends AsyncTask<byte[], Void, Void> {
        @Override
        protected Void doInBackground(byte[]... voids) {
            try{
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP, port), 12000);

                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(voids[0].length);
                dos.write(voids[0], 0, voids[0].length);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"Analysing Meal. Please Wait...",Toast.LENGTH_LONG).show();
                        //textView.setText("Analysing Meal. Please Wait...");
                    }
                });

                dos.close();
                out.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
