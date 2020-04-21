package com.lrs.livepushapplication.activity.main.item

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import cn.bmob.v3.Bmob
import com.alivc.live.pusher.*
import com.alivc.live.pusher.a.i
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.activity.live.LiveConfigActivity
import com.lrs.livepushapplication.activity.live.LivePushActivity
import com.lrs.livepushapplication.activity.main.MainActivity
import com.lrs.livepushapplication.application.Application
import com.lrs.livepushapplication.application.Application.*
import com.lrs.livepushapplication.application.Application.Companion.mAlivcLivePushConfig
import com.lrs.livepushapplication.base.LazyLoadingFragment
import com.lrs.livepushapplication.utils.Common
import com.lrs.livepushapplication.utils.sharedpreferences.SharedPreferenceUtils
import com.xuexiang.xui.widget.alpha.XUIAlphaButton
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner
import java.io.File

/**
 * @description 作用:
 * @date: 2020/4/15
 * @author: 卢融霜
 */
class CreateLiveFragment : LazyLoadingFragment(), View.OnClickListener {
    @BindView(R.id.btnLiveGo)
    lateinit var xuiAlphaButton: XUIAlphaButton;

    @BindView(R.id.msUrl)
    lateinit var msUrl: MaterialSpinner;

    companion object {
        lateinit var fragment: CreateLiveFragment;
        fun newInstance(): CreateLiveFragment? {
            val args = Bundle()
            fragment =
                    CreateLiveFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun init(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) {
        xuiAlphaButton.setOnClickListener(this);
    }

    override fun onFirstVisibleToUser(): Boolean {
        return false;
    }

    override fun onInvisibleToUser() {

    }

    override fun getLayRes(): Int {

        return R.layout.fragment_create_live;
    }

    override fun onVisibleToUser() {
        (activity as MainActivity).setTitle(false, "直播", getString(R.string.iconshezhi)) {
            openActivity(LiveConfigActivity().javaClass);
        };
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLiveGo -> {
                Application.application?.initAliManger();
                LivePushActivity.startActivity(activity!!, mAlivcLivePushConfig, msUrl.text.toString())
            }
        }
    }

}