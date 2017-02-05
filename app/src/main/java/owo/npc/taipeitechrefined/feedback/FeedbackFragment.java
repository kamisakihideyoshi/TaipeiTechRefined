package owo.npc.taipeitechrefined.feedback;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
    private static final String NO_MESSAGE = "NO_MESSAGE";
    private static final String NO_CONTACT_INFORMATION = "NO_CONTACT_INFORMATION";
    private static final String OK = "OK";


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
                    switch (setMessage()) {
                        case NO_MESSAGE:
                            Toast.makeText(getActivity(), R.string.blank_forbid,
                                    Toast.LENGTH_LONG).show();
                            return;
                        case NO_CONTACT_INFORMATION:
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setMessage(R.string.feedback_check);
                            alertDialog.setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    writeDataBase(getMessage());
                                    initFeedback();
                                    Utility.showDialog(getString(R.string.send_suceed), getString(R.string.feedback_finish), getActivity());
                                }
                            });
                            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            alertDialog.show();
                            break;
                        case OK:
                            writeDataBase(getMessage());
                            initFeedback();
                            Utility.showDialog(getString(R.string.send_suceed), getString(R.string.feedback_finish), getActivity());
                            break;
                    }
                }
                else{
                    Toast.makeText(getActivity(), R.string.check_network_available,
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void initFeedback() {
        edt_feedback.setText("");
        feedback = null;
        contact_imformation = null;
        edt_contact_imformation.setText("");
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

    private String setMessage() {
        feedback = String.valueOf(edt_feedback.getText());
        contact_imformation = String.valueOf(edt_contact_imformation.getText());
        if(feedback.isEmpty()){
            return NO_MESSAGE;
        }
        else if (contact_imformation.isEmpty()){
            return NO_CONTACT_INFORMATION;
        }
        else {
            return OK;
        }
    }

    private String getMessage(){
        return (feedback+"，"+contact_imformation);
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
