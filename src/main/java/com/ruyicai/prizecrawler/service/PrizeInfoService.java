package com.ruyicai.prizecrawler.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.prizecrawler.consts.ErrorCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.exception.PrizeInfoNullException;

@Service("prizeInfoService")
@Transactional
public class PrizeInfoService {

	@PersistenceContext
	EntityManager em;

	public void persist(PrizeInfo prizeInfo) {
		List<PrizeInfo> list = em.createQuery("select o from PrizeInfo o where o.lotno=? and o.batchcode=?", PrizeInfo.class).setParameter(1, prizeInfo.getLotno()).setParameter(2, prizeInfo.getBatchcode()).getResultList();
		if(list.isEmpty()) {
			em.persist(prizeInfo);
		}
	}

	public void merge(PrizeInfo prizeInfo) {
		em.merge(prizeInfo);
	}

	@Transactional(readOnly = true)
	public PrizeInfo findByLotnoAndBatchcode(String lotno, String batchcode)
			throws PrizeInfoNullException {

		TypedQuery<PrizeInfo> query = em
				.createQuery(
						"select o from PrizeInfo o where o.lotno=? and o.batchcode=?",
						PrizeInfo.class).setParameter(1, lotno)
				.setParameter(2, batchcode);

		if (query.getResultList().isEmpty()) {
			throw new PrizeInfoNullException(ErrorCode.PRIZEINFONULL);
		}

		return query.getSingleResult();
	}

	
	@Transactional(readOnly = true)
	public List<PrizeInfo> findNoNotice(String lotno) {
		List<PrizeInfo> list = em
				.createQuery(
						"select o from PrizeInfo o where o.lotno=? and o.crawlState=? and o.noticeState=?",
						PrizeInfo.class).setParameter(1, lotno).setParameter(2, 1)
				.setParameter(3, 0).getResultList();

		return list;
	}
	
	
	@Transactional(readOnly = true)
	public List<PrizeInfo> findALLNoNotice() {
		List<PrizeInfo> list = em
				.createQuery(
						"select o from PrizeInfo o where o.crawlState=? and o.noticeState=?",
						PrizeInfo.class).setParameter(1, 1)
				.setParameter(2, 0).getResultList();

		return list;
	}

	
	@Transactional(readOnly = true)
	public List<PrizeInfo> findNoCrawl(String lotno) {
		List<PrizeInfo> list = em
				.createQuery(
						"select o from PrizeInfo o where o.lotno=? and o.crawlState=?",
						PrizeInfo.class).setParameter(1, lotno)
				.setParameter(2, 0).getResultList();

		return list;
	}
	
	
	
	@Transactional(readOnly = true)
	public List<PrizeInfo> findAllNoCrawl() {
		List<PrizeInfo> list = em
				.createQuery(
						"select o from PrizeInfo o where o.crawlState=?",
						PrizeInfo.class).setParameter(1, 0).getResultList();

		return list;
	}

}
