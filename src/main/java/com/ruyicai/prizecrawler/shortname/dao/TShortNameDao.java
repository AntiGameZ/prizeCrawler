package com.ruyicai.prizecrawler.shortname.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.consts.ShortNameType;
import com.ruyicai.prizecrawler.shortname.ShortNames;

@Service
public class TShortNameDao {

	private Logger logger = LoggerFactory.getLogger(TShortNameDao.class);
	
	@Resource(name = "twodataSource")
	private DataSource dataSource;
	
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (null != rs) {
				rs.close();
			}
			if (null != stmt) {
				stmt.close();
			}
			if (null != conn && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * 查询北单简称
	 * @param lotno
	 * @param batchcode
	 * @param no
	 * @return
	 */
	public ShortNames findShortName(String name,int type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ShortNames forshort = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select * from tshortname t where t.name=? and t.type=?");
			pstmt.setObject(1, name);
			pstmt.setObject(2, type);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				forshort = buildShortName(rs);
			}
		}catch(Exception e) {
			logger.info("findShortName出错",e);
		}finally {
			close(conn, pstmt, rs);
		}
		return forshort;
	}
	
	public int persist(ShortNames shortname) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("insert into tshortname (name,shortname,type) values (?,?,?)");
			pstmt.setObject(1, shortname.getName());
			pstmt.setObject(2, shortname.getShortname());
			pstmt.setObject(3, shortname.getType());
			return pstmt.executeUpdate();
		}catch(Exception e) {
			logger.info("persist ShortNames出错",e);
		}finally {
			close(conn, pstmt, null);
		}
		return 0;
	}
	
	
	/**
	 * 组合short
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	private ShortNames buildShortName(ResultSet rs) throws Exception {
		ShortNames forshort = new ShortNames();
		forshort.setId(rs.getInt("id"));
		forshort.setName(rs.getString("name"));
		forshort.setShortname(rs.getString("shortname"));
		forshort.setType(rs.getInt("type"));
		return forshort;
	}
	
	
	
	public ShortNames findBeidanLeague(String name) {
		return findShortName(name, ShortNameType.BEIDAN_LEAGUE.value.intValue());
	}
	
	public ShortNames findBeidanTeam(String name) {
		return findShortName(name, ShortNameType.BEIDAN_TEAM.value.intValue());
	}
	
	public ShortNames findJingcaiZQLeague(String name) {
		return findShortName(name, ShortNameType.JINGCAI_ZQ_LEAGUE.value.intValue());
	}
	
	public ShortNames findJingcaiLQLeague(String name) {
		return findShortName(name, ShortNameType.JINGCAI_LQ_LEAGUE.value.intValue());
	}
	
	public ShortNames findJingcaiZQTeam(String name) {
		return findShortName(name, ShortNameType.JINGCAI_ZQ_TEAM.value.intValue());
	}
	
	public ShortNames findJingcaiLQTeam(String name) {
		return findShortName(name, ShortNameType.JINGCAI_LQ_TEAM.value.intValue());
	}
	
	public ShortNames findZucaiLeague(String name) {
		return findShortName(name, ShortNameType.ZUCAI_LEAGUE.value.intValue());
	}
	
	public ShortNames findZucaiTeam(String name) {
		return findShortName(name, ShortNameType.ZUCAI_TEAM.value.intValue());
	}

}
