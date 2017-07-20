package owo.npc.taipeitechrefined.course.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import owo.npc.taipeitechrefined.R;
import owo.npc.taipeitechrefined.course.CourseFragment;
import owo.npc.taipeitechrefined.model.Model;
import owo.npc.taipeitechrefined.utility.NportalConnector;

import java.lang.ref.WeakReference;

/**
 * Created by Alan on 2015/9/13.
 */
public class CourseDetailTask extends AsyncTask<String, Void, Object> {
    private WeakReference<CourseFragment> mCourseFragmentWeakReference;
    private WeakReference<ProgressDialog> mProgressDialogWeakReference;

    public CourseDetailTask(CourseFragment fragment) {
        mCourseFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null && !NportalConnector.isLogin()) {
            ProgressDialog progressDialog = ProgressDialog.show(fragment.getContext(), null, "學期清單查詢中…");
            mProgressDialogWeakReference = new WeakReference<>(progressDialog);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        Object result;
        try {
            if (!NportalConnector.isLogin()) {
                String account = Model.getInstance().getAccount();
                String password = Model.getInstance().getPassword();
                NportalConnector.login(account, password);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        if (mProgressDialogWeakReference != null) {
            ProgressDialog progressDialog = mProgressDialogWeakReference.get();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null) {
            fragment.startCourseDetail(object);
        }
    }
}
