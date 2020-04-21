package com.lrs.livepushapplication.activity.main.item

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.activity.login.LoginActivity
import com.lrs.livepushapplication.activity.main.MainActivity
import com.lrs.livepushapplication.base.LazyLoadingFragment
import com.lrs.livepushapplication.utils.sharedpreferences.SharedPreferencesHelper
import com.xuexiang.xui.XUI
import java.util.*

/**
 * @description 作用:
 * @date: 2020/3/27
 * @author: 卢融霜
 */
class MyselfFragment : LazyLoadingFragment(){

    companion object {
        lateinit var fragment: MyselfFragment;
        fun newInstance(): MyselfFragment? {
            val args = Bundle()
            fragment =
                MyselfFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun init(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        root_view.findViewById<View>(R.id.btOuLogin).setOnClickListener {
            doOut();
        };
    }

    override fun onFirstVisibleToUser(): Boolean {
        return false;
    }

    override fun onInvisibleToUser() {
    }

    override fun getLayRes(): Int {
        return R.layout.fragment_myself;
    }

    override fun onVisibleToUser() {
        (activity as MainActivity).setTitle(false, "我的");
    }


    /**
     * 登出
     */
    fun doOut() {
        SharedPreferencesHelper.setPrefString(activity, "userId", null)
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                activity?.finish()
            }
        }
        val timer = Timer()
        timer.schedule(task, 1 * 1000.toLong())
    }

}