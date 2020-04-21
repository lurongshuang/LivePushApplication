package com.lrs.livepushapplication.activity.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gyf.barlibrary.ImmersionBar
import com.lrs.livepushapplication.activity.login.model.LoginModel
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.databinding.Login
import com.lrs.livepushapplication.utils.sharedpreferences.SharedPreferencesHelper


class LoginActivity : AppCompatActivity() {
    private lateinit var login: Login;
    private lateinit var lm: LoginModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        login = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initModel();
        //设置沉浸色为白色
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true).init();

    }

    private fun initModel() {
        lm = LoginModel(this);
        login.ln = lm;
    }
}
