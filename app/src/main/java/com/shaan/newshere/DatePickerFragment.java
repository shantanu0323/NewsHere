package com.shaan.newshere;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Shaan on 09-02-18.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    String fromDate;
    OnDatePickedListener mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCallback = (OnDatePickedListener) getActivity();
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        Calendar minDate = Calendar.getInstance();
        minDate.set(getArguments().getInt("minYear"), getArguments().getInt("minMonth"),
                getArguments().getInt("minDay"));
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                R.style.CustomDatePickerDialogTheme, this, year, month, day);
        dpd.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dpd.getDatePicker().setMaxDate(currentDate.getTimeInMillis());
        dpd.setTitle(getArguments().getString("title"));
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the chosen date
        fromDate = year + "-" + month + "-" + day;
        if (mCallback != null) {
            mCallback.onDatePicked(year, month, day);
        }
    }


    public interface OnDatePickedListener {
        public void onDatePicked(int year, int month, int day);
    }
}