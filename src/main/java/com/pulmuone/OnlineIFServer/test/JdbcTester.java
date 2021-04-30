package com.pulmuone.OnlineIFServer.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class JdbcTester {
	public static void main(String[] args) {
		if(args.length < 5) {
			System.out.println("Ussage: JdbcTester oracle.jdbc.driver.OracleDriver jdbc:oracle:thin:@//172.17.2.26:1551/mallif RELAY xxxx 10 \"select sysdate from dual\"");
			return; 
		}
		
		String JDBC_DRIVER = args[0];
		String DB_URL = args[1];
		String USER = args[2];
		String PASS = args[3];
		int maxCnt = Integer.parseInt(args[4]);
		String sql = args[5];
		
		if(sql.equals("-")) {
	        InputStream in = System.in;
	        InputStreamReader reader = new InputStreamReader(in);
	        BufferedReader br = new BufferedReader(reader);
	        StringBuffer sb = new StringBuffer();

	        try {
	        	String line = null;
        		System.out.print("sql> ");
	        	while(true) {
					line = br.readLine();
					if(line.equals("/")) {
						sql = sb.toString();
						break;
					}
					line = line.replaceAll("--.*", "");
					sb.append(line);
	        	}
			} catch (IOException e) {}
		}
		
		Connection conn = null;
		Statement stmt = null;
		///PreparedStatement pstmt = null;
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			///pstmt = conn.prepareStatement(sql);
			///ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int colcnt = rsmd.getColumnCount();

			for( int colidx = 1; colidx <= colcnt; ++colidx) {
				System.out.print("("+colidx+")"+rsmd.getColumnName(colidx)+"|");
			}
			System.out.println("<end-of-record>");
			System.out.println("-----------------------------------------------------");
			
			for (int i=0; rs.next() && i <maxCnt; i++) {
				for( int colidx = 1; colidx <= colcnt; ++colidx) {
					System.out.print("("+colidx+")"+rs.getObject(colidx)+"|");
				}
				System.out.println("<end-of-record>");
			}
			
			rs.close();
			stmt.close();
			///pstmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				///if (pstmt != null)
				///	pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		System.out.println("End!");
	}
}
