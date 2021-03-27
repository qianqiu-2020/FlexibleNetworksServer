package com.company;
/*用户类，为在线列表中的一项*/
public class User {
    public long id;
    public String name;
    public String ip;
    public int port;
    public User(long id,String name,String ip,int port){
        this.id=id;
        this.name=name;
        this.ip=ip;
        this.port=port;
    }
}
