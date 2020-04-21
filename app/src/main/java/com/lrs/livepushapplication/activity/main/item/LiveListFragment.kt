package com.lrs.livepushapplication.activity.main.item

import android.os.Bundle
import androidx.recyclerview.widget.*
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.activity.main.MainActivity
import com.lrs.livepushapplication.activity.main.adapter.LiveListAdapter
import com.lrs.livepushapplication.base.BaseAdapter
import com.lrs.livepushapplication.base.ListBaseFragment
import com.lrs.livepushapplication.bean.LiveBean
import com.lrs.livepushapplication.utils.net.CustomCallBack
import com.lrs.livepushapplication.utils.net.NetworkUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*


/**
 * @description 作用:
 * @date: 2020/3/27
 * @author: 卢融霜
 */
class LiveListFragment : ListBaseFragment() {
    var page = 1;

    companion object {
        lateinit var fragment: LiveListFragment;
        fun newInstance(): LiveListFragment? {
            val args = Bundle()
            fragment =
                    LiveListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    //    override fun initRefreshLayout(rc: RecyclerView) {
//        var layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
//        rc.layoutManager = layoutManager
//    }
    override fun loadData(adapter: BaseAdapter<*>?) {
        if (page == 1) {
            showLoading();
        }
        var url = "https://api.cntv.cn/list/getCboxLiveList?serviceId=cbox&type=zbzg&p=$page&n=18&cb=jQuery172006951352399237698_1587088108792&_=1587088108815";
        NetworkUtils.getInstance().get(url, object : CustomCallBack() {
            override fun onSuccess(call: Call?, result: String?) {
                var jsonObj = JSONObject(result?.substring((result?.indexOf("(") + 1), result?.lastIndexOf(")")));
                if (jsonObj != null) {
                    var arr = jsonObj.getJSONArray("liveList");
                    if (arr != null && arr.length() > 0) {
                        for (i in 0 until arr.length()) {
                            var objIt = arr.getJSONObject(i);
                            var id = objIt.getString("liveId");
                            var name = objIt.getString("covertitle");
                            var url = objIt.getString("coverimg");
                            var videoUrl = objIt.getString("detailUrl");
                            var author = objIt.getString("wxappname");
                            var ben = LiveBean(id, name, url, author, videoUrl);
                            (adapter as BaseAdapter<LiveBean>)?.addData(ben);
                        }
                        showContent();
                    } else {
                        showEmpty();
                    }
                }
            }

            override fun onError(call: Call?, e: IOException?) {
                showError {
                    loadData(adapter);
                }
            }
        })
//        var json = activity?.assets?.open("dataJson");
//        var jsonObject = JSONObject(convertStreamToString(json));
//        if (jsonObject != null) {
//            var listData = jsonObject.getJSONObject("data").getJSONObject("liveCardList").getJSONArray("list");
//            if (listData != null) {
//                for (i in 0 until listData.length()) {
//                    var objIt = listData.getJSONObject(i);
//                    var id = objIt.getString("id");
//                    var name = objIt.getString("caption");
//                    var url = objIt.getString("poster");
//                    var videoUrl = objIt.getJSONArray("playUrls").getJSONObject(0).getString("url")
//                    var author = objIt.getJSONObject("user").getString("name");
//                    var ben = LiveBean(id, name, url, author, videoUrl);
//                    (adapter as BaseAdapter<LiveBean>)?.addData(ben);
//                }
//                showContent();
//            } else {
//                showEmpty();
//            }
//        }
    }

    override fun onFirstVisibleToUser(): Boolean {

        return true;
    }

    override fun listOnLoadMore(refreshLayout: RefreshLayout?, recyclerView: RecyclerView?) {
        page += 1;
    }

    override fun listonRefresh(refreshLayout: RefreshLayout?, recyclerView: RecyclerView?) {
        page = 1;
    }

    override fun initAdapter(): BaseAdapter<*> {
        return LiveListAdapter(activity!!, R.layout.adapter_live_list_item);
    }

    override fun onInvisibleToUser() {
    }

    override fun onVisibleToUser() {
        (activity as MainActivity).setTitle(false, "观看");
    }


    private fun convertStreamToString(inp: InputStream?): String? {
        var str: String? = "";
        var sacnner = Scanner(inp, "UTF-8").useDelimiter("\\A");
        if (sacnner.hasNext()) {
            str = sacnner.next();
        }
        sacnner.close();

        return str;
    }

}