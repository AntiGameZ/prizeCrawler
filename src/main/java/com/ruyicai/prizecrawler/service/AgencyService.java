package com.ruyicai.prizecrawler.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.prizecrawler.domain.Agency;
import com.ruyicai.prizecrawler.enums.WeightType;

@Service("agencyService")
@Transactional
public class AgencyService {

	@PersistenceContext
	private EntityManager em;

	public void merge(Agency agency) {
		em.merge(agency);
	}

	@Transactional(readOnly = true)
	public Agency find(String lotno, String agencyno,WeightType type) {
		TypedQuery<Agency> query = em
				.createQuery(
						"select o from Agency o where o.lotno=? and o.agencyno=? and o.type=?",
						Agency.class).setParameter(1, lotno)
				.setParameter(2, agencyno).setParameter(3, type.value);

		if(query.getResultList().size()!=1) {
			return null;
		}
		return query.getSingleResult();
	}
	
	
	@Transactional(readOnly = true)
	public List<Agency> find(String lotno,WeightType type) {
		TypedQuery<Agency> query = em
				.createQuery(
						"select o from Agency o where o.lotno=? and o.type=?",
						Agency.class).setParameter(1, lotno).setParameter(2, type.value);

		return query.getResultList();
	}

}
