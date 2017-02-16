package owo.npc.taipeitechrefined.course;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import owo.npc.taipeitechrefined.course.task.CourseDetailTask;
import owo.npc.taipeitechrefined.course.task.QuerySemesterTask;
import owo.npc.taipeitechrefined.model.Model;
import owo.npc.taipeitechrefined.utility.Constants;
import owo.npc.taipeitechrefined.utility.WifiUtility;
import owo.npc.taipeitechrefined.BaseFragment;
import owo.npc.taipeitechrefined.R;
import owo.npc.taipeitechrefined.course.data.Semester;
import owo.npc.taipeitechrefined.course.task.SearchCourseTask;
import owo.npc.taipeitechrefined.model.CourseInfo;
import owo.npc.taipeitechrefined.model.StudentCourse;
import owo.npc.taipeitechrefined.utility.Utility;

import java.util.ArrayList;
import java.util.Locale;

public class CourseFragment extends BaseFragment implements OnClickListener,
        CourseTableLayout.TableInitializeListener {
    private static String[] TIME_ARRAY;
    private ArrayList<Semester> semesters = new ArrayList<>();
    private CourseTableLayout courseTable;
    private EditText sidText;
    private String sid = "";
    private String lastSid = "";
    private Semester mSemester;
    private SemesterSelector mSemesterSelector;
    public String selectedCourseNo = "";
    private static View fragmentView;
    private boolean needShowSemesterDialog = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    sid = data.getStringExtra("sid");
                    sidText.setText(sid);
                    lockSemesterSpinner();
                    searchCourseTable(sid, mSemester);
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TIME_ARRAY = getResources().getStringArray(R.array.time_array);
        fragmentView = inflater.inflate(R.layout.fragment_course, container,
                false);
        fragmentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                closeSoftKeyboard();
            }
        });
        initCourseTable();
        mSemesterSelector = (SemesterSelector) fragmentView.findViewById(R.id.semester);
        mSemesterSelector.setOnSemesterSelectedListener(semesterSelectedLis);
        lockSemesterSpinner();
        sidText = (EditText) fragmentView.findViewById(R.id.sidText);
        sidText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals(lastSid)) {
                    lockSemesterSpinner();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sidText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String sid = sidText.getText().toString();
                    needShowSemesterDialog = false;
                    searchLatestCourseTable(sid);
                    return true;
                }
                return false;
            }
        });
        if (Model.getInstance().getStudentCourse() != null) {
            StudentCourse student_course = Model.getInstance()
                    .getStudentCourse();
            sid = student_course.getSid();
            mSemester = new Semester(student_course.getYear(), student_course.getSemester());
            semesters.add(mSemester);
            sidText.setText(sid);
        }
        return fragmentView;
    }

    private void searchLatestCourseTable(String sid) {
        if (!TextUtils.isEmpty(sid)) {
            if (WifiUtility.isNetworkAvailable(getActivity())) {
                closeSoftKeyboard();
                if(Utility.checkAccount(getActivity())) {
                    lastSid = sid;
                    QuerySemesterTask querySemesterTask = new QuerySemesterTask(this);
                    querySemesterTask.execute(sid);
                }
            } else {
                Toast.makeText(getActivity(), R.string.check_network_available,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.course_no_studentid_typed, Toast.LENGTH_LONG).show();
        }
    }

    private void searchCourseTable(String sid, Semester semester) {
        if (!TextUtils.isEmpty(sid)) {
            if (WifiUtility.isNetworkAvailable(getActivity())) {
                closeSoftKeyboard();
                if(Utility.checkAccount(getActivity())) {
                    SearchCourseTask searchCourseTask = new SearchCourseTask(this);
                    searchCourseTask.execute(sid, semester.getYear(), semester.getSemester());
                }
            } else {
                Toast.makeText(getActivity(), R.string.check_network_available,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.course_no_studentid_typed, Toast.LENGTH_LONG).show();
        }
    }

    private void lockSemesterSpinner() {
        mSemesterSelector.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    String sid = sidText.getText().toString();
                    needShowSemesterDialog = true;
                    searchLatestCourseTable(sid);
                }
                return true;
            }
        });
    }

    private void initCourseTable() {
        courseTable = (CourseTableLayout) fragmentView
                .findViewById(R.id.courseTable);
        courseTable.setTableInitializeListener(this);
        courseTable.setOnCourseClickListener(this);
        courseTable.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    closeSoftKeyboard();
                }
                return false;
            }
        });
    }

    public void startCourseDetail(Object object) {
        if (object instanceof Boolean) {
            Intent i = new Intent(getActivity(),
                    CourseDetailActivity.class);
            i.putExtra("CourseNo", selectedCourseNo);
            startActivityForResult(i, 2);
        } else if (object instanceof String) {
            showAlertMessage((String) object);
        }
    }

    public void obtainSemesterList(Object object) {
        if (object instanceof ArrayList) {
            ArrayList<Semester> result = Utility.castListObject(object,
                    Semester.class);
            if (result != null) {
                semesters.clear();
                semesters.addAll(result);
                mSemesterSelector.setOnTouchListener(null);
                mSemesterSelector.setSemesterList(semesters);
                if (needShowSemesterDialog) {
                    mSemesterSelector.performClick();
                } else {
                    semesterSelectedLis.onSemesterSelected(semesters.get(0));
                }
            }
        } else if (object instanceof String) {
            showAlertMessage((String) object);
        }
    }

    public void obtainStudentCourse(Object object) {
        if (object instanceof StudentCourse) {
            StudentCourse result = (StudentCourse) object;
            Model.getInstance().setStudentCourse(result);
            StudentCourse studentCourse = Model.getInstance().getStudentCourse();
            showCourse(studentCourse);
            Snackbar.make(fragmentView.findViewById(R.id.main_layout), getText(R.string.course_offline_save)
                    , Snackbar.LENGTH_LONG).setAction(R.string.save_button, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    StudentCourse course = Model.getInstance().getStudentCourse();
                    saveStudentCourse();
                }
            }).setActionTextColor(getResources().getColor(R.color.dark_red)).show();
        } else if (object instanceof String) {
            showAlertMessage((String) object);
        }
    }


    public DialogInterface.OnClickListener courseDetailDialogLis = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            if (selectedCourseNo.equals("0")) {
                Toast.makeText(getActivity(), R.string.course_classmeeting,
                        Toast.LENGTH_LONG).show();
            } else {
                if (WifiUtility.isNetworkAvailable(getActivity())) {
                    if(Utility.checkAccount(getActivity())) {
                        CourseDetailTask courseDetailTask = new CourseDetailTask(CourseFragment.this);
                        courseDetailTask.execute(selectedCourseNo);
                    }
                } else {
                    Toast.makeText(getActivity(),
                            R.string.check_network_available, Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    };

    public SemesterSelector.OnSemesterSelectedListener semesterSelectedLis = new SemesterSelector.OnSemesterSelectedListener() {
        @Override
        public void onSemesterSelected(Semester semester) {
            if (semesters.size() == 0) {
                showAlertMessage(getString(R.string.course_nosemester));
                lockSemesterSpinner();
                return;
            }
            mSemester = semester;
            if (!TextUtils.isEmpty(lastSid)) {
                searchCourseTable(lastSid, mSemester);
            }
        }
    };

    @Override
    public void onClick(View view) {
        CourseInfo item = (CourseInfo) view.getTag();
        showInfoDialog(view.getId(), item.getCourseName(), item);
    }

    private void saveStudentCourse() {
        Model.getInstance().saveStudentCourse();
        Intent intent = new Intent(
                Constants.ACTION_COURSEWIDGET_UPDATE_STR);
        getActivity().sendBroadcast(intent);
        Toast.makeText(getActivity(), R.string.course_offline_saved,
                Toast.LENGTH_SHORT).show();
    }

    private void showInfoDialog(int id, String courseName, CourseInfo course) {
        selectedCourseNo = course.getCourseNo();
        Builder course_dialog_builder = new AlertDialog.Builder(getActivity());
        course_dialog_builder.setTitle(courseName);
        String message = String.format(Locale.TAIWAN,
                getString(R.string.course_courseid) + "%s\n" + getString(R.string.course_classtime) + "%s\n" + getString(R.string.course_classplace) + "%s\n" + getString(R.string.course_classteacher) + "%s", course.getCourseNo(),
                TIME_ARRAY[id - 1], course.getCourseRoom(),
                course.getCourseTeacher());
        course_dialog_builder.setMessage(message);
        course_dialog_builder.setPositiveButton(R.string.course_classdetail, courseDetailDialogLis);
        course_dialog_builder.show();
    }

    @Override
    public void onTableInitialized(CourseTableLayout course_table) {
        StudentCourse studentCourse = Model.getInstance()
                .getStudentCourse();
        if (studentCourse != null) {
            showCourse(studentCourse);
            Toast.makeText(getActivity(), R.string.course_click_for_detail, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_course, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                if (Model.getInstance().getStudentCourse() != null) {
                    StudentCourse course = Model.getInstance().getStudentCourse();
                    saveStudentCourse();
                } else {
                    Toast.makeText(getActivity(), R.string.course_nocourse,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_clear:
                Model.getInstance().deleteStudentCourse();
                Intent intent = new Intent(
                        Constants.ACTION_COURSEWIDGET_UPDATE_STR);
                getActivity().sendBroadcast(intent);
                Toast.makeText(getActivity(), R.string.course_offline_cleared, Toast.LENGTH_SHORT)
                        .show();
                break;
        }
        return true;
    }

    @Override
    public int getTitleColorId() {
        return R.color.dark_green;
    }

    @Override
    public int getTitleStringId() {
        return R.string.course_text;
    }

    private void showCourse(StudentCourse studentCourse) {
        mSemester = new Semester(studentCourse.getYear(), studentCourse.getSemester());
        mSemesterSelector.setText(mSemester);
        courseTable.showCourse(studentCourse);
    }
}
