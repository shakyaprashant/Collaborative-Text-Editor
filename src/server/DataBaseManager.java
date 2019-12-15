package server;

import java.sql.*;

public class DataBaseManager {
    private String driver ="com.mysql.cj.jdbc.Driver";
    private String database ="jdbc:mysql://localhost:";
    private String username="root";
    private String password="password";
    private String port="3306";
    private Connection con;
    private Statement st;
    /*
    *  Table Name - interv fields- int_ID,username,password
    *  table name- users fields- user_ID, username, password, int_ID
    * */
    public void updateConnection()throws Exception {
        Class.forName(driver);
        con= DriverManager.getConnection("jdbc:mysql://localhost:3306",username,password);
        st = con.createStatement();
    }

    private void createConnection()throws Exception {
        Class.forName(driver);
        con= DriverManager.getConnection("jdbc:mysql://localhost:3306",username,password);
        st=con.createStatement();
    }

    private boolean createSchema() {
        try {
            createConnection();
        }catch(Exception e) {
            return false;
        }
        try {
            st.executeUpdate("create database intercode");
            System.out.println("Database created as intercode");
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return true;
    }
    public void setUsername(String username) {
        this.username=username;
    }
    public void setPortNumber(String port) {
        if(port==null||port.equals("")) {
            database="jdbc:mysql://localhost:"+"3306"+"/intercode";
        }
        else {
            database="jdbc:mysql://localhost:"+port+"/intercode";
        }
    }
    public Connection getConnection() {
        return con;
    }



    public void close() {
        try {
            con.close();
            st.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    //Specific to my tables
    public void createIntTable() throws SQLException{
        st.executeUpdate("create table if not exists interv (int_ID INT(10) PRIMARY KEY AUTO_INCREMENT," +
                " username VARCHAR(30) NOT NULL UNIQUE , password VARCHAR(30) NOT NULL);");
    }

    public void createUserTable() throws SQLException {
        st.executeUpdate("create table if not exists users (user_ID INT(10) PRIMARY KEY AUTO_INCREMENT," +
                " username VARCHAR(30) NOT NULL UNIQUE, password VARCHAR(30) NOT NULL," +
                " int_ID INT(10), FOREIGN KEY (int_ID) REFERENCES interv(int_ID));");
    }
    public void createDatabase() throws SQLException {
        st.executeUpdate("CREATE DATABASE IF NOT EXISTS softablitz");
        st.executeUpdate("USE softablitz");
    }
    public boolean usernameExistsInterv(String username) throws SQLException{
        ResultSet rs = st.executeQuery("SELECT count(*) FROM interv WHERE username = '" + username + "'");
        rs.next();
        if(rs.getInt(1) > 0)
            return true;
        return false;
    }

    public boolean addInterv(String username,String password) throws SQLException{
        int status = 0;
        if(!usernameExistsInterv(username))
        {
            status = st.executeUpdate("INSERT INTO interv (username, password) VALUES ('" + username + "', '" + password + "')");
        }
        else {
            System.out.println("Username " + username + " exists");
        }
        //System.out.println("addInterv status " + username + " " + status);
        if(status > 0)
            return true;
        return false;
    }
    public boolean usernameExistsUser(String username) throws SQLException{
        ResultSet rs = st.executeQuery("SELECT count(*) FROM users WHERE username = '" + username + "'");
        rs.next();
        if(rs.getInt(1) > 0)
            return true;
        return false;
    }
    public boolean addUser(String username,String password,String int_ID) throws SQLException{
        int status = 0;
        if(int_ID == null || int_ID == "-1")
        {
            System.out.println("Interviewer not exists");
            return false;
        }
        if(!usernameExistsUser(username)) {
            System.out.println("Adding User " + username + " " + password + " " + int_ID);
            status = st.executeUpdate(String.format("INSERT INTO users (username, password, int_ID) " +
                    "VALUES ('%s','%s', %s)",
                    username,password,int_ID));
        }
        else {
            System.out.println("Username Exists in the users table - "+ username);
        }
        if(status > 0)
            return true;
        return false;
    }
    public int getint_ID(String username) throws SQLException {
        ResultSet rs = st.executeQuery(String.format("SELECT int_ID from interv WHERE username = '%s'",username));
        if(rs.next())
            return rs.getInt(1);
        return -1;
    }
    public DataBaseManager(String username,String password,String port) {
        this.username = username;
        this.password = password;
        this.port = port;
    }
    public static void main(String[] args) throws Exception{
        DataBaseManager dataBaseManager = new DataBaseManager("root","20174071","3306");
        dataBaseManager.createConnection();
        dataBaseManager.createDatabase();
        dataBaseManager.createIntTable();
        dataBaseManager.createUserTable();
        System.out.println("Add interviewwer status - " + dataBaseManager.addInterv("prashant","prashant"));
        Integer int_ID = dataBaseManager.getint_ID("prashant");
        System.out.println(dataBaseManager.addUser("prashant_user","password",int_ID.toString()));
        dataBaseManager.close();
    }
}
