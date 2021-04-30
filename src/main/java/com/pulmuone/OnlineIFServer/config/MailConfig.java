package com.pulmuone.OnlineIFServer.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.dto.MailInfo;
import com.pulmuone.OnlineIFServer.service.MailService;

@Component
public class MailConfig {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String from = "OnlineMallManager@pulmuone.com";
    private final static String toManager = "yb.shim@metanetglobal.com";
    private final static String subject = "온라인 통합몰 중계서버 메일링 보고입니다.";
    private final static String utf8 = "UTF-8";
    
	@Autowired	
	MailService mailService;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	@Scheduled(cron="0 35 16 * * ?")
	public void sendSuccessMail() throws Exception{
		Map<String, MailInfo> list = new HashMap<>();
		
		Date sys = new Date();	
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd160000");
		SimpleDateFormat format3 = new SimpleDateFormat("yyyyMMdd170000");
		SimpleDateFormat format4 = new SimpleDateFormat("MM월 dd일");
		SimpleDateFormat format5 = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
		
		String time = format1.format(sys);
		String time2 = format2.format(sys);
		String time3 = format3.format(sys);		
		
		String posFrom = "-";
		String posTo = "-";
		String erpFrom = "-";
		String erpTo = "-";	
		
		long posTotalTime = 0L;
		long erpTotalTime = 0L;	
		
		int pos = 0;
		int erp = 0;
		
		List<Map<String, Object>> posTime, erpTime;
		
		//테이블 목록 가져와서 소요시간, 카운트 개수 등의 정보 설정.
		List<Map<String, Object>> tables = mailService.getTableList();
		
		for(int i=0; i < tables.size(); i++) {
			MailInfo info = new MailInfo();
			
			String tableId = (String) tables.get(i).get("TABLE_NAME");
			String tableNm = mailService.getTableName(tableId);
			pos = mailService.getPosCount(tableId, time);
			erp = mailService.getErpCount(tableId, time);
			posTime = mailService.getPosTime(tableId.toLowerCase(), time2, time3);
			erpTime = mailService.getErpTime(tableId, time, time2, time3);
			
			if(posTime.size() != 0 && erpTime.size() != 0) {
				posFrom = posTime.get(posTime.size()-1).get("POS").toString();
				posTo = posTime.get(0).get("POS").toString();
				
			    erpFrom = erpTime.get(erpTime.size()-1).get("ITF_DAT").toString();
				erpTo = erpTime.get(0).get("ITF_DAT").toString();
				
				posTotalTime = ((format5.parse(posTo).getTime() - format5.parse(posFrom).getTime()))/1000;
				erpTotalTime = ((format5.parse(erpTo).getTime() - format5.parse(erpFrom).getTime())/1000);
			}			
			
			info.setDate(format4.format(sys));
			info.setName(tableNm);
			info.setPosCount(pos);
			info.setErpCount(erp);		
			info.setPosStart(posFrom);
			info.setPosEnd(posTo);
			info.setPosTime(posTotalTime + "초");
			info.setErpStart(erpFrom);
			info.setErpEnd(erpTo);
			info.setErpTime(erpTotalTime + "초");
			if(pos != erp) 
				info.setMessage("불일치 " + String.valueOf(Math.abs(pos-erp)) + " 건");
			else
				info.setMessage("-");
			
			posFrom = "-";
			posTo = "-";
			erpFrom = "-";
			erpTo = "-";	
			
			posTotalTime = 0L;
			erpTotalTime = 0L;	
			
			list.put(i+"", info);
		}
		
				
    	StringBuffer sb =  new StringBuffer();
		
    	sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"); 
    	sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");  
		sb.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8;' />");		
		sb.append("<meta name='viewport' content='width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0'>");		
		sb.append("<meta http-equiv='X-UA-Compatible' content='IE=edge' />");
		sb.append("</head>");			                               
		sb.append("     <table width='100%' cellspacing='0' cellpadding='0' border='0'>");
		sb.append("         <tr>");
		sb.append("             <th colspan='11' style='font-size:18px;color:#4c8907;height:20px;border-top:2px solid #b0cc5a;padding-left:20px;padding-top:10px;padding-bottom:10px;'>올가 NEW POS 주문마감 모니터링 Report</th>");
		sb.append("			</tr>");
		sb.append("         <tr style='text-align: center;'>");
		sb.append("             <th colspan='2' style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;border-bottom:1px solid #c1c1c1;'>API</td>");
		sb.append("             <th colspan='4' style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;border-left:1px solid #c1c1c1;border-bottom:1px solid #c1c1c1;'>POS</td>");
		sb.append("             <th colspan='4' style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;border-left:1px solid #c1c1c1;border-right: 1px solid #c1c1c1;border-bottom:1px solid #c1c1c1;'>ERP</td>");
		sb.append("             <th rowspan='2' style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;'>비고</td>");
		sb.append("			</tr>");
		sb.append("			<tr style='text-align: center;'>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;'>일자</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>항목</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>시작시간</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>종료시간</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>소요시간</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>주문건수</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>시작시간</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>종료시간</td>");
		sb.append("				<th style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;'>소요시간</td>");
		sb.append("				<th	style='color:#444;font-size:12px;font-weight:600;letter-spacing:-1px;padding:6px 5px 4px;background:#f7f7f7;border-left:1px solid #c1c1c1;border-right: 1px solid #c1c1c1;'>주문건수</td>");
		sb.append("			</tr>");	
		for(int i = 0; i < list.size(); i++) {
			String index = Integer.toString(i);
			sb.append("		<tr style='text-align: center;'>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;text-overflow:ellipsis;'>"+ list.get(index).getDate()+ "</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;text-overflow:ellipsis;'>"+ list.get(index).getName() +"</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getPosStart() +"</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getPosEnd() +"</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getPosTime() +"</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getPosCount() +"건</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getErpStart() +"</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getErpEnd() +"</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getErpTime() +"</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'>"+ list.get(index).getErpCount() +"건</td>");
			sb.append("			<td	style='font-size:12px;height:20px;padding:5px 5px 3px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-top:1px solid #c1c1c1;color:#666;overflow:hidden;text-overflow:ellipsis;'>"+ list.get(index).getMessage() +"</td>");
			sb.append("		</tr>");
		}
		sb.append("		</table>");
		sb.append("</html>");	
		
//		logger.info(sb.toString());  		

		//수신자 목록 및 정보 가져와서 메일 전송 로직 설계.
		List<Map<String, Object>> receivers = mailService.getOrgaReceivers();		
		
		for(Map<String, Object> receiver : receivers) {
			String email = (String) receiver.get("MAIL_ADDRESS");
			
			try{				
				MimeMessage mailMessage = javaMailSender.createMimeMessage();
	        	MimeMessageHelper hepler = new MimeMessageHelper(mailMessage, true, utf8);
	        	hepler.setFrom(from);
	        	hepler.setTo(email);
	        	hepler.setSubject(subject);
	        	hepler.setText(sb.toString(), true);	//html 적용
	        	hepler.setSentDate(new Date());
	            
	            javaMailSender.send(mailMessage);
	            
	        }catch (Exception e) {
	        	logger.error("메세징 오류 발생!! \n" + e.getMessage());
	    	}    
		}	
		       
    }

	public void sendFailMail(String ifId, String systemId, String ifNm ,String requestData, String failReason) throws Exception{
		//서버 id
		String server = System.getProperty("server.id");
		
		switch(server) {
		    case "local":
		    	server = "로컬";
		    	break;
		    case "dev":
		    	server = "개발";
		    	break;
		    case "prd1":
		    	server = "운영";
		    	break;
		    case "prd2":
		    	server = "운영";
		    	break;
		}
		//시스템명
		String systemNm = null;
		
		switch(systemId) {
		    case "orpos":
		    	systemNm = "올가POS";
		    	break;
		    case "orga":
		    	systemNm = "올가OMS";
		    	break;
		    case "mall":
		    	systemNm = "통합몰";
		        break;
		    case "system1":
		    	systemNm = "테스트용 시스템";
		        break;
		    case "cj":
		    	systemNm = "CJ물류";
		        break;   
		    case "fd":
		    	systemNm = "하이톡";
		        break;
		}
		Date sys = new Date();	
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);	
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.KOREA);		
		String time = format.format(sys);	
		String time2 = format2.format(sys);
		
		StringBuffer sb =  new StringBuffer();		
    	sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"); 
    	sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");    	
		sb.append("<head>");
		sb.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8;' />");		
		sb.append("<meta name='viewport' content='width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0'>");		
		sb.append("<meta http-equiv='X-UA-Compatible' content='IE=edge' />");
		sb.append("<title>Online Interface Mail Report</title>");
		sb.append("</head>");	                                 
		sb.append("<table width='100%' cellspacing='0' cellpadding='0' border='0'>");
		sb.append("     <tr>");
		sb.append("         <th colspan='4' style='font-size:18px;color:#4c8907;border-top:2px solid #b0cc5a;padding-left:20px;padding-top:10px;padding-bottom:10px;'>실시간 모니터링 인터페이스 결과(Fail) Report</th>");
		sb.append("		</tr>");
		sb.append("     <tr>");
		sb.append("         <th style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:6px;padding-right:5px;padding-bottom:4px;padding-left:5px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;border-left:1px solid #ddd;'>호출일시</th>");
		sb.append("         <th style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:6px;padding-right:5px;padding-bottom:4px;padding-left:5px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;border-left:1px solid #ddd;'>호출서버</th>");
		sb.append("         <th style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:6px;padding-right:5px;padding-bottom:4px;padding-left:5px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;border-left:1px solid #ddd;'>API 명</th>");
		sb.append("         <th style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:6px;padding-right:5px;padding-bottom:4px;padding-left:5px;background-color:#f7f7f7;border-top:2px solid #b0cc5a;border-left:1px solid #ddd;border-right:1px solid #ddd;'>API ID</th>");
		sb.append("		</tr>");
		sb.append("		<tr style='text-align:center;'>");
		sb.append("			<td style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:5px;padding-right:5px;padding-bottom:3px;padding-left:5px;border-left:1px solid #ddd;color:#666;table-layout:fixed;overflow:hidden;text-overflow:ellipsis;'>" + time +"</td>");
		sb.append("			<td style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:5px;padding-right:5px;padding-bottom:3px;padding-left:5px;border-left:1px solid #ddd;color:#666;table-layout:fixed;overflow:hidden;text-overflow:ellipsis;'>" + server +"</td>");
		sb.append("			<td style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:5px;padding-right:5px;padding-bottom:3px;padding-left:5px;border-left:1px solid #ddd;color:#666;table-layout:fixed;overflow:hidden;text-overflow:ellipsis;'>" + ifNm.replace("_", "-") + "</td>");
		sb.append("			<td style='word-break:break-all;font-size:12px;height:20px;width:25%;padding-top:5px;padding-right:5px;padding-bottom:3px;padding-left:5px;border-left:1px solid #ddd;color:#666;table-layout:fixed;border-right:1px solid #ddd;'>" + ifId.replace("_", "-") + "</td>");
		sb.append("		</tr>");			
		sb.append("</table>");
		sb.append("<table width='100%' cellspacing='0' cellpadding='0' border='0'>");
		sb.append("     <tr>");
		sb.append("         <th style='color:#444;font-size:12px;height:20px;width:25%;padding-top:6px;padding-right:5px;padding-bottom:4px;padding-left:5px;background-color:#f7f7f7;border-top:1px solid #ddd;border-left:1px solid #ddd;'>시스템</th>");
		sb.append("         <th style='color:#444;font-size:12px;height:20px;width:75%;padding-top:6px;padding-right:5px;padding-bottom:4px;padding-left:5px;background-color:#f7f7f7;border-top:1px solid #ddd;border-left:1px solid #ddd;border-right:1px solid #ddd'>실패원인</th>");
		sb.append("		</tr>");
		sb.append("		<tr style='text-align:center;'>");
		sb.append("			<td style='word-break:break-all;font-size:12px;width:25%;padding-top:5px;padding-right:5px;padding-bottom:3px;padding-left:5px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-bottom:1px solid #ddd;color:#666;overflow:hidden;text-overflow:ellipsis;'>" + systemNm + "</td>");
		sb.append("			<td style='word-break:break-all;font-size:12px;width:75%;padding-top:5px;padding-right:5px;padding-bottom:3px;padding-left:5px;border-bottom:1px solid #ddd;border-left:1px solid #ddd;border-right:1px solid #ddd;border-bottom:1px solid #ddd;color:#666;table-layout:fixed;overflow:hidden;text-overflow:ellipsis;'>" + failReason.replace("_", "-") + "</td>");
		sb.append("		</tr>");			
		sb.append("</table>");
		
//		logger.info(sb.toString());    	
		
		//수신자 목록 및 정보 가져와서 메일 전송 로직 설계.
		List<Map<String, Object>> receivers = mailService.getReceivers(); 
		
		for(Map<String, Object> receiver : receivers) {
			//2-27 sim system 인증키로 메일 수신자 구분 지음.
			String mailer = receiver.get("SYSTEM").toString();
			
			if(!mailer.equals("system1")) {
				if(!mailer.equals(systemId))
					continue;
			}			
			
			String email = (String) receiver.get("MAIL_ADDRESS");
			
	        try{
	        	MimeMessage mailMessage = javaMailSender.createMimeMessage();
	        	MimeMessageHelper hepler = new MimeMessageHelper(mailMessage, true, utf8);
	        	hepler.setFrom(from);
	        	hepler.setTo(email);
	        	hepler.setSubject("온라인 통합몰 중계서버(" + server + "환경) 메일링 보고입니다.");
	        	hepler.setText(sb.toString(), true);	//html 적용
	        	hepler.setSentDate(new Date());  
	        	
	        	//3-9 sim 메일링 첨부파일 기능 추가
	        	if(requestData != null) {
	        		//File file = new File("C:\\APPS\\orga\\OnlineIFServer\\data.txt");
	        		File file = new File("/home/webadm01/OnlineIFServer/data.txt");
		        	BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		        	
		        	if(file.isFile() && file.canWrite()){
		                //요청 json data 파일에 쓰기(자동 overwrite됨)
		                bufferedWriter.write(requestData);
		                
		                bufferedWriter.close();
		            }
		        	hepler.addAttachment("RequestData_"+time2+".txt", file); //파일 첨부 필요할시 다음과 같이 구현
	        	}	        	
	            
	            javaMailSender.send(mailMessage);
	            
	        }catch (Exception e) {
	        	logger.error("메세징 오류 발생!! \n" + e.getMessage());
	    	}    
		}
    }
	/*
//	@Scheduled(cron="0 28 23 * * ?")
	public void sendOrgaCountMail() throws Exception{
		Map<String, MailInfo> list = new HashMap<>();
		
		//sys : 오늘 sys2 : 내일
		Date syss = new Date();	
		Date sys = new Date(syss.getTime()+(1000*60*60*24*-1));
		Date sys2 = new Date();
		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd000000");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd120000");
		SimpleDateFormat format3 = new SimpleDateFormat("yyyyMMdd");
		
		String today = format1.format(sys);
		String medium = format2.format(sys);
		String time = format3.format(sys);	
		String tommorow = format1.format(sys2);		
				
		Map<String, Object> orgaCount = mailService.getOrgaCount(today, medium, time, tommorow);
		
    	StringBuffer sb =  new StringBuffer();

		sb.append("<html lang='ko'>");		
		sb.append("<head>");
		sb.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8;' />");		
		sb.append("<meta name='viewport' content='width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0'>");		
		sb.append("<meta http-equiv='X-UA-Compatible' content='IE=edge' />");
		sb.append("<title>Online Interface Mail Report</title>");
		sb.append("</head>");		
		sb.append("<body style='color:#666;margin:0;padding:0;*word-break:break-all;-ms-word-break:break-all;font-size:12px;font-family:dotum,Helvetica,sans-serif !important'>");													                                                                                                                                                                         
		sb.append("<div style='padding-bottom:10px'>");
		sb.append("</div>");
		sb.append("<div style='width:800px;position:relative;background:#f9f9f9;padding:20px;margin-left:10px;border:1px solid #cccdbb'>");
		sb.append("<div style='position:relative;width:100%;height:66px;border-top:3px solid #b0cc5a;border-bottom:1px solid #dadada;background:#f9f9f9;padding:0'>");
		sb.append("<h1 style='position:absolute;left:30px;top:15px;font-size:16px;color:#4c8907'>올가 NEW POS Report</h1>");
		sb.append("</div>");
		sb.append("<div>");		
		sb.append("		<div style='border:1px solid #e9e9e9;background:#fff;font:normal 14px dotum !important;margin-top:20px'>");                                  
		sb.append("         <table width='100%' cellspacing='0' cellpadding='0'>");
		sb.append("             <tr>");
		sb.append("                 <th style='color:#444;font-size:12px;height:20px;letter-spacing:-1px;padding:6px 5px 4px;border-top:2px solid #b0cc5a;border-left:1px solid #c1c1c1;border-bottom:1px solid #c1c1c1;width:50%'>항목명</th>");
		sb.append("                 <th style='color:#444;font-size:12px;height:20px;letter-spacing:-1px;padding:6px 5px 4px;border-top:2px solid #b0cc5a;border-left:1px solid #c1c1c1;border-bottom:1px solid #c1c1c1;width:50%'>갯수</th>");
		sb.append("				</tr>");	
		for( String key : orgaCount.keySet() ){ 
			if(orgaCount.get(key) != "0") {
				sb.append("				<tr style='text-align: center;'>");	
				sb.append("					<td style='color:#444;font-size:12px;height:20px;padding:6px 5px 4px;border-left:1px solid #c1c1c1;border-bottom:1px solid #c1c1c1'>"+ key +"</td>");
				sb.append("					<td style='color:#444;font-size:12px;height:20px;padding:6px 5px 4px;border-left:1px solid #c1c1c1;border-bottom:1px solid #c1c1c1'>"+ orgaCount.get(key) +"</td>");
				sb.append("				</tr>");
			}
		}
		sb.append("			</table>");
		sb.append("		</div>");
		sb.append("	</div>");
		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");	
		
//		logger.info(sb.toString());  		
			
		try{
        	SimpleMailMessage mailMessage = new SimpleMailMessage();
        	mailMessage.setTo("yb.shim@metanetglobal.com");
            mailMessage.setSubject("Mail");
            mailMessage.setText(sb.toString());
            mailMessage.setSentDate(new Date());
            
            javaMailSender.send(mailMessage);
            
        }catch (Exception e) {
        	logger.error("메세징 오류 발생!! \n" + e.getMessage());
    	}      
    }
	
	*/
}
