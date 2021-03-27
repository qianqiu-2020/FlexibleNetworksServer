package com.company;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/*此类用于管理数据库，以及封装一些常用操作*/
public class Database
{

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://119.45.115.128:3306/mysql?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static final String USER = "root";
    static final String PASS = "0.1.2.3.";
    private Connection conn = null;
    private Statement stmt = null;
    public Database() {
        conn = null;
        stmt = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            // 执行查询
            System.out.println("实例化Statement对象...");
            stmt = conn.createStatement();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }

    }
    public List<User> getOnlineList(){
        List<User> onlineList=new ArrayList<User>();
        String sql;
        try {
            sql = "SELECT id,name,ip,port FROM MySql.online_list";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                // 通过字段检索
                long id = rs.getInt("id");
                String name = rs.getString("name");
                String ip = rs.getString("ip");
                int port = rs.getInt("port");
                User result = new User(id, name, ip, port);
                onlineList.add(result);
            }
            rs.close();
            return onlineList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return onlineList;
        }
    }
    public SearchResult search(String search_name) {
        try {
            String sql;
            sql = "SELECT id, name, password FROM MySql.register_user_list where name = \"" +search_name+"\"";
            ResultSet rs = stmt.executeQuery(sql);
            if (!rs.next())
            {
                rs.close();
                SearchResult result=new SearchResult(0);
                return result;
            }
            else
             {
                // 通过字段检索
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                SearchResult result=new SearchResult(1,id,name,password);
                rs.close();
                return result;
            }
        } catch (SQLException se) {// 处理 JDBC 错误
            se.printStackTrace();
            SearchResult result=new SearchResult(0);
            return result;
        }
    }

    public void close(){
            // 关闭资源
        try{
            if(stmt!=null) stmt.close();
        }catch(SQLException se2){}// 什么都不做
        try{
            if(conn!=null) conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
        }
    public boolean add_register(String name,String password) {
        try {
            String sql;
            sql="insert into MySql.register_user_list (name,password) values("+"\""+name+"\""+","+"\""+password+"\""+")";
            //sql = "SELECT id, name, password FROM MySql.register_user_list where name = \"" +search_name+"\"";
            boolean rs = stmt.execute(sql);
            System.out.println(rs);
            return rs;
        } catch (SQLException se) {// 处理 JDBC 错误
            se.printStackTrace();
            SearchResult result=new SearchResult(0);
            return false;
        }
    }
    public boolean add_online(int id,String name,String ip,int port) {
        try {
            String sql;
            sql="insert into MySql.online_list (id,name,ip,port) values("+"\""+id+"\""+","+"\""+name+"\""+","+"\""+ip+"\""+","+"\""+port+"\""+")";
            //sql = "SELECT id, name, password FROM MySql.register_user_list where name = \"" +search_name+"\"";
            boolean rs = stmt.execute(sql);
            System.out.println(rs);
            return rs;
        } catch (SQLException se) {// 处理 JDBC 错误
            se.printStackTrace();
            SearchResult result=new SearchResult(0);
            return false;
        }
    }
    public boolean add_LBS(long id,String positon) {
        try {
            String sql;
            sql="insert into MySql.lbs (id,position) values("+"\""+id+"\""+","+"\""+positon+"\""+")";
            //sql = "SELECT id, name, password FROM MySql.register_user_list where name = \"" +search_name+"\"";
            boolean rs = stmt.execute(sql);
            System.out.println(rs);
            return rs;
        } catch (SQLException se) {// 处理 JDBC 错误
            se.printStackTrace();
            SearchResult result=new SearchResult(0);
            return false;
        }
    }
    public boolean delete_online(long id) {
        try {
            String sql;
            sql="delete from MySql.online_list where id = \""+id+"\"";
            //sql = "SELECT id, name, password FROM MySql.register_user_list where name = \"" +search_name+"\"";
            boolean rs = stmt.execute(sql);
            System.out.println(rs);
            return rs;
        } catch (SQLException se) {// 处理 JDBC 错误
            se.printStackTrace();
            SearchResult result=new SearchResult(0);
            return false;
        }
    }
}
