package com.example.asmack;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.amack.tools.AmackManage;

public class MainActivity extends Activity implements OnClickListener {
	private EditText inputUserName, inputUserPwd;
	private Button btnLogin, btnRegister, btnDelete, btnCheck;
	private String nameStr, pwdStr;
	private AmackManage amackManager;
	private Handler mHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (AmackManage.checkConnection()) {
					AmackManage.addReconnectionListener();
				}
			}
		};
		amackManager = new AmackManage();
		new Thread() {
			public void run() {
				amackManager.connectServer();
				mHandler.sendEmptyMessage(0);
			};
		}.start();
		initView();
	}

	private void initView() {
		inputUserName = (EditText) findViewById(R.id.username);
		inputUserPwd = (EditText) findViewById(R.id.userpwd);
		btnLogin = (Button) findViewById(R.id.user_login);
		btnRegister = (Button) findViewById(R.id.user_register);
		btnDelete = (Button) findViewById(R.id.user_delete);
		btnCheck = (Button) findViewById(R.id.user_sendmsg);
		btnLogin.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		nameStr = inputUserName.getText().toString();
		pwdStr = inputUserPwd.getText().toString();
		Log.i("chen", "click");
		switch (v.getId()) {
		case R.id.user_delete:
			AmackManage.getOnLineMsg(getApplicationContext());
			break;
		case R.id.user_login:
			Log.i("chen", "click  login");
			new Thread() {
				public void run() {
					amackManager.login(nameStr, pwdStr);
				};
			}.start();
			break;
		case R.id.user_register:
			Log.i("chen", "click   register");
			new Thread() {
				public void run() {
					amackManager.register(nameStr, pwdStr);
				};
			}.start();
			break;
		case R.id.user_sendmsg:
			new Thread() {
				public void run() {
					AmackManage.sendTalkMsg("chen@win-cn8f96d0cqi", "hello chen!");
					Log.i("chen", "sent");
				};
			}.start();;
			break;
		default:
			break;
		}

	}

}
