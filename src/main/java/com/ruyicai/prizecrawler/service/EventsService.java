package com.ruyicai.prizecrawler.service;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ruyicai.prizecrawler.domain.Events;

@Service("eventsService")
@Transactional
public class EventsService {

	@PersistenceContext
	private EntityManager em;

	@Transactional(readOnly = true)
	public Events findEvent(BigDecimal type, String name) {
		TypedQuery<Events> query = em
				.createQuery(
						"select o from Events o where o.type=? and o.name=?",
						Events.class).setParameter(1, type)
				.setParameter(2, name);

		if (query.getResultList().size() != 1) {
			return null;
		}
		return query.getSingleResult();
	}

	public void saveEvents(Events events) {

		Assert.hasText(events.getName(), "name is not has text");
		Assert.hasText(events.getShortname(), "shortname is not has text");
		Assert.isTrue(
				events.getType() != null
						&& (events.getType().equals(BigDecimal.ONE) || events
								.getType().equals(BigDecimal.ZERO)),
				"type is wrong");

		Events ishas = findEvent(events.getType(), events.getName());

		if (ishas != null) {
			throw new RuntimeException("events is exist:" + events.toString());
		}

		em.persist(events);
	}
}
