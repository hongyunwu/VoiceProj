package com.autoio.voice;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autoio.voice_view.VoiceView;
import com.mobvoi.speech.SpeechClient;
import com.mobvoi.speech.SpeechClientListener;
import com.mobvoi.speech.VadType;
import com.mobvoi.speech.tts.TTSRequest;

import java.util.ArrayList;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.hint_bottom_text_src)
    TextView hint_bottom_text_src;
    @BindView(R.id.hint_bottom_text_des)
    TextView hint_bottom_text_des;
    @BindString(R.string.can_i_help_you)
    String can_i_help_you;
    @BindString(R.string.i_am_listening)
    String i_am_listening;
    @BindString(R.string.please_cay_number)
    String please_cay_number;
    @BindString(R.string.searching)
    String searching;
    @BindView(R.id.hint_bottom_content)
    RelativeLayout hint_bottom_content;
    private long hint_duration = 1000;
    private ValueAnimator widthAnimator;
    public static final String TAG = "MainActivity";
    private boolean needAnimateTop = true;
    @BindView(R.id.voice_view)
    VoiceView voice_view;
    @BindDimen(R.dimen.voice_circle_margin_large_top)
    int voice_circle_margin_large_top;
    @BindDimen(R.dimen.voice_circle_margin_small_top)
    int voice_circle_margin_small_top;
    @BindDimen(R.dimen.voice_circle_large_size)
    int voice_circle_large_size;
    @BindDimen(R.dimen.voice_circle_small_size)
    int voice_circle_small_size;
    @BindView(R.id.recycler_list)
    RecyclerView recycler_list;
    @BindView(R.id.hint_bottom_diot_left)
    ImageView hint_bottom_diot_left;
    @BindView(R.id.hint_bottom_diot_right)
    ImageView hint_bottom_diot_right;
    @BindView(R.id.rl_hint_bottom)
    RelativeLayout rl_hint_bottom;
    @BindDimen(R.dimen.voice_bottom_margin_top)
    int voice_bottom_margin_top;
    @BindDimen(R.dimen.hint_top_margin_top)
    int hint_top_margin_top;
    @BindView(R.id.rl_hint_top)
    RelativeLayout rl_hint_top;
    @BindView(R.id.hint_top_text_desc)
    TextView hint_top_text_desc;
    @BindView(R.id.top_diot_left)
    ImageView top_diot_left;
    @BindView(R.id.top_diot_right)
    ImageView top_diot_right;
    private SearchResultAdapter searchResultAdapter;
    private boolean isDiotScaled = false;
    @BindView(R.id.shadow)
    ImageView shadow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initSpeechClient();
        initAnimation();



        voice_view.setOnPressedListener(new VoiceView.OnPressedListener() {

            @Override
            public void onVoiceDown() {
                // 开始Mix的语音搜索
                SpeechClient.getInstance().startMixRecognizer(sClientName);
                //按下时,
                setBottomText(i_am_listening);
                //hint_bottom_text.setText(i_am_listening);
            }

            @Override
            public void onVoiceCancel() {
                SpeechClient.getInstance().cancelReconizer(sClientName);
                setBottomText(can_i_help_you);
                //时间太短
            }

            //此方法可以不用
            @Override
            public void onVoiceUp() {

            }

            @Override
            public void onVoiceSearch() {
                //抬起
                setBottomText(searching);
                // 结束Mix的语音搜索
                SpeechClient.getInstance().stopRecognizer(sClientName);
            }
        });
    }

    /**
     * 初始动画
     */
    private void initAnimation() {
        hint_bottom_content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                hint_bottom_content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                shadow.setAlpha(0f);
                hint_bottom_text_des.setScaleX(0f);
                hint_bottom_text_des.setScaleY(0f);
                hint_bottom_text_des.setAlpha(0f);
                shadow.animate().alpha(1f).setDuration(3000).setInterpolator(new LinearInterpolator()).setStartDelay(200).start();
                hint_bottom_text_src.setVisibility(View.GONE);
                hint_bottom_content.measure(0,0);
                final int desWidth = hint_bottom_content.getMeasuredWidth();
                //已知原宽度和目的宽度，差值就是需要做的动画
                ValueAnimator initLargeAnimator = ValueAnimator.ofInt(0, desWidth);
                initLargeAnimator.setDuration(1200);//动画时间暂定为1s
                initLargeAnimator.setRepeatCount(0);
                initLargeAnimator.setInterpolator(new OvershootInterpolator());
                initLargeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int width = (int) animation.getAnimatedValue();
                        if (hint_bottom_content.getVisibility()!=View.VISIBLE&&width>=hint_bottom_diot_left.getWidth()){
                            hint_bottom_content.setVisibility(View.VISIBLE);
                        }

                        ViewGroup.LayoutParams layoutParams = hint_bottom_content.getLayoutParams();
                        layoutParams.width = width;
                        hint_bottom_content.setLayoutParams(layoutParams);
                    }
                });
                initLargeAnimator.start();

                hint_bottom_text_des.animate().alpha(1f).scaleX(1f).scaleY(1f).setInterpolator(new OvershootInterpolator()).setDuration(1200).start();
            }
        });



    }


    // 非正式Appkey， 仅提供给开发者Demo使用
    private static final String sAppKey = "com.mobvoi.test";
    // 仅用作统计，请全局使用唯一字符串
    private static final String sClientName = "autoio_voice";
    // 联系人列表，供离线识别使用，语义为“打电话给王斌”，“给熊伟打电话”，“发短信给邓凯”等
    private static final String[] sContacts = {"邓凯", "王斌", "熊伟"};
    // 应用列表，供离线识别使用，语义为“打开支付宝”，“关闭支付宝”等
    private static final String[] sApps = {"导航", "音乐", "收音机","电话"};
    // 命令词列表，供离线识别使用，语义为“关机”，“重启”等
    private static final String[] sVoiceCommands = {"关机", "重启", "飞行模式"};
    // 位置信息，格式为 “国家，省，市，区，街道，门牌号，纬度，经度”
    private static final String sLocation = "中国,北京市,北京市,海淀区,苏州街,3号,39.989602,116.316568";
    /**
     * 进行出门问问初始化
     */
    private void initSpeechClient() {
        // 设置应用名称列表
        SpeechClient.getInstance().setApps(sApps);
        // 设置联系人列表
        SpeechClient.getInstance().setContacts(sContacts);
        // 设置语音命令词
        SpeechClient.getInstance().setVoiceAction(sVoiceCommands);
        // 设置VAD（静音检测）参数
        SpeechClient.getInstance().setLocalVadParams(sClientName, VadType.DNNBasedVad, 50, 500);
        // 设置位置信息，最好在每次搜索前设置以提高搜索准确度
        //SpeechClient.getInstance().setLocationString(deviceName, sLocation);
        // 设置回调函数，具体后面有介绍
        SpeechClient.getInstance().setClientListener(sClientName, new SpeechClientListenerImpl());
        // 初始化，后两个参数分别为：是否激活在线识别，是否激活离线识别
        SpeechClient.getInstance().init(this, sAppKey, true, true);

        TTSRequest ttsRequest = new TTSRequest("欢迎使用友衷语音");
        SpeechClient.getInstance().startTTS(ttsRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SpeechClient.getInstance().stopTTS();
    }

    private class SpeechClientListenerImpl implements SpeechClientListener {

        // 开始提供录音数据给语音识别引擎时回调
        public void onStartRecord() {
            Log.i(TAG,"onStartRecord->");
        }

        // 服务器端检测到静音（说话人停止说话）后回调
        public void onRemoteSilenceDetected() {
            Log.i(TAG,"onRemoteSilenceDetected->");
        }

        // 输入语音数据实时的音量回调，范围为[0, 60]
        public void onVolume(double volume) {
        }

        // 语音识别部分结果返回，比如“今天天气怎么样”，会按顺序返回“今天”，“今天天气”，“今天天气怎么样”，前两个就属于Partial Transcription
        public void onPartialTranscription(String fixedContent) {
            Log.i(TAG,"onPartialTranscription->"+fixedContent);

        }

        // 语音识别最终结果返回，比如“今天天气怎么样”，会按顺序返回“今天”，“今天天气”，“今天天气怎么样”，最后一个就是Final Transcription
        public void onFinalTranscription(final String result) {
            Log.i(TAG,"onPartialTranscription->"+result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getSearchResult();
                    Toast.makeText(MainActivity.this,"result:"+result,Toast.LENGTH_SHORT).show();
                }
            });

        }

        // 语音搜索结果返回, 为JSON格式字符串
        public void onResult(final String result) {
            Log.i(TAG,"onResult->"+result);
        }

        /**
         0 	语音服务器错误　
         1 	网络错误　　　　
         2 	无网络　　　　　
         3 	录音设备错误　　
         4 	识别内容为空　　
         5 	输入语音过长　　
         6 	起始静音时间过长
         7 	网络太慢　　　　
         * @param errorCode
         */
        public void onError(final int errorCode) {
            Log.i(TAG,"onError->"+errorCode);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getSearchResult();
                    Toast.makeText(MainActivity.this,"errorCode:"+errorCode,Toast.LENGTH_SHORT).show();
                }
            });

        }

        // 在检测到本地语音之后，又检测到本地静音时回调
        public void onLocalSilenceDetected() {
            Log.i(TAG,"onLocalSilenceDetected->");
        }

        // 一段时间未检测到本地语音时回调
        public void onNoSpeechDetected() {
            Log.i(TAG,"onNoSpeechDetected->");
        }

        // 检测到本地语音时回调
        public void onSpeechDetected() {
            Log.i(TAG,"onSpeechDetected->");
        }
    }

    /*****************************************************************/
    private void getSearchResult() {
        voice_view.changeState(VoiceView.END_SEARCH_STATE);
        //第一次改变状态是需要做动画
        if(needAnimateTop){
            needAnimateTop = false;
            animate2Top();


        }
        //setBottomText(can_i_help_you);
    }

    /**
     * voice上移动画
     */
    private void animate2Top() {
        voice_view
                .animate()
                .scaleX(voice_circle_small_size*1f/voice_circle_large_size)
                .scaleY(voice_circle_small_size*1f/voice_circle_large_size)
                .translationYBy(voice_circle_margin_small_top - voice_circle_margin_large_top)
                .setInterpolator(new AnticipateOvershootInterpolator(3))
                .setDuration(1500)
                .start();
        //bottom hint需要缩小
        rl_hint_bottom.measure(0,0);
        int measuredWidth = rl_hint_bottom.getMeasuredWidth();
        hint_bottom_content.animate().alpha(0f).setDuration(200).start();
        hint_bottom_diot_left.animate().translationX(measuredWidth/2).setDuration(300).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                doDiotScale();

            }
        }).start();
        hint_bottom_diot_right.animate().translationX(-measuredWidth/2).setDuration(300).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                doDiotScale();


            }
        }).start();

    }
    private  synchronized void doDiotScale() {
        if (!isDiotScaled){
            //在放大
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    diotScale();
                }
            });
        }

    }

    /**
     * 点的缩放
     */
    private void diotScale() {
        hint_bottom_diot_left.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hint_bottom_diot_left.animate().scaleX(1f).scaleY(1f).setInterpolator(new LinearInterpolator()).setDuration(150).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //结束缩放后需要向上弹起
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        diotAnimate2Top();
                                    }
                                });



                            }
                        }).start();



                    }


                });


            }
        }).start();
    }

    /**
     * 点向上弹起
     */
    private void diotAnimate2Top() {
        //
        rl_hint_top.measure(0,0);

        int offsetHeight = voice_bottom_margin_top - hint_top_margin_top + rl_hint_top.getMeasuredHeight()/2;

        rl_hint_bottom.animate().translationY(-offsetHeight).setDuration(300).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //结束时在进行缩放
                        rl_hint_top.setVisibility(View.VISIBLE);
                        rl_hint_bottom.setVisibility(View.INVISIBLE);
                        diotAnimate2Large();
                    }
                });

            }
        }).start();

    }
    private void diotAnimate2Large() {

        updateText();
        //重新设置文字
        hint_top_text_desc.measure(0,0);
        int measuredWidth = hint_top_text_desc.getMeasuredWidth();
        Log.i(TAG,"hint_top_text_desc:"+ measuredWidth);//180
        //目标宽度
        hint_top_text_desc.setScaleX(0f);
        hint_top_text_desc.setScaleY(0f);
        hint_top_text_desc.setAlpha(0f);
        ValueAnimator largeAnimator = ValueAnimator.ofInt(0, measuredWidth);
        largeAnimator.setDuration(300);
        largeAnimator.setInterpolator(new LinearInterpolator());
        largeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //
                if (hint_top_text_desc.getVisibility()!=View.VISIBLE){
                    hint_top_text_desc.setVisibility(View.VISIBLE);
                }
                int width = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = hint_top_text_desc.getLayoutParams();
                layoutParams.width = width;
                hint_top_text_desc.setLayoutParams(layoutParams);
                //hint_top_text_desc.requestLayout();
            }
        });

        //list
        recycler_list.measure(0,0);
        int recycler_list_width = recycler_list.getMeasuredWidth();
        Log.i(TAG,"recycler_list_width:"+recycler_list_width);
        recycler_list.setScaleX(0f);
        recycler_list.setScaleY(0f);
        recycler_list.setAlpha(0f);
        ValueAnimator recyclerAnimator = ValueAnimator.ofInt(0, recycler_list_width);
        recyclerAnimator.setDuration(300);
        recyclerAnimator.setInterpolator(new LinearInterpolator());
        recyclerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //
                if (recycler_list.getVisibility()!=View.VISIBLE){
                    recycler_list.setVisibility(View.VISIBLE);
                }
                int width = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = recycler_list.getLayoutParams();
                layoutParams.width = width;
                recycler_list.setLayoutParams(layoutParams);
                //hint_top_text_desc.requestLayout();
            }
        });
        recyclerAnimator.start();
        largeAnimator.start();
        hint_top_text_desc.animate().alpha(1f).scaleX(1f).scaleY(1f).setInterpolator(new LinearInterpolator()).setDuration(200).setStartDelay(100).start();
        recycler_list.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(200).setStartDelay(100).setInterpolator(new LinearInterpolator()).start();

    }

    ArrayList<String> searchResults = new ArrayList<>();
    /**
     * 重新设置文字内容
     */
    @TargetApi(19)
    private void updateText() {
        if (searchResultAdapter==null){
            recycler_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            searchResultAdapter = new SearchResultAdapter(this, updateSearchResult());
            recycler_list.setAdapter(searchResultAdapter);
        }else{
            searchResultAdapter.setSearchResults(updateSearchResult());
        }

    }

    private ArrayList<String> updateSearchResult() {
        searchResults.clear();
        searchResults.add("音乐， 歌曲名称");
        searchResults.add("收音机， 调频");
        searchResults.add("电话， 名字");

        return searchResults;
    }

    /**
     * 需要执行动画效果
     *
     * @param desText 文字
     */
    @TargetApi(19)
    private void setBottomText(String desText) {

        String srcText = hint_bottom_text_des.getText().toString();
        hint_bottom_text_src.setText(srcText);
        hint_bottom_text_src.setAlpha(1f);
        hint_bottom_text_src.measure(0,0);
        final int srcWidth = hint_bottom_text_src.getMeasuredWidth();
        hint_bottom_text_src.setVisibility(View.VISIBLE);//可见==VISIBBLE
        hint_bottom_text_des.setAlpha(0f);//不可见==INVISIBLE

        hint_bottom_text_des.setText(desText);
        hint_bottom_text_des.measure(0,0);
        final int desWidth = hint_bottom_text_des.getMeasuredWidth();
        //已知原宽度和目的宽度，差值就是需要做的动画
        if (widthAnimator==null){
            widthAnimator = ValueAnimator.ofInt(srcWidth, desWidth);
            widthAnimator.setDuration(hint_duration/2);//动画时间暂定为1s
            widthAnimator.setRepeatCount(0);
            widthAnimator.setInterpolator(new LinearInterpolator());
            widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int width = (int) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = hint_bottom_content.getLayoutParams();
                    layoutParams.width = width;
                    hint_bottom_content.setLayoutParams(layoutParams);
                }
            });

        }else{
            widthAnimator.setIntValues(srcWidth,desWidth);
        }

        if (widthAnimator!=null&&widthAnimator.isRunning()){
            widthAnimator.cancel();
        }
        hint_bottom_text_src.animate().cancel();
        hint_bottom_text_des.animate().cancel();
        widthAnimator.removeAllListeners();
        if (srcWidth <=desWidth){

            widthAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (srcWidth<=desWidth){
                        hint_bottom_text_des.setScaleX(0f);
                        hint_bottom_text_des.setScaleY(0f);
                        hint_bottom_text_src.animate().alpha(0f).setDuration(hint_duration/2).start();
                        hint_bottom_text_des.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(hint_duration/2).start();
                    }

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            Log.i(TAG,"widthAnimator.start()");
            widthAnimator.start();
        }else{
            hint_bottom_text_des.setScaleX(0f);
            hint_bottom_text_des.setScaleY(0f);
            hint_bottom_text_src.animate().alpha(0f).setDuration(hint_duration/2).start();
            hint_bottom_text_des.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(hint_duration/2).withEndAction(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (srcWidth>desWidth){
                                widthAnimator.start();
                            }
                        }
                    });
                }
            }).start();

        }


        //


    }
}
