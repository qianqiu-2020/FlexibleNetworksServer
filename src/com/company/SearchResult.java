package com.company;
/*用于查询数据库时一次返回多个值（正常只能返回一个对象），即查询结果类*/
public class SearchResult {
    public int success;
    public int id;
    public String name;
    public String password;
    public SearchResult(int success,int id,String name,String password){
        this.success=success;
        this.id=id;
        this.name=name;
        this.password=password;
    }
    public SearchResult(int success){
        this.success=success;
    }
}
