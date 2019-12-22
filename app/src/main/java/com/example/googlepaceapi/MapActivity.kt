package com.example.googlepaceapi

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
const val USER_NEED_AUTH = "user_need_auth"

class MapActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context?, bundle: Bundle? = null, requestCode: Int? = null) {
            val intent = Intent(context, MapActivity::class.java)
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            if (requestCode == null) {
                (context as Activity).startActivity(intent)
            } else {
                val data = bundle ?: Bundle()
                data.putBoolean(USER_NEED_AUTH, true)
                intent.putExtras(data)
                (context as Activity).startActivityForResult(intent, requestCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
    }

}
