package com.example.socialv2;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;

public class TimerDialog extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }
    public TimerDialog(getTime getTime,int state){
        this.getTime=getTime;
        this.state=state;
    }
    private static final int INTERVAL = 5;
    private static final DecimalFormat FORMATTER = new DecimalFormat("00");
    int state;
    private getTime getTime;
    private TimePicker timePicker;
    private NumberPicker minutePicker;
    private Button setTime;
    private String TAG="TimerDialog";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_dialog_layout, container, false);
        return view;
    }
    public interface getTime{
        void onTimeClicked(String hour,String minute,int state);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        timePicker=view.findViewById(R.id.timeSpinner);
        setTime=view.findViewById(R.id.setTime);
        if(state==1){




        }else{
            timePicker.setHour(1);
            timePicker.setMinute(0);
            setMinutePicker();

        }

       timePicker.setIs24HourView(true);

       setTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(state==1){
                    int hour=  timePicker.getHour();

                    int minute= timePicker.getMinute();
                    getTime.onTimeClicked(String.valueOf(hour),  String.valueOf(minute),state );

                }else{
                    int hour=  timePicker.getHour();
                    int minute= getMinute();
                    getTime.onTimeClicked(String.valueOf(hour),  String.valueOf(minute),state );
                }

            }
        });




        super.onViewCreated(view, savedInstanceState);
    }

    public void setMinutePicker() {
        int numValues = 60 / INTERVAL;
        String[] displayedValues = new String[numValues];
        for (int i = 0; i < numValues; i++) {
            displayedValues[i] = FORMATTER.format(i * INTERVAL);
        }

        View minute = timePicker.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        if ((minute != null) && (minute instanceof NumberPicker)) {
            minutePicker = (NumberPicker) minute;
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(numValues - 1);
            minutePicker.setDisplayedValues(displayedValues);
        }
    }
    public int getMinute() {
        if (minutePicker != null) {
            return (minutePicker.getValue() * INTERVAL);
        } else {
            return timePicker.getCurrentMinute();
        }
    }
}
