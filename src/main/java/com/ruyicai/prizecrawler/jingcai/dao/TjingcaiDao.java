package com.ruyicai.prizecrawler.jingcai.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.consts.JingcaiState;
import com.ruyicai.prizecrawler.domain.TJingcaiParam;
import com.ruyicai.prizecrawler.domain.TjingcaiGYJMatch;
import com.ruyicai.prizecrawler.domain.TjingcaiResult;
import com.ruyicai.prizecrawler.domain.TjingcaiScore;
import com.ruyicai.prizecrawler.domain.Tjingcaimatches;

@Service
public class TjingcaiDao {

	private Logger logger = LoggerFactory.getLogger(TjingcaiDao.class);

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

	public int updateSaleflag(BigDecimal type, String day, BigDecimal weekid,
			String teamid, BigDecimal saleflag) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.saleflag=? where t.type=? and t.day=? and t.weekid=? and t.teamid=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, saleflag);
			pstmt.setObject(2, type);
			pstmt.setObject(3, day);
			pstmt.setObject(4, weekid);
			pstmt.setObject(5, teamid);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateSaleflag出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}
	
	
	public int updateShortname(BigDecimal type, String day, BigDecimal weekid,
			String teamid,String shortname) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.shortname=? where t.type=? and t.day=? and t.weekid=? and t.teamid=?");
			pstmt.setObject(1, shortname);
			pstmt.setObject(2, type);
			pstmt.setObject(3, day);
			pstmt.setObject(4, weekid);
			pstmt.setObject(5, teamid);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateShortname出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}
	
	
	public int updateTeamShortname(BigDecimal type, String day, BigDecimal weekid,
			String teamid,String teamshortname) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.teamshortname=? where t.type=? and t.day=? and t.weekid=? and t.teamid=?");
			pstmt.setObject(1, teamshortname);
			pstmt.setObject(2, type);
			pstmt.setObject(3, day);
			pstmt.setObject(4, weekid);
			pstmt.setObject(5, teamid);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateTeamShortname出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}

	public int updateEndtime(BigDecimal type, String day, BigDecimal weekid,
			String teamid, Date endtime) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.endtime=? where t.type=? and t.day=? and t.weekid=? and t.teamid=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, endtime);
			pstmt.setObject(2, type);
			pstmt.setObject(3, day);
			pstmt.setObject(4, weekid);
			pstmt.setObject(5, teamid);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateEndtime出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}

	public int updateTime(BigDecimal type, String day, BigDecimal weekid,
			String teamid, Date time, Date endtime) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.time=?, t.endtime=? where t.type=? and t.day=? and t.weekid=? and t.teamid=?");
			pstmt.setObject(1, time);
			pstmt.setObject(2, endtime);
			pstmt.setObject(3, type);
			pstmt.setObject(4, day);
			pstmt.setObject(5, weekid);
			pstmt.setObject(6, teamid);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateTime出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}

	public int updateToResulted(BigDecimal type, String day, BigDecimal weekid,
			String teamid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.state=? where t.type=? and t.day=? and t.weekid=? and t.teamid=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, JingcaiState.Resulted.value);
			pstmt.setObject(2, type);
			pstmt.setObject(3, day);
			pstmt.setObject(4, weekid);
			pstmt.setObject(5, teamid);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateToResulted出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}

	public int updateToEncashed(BigDecimal type, String day, BigDecimal weekid,
			String teamid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.state=? where t.type=? and t.day=? and t.weekid=? and t.teamid=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, JingcaiState.ENCASH.value);
			pstmt.setObject(2, type);
			pstmt.setObject(3, day);
			pstmt.setObject(4, weekid);
			pstmt.setObject(5, teamid);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateToEncashed出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}
	
	public TJingcaiParam findParamByid(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TJingcaiParam param = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from TJingcaiParam t where t.id=?");
			pstmt.setObject(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				param = new TJingcaiParam();
				param.setId(rs.getString("id"));
				param.setValue(rs.getString("value"));
				param.setMemo(rs.getString("memo"));
			}
		} catch (Exception e) {
			logger.error("竞彩findParamByid出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return param;
	}

	public TjingcaiResult findResultByid(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TjingcaiResult result = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from TjingcaiResult t where t.id=?");
			pstmt.setObject(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = buildTjingcaiResult(rs);
			}
		} catch (Exception e) {
			logger.error("竞彩findResultByid出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return result;
	}

	private TjingcaiResult buildTjingcaiResult(ResultSet rs) throws Exception {
		TjingcaiResult result = new TjingcaiResult();
		result.setId(rs.getString("id"));
		result.setB0(rs.getString("b0"));
		result.setB1(rs.getString("b1"));
		result.setB2(rs.getString("b2"));
		result.setB3(rs.getString("b3"));
		result.setB4(rs.getString("b4"));
		result.setB5(rs.getString("b5"));
		result.setB6(rs.getString("b6"));
		result.setBasepoint(rs.getString("basepoint"));
		result.setCancel(rs.getBigDecimal("cancel"));
		result.setFirsthalfresult(rs.getString("firsthalfresult"));
		result.setLetpoint(rs.getString("letpoint"));
		result.setResult(rs.getString("result"));
		result.setAudit(rs.getBigDecimal("audit"));
		return result;
	}

	public List<String> findMatchlist(BigDecimal type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select CONCAT(t.day, '_', t.weekid, '_', t.teamid) from Tjingcaimatches t where t.saleflag=? and t.endtime > ? and t.type=? and (t.audit is null or t.audit = 0) order by t.time asc, t.teamid asc");
			pstmt.setObject(1, BigDecimal.ZERO);
			pstmt.setObject(2, new Date());
			pstmt.setObject(3, type);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error("竞彩findBytype出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	public List<Tjingcaimatches> findBytype(BigDecimal type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tjingcaimatches> list = new ArrayList<Tjingcaimatches>();
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.saleflag=? and t.endtime > ? and t.type=? and (t.audit is null or t.audit = 0) order by t.time asc, t.teamid asc");
			pstmt.setObject(1, BigDecimal.ZERO);
			pstmt.setObject(2, new Date());
			pstmt.setObject(3, type);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tjingcaimatches tjingcaimatches = buildTjingcaimatches(rs);
				list.add(tjingcaimatches);
			}
		} catch (Exception e) {
			logger.error("竞彩findBytype出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	public List<Tjingcaimatches> findBytypeAndtime(BigDecimal type, String time) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tjingcaimatches> list = new ArrayList<Tjingcaimatches>();
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.saleflag=? and day = ? and t.type=? and (t.audit is null or t.audit = 0) order by t.time asc, t.teamid asc");
			pstmt.setObject(1, BigDecimal.ZERO);
			pstmt.setObject(2, time);
			pstmt.setObject(3, type);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tjingcaimatches tjingcaimatches = buildTjingcaimatches(rs);
				list.add(tjingcaimatches);
			}
		} catch (Exception e) {
			logger.error("竞彩findBytypeAndtime出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	public List<String> findActiveTimes(BigDecimal type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("SELECT day AS activetime FROM Tjingcaimatches t WHERE t.saleflag=? AND t.endtime > ? AND t.type=?  and (t.audit is null or t.audit = 0) GROUP BY t.day");
			pstmt.setObject(1, BigDecimal.ZERO);
			pstmt.setObject(2, new Date());
			pstmt.setObject(3, type);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("activetime"));
			}
		} catch (Exception e) {
			logger.error("竞彩findActiveTimes出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	public Object[] findByDateAndType(BigDecimal type, String date) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tjingcaimatches> matches = new LinkedList<Tjingcaimatches>();
		List<TjingcaiResult> results = new LinkedList<TjingcaiResult>();
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM tjingcaimatches t LEFT JOIN tjingcairesult r ON CONCAT(t.type, '_', t.day, '_', t.weekid, '_', t.teamid) = r.id WHERE DATE_FORMAT(t.endtime, '%Y%m%d') = ? and t.type=? and (t.audit is null or t.audit = 0) ORDER BY t.endtime ASC, t.teamid ASC");
			pstmt.setObject(1, date);
			pstmt.setObject(2, type);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tjingcaimatches tjingcaimatches = buildTjingcaimatches(rs);
				TjingcaiResult tjingcaiResult = buildTjingcaiResult(rs);
				matches.add(tjingcaimatches);
				results.add(tjingcaiResult);
			}
		} catch (Exception e) {
			logger.error("竞彩findByDateAndType出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return new Object[] { matches, results };
	}

	public List<Tjingcaimatches> findByStateAndType(BigDecimal state,
			BigDecimal type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tjingcaimatches> list = new ArrayList<Tjingcaimatches>();
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.state=? and t.type=? and (t.audit is null or t.audit = 0) order by day asc, weekid asc, teamid asc");
			pstmt.setObject(1, state);
			pstmt.setObject(2, type);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tjingcaimatches tjingcaimatches = buildTjingcaimatches(rs);
				list.add(tjingcaimatches);
			}
		} catch (Exception e) {
			logger.error("竞彩findTjingcaimatches出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	public List<Tjingcaimatches> findTjingcaimatches(BigDecimal type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tjingcaimatches> list = new ArrayList<Tjingcaimatches>();
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.saleflag=? and t.endtime > ? and t.type=? and (t.audit is null or t.audit = 0) order by t.time asc, t.teamid asc");
			pstmt.setObject(1, BigDecimal.ZERO);
			pstmt.setObject(2, new Date());
			pstmt.setObject(3, type);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tjingcaimatches tjingcaimatches = buildTjingcaimatches(rs);
				list.add(tjingcaimatches);
			}
		} catch (Exception e) {
			logger.error("竞彩findTjingcaimatches出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	private Tjingcaimatches buildTjingcaimatches(ResultSet rs) throws Exception {
		Tjingcaimatches tjingcaimatches = new Tjingcaimatches();
		tjingcaimatches.setType(rs.getBigDecimal("type"));
		tjingcaimatches.setDay(rs.getString("day"));
		tjingcaimatches.setWeekid(rs.getBigDecimal("weekid"));
		tjingcaimatches.setTeamid(rs.getString("teamid"));
		Timestamp timestamp = rs.getTimestamp("endtime");
		tjingcaimatches.setEndtime(new Date(timestamp.getTime()));
		tjingcaimatches.setSaleflag(rs.getBigDecimal("saleflag"));
		tjingcaimatches.setState(rs.getBigDecimal("state"));
		tjingcaimatches.setLeague(rs.getString("league"));
		tjingcaimatches.setTeam(rs.getString("team"));
		timestamp = rs.getTimestamp("time");
		tjingcaimatches.setTime(new Date(timestamp.getTime()));
		tjingcaimatches.setUnsupport(rs.getString("unsupport"));
		tjingcaimatches.setAudit(rs.getBigDecimal("audit"));
		tjingcaimatches.setShortname(rs.getString("shortname"));
		tjingcaimatches.setLetpoint(rs.getString("letpoint"));
		tjingcaimatches.setTeamshortname(rs.getString("teamshortname"));
		return tjingcaimatches;
	}
	
	public Tjingcaimatches findTjingcaimatches(BigDecimal type, BigDecimal weekid, String teamid, String league, String team, String day) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Tjingcaimatches tjingcaimatches = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.type=? and t.weekid=? and t.teamid=? and t.league=? and t.team=? and t.day=?");
			pstmt.setObject(1, type);
			pstmt.setObject(2, weekid);
			pstmt.setObject(3, teamid);
			pstmt.setObject(4, league);
			pstmt.setObject(5, team);
			pstmt.setObject(6, day);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				tjingcaimatches = buildTjingcaimatches(rs);
			}
		} catch (Exception e) {
			logger.error("竞彩findTjingcaimatches出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return tjingcaimatches;
	}
	
	public Tjingcaimatches findTjingcaimatches(BigDecimal type, BigDecimal weekid, String teamid, String league, String team) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Tjingcaimatches tjingcaimatches = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.type=? and t.weekid=? and t.teamid=? and t.league=? and t.team=? order by t.day desc");
			pstmt.setObject(1, type);
			pstmt.setObject(2, weekid);
			pstmt.setObject(3, teamid);
			pstmt.setObject(4, league);
			pstmt.setObject(5, team);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				tjingcaimatches = buildTjingcaimatches(rs);
			}
		} catch (Exception e) {
			logger.error("竞彩findTjingcaimatches出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return tjingcaimatches;
	}
	
	public int findAnnouncement(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select count(*) from Announcement t where t.id=?");
			pstmt.setObject(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("竞彩findAnnouncement出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return 0;
	}
	
	public int persistAnnouncement(String id, String content) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("insert into Announcement(id, content) values(?,?)");
			pstmt.setObject(1, id);
			pstmt.setObject(2, content);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩persistAnnouncement出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}

	public Tjingcaimatches findTjingcaimatches(BigDecimal type, String day,
			BigDecimal weekid, String teamid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Tjingcaimatches tjingcaimatches = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.type=? and t.day=? and t.weekid=? and t.teamid=?");
			pstmt.setObject(1, type);
			pstmt.setObject(2, day);
			pstmt.setObject(3, weekid);
			pstmt.setObject(4, teamid);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				tjingcaimatches = buildTjingcaimatches(rs);
			}
		} catch (Exception e) {
			logger.error("竞彩findTjingcaimatches出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return tjingcaimatches;
	}

	public Date findEndtime(BigDecimal type, String day, BigDecimal weekid,
			String teamid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Date endtime = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select endtime from Tjingcaimatches t where t.type=? and t.day=? and t.weekid=? and t.teamid=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, type);
			pstmt.setObject(2, day);
			pstmt.setObject(3, weekid);
			pstmt.setObject(4, teamid);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Timestamp timestamp = rs.getTimestamp("endtime");
				endtime = new Date(timestamp.getTime());
			}
		} catch (Exception e) {
			logger.error("竞彩findEndtime出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return endtime;
	}

	public Object[] findTjingcaiMatchesresult(BigDecimal type, String day,
			BigDecimal weekid, String teamid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM tjingcaimatches t LEFT JOIN tjingcairesult r ON CONCAT(t.type, '_', t.day, '_', t.weekid, '_', t.teamid) = r.id WHERE t.type=? and t.day=? and t.weekid=? and t.teamid=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, type);
			pstmt.setObject(2, day);
			pstmt.setObject(3, weekid);
			pstmt.setObject(4, teamid);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return new Object[] { buildTjingcaimatches(rs),
						buildTjingcaiResult(rs) };
			}
		} catch (Exception e) {
			logger.error("竞彩findTjingcaimatches出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return null;
	}

	public void checktoend() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.saleflag=?, t.state=? where t.endtime <= ? and t.state=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, BigDecimal.ONE);
			pstmt.setObject(2, JingcaiState.END.value);
			pstmt.setObject(3, new Date());
			pstmt.setObject(4, JingcaiState.Normal.value);
			pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩checktoend出错", e);
		} finally {
			close(conn, pstmt, null);
		}
	}

	public void updateToend(BigDecimal type, String day, BigDecimal weekid,
			String teamid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update Tjingcaimatches t set t.saleflag=?, t.state=? where t.type=? and t.day=? and t.weekid=? and t.teamid=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, BigDecimal.ONE);
			pstmt.setObject(2, JingcaiState.END.value);
			pstmt.setObject(3, type);
			pstmt.setObject(4, day);
			pstmt.setObject(5, weekid);
			pstmt.setObject(6, teamid);
			pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateToend出错", e);
		} finally {
			close(conn, pstmt, null);
		}
	}

	public List<Tjingcaimatches> findToend() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tjingcaimatches> list = new ArrayList<Tjingcaimatches>();
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from Tjingcaimatches t where t.endtime <= ? and t.state=? and (t.audit is null or t.audit = 0)");
			pstmt.setObject(1, new Date());
			pstmt.setObject(2, JingcaiState.Normal.value);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tjingcaimatches tjingcaimatches = buildTjingcaimatches(rs);
				list.add(tjingcaimatches);
			}
		} catch (Exception e) {
			logger.error("竞彩findToend出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	public int persist(Tjingcaimatches tjingcaimatches) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("insert into Tjingcaimatches(type,day,weekid,teamid,endtime,saleflag,state,league,team,time,unsupport,audit,ctstate,letpoint) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setObject(1, tjingcaimatches.getType());
			pstmt.setObject(2, tjingcaimatches.getDay());
			pstmt.setObject(3, tjingcaimatches.getWeekid());
			pstmt.setObject(4, tjingcaimatches.getTeamid());
			pstmt.setObject(5, tjingcaimatches.getEndtime());
			pstmt.setObject(6, tjingcaimatches.getSaleflag());
			pstmt.setObject(7, tjingcaimatches.getState());
			pstmt.setObject(8, tjingcaimatches.getLeague());
			pstmt.setObject(9, tjingcaimatches.getTeam());
			pstmt.setObject(10, tjingcaimatches.getTime());
			pstmt.setObject(11, tjingcaimatches.getUnsupport());
			pstmt.setObject(12, tjingcaimatches.getAudit());
			pstmt.setObject(13, tjingcaimatches.getCtstate());
			pstmt.setObject(14, tjingcaimatches.getLetpoint());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩persist出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}

	public int persist(TjingcaiResult tjingcaiResult) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO tjingcairesult(id,letpoint,basepoint,result,firsthalfresult,b0,b1,b2,b3,b4,b5,b6,cancel,audit) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setObject(1, tjingcaiResult.getId());
			pstmt.setObject(2, tjingcaiResult.getLetpoint());
			pstmt.setObject(3, tjingcaiResult.getBasepoint());
			pstmt.setObject(4, tjingcaiResult.getResult());
			pstmt.setObject(5, tjingcaiResult.getFirsthalfresult());
			pstmt.setObject(6, tjingcaiResult.getB0());
			pstmt.setObject(7, tjingcaiResult.getB1());
			pstmt.setObject(8, tjingcaiResult.getB2());
			pstmt.setObject(9, tjingcaiResult.getB3());
			pstmt.setObject(10, tjingcaiResult.getB4());
			pstmt.setObject(11, tjingcaiResult.getB5());
			pstmt.setObject(12, tjingcaiResult.getB6());
			pstmt.setObject(13, tjingcaiResult.getCancel());
			pstmt.setObject(14, tjingcaiResult.getAudit());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩persist出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}
	
	
	
	
	public TjingcaiGYJMatch getGYJMatche(String saishi,BigDecimal type,String number) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TjingcaiGYJMatch tjingcaiGYJMatch = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select * from tjingcaigyjmatch t where t.saishi = ? and t.type=? and t.number=?");
			pstmt.setObject(1, saishi);
			pstmt.setObject(2, type);
			pstmt.setObject(3, number);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				tjingcaiGYJMatch = buildTjingcaiGYJMatch(rs);
			}
		} catch (Exception e) {
			logger.error("竞彩findToend出错", e);
		} finally {
			close(conn, pstmt, rs);
		}
		return tjingcaiGYJMatch;
	}
	
	
	private TjingcaiGYJMatch buildTjingcaiGYJMatch(ResultSet rs) throws Exception {
		TjingcaiGYJMatch tjingcaiGYJMatch = new TjingcaiGYJMatch();
		tjingcaiGYJMatch.setId(rs.getLong("id"));
		tjingcaiGYJMatch.setSaishi(rs.getString("saishi"));
		tjingcaiGYJMatch.setType(rs.getBigDecimal("type"));
		tjingcaiGYJMatch.setNumber(rs.getString("number"));
		tjingcaiGYJMatch.setTeam(rs.getString("team"));
		tjingcaiGYJMatch.setState(rs.getBigDecimal("state"));
		tjingcaiGYJMatch.setAward(rs.getString("award"));
		tjingcaiGYJMatch.setPopularityRating(rs.getString("popularityRating"));
		tjingcaiGYJMatch.setProbability(rs.getString("probability"));
		return tjingcaiGYJMatch;
	}
	
	
	
	public void updateTjingcaiGYCMatches(String saishi, BigDecimal type,String number,
			BigDecimal state,String award,String popularityRating,String probability,long id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update tjingcaigyjmatch t set t.award=? , t.popularityRating=? , t.probability=? where t.saishi=? and t.type=? and t.number=? and t.id=?");
//			pstmt.setObject(1, state);
			pstmt.setObject(1, award);
			pstmt.setObject(2, popularityRating);
			pstmt.setObject(3, probability);
			pstmt.setObject(4, saishi);
			pstmt.setObject(5, type);
			pstmt.setObject(6, number);
			pstmt.setObject(7, id);
			pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateTjingcaiGYCMatches出错", e);
		} finally {
			close(conn, pstmt, null);
		}
	}
	
	
	public int persist(TjingcaiGYJMatch match) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO tjingcaigyjmatch(type,number,team,state,award,popularityRating,probability,saishi) VALUES (?,?,?,?,?,?,?,?)");
			pstmt.setObject(1, match.getType());
			pstmt.setObject(2, match.getNumber());
			pstmt.setObject(3, match.getTeam());
			pstmt.setObject(4, match.getState());
			pstmt.setObject(5, match.getAward());
			pstmt.setObject(6, match.getPopularityRating());
			pstmt.setObject(7, match.getProbability());
			pstmt.setObject(8, match.getSaishi());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩persist TjingcaiGYJMatch出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}
	
	
	public void updateLetPoint(BigDecimal type,String day,BigDecimal weekid,String teamid,String letpoint) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update tjingcaimatches t set t.letpoint=? where t.type=? and t.day=? and t.weekid=? and t.teamid=?");
			pstmt.setObject(1, letpoint);
			pstmt.setObject(2, type);
			pstmt.setObject(3, day);
			pstmt.setObject(4, weekid);
			pstmt.setObject(5, teamid);
			pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩updateLetPoint出错", e);
		} finally {
			close(conn, pstmt, null);
		}
	}
	
	public TjingcaiScore findTjingcaiScore(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TjingcaiScore score = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select * from tjingcaiscore t where t.id=?");
			pstmt.setObject(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				score = buildTjingcaiScore(rs);
			}
		}catch(Exception e) {
			logger.info("findTjingcaiScore出错",e);
		}finally {
			close(conn, pstmt, rs);
		}
		return score;
	}
	
	public int persist(TjingcaiScore score) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO tjingcaiscore(id,cancel,result,firsthalfresult,createtime,audittime,auditname,audit) VALUES (?,?,?,?,?,?,?,?)");
			pstmt.setObject(1, score.getId());
			pstmt.setObject(2, score.getCancel());
			pstmt.setObject(3, score.getResult());
			pstmt.setObject(4, score.getFirsthalfresult());
			pstmt.setObject(5, score.getCreatetime());
			pstmt.setObject(6, score.getAudittime());
			pstmt.setObject(7, score.getAuditname());
			pstmt.setObject(8, score.getAudit());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("竞彩persist TjingcaiScore出错", e);
		} finally {
			close(conn, pstmt, null);
		}
		return 0;
	}
	
	
	private TjingcaiScore buildTjingcaiScore(ResultSet rs) throws Exception {
		TjingcaiScore score = new TjingcaiScore();
		score.setId(rs.getString("id"));
		score.setCancel(rs.getBigDecimal("cancel"));
		score.setResult(rs.getString("result"));
		score.setFirsthalfresult(rs.getString("firsthalfresult"));
		score.setCreatetime(new Date(rs.getTimestamp("createtime").getTime()));
		if(rs.getTimestamp("audittime")!=null) {
			score.setAudittime(new Date(rs.getTimestamp("audittime").getTime()));
		}
		score.setAuditname(rs.getString("auditname"));
		return score;
	}
}
