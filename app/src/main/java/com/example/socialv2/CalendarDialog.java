package com.example.socialv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialv2.Adapters.CreateListAdapter;
import com.example.socialv2.Model.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CalendarDialog extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }
    public CalendarDialog(getDate getDate){
        this.getDate=getDate;
    }

    private getDate getDate;
    private CalendarView mCalendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_dialog_layout, container, false);
        return view;
    }
    public interface getDate{
        void onDateClicked(String date);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mCalendarView=view.findViewById(R.id.calendarView);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = i2 + "/" + (i1+1) + "/" + i;
                getDate.onDateClicked(date);
            }
        });



        super.onViewCreated(view, savedInstanceState);
    }
}
