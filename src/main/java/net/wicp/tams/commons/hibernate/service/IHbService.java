package net.wicp.tams.commons.hibernate.service;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example.PropertySelector;

import net.wicp.tams.commons.web.PageAssist;

/****
 * Hibernate辅助类,拥有事务
 *
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public interface IHbService {

	/***
	 * 跟据ID查找对象
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T extends Serializable> T findById(Class clazz, Serializable id);

	/***
	 * 跟据ID删除对象
	 * 
	 * @param clazz
	 * @param entityId
	 */
	void delById(Class clazz, Serializable entityId);

	/***
	 * 保存或新增PO对象
	 * 
	 * @param obj
	 */
	public <T extends Serializable> void saveOrUpdate(T obj);

	/****
	 * Oracle/mysql 支持悲观锁的会用 select * from XXX for update 锁记录，其它数据库同 attachQuery
	 *
	 * @param entity
	 */
	public <T extends Serializable> void attachLock(T entity);

	/***
	 * 跟据hql语句得到Query对象
	 * 
	 * @param hql
	 * @return
	 */
	public Query getQuery(String hql);

	/***
	 * 跟据sql得到Query对象
	 *
	 * @param sql
	 * @return
	 */
	public Query getQuerySQL(String sql);

	/***
	 * 通过示例查询，注意它会忽略 主键
	 *
	 * @param entity
	 * @param selector
	 * @return
	 */
	public <T extends Serializable> List<T> findByExample(T entity, PropertySelector selector);

	/***
	 * 通过示例查询，注意它会忽略 主键
	 *
	 * @param entity
	 * @param excludes
	 *            示例中被排除的查询条件
	 * @return
	 */
	public <T extends Serializable> List<T> findByExample(T entity, String... excludes);

	/****
	 * 通过requesty请求来构建翻页
	 * 
	 * @param criteria
	 * @param request
	 * @return
	 */
	public PageAssist findByCriteriaPage(Criteria criteria, HttpServletRequest request);

	public PageAssist findByCriteriaPage(Criteria criteria, PageAssist reqPageAssist);

	/***
	 * Query的翻页查询，PageAssist由系统默认生成
	 * 
	 * @param queryparam
	 * @return
	 */
	public PageAssist findByQueryPage(Query queryparam, HttpServletRequest request);

	public PageAssist findByQueryPage(Query queryparam, PageAssist reqPageAssist);

	/****
	 * 得到当前的session
	 * 
	 * @return
	 */
	public Session getSession();

}
