package itm.capstone.skychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import info.androidhive.webgroupchat.R;

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
        View view = inflater.inflate(R.layout.dialog_changech, container, false);
        Yes = (Button) view.findViewById(R.id.changeYes);
        No = (Button) view.findViewById(R.id.changeNo);
        Yes.setOnClickListener(clickYes);
        No.setOnClickListener(clickNo);
        return view;
    }

    View.OnClickListener clickYes = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent()
                    .putExtra("changech", "true");
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    };

    View.OnClickListener clickNo = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent()
                    .putExtra("changech", "false");
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
    }
}
