package com.autoio.voice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.autoio.voice_view.VoiceView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.hint_bottom_text)
    TextView hint_bottom_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        final VoiceView voiceView = (VoiceView) findViewById(R.id.voice_view);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        voiceView.changeState(VoiceView.PRE_SEARCH_STATE);
                    }
                });

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(11000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        voiceView.changeState(VoiceView.END_SEARCH_STATE);
                    }
                });

            }
        }).start();*/
        voiceView.setOnPressedListener(new VoiceView.OnPressedListener() {
            @Override
            public void onVoicePressed() {
                //按下时,
                hint_bottom_text.setText("我在听呢");
            }

            @Override
            public void onVoiceUnPressed() {
                //抬起
                hint_bottom_text.setText("正在搜索");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                voiceView.changeState(VoiceView.END_SEARCH_STATE);
                                hint_bottom_text.setText("");
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
