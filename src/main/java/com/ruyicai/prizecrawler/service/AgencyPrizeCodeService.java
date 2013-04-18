package com.ruyicai.prizecrawler.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.prizecrawler.domain.Agency;
import com.ruyicai.prizecrawler.domain.AgencyPrizeCode;
import com.ruyicai.prizecrawler.domain.pk.AgencyPrizePK;
import com.ruyicai.prizecrawler.enums.CheckStateType;
import com.ruyicai.prizecrawler.enums.CrawkStateType;
import com.ruyicai.prizecrawler.enums.WeightType;

@Service("agencyPrizeCodeService")
@Transactional
public class AgencyPrizeCodeService {

	private static Logger logger = LoggerFactory.getLogger(AgencyPrizeCodeService.class);
	
	@PersistenceContext
	private EntityManager em;

	@Resource
	private AgencyService agencyService;

	private void merge(AgencyPrizeCode agencyPrizeCode) {
		em.merge(agencyPrizeCode);
	}

	
	

	/**
	 * 添加一个AgencyPrizeCode，同时设置weight
	 * 
	 * @param agencyPrizeCode
	 */
	public boolean mergeWithWeight(AgencyPrizeCode agencyPrizeCode) {
		Agency agency = agencyService.find(agencyPrizeCode.getId().getLotno(),
				agencyPrizeCode.getId().getAgencyno(),WeightType.PRIZECODE);
		if(agency==null) {
			logger.info("Agencyno为"+agencyPrizeCode.getId().getAgencyno()+"的渠道不存在，忽略此次开奖号码信息");
			return false;
		}
		agencyPrizeCode.setWeight(agency.getWeight());
		merge(agencyPrizeCode);
		return true;
	}
	
	
	public boolean mergeIfAgencyExist(AgencyPrizeCode agencyPrizeCode) {
		Agency agency = agencyService.find(agencyPrizeCode.getId().getLotno(),
				agencyPrizeCode.getId().getAgencyno(),WeightType.PRIZECODE);
		if(agency==null) {
			logger.info("Agencyno为"+agencyPrizeCode.getId().getAgencyno()+"的渠道不存在，不能插入或者更新");
			return false;
		}
		merge(agencyPrizeCode);
		return true;
	}
	
	
	/**
	 * 添加改彩种的所有渠道
	 * @param lotno
	 * @param batchcode
	 */
	public void createAllAgencyPrizeCode(String lotno,String batchcode) {
		List<Agency> agencys = agencyService.find(lotno,WeightType.PRIZECODE);
		for(Agency agency:agencys) {
			AgencyPrizeCode code = new AgencyPrizeCode();
			code.setId(new AgencyPrizePK(agency.getAgencyno(), lotno, batchcode));
			code.setCheckstate(CheckStateType.CREATE.value);
			code.setCrawlstate(agency.getIscrawl()==1?CrawkStateType.WAIT.value:CrawkStateType.SUCCESS.value);
			code.setCrawltimes(0);
			code.setWeight(agency.getWeight());
			code.setWincode("");
			code.setCreatedate(new Date());
			merge(code);
		}
	}
	
	
	
	/**
	 * 修改审核状态
	 * @param AgencyPrizeCode
	 * @param checkstate
	 */
	public void updateCheckState(AgencyPrizePK id,CheckStateType state) {
		AgencyPrizeCode agencyPrizeCode = find(id.getAgencyno(),id.getLotno(),id.getBatchcode());
		if(agencyPrizeCode==null) {
			logger.info("要更新的AgencyPrizeCode不存在,AgencyPrizePK"+id.toString());
			return;
		}
		agencyPrizeCode.setCheckstate(state.value);
		merge(agencyPrizeCode);
		
	}
	
	
	/**
	 * 根据lotno batchcode查询所有已经抓取的开奖
	 * 
	 * @param lotno
	 * @param batchcode
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AgencyPrizeCode> find(String lotno, String batchcode) {
		TypedQuery<AgencyPrizeCode> query = em
				.createQuery(
						"select o from AgencyPrizeCode o where o.id.lotno=? and o.id.batchcode=?",
						AgencyPrizeCode.class).setParameter(1, lotno)
				.setParameter(2, batchcode);
		return query.getResultList();
	}
	
	
	/**
	 * 根据lotno batchcode crawlstate查询开奖
	 * @param lotno
	 * @param batchcode
	 * @param crawlstate
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AgencyPrizeCode> findNotCrawl() {
		TypedQuery<AgencyPrizeCode> query = em
				.createQuery(
						"select o from AgencyPrizeCode o where o.crawlstate=?",
						AgencyPrizeCode.class).setParameter(1, CrawkStateType.WAIT.value);
		return query.getResultList();
	}
	
	
	/**
	 * 通过agencyno,lotno,batchcode找到一个AgencyPrizeCode,如果没有返回空
	 * 
	 * @param agencyno
	 * @param lotno
	 * @param batchcode
	 * @return
	 */
	@Transactional(readOnly = true)
	public AgencyPrizeCode find(String agencyno, String lotno, String batchcode) {
		TypedQuery<AgencyPrizeCode> query = em
				.createQuery(
						"select o from AgencyPrizeCode o where o.id.agencyno=? and o.id.lotno=? and o.id.batchcode=?",
						AgencyPrizeCode.class).setParameter(1, agencyno)
				.setParameter(2, lotno).setParameter(3, batchcode);
		if (query.getResultList()==null) {
			return null;
		}
		return query.getSingleResult();
	}

	
	/**
	 * 通过审核状态查询AgencyPrizeCode信息
	 * @param lotno
	 * @param batchcode
	 * @param checkstate
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AgencyPrizeCode> findByCheckState(String lotno,
			String batchcode,CheckStateType state) {
		TypedQuery<AgencyPrizeCode> query = em
				.createQuery(
						"select o from AgencyPrizeCode o where o.lotno=? and o.batchcode=? and o.checkstate=?",
						AgencyPrizeCode.class).setParameter(1, lotno)
				.setParameter(2, batchcode).setParameter(3, state.value);
		return query.getResultList();
	}
	
	
	/**
	 * 审核一个开奖号码为成功
	 * 当没有记录的时候，不能审核
	 * 当本个号码被审核过不能审核
	 * 设置所有相同号码记录为成功不相同为失败
	 * @param lotno
	 * @param batchcode
	 * @param wincode
	 */
	public void checkWincodeSuccess(String lotno,String batchcode,String wincode) {
		List<AgencyPrizeCode> agencyPrizeCodes = find(lotno, batchcode);
		if(agencyPrizeCodes.size()==0) {
			logger.info("彩种"+lotno+" 期号"+batchcode+"没有开奖记录，不能审核");
			return;
		}
		
		for(AgencyPrizeCode a:agencyPrizeCodes) {
			if(wincode.equals(a.getWincode())&&a.getCheckstate()!=CheckStateType.WAIT.value) {
				logger.info("彩种"+lotno+" 期号"+batchcode+"号码"+wincode+"已经审核过，审核状态为"+a.getCheckstate()+"(成功[1],失败[2])本次不能再审核");
				return;
			}
		}
		
		for(AgencyPrizeCode a:agencyPrizeCodes) {
			if(a.getCheckstate()!=CheckStateType.CREATE.value) {
				if(wincode.equals(a.getWincode())) {
					updateCheckState(a.getId(), CheckStateType.PASS);
				}else {
					updateCheckState(a.getId(), CheckStateType.NOTPASS);
				}
			}
		}
	}
	
	
	
	/**
	 * 审核一个开奖号码为失败
	 * 当记录为空不能审核
	 * 当本条记录被审核过不能审核
	 * 设置与本条记录相同的记录为失败
	 * @param lotno
	 * @param batchcode
	 * @param wincode
	 */
	public void checkWincodeFail(String lotno,String batchcode,String wincode) {
		List<AgencyPrizeCode> agencyPrizeCodes = find(lotno, batchcode);
		if(agencyPrizeCodes.size()==0) {
			logger.info("彩种"+lotno+" 期号"+batchcode+"没有开奖记录，不能审核");
			return;
		}
		
		for(AgencyPrizeCode a:agencyPrizeCodes) {
			if(wincode.equals(a.getWincode())&&a.getCheckstate()!=CheckStateType.WAIT.value) {
				logger.info("彩种"+lotno+" 期号"+batchcode+"号码"+wincode+"已经审核过，审核状态为"+a.getCheckstate()+"(成功[1],失败[2])本次不能再审核");
				return;
			}
		}
		
		for(AgencyPrizeCode a:agencyPrizeCodes) {
			if(a.getCheckstate()!=CheckStateType.CREATE.value) {
				if(wincode.equals(a.getWincode())) {
					updateCheckState(a.getId(), CheckStateType.NOTPASS);
				}
			}
		}
	}
}
