package mars.co.in.digimodel;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Set;



public class MainActivity extends AppCompatActivity {

    private SensorManager msensorManager;
    private Sensor msensor,msensorg;
    private long lastupdate=0;
    TextView xvalue,yvalue,zvalue,xvaluerot,yvaluerot,zvaluerot;
    double[] gravity,linear_acceleration;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    SensorEventListener mAccelerometerSensorListener,mGyroSensorListener;
    Button start,stop;
    WifiManager wifiManager;
    DhcpInfo dhcpInfo;
    Socket client=null;
    OutputStream outputStream;
    DataOutputStream dataOutputStream;
    double xdisp=0,xv=0,xu=0,dt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lastupdate=System.currentTimeMillis();
        startActivity(new Intent("android.settings.WIFI_SETTINGS"));
        msensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mapping();
        gravity=new double[3];
        linear_acceleration=new double[3];
        msensor=msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensorg = msensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        msensorManager.registerListener(mGyroSensorListener,msensorg,SensorManager.SENSOR_DELAY_NORMAL);
        msensorManager.registerListener(mAccelerometerSensorListener,msensor,SensorManager.SENSOR_DELAY_NORMAL);

        mAccelerometerSensorListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor mysensor=event.sensor;
                Log.e("Insidefirst","Accelero");
                if(mysensor.getType()==Sensor.TYPE_ACCELEROMETER){
                    long curtime= System.currentTimeMillis();
                    if((curtime-lastupdate)>100) {
                        dt=curtime-lastupdate;
                        lastupdate = curtime;
                        final double alpha = 0.8;

                        // Isolate the force of gravity with the low-pass filter.
                        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                        // Remove the gravity contribution with the high-pass filter.
                        linear_acceleration[0] = event.values[0] - gravity[0];
                        linear_acceleration[1] = event.values[1] - gravity[1];
                        linear_acceleration[2] = event.values[2] - gravity[2];
                        if(linear_acceleration[0]>0.5 || linear_acceleration[0]<-0.5)
                        {
                        xv+=(linear_acceleration[0]*dt/1000);
//                        xdisp=(Math.pow(xv,2)-Math.pow(xu,2))/(2*linear_acceleration[0]);
                        xdisp+=(xv*dt/1000)+(0.5*linear_acceleration[0]*Math.pow(dt,2)/1000000);}
                        xvalue.setText(xv + "");
                        yvalue.setText(xdisp + "");
                        zvalue.setText(linear_acceleration[0] + "");
                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mGyroSensorListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor mysensor=event.sensor;
                Log.e("InsideSecond","Gyro");
                if(mysensor.getType()==Sensor.TYPE_GYROSCOPE){
                    if (timestamp != 0) {
                        final float dT = (event.timestamp - timestamp) * NS2S;
                        // Axis of the rotation sample, not normalized yet.
                        float axisX = event.values[0];
                        float axisY = event.values[1];
                        float axisZ = event.values[2];

                        // Calculate the angular speed of the sample
                        double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                        // Normalize the rotation vector if it's big enough to get the axis
                        // (that is, EPSILON should represent your maximum allowable margin of error)
                        if (omegaMagnitude > Math.pow(10,-2)) {
                            axisX /= omegaMagnitude;
                            axisY /= omegaMagnitude;
                            axisZ /= omegaMagnitude;
                        }

                        // Integrate around this axis with the angular speed by the timestep
                        // in order to get a delta rotation from this sample over the timestep
                        // We will convert this axis-angle representation of the delta rotation
                        // into a quaternion before turning it into the rotation matrix.
                        double thetaOverTwo = omegaMagnitude * dT / 2.0f;
                        double sinThetaOverTwo = Math.sin(thetaOverTwo);
                        double cosThetaOverTwo = Math.cos(thetaOverTwo);
                        deltaRotationVector[0] = (float) (sinThetaOverTwo * axisX);
                        deltaRotationVector[1] = (float) (sinThetaOverTwo * axisY);
                        deltaRotationVector[2] = (float) (sinThetaOverTwo * axisZ);
                        deltaRotationVector[3] = (float) (cosThetaOverTwo);
                        xvaluerot.setText(axisX+"");
                        yvaluerot.setText(axisY+"");
                        zvaluerot.setText(axisZ+"");
                    }
                    timestamp = event.timestamp;
                    float[] deltaRotationMatrix = new float[9];
                    SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                    // User code should concatenate the delta rotation we computed with the current rotation
                    // in order to get the updated rotation.
                    // rotationCurrent = rotationCurrent * deltaRotationMatrix;
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(getApplication(),"Sending Started",Toast.LENGTH_LONG).show();
                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                dhcpInfo = wifiManager.getDhcpInfo();
                new ConnectClientToServer().execute(Protocols.convertIntIPtoStringIP(dhcpInfo.serverAddress));

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(),"Sending Stoped",Toast.LENGTH_LONG).show();
                try {
                    client.close();
                }
                catch (Exception e){
                    Log.e("stopped",e.toString());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void mapping(){
        xvalue=(TextView)findViewById(R.id.xvaluedisplay);
        yvalue=(TextView)findViewById(R.id.yvaluedisplay);
        zvalue=(TextView)findViewById(R.id.zvaluedisplay);
        xvaluerot=(TextView)findViewById(R.id.xvaluerotdisplay);
        yvaluerot=(TextView)findViewById(R.id.yvaluerotdisplay);
        zvaluerot=(TextView)findViewById(R.id.zvaluerotdisplay);
        start=(Button)findViewById(R.id.startsending);
        stop=(Button)findViewById(R.id.stopsending);

    }

    @Override
    protected void onResume() {
        super.onResume();
        msensorManager.registerListener(mAccelerometerSensorListener,msensor,SensorManager.SENSOR_DELAY_NORMAL);
        msensorManager.registerListener(mGyroSensorListener,msensorg,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        msensorManager.unregisterListener(mAccelerometerSensorListener);
        msensorManager.unregisterListener(mGyroSensorListener);

    }

    class ConnectClientToServer extends AsyncTask<String,String,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplication(),"Socket Started",Toast.LENGTH_LONG).show();


        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                client=new Socket();
                InetAddress inetAddress = InetAddress.getByName(params[0]);
                client.connect(new InetSocketAddress(inetAddress,8080));
                outputStream= client.getOutputStream();
                dataOutputStream=new DataOutputStream(outputStream);
                dataOutputStream.writeUTF(linear_acceleration[0]+"/");
                dataOutputStream.flush();
            }
            catch (Exception e){
                Log.e("IP address probs",e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Toast.makeText(getApplication(),"Socket connection done",Toast.LENGTH_LONG).show();

        }
    }
}
