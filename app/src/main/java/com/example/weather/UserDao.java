package com.example.weather;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(com.example.weather.User user);

    @Query("SELECT * FROM users WHERE login = :login AND password = :password")
    com.example.weather.User getUser(String login, String password);

    @Query("SELECT * FROM users WHERE login = :login")
    com.example.weather.User getUserByLogin(String login);
}
