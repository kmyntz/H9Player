package com.h9studio.h9player.player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.h9studio.h9player.R;
import com.h9studio.h9player.utils.*;

import java.io.IOException;

public class H9PlayerMainActivity extends Activity implements SurfaceHolder.Callback {
    private static final int DELAY_SEEKBAR_UPDATE = 300;
    private static final int DELAY_CONTROL_BAR_HIDE = 3000;
    private static final int DELAY_CONTROL_BAR_HIDE_TOUCH_INTERVAL = 1000;
    private static final int MSG_WHAT_SEEKBAR_CHANGED = 0;
    private static final int MSG_WHAT_CONTROL_BAR_HIDE = 1;
    private Uri mUri;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private ImageButton mPlayPauseButton;
    private SeekBar mSeekBar;
    private TextView mCurrentTimeTextView;
    private TextView mTotalTimeTextView;
    private ViewGroup mControllerLayout;
    private SurfaceHolder mSurfaceHolder;
    private long mLastControllerVisibleTime = -1;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mUri = getIntent().getData();
        setContentView(R.layout.activity_player_main);
        viewInit();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void viewInit() {
        mSurfaceView = findViewById(R.id.surface);
        mPlayPauseButton = findViewById(R.id.playpause_btn);
        mSeekBar = findViewById(R.id.seekbar);
        mCurrentTimeTextView = findViewById(R.id.current_time_text);
        mTotalTimeTextView = findViewById(R.id.total_time_text);
        mControllerLayout = findViewById(R.id.controller_layout);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        int op = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        mSurfaceView.setSystemUiVisibility(op);

        mSurfaceView.setOnClickListener(mClickListener);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(mSystemUiVisibilityChangeListener);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mPlayPauseButton.setOnClickListener(mClickListener);

    }

    private void mediaPlayerInit() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(mOnPrepareListener);
        mMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer.setSurface(mSurfaceView.getHolder().getSurface());
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), mUri);
        } catch (IOException e) {
            VLog.d(e.getMessage());
        }
        mMediaPlayer.prepareAsync();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
//        int option =  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        getWindow().getDecorView().setSystemUiVisibility(option);
    }

    private MediaPlayer.OnPreparedListener mOnPrepareListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            VLog.d("onPrepared");
            int duration = mMediaPlayer.getDuration();
            int curPosition = mMediaPlayer.getCurrentPosition();
            mTotalTimeTextView.setText(H9Utils.changeTimeToText(duration));
            mCurrentTimeTextView.setText(H9Utils.changeTimeToText(curPosition));
            mSeekBar.setMax(duration);
            mp.start();
            mHandler.sendEmptyMessage(MSG_WHAT_SEEKBAR_CHANGED);
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        VLog.d("surfaceCreated");
        mediaPlayerInit();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        VLog.d("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        VLog.d("surfaceDestroyed");
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playpause_btn:
                    if (mMediaPlayer != null) {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                            mHandler.removeMessages(MSG_WHAT_SEEKBAR_CHANGED);
                        } else {
                            mMediaPlayer.start();
                            mHandler.sendEmptyMessage(MSG_WHAT_SEEKBAR_CHANGED);
                        }
                    }
                    break;
                case R.id.surface:
                    if (mControllerLayout.getVisibility() == View.INVISIBLE) {
                    } else {
                        int delay = 0;
                        if (mLastControllerVisibleTime > 0) {
                            long currentTime = System.currentTimeMillis();
                            int diff = ((int)(currentTime - mLastControllerVisibleTime));
                            delay = DELAY_CONTROL_BAR_HIDE_TOUCH_INTERVAL - diff;
                        }
                        VLog.d("delay:"+delay);
                        mHandler.removeMessages(MSG_WHAT_CONTROL_BAR_HIDE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_CONTROL_BAR_HIDE, delay);
                    }

                    break;
                default:
                    break;
            }

        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_SEEKBAR_CHANGED:
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mSeekBar != null) {
                        int curPosition = mMediaPlayer.getCurrentPosition();
                        mCurrentTimeTextView.setText(H9Utils.changeTimeToText(curPosition));
                        mSeekBar.setProgress(curPosition);
                    }
                    if (!mHandler.hasMessages(MSG_WHAT_SEEKBAR_CHANGED)) {
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_SEEKBAR_CHANGED, DELAY_SEEKBAR_UPDATE);
                    }
                    break;
                case MSG_WHAT_CONTROL_BAR_HIDE:
                    setFullScreen();
                    break;

                default:
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mMediaPlayer.seekTo(progress);
                mCurrentTimeTextView.setText(H9Utils.changeTimeToText(progress));
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(MSG_WHAT_SEEKBAR_CHANGED);
            mHandler.removeMessages(MSG_WHAT_CONTROL_BAR_HIDE);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mHandler.sendEmptyMessage(MSG_WHAT_SEEKBAR_CHANGED);
            }
        }
    };

    private void setFullScreen() {
        int option =  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(option);
    }

    private View.OnSystemUiVisibilityChangeListener mSystemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            VLog.d("visibility:"+visibility);
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                mLastControllerVisibleTime = System.currentTimeMillis();
                VLog.d("show controller");
                mControllerLayout.setVisibility(View.VISIBLE);
            } else {
                VLog.d("hide controller");
                mControllerLayout.setVisibility(View.INVISIBLE);
            }
        }
    };

}
