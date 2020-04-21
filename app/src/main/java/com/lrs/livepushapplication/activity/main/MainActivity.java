package com.lrs.livepushapplication.activity.main;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import butterknife.BindView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lrs.livepushapplication.R;
import com.lrs.livepushapplication.activity.live.LiveConfigActivity;
import com.lrs.livepushapplication.activity.login.LoginActivity;
import com.lrs.livepushapplication.activity.main.adapter.MyFragmentPagerAdapter;
import com.lrs.livepushapplication.activity.main.item.CreateLiveFragment;
import com.lrs.livepushapplication.activity.main.item.LiveListFragment;
import com.lrs.livepushapplication.activity.main.item.MyselfFragment;
import com.lrs.livepushapplication.application.Application;
import com.lrs.livepushapplication.base.BaseActivity;
import com.lrs.livepushapplication.utils.sharedpreferences.SharedPreferencesHelper;
import com.lrs.livepushapplication.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    @BindView(R.id.main_viewPager)
    NoScrollViewPager viewPager;
    @BindView(R.id.mRadioGroupId)
    RadioGroup radioGroup;
    @BindView(R.id.radio0)
    RadioButton radioButton0;
    @BindView(R.id.radio1)
    RadioButton radioButton1;
    @BindView(R.id.radio2)
    RadioButton radioButton2;
    /**
     * 用户id
     */
    public String userId;
    private String userName;
    private String userURL;
    /**
     * handler
     */
    private Handler handler;

    /**
     * 消息监听
     */
    /**
     * 消息管理
     */
    /**
     * 消息客户端
     */

    /**
     * pageList
     *
     * @return
     */
    private List<Fragment> allFragment;

    @Override
    protected int loadView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        userId = SharedPreferencesHelper.getPrefString(this, "userId", null);
        userName = SharedPreferencesHelper.getPrefString(this, "userName", null);
        userURL = SharedPreferencesHelper.getPrefString(this, "userURL", null);
        if (userId == null) {
            Intent in = new Intent(this, LoginActivity.class);
            startActivity(in);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1 * 1000);
            return;
        }
        //初始化page
        initPage();
        //重新计算底部按钮大小
        initButton();
    }

    /**
     * 初始化page
     */
    LiveListFragment fragment_Item_1;
    CreateLiveFragment fragment_Item_2;

    private void initPage() {
        fragment_Item_1 = LiveListFragment.Companion.newInstance();
        fragment_Item_2 = CreateLiveFragment.Companion.newInstance();
        MyselfFragment fragment_Item_3 = MyselfFragment.Companion.newInstance();
        allFragment = new ArrayList<>();
        allFragment.add(fragment_Item_1);
        allFragment.add(fragment_Item_2);
        allFragment.add(fragment_Item_3);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), allFragment));
        viewPager.setCurrentItem(0);
    }

    /**
     * 初始化按钮
     */
    private void initButton() {
        RadioButton rab[] = {radioButton0, radioButton1, radioButton2};
        for (RadioButton radioButton : rab) {
            Drawable drs[] = radioButton.getCompoundDrawables();
            int w = (int) (drs[1].getMinimumWidth() / 5.6);
            int h = (int) (drs[1].getMinimumHeight() / 5.6);
            Rect r = new Rect(0, 0, w, h);
            drs[1].setBounds(r);
            radioButton.setCompoundDrawables(null, drs[1], null, null);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {
                    case R.id.radio0:
                        viewPager.setCurrentItem(0, false);
                        break;
                    case R.id.radio1:
                        viewPager.setCurrentItem(1, false);
                        break;
                    case R.id.radio2:
                        viewPager.setCurrentItem(2, false);
                        break;
                }
            }
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
//                if (Application.manager == null) {
//                    Application.manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                }
//                Application.manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    protected void clearData() {

    }

}
