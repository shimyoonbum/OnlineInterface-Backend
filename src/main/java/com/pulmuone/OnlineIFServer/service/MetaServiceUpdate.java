package com.pulmuone.OnlineIFServer.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pulmuone.OnlineIFServer.common.ConnectionCommon;
import com.pulmuone.OnlineIFServer.common.Constants;
import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.MetaConfig;
import com.pulmuone.OnlineIFServer.config.SystemConfig;
import com.pulmuone.OnlineIFServer.util.CUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MetaServiceUpdate {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// Temporary
	private final static String tableTemp = "table";
	private final static String valueTemp = "value";
	private final static String restrictTemp = "restrict";
	private final static String restrictTemp2 = "restrict2";
	private final static String needRestrictTemp = "needRestrict";
	private final static String needRestrictTemp2 = "needRestrict2";
	private final static String joinTableTemp = "joinTable";
	private final static String joinCondTemp = "joinCond";
	private final static String joinTableListTemp = "joinTableList";
	private final static String conditionTemp = "condition";
	private final static String setColumnsTemp = "setColumns";
	private final static String whereColumnsTemp = "whereColumns";

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	SystemConfig systemConfig;

	private String flagOnly = null;

	public String[] updateChildMap(Map parentMap, Map metaMap, Map<String, Object> commonMap,
			Map<String, Object> paramMap) throws IFException {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		Connection connStack = null;
		ConnectionCommon connect = ConnectionCommon.getInstance();

		this.flagOnly = (String) metaMap.get(Constants.flagOnly);

		getSqlPart(parentMap, metaMap, commonMap, paramMap, resultMap);

		setChildSql(metaMap, resultMap);

		try {

			connStack = connect.beginTransaction(jdbcTemplate);
			updateChild(connect, connStack, metaMap, commonMap, paramMap, resultMap);
			connect.endTransaction(connStack, true);

		} catch (Exception e) {
			connect.endTransaction(connStack, false);
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		} finally {
			if (connStack != null) {
				connStack = null;
			}
		}

		resultMap.remove(restrictTemp);
		resultMap.remove(restrictTemp2);
		resultMap.remove(needRestrictTemp);
		resultMap.remove(needRestrictTemp2);
		resultMap.remove(valueTemp);
		resultMap.remove(joinTableListTemp);
		resultMap.remove(joinCondTemp);

		if (resultMap.get("responseCode") != null)
			return new String[] { resultMap.get("responseCode").toString(), new Gson().toJson(resultMap) };

		resultMap.put("responseCode", ResponseStatus.OK.value());
		resultMap.put("responseMessage", ResponseStatus.OK.phrase());

		return new String[] { ResponseStatus.OK.value(), new Gson().toJson(resultMap) };
	}

	private void getSqlPart(Map parentMap, Map metaMap, Map<String, Object> commonMap, Map<String, Object> paramMap,
			Map<String, Object> resultMap) throws IFException {
		List<Map> paramList = (List<Map>) paramMap.get(metaMap.get(Constants.path).toString());
		if (paramList == null)
			return;

		for (Map param : paramList) {
			buildSqlPart(parentMap, metaMap, commonMap, param, resultMap);

			List<Map> children = (List<Map>) metaMap.get(MetaConfig.childrenMap);
			if (children == null)
				continue;

			for (Map child : children)
				getSqlPart(metaMap, child, commonMap, param, resultMap);
		}
	}

	private void buildSqlPart(Map parentMap, Map metaMap, Map<String, Object> commonMap, Map<String, Object> paramMap,
			Map<String, Object> resultMap) throws IFException {
		String path = metaMap.get(Constants.path).toString();
		StringBuffer sbCol = new StringBuffer();
		StringBuffer sbWhere = new StringBuffer();
		List<Object> listValue = (List<Object>) resultMap.get(valueTemp);
		if (listValue == null) {
			listValue = new ArrayList<Object>();
			resultMap.put(valueTemp, listValue);
		}

		Map sqlMap = (Map) resultMap.get(path);
		if (sqlMap == null) {
			sqlMap = new HashMap<String, Object>();
			resultMap.put(path, sqlMap);
		}

		Map<String, Object> conditionMap = paramMap.get(conditionTemp) == null ? new HashMap<String, Object>()
				: (Map<String, Object>) paramMap.get(conditionTemp);

		List<List> setColumns = (List<List>) sqlMap.get(setColumnsTemp);
		if (setColumns == null) {
			setColumns = new ArrayList<List>();
			sqlMap.put(setColumnsTemp, setColumns);
		}

		List<List> whereColumns = (List<List>) sqlMap.get(whereColumnsTemp);
		if (whereColumns == null) {
			whereColumns = new ArrayList<List>();
			sqlMap.put(whereColumnsTemp, whereColumns);
		}

		List<Map> dataList = (List<Map>) sqlMap.get("dataList");
		if (dataList == null) {
			dataList = new ArrayList<Map>();
			sqlMap.put("dataList", dataList);
		}

		List<List> charList = (List<List>) sqlMap.get("charList");
		if (charList == null) {
			charList = new ArrayList<List>();
			sqlMap.put("charList", charList);
		}

		Map nullList = (Map) sqlMap.get("nullList");
		if (nullList == null) {
			nullList = new HashMap();
			sqlMap.put("nullList", nullList);
		}

		List<Object[]> setList = new ArrayList<Object[]>();
		List<Object[]> whereList = new ArrayList<Object[]>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Map<String, Object>> charTempList = new ArrayList<>();

		String alias = metaMap.get(Constants.alias).toString();
		String aliasParent = (parentMap.get(Constants.alias) == null) ? null
				: parentMap.get(Constants.alias).toString();
		String parent = (metaMap.get(Constants.parent) == null) ? null : metaMap.get(Constants.parent).toString();
		String target = (commonMap.get(Constants.target) == null) ? null : commonMap.get(Constants.target).toString();
		String systemId = commonMap.get(Constants.systemId).toString();

		String restrict = (String) metaMap.get(Constants.restrict);
		String restrictCondition = null;
		boolean needRestrict = false;
		if (restrict != null) {
			String[] restricts = restrict.split(":");
			String[] systems = restricts[0].split(",");
			List<String> restrictSystems = new ArrayList<String>();
			for (String s : systems)
				restrictSystems.add(s.trim());
			restrictCondition = restricts[1];

			if (restrictSystems.contains(systemId))
				needRestrict = true;
		}

		String restrict2 = (String) metaMap.get(Constants.restrict2);
		String restrictCondition2 = null;
		boolean needRestrict2 = false;
		if (restrict2 != null) {
			String[] restricts = restrict2.split(":");
			String[] systems = restricts[0].split(",");
			List<String> restrictSystems = new ArrayList<String>();
			for (String s : systems)
				restrictSystems.add(s.trim());
			restrictCondition2 = restricts[1];

			if (restrictSystems.contains(systemId))
				needRestrict2 = true;
		}

		boolean needAnd = false;

		List<Map> metaAttrs = (List<Map>) metaMap.get(MetaConfig.ifCols);
		if (metaAttrs == null)
			throw new IFException(ResponseStatus.FAIL, path + "의 ifmeta_attr정보가 없습니다.");

		// 2-24 SIM 날짜 관련 검색 조건 추가
		if (paramMap.get(conditionTemp) != null) {
			Map<String, Object> condMap = (Map<String, Object>) paramMap.get(conditionTemp);
			Set set = condMap.entrySet();
			Iterator iterator = set.iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();

				if (((String) entry.getKey()).contains("toChar")) {

					Map<String, Object> charMap = new HashMap<>();

					charMap.put(entry.getKey().toString().split("_")[0], (String) entry.getValue());
					charMap.put("toKey", entry.getKey().toString().split("_")[0]);
					charMap.put("toValue", entry.getValue().toString());
					charMap.put("format", entry.getKey().toString().split("_")[2]);
					charMap.put("toChar", entry.getKey().toString());

					charTempList.add(charMap);
				}

			}
		}

		boolean needComma = false;
		for (Map metaAttr : metaAttrs) {
			String ifCol = metaAttr.get(Constants.ifCol).toString();
			String useYn = metaAttr.get(Constants.useYn).toString();
			if (useYn == null || useYn.equals("N"))
				continue;

			String dbCol = metaAttr.get(Constants.dbCol).toString();
			String nvlYn = metaAttr.get(Constants.nvlYn).toString();
			String aCol = alias + "." + dbCol;
			Object[] col = new Object[3];
			
			//9-27 	nvl 여부에 따라 ? 혹은 ! 로 구분하기 위해 삼항연산자절 추가 수정
			String inConv = (metaAttr.get("inConv") == null) ? (nvlYn.equals("N") ? "?" : "!")
					: metaAttr.get("inConv").toString().replaceAll("\\$", "?");
			String outConv = (metaAttr.get("outConv") == null) ? null : metaAttr.get("outConv").toString();
			col[0] = aCol;
			col[1] = inConv;

			if (needComma)
				sbCol.append(",");

			if (outConv == null)
				sbCol.append(aCol);
			else
				sbCol.append(outConv.replaceAll("\\$", aCol) + " " + dbCol);

			needComma = true;

			if (parent != null && metaAttr.get(Constants.parentCol) != null) {
				String parentCol = aliasParent + "." + metaAttr.get(Constants.parentCol).toString();
				if (needAnd)
					sbWhere.append("\t   and ");
				//8-17 조인 where 절 에서 null 비교 여부 제외
				//sbWhere.append("(" + aCol + " is null or " + aCol + "=" + parentCol + ") \n");
				sbWhere.append("(" + aCol + "=" + parentCol + ") \n");
				needAnd = true;
			} // if parent

			if (paramMap.get(ifCol) != null) {
				Object val = paramMap.get(ifCol);
				col[2] = val;
				setList.add(col);
			} else if (conditionMap.get(ifCol) != null) {

				// 5-21 sim isNull 조건일 경우 nullList에 따로 넣고, condtion param값은 제거.
				if (conditionMap.get(ifCol).equals("isNull")) {

					nullList.put(aCol, "isNull");
					conditionMap.remove(ifCol);
					continue;
				}				
				
				String val = conditionMap.get(ifCol).toString();
				// 8-12 변수 NULL값 들어올 시에 제외 로직
				if(!CUtil.isEmpty(val)) {
					col[2] = conditionMap.get(ifCol);
					whereList.add(col);					
				}				
			}

			// 2-25 sim where 절 to_char 설계
			for (Map charMap : charTempList) {
				if (charMap.get(ifCol) != null) {
					String charVal = charMap.get(ifCol).toString();

					if (inConv.equals("?"))
						throw new IFException(ResponseStatus.NOT_DATE, "컬럼(" + ifCol + ") 은 날짜 형식이 아닙니다.");

					if (charVal != null) {

						int format = charMap.get("format").toString().length();
						int val = charVal.length();

						if (charVal.contains("~")) {
							charMap.replace("toKey", dbCol);

							int fromVal = charVal.split("~")[0].length();
							int toVal = charVal.split("~")[1].length();

							if (fromVal != format || toVal != format)
								throw new IFException(ResponseStatus.NOT_DATE, charMap.get("toChar")
										+ "조건의 value 형식이 날짜 변환 형식(" + charMap.get("format") + ")과 맞지 않습니다.");
							continue;
						}

						if (charVal.startsWith(">") || charVal.startsWith("<")) {
							charMap.replace("toKey", dbCol);
							if (format != (val - 2))
								throw new IFException(ResponseStatus.NOT_DATE, charMap.get("toChar")
										+ "조건의 value 형식이 날짜 변환 형식(" + charMap.get("format") + ")과 맞지 않습니다.");
							continue;

						} else if (charVal.startsWith("isNull")) {
							charMap.replace("toKey", dbCol);
							continue;
						} else {
							if (format != val)
								throw new IFException(ResponseStatus.NOT_DATE, charMap.get("toChar")
										+ "조건의 value 형식이 날짜 변환 형식(" + charMap.get("format") + ")과 맞지 않습니다.");
						}

						Object[] charCol = new Object[3];
						charCol[0] = "to_char(" + aCol + ", '" + charMap.get("format") + "')";
						charCol[1] = "?";
						charCol[2] = charMap.get(ifCol);
						whereList.add(charCol);

						charMap.replace("toKey", dbCol);
					}
				}
			}
			//값이 존재하지 않으면 pass
			if(conditionMap.get(ifCol) != null) {
				String val = conditionMap.get(ifCol).toString();
				if(!CUtil.isEmpty(val))
					dataMap.put(dbCol, conditionMap.get(ifCol));				
			}
				

			// 2021-3-3 sim 업데이트 제한 컬럼 조건 추가
			if (metaAttr.get(Constants.updRestirct) != null) {
				String updRestrict = metaAttr.get(Constants.updRestirct).toString();
				if (updRestrict.equals(Constants.exceptMandatory)) {
					if (paramMap.get(ifCol) != null)
						throw new IFException(ResponseStatus.NOT_UPD, ifCol + " 컬럼은 업데이트가 불가능한 컬럼입니다.");

				}
			}

		} // for

		if (this.flagOnly != null && this.flagOnly.equals("Y") && setList.size() > 0)
			throw new IFException(ResponseStatus.UPD_COND, "flag 업데이트만 가능합니다.");

		if (!charTempList.isEmpty())
			paramMap.put("charMap", charTempList);
		dataList.add(dataMap);
		charList.add(charTempList);

		setColumns.add(setList);
		whereColumns.add(whereList);

		String ifTbl = metaMap.get(Constants.ifTbl).toString();
		sqlMap.put(tableTemp, ifTbl + " " + alias);
		sqlMap.put(joinTableTemp, ifTbl + " " + alias);
		sqlMap.put(joinCondTemp, sbWhere.toString());
		sqlMap.put(restrictTemp, restrictCondition);
		sqlMap.put(restrictTemp2, restrictCondition2);
		sqlMap.put(needRestrictTemp, needRestrict);
		sqlMap.put(needRestrictTemp2, needRestrict2);

		resultMap.put(path, sqlMap);
	}

	private boolean setChildSql(Map metaMap, Map<String, Object> resultMap) throws IFException {
		try {
			boolean hasNoChild = false;

			String path = metaMap.get(Constants.path).toString();
			Map sqlMap = (Map) resultMap.get(path);
			List<String> joinTableList = (List<String>) resultMap.get(joinTableListTemp);
			if (joinTableList == null) {
				joinTableList = new ArrayList<String>();
				resultMap.put(joinTableListTemp, joinTableList);
			}
			joinTableList.add((String) sqlMap.get(joinTableTemp));

			String joinCond = sqlMap.get(joinCondTemp).toString();
			resultMap.put(joinCondTemp,
					resultMap.get(joinCondTemp) == null || resultMap.get(joinCondTemp).toString().length() == 0
							? joinCond
							: resultMap.get(joinCondTemp).toString() + "\t   and " + joinCond);

			String restrict = (String) sqlMap.get(restrictTemp);
			resultMap.put(restrictTemp,
					resultMap.get(restrictTemp) == null || resultMap.get(restrictTemp).toString().length() == 0
							? restrict
							: resultMap.get(restrictTemp).toString() + " or " + restrict);
			boolean needRestrict = (boolean) sqlMap.get(needRestrictTemp);
			if (needRestrict)
				resultMap.put(needRestrictTemp, true);

			String restrict2 = (String) sqlMap.get(restrictTemp2);
			resultMap.put(restrictTemp2,
					resultMap.get(restrictTemp2) == null || resultMap.get(restrictTemp2).toString().length() == 0
							? restrict2
							: resultMap.get(restrictTemp2).toString());
			boolean needRestrict2 = (boolean) sqlMap.get(needRestrictTemp2);
			if (needRestrict2)
				resultMap.put(needRestrictTemp2, true);

			List<Map> children = (List<Map>) metaMap.get(MetaConfig.childrenMap);
			if (children == null)
				return true;

			for (Map child : children) {
				hasNoChild = setChildSql(child, resultMap);
			}
		} catch (Exception e) {
			logger.error("\n---------------------------- line null 에러  ! --------------------------------\n"
					+ e.getMessage());
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}

		return false;
	}

	private void updateChild(ConnectionCommon connect, Connection connStack, Map metaMap, Map<String, Object> commonMap,
			Map<String, Object> paramMap, Map<String, Object> resultMap) throws IFException {

		executeUpdate(connect, connStack, metaMap, commonMap, paramMap, resultMap);

		List<Map> children = (List<Map>) metaMap.get(MetaConfig.childrenMap);
		if (children == null)
			return;

		for (Map child : children) {
			updateChild(connect, connStack, child, commonMap, paramMap, resultMap);
		}
	}

	@SuppressWarnings("unchecked")
	private void executeUpdate(ConnectionCommon connect, Connection connStack, Map metaMap,
			Map<String, Object> commonMap, Map<String, Object> paramMap, Map<String, Object> resultMap)
			throws IFException {
		String path = metaMap.get(Constants.path).toString();
		String systemId = commonMap.get(Constants.systemId).toString();
		String alias = metaMap.get(Constants.alias).toString();
		Map<String, Object> sqlMap = (Map<String, Object>) resultMap.get(path);
		if (sqlMap == null)
			return;

		List<List> setColumns = (List<List>) sqlMap.get(setColumnsTemp);
		List<List> whereColumns = (List<List>) sqlMap.get(whereColumnsTemp);

		List<Object> value = (List<Object>) resultMap.get(valueTemp);
		Object[] values = new Object[value.size()];
		value.toArray(values);

		String table = sqlMap.get(tableTemp).toString();
		List<String> joinTableList = (List<String>) resultMap.get(joinTableListTemp);
		String joinCond = resultMap.get(joinCondTemp).toString();

		String restrict = (String) resultMap.get(restrictTemp);
		String restrict2 = (String) resultMap.get(restrictTemp2);

		boolean needRestrict = resultMap.get(needRestrictTemp) == null ? false
				: (boolean) resultMap.get(needRestrictTemp);
		boolean needRestrict2 = resultMap.get(needRestrictTemp2) == null ? false
				: (boolean) resultMap.get(needRestrictTemp2);

		Map<String, List> unAffectedMap = (Map<String, List>) resultMap.get(Constants.unAffected);
		if (unAffectedMap == null) {
			unAffectedMap = new HashMap<String, List>();
			resultMap.put(Constants.unAffected, unAffectedMap);
		}

		StringBuffer sbSql = new StringBuffer();

		sbSql.append("\nupdate " + table + "\n");

		Map<String, Object> attrs = new HashMap<String, Object>();
		for (List<Object[]> list : setColumns)
			for (Object[] col : list)
				attrs.put(col[0].toString(), col[1].toString());

		List<Object[]> setArgs = new ArrayList<Object[]>();
		List<Object[]> whereArgs = new ArrayList<Object[]>();

		boolean needComma = false;
		int seq = 0;
		sbSql.append("set ");
		String target = (commonMap.get(Constants.target) == null) ? null : commonMap.get(Constants.target).toString();
		boolean foundTarget = false;
		if (target != null) {
			String[] targetRefs = target.split(",");
			for (String targetRef : targetRefs) {
				String[] targets = targetRef.split("-");
				if (!path.equals(targets[0].trim()))
					continue;

				sbSql.append(" " + alias + "." + targets[1].trim() + "_FLG='Y', " + alias + "." + targets[1].trim()
						+ "_DAT=sysdate ");
				needComma = true;
				foundTarget = true;
			}
		}
		//9-30 update set절에 nvl 처리를 하는 컬럼이 들어올 시에, value 값이 !로 들어오는 문제 해결.
		for (Entry<String, Object> entry : attrs.entrySet()) {
			if (needComma)
				sbSql.append(", ");
			
			if(entry.getValue().equals("!"))
				sbSql.append(entry.getKey() + "=? ");
			else
				sbSql.append(entry.getKey() + "=" + entry.getValue() + " ");
				
			entry.setValue(seq++);
			needComma = true;
		}
		sbSql.append("\n");

		String joinFrom = "";
		boolean needJoin = false;
		for (String joinTable : joinTableList) {
			if (joinTable.equals(table))
				continue;
			if (needJoin)
				joinFrom += ", ";
			joinFrom += joinTable;
			needJoin = true;
		}
		if (needJoin) {
			sbSql.append("\nwhere exists (\n");
			sbSql.append("\tselect 1\n");
			sbSql.append("\t from " + joinFrom + "\n");
		}

		int attrSize = attrs.keySet().size();
		if (attrSize == 0 && !foundTarget) {
			int sizeWhere = 0;
			for (List<Object[]> list : whereColumns)
				sizeWhere += list.size();
			if (sizeWhere > 0 && target == null)
				throw new IFException(ResponseStatus.MISSING, path + "의 업데이트 항목이 없습니다.");
			resultMap.remove(path);
			return;
		}

		for (List<Object[]> list : setColumns) {
			Object[] oa = new Object[attrSize];
			for (Object[] col : list) {
				int idx = (int) attrs.get(col[0].toString());
				oa[idx] = col[2];
			}
			setArgs.add(oa);
		}

		attrs = new HashMap<String, Object>();
		for (List<Object[]> list : whereColumns) {
			for (Object[] col : list) {
				//8-20 String null 체크 추가
				String val = CUtil.nullString(col[2].toString(), "");
				
				// 5-20 sim where 조건절에 이상/이하/null 조건절 추가
				if (val.startsWith(">"))
					attrs.put(col[0].toString(), ">" + col[1].toString());
				else if (val.startsWith("<"))
					attrs.put(col[0].toString(), "<" + col[1].toString());
				else if (val.equals("isNull"))
					attrs.put(col[0].toString(), col[2].toString());
				else if (val.equals(""))
					attrs.put(col[0].toString(), "");
				else
					attrs.put(col[0].toString(), col[1].toString());
			}
		}

		boolean needAnd = false;
		seq = 0;
		sbSql.append("\t where ");
		for (Entry<String, Object> entry : attrs.entrySet()) {
			if (needAnd)
				sbSql.append("\t   and ");

			// 5-20 sim where 조건절에 이상/이하 조건절 추가
			String val = entry.getValue().toString();

			if (val.startsWith(">"))
				sbSql.append("(? is null or " + entry.getKey() + " >= " + val.substring(1, val.length()) + ") \n");
			else if (val.startsWith("<"))
				sbSql.append("(? is null or " + entry.getKey() + " <= " + val.substring(1, val.length()) + ") \n");
			else if (val.equals("isNull")) {
				sbSql.append(entry.getKey() + " is null \n");
			}
			// 8-12 UPDATE 쿼리 수정 진행  		9-27 NVL 처리를 위해 '!'알때는 NVL로 처리되도록 수정	
			else {
				if(val == "?")				
					sbSql.append("(" + entry.getKey() + " = ?) \n");
				else if(val == "!")	
					sbSql.append("(nvl(" + entry.getKey() + ", '-1') = ?) \n");
//				sbSql.append("(? is null or " + entry.getKey() + " is null or " + entry.getKey() + "=?) \n");
			}
			
			entry.setValue(seq++);
			needAnd = true;
		}

		if (!joinCond.trim().equals(""))
			sbSql.append("\t   and " + joinCond);

		if (needRestrict && restrict != null) {
			if (systemId.equals("cj")) {
				sbSql.append("\t   and (" + restrict + ")\n");
			}
		}

		if (needRestrict2 && restrict2 != null) {
			if (systemId.equals("orga")) {
				sbSql.append("\t   and (" + restrict2 + ")\n");
			}
		}

		// 5_20 to_char 관련 쿼리 추가 여부(배열 시 쿼리 하나만 추가하도록 함)
		List<String[]> toKey = null;
		List<String[]> toValue = null;

		boolean append = true;
		boolean append2 = true;
		boolean append3 = true;
		boolean append4 = true;

		List<Map<String, Object>> sql = new ArrayList<>();

		// 2-25 sim where 절 to_char 설계
		if (paramMap.get("header") != null) {
			List<Map> header = (List<Map>) paramMap.get("header");

			for (int i = 0; i < header.size(); i++) {
				// 5-20 sim Line의 condtionMap을 추적해서 쿼리 생성합니다.
				if (header.get(i).get(path) != null) {
					List<Map> line = (List<Map>) header.get(i).get(path);

					// 3-26 sim paramMap 배열 처리 추가
					for (int j = 0; j < line.size(); j++) {
						if (line.get(j).get("charMap") != null) {
							sql = (List<Map<String, Object>>) line.get(j).get("charMap");

							String[] keyTemp = new String[sql.size()];
							String[] valTemp = new String[sql.size()];

							if (toKey == null || toValue == null) {
								toKey = new ArrayList<>();
								toValue = new ArrayList<>();
							}

							for (int k = 0; k < sql.size(); k++) {
								String key = sql.get(k).get("toKey").toString();
								String val = sql.get(k).get("toValue").toString();

								if (val.contains("~") && append) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') between ? and ? ) \n");

									append = false;

								} else if (val.startsWith(">") && append2) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') >= ? ) \n");

									append2 = false;

								} else if (val.startsWith("<") && append3) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') <= ? ) \n");

									append3 = false;

								} else if (val.startsWith("isNull") && append4) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') is null ) \n");

									append4 = false;
								}

								keyTemp[k] = key;
								valTemp[k] = val;
							}

							toKey.add(keyTemp);
							toValue.add(valTemp);
						}

						line.get(j).remove("charMap");
					}
					// 5-20 sim header의 condtionMap을 추적해서 쿼리 생성합니다.
				} else if (header.get(i).get("charMap") != null) {
					sql = (List<Map<String, Object>>) header.get(i).get("charMap");

					// 3-26 sim paramMap 배열 처리 추가
					for (int j = 0; j < header.size(); j++) {
						if (header.get(j).get("charMap") != null) {
							sql = (List<Map<String, Object>>) header.get(j).get("charMap");

							String[] keyTemp = new String[sql.size()];
							String[] valTemp = new String[sql.size()];

							if (toKey == null || toValue == null) {
								toKey = new ArrayList<>();
								toValue = new ArrayList<>();
							}

							for (int k = 0; k < sql.size(); k++) {
								String key = sql.get(k).get("toKey").toString();
								String val = sql.get(k).get("toValue").toString();

								if (val.contains("~") && append) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') between ? and ? ) \n");

									append = false;

								} else if (val.startsWith(">") && append2) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') >= ? ) \n");

									append2 = false;

								} else if (val.startsWith("<") && append3) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') <= ? ) \n");

									append3 = false;

								} else if (val.startsWith("isNull") && append4) {
									if (needAnd)
										sbSql.append("\t   and ");

									sbSql.append("(to_char(" + alias + "." + key + ", '"
											+ sql.get(k).get("format").toString() + "') is null ) \n");

									append4 = false;
								}

								keyTemp[k] = key;
								valTemp[k] = val;
							}

							toKey.add(keyTemp);
							toValue.add(valTemp);
						}

						header.get(i).remove("charMap");
					}
				}
			}
			// 5-21 sim 컬럼 is null 쿼리를 생성하기 위해 sqlMap에서 조건을 가져옴. 
			// is null 조건은 무조건 맨 마지막에 생성
			Map nullMap = new HashMap();
			nullMap = (Map) sqlMap.get("nullList");
			List<String> sqlNull = new ArrayList<>();

			for (Object obj : nullMap.keySet()) {
				if (nullMap.get(obj).equals("isNull"))
					sqlNull.add(obj.toString());
			}

			for (String str : sqlNull) {
				if (needAnd)
					sbSql.append("\t   and ");

				sbSql.append("(" + str + " is null) \n");
			}
			// 여기까지

			if (needJoin)
				sbSql.append(")\n");
			else
				sbSql.append("\n");
			//변수 2개 묷음이 아닌 한개씩 매핑되도록 수정
			attrSize = attrs.keySet().size();
			for (List<Object[]> list : whereColumns) {
				Object[] oa = new Object[attrSize];
				for (Object[] col : list) {
					int idx = (int) attrs.get(col[0].toString());
					oa[idx] = col[2];
					//oa[idx * 2 + 1] = col[2];
				}
				whereArgs.add(oa);
			}

			List<Object[]> batchArgs = new ArrayList<Object[]>();
			for (int i = 0; i < setArgs.size(); i++) {
				Object[] setOa = setArgs.get(i);
				Object[] whereOa = whereArgs.get(i);
				List<String> conValue = null;
				Object[] oa = null;

				// 2-25 sim where 절 to_char 설계 //3-26 sim 배열 처리 추가
				if (toValue != null) {
					for (String[] temp : toValue) {
						conValue = new ArrayList<>();					
						for (int j = 0; j < temp.length; j++) {

							if (temp[j].contains("~")) {
								conValue.add(temp[j].split("~")[0]);
								conValue.add(temp[j].split("~")[1]);						
							}
							
							if (temp[j].contains(">") || temp[j].contains("<")) {		
								conValue.add(temp[j].substring(2, temp[j].length()));								
							}	
						}
						
						oa = new Object[setOa.length + whereOa.length + conValue.size()];
						int idx = 0;
						for (Object value2 : setOa)
							oa[idx++] = value2;
						
						// 5-20 sim where 조건절에 이상/이하 조건 변수 수정 추가
						for (Object value2 : whereOa) {					
							// 6-29 sim value : null일때   null 처리 추가.
							if(value2 == null) {
								oa[idx++] = "";
								continue;
							}
							
							if (value2.toString().startsWith(">") || value2.toString().startsWith("<"))
								oa[idx++] = value2.toString().substring(2, value2.toString().length());
							else if (value2.toString().equals("isNull"))
								continue;
							else
								oa[idx++] = value2;
						}					
						
						for (Object value2 : conValue)
							oa[idx++] = value2;
					}

				} else {
					oa = new Object[setOa.length + whereOa.length];
					int idx = 0;
					for (Object value2 : setOa)
						oa[idx++] = value2;

					// 5-20 where 조건절에 이상/이하 조건 변수 수정 추가
					for (Object value2 : whereOa) {
						// 6-29 value : null일때   null 처리 추가. 9-27 NVL 처리를 위해 -1 로 변수값 수정	
						if(value2 == null) {
							oa[idx++] = "-1";
							continue;
						}
						
						if (value2.toString().startsWith(">") || value2.toString().startsWith("<"))
							oa[idx++] = value2.toString().substring(2, value2.toString().length());
						else if (value2.toString().equals("isNull"))
							continue;
						else
							oa[idx++] = value2;
					}
				}

				batchArgs.add(oa);
			}

			logger.debug(sbSql.toString() + ";\n" + CUtil.convertListOfObjectArrayToJsonString(batchArgs) + "\n");

			try {
				int[] iRet = connect.batchUpdate(connStack, sbSql.toString(), batchArgs);

				List<Map> unAffectedList = new ArrayList<Map>();
				for (int idx = 0; idx < iRet.length; idx++) {
					if (iRet[idx] > 0)
						continue;

					List<Object[]> columnList = whereColumns.get(idx);
					Map<String, Object> dataMap = new HashMap<String, Object>();
					for (Object[] col : columnList) {
						if (col[0].toString().contains("to_char"))
							continue;
						String key = col[0].toString().replaceFirst("^.*\\.", "");
						Object val = col[2];
						dataMap.put(key, val);
					}
					// 3-26 sim 배열 처리 추가 5-21 sim value값이 isNull이면 미반영 맵 put 대상에서 제외
					if (toKey != null) {
						
						String[] keyTemp = toKey.get(idx);
						String[] valTemp = toValue.get(idx);
											
						for(int i = 0; i < valTemp.length; i++) {
							if(!valTemp[i].equals("isNull"))
								dataMap.put(keyTemp[i], valTemp[i]);
						}						
	
					}

					unAffectedList.add(dataMap);
				}

				if (unAffectedList.size() > 0)
					unAffectedMap.put(path, unAffectedList);

				resultMap.remove(path);
			} catch (Exception e) {
				throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
			}
		}
	}
}
