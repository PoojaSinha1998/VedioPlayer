package application.pooja.com.vedioplayer.activities;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import application.pooja.com.vedioplayer.R;
import application.pooja.com.vedioplayer.adapter.myViewAdapter;
import application.pooja.com.vedioplayer.application.myapplication;
import application.pooja.com.vedioplayer.database.DBHelper;
import application.pooja.com.vedioplayer.model.Example;

public class ViewVedioActivity extends AppCompatActivity implements myViewAdapter.ItemClickListener {


    private SimpleExoPlayer mSimpleExoPlayer;

    private SimpleExoPlayerView mSimpleExoPlayerView;

    private LoopingMediaSource mLoopingMediaSource;
    private Handler mMainHandler;
    private AdaptiveTrackSelection.Factory mAdaptiveTrackSelectionFactory;
    private TrackSelector mTrackSelector;
    private LoadControl mLoadControl;
    private DefaultBandwidthMeter mBandwidthMeter;
    private ProgressBar mProgressBar;
    FirebaseAuth mAuth;
    RequestQueue requestQueue;
    TextView mTitle, mDes;
    ArrayList<Example> posts;
    myViewAdapter mAdapter;
    RecyclerView recyclerView;
    String id;
    DBHelper dbHelper;
    int i;
    private boolean mPlayVideoWhenForegrounded;
    private long mLastPosition, mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vedio);
        dbHelper = new DBHelper(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recyclerView = findViewById(R.id.recyclerView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("PLAYVEDIOID");
            Log.d("VT", "id of vedio In ViewVedioActivity: " + id);
        }
        mTitle = findViewById(R.id.viewTitle);
        mDes = findViewById(R.id.viewDis);
        setAdapterOnRecyclerView();

        mSimpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.videoPlayer);
        mProgressBar = (ProgressBar) findViewById(R.id.amPrgbrLoading);
        posts = myapplication.getExamples();
        mBandwidthMeter = new DefaultBandwidthMeter();
        mAdaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(mBandwidthMeter);
        mTrackSelector = new DefaultTrackSelector(mAdaptiveTrackSelectionFactory);

        mLoadControl = new DefaultLoadControl();

        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, mTrackSelector, mLoadControl);
        playMedia(id);
    }

    private void setAdapterOnRecyclerView() {
        mAdapter = new myViewAdapter(myapplication.getExamples(), this, id);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter.setClickListener(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        recyclerView.setAdapter(mAdapter);
    }

    /*
     * this method will play audio and video with hls streaming.
     */
    private void playMedia(String mid) {
        id = mid;
        setAdapterOnRecyclerView();
        int mId = Integer.parseInt(id) - 1;
        String url = posts.get(mId).getUrl();
        Log.d("VT", "url of vedio In ViewVedioActivity: " + posts.get(mId).getTitle());

        mTitle.setText(posts.get(mId).getTitle());
        mDes.setText(posts.get(mId).getDescription());
        mSimpleExoPlayerView.setPlayer(mSimpleExoPlayer);

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(ViewVedioActivity.this,
                Util.getUserAgent(ViewVedioActivity.this, "com.exoplayerdemo"), bandwidthMeter);

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(url),
                dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        mLoopingMediaSource = new LoopingMediaSource(videoSource);

        mSimpleExoPlayer.prepare(videoSource);

        mSimpleExoPlayer.setPlayWhenReady(true);
        Log.d("VT","vedio started "+id);
        String seekTime = dbHelper.fetchSeekTime(id);
        String totalDuration = dbHelper.fetchTotalDuration(id);
        Log.d("VT","value of seek : "+seekTime);
        if(seekTime== null)
        {

            //   mProgressBar.setVisibility(View.VISIBLE);
            // mSimpleExoPlayer.setPlayWhenReady(mPlayVideoWhenForegrounded);
        }
        if(seekTime!=null) {
            int seek = Integer.parseInt(seekTime);
            int total = Integer.parseInt(totalDuration);
            // Seek to the last position of the player.
            mSimpleExoPlayer.seekTo(seek);

            // Put the player into the last state we were in.
            mSimpleExoPlayer.setPlayWhenReady(mPlayVideoWhenForegrounded);
        }

        mSimpleExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.d("VT","onTimelineChanged");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d("VT","onTracksChanged");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d("VT","onLoadingChanged");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch(playbackState) {
                    case ExoPlayer.STATE_BUFFERING:

                        mProgressBar.setVisibility(View.VISIBLE);

                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d("VT","vedio off "+id);
                        mSimpleExoPlayer.seekTo(0);
                        i = Integer.parseInt(id);
                        i++;
                        if(i<14) {
                            playMedia(String.valueOf(i));
                        }
                        else if(i==14)
                        {
                            i=1;
                            id = String.valueOf(i);
                            playMedia(id);
                        }
                        break;
                    case ExoPlayer.STATE_IDLE:
                        break;
                    case ExoPlayer.STATE_READY:
//                        Log.d("VT","vedio started "+id);
//                        String seekTime = dbHelper.fetchSeekTime(id);
//                        String totalDuration = dbHelper.fetchTotalDuration(id);
//                        Log.d("VT","value of seek : "+seekTime);
//                        if(seekTime== null)
//                        {
//
//                         //   mProgressBar.setVisibility(View.VISIBLE);
//                            // mSimpleExoPlayer.setPlayWhenReady(mPlayVideoWhenForegrounded);
//                        }
//                        if(seekTime!=null) {
//                            int seek = Integer.parseInt(seekTime);
//                            int total = Integer.parseInt(totalDuration);
//                            // Seek to the last position of the player.
//                            mSimpleExoPlayer.seekTo(seek);
//
//                            // Put the player into the last state we were in.
//                            mSimpleExoPlayer.setPlayWhenReady(mPlayVideoWhenForegrounded);
//                        }

                        Log.d("VT","vedio ready "+id);
                        mProgressBar.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }

                Log.d("VT","onPlayerStateChanged");
               /* if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }*/

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(ViewVedioActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPositionDiscontinuity() {
               /* int latestWindowIndex = mSimpleExoPlayer.getCurrentWindowIndex();
                int lastWindowIndex = 12;
                Log.d("VT","Current time"+mSimpleExoPlayer.getCurrentTimeline());
                Log.d("VT","Current Video Duration of id : "+mSimpleExoPlayer.getDuration()+ ": "+mSimpleExoPlayer.getCurrentTrackSelections());
                if (latestWindowIndex != lastWindowIndex) {
                    // item selected in playlist has changed, handle here
                    i = Integer.parseInt(id);
                    i++;
                    playMedia(String.valueOf(i));

                    // ...
                }
                else if(latestWindowIndex == lastWindowIndex)
                {
                    id = "1";
                    i = Integer.parseInt(id);
                    playMedia(String.valueOf(i));
                }*/
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMedia();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMedia();
    }

    private void stopMedia() {
//        mSimpleExoPlayer.stop();
        // Store off if we were playing so we know if we should start when we're foregrounded again.
        mPlayVideoWhenForegrounded = mSimpleExoPlayer.getPlayWhenReady();

        // Store off the last position our player was in before we paused it.
            mLastPosition = mSimpleExoPlayer.getCurrentPosition();
            mDuration = mSimpleExoPlayer.getDuration();

            Log.d("VT","StopMedia  last position: "+mLastPosition+" totla duration : "+mSimpleExoPlayer.getDuration()+" id  :"+id);

            if(mLastPosition < mDuration)
            {
              boolean added =  dbHelper.insertVideoStatus(id,mDuration,mLastPosition);
              if(added)
              {
                  Log.d("VT","One record added");
              }
              else
              {
                  Log.d("VT","no record added record is already added, so that is updated");
              }
            }
            else
            {
                Log.d("VT","Dont add any record, ");
            }


        // Pause the player
        mSimpleExoPlayer.setPlayWhenReady(false);
        mSimpleExoPlayer.release();
    }

    @Override
    public void onItemClick(View view, String id) {
        this.id = id;
        playMedia(id);
    }
}
