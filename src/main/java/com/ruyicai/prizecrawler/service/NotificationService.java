package com.ruyicai.prizecrawler.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.prizecrawler.domain.Notification;
import com.ruyicai.prizecrawler.enums.NoticeStateType;

@Service("notificationService")
@Transactional
public class NotificationService {

	@PersistenceContext
	private EntityManager em;

	public void merge(Notification notification) {
		em.merge(notification);
	}

	@Transactional(readOnly = true)
	public List<Notification> findNoNotice() {
		TypedQuery<Notification> query = em.createQuery(
				"select o from Notification o where o.noticestate=?",
				Notification.class).setParameter(1, NoticeStateType.WAIT.value);
		return query.getResultList();
	}
}
