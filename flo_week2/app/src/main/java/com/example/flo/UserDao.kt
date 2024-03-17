package com.example.flo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    // 모든 user 정보 가져오기
    @Query("SELECT * FROM UserTable")
    fun getUsers(): List<User> // user정보 가져오기

    // 한 명의 user 정보 가져오기
    @Query("SELECT * FROM UserTable WHERE email = :email AND password = :password")
    fun gerUser(email: String, password: String): User? // 정보가 있을 수도, 없을 수도 있기에!
}