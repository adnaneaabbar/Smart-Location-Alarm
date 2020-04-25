package com.example.smartlocationalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivityAlarms extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<alarm> arrayList;
    private FirebaseRecyclerOptions<alarm> options;
    private FirebaseRecyclerAdapter<alarm, FirebaseViewHolder> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<alarm>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("alarm");
        databaseReference.keepSynced(true);
        options = new FirebaseRecyclerOptions.Builder<alarm>().setQuery(databaseReference,alarm.class).build()  ;
        adapter = new FirebaseRecyclerAdapter<alarm, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull final alarm alarm) {
                firebaseViewHolder._name.setText(alarm.getName());
                firebaseViewHolder._date.setText(alarm.getLatitude());
                firebaseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivityAlarms.this,MainActivityAlarms.class);
                        intent.putExtra("name", alarm.getName());
                        Intent intent1 = intent.putExtra("latitude", alarm.getLatitude());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(MainActivityAlarms.this).inflate(R.layout.row,parent,false));
            }
        };
        recyclerView.setAdapter(adapter);

    }



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Delete")){
            deleteTask(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete_all){
            databaseReference.removeValue();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTask (String key){
        databaseReference.child(key).removeValue();
    }
}