package com.lrs.livepushapplication.activity.main.adapter

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseViewHolder
import com.hyrc99.a.watercreditplatform.activity.webview.WebviewActivity
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.activity.main.MainActivity
import com.lrs.livepushapplication.base.BaseAdapter
import com.lrs.livepushapplication.bean.LiveBean

/**
 * @description 作用:
 * @date: 2020/4/14
 * @author: 卢融霜
 */
class LiveListAdapter(context: Context, layoutId: Int) : BaseAdapter<LiveBean>(layoutId, context) {
    override fun itemInit(helper: BaseViewHolder?, item: LiveBean?) {
        var image = helper?.getView<ImageView>(R.id.ivLiveUrl);
        image?.tag = item;
        Glide.with(mContext).load(item?.url).into(image!!);
        image.setOnClickListener {
            var bunle = Bundle();
            var ben = it.tag as LiveBean;
            bunle.putString("loadUrl", "https://livechina.cctv.com/zby/index.shtml?spm=C04362.PAup3mg36SVX.ETYxjxdiAIXp.3&id=" + ben.id);
            bunle.putString("title", ben.name);
            (mContext as MainActivity).openAcitivty(WebviewActivity().javaClass, bunle)
        }
        if (item?.name == null || item?.name == "null" || item?.name.isEmpty()) {
            helper?.setText(R.id.tvLiveName, item?.author + "的直播间");
        } else {
            helper?.setText(R.id.tvLiveName, item?.name);
        }
        helper?.setText(R.id.tvAuthor, item?.author);
    }
}