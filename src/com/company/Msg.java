package com.company;

//package com.example.flexiblenetworks;

/*该类用于解析与构造数据*/
public class Msg extends Object{
    /*点对点通信*/
    public static final int TYPE_RECEIVERD=0;
    public static final int TYPE_SENT=1;
    /*与主服务器通信*/
    public static final int TYPE_LOGIN=2;
    public static final int TYPE_KEEP_LOGIN=3;
    public static final int TYPE_QUIT=4;
    public static final int TYPE_SEND_BROADCAST=5;
    public static final int TYPE_RECEIVE_BROADCAST=6;
    public static final int TYPE_LOGIN_REGISTER=7;
    public static final int TYPE_ONLINE_LIST=8;
    public static final int TYPE_LBS=9;
    public static final int TYPE_GET_ONLINELIST=10;
    public static final int TYPE_TULING_OK=100;
    private int type;
    private long sender_id;
    private String content;
    public Msg(int type,long sender_id,String content){
        this.content=content;
        this.type=type;
        this.sender_id=sender_id;
    }
    //解析消息
    public Msg(String temp){
        String[] list=temp.split("\r\n");
        type=Integer.parseInt(list[0]);
        sender_id=Integer.parseInt(list[1]);
        content=list[2];
    }
    public String getContent(){
        return content;
    }
    public long getsender_id(){
        return sender_id;
    }
    public int getType(){
        return type;
    }

}
