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
		Log.i(tag, "����org.jivesoftware.smack.ReconnectionManager  �ɹ���");
	} catch (ClassNotFoundException e) {
		Log.i(tag, "����org.jivesoftware.smack.ReconnectionManager   ʧ��");
		e.printStackTrace();
	}
}
	// ���ӷ�����
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
		Log.i(tag, "��ʼ���ӷ�����");
		try {
			connection.connect();
			Log.i(tag, "���ӷ������ɹ�");
			return true;
		} catch (XMPPException e) {
			Log.i(tag, "���ӷ�����ʧ��");
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
	 * @return 0:Ϊ���ӷ�������1��ע��ɹ������ࣺʧ��
	 */
	public int register(String account, String password) {
		if (connection == null) {
			Log.i(tag, "reguster connection = null");
			return 0;
		}
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(connection.getServiceName());
		reg.setUsername(account);// ע������createAccountע��ʱ��������username������jid���ǡ�@��ǰ��Ĳ��֡�
		reg.setPassword(password);
		reg.addAttribute("android", "geolo_createUser_android");// ���addAttribute����Ϊ�գ������������������־��android�ֻ������İɣ���������
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = connection.createPacketCollector(filter);
		connection.sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// ֹͣ����results���Ƿ�ɹ��Ľ����
		if (result == null) {
			Log.i(tag, "No response from server.");
			return 0;
		} else if (result.getType() == IQ.Type.RESULT) {
			Log.i("chen", "ע��ɹ�");
			return 1;
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.i(tag, "IQ.Type.ERROR: " + result.getError().toString());// �û��Ѿ�����
				return 2;
			} else {
				Log.i(tag, "IQ.Type.ERROR: " + result.getError().toString());
				return 3;
			}
		}

	}

	/**
	 * ��¼
	 * 
	 * @param a
	 *            ��¼�ʺ�
	 * @param p
	 *            ��¼����
	 * @return
	 */
	public boolean login(String a, String p) {
		if (connection == null) {
			return false;
		}
		try {
			/** ��¼ */
			connection.login(a, p);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * �޸�����
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
	 * �����û�״̬
	 */
	public void setPresence(int code) {
		if (connection == null)
			return;
		Presence presence;
		switch (code) {
		case 0:
			presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			Log.v("state", "��������");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			connection.sendPacket(presence);
			Log.v("state", "����Q�Ұ�");
			System.out.println(presence.toXML());
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			connection.sendPacket(presence);
			Log.v("state", "����æµ");
			System.out.println(presence.toXML());
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			connection.sendPacket(presence);
			Log.v("state", "�����뿪");
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
			// ��ͬһ�û��������ͻ��˷�������״̬
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(connection.getUser());
			presence.setTo(StringUtils.parseBareAddress(connection.getUser()));
			connection.sendPacket(presence);
			Log.v("state", "��������");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			connection.sendPacket(presence);
			Log.v("state", "��������");
			break;
		default:
			break;
		}
	}

	/**
	 * ɾ����ǰ�û�
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
	 * ������Ϣ
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
	 * ��ȡ���ߵ���Ϣ
	 * 
	 * @return
	 */
	public static List<org.jivesoftware.smack.packet.Message> getOffLine() {
		List<org.jivesoftware.smack.packet.Message> msglist = new ArrayList<org.jivesoftware.smack.packet.Message>();
		OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);
		// ��ȡ������Ϣ,�߳����� ����Toast
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
				// ��������
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
	 * ��ȡ������Ϣ
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
	 * �ı��㲥
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
