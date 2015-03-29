package com.example.asmack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ChatActivity extends Activity{
	private ListView lsvChat;
	private List<HashMap<String, Object>>listChat=new ArrayList<HashMap<String,Object>>();
	private ChatAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		initData();
		lsvChat=(ListView)findViewById(R.id.chat_lsv);
		adapter=new ChatAdapter(listChat, getApplicationContext());
		lsvChat.setAdapter(adapter);
	}
	
	private void initData(){
		HashMap<String,Object>hMLeft=new HashMap<String, Object>();
		hMLeft.put("position", 0);
		hMLeft.put("msg", "left");
		listChat.add(hMLeft);
		
		HashMap<String,Object>	hMRight=new HashMap<String, Object>();
		hMRight.put("position",1);
		hMRight.put("msg", "right");
		listChat.add(hMRight);
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMLeft));
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMRight));
		
		listChat.add(new HashMap<String, Object>(hMRight));
		
	}

}
