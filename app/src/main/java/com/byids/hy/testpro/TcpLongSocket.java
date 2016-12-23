package com.byids.hy.testpro;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/*
 TcpSocket
 */
public class TcpLongSocket {
	private String TAG = "recData_hy";
	private DataOutputStream out;   // 发送数据流
	private DataInputStream in;   // 接收数据流
	private Socket mSocket;  // socket连接对象
	private SocketAddress address;
	private int timeOut = 1000 * 30;// 延迟时间
	// 启动�?个线程，不停接收服务器数据
	private RecThrad recThread;// 接收数据线程
	private SendThread sendThread;// 发送数据线程
	private ConnectThread connThread;//连接线程
	private boolean threadBoo = true;
	private TCPLongSocketCallback callBack;// 回调接口
	private byte[] buffer = new byte[1024 * 100];// 缓冲区字节数组，信息不能大于此缓冲区
	private byte[] tmpBuffer;// 临时缓冲字节数组
	private static List<byte[]> datas = new ArrayList<byte[]>();// 待发送数据队列
	private TcpLongSocket tcplongSocket;

	// 构造函数
	public TcpLongSocket(TCPLongSocketCallback callback) {
		this.callBack = callback;
	}

	// 开始连接
	public void startConnect(String ip, int port) {
		// 启动一个接收线程
		// try {
		connThread = new ConnectThread(ip, port);
		connThread.start();
		Log.i(TAG, "startConnect: 连接上服务器，服务器ip："+ip+"   服务器port："+port);

		// } catch (IOException e) {
		// // 连接失败告诉调用者，重新连接
		// e.printStackTrace();
		// callBack.disconnect();
		// }
	}

	// 获取当前连接状态
	/*public boolean getConnectStatus() {
		if (mSocket != null){
			Log.i(TAG, "getConnectStatus: --------mSocket不等于空--------是否连接上-"+mSocket.isConnected());
			return mSocket.isConnected();
		} else{
			Log.i(TAG, "getConnectStatus: ------mSocket等于空-----");
			return false;
		}
	}*/

	// 获取当前连接状态
	public boolean getConnectStatus(){
		try{
			mSocket.sendUrgentData(0xFF);
			return true;
		}catch(Exception e){
			return false;
		}
	}


	private void sendData(byte[] data) {
		if (out != null) {
			try {
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
				callBack.disconnect();
			}
		}
	}

	public void writeDate(byte[] data) {
		Log.i("hy_result", "writeDate: "+byteStringLog(data));
		datas.add(data);      // 将发送数据添加到发送队列
	}

	//连接线程
	class ConnectThread extends Thread {
		String ip;
		int port;

		public ConnectThread(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		@Override
		public void run() {
			super.run();
			mSocket = new Socket();
			address = new InetSocketAddress(ip, port);
			try {
				mSocket.connect(address, timeOut);
				mSocket.isConnected();
				callBack.connected();
				out = new DataOutputStream(mSocket.getOutputStream());// 获取网络输出流
				in = new DataInputStream(mSocket.getInputStream());// 获取网络输入流
				threadBoo = true;
				recThread = new RecThrad();
				recThread.start();//打开接收数据线程
				sendThread = new SendThread();
				sendThread.start();//打开发送数据线程

			} catch (IOException e1) {
				Log.i("result", "fanliang......连接失败");
				e1.printStackTrace();
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
					if (mSocket != null && !mSocket.isClosed()) {   // 判断socket不为空并且是连接状态??
						mSocket.close();// 关闭socket
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
				if (callBack != null)
					callBack.disconnect();
			}
		}
	}


	//测试，用来显示byte[]
	private String byteStringLog(byte[] bs){
		String log = new String();
		for (int i = 0;i<bs.length;i++){
			int bi = (int)bs[i];
			log=log+" "+ String.valueOf(bi);
		}
		System.out.println(log);
		return log;
	}
	/**
	 * 发送数据线程
	 */
	class SendThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (threadBoo) {
				try {
					Thread.sleep(200);
					//Log.i("result", "run: --发送数据--发送数据--发送数据--发送数据--发送数据--");
				} catch (InterruptedException e) {
					close();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (datas.size() > 0) {
					byte[] data = datas.remove(0);
					Log.i("hy_result", "fanliang......发送数据 =" + new String(data));
					sendData(data);
				}
			}
			this.close();
		}

		public void close() {
			threadBoo = false;
		}
	}

	/**
	 * 接收数据线程 关闭资源 打开资源
	 */
	class RecThrad extends Thread {

		//用来拼接字符串
		private byte[] c;
		private byte[] allTmpBuffer = new byte[]{};
		private byte[] spliceBytes(byte[] a,byte[] b){
			if (a==null){
				a = new byte[]{};
			}
			c = new byte[a.length+b.length];
			System.arraycopy(a, 0, c, 0, a.length);
			System.arraycopy(b, 0, c, a.length, b.length);
			return c;
		}

		public void run() {
			super.run();
			while (threadBoo) {
				if (in != null) {
					int len = 0;
					try {
						int flag = 1;
						buffer = new byte[1024*100];
						while ((len = in.read(buffer)) > 0) {
							//sleep(10);
							Log.i(TAG, "run: ---------len------------"+len);
							tmpBuffer = new byte[len];
							System.arraycopy(buffer, 0, tmpBuffer, 0, len);
							//String recData = new String(tmpBuffer);   //接受的数据

							Log.i("result", "-------这是第"+flag+"段加密数据--------"+new String(tmpBuffer));
							if("Hello client".equals(new String(tmpBuffer))){
								Log.i("result", "--------心跳-------");
							}else {
								Log.i(TAG, "run: -------------------"+new String(tmpBuffer));
								//Log.i(TAG, "run: ！！！！！tmpBuffer"+byteStringLog(tmpBuffer));
								allTmpBuffer = spliceBytes(allTmpBuffer,tmpBuffer);
								if (len<1024*100){
									buffer = new byte[]{};
									Log.i(TAG, "run: @@@@@@@@@@@@@@len小于1024了@@@@@@@@@@@@@@@");
								}
							}
							flag++;
						}
						//拼接tmpBuffer
						if (allTmpBuffer==null){
							Log.i(TAG, "run: --------接收到的allTmpBuffer为空---------");
							//return;
						}else {
							//Log.i(TAG, "run: 跳出循环？？？？？？？？？？？？拼接后的byte[]:"+byteStringLog(allTmpBuffer));
						 	/*String strRoomInfo = testDecryptByte(allTmpBuffer);
							LongLogCatUtil.logE("result",strRoomInfo);*/
							if (callBack!=null){
								callBack.receive(allTmpBuffer);
							}
							allTmpBuffer = null;
						}
						sleep(500);
						Log.i(TAG, "run: ----------循环读取数据--------");
					} catch (IOException e) {
						e.printStackTrace();
						try {
							if (out != null) {
								out.close();
							}
							if (in != null) {
								in.close();
							}
							if (mSocket != null && !mSocket.isClosed()) {  // 判断socket不为空并且是连接状态
								mSocket.close();// 关闭socket
							}
						} catch (Exception e2) {
							// TODO: handle exception
						}
						if (callBack != null)
               							callBack.disconnect();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}

		//
		/*public void close() {
			threadBoo = false;
			this.close();
		}*/
	}

	// 关闭打开的资源
	public void close() {
		threadBoo = false;
		try {
			// if (mSocket != null) {
			// if (!mSocket.isInputShutdown()) {
			// mSocket.shutdownInput();
			// }
			// if (!mSocket.isOutputShutdown()) {
			// mSocket.shutdownOutput();
			// }
			// }
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (mSocket != null && !mSocket.isClosed()) {// 判断socket不为空并且是连接状�??
				mSocket.close();// 关闭socket
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out = null;
			in = null;
			mSocket = null;// 制空socket对象
			recThread = null;
			sendThread = null;
			connThread = null;
			callBack = null;
		}
	}
}
