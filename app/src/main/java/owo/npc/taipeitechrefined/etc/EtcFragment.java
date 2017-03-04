package owo.npc.taipeitechrefined.etc;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import owo.npc.taipeitechrefined.BaseFragment;
import owo.npc.taipeitechrefined.MainApplication;
import owo.npc.taipeitechrefined.R;

import static owo.npc.taipeitechrefined.MainApplication.lang;

/**
 * Created by kamisakihideyoshi on 2017/02/27.
 */

public class EtcFragment extends BaseFragment {
    private static View fragmentView = null;

    TextView uiLang_textView;
    TextView uiLangHint_textView;
    TextView uiE_textView;
    TextView uiC_textView;
    TextView uiJ_textView;
    TextView courseLang_textView;
    TextView courseLangHint_textView;
    TextView courseE_textView;
    TextView courseC_textView;
    SeekBar uiLang_seekBar;
    SeekBar courseLang_seekBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_etc, container,
                false);
        uiLang_textView = (TextView) fragmentView.findViewById(R.id.uiLang_textView);
        uiLangHint_textView = (TextView) fragmentView.findViewById(R.id.uiLangHint_textView);
        uiE_textView = (TextView) fragmentView.findViewById(R.id.uiE_textView);
        uiC_textView = (TextView) fragmentView.findViewById(R.id.uiC_textView);
        uiJ_textView = (TextView) fragmentView.findViewById(R.id.uiJ_textView);
        courseLang_textView = (TextView) fragmentView.findViewById(R.id.courseLang_textView);
        courseLangHint_textView = (TextView) fragmentView.findViewById(R.id.courseLangHint_textView);
        courseE_textView = (TextView) fragmentView.findViewById(R.id.courseE_textView);
        courseC_textView = (TextView) fragmentView.findViewById(R.id.courseC_textView);
        uiLang_seekBar = (SeekBar) fragmentView.findViewById(R.id.uiLang_seekBar);
        courseLang_seekBar = (SeekBar) fragmentView.findViewById(R.id.courseLang_seekBar);

        uiLang_textView.setText(R.string.etc_uilanguage_text);
        uiLangHint_textView.setText(R.string.etc_uilanguage_hint);
        uiE_textView.setText(R.string.etc_language_en);
        uiC_textView.setText(R.string.etc_language_zh);
        uiJ_textView.setText(R.string.etc_language_ja);
        courseLang_textView.setText(R.string.etc_courselanguage_text);
        courseLangHint_textView.setText(R.string.etc_courselanguage_hint);
        courseE_textView.setText(R.string.etc_language_en);
        courseC_textView.setText(R.string.etc_language_zh);
        uiLang_seekBar.setProgress(getCurrentUILang(MainApplication.readSetting("uiLang")));
        courseLang_seekBar.setProgress(getCurrentCourseLang(lang));

        uiLang_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int mProgress = seekBar.getProgress();
                int uiLang = getCurrentUILang(MainApplication.readSetting("uiLang"));
                if (mProgress >= 0 & mProgress < 33) {
                    seekBar.setProgress(0);
                    if (uiLang != 0) {
                        MainApplication.writeSetting("uiLang", "en");
                        switchLanguage("en");
                    }
                } else if (mProgress > 32 & mProgress < 66) {
                    seekBar.setProgress(50);
                    if (uiLang != 50) {
                        MainApplication.writeSetting("uiLang", "zh");
                        switchLanguage("zh");
                    }
                } else {
                    seekBar.setProgress(100);
                    if (uiLang != 100) {
                        MainApplication.writeSetting("uiLang", "ja");
                        switchLanguage("ja");
                    }
                }
            }
        });
        courseLang_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int mProgress = seekBar.getProgress();
                int courseLang = getCurrentCourseLang(MainApplication.readSetting("courseLang"));
                if(mProgress < 50) {
                    seekBar.setProgress(0);
                    if (courseLang != 0) {
                        MainApplication.writeSetting("courseLang", "en");
                        lang = "en";
                        Toast.makeText(getActivity(), R.string.etc_courselanguage_applied, Toast.LENGTH_LONG).show();
                    }
                } else {
                    seekBar.setProgress(100);
                    if (courseLang != 100) {
                        MainApplication.writeSetting("courseLang", "zh");
                        lang = "zh";
                        Toast.makeText(getActivity(), R.string.etc_courselanguage_applied, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return fragmentView;
    }

    protected void switchLanguage(String lang) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (lang) {
            case "zh":
                configuration.locale = Locale.TAIWAN;
                break;
            case "ja":
                configuration.locale = Locale.JAPAN;
                break;
            default:
                configuration.locale = Locale.ENGLISH;
                break;
        }

        resources.updateConfiguration(configuration, displayMetrics);
        MainApplication.writeSetting("uiLang", lang);
        //*
        getActivity().finish();
        getActivity().startActivity(getActivity().getIntent());
        //*/
    }

    private int getCurrentUILang(String lang) {
        switch (lang) {
            case "zh":
                return 50;
            case "ja":
                return 100;
            default:
                return 0;
        }
    }

    private int getCurrentCourseLang(String lang) {
        if(lang.equals("zh")) {
            return 100;
        } else{
            return 0;
        }
    }

    @Override
    public int getTitleColorId() {
        return R.color.lime;
    }

    @Override
    public int getTitleStringId() {
        return R.string.etc_text;
    }
}
