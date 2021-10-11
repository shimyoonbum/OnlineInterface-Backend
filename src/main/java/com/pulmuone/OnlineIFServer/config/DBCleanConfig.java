package com.pulmuone.OnlineIFServer.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Component
@ConfigurationProperties(prefix="dbclean")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBCleanConfig {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String PREV_TIME_TEMP = "prevTime";

	//dbclean info
	public static final String ENABLED = "enabled";
	public static final String TABLES = "tables";
	public static final String BACKUP = "backup";
	public static final String DELETE = "delete";
	public static final String FLAG = "flag";
	public static final String PERIOD = "period";
	public static final String LOCK_WAIT = "lock_wait";
	public static final String BACKUP_TABLE = "backup_table";

	//tables map
	public static final String TABLE = "table";
	public static final String COLUMN = "column";
	public static final String FORMAT = "format";
	public static final String TYPE = "type";	//column type
	public static final String USE = "use";	//Y(default)/N

	//period unit
	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DAY = "day";
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	public static final String SECOND = "second";

	//column type
    public static final String DATE = "date";
	public static final String TEXT = "text";

	private Map info;

    private Connection conn = null;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private class Expiry {
		int interval = 0;
		String unit = null;
		
		public Expiry(String expiry) {
			if(expiry==null)
				return;
			String[] arr = expiry.split(" |\t");
			interval = Integer.parseInt(arr[0]);
			unit = arr[1].trim();
		}
		
		public int getInterval() { return interval; }
		public String getUnit() { return unit; }
    }
 
    public void clearTables() {
		Map infoMap = this.info;
		if(infoMap == null)
			return;

		boolean active = (Boolean) infoMap.get(ENABLED);
		if(!active)
			return;
		
		Map cleanTables = (Map) infoMap.get(TABLES);
		Expiry backupCommon = new Expiry((String) infoMap.get(BACKUP));
		Expiry deleteCommon = new Expiry((String) infoMap.get(DELETE));
		Expiry periodCommon = new Expiry((String) infoMap.get(PERIOD));
		int lockWait = (int) infoMap.get(LOCK_WAIT);
		
		if(!lockBackup(lockWait))
			return;
		
		Calendar backupBoundaryCommon = setBoundaryTime(backupCommon);
		Calendar deleteBackupBoundary = setBoundaryTime(deleteCommon);
		Calendar backupBoundary = null;
		
		for(int i=0; i < cleanTables.size(); i++) {
			Map tableMap = (Map) cleanTables.get(""+i);
			String use = (String)tableMap.get(USE);
			if(use != null && use.equals("N"))
				continue;
			
			Expiry backupExpiry = new Expiry((String) tableMap.get(BACKUP));
			Expiry periodExpiry = new Expiry((String) tableMap.get(PERIOD));
			
			if(periodExpiry.getUnit() == null || periodExpiry.getUnit().equals("")) {
				if(!checkBoundaryTime(tableMap, periodCommon))
					continue;
				backupBoundary = backupBoundaryCommon;
			} else {
				if(!checkBoundaryTime(tableMap, periodExpiry))
					continue;				
				backupBoundary = setBoundaryTime(backupExpiry);
			}
			
			try {
				beginTransaction();
				backupTable(tableMap, backupBoundary);
				endTransaction(true);
			} catch (Exception e) {
				endTransaction(false);
				logger.error(e.getMessage());
			}
		} //for
		
		Map backupTable = (Map) infoMap.get(BACKUP_TABLE);
		if(backupTable==null) {
			backupTable = new HashMap();
			infoMap.put(BACKUP_TABLE, backupTable);
		}
		if(checkBoundaryTime(backupTable, periodCommon)) {
			try {
				beginTransaction();
				deleteBackupTable(deleteBackupBoundary);
				endTransaction(true);
			} catch (Exception e) {
				endTransaction(false);
				logger.error(e.getMessage());
			}
		}
		
		unlockBackup();

	} //clearTables

	private boolean lockBackup(int wait) {
		Calendar boundary = Calendar.getInstance();
		boundary.add(Calendar.SECOND, - wait);
		String periodBoundary = String.format("%04d%02d%02d%02d%02d%02d", boundary.get(Calendar.YEAR), boundary.get(Calendar.MONTH)+1, boundary.get(Calendar.DAY_OF_MONTH), boundary.get(Calendar.HOUR_OF_DAY),boundary.get(Calendar.MINUTE),boundary.get(Calendar.SECOND) );

		String tableName = "ifbackup_lock";
		String columnName = "lock_date";
		String format = "yyyymmddhh24miss";

		String query = "select "+columnName+" from "+tableName+" where "+columnName+" > to_date('"+periodBoundary+"','"+format+"') ";
		
		List<Map<String, Object>> resList = jdbcTemplate.queryForList(query);
		//logger.info(query+"; "+"->"+resList.toString());
		if(resList.size() > 0)
			return false;
		
		query = "insert into "+tableName+" ("+columnName+") values (sysdate)";
		
		jdbcTemplate.execute(query);
		
		return true;
	}
	
	private void unlockBackup() {
		String tableName = "ifbackup_lock";
		String query = "delete from "+tableName;
		
		jdbcTemplate.execute(query);
	}
	
	private boolean checkBoundaryTime(Map tableMap, Expiry expiry) {
		Calendar now = Calendar.getInstance();
		Calendar prevTime = (Calendar) tableMap.get(PREV_TIME_TEMP);
		if(prevTime==null) {
			tableMap.put(PREV_TIME_TEMP, now);
			return true;
		}
		
		Calendar nextTime = (Calendar) prevTime.clone();
		if(expiry.getUnit().equals(YEAR))
			nextTime.add(Calendar.YEAR, expiry.getInterval());
		else if(expiry.getUnit().equals(MONTH))
			nextTime.add(Calendar.MONTH, expiry.getInterval());
		else if(expiry.getUnit().equals(DAY))
			nextTime.add(Calendar.DAY_OF_MONTH, expiry.getInterval());
		else if(expiry.getUnit().equals(HOUR))
			nextTime.add(Calendar.HOUR_OF_DAY, expiry.getInterval());
		else if(expiry.getUnit().equals(MINUTE))
			nextTime.add(Calendar.MINUTE, expiry.getInterval());
		else if(expiry.getUnit().equals(SECOND))
			nextTime.add(Calendar.SECOND, expiry.getInterval());
		else {
			logger.error("Unknown period unit("+expiry.getUnit()+") error!");
			return false;
		}
		
		if(now.compareTo(nextTime) > 0) {
			tableMap.put(PREV_TIME_TEMP, now);
			return true;
		}
		
		return false;
	}
	
	private Calendar setBoundaryTime(Expiry expiry) {
		Calendar boundary = Calendar.getInstance();
		if(expiry.getUnit().equals(YEAR))
			boundary.add(Calendar.YEAR, - expiry.getInterval());
		else if(expiry.getUnit().equals(MONTH))
			boundary.add(Calendar.MONTH, - expiry.getInterval());
		else if(expiry.getUnit().equals(DAY))
			boundary.add(Calendar.DAY_OF_MONTH, - expiry.getInterval());
		else if(expiry.getUnit().equals(HOUR))
			boundary.add(Calendar.HOUR_OF_DAY, - expiry.getInterval());
		else if(expiry.getUnit().equals(MINUTE))
			boundary.add(Calendar.MINUTE, - expiry.getInterval());
		else if(expiry.getUnit().equals(SECOND))
			boundary.add(Calendar.SECOND, - expiry.getInterval());
		
		return boundary;
	}
	
	private String setCleanPeriodBoundary(Calendar boundary, String format) {
		String periodBoundary = null;

		if(format.equals("yymmdd"))
			periodBoundary = String.format("%02d%02d%02d", boundary.get(Calendar.YEAR),boundary.get(Calendar.MONTH)+1,boundary.get(Calendar.DAY_OF_MONTH) );
		else if(format.equals("yyyymmdd"))
			periodBoundary = String.format("%04d%02d%02d", boundary.get(Calendar.YEAR),boundary.get(Calendar.MONTH)+1,boundary.get(Calendar.DAY_OF_MONTH) );
		else if(format.equals("yyyymmddhh24miss"))
			periodBoundary = String.format("%04d%02d%02d%02d%02d%02d", boundary.get(Calendar.YEAR), boundary.get(Calendar.MONTH)+1, boundary.get(Calendar.DAY_OF_MONTH), boundary.get(Calendar.HOUR_OF_DAY),boundary.get(Calendar.MINUTE),boundary.get(Calendar.SECOND) );
		else {
			logger.error("clearTables>setCleanPeriodBoundary("+periodBoundary+") check error! Unknown format("+format+")!");
			return null;
		}
		return periodBoundary;
	}

	private void backupTable(Map tableMap, Calendar boundary) throws Exception {
		String tableName = tableMap.get(TABLE).toString();
		String columnName = tableMap.get(COLUMN).toString();
		String format = tableMap.get(FORMAT).toString();
		String type = tableMap.get(TYPE).toString();		

		String periodBoundary = setCleanPeriodBoundary(boundary, format);
		if(periodBoundary == null || periodBoundary.equals("null")) {
			logger.error("Table backup periodBoundary("+periodBoundary+") check error!");
			return;
		}
		
		String queryBackup = null;		
		String backup = null;
		String queryDelete = null;
		
		if(tableMap.get(BACKUP) != null) {
			
			backup = tableMap.get(BACKUP).toString();
			if(!backup.equals("0 day")) {
				
				queryBackup = buildBackupSql(tableName, columnName, format, type, periodBoundary);
			    logger.debug(queryBackup);
				
				///jdbcTemplate.execute(queryBackup);
				batchUpdate(queryBackup, new ArrayList<Object[]>());
			}
		}else {
			queryBackup = buildBackupSql(tableName, columnName, format, type, periodBoundary);
		    logger.debug(queryBackup);
			
			///jdbcTemplate.execute(queryBackup);
			batchUpdate(queryBackup, new ArrayList<Object[]>());
		}		
		
		if(type.equals(TEXT))
			queryDelete = "delete from "+tableName+" where "+columnName+" < '"+periodBoundary+"'";
		else if(type.equals(DATE)) {
			//6-14 sim 배송권역 관련 FLAG 값이 초기화 된 데이터만 삭제되도록 설정 추가.
			if(tableMap.get(FLAG) != null) {
				queryDelete = "delete from "+tableName+" where "+columnName+" < to_date('"+periodBoundary+"','"+format+"') and itf_flg = 'Y'";			
			}else {
				queryDelete = "delete from "+tableName+" where "+columnName+" < to_date('"+periodBoundary+"','"+format+"')";	
			}			
		}
			
		logger.info(queryDelete+"; ");
		
		///jdbcTemplate.execute(queryDelete);
		batchUpdate(queryDelete, new ArrayList<Object[]>());

	} //backupTable

	private String buildBackupSql(String tableName, String columnName, String format, String type, String periodBoundary) {
	    StringBuffer sb = new StringBuffer();
	    StringBuffer jsonSB = new StringBuffer();
	    String colNm = null;
	    String colType = null;
	    
		List<Map<String, Object>> list = jdbcTemplate.queryForList("select column_name,data_type,nullable from user_tab_columns where table_name = '"+tableName.toUpperCase()+"'");

		boolean needComma= false;
		jsonSB.append("'{");
		for (Map<String, Object> rec : list) {
			colNm = rec.get("column_name").toString().toLowerCase();
	    	colType = rec.get("data_type").toString().toLowerCase();
	    
	    	if(needComma)
				jsonSB.append(",");
	    	needComma= true;
	    	//01-27 sim 로그 백업 처리할 떄 replace 처리 진행
	    	if(colType.equals("varchar2") || colType.equals("number") || colType.equals("nvarchar2") 
					|| colType.equals("char") || colType.equals("nchar") || colType.equals("clob")) {
	    		if(tableName.equalsIgnoreCase("iflog"))
	    			jsonSB.append("\""+colNm+"\":\"'||replace(replace("+colNm+",'\\',''),'\"', '\\\"')||'\"");
	    		else
	    			jsonSB.append("\""+colNm+"\":\"'||REGEXP_REPLACE("+ colNm + ", '['||chr(13)||chr(10)||CHR(9)||CHR(34)||CHR(92)||']+','')||'\"");
			} else if(colType.equals("date")) {
				jsonSB.append("\""+colNm+"\":\"'||to_char("+colNm+",'YYYYMMDDHH24MISS')||'\"");
			} else {
				jsonSB.append("\""+colNm+"\":'||"+colNm+"||'");
			}
		}
		jsonSB.append("}'");

	    sb.append("insert into ifbackup");
	    sb.append("(backup_date,tbl_nm,json) ");
	    sb.append("select sysdate,'"+tableName+"',");
	    sb.append(jsonSB.toString());
	    sb.append(" from "+tableName);
		if(type.equals(TEXT))
			sb.append(" where "+columnName+" < '"+periodBoundary+"'");
		else if(type.equals(DATE))
			sb.append(" where "+columnName+" < to_date('"+periodBoundary+"','"+format+"') ");
	   
	    return sb.toString();
	}
	
	private void deleteBackupTable(Calendar boundary) throws Exception {
		String tableName = "ifbackup";
		String columnName = "backup_date";
		String format = "yyyymmddhh24miss";

		String periodBoundary = setCleanPeriodBoundary(boundary, format);
		if(periodBoundary == null || periodBoundary.equals("null")) {
			logger.error("Backup delete periodBoundary("+periodBoundary+") check error!");
			return;
		}
		
		String query = "delete from "+tableName+" where "+columnName+" < to_date('"+periodBoundary+"','"+format+"') ";
		logger.info(query+"; ");
		
		///jdbcTemplate.execute(query);
		batchUpdate(query, new ArrayList<Object[]>());
	} //deleteBackupTable

	private void beginTransaction() throws IFException {
		try {
			conn = jdbcTemplate.getDataSource().getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			try { conn.rollback(); } catch (SQLException e1) { }
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
	}
	
	private void endTransaction(boolean isSuccess) {
		try {
			if(isSuccess)
				conn.commit();
			else
				conn.rollback();
			
			conn.close();
		} catch (SQLException e) {
			try { conn.rollback(); } catch (SQLException e1) { }
			///throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
	}
	
	private void batchUpdate(String sql, List<Object[]> batchArgs) throws IFException {
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = conn.prepareStatement(sql);
			for(Object[] args : batchArgs) {
				for(int i=1; i <= args.length; i++)
					preparedStatement.setObject(i, args[i-1]);

		        preparedStatement.addBatch();
		    }
			
			preparedStatement.execute();
			
			return;
		} catch (SQLException e) {
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		} finally {
		    if(preparedStatement != null)
		        try { preparedStatement.close(); } catch (SQLException e) { }
		}
	}

}
