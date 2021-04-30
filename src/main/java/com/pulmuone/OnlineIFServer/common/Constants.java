package com.pulmuone.OnlineIFServer.common;

public class Constants {
	///ifMeta 속성
	public final static String ifId = "ifId";
	public final static String ifTbl = "ifTbl";
	public final static String path = "path";
	public final static String parent = "parent";
	public final static String alias = "alias";
	public final static String action = "action";
	public final static String restrict = "restrict";
	public final static String restrict2 = "restrict2";
	public final static String flagOnly = "flagOnly";
	
	///ifMetaAttr 속성
	public final static String ifCol = "ifCol";
	public final static String dbCol = "dbCol";
	public final static String parentCol = "parentCol";
	public final static String inConv = "inConv";
	public final static String mandatory = "mandatory";
	public final static String useYn = "useYn";
	public final static String updRestirct = "updrestirct";
	
	///mandatory 속성
	public final static String systemMandatory = "S";	//입력시 필수 체크
	public final static String joinMandatory = "J";	//입력시 하나이상 존재 체크, 조회시 _filter 조건에 존재 체크
	public final static String exceptMandatory = "E";	//입력시 제외(sequence 컬럼등)
	
	///ifUse 속성
	public final static String systemId = "systemId";
	
	///Request URL param 속성
	public final static String page = "page";
	public final static String target = "target";
	public final static String order = "_order";
	public final static String filter = "_filter";	//조회시 option
	
	///Response 속성
	public final static String totalPage = "totalPage";
	public final static String currentPage = "currentPage";
	public final static String pageSize = "pageSize";
	public final static String unAffected = "unAffected";
}
