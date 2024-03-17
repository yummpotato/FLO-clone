package com.example.flo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week1.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 가입 완료 버튼을 눌렀을 때, signUp 실행
        binding.signUpSignUpBtn.setOnClickListener {
            signUp()
            finish() // 로그인 화면으로 이동
        }
    }

    private fun getUser(): User {
        val email : String = binding.signUpIdEt.text.toString() + "@" + binding.signUpDirectInputEt.text.toString()
        val pwd : String = binding.signUpPasswordEt.text.toString()

        return User(email, pwd) // 사용자의 입력값 리턴
    }

    private fun signUp() {
        // id칸이나 pw칸이 입력되지 않았거나 pw를 잘못 입력했을 경우, 입력이 되지 않게끔!
        if(binding.signUpIdEt.text.toString().isEmpty() || binding.signUpDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this@SignUpActivity, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.signUpPasswordEt.text.toString().isEmpty() || binding.signUpPasswordCheckEt.text.toString().isEmpty()) {
            Toast.makeText(this@SignUpActivity, "비밀번호를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()) {
            Toast.makeText(this@SignUpActivity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            return
        }

        // db에 저장
        val userDB = SongDatabase.getInstance(this@SignUpActivity)!!
        userDB.userDao().insert(getUser()) // userDB에 추가
        // getUser(): 현재 입력한 정보

        val user = userDB.userDao().getUsers() // 현재 테이블에 저장된 정보 가져오기
        Log.d("SIGNUP_ACT", user.toString())
    }
}