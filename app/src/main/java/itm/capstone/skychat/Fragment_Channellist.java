package itm.capstone.skychat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import info.androidhive.webgroupchat.R;
import itm.capstone.skychat.other.WsConfig;

/**
 * Created by User on 2016-05-28.
 */
public class Fragment_Channellist extends Fragment {

    Context ctx;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_CHANNELID = "channel_id";
    private static final String TAG_CHANNELNAME = "channel_name";
    private static final String TAG_PROGRAMNAME = "program_name";
    private static final String TAG_PROGRAMCATEGORY = "category";
    private static final String TAG_PROGRAMSTIME = "stime";
    private static final String TAG_PROGRAMETIME = "etime";
    private static final String TAG_PROGRAMCAST = "cast";
    private static final String TAG_PROGRAMSUMMARY = "summary";

    JSONArray chat_data = null;
    ListView listview;
    ListViewAdapter Adapter;
    //ArrayList<ChattingRoom> chatroomlist;

    public static Fragment_Channellist newInstance(Context ctx) {
        Fragment_Channellist f = new Fragment_Channellist();
        f.ctx = ctx;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        get_Data("http://" + WsConfig.IP + "/ChatroomList.php");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channellist, container, false);
        //ArrayAdapter Adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
        listview = (ListView) view.findViewById(R.id.chatlist);
        //chatroomlist = new ArrayList<ChattingRoom>();
        Adapter = new ListViewAdapter();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChattingRoom room = (ChattingRoom) Adapter.getItem(position);
                String ch_id = room.getChannel_id();

                Fragment fragment = Fragment_Chat.newInstance(ctx, ch_id);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.mainContent, fragment);
                fragmentTransaction.commit();

                SetCurCh setcurch = new SetCurCh();
                Log.d("TAG", ch_id);
                setcurch.execute(ch_id);

            }
        });

        return view;
    }

    protected void makeChatroom(String json) {
        try {
            JSONArray jarray = new JSONArray(json);

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject c = jarray.getJSONObject(i);
                String c_id = c.getString(TAG_CHANNELID);
                String c_name = c.getString(TAG_CHANNELNAME);
                String p_name = c.getString(TAG_PROGRAMNAME);
                String p_cate = c.getString(TAG_PROGRAMCATEGORY);
                String p_stime = c.getString(TAG_PROGRAMSTIME);
                String p_etime = c.getString(TAG_PROGRAMETIME);
                String p_cast = c.getString(TAG_PROGRAMCAST);
                String p_summary = c.getString(TAG_PROGRAMSUMMARY);
                Adapter.addItem(c_id, c_name, p_name, p_cate, p_stime, p_etime, p_cast, p_summary);
            }

            listview.setAdapter(Adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void get_Data(String url) {
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<ChattingRoom> listViewItemList = new ArrayList<ChattingRoom>();

        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return listViewItemList.size();
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.chatroom_item, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView c_name = (TextView) convertView.findViewById(R.id.c_name);
            TextView p_name = (TextView) convertView.findViewById(R.id.p_name);

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            ChattingRoom chatroom = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            //iconImageView.setImageDrawable(listViewItem.getIcon());
            c_name.setText(chatroom.getChannel_name());
            p_name.setText(chatroom.getProgram_name());

            return convertView;
        }

        public String getChannelId(int position) {
            String c_id = listViewItemList.get(position).getChannel_id();
            return c_id;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position);
        }

        // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
        public void addItem(String channel_id, String channel_name, String program_name, String program_category, String program_stime, String program_etime, String program_cast, String program_summary) {
            ChattingRoom item = new ChattingRoom(channel_id, channel_name, program_name, program_category, program_stime, program_etime, program_cast, program_summary);

            listViewItemList.add(item);
        }
    }

    class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String uri = params[0];

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            makeChatroom(result);
        }
    }

    class SetCurCh extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String ch_id = params[0];

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL("http://" + WsConfig.IP + "/UpdateCh.php?ch_id=" + ch_id);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            makeChatroom(result);
        }
    }
}
