package com.company;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*TCP网络线程。同时服务器启动的main函数也在这里面*/
public class Main extends Thread
{
    private ServerSocket serverSocket;
    private int port;

    public static Database db;
    Socket server;

    public Main(int port) throws IOException {
        this.port=port;
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(100000);
        db=new Database();
    }
    /*TCP网络线程*/
    public void run()
    {
        while(true)
        {
            try
            {
                System.out.println("等待远程连接，端口号为：" + serverSocket.getLocalPort() + "...");
                server = serverSocket.accept();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                System.out.println("【"+df.format(new Date())+"】");// new Date()为获取当前系统时间
                System.out.println("远程主机地址："+server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                System.out.println("test");
                String temp=in.readUTF();
                System.out.println("test1");
                if(!temp.isEmpty())//防止出现空包导致卡死？
                {parsemsg(temp);
                    System.out.println("test2");}
                System.out.println("test3");
                server.close();
                System.out.println("【end】");// new Date()为获取当前系统时间
            }catch(SocketTimeoutException s)
            {
                System.out.println("Socket timed out!");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                System.out.println("test【"+df.format(new Date())+"】");// new Date()为获取当前系统时间
                //break;
            }catch(EOFException e)
            {
                //EOFException
                e.printStackTrace();
            }catch(SocketException e)
            {
                //Connection reset
                /*一端退出，但退出时并未关闭该连接，另一端如果在从连接中读数据则抛出该异常（Connection reset）。简单的说就是在连接断开后的读和写操作引起的。*/
                e.printStackTrace();
            }catch(IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }

    /*程序从这里启动*/
    public static void main(String [] args) throws IOException {
        int port = 12000;
        Thread t = new Main(port);//实例化TCP线程，同时数据库类也在里面被实例化
        NetThreadUDP threadUDP=new NetThreadUDP();//实例化UDP线程
        t.start();
        System.out.println("登录线程已启动");
        //new Thread(threadUDP).start();
        //System.out.println("在线列表线程已启动");
    }

    /*解析并处理客户端发来的消息*/
    public void parsemsg(String temp) throws IOException {
        Msg msg=new Msg(temp);//解析出一个Msg类型对象
        switch (msg.getType()){
            case Msg.TYPE_LOGIN://登录消息
            {
                String[] list=msg.getContent().split("@@");
                String account=list[0];
                String password=list[1];

                /*在数据库中验证*/
                SearchResult result=db.search(account);
              if (result.success==0) {
                  //账号不存在，请注册
                  System.out.println("未找到" );
                  Msg reply=new Msg(2,0,"1112");
                  DataOutputStream out = new DataOutputStream(server.getOutputStream());
                  out.writeUTF(reply.getType()+"\r\n"+reply.getsender_id()+"\r\n"+reply.getContent());
              }
              else{
                if(result.password.equals(password)){//密码正确，登录成功
                    System.out.println("登录成功" );
                    db.add_online(result.id,result.name,server.getInetAddress().toString().substring(1),server.getPort());
                    Msg reply=new Msg(2,0,"1111"+"@@"+result.id);
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF(reply.getType()+"\r\n"+reply.getsender_id()+"\r\n"+reply.getContent());
                  }//密码错误
                  else {
                    System.out.println("密码错误" );
                    System.out.println("正确密码"+result.password+"错误密码"+password);
                    Msg reply = new Msg(2, 0, "1113");
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF(reply.getType() + "\r\n" + reply.getsender_id() + "\r\n" + reply.getContent());
                }
              }
              break;
            }
            case Msg.TYPE_LOGIN_REGISTER:{
                String[] list=msg.getContent().split("@@");
                String account=list[0];
                String password=list[1];

                /*在数据库中验证*/
                SearchResult result=db.search(account);
                if (result.success==0) {
                    //账号不存在，注册
                    System.out.println("注册账号" );
                    if(!db.add_register(account,password)) {
                        System.out.println("注册成功");
                        Msg reply = new Msg(2, 0, "1114");
                        DataOutputStream out = new DataOutputStream(server.getOutputStream());
                        out.writeUTF(reply.getType() + "\r\n" + reply.getsender_id() + "\r\n" + reply.getContent());
                    }
                    else
                        System.out.println("注册失败");
                    }
                else{//该账号已注册，请登录
                        System.out.println("已注册" );
                        Msg reply=new Msg(2,0,"1115");
                        DataOutputStream out = new DataOutputStream(server.getOutputStream());
                        out.writeUTF(reply.getType()+"\r\n"+reply.getsender_id()+"\r\n"+reply.getContent());
                }
                break;
            }
            case Msg.TYPE_QUIT:{
                System.out.println("ID:"+msg.getsender_id()+" 退出登录");
                db.delete_online(msg.getsender_id());
                break;
            }
            case Msg.TYPE_SEND_BROADCAST:{
                System.out.println("收到广播请求");
                Msg reply=new Msg(Msg.TYPE_RECEIVE_BROADCAST,0,"这是测试版\n你好\n2021新年快乐呀！\n最新版本为v0.11");
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(reply.getType()+"\r\n"+reply.getsender_id()+"\r\n"+reply.getContent());
                break;//break不能忘！！！
            }
            case Msg.TYPE_LBS:{
                System.out.println("收到位置信息,发送者id"+msg.getsender_id()+"\n位置：\n"+msg.getContent());
                if(!msg.getContent().contains("null"))
                db.add_LBS(msg.getsender_id(),msg.getContent());
                break;
            }
            case  Msg.TYPE_GET_ONLINELIST:{
                System.out.println("收到在线列表请求");
                /*获取在线列表*/
                List<User> list=Main.db.getOnlineList();
                Msg reply;
                if(!list.isEmpty())
                {
                    String data="";
                    for (User templist:list) {
                        data=data+templist.id+"|"+templist.name+"|"+templist.ip+"|"+templist.port+"\n";
                    }
                reply=new Msg(Msg.TYPE_GET_ONLINELIST,0,data);
                }else reply=new Msg(Msg.TYPE_GET_ONLINELIST,0,"no user online");

                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(reply.getType()+"\r\n"+reply.getsender_id()+"\r\n"+reply.getContent());
                break;//break不能忘！！！
            }

            default:

        }
    }
}
