package com.pulmuone.OnlineIFServer.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/*
 * 	4.30 sim INSERT / UPDATE 수행시 이 클래스에서 connection 관련 수행을 진행한다.
 */

@Component
public class ConnectionCommon {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());   
    
    //Instance
    private static ConnectionCommon instance = new ConnectionCommon();

    //private construct
    private ConnectionCommon() {}
    
    public static ConnectionCommon getInstance() {
        return instance;
    }   
    
	public Connection beginTransaction(JdbcTemplate jdbcTemplate) throws IFException {
		Connection conn = null;
		try {
			conn = jdbcTemplate.getDataSource().getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			try { 
				conn.rollback(); 
				conn = null;
			}catch (SQLException e1) { 
				logger.error("롤백 에러");
				throw new IFException(ResponseStatus.FAIL, e1.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));				
			}
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
		return conn;
	}
	
	public void endTransaction(Connection connStack, boolean isSuccess) throws IFException {
		try {
			if(isSuccess)
				connStack.commit();
			else
				connStack.rollback();
			
			connStack.close();
		} catch (Exception e) {
			try { 
				logger.error("롤백 진행");
				connStack.rollback(); 
				if(! connStack.isClosed()) connStack.close();
			} catch (Exception e1) { 
				logger.error("롤백 에러");
				throw new IFException(ResponseStatus.FAIL, e1.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
			}
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
	}
	
	public int[] batchUpdate(Connection connStack, String sql, List<Object[]> batchArgs) throws IFException {
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connStack.prepareStatement(sql);
			for(Object[] args : batchArgs) {
				for(int i=1; i <= args.length; i++)
					preparedStatement.setObject(i, args[i-1]);

		        preparedStatement.addBatch();
		    }
			
			int[] affectedRecords = preparedStatement.executeBatch();
			
			return affectedRecords;
			
		} catch (Exception e) {
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		} finally {
		    if(preparedStatement != null)
		        try { preparedStatement.close(); } 
		    	catch (Exception e) { 
		        	throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'")); 
		        }
		}
	}
}
