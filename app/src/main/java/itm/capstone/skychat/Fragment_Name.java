package itm.capstone.skychat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import info.androidhive.webgroupchat.R;

/**
 * Created by User on 2016-08-28.
 */
public class Fragment_Name extends Fragment {

    private Button btnJoin;
    private EditText txtName;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_name, container, false);

        btnJoin = (Button) view.findViewById(R.id.btnJoin);
        txtName = (EditText) view.findViewById(R.id.name);

        // Hiding the action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        btnJoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().trim().length() > 0) {
                    Activity_Main.name = txtName.getText().toString().trim();
                    ((TextView) getActivity().findViewById(R.id.userName)).setText(txtName.getText().toString().trim());
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment fragment = null;
                    fragment = (Fragment) Fragment_Chat.newInstance(getContext(), "default");
                    ft.replace(R.id.mainContent, fragment).commit();
                } else {
                    Toast.makeText(getContext(),
                            "Please enter your name", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public static Fragment newInstance(Context ctx) {
        Fragment_Name f = new Fragment_Name();
        return f;
    }
}