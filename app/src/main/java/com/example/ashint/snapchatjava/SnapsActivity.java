package com.example.ashint.snapchatjava;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.Inflater;

public class SnapsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ListView snapsListView;
    ArrayList<String> emails = new ArrayList<String>();
    ArrayList<DataSnapshot> snaps = new ArrayList<DataSnapshot>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);

        mAuth = FirebaseAuth.getInstance();

        snapsListView = findViewById(R.id.snapsListView);

        final ArrayAdapter arrayAdapter = new ArrayAdapter(SnapsActivity.this, android.R.layout.simple_list_item_1, emails);

        snapsListView.setAdapter(arrayAdapter);

        final ChildEventListener childEventListener = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                emails.add(dataSnapshot.child("from").getValue().toString());
                snaps.add(dataSnapshot);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                int index = 0;

                for (DataSnapshot snap : snaps) {

                    Log.i("B4IFSnapLog", snap.getKey().toString());
                    Log.i("B4IFDatasnapLog", dataSnapshot.getKey().toString());

                    if (dataSnapshot.getKey().equals(snap.getKey())) {

                        Log.i("SnapLog", snap.getKey().toString());
                        Log.i("DatasnapLog", dataSnapshot.getKey().toString());

                        snaps.remove(index);
                        emails.remove(index);

                    } else {

                        Log.i("ERROR", "COULD NOT EXECUTE");

                    }

                    index++;

                }

                arrayAdapter.notifyDataSetChanged();

            }

        });

        snapsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               DataSnapshot snapshot = snaps.get(i);

               Intent intent = new Intent (SnapsActivity.this, ViewSnapActivity.class);
               intent.putExtra("imageName", snapshot.child("imageName").getValue().toString());
               intent.putExtra("imageURL", snapshot.child("imageURL").getValue().toString());
               intent.putExtra("message", snapshot.child("message").getValue().toString());
               intent.putExtra("snapKey", snapshot.getKey());

               startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.snaps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addSnap) {

            Intent intent = new Intent(this, CreateSnapActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.logout) {
             mAuth.signOut();
             finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
    }
}
