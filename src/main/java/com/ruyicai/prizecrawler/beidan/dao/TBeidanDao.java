package com.ruyicai.prizecrawler.beidan.dao;

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

import com.ruyicai.prizecrawler.beidan.TBeiDanMatches;
import com.ruyicai.prizecrawler.beidan.TBeiDanResult;

@Service
public class TBeidanDao {

	private Logger logger = LoggerFactory.getLogger(TBeidanDao.class);

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
	
	
	public TBeiDanMatches findBeidanMatch(String lotno,String batchcode,String no) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TBeiDanMatches match = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select * from tbeidanmatches t where t.lotno=? and t.batchcode=? and t.no=?");
			pstmt.setObject(1, lotno);
			pstmt.setObject(2, batchcode);
			pstmt.setObject(3, no);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				match = buildTBeiDanMatches(rs);
			}
		}catch(Exception e) {
			logger.info("findBeidanMatch出错",e);
		}finally {
			close(conn, pstmt, rs);
		}
		return match;
	}
	
	
	public int persist(TBeiDanMatches match) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("insert into tbeidanmatches (lotno,batchcode,no,leaguename,host,guest,createtime,endtime,handicap,state,salestate,audit) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setObject(1, match.getLotno());
			pstmt.setObject(2, match.getBatchcode());
			pstmt.setObject(3, match.getNo());
			pstmt.setObject(4, match.getLeaguename());
			pstmt.setObject(5, match.getHost());
			pstmt.setObject(6, match.getGuest());
			pstmt.setObject(7, match.getCreatetime());
			pstmt.setObject(8, match.getEndtime());
			pstmt.setObject(9, match.getHandicap());
			pstmt.setObject(10, match.getState());
			pstmt.setObject(11, match.getSalestate());
			pstmt.setObject(12, match.getAudit());
			return pstmt.executeUpdate();
		}catch(Exception e) {
			logger.info("persist TBeiDanMatches出错",e);
		}finally {
			close(conn, pstmt, null);
		}
		return 0;
			
	}
	
	
	
	public TBeiDanResult findBeidanResult(String lotno,String batchcode,String no) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TBeiDanResult result = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select * from tbeidanresult t where t.lotno=? and t.batchcode=? and t.no=?");
			pstmt.setObject(1, lotno);
			pstmt.setObject(2, batchcode);
			pstmt.setObject(3, no);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = buildTBeiDanResult(rs);
			}
		}catch(Exception e) {
			logger.info("findBeidanResult出错",e);
		}finally {
			close(conn, pstmt, rs);
		}
		return result;
	}
	
	
	public int persist(TBeiDanResult result) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("insert into tbeidanresult (lotno,batchcode,no,iscancel,scorehalf,scoreall,result,peilu,handicap,createtime,audittime,audit) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setObject(1, result.getLotno());
			pstmt.setObject(2, result.getBatchcode());
			pstmt.setObject(3, result.getNo());
			pstmt.setObject(4, result.getIscancel());
			pstmt.setObject(5, result.getScorehalf());
			pstmt.setObject(6, result.getScoreall());
			pstmt.setObject(7, result.getResult());
			pstmt.setObject(8, result.getPeilu());
			pstmt.setObject(9, result.getHandicap());
			pstmt.setObject(10, result.getCreatetime());
			pstmt.setObject(11, result.getAudittime());
			pstmt.setObject(12, result.getAudit());
			return pstmt.executeUpdate();
		}catch(Exception e) {
			logger.info("persist TBeiDanResult出错",e);
		}finally {
			close(conn, pstmt, null);
		}
		return 0;
			
	}
	
	
	
	
	private TBeiDanMatches buildTBeiDanMatches(ResultSet rs) throws Exception {
		TBeiDanMatches match = new TBeiDanMatches();
		match.setLotno(rs.getString("lotno"));
		match.setBatchcode(rs.getString("batchcode"));
		match.setNo(rs.getString("no"));
		match.setLeaguename(rs.getString("leaguename"));
		match.setHost(rs.getString("host"));
		match.setGuest(rs.getString("guest"));
		match.setCreatetime(rs.getDate("createtime"));
		match.setEndtime(rs.getDate("endtime"));
		match.setHandicap(rs.getBigDecimal("handicap"));
		match.setState(rs.getBigDecimal("state"));
		match.setSalestate(rs.getBigDecimal("salestate"));
		match.setAudit(rs.getBigDecimal("audit"));
		return match;
	}
	
	private TBeiDanResult buildTBeiDanResult(ResultSet rs) throws Exception {
		TBeiDanResult result = new TBeiDanResult();
		result.setLotno(rs.getString("lotno"));
		result.setBatchcode(rs.getString("batchcode"));
		result.setNo(rs.getString("no"));
		result.setIscancel(rs.getBigDecimal("iscancel"));
		result.setScoreall(rs.getString("scoreall"));
		result.setScorehalf(rs.getString("scorehalf"));
		result.setResult(rs.getString("result"));
		result.setPeilu(rs.getString("peilu"));
		result.setHandicap(rs.getBigDecimal("handicap"));
		result.setCreatetime(rs.getDate("createtime"));
		result.setAudittime(rs.getDate("audittime"));
		result.setAudit(rs.getBigDecimal("audit"));
		return result;
		
	}
	
	
	
}
