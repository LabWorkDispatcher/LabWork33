package com.example.laba30.UI;

import static com.example.laba30.data.Constants.APP_KEY_GROUP;
import static com.example.laba30.data.Constants.APP_KEY_NAME;
import static com.example.laba30.data.Constants.APP_KEY_SURNAME;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_ACCEPTED_AMOUNT;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_ACCEPTED_DATES;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_COMPLETED_AMOUNT;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_COMPLETED_DATES;
import static com.example.laba30.data.Constants.APP_RESULTS_FILE_NAME;
import static com.example.laba30.utils.Utils.checkIfDataIsAlreadySaved;
import static com.example.laba30.utils.Utils.moveToActivity;
import static com.example.laba30.utils.Utils.writeFileOnInternalStorage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.laba30.adapters.MyRecyclerViewAdapter;
import com.example.laba30.adapters.RecyclerViewItem;
import com.example.laba30.data.Date;
import com.example.laba30.data.PersonalDataFormat;
import com.example.laba30.databinding.ActivityFinalBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class FinalActivity extends AppCompatActivity {
    private ActivityFinalBinding binding;
    private Bundle prevActivityBundle;
    private ArrayList<RecyclerViewItem> recyclerViewList;
    private ArrayList<Date> acceptedWorks, completedWorks;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prevActivityBundle = getIntent().getExtras();
        binding.textView1.setText(prevActivityBundle.getString(APP_KEY_NAME) + " " + prevActivityBundle.getString(APP_KEY_SURNAME) + "\n" + prevActivityBundle.getString(APP_KEY_GROUP));
        acceptedWorks = prevActivityBundle.getParcelableArrayList(APP_KEY_WORKS_ACCEPTED_DATES);
        completedWorks = prevActivityBundle.getParcelableArrayList(APP_KEY_WORKS_COMPLETED_DATES);

        binding.saveButton.setOnClickListener(view -> {
            saveResult();
        });

        binding.leaveButton.setOnClickListener(view -> {
            this.finish();
            System.exit(0);
        });

        binding.goBackButton.setOnClickListener(view -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(APP_KEY_NAME, prevActivityBundle.getString(APP_KEY_NAME));
            i.putExtra(APP_KEY_SURNAME, prevActivityBundle.getString(APP_KEY_SURNAME));
            i.putExtra(APP_KEY_GROUP, prevActivityBundle.getString(APP_KEY_GROUP));
            i.putExtra(APP_KEY_WORKS_COMPLETED_AMOUNT, prevActivityBundle.getInt(APP_KEY_WORKS_COMPLETED_AMOUNT));
            i.putExtra(APP_KEY_WORKS_ACCEPTED_AMOUNT, prevActivityBundle.getInt(APP_KEY_WORKS_ACCEPTED_AMOUNT));
            moveToActivity(this, i);
        });


        recyclerViewList = new ArrayList<>();
        for (int i = 0; i < acceptedWorks.size(); i++) {
            recyclerViewList.add(new RecyclerViewItem(acceptedWorks.get(i).year, acceptedWorks.get(i).month, acceptedWorks.get(i).day, i+1, true));
        }
        for (int i = 0; i < completedWorks.size(); i++) {
            recyclerViewList.add(new RecyclerViewItem(completedWorks.get(i).year, completedWorks.get(i).month, completedWorks.get(i).day, acceptedWorks.size()+i+1, false));
        }

        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter();
        adapter.differ.submitList(recyclerViewList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void saveResult() {
        PersonalDataFormat resultObj = new PersonalDataFormat(prevActivityBundle.getString(APP_KEY_NAME), prevActivityBundle.getString(APP_KEY_SURNAME), prevActivityBundle.getString(APP_KEY_GROUP),
                prevActivityBundle.getInt(APP_KEY_WORKS_COMPLETED_AMOUNT), prevActivityBundle.getInt(APP_KEY_WORKS_ACCEPTED_AMOUNT), completedWorks, acceptedWorks);
        String saveResult = new Gson().toJson(resultObj);

        if (checkIfDataIsAlreadySaved(this, APP_RESULTS_FILE_NAME, saveResult)) {
            Toast.makeText(this, "The data is already saved.", Toast.LENGTH_SHORT).show();
            return;
        }

        writeFileOnInternalStorage(this, APP_RESULTS_FILE_NAME, saveResult);
        Toast.makeText(this, "The data has successfully been saved!", Toast.LENGTH_SHORT).show();
    }
}
