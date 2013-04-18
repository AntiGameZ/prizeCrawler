package com.ruyicai.prizecrawler.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.prizecrawler.domain.Threshold;
import com.ruyicai.prizecrawler.enums.WeightType;

@Service("thresholdService")
@Transactional
public class ThresholdService {

	@PersistenceContext
	private EntityManager em;

	public void merge(Threshold threshold) {
		em.merge(threshold);
	}

	@Transactional(readOnly = true)
	public Threshold find(String lotno,WeightType type) {
		TypedQuery<Threshold> query = em.createQuery(
				"select o from Threshold o where o.lotno=? and o.type=?", Threshold.class)
				.setParameter(1, lotno).setParameter(2, type.value);

		return query.getSingleResult();
	}
}
