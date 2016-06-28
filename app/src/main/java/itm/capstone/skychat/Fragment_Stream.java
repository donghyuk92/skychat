package itm.capstone.skychat;

/**
 * Created by User on 2016-05-29.
 */

import java.io.IOException;
import java.util.HashSet;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import info.androidhive.webgroupchat.R;

public class Fragment_Stream extends Fragment {

    private Context ctx;

    private MediaPlayer mPlayer;
    private Button buttonPlay;
    private Button buttonStop;

    public static Fragment_Stream newInstance(Context ctx) {
        Fragment_Stream f = new Fragment_Stream();
        f.ctx = ctx;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container, false);

        buttonPlay = (Button) view.findViewById(R.id.play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Uri myUri1 = Uri.parse("http://117.17.187.85/classic.mp3");
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mPlayer.setDataSource(ctx, myUri1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    public void onPrepared(MediaPlayer player) {
                        player.start();
                    }

                });
                mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mPlayer.reset();
                        return false;
                    }
                });
                mPlayer.prepareAsync();
            }
        });

        buttonStop = (Button) view.findViewById(R.id.stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
            }
        });

        return view;
    }

}
