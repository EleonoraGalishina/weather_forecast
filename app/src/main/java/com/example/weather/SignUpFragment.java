package com.example.weather;

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

public class SignUpFragment extends Fragment {

    private EditText loginField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button btnRegister;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        db = new DatabaseHelper(getContext());

        loginField = view.findViewById(R.id.loginField);
        passwordField = view.findViewById(R.id.passwordField);
        confirmPasswordField = view.findViewById(R.id.confirmPasswordField);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String confirmPassword = confirmPasswordField.getText().toString().trim();

                if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getContext(), "Пожалуйста, заполните все поля!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                } else if (db.checkLogin(login)) {
                    Toast.makeText(getContext(), "Вы уже зарегистрированы", Toast.LENGTH_SHORT).show();
                } else {
                    boolean insert = db.insertUser(login, password);
                    if (insert) {
                        Toast.makeText(getContext(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                        loginField.setText("");
                        passwordField.setText("");
                        confirmPasswordField.setText("");
                    } else {
                        Toast.makeText(getContext(), "Регистрация не удалась", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}
