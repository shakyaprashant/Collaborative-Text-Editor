package server;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Create object obj
 * obj.setUsername (mysql ka, if different)
 * obj.setPassword (mysql ka, if different)
 * obj.setPortNumber (mysql ka, if different)
 * obj.create()---- This doesn't change the existing tables ----
 * obj.add/change/check()
 * obj.destroy() ***(only if you want to remove the database completely)**
 * obj.close()
 * 
 * 
 * 
 */

public class DataBaseManager {
	private String driver ="com.mysql.cj.jdbc.Driver";
	private String database ="jdbc:mysql://localhost:";
	private String username="root";
	private String password="password";
	private Connection con;
	private Statement st;
	
	public void updateConnection()throws Exception {
		Class.forName(driver);
		con= DriverManager.getConnection(database,username,password);
		st=con.createStatement();
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
			st.executeUpdate("create schema intercode");
			System.out.println("Schema created as intercode");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return true;
	}
	
	public void setUsername(String username) {
		this.username=username;
	}

	public void setPassword(String pass) {
		password=pass;
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
	
	public boolean checkuID(String str) {
		ResultSet rs;
		try {
			rs=st.executeQuery("select uID from userInfo where uID = "+str);
			while(rs.next()) {
				if(str.equals(rs.getString(1))) {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Error in checking userID present : "+e);
		}
		return false;
	}
	
	public boolean checkIntLogin(String uID, char pass[] ) {
		System.out.println("Login request to Interviewer "+uID+" pass= "+pass.toString());
		ResultSet rs;
		try {
			rs=st.executeQuery("select * from intInfo");
			while(rs.next()) {
				if(uID.equals(rs.getString(1))) {
					if(pass.equals(rs.getString(2))){
						System.out.println("Login successful to :"+rs.getString(1));
						return true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error in checking login : "+e);
		}
		System.out.println("Login unsuccessful to :"+uID+","+pass.toString());
		return false;
	}
	
	public boolean checkLogin(String uID, char pass[] ) {
		System.out.println("Login request to "+uID+" pass= "+pass.toString());
		ResultSet rs;
		try {
			rs=st.executeQuery("select * from userInfo");
			while(rs.next()) {
				if(uID.equals(rs.getString(1))) {
					if(pass.toString().equals(rs.getString(2))){
						System.out.println("Login successful to :"+rs.getString(1));
						return true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error in checking login : "+e);
		}
		System.out.println("Login unsuccessful to :"+uID+","+pass.toString());
		return false;
	}
	
	public boolean changeUserPassword(String uID, char pass[]) {
		try {
			st.executeUpdate("UPDATE userinfo SET password = '"+pass.toString()+"' WHERE uID = '"+uID+"'");
		}
		catch(Exception e) {
			System.out.println("Password change unsuccessful\n"+e);
			return false;
		}
		return true;
	}
	
	public boolean changeIntPassword(String uID, char oldP[],char newP[]) {
		try {
			if(checkLogin(uID, oldP)) {
				st.executeUpdate("UPDATE intInfo SET password = '"+newP.toString()+"' WHERE uID = '"+uID+"'");
			}
			else { return false;
			}
		}
		catch(Exception e) {
			System.out.println("Int Password change unsuccessful\n"+e);
			return false;
		}
		return true;
	}
	
	public void addInt(String I_UID, char pass[]) throws Exception {
		PreparedStatement ps= con.prepareStatement("insert into intInfo "
				+ "values (?,?) ");
		ps.setString(1, I_UID);
		ps.setString(2, pass.toString());
		ps.executeUpdate();
		System.out.println("Int added to database :"+I_UID);
	}
	
	public void addUser(String uID, String IntUID ,char pass[]) throws Exception {
		PreparedStatement ps = con.prepareStatement("INSERT INTO userinfo "
                + "values(?,?,?,?,?) ");
		ps.setString(1, uID);
		ps.setString(3, IntUID);
		ps.setInt(4, 0);
		ps.setString(5, "");
		ps.setString(2, pass.toString());
		ps.executeUpdate();		
		System.out.println("user added to database :"+uID);
	}
	
	public void destroy() {
		try {
			updateConnection();
		} catch (Exception e) {
			System.out.println(e);
		}
		try {
			st.executeUpdate("drop Database intercode");
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	public List<String> getInterviewee(String I_UID){
		List<String> list =new ArrayList<>();
		try {
			ResultSet rs=st.executeQuery("Select UserID from UserInfo where I_UID= '"+I_UID+"'");
			while(rs.next()) {
				list.add(rs.getString(1));
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return list;
	}
	
	public void addScore(String userID, String score, String comments) {
		try {
			st.executeUpdate("UPDATE userInfo SET score = '"+score+"', comments = '"+comments+"' WHERE userID = '"+userID+"'");
			System.out.println("");
		}
		catch(Exception e2) {
			System.out.println(e2);
		
		}
	}
	
	public void createIntInfo() throws Exception {
		st.executeUpdate("CREATE TABLE intInfo (userID VARCHAR(30), "
												+ " password VARCHAR(30), "
												+ " UNIQUE INDEX `userID_UNIQUE` (`userID` ASC) VISIBLE)");
	}
	
	public void createUserInfo() throws Exception {
		st.executeUpdate("CREATE TABLE userInfo (userID VARCHAR(30), "
												+ " password VARCHAR(30), "
												+ " I_UID VARCHAR(30), "
												+ " score int(10), "
												+ " comments VARCHAR(1000), "
												+ " UNIQUE INDEX `userID_UNIQUE` (`userID` ASC) VISIBLE)");
	}	
	
	public DataBaseManager(String username, String password, String port) {
		this.username=username;
		this.password=password;
		setPortNumber(port);
	}
	
	public boolean create() {
		destroy();
		if(!createSchema()) {
			return false;
		}
		try {
		createUserInfo();
		createIntInfo();
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
		try {
			updateConnection();
		} catch (Exception e) {
			System.out.println(e);
		}
		return true;
	}
}
