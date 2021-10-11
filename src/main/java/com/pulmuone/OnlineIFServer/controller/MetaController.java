package com.pulmuone.OnlineIFServer.controller;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulmuone.OnlineIFServer.service.CommonService;
import com.pulmuone.OnlineIFServer.util.MetaUtil;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/itf")
public class MetaController {

	@Autowired
	MetaUtil metaUtil;

	@Autowired
	CommonService commonService;

	/**** 상품정보****/
    @PostMapping("/common")
    public void inputCommon(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/common")
    public void updateCommon(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/common")
    public void searchCommon(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 주문|취소 ****/
    @PostMapping("/custord")
    public void inputPO(HttpServletRequest request, HttpServletResponse response) throws SQLIntegrityConstraintViolationException {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/custord")
    public void searchPO(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/custord")
    public void updatePO(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 송장 ****/
    @PutMapping("/dlv")
    public void updateDlv(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/dlv")
    public void searchDlv(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 미출 ****/
    @PutMapping("/mis")
    public void updateMis(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/mis")
    public void searchMis(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 매출확정 ****/
    @PutMapping("/sal")
    public void updateSal(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/sal")
    public void searchSal(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 기타주문 ****/
    @PostMapping("/ordetc")
    public void inputOrdetc(HttpServletRequest request, HttpServletResponse response) throws SQLIntegrityConstraintViolationException {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/ordetc")
    public void searchOrdetc(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/ordetc")
    public void updateOrdetc(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 기타주문 송장 ****/
    @PutMapping("/ordetcDlv")
    public void updateOrdetcDlv(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/ordetcDlv")
    public void searchOrdetcDlv(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 구매발주 ****/
    @PostMapping("/purchase")
    public void inputOrder(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/purchase")
    public void searchOrder(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/purchase")
    public void updateOrder(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 재고수량 ****/
    @PostMapping("/stock")
    public void inputStock(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @GetMapping("/stock")
    public void searchStock(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/stock")
    public void updateStock(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 마감임박폐기예정 ****/
    @PostMapping("/stockdead")
    public void inputScrap(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @GetMapping("/stockdead")
    public void searchScrap(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @PutMapping("/stockdead")
    public void updateScrap(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
	/**** 올가R2발주스케쥴 ****/
    @PostMapping("/purchasesch")
    public void inputOrdSch(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @PutMapping("/purchasesch")
    public void updateOrdSch(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @GetMapping("/purchasesch")
    public void searchOrdSch(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
	/**** 상품정보****/
    @PostMapping("/goods")
    public void inputGoods(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/goods")
    public void updateGoods(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/goods")
    public void searchGoods(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 상품가격정보****/
    @PostMapping("/price")
    public void inputPrice(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @PutMapping("/price")
    public void updatePrice(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @GetMapping("/price")
    public void searchPrice(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 상품영양정보****/
    @PostMapping("/nutri")
    public void inputNutri(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @PutMapping("/nutri")
    public void updateNutri(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @GetMapping("/nutri")
    public void searchNutri(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
	/**** 납품처정보****/
    @PostMapping("/shipto")
    public void inputShipto(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @PutMapping("/shipto")
    public void updateShipto(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    @GetMapping("/shipto")
    public void searchShipto(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
	/**** 임직원정보****/
    @PostMapping("/empl")
    public void inputEmpl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/empl")
    public void updateEmpl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/empl")
    public void searchEmpl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 조직정보 ****/
    @PostMapping("/emplDept")
    public void inputGroup(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/emplDept")
    public void updateGroup(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/emplDept")
    public void searchGroup(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 올가매장상품정보 ****/
    @PostMapping("/orgashopStock")
    public void inputOrGoods(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/orgashopStock")
    public void updateOrGoods(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/orgashopStock")
    public void searchOrGoods(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 매장정보 ****/
    @PostMapping("/store")
    public void inputShop(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/store")
    public void updateShop(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/store")
    public void searchShop(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 매장배송관리 ****/
    @PostMapping("/storeDeliver")
    public void inputDeliver(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/storeDeliver")
    public void updateDeliver(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/storeDeliver")
    public void searchDeliver(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 매장주문시간 ****/
    @PostMapping("/storeOrdtime")
    public void inputOrdTime(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/storeOrdtime")
    public void updateOrdTime(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/storeOrdtime")
    public void searchOrdTime(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 배송권역 ****/
    @PostMapping("/dlvzone")
    public void inputDlvZone(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/dlvzone")
    public void updateDlvZone(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/dlvzone")
    public void searchDlvZone(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 휴무일정보 ****/
    @PostMapping("/holiday")
    public void inputPersDay(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/holiday")
    public void updatePersDay(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/holiday")
    public void searchPersDay(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** VOC ****/
    @PostMapping("/voc")
    public void inputVOC(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/voc")
    public void searchVOC(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/voc")
    public void updateVOC(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 3PL상품정보 ****/
    @PostMapping("/goods3pl")
    public void inputGoods3pl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/goods3pl")
    public void searchGoods3pl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/goods3pl")
    public void updateGoods3pl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 3PL상품변경 ****/
    @PostMapping("/goods3plUpd")
    public void inputGoods3plUpd(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/goods3plUpd")
    public void searchGoods3plUpd(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/goods3plUpd")
    public void updateGoods3plUpd(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 3PL재고수량 ****/
    @PostMapping("/stock3pl")
    public void inputStock3pl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/stock3pl")
    public void searchStock3pl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/stock3pl")
    public void updateStock3pl(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 3PL재고조정 ****/
    @PostMapping("/stock3plAdj")
    public void inputStock3plAdj(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/stock3plAdj")
    public void searchStock3plAdj(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/stock3plAdj")
    public void updateStock3plAdj(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 온라인몰재고현황 ****/
    @PostMapping("/stockEshop")
    public void inputStockEshop(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/stockEshop")
    public void updateStockEshop(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/stockEshop")
    public void searchStockEshop(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 보냉백 회수현황 ****/
    @PostMapping("/bagCancel")
    public void inputBagCancel(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/bagCancel")
    public void updateBagCancel(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/bagCancel")
    public void searchBagCancel(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

	/**** 임직원지원금 AP 송장 발행 ****/
    @PostMapping("/employeeBenefitAp")
    public void inputEmployeeBenefitAp(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/employeeBenefitAp")
    public void updateEmployeeBenefitAp(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/employeeBenefitAp")
    public void searchEmployeeBenefitAp(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    /**** 올가 OFFLINE ****/
    @PostMapping("/orgaOfflinePo")
    public void inputOrgaOfflinePo(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/orgaOfflinePo")
    public void updateOrgaOfflinePo(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/orgaOfflinePo")
    public void searchOrgaOfflinePo(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }
    
    /**** 주문|취소 테스트 ****/
    @PostMapping("/custordt")
    public void inputPOT(HttpServletRequest request, HttpServletResponse response) throws SQLIntegrityConstraintViolationException {
		metaUtil.interfaces(request, response);
    }

    @GetMapping("/custordt")
    public void searchPOT(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }

    @PutMapping("/custordt")
    public void updatePOT(HttpServletRequest request, HttpServletResponse response) {
		metaUtil.interfaces(request, response);
    }


}
