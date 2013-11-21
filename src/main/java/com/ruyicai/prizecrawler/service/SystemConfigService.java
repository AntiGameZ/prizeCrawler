package com.ruyicai.prizecrawler.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.domain.SystemConfig;

@Service("systemConfigService")
public class SystemConfigService {

	@PersistenceContext
	EntityManager em;

	public String getString(String id) {
		SystemConfig config = findById(id);
		return config.getValue();
	}
	
	public int getInt(String id) {
		SystemConfig config = findById(id);
		return Integer.parseInt(config.getValue());
	}
	
	public boolean getBoolean(String id) {
		SystemConfig config = findById(id);
		return Boolean.parseBoolean(config.getValue());
	}

	public SystemConfig findById(String id) {
		return em
				.createQuery("select o from SystemConfig o where o.key=?",
						SystemConfig.class).setParameter(1, id).getResultList()
				.get(0);
	}
}
