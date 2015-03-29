package com.example.asmack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
	private List<HashMap<String,Object>>list=new ArrayList<HashMap<String,Object>>();
	private LayoutInflater mInflater;
	public ChatAdapter(Context context,List<HashMap<String,Object>> list) {
		this.list=list;
		mInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vH;
		if(null == convertView){
			convertView=mInflater.inflate(R.layout.chat_item, null);
		}
		vH=(ViewHolder) convertView.getTag();
		if(null == vH){
			vH=new ViewHolder();
			vH.imLeft=(ImageView) convertView.findViewById(R.id.chat_left_bg);
			vH.imRight=(ImageView)convertView.findViewById(R.id.chat_right_bg);
			vH.tvLeft=(TextView)convertView.findViewById(R.id.chat_left_tv);
			vH.tvRight=(TextView)convertView.findViewById(R.id.chat_right_tv);
		}
		HashMap<String, Object>hm=list.get(position);
		int p=(Integer) hm.get("position");
		if(0==p){
			vH.imLeft.setVisibility(View.VISIBLE);
			vH.imRight.setVisibility(View.INVISIBLE);
			vH.tvLeft.setVisibility(View.VISIBLE);
			vH.tvRight.setVisibility(View.INVISIBLE);
			vH.tvLeft.setText((CharSequence) hm.get("msg"));
		}else{
			vH.imLeft.setVisibility(View.INVISIBLE);
			vH.imRight.setVisibility(View.VISIBLE);
			vH.tvLeft.setVisibility(View.INVISIBLE);
			vH.tvRight.setVisibility(View.VISIBLE);
			vH.tvRight.setText((CharSequence) hm.get("msg"));
		}
		return convertView;
	}
	private class ViewHolder{
		TextView tvLeft,tvRight;
		ImageView imLeft,imRight;
	};

}
