package com.example.laba30.UI;

import static com.example.laba30.data.Constants.APP_KEY_GROUP;
import static com.example.laba30.data.Constants.APP_KEY_NAME;
import static com.example.laba30.data.Constants.APP_KEY_SURNAME;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_ACCEPTED_AMOUNT;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_ACCEPTED_DATES;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_COMPLETED_AMOUNT;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_COMPLETED_DATES;
import static com.example.laba30.data.Constants.APP_MIN_AMOUNT_WORKS_ACCEPTED;
import static com.example.laba30.data.Constants.APP_MIN_AMOUNT_WORKS_COMPLETED;
import static com.example.laba30.data.Constants.APP_MIN_LENGTH_GROUP;
import static com.example.laba30.data.Constants.APP_MIN_LENGTH_NAME;
import static com.example.laba30.data.Constants.APP_MIN_LENGTH_SURNAME;
import static com.example.laba30.data.Constants.APP_RESULTS_FILE_NAME;
import static com.example.laba30.data.Constants.APP_TOAST_MESSAGE_EMPTY_FIELDS;
import static com.example.laba30.utils.Utils.moveToActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.laba30.R;
import com.example.laba30.data.PersonalDataFormat;
import com.example.laba30.databinding.ActivityMainBinding;
import com.example.laba30.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle prevActivityBundle = getIntent().getExtras();
        if (prevActivityBundle != null) {
            binding.userName.setText(prevActivityBundle.getString(APP_KEY_NAME));
            binding.userSurname.setText(prevActivityBundle.getString(APP_KEY_SURNAME));
            binding.userGroup.setText(prevActivityBundle.getString(APP_KEY_GROUP));
            binding.worksCompleted.setText("" + prevActivityBundle.getInt(APP_KEY_WORKS_COMPLETED_AMOUNT));
            binding.worksAccepted.setText("" + prevActivityBundle.getInt(APP_KEY_WORKS_ACCEPTED_AMOUNT));
        }

        binding.goFurtherButton.setOnClickListener(view -> {
            String nameText = binding.userName.getText().toString(),
                    surnameText = binding.userSurname.getText().toString(),
                    groupText = binding.userGroup.getText().toString(),
                    worksCompletedText = binding.worksCompleted.getText().toString(),
                    worksAcceptedText = binding.worksAccepted.getText().toString();

            if (nameText.isEmpty() || surnameText.isEmpty() || groupText.isEmpty() || worksCompletedText.isEmpty() || worksAcceptedText.isEmpty()) {
                Toast.makeText(this, APP_TOAST_MESSAGE_EMPTY_FIELDS, Toast.LENGTH_SHORT).show();
                return;
            }

            if (nameText.length() < APP_MIN_LENGTH_NAME) {
                Toast.makeText(this, "The name should be at least " + APP_MIN_LENGTH_NAME + " letter(s) long.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (surnameText.length() < APP_MIN_LENGTH_NAME) {
                Toast.makeText(this, "The surname should be at least " + APP_MIN_LENGTH_SURNAME + " letter(s) long.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (groupText.length() < APP_MIN_LENGTH_GROUP) {
                Toast.makeText(this, "The group should be at least " + APP_MIN_LENGTH_GROUP + " character(s) long.", Toast.LENGTH_SHORT).show();
                return;
            }

            int completedWorksAmount = Integer.parseInt(worksCompletedText), acceptedWorksAmount = Integer.parseInt(worksAcceptedText);
            if (completedWorksAmount < APP_MIN_AMOUNT_WORKS_COMPLETED) {
                Toast.makeText(this, "There should be at least " + APP_MIN_AMOUNT_WORKS_COMPLETED + " completed work(s).", Toast.LENGTH_SHORT).show();
                return;
            }
            if (acceptedWorksAmount < APP_MIN_AMOUNT_WORKS_ACCEPTED) {
                Toast.makeText(this, "There should be at least " + APP_MIN_AMOUNT_WORKS_ACCEPTED + " accepted work(s).", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(this, AcceptedWorksDateInsertionActivity.class);
            i.putExtra(APP_KEY_NAME, nameText);
            i.putExtra(APP_KEY_SURNAME, surnameText);
            i.putExtra(APP_KEY_GROUP, groupText);
            i.putExtra(APP_KEY_WORKS_COMPLETED_AMOUNT, completedWorksAmount);
            i.putExtra(APP_KEY_WORKS_ACCEPTED_AMOUNT, acceptedWorksAmount);
            moveToActivity(this, i);
        });


        ArrayList<PersonalDataFormat> resultList = Utils.getResultListFromFile(this, APP_RESULTS_FILE_NAME);
        ArrayList<String> spinnerList = new ArrayList<>();
        for (PersonalDataFormat i : resultList) {
            String printName = i.name + " " + i.surname;

            int appearanceAmount = 0;
            for (int j = spinnerList.size(); j > 0; j--) {
                String comparisonName;
                if (j == 1) {
                    comparisonName = printName;
                } else {
                    comparisonName = printName + " (" + j + ")";
                }
                if (spinnerList.contains(comparisonName)) {
                    appearanceAmount = j;
                    break;
                }
            }
            if (appearanceAmount > 0) {
                printName += (" (" + (appearanceAmount+1) + ")");
            }

            spinnerList.add(printName);
        }
        if (spinnerList.isEmpty()) { binding.chooseFromFileSpinner.setEnabled(false); return; }

        spinnerList.add(0, "");
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_item, spinnerList);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.chooseFromFileSpinner.setAdapter(spinnerAdapter);
        binding.chooseFromFileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) { return; }
                PersonalDataFormat chosenItem = resultList.get(i - 1);

                Intent intent = new Intent(binding.getRoot().getContext(), FinalActivity.class);
                intent.putExtra(APP_KEY_NAME, chosenItem.name);
                intent.putExtra(APP_KEY_SURNAME, chosenItem.surname);
                intent.putExtra(APP_KEY_GROUP, chosenItem.group);
                intent.putExtra(APP_KEY_WORKS_ACCEPTED_AMOUNT, chosenItem.acceptedWorksAmount);
                intent.putExtra(APP_KEY_WORKS_COMPLETED_AMOUNT, chosenItem.completedWorksAmount);
                intent.putParcelableArrayListExtra(APP_KEY_WORKS_ACCEPTED_DATES, chosenItem.acceptedWorksDates);
                intent.putParcelableArrayListExtra(APP_KEY_WORKS_COMPLETED_DATES, chosenItem.completedWorksDates);
                moveToActivity((AppCompatActivity)binding.getRoot().getContext(), intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}