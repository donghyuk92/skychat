package itm.capstone.skychat;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by User on 2016-05-26.
 */
public class Fragment_Event extends Fragment {

    private Context ctx;

    public static Fragment_Event newInstance(Context ctx) {
        Fragment_Event f = new Fragment_Event();
        f.ctx = ctx;
        return f;
    }
}
