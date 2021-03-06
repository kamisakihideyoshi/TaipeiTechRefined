package owo.npc.taipeitechrefined.course.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import owo.npc.taipeitechrefined.R;
import owo.npc.taipeitechrefined.course.CourseFragment;
import owo.npc.taipeitechrefined.model.Model;
import owo.npc.taipeitechrefined.utility.Constants;
import owo.npc.taipeitechrefined.utility.CourseConnector;
import owo.npc.taipeitechrefined.utility.NportalConnector;

import java.lang.ref.WeakReference;

/**
 * Created by Alan on 2015/9/13.
 */
public class SearchCourseTask extends AsyncTask<String, Void, Object> {
    private WeakReference<CourseFragment> mCourseFragmentWeakReference;
    private WeakReference<ProgressDialog> mProgressDialogWeakReference;

    public SearchCourseTask(CourseFragment fragment) {
        mCourseFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null) {
            ProgressDialog progressDialog = ProgressDialog.show(fragment.getContext(), null, fragment.getContext().getString(R.string.course_load_course));
            mProgressDialogWeakReference = new WeakReference<>(progressDialog);
        } else {
            cancel(true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        int retryCount = 0;
        Object result;
        do {
            try {
                if (!NportalConnector.isLogin()) {
                    String account = Model.getInstance().getAccount();
                    String password = Model.getInstance().getPassword();
                    NportalConnector.login(account, password);
                }
                if (!CourseConnector.isLogin()) {
                    CourseConnector.loginCourse();
                }
                result = CourseConnector.getStudentCourse(params[0], params[1],
                        params[2]);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                retryCount++;
            }
        } while (retryCount <= Constants.RETRY_MAX_COUNT_INT);
        return result;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        ProgressDialog progressDialog = mProgressDialogWeakReference.get();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null) {
            fragment.obtainStudentCourse(object);
        }
    }
}
