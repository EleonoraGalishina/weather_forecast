package com.example.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        AppDatabase db = AppDatabase.getDatabase(getContext());
        userDao = db.userDao();

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
                    Toast.makeText(getContext(), "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                } else {
                    new CheckUserTask().execute(login, password);
                }
            }
        });

        return view;
    }

    private class CheckUserTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String login = params[0];
                return userDao.getUserByLogin(login) != null;
            } catch (Exception e) {
                Log.e("SignUpFragment", "Ошибка проверки пользователя", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean userExists) {
            if (userExists) {
                Toast.makeText(getContext(), "Вы уже зарегистрированы", Toast.LENGTH_SHORT).show();
            } else {
                new RegisterUserTask().execute(loginField.getText().toString().trim(), passwordField.getText().toString().trim());
            }
        }
    }

    private class RegisterUserTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String login = params[0];
                String password = params[1];

                User user = new User();
                user.setLogin(login);
                user.setPassword(password);
                userDao.insert(user);
                return true;
            } catch (Exception e) {
                Log.e("SignUpFragment", "Ошибка регистрации пользователя", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                loginField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } else {
                Toast.makeText(getContext(), "Регистрация не удалась", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
