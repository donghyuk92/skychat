package itm.capstone.skychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;

/**
 * Created by User on 2016-05-26.
 */
public class Dialog_ChangeCh extends DialogFragment {

    private Context ctx;

    Button Yes;
    Button No;

    public static Dialog_ChangeCh newInstance(Context ctx) {
        Dialog_ChangeCh f = new Dialog_ChangeCh();
        f.ctx = ctx;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = new LinearLayout(ctx);
        Yes = new YesButton(ctx);
        ll.addView(Yes,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        No = new NoButton(ctx);
        ll.addView(No,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        return ll;
    }

    class YesButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent()
                        .putExtra("changech","true");
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
            }
        };

        public YesButton(Context ctx) {
            super(ctx);
            setText("Yes");
            setOnClickListener(clicker);
        }
    }

    class NoButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent()
                        .putExtra("changech","false");
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
            }
        };

        public NoButton(Context ctx) {
            super(ctx);
            setText("No");
            setOnClickListener(clicker);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
