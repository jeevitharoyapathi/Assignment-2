package com.jeevitharoyapathi.assignment_2.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.jeevitharoyapathi.assignment_2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsDialogFragment extends DialogFragment {

    @BindView(R.id.txtBeginDate)
    EditText mBeginDate;
    @BindView(R.id.spSortOrder)
    Spinner mSpinner;
    @BindView(R.id.cbArts)
    CheckBox mArts;
    @BindView(R.id.cbFashion)
    CheckBox mFashion;
    @BindView(R.id.cbSports)
    CheckBox mSports;
    private SettingsChangeListener mSettingsChanged;
    private final Calendar calendar = Calendar.getInstance();
    private SharedPreferences mSharedPreference;
    public final static String SETTINGS = "Settings";
    private static Context mContext;

    public static SettingsDialogFragment newInstance(Context context) {
        SettingsDialogFragment newDialogFragment = new SettingsDialogFragment();
        mContext = context;
        Bundle args = new Bundle();
        newDialogFragment.setArguments(args);
        return newDialogFragment;
    }

    public interface SettingsChangeListener {
        void onSettingsChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mSettingsChanged = (SettingsChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddItemListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mSharedPreference = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View container = inflater.inflate(R.layout.settings, null);
        ButterKnife.bind(this, container);
        View view = inflater.inflate(R.layout.dialog_custom_title, null);
        builder.setCustomTitle(view);
        builder.setView(container);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSettingsPreference();
                dialog.dismiss();
                mSettingsChanged.onSettingsChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        setListeners();
        setValues();
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    private void setValues() {
        mBeginDate.setText(getDate(mSharedPreference.getLong("Date", 0)));
        mSpinner.setSelection(mSharedPreference.getInt("SortPosition", 0));
        Set<String> set = mSharedPreference.getStringSet("Categories", null);
        if (set != null && !set.isEmpty()) {
            mArts.setChecked(set.contains("Arts"));
            mFashion.setChecked(set.contains("Fashion"));
            mSports.setChecked(set.contains("Sports"));
        }
    }

    private void saveSettingsPreference() {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putLong("Date", calendar.getTimeInMillis());
        editor.putString("Sort", mSpinner.getSelectedItem().toString());
        editor.putInt("SortPosition", mSpinner.getSelectedItemPosition());
        Set<String> set = new HashSet<String>();
        if (mArts.isChecked()) {
            set.add("Arts");
        }
        if (mFashion.isChecked()) {
            set.add("Fashion");
        }
        if (mSports.isChecked()) {
            set.add("Sports");
        }
        editor.putStringSet("Categories", set);
        editor.commit();
    }


    private void setListeners() {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        mBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeginDate.length() == 0) {
                    mBeginDate.setText(" ");
                }

                DatePickerFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mBeginDate.setText(getDate(calendar.getTimeInMillis()).toString());

                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mBeginDate.setText(null);
                    }
                };
                //Displays the dialog
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
            }
        });
    }

    public static String getDate(long date) {
        if (date == 0)
            return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }
}
