package foodbook.thinmint.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pools;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Zachery.Sogolow on 5/16/2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private DayActivityFragment.DayFragmentDataListener mDayCallback;

    private Date mDate;

    public DatePickerFragment() {
        mDate = new Date(System.currentTimeMillis());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mDayCallback = (DayActivityFragment.DayFragmentDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

//            mDayCallback.selectDay(new Date(System.currentTimeMillis()));
    }

    public void setDate(Date date) {
        mDate = date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mDayCallback.selectDay(calendar.getTime());
    }
}