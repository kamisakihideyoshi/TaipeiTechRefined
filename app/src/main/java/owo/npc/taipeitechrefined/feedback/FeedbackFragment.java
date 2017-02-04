package owo.npc.taipeitechrefined.feedback;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import owo.npc.taipeitechrefined.runnable.BaseRunnable;
import owo.npc.taipeitechrefined.BaseFragment;
import owo.npc.taipeitechrefined.R;
import owo.npc.taipeitechrefined.utility.Utility;
import owo.npc.taipeitechrefined.utility.WifiUtility;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class FeedbackFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener {
    private static View fragmentView = null;
    FloatingActionButton mFab = null;
    EditText edt_feedback;
    EditText edt_contact_imformation;
    String feedback, contact_imformation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_feedback, container,
                false);
        edt_feedback = (EditText) fragmentView.findViewById(R.id.feedback);
        edt_contact_imformation = (EditText) fragmentView.findViewById(R.id.contact_imformatoin);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initFab();
        super.onActivityCreated(savedInstanceState);
    }

    private void initFab(){
        mFab = (android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(WifiUtility.isNetworkAvailable(getActivity())){
                    setMessage();
                    writeDataBase(getMessage());
                    Utility.showDialog("傳送成功", "感謝您的提問與建議，我們將盡快解決您的問題", getActivity());
                }
                else{
                    Toast.makeText(getActivity(), R.string.check_network_available,
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Override
    public int getTitleColorId() {
        return R.color.blue;
    }

    @Override
    public int getTitleStringId() {
        return R.string.feedback_text;
    }



    static class FeedbackHandler extends Handler {
        private WeakReference<FeedbackFragment> fragmentRef;

        public FeedbackHandler(FeedbackFragment fragment) {
            fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.ERROR:
                    FeedbackFragment fragment = fragmentRef.get();
                    if (fragment != null) {
                        Utility.showDialog("提示", (String) msg.obj,
                                fragment.getActivity());
                    }
                    break;
            }
        }
    }

    private void setMessage(){
        feedback = String.valueOf(edt_feedback.getText());
        contact_imformation = String.valueOf(edt_contact_imformation.getText());
    }

    private String getMessage(){
        return (feedback+contact_imformation);
    }

    private void writeDataBase(String message){
        // Write a message to the database
        FirebaseApp.initializeApp(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Calendar mCal = Calendar.getInstance();
        CharSequence s = DateFormat.format("yyyy-MM-dd kk:mm:ss", mCal.getTime());
        DatabaseReference myRef = database.getReference((String) s);
        myRef.setValue(message);
    }


    @Override
    public void onRefresh() {

    }
}
