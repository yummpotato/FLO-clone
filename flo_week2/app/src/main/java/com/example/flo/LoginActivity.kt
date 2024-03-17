package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week1.MainActivity
import com.example.week1.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        // login 실행
        binding.loginSignInBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        // 입력하지 않은 부분이 있는지 check
        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this@LoginActivity, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this@LoginActivity, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val email : String = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val pwd : String = binding.loginPasswordEt.text.toString()

        // DB에서 사용자가 입력한 정보가 존재하는 지 check
        val songDB = SongDatabase.getInstance(this@LoginActivity)!!
        // 해당 user가 존재하는 지 check
        val user = songDB.userDao().gerUser(email, pwd)

        // user가 null이 아닐 때
        user?.let {
            Log.d("LOGIN_ACT/GET_USER", "userId: ${user.id}, $user")
            saveJwt(user.id)
            startMainActivity()
        }

        if(user == null) {
            Toast.makeText(this@LoginActivity, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(this@LoginActivity, "${user!!.id}번 회원님 반갑습니다.", Toast.LENGTH_SHORT).show()
    }

    // jwt 저장 함수
    private fun saveJwt(jwt: Int) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putInt("jwt", jwt) // 어떤 키 값으로 저장할 지
        editor.apply()
    }

    // 화면 이동
    private fun startMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }
}