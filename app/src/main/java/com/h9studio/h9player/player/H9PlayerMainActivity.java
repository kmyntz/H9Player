package com.h9studio.h9player.player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

public class H9PlayerMainActivity extends Activity implements SurfaceHolder.Callback{
    private int mUiOption;
    private Uri mUri;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private ImageButton mPlayPauseButton;
    private SeekBar mSeekBar;
    private TextView mCurrentTimeTextView;
    private TextView mTotalTimeTextView;
    private ViewGroup mControllerLayout;
    private SurfaceHolder mSurfaceHolder;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mUiOption  |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        mUiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        mUiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        mUri = getIntent().getData();
        setContentView(R.layout.activity_player_main);
        viewInit();
    }
    @Override
    public void onStart() {
        super.onStart();
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
    }

    private void mediaPlayerInit() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(mOnPrepareListener);
        mMediaPlayer.setSurface(mSurfaceView.getHolder().getSurface());
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), mUri);
        } catch (IOException e) {
            VLog.d(e.getMessage());
        }
        mMediaPlayer.prepareAsync();

    }

    private void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(mUiOption);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            setFullScreen();
        }
    }

    private MediaPlayer.OnPreparedListener mOnPrepareListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            VLog.d("onPrepared");
            int duration = mMediaPlayer.getDuration();
            mTotalTimeTextView.setText(H9Utils.changeTimeToText(duration));
            mp.start();


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
}
