package com.flowers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flowers.world.PlayerState;

public class MainActivity extends AppCompatActivity {

    private Button _runButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, GameActivity.class);

        _runButton = findViewById(R.id.runButton);
        _runButton.setOnClickListener(v -> {
            startActivity(intent);
            EditText editText = findViewById(R.id.editText);
            PlayerState.getInstance().setName(editText.getText().toString());
        });
    }
}