package com.byids.hy.testpro.utils;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *  ----------------------udp 用来获取主机ip --------------------------
 *
 * Created by gqgz2 on 2016/11/9.
 */

public class UDPSocket {

    public static final int DEFAULT_PORT = 57816;//端口号
    public static final String LOG_TAG = "WifiBroadcastActivity";
    public static final String TAG = "result";
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private static final int MAX_DATA_PACKET_LENGTH = 100;
    private String udpCheck = "";
    private String ip;    //接收到的ip地址
    private String[] ipList;       //因为一个端口下可能有不止一个主机，所以创建一个存放ip的数组
    private String sendContent;

    public UDPSocket(String sendContent) {
        this.sendContent = sendContent;
    }

    //测试  包装发送udp
    public void sendEncryptUdp(){
        String udpJson="{\"command\":\"find\",\"data\":{\"hid\":\""+sendContent+"\",\"loginName\":\"byids\"}}";
        Log.i("hy_udp_service", "test: ------------------"+udpJson);
        byte[] enByte = AES.encrpt(udpJson);//加密
        if (enByte == null)
            return;
        byte[] lengthByte = ByteUtils.intToByteBigEndian(enByte.length);
        byte[] headByte = new byte[8];
        for (int i = 0;i<headByte.length;i++) {
            headByte[i] = 0x50;
        }
        byte[] tailByte = new byte[4];
        tailByte[0] = 0x0d;
        tailByte[1] = 0x0a;
        tailByte[2] = 0x0d;
        tailByte[3] = 0x0a;

        byte[] sendByte = ByteUtils.byteJoin(headByte,lengthByte,enByte,tailByte);
        //String jiaMi = AES.byteStringLog(sendByte);
        //Log.i(TAG, "test: 发送加密的udp广播，以string[]的形式打印出来"+jiaMi);

        //发送udp广播
        BroadCastUdp udp = new BroadCastUdp(sendByte);
        udp.start();

    }

    //发送UDP广播
    public class BroadCastUdp extends Thread {
        DatagramPacket dataPacket = null;
        DatagramPacket receiveData= null;
        private byte[] dataByte;
        private DatagramSocket udpSocket;
        public BroadCastUdp(byte[] sendByte) {
            this.dataByte = sendByte;
        }
        public void run() {
            try {
                //udpSocket = new DatagramSocket(DEFAULT_PORT);
                if (udpSocket==null){
                    udpSocket = new DatagramSocket(null);
                    udpSocket.setReuseAddress(true);
                    udpSocket.bind(new InetSocketAddress(DEFAULT_PORT));
                }
                dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);       //发送数据
                receiveData= new DatagramPacket(buffer,MAX_DATA_PACKET_LENGTH);        //接收数据
                if (this.dataByte == null){
                    return;
                }
                byte[] data = this.dataByte;
                dataPacket.setData(data);
                dataPacket.setLength(data.length);
                dataPacket.setPort(DEFAULT_PORT);

                InetAddress broadcastAddr;
                broadcastAddr = InetAddress.getByName("255.255.255.255");
                dataPacket.setAddress(broadcastAddr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            // while( start ){
            try {
                udpSocket.send(dataPacket);
                sleep(10);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }


            for (int i=0;i<3;i++){
                try {
                    udpSocket.receive(receiveData);
                    //udpSocket.receive(receiveData);            //写几次就是用来区分同一个端口下的不同设备
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                    udpSocket.close();
                }

                if (null!=receiveData){

                    if( 0!=receiveData.getLength() ) {
                        String codeString = new String( buffer, 0, receiveData.getLength() );         //接收到的udp数据
                        udpCheck = codeString.substring(2,4);   //用来判断是否找到主机
                        Log.i("udp_search_ip", "接收到数据为: "+udpCheck);
                        try{
                            ip = receiveData.getAddress().getHostAddress();        //发送udp广播，收到的主机的ip地址
                        }catch (Exception e){
                            ip = "网络中断";
                        }
                        //Log.i("result","recivedataIP地址为："+receiveData.getAddress().toString().substring(1));//此为IP地址
                        Log.i("udp_search_ip", "run: "+ip+"----port"+receiveData.getPort());
                    }
                }else{
                    try {
                        udpSocket.send(dataPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }



            udpSocket.close();
        }
    }

    //获取主机ip地址
    public String getIp() {
        Log.i("udp_search_ip", "getIp: -----------------------主机ip："+ip);
        if (udpCheck.equals("ip")){
            return ip;
        }else {
            return "";
        }
    }

    //判断是否找到主机
    public String getUdpCheck() {
        return udpCheck;
    }
}
