package net.wicp.tams.commons.mybatis.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.wicp.tams.commons.web.PageAssist;

/*****
 * ibatis公用服务
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("rawtypes")
public interface IbatisService {

	/****
	 * 通过参考得到Map列表
	 * 
	 * @param sqlId
	 * @param keysAndValues
	 *            key/value值对
	 * @return
	 */
	public List<Map<String, Object>> queryForList(String sqlId, Object... keysAndValues);

	/***
	 * 通过sqlId和参数得到Map列表
	 * 
	 * @param sqlId
	 * @param params
	 *            参数
	 * @return 元素为Map
	 */
	public List<Map<String, Object>> queryForList(String sqlId, Map params);

	/**
	 * Ibatis获取分页数据的统一方法
	 * 
	 * @param listSqlId
	 *            获取结果数据数据的SQLId
	 * @param countSqlId
	 *            获取记录总数 的sqlid
	 * @param params
	 *            查询参数
	 * @param pageSize
	 *            每页的记录数
	 * @param pageNo
	 *            取第几页的数据
	 * @param pagedByDb
	 *            是否是数据库实现的分页
	 * @return
	 */
	public PageAssist queryForPagedList(final String listSqlId, final String countSqlId, final Map params,
			final int pageSize, final int pageNo, final long countNum, boolean pagedByDb);

	/****
	 * 分页
	 * 
	 * @param listSqlId
	 *            获取结果数据数据的SQLId
	 * @param countSqlId
	 *            获取记录总数 的sqlid
	 * @param params
	 *            查询参数
	 * @param request
	 *            翻页
	 * @param pagedByDb
	 *            是否真翻页
	 * @return
	 */
	public PageAssist queryForPagedList(final String listSqlId, final String countSqlId, final Map params,
			HttpServletRequest request, boolean pagedByDb);

	/****
	 * 分页
	 * 
	 * @param listSqlId
	 *            获取结果数据数据的SQLId
	 * @param params
	 *            查询参数
	 * @param request
	 *            翻页
	 * @param pagedByDb
	 *            是否真翻页
	 * @return
	 */
	public PageAssist queryForPagedList(final String listSqlId, final Map params, HttpServletRequest request,
			boolean pagedByDb);

	/***
	 * 真翻页
	 * 
	 * @param listSqlId
	 * @param params
	 * @param request
	 * @return
	 */
	public PageAssist queryForPagedList(final String listSqlId, final Map params, HttpServletRequest request);

	/**
	 * 用sql语句执行查询操作
	 * 
	 * @param sql
	 *            sql语句
	 * @param inParams
	 *            输入参数对象
	 * @return 返回记录集
	 */
	public List executeSqlQuery(final String sql, final Object[] inParams);

	/***
	 * 保存或更新实例，可以用到JPA的ＰＯ类（未测试）
	 * 
	 * @param entity
	 *            PO对旬
	 * @param update
	 *            是否更新
	 */
	public int saveOrUpdateEntity(Object entity, boolean update);

	/***
	 * 插入或更新记录
	 * 
	 * @param sqlId
	 * @param entity
	 * @param update
	 * @return
	 */
	public int saveOrUpdateEntity(String sqlId, Object entity, boolean update);

	/****
	 * 删除
	 * 
	 * @param sqlId
	 * @param parameterObject
	 */
	public void deleteEntity(String sqlId, Object parameterObject);

	/***
	 * 删除实体对象,可以用到JPA的ＰＯ类（未测试）
	 * 
	 * @param entity
	 */
	public void deleteEntity(Object entity);

	/***
	 * Map参数的记录更新
	 * 
	 * @param sqlId
	 * @param params
	 * @return
	 */
	public int updateRecord(String sqlId, Map params);

	/***
	 * Map参数的记录插入
	 * 
	 * @param sqlId
	 * @param params
	 * @return
	 */
	public int insertRecord(String sqlId, Map params);
}
