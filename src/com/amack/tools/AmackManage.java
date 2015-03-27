package com.amack.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AmackManage {
	private static String tag = "chen";
	private static String serverAddress = "202.194.81.12";
	private static int serverPort = 5222;
	private static XMPPConnection connection = null;
public AmackManage() {
	try {
		Class.forName("org.jivesoftware.smack.ReconnectionManager");
		Log.i(tag, "加载org.jivesoftware.smack.ReconnectionManager  成功！");
	} catch (ClassNotFoundException e) {
		Log.i(tag, "加载org.jivesoftware.smack.ReconnectionManager   失败");
		e.printStackTrace();
	}
}
	// 连接服务器
	public static boolean connectServer() {
		ConnectionConfiguration config = new ConnectionConfiguration(serverAddress, serverPort);
		config.setReconnectionAllowed(true);
		config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
		config.setSASLAuthenticationEnabled(true);
		config.setTruststorePath("/system/etc/security/cacerts.bks");
		config.setTruststorePassword("changeit");
		config.setTruststoreType("bks");

		config.setSASLAuthenticationEnabled(false);
		connection = new XMPPConnection(config);
		Log.i(tag, "开始连接服务器");
		try {
			connection.connect();
			Log.i(tag, "连接服务器成功");
			return true;
		} catch (XMPPException e) {
			Log.i(tag, "连接服务器失败");
			e.printStackTrace();
		}

		
		return false;
	}
	public static void addReconnectionListener(){
		connection.addConnectionListener(new ConnectionListener() {

			@Override
			public void reconnectionSuccessful() {
				Log.i(tag, "reconnectionSuccessful");

			}

			@Override
			public void reconnectionFailed(Exception arg0) {
				Log.i(tag, "reconnectionFailed>>" + arg0.toString());

			}

			@Override
			public void reconnectingIn(int arg0) {
				Log.i(tag, "reconnectingIn>>>" + arg0);

			}

			@Override
			public void connectionClosedOnError(Exception arg0) {
				Log.i(tag, "connectionClosedOnError>>>" + arg0);

			}

			@Override
			public void connectionClosed() {
				Log.i(tag, "connectionClosed");

			}
		});

	}

	public static boolean checkConnection() {
		return connection.isConnected();
	}

	/**
	 * 
	 * @param account
	 * @param password
	 * @return 0:为连接服务器，1：注册成功，其余：失败
	 */
	public int register(String account, String password) {
		if (connection == null) {
			Log.i(tag, "reguster connection = null");
			return 0;
		}
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(connection.getServiceName());
		reg.setUsername(account);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
		reg.setPassword(password);
		reg.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = connection.createPacketCollector(filter);
		connection.sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		if (result == null) {
			Log.i(tag, "No response from server.");
			return 0;
		} else if (result.getType() == IQ.Type.RESULT) {
			Log.i("chen", "注册成功");
			return 1;
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.i(tag, "IQ.Type.ERROR: " + result.getError().toString());// 用户已经存在
				return 2;
			} else {
				Log.i(tag, "IQ.Type.ERROR: " + result.getError().toString());
				return 3;
			}
		}

	}

	/**
	 * 登录
	 * 
	 * @param a
	 *            登录帐号
	 * @param p
	 *            登录密码
	 * @return
	 */
	public boolean login(String a, String p) {
		if (connection == null) {
			return false;
		}
		try {
			/** 登录 */
			connection.login(a, p);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 修改密码
	 * 
	 * @param connection
	 * @return
	 */
	public static boolean changePassword(XMPPConnection connection, String pwd) {
		if (connection == null) {
			return false;
		}
		try {
			connection.getAccountManager().changePassword(pwd);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 更改用户状态
	 */
	public void setPresence(int code) {
		if (connection == null)
			return;
		Presence presence;
		switch (code) {
		case 0:
			presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			Log.v("state", "设置在线");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			connection.sendPacket(presence);
			Log.v("state", "设置Q我吧");
			System.out.println(presence.toXML());
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			connection.sendPacket(presence);
			Log.v("state", "设置忙碌");
			System.out.println(presence.toXML());
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			connection.sendPacket(presence);
			Log.v("state", "设置离开");
			System.out.println(presence.toXML());
			break;
		case 4:
			Roster roster = connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(connection.getUser());
				presence.setTo(entry.getUser());
				connection.sendPacket(presence);
				System.out.println(presence.toXML());
			}
			// 向同一用户的其他客户端发送隐身状态
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(connection.getUser());
			presence.setTo(StringUtils.parseBareAddress(connection.getUser()));
			connection.sendPacket(presence);
			Log.v("state", "设置隐身");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			connection.sendPacket(presence);
			Log.v("state", "设置离线");
			break;
		default:
			break;
		}
	}

	/**
	 * 删除当前用户
	 * 
	 * @param connection
	 * @return
	 */
	public static boolean deleteAccount() {
		try {
			connection.getAccountManager().deleteAccount();
			connection = null;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param to
	 * @param msg
	 */
	public static void sendTalkMsg(String to, String msg) {
		Chat chat = connection.getChatManager().createChat(to, null);
		try {
			chat.sendMessage(msg);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取离线的消息
	 * 
	 * @return
	 */
	public static List<org.jivesoftware.smack.packet.Message> getOffLine() {
		List<org.jivesoftware.smack.packet.Message> msglist = new ArrayList<org.jivesoftware.smack.packet.Message>();
		OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);
		// 获取离线消息,线程阻塞 不能Toast
		try {
			Iterator<org.jivesoftware.smack.packet.Message> it = offlineMessageManager.getMessages();
			while (it.hasNext()) {
				org.jivesoftware.smack.packet.Message message = it.next();
				msglist.add(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 设置在线
				Presence presence = new Presence(Presence.Type.available);
				connection.sendPacket(presence);
				offlineMessageManager.deleteMessages();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return msglist;
	}

	/**
	 * 获取在线消息
	 * 
	 * @return
	 */
	public static void getOnLineMsg(final Context context) {
		ChatManager cm = connection.getChatManager();
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat chat2, Message message) {
						Log.i(tag, "getMsg>>>>"+message.getBody());
						sendBroadcastMsg(context, message.getBody());
					}
				});
			}
		});
	}

	/**
	 * 文本广播
	 * 
	 * @param context
	 */
	private static void sendBroadcastMsg(Context context, String txt) {
		Intent intent = new Intent();
		intent.setAction("msg_receiver");
		intent.putExtra("msg", txt);
		context.sendBroadcast(intent);
	}

}
