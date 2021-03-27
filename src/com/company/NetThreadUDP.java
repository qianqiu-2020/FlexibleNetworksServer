package com.company;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
/*UDP网络线程*/
public class NetThreadUDP implements Runnable{
    /*发送UDP数据报*/
    public void sendUdpData(Msg msg,String ip,int port) {
        try{
            /*UDP发送消息*/
            // 创建发送端Socket, 绑定本机IP地址, 绑定任意一个未使用的端口号
            DatagramSocket socket = new DatagramSocket(13000);
            // 创建发送端Packet, 指定数据, 长度, 地址, 端口号
            String temp = msg.getType() + "\r\n" + msg.getsender_id() + "\r\n" + msg.getContent();
            DatagramPacket packet = new DatagramPacket(temp.getBytes("UTF-8"), temp.getBytes().length, InetAddress.getByName(ip), port);
            // 使用Socket发送Packet
            socket.send(packet);
            // 关闭Socket
            socket.close();
        } catch (SocketException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*UDP网络线程*/
    @Override
    public void run() {
        while (true){
            //获取在线列表
            List<User> list=Main.db.getOnlineList();
            if(!list.isEmpty())
            {
                String data="";
            for (User temp:list) {
                data=data+temp.id+"|"+temp.name+"|"+temp.ip+"|"+temp.port+"\n";
            }
            System.out.println("在线列表如下:\n"+data);
            // 发送

            for (User temp:list) {
                System.out.println("在线列表已发送给"+temp.ip+" "+temp.port);
                Msg msg=new Msg(Msg.TYPE_ONLINE_LIST,0,data);
                sendUdpData(msg,temp.ip,temp.port);
            }
            }
            //延迟10s
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
