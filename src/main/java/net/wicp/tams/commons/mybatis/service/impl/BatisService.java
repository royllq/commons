package net.wicp.tams.commons.mybatis.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.mybatis.service.IbatisService;
import net.wicp.tams.commons.web.PageAssist;
import net.wicp.tams.commons.web.service.IPageBuild;

@SuppressWarnings("rawtypes")
public class BatisService implements IbatisService {
	private final Logger logger = LogHelp.getLogger(getClass());

	private SqlSessionFactory sqlSessionFactory;

	private IPageBuild pageBuild;

	public List queryForList(String sqlId, Object... keysAndValues) {
		if (ArrayUtils.isEmpty(keysAndValues)) {
			return queryForList(sqlId, new HashMap());
		}
		JSONObject json = new JSONObject(keysAndValues);
		return queryForList(sqlId, json.toMap());
	}


	public List queryForList(String sqlId, Map params) {
		long t1 = System.currentTimeMillis();
		List list = getSqlSession().selectList(sqlId, params);
		long t2 = System.currentTimeMillis();
		logger.info("\"{}\"查询耗时{}ms：", sqlId, (t2 - t1));
		return list;
	}

	@Override
	public PageAssist queryForPagedList(String listSqlId, String countSqlId, Map params, int pageSize, int pageNo,
			long countNum, boolean pagedByDb) {
		RowBounds rowbound = new RowBounds((pageNo - 1) * pageSize, pageSize);
		String countSqlIdTrue = StringUtil.hasNull(countSqlId, listSqlId + "_count");
		long t1 = System.currentTimeMillis();
		PageAssist pc = new PageAssist(pageSize, pageNo);
		if (countNum < 0) {// 如果总记录数不为空则不再计算记录数
			Object obj = getSqlSession().selectOne(countSqlIdTrue, params);
			if (obj != null) {
				int allNum = (Integer) obj;
				pc.setAllNum(allNum);
			}
		} else {
			pc.setAllNum(countNum);
		}

		long t2 = System.currentTimeMillis();
		logger.info("{}：查询总数耗时{}ms：", listSqlId, (t2 - t1));
		if (pagedByDb) {// 真分页
			List list = getSqlSession().selectList(listSqlId, params, rowbound);
			pc.setResult(list);
			long t3 = System.currentTimeMillis();
			logger.info("{}：真分页查询耗时{}ms：", listSqlId, (t3 - t2));
			return pc;
		} else {// 假分页也就是不分页
			List list = getSqlSession().selectList(listSqlId, params);
			pc.setResult(list);
			long t3 = System.currentTimeMillis();
			logger.info("{}：假分页查询耗时{}ms：", listSqlId, (t3 - t2));
			return pc;
		}

	}

	@Override
	public PageAssist queryForPagedList(String listSqlId, String countSqlId, Map params, HttpServletRequest request,
			boolean pagedByDb) {
		PageAssist pageAssist = pageBuild.build(request);
		return queryForPagedList(listSqlId, countSqlId, params, pageAssist.getPageSize(), pageAssist.getPageNo(),
				pageAssist.getAllNum(), pagedByDb);
	}

	@Override
	public PageAssist queryForPagedList(String listSqlId, Map params, HttpServletRequest request, boolean pagedByDb) {
		PageAssist pageAssist = pageBuild.build(request);
		return queryForPagedList(listSqlId, null, params, pageAssist.getPageSize(), pageAssist.getPageNo(),
				pageAssist.getAllNum(), pagedByDb);
	}

	@Override
	public PageAssist queryForPagedList(String listSqlId, Map params, HttpServletRequest request) {
		PageAssist pageAssist = pageBuild.build(request);
		return queryForPagedList(listSqlId, params, pageAssist);
	}

	@Override
	public PageAssist queryForPagedList(String listSqlId, Map params, PageAssist reqPageAssist) {
		PageAssist pageAssist = reqPageAssist == null ? pageBuild.build(null) : reqPageAssist;
		return queryForPagedList(listSqlId, null, params, pageAssist.getPageSize(), pageAssist.getPageNo(),
				pageAssist.getAllNum(), true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List executeSqlQuery(String sql, Object[] inParams) {
		List list = new ArrayList();
		PreparedStatement proc = null;
		ResultSet rs = null;
		try {
			Connection conn = getSqlSession().getConnection();
			proc = conn.prepareStatement(sql);
			for (int i = 0; inParams != null && i < inParams.length; i++) {
				proc.setObject(i + 1, inParams[i]);
			}
			rs = proc.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int num = rsmd.getColumnCount();

			while (rs.next()) {
				List row = new ArrayList();
				for (int j = 0; j < num; j++) {
					row.add(rs.getObject(j + 1));
				}
				list.add(row);
			}
			return list;
		} catch (SQLException e) {
			logger.error("SQL语句执行错误.", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("关闭rs错误");
				}
			}
			if (proc != null) {
				try {
					proc.close();
				} catch (SQLException e) {
					logger.error("关闭PreparedStatement错误");
				}
			}
		}
		return list;
	}

	@Override
	public int saveOrUpdateEntity(String sqlId, Object entity, boolean update) {
		if (update) {
			return getSqlSession().update(sqlId, entity);
		} else {
			return getSqlSession().insert(sqlId, entity);
		}
	}

	@Override
	public int saveOrUpdateEntity(Object entity, boolean update) {
		String clsName = entity.getClass().getSimpleName();
		if (update) {
			clsName = clsName + "_update";
		} else {
			clsName = clsName + "_insert";
		}
		return saveOrUpdateEntity(clsName, entity, update);
	}

	@Override
	public void deleteEntity(String sqlId, Object parameterObject) {
		getSqlSession().delete(sqlId, parameterObject);
	}

	@Override
	public void deleteEntity(Object entity) {
		String clsName = entity.getClass().getSimpleName();
		deleteEntity(clsName + ".delete", entity);
	}

	@Override
	public int updateRecord(String sqlId, Map params) {
		return saveOrUpdateEntity(sqlId, params, true);
	}

	@Override
	public int insertRecord(String sqlId, Map params) {
		return saveOrUpdateEntity(sqlId, params, false);
	}

	private final SqlSession getSqlSession() {
		return this.sqlSessionFactory.openSession();
	}

	//////////////////////// get set方法////////////////////////////////////
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public IPageBuild getPageBuild() {
		return pageBuild;
	}

	public void setPageBuild(IPageBuild pageBuild) {
		this.pageBuild = pageBuild;
	}

}
