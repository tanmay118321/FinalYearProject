package com.deepdefender.finalyearproject;


import android.content.Context;


import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AttendanceResetWorker extends Worker {


    public AttendanceResetWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }


    @NonNull
    @Override
    public Result doWork() {
// Delete yesterday's node if you want cleanup, OR just rely on date-based nodes (recommended)
// Here we do nothing destructive: we simply ensure a new day starts clean by design.
// Optional cleanup example:
// String yesterday = getDate(-1);
// FirebaseDatabase.getInstance().getReference("Attendance").child(yesterday).removeValue();
        return Result.success();
    }


    private String getDate(int offsetDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, offsetDays);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
    }
}