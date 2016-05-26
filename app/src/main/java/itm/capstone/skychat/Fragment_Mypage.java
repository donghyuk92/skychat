package itm.capstone.skychat;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by User on 2016-05-26.
 */
public class Fragment_Mypage extends Fragment {

    private Context ctx;

    public static Fragment_Mypage newInstance(Context ctx) {
        Fragment_Mypage f = new Fragment_Mypage();
        f.ctx = ctx;
        return f;
    }
}
