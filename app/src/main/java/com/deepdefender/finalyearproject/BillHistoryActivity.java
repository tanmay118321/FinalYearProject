package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class BillHistoryActivity extends AppCompatActivity {

    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_bill_history);

        recycler = findViewById(R.id.recyclerBills);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<BillModel> options =
                new FirebaseRecyclerOptions.Builder<BillModel>()
                        .setQuery(
                                FirebaseDatabase.getInstance()
                                        .getReference("monthlyBills"),
                                BillModel.class
                        ).build();

        FirebaseRecyclerAdapter<BillModel, BillVH> adapter =
                new FirebaseRecyclerAdapter<>(options) {

                    @Override
                    protected void onBindViewHolder(BillVH h, int p, BillModel m) {
                        h.text.setText(m.month+" - ₹"+m.total);
                    }

                    @Override
                    public BillVH onCreateViewHolder(android.view.ViewGroup p,int v){
                        return new BillVH(
                                android.view.LayoutInflater.from(p.getContext())
                                        .inflate(android.R.layout.simple_list_item_1,p,false)
                        );
                    }
                };

        recycler.setAdapter(adapter);
        adapter.startListening();
    }

    static class BillVH extends RecyclerView.ViewHolder {
        TextView text;
        BillVH(android.view.View v){
            super(v);
            text = v.findViewById(android.R.id.text1);
        }
    }
}
