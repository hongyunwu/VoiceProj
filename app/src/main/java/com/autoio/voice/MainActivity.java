package com.autoio.voice;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autoio.voice_view.VoiceView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


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
        voice_view.setOnPressedListener(new VoiceView.OnPressedListener() {
            @Override
            public void onVoicePressed() {
                //按下时,
                setBottomText(i_am_listening);
                //hint_bottom_text.setText(i_am_listening);
            }

            @Override
            public void onVoiceUnPressed() {
                //抬起
                setBottomText(searching);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getSearchResult();
                            }
                        });
                    }
                }).start();
            }
        });
    }

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
                Log.i(TAG,"onAnimationUpdate->"+width);
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
                Log.i(TAG,"onAnimationUpdate->"+width);
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
                        Log.i(TAG,"onAnimationEnd...-animate");
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
                                Log.i(TAG,"withEndAction widthAnimator.start()");
                            }
                        }
                    });
                }
            }).start();

            Log.i(TAG,"hint_bottom_text_des.animate");
        }


        //


    }
}
