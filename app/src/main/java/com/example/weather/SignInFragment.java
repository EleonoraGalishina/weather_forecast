package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignInFragment extends Fragment {

    private EditText loginField;
    private EditText passwordField;
    private Button btnLogin;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        db = new DatabaseHelper(getContext());

        loginField = view.findViewById(R.id.loginField);
        passwordField = view.findViewById(R.id.passwordField);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Пожалуйста, заполните все поля!", Toast.LENGTH_SHORT).show();
                } else if (db.checkUser(login, password)) {
                    Intent intent = new Intent(getContext(), WelcomeActivity.class);
                    intent.putExtra("LOGIN", login);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Ошибка входа", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
