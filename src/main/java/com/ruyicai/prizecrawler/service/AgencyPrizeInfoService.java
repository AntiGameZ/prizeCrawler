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
import com.ruyicai.prizecrawler.domain.AgencyPrizeInfo;
import com.ruyicai.prizecrawler.domain.pk.AgencyPrizePK;
import com.ruyicai.prizecrawler.enums.CheckStateType;
import com.ruyicai.prizecrawler.enums.WeightType;

@Service("agencyPrizeInfoService")
@Transactional
public class AgencyPrizeInfoService {

	private static Logger logger = LoggerFactory.getLogger(AgencyPrizeInfoService.class);
	
	@PersistenceContext
	private EntityManager em;

	@Resource
	private AgencyService agencyService;

	private void merge(AgencyPrizeInfo agencyPrizeInfo) {
		em.merge(agencyPrizeInfo);
	}

	/**
	 * 通过agencyno,lotno,batchcode找到一个AgencyPrizeInfo,如果没有返回空
	 * 
	 * @param agencyno
	 * @param lotno
	 * @param batchcode
	 * @return
	 */
	@Transactional(readOnly = true)
	public AgencyPrizeInfo find(String agencyno, String lotno, String batchcode) {
		TypedQuery<AgencyPrizeInfo> query = em
				.createQuery(
						"select o from AgencyPrizeInfo o where o.id.agencyno=? and o.id.lotno=? and o.id.batchcode=?",
						AgencyPrizeInfo.class).setParameter(1, agencyno)
				.setParameter(2, lotno).setParameter(3, batchcode);
		if (query.getResultList()==null) {
			return null;
		}
		return query.getSingleResult();
	}

	/**
	 * 添加一个AgencyPrizeInfo，同时设置weight
	 * 
	 * @param agencyPrizeInfo
	 */
	public boolean mergeWithWeight(AgencyPrizeInfo agencyPrizeInfo) {
		Agency agency = agencyService.find(agencyPrizeInfo.getId().getLotno(),
				agencyPrizeInfo.getId().getAgencyno(),WeightType.PRIZEINFO);
		if(agency==null) {
			logger.info("Agencyno为"+agencyPrizeInfo.getId().getAgencyno()+"的渠道不存在，忽略此次开奖信息");
			return false;
		}
		agencyPrizeInfo.setWeight(agency.getWeight());
		merge(agencyPrizeInfo);
		return true;
	}
	
	
	/**
	 * 添加改彩种的所有渠道
	 * @param lotno
	 * @param batchcode
	 */
	public void createAllAgencyPrizeInfo(String lotno,String batchcode) {
		List<Agency> agencys = agencyService.find(lotno,WeightType.PRIZEINFO);
		for(Agency agency:agencys) {
			AgencyPrizeInfo info = new AgencyPrizeInfo();
			info.setId(new AgencyPrizePK(agency.getAgencyno(), lotno, batchcode));
			info.setCheckstate(CheckStateType.CREATE.value);
			info.setWeight(agency.getWeight());
			info.setWininfo("");
			info.setCreatedate(new Date());
			merge(info);
		}
	}
	
	/**
	 * 修改审核状态
	 * @param agencyPrizeInfo
	 * @param checkstate
	 */
	public void updateCheckState(AgencyPrizePK id,CheckStateType state) {
		AgencyPrizeInfo agencyPrizeInfo = find(id.getAgencyno(),id.getLotno(),id.getBatchcode());
		if(agencyPrizeInfo==null) {
			logger.info("要更新的AgencyPrizeInfo不存在,AgencyPrizePK"+id.toString());
			return;
		}
		agencyPrizeInfo.setCheckstate(state.value);
		merge(agencyPrizeInfo);
		
	}
	
	
	/**
	 * 根据lotno batchcode查询所有开奖
	 * 
	 * @param lotno
	 * @param batchcode
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AgencyPrizeInfo> find(String lotno, String batchcode) {
		TypedQuery<AgencyPrizeInfo> query = em
				.createQuery(
						"select o from AgencyPrizeInfo o where o.id.lotno=? and o.id.batchcode=?",
						AgencyPrizeInfo.class).setParameter(1, lotno)
				.setParameter(2, batchcode);
		return query.getResultList();
	}

	
	/**
	 * 通过审核状态查询AgencyPrizeInfo信息
	 * @param lotno
	 * @param batchcode
	 * @param checkstate
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AgencyPrizeInfo> findByCheckState(String lotno,
			String batchcode, CheckStateType state) {
		TypedQuery<AgencyPrizeInfo> query = em
				.createQuery(
						"select o from AgencyPrizeInfo o where o.lotno=? and o.batchcode=? and o.checkstate=?",
						AgencyPrizeInfo.class).setParameter(1, lotno)
				.setParameter(2, batchcode).setParameter(3, state.value);
		return query.getResultList();
	}
	
	
	/**
	 * 审核一个开奖详情为成功
	 * 当没有记录不能审核
	 * 当本条详情被审核过，不能审核
	 * 设置所有与当前详情相等的记录为成功，不相等的为失败
	 * @param lotno
	 * @param batchcode
	 * @param wininfo
	 */
	public void checkWininfoSuccess(String lotno,String batchcode,String wininfo) {
		List<AgencyPrizeInfo> agencyPrizeInfos = find(lotno, batchcode);
		if(agencyPrizeInfos.size()==0) {
			logger.info("彩种"+lotno+" 期号"+batchcode+"没有开奖记录，不能审核");
			return;
		}
		
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(wininfo.equals(a.getWininfo())&&a.getCheckstate()!=CheckStateType.WAIT.value) {
				logger.info("彩种"+lotno+" 期号"+batchcode+"号码"+wininfo+"已经审核过，审核状态为"+a.getCheckstate()+"(成功[1],失败[2])本次不能再审核");
				return;
			}
		}
		
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(a.getCheckstate()!=CheckStateType.CREATE.value) {
				if(wininfo.equals(a.getWininfo())) {
					updateCheckState(a.getId(), CheckStateType.PASS);
				}else {
					updateCheckState(a.getId(), CheckStateType.NOTPASS);
				}
			}
		}
	}
	
	
	/**
	 * 审核一个开奖详情为失败
	 * 如果不存在记录，不能审核
	 * 如果本条详情被审核过，不能审核
	 * 设置所有相同详情的记录为失败
	 * @param lotno
	 * @param batchcode
	 * @param wininfo
	 */
	public void checkWininfoFail(String lotno,String batchcode,String wininfo) {
		List<AgencyPrizeInfo> agencyPrizeInfos = find(lotno, batchcode);
		if(agencyPrizeInfos.size()==0) {
			logger.info("彩种"+lotno+" 期号"+batchcode+"没有开奖记录，不能审核");
			return;
		}
		
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(wininfo.equals(a.getWininfo())&&a.getCheckstate()!=CheckStateType.WAIT.value) {
				logger.info("彩种"+lotno+" 期号"+batchcode+"号码"+wininfo+"已经审核过，审核状态为"+a.getCheckstate()+"(成功[1],失败[2])本次不能再审核");
				return;
			}
		}
		
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(a.getCheckstate()!=CheckStateType.CREATE.value) {
				if(wininfo.equals(a.getWininfo())) {
					updateCheckState(a.getId(), CheckStateType.NOTPASS);
				}
			}
		}
	}

}
