package ud.example.sonidos;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    private SoundPool sPool;
    private int sound1, sound2, sound3, sound4;
    public MediaPlayer player;
    private  AudioManager audioManager;
    private Button boton03, boton04;
    private SeekBar volumen;
    private TextView total, hasonado, duracion;
    private ProgressBar Tiempo;
   // private Thread t;
    static volatile boolean continuarhilo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(10)
                .build();
        }
        else{

            sPool = new SoundPool(6, AudioManager.STREAM_MUSIC,0);

        }

        sound1 = sPool.load(this,R.raw.gallina,1);
        sound2 = sPool.load(this,R.raw.perro,1);
        sound3 = sPool.load(this,R.raw.leon,1);
        sound4= sPool.load(this,R.raw.serpiente,1);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        boton03 = findViewById(R.id.button3);
        boton04 = findViewById(R.id.button4);
        boton04.setEnabled(false);

        total = findViewById(R.id.textView3);
        total.setText("");
        hasonado = findViewById(R.id.textView4);
        hasonado.setText("");
        duracion = findViewById(R.id.textView5);


        setVolumeControlStream(AudioManager.STREAM_MUSIC); //con essta

        volumen = findViewById(R.id.seekBar2);
        volumen.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Tiempo = findViewById(R.id.progressBar);

    }

    public  void  suenaboton01(View v){ sPool.play(sound1,1,1,1,0,1); }
    public void suenaboton02(View v){sPool.play(sound2,1,1,1,2,1);}
    public  void  suenaboton05(View v){ sPool.play(sound3,1,1,1,0,1); }
    public void suenaboton06(View v){sPool.play(sound4,1,1,1,0,1);}


    public void detenerplayer(){

        continuarhilo=false;
        player.stop();
        Tiempo.setProgress(0);
        boton03.setText("Reproducir Mediaplayer");
        total.setText("Duración Total");
        hasonado.setText(" ");
        boton04.setEnabled(false);
        boton04.setText("Pausar");

    }

    public void suenaboton03(View v){

          if((player!=null && player.isPlaying() )){
                 detenerplayer();
              }

          else{
               player = MediaPlayer.create(this,R.raw.pharrell_williams_happy);

               total.setText(String.valueOf(player.getDuration()));
               setVolumeControlStream(AudioManager.STREAM_MUSIC); //con essta
               volumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

               boton03.setText("Detener MediaPlayer");
               boton04.setText("Pausar");
               boton04.setEnabled(true);


               player.start();
              if(player.getTrackInfo().length == player.getDuration()){
                  detenerplayer();
              }

              Tiempo.setMax(player.getDuration());

             new Thread(){
                 @Override
                   public void run() {

                      int totalProgressTime = player.getDuration();

                     while(continuarhilo) {
                         while (player.getCurrentPosition() < totalProgressTime) {
                             try {

                                 duracion.setText(String.valueOf(player.getCurrentPosition()));
                                 Tiempo.setProgress(player.getCurrentPosition());
                                 sleep(200);

                             } catch (InterruptedException e) {


                             }
                         }
                     }
                   }
               }.start();



           }

     }



    public void suenaboton04(View v){

        if(player != null){
            if(player.isPlaying()){
                player.pause();
                boton04.setText("Reiniciar");
                hasonado.setText(String.valueOf(player.getCurrentPosition()));
                Tiempo.setProgress(player.getCurrentPosition());


            }
            else{
                setVolumeControlStream(AudioManager.STREAM_MUSIC); //con essta
                volumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                player.start();
                boton04.setText("Pausar");
                boton04.setEnabled(true);

            }
        }
    }



    @Override
    protected void onDestroy(){
       super.onDestroy();
       sPool.release();
       sPool = null;
       player.release();
      player = null;

    }

}