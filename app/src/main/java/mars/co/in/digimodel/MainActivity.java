package mars.co.in.digimodel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager msensorManager;
    private Sensor msensor;
    private long lastupdate=0;
    TextView xvalue,yvalue,zvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mapping();
        msensor=msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensorManager.registerListener(this,msensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void mapping(){
        xvalue=(TextView)findViewById(R.id.xvaluedisplay);
        yvalue=(TextView)findViewById(R.id.yvaluedisplay);
        zvalue=(TextView)findViewById(R.id.zvaluedisplay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        msensorManager.registerListener(this,msensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        msensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mysensor=event.sensor;
        if(mysensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long curtime= System.currentTimeMillis();
            if((curtime-lastupdate)>100){
                long diffTime = (curtime - lastupdate);
                lastupdate = curtime;
            }
            xvalue.setText(x+"");
            yvalue.setText(y+"");
            zvalue.setText(z+"");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
