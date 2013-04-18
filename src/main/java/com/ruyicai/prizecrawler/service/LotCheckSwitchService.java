package com.ruyicai.prizecrawler.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.prizecrawler.domain.LotCheckSwitch;

@Service("lotCheckSwitchService")
@Transactional
public class LotCheckSwitchService {

	@PersistenceContext
	private EntityManager em;
	
	
	public void merge(LotCheckSwitch lotCheckSwitch) {
		em.merge(lotCheckSwitch);
	}
	
	
	public LotCheckSwitch findByLotno(String lotno) {
		TypedQuery<LotCheckSwitch> query = em.createQuery(
				"select o from LotCheckSwitch o where o.lotno=?",
				LotCheckSwitch.class).setParameter(1, lotno);
		return query.getSingleResult();
	}
	
	
	public void updateState(String lotno,int state) {
		LotCheckSwitch lotCheckSwitch = findByLotno(lotno);
		lotCheckSwitch.setState(state);
		merge(lotCheckSwitch);
	}
	
	
	
	
	public boolean isCheck(String lotno) {
		try {
			LotCheckSwitch lotCheckSwitch = findByLotno(lotno);
			if(lotCheckSwitch.getState()==1) {
				return true;
			}
			return false;
		}catch (Exception e) {
			return false;
		}
	}
	
	
}
