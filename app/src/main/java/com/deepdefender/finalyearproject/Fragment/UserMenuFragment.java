package com.deepdefender.finalyearproject.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.deepdefender.finalyearproject.MenuModel;
import com.deepdefender.finalyearproject.R;
import com.google.firebase.database.*;

public class UserMenuFragment extends Fragment implements View.OnClickListener {

    // Day TextViews
    TextView dayMon, dayTue, dayWed, dayThu, dayFri, daySat, daySun;

    // Menu TextViews
    TextView b1, b2, b3;
    TextView l1, l2, l3, l4;
    TextView d1, d2, d3, d4;

    DatabaseReference dbRef;
    String selectedDay = "Monday";

    TextView[] dayViews;
    String[] days = {
            "Monday","Tuesday","Wednesday",
            "Thursday","Friday","Saturday","Sunday"
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_usermenu, container, false);

        // Days
        dayMon = v.findViewById(R.id.dayMon);
        dayTue = v.findViewById(R.id.dayTue);
        dayWed = v.findViewById(R.id.dayWed);
        dayThu = v.findViewById(R.id.dayThu);
        dayFri = v.findViewById(R.id.dayFri);
        daySat = v.findViewById(R.id.daySat);
        daySun = v.findViewById(R.id.daySun);

        dayViews = new TextView[]{
                dayMon, dayTue, dayWed, dayThu, dayFri, daySat, daySun
        };

        // Breakfast items
        b1 = v.findViewById(R.id.txtBreakfastItem1);
        b2 = v.findViewById(R.id.txtBreakfastItem2);
        b3 = v.findViewById(R.id.txtBreakfastItem3);

        // Lunch items
        l1 = v.findViewById(R.id.txtLunchItem1);
        l2 = v.findViewById(R.id.txtLunchItem2);
        l3 = v.findViewById(R.id.txtLunchItem3);
        l4 = v.findViewById(R.id.txtLunchItem4);

        // Dinner items
        d1 = v.findViewById(R.id.txtDinnerItem1);
        d2 = v.findViewById(R.id.txtDinnerItem2);
        d3 = v.findViewById(R.id.txtDinnerItem3);
        d4 = v.findViewById(R.id.txtDinnerItem4);

        // Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("WeeklyMenu");

        // Set listeners
        for (TextView tv : dayViews) {
            tv.setOnClickListener(this);
        }

        // Default load Monday
        selectDay(0);

        return v;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < dayViews.length; i++) {
            if (v.getId() == dayViews[i].getId()) {
                selectDay(i);
                break;
            }
        }
    }

    private void selectDay(int index) {
        selectedDay = days[index];

        for (int i = 0; i < dayViews.length; i++) {
            dayViews[i].setBackgroundColor(
                    i == index ? Color.parseColor("#00BCD4") : Color.WHITE
            );
        }

        loadMenu(selectedDay);
    }

    private void loadMenu(String day) {
        dbRef.child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MenuModel menu = snapshot.getValue(MenuModel.class);
                if (menu == null) return;

                // Split items safely
                setItems(menu.breakfast, b1, b2, b3);
                setItems(menu.lunch, l1, l2, l3, l4);
                setItems(menu.dinner, d1, d2, d3, d4);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void setItems(String data, TextView... views) {
        String[] items = data.split("/");
        for (int i = 0; i < views.length; i++) {
            if (i < items.length) {
                views[i].setText("• " + items[i].trim());
                views[i].setVisibility(View.VISIBLE);
            } else {
                views[i].setVisibility(View.GONE);
            }
        }
    }
}
