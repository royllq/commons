package net.wicp.tams.commons.hibernate.service.impl;

import static org.hibernate.criterion.Example.create;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Example.PropertySelector;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.AbstractQueryImpl;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.hibernate.service.IHbService;
import net.wicp.tams.commons.web.PageAssist;
import net.wicp.tams.commons.web.service.IPageBuild;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class HbService implements IHbService {
	private final Logger logger = LogHelp.getLogger(getClass());

	private SessionFactory sessionFactory;

	private IPageBuild pageBuild;

	@Override
	public <T extends Serializable> T findById(Class clazz, Serializable id) {
		return (T) getSession().get(clazz, id);
	}

	@Override
	public void delById(Class clazz, Serializable entityId) {
		Object entity = getSession().get(clazz, entityId);
		Preconditions.checkState(entity != null);
		getSession().delete(entity);
	}

	@Override
	public <T extends Serializable> void saveOrUpdate(T obj) {
		getSession().saveOrUpdate(obj);
	}

	@Override
	public <T extends Serializable> void attachLock(T entity) {
		try {
			getSession().buildLockRequest(LockOptions.UPGRADE).lock(entity);
		} catch (RuntimeException re) {
			logger.error("attach failed", re);
			throw re;
		}
	}

	@Override
	public Query getQuery(String hql) {
		return getSession().createQuery(hql);
	}

	@Override
	public Query getQuerySQL(String sql) {
		return getSession().createSQLQuery(sql);
	}

	@Override
	public <T extends Serializable> List<T> findByExample(T entity, PropertySelector selector) {
		try {
			Example example = create(entity).excludeZeroes();
			if (selector != null) {
				example.setPropertySelector(selector);
			}
			List<T> results = (List<T>) getSession().createCriteria(entity.getClass()).add(example).list();
			return results;
		} catch (RuntimeException re) {
			logger.error("find by example failed", re);
			throw re;
		}
	}

	@Override
	public <T extends Serializable> List<T> findByExample(T entity, String... excludes) {
		try {
			Example example = create(entity).excludeZeroes();
			if (ArrayUtils.isNotEmpty(excludes)) {
				for (String proName : excludes) {
					example.excludeProperty(proName);
				}
			}
			List<T> results = (List<T>) getSession().createCriteria(entity.getClass()).add(example).list();
			return results;
		} catch (RuntimeException re) {
			logger.error("find by example failed", re);
			throw re;
		}
	}

	@Override
	public PageAssist findByCriteriaPage(Criteria criteria, PageAssist pageAssist) {
		PageAssist pageAssistTrue = pageAssist == null ? pageBuild.build() : pageAssist;
		if (pageAssistTrue.getAllNum() < 0) {
			long totalCount = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
			pageAssistTrue.setAllNum(totalCount);
			criteria.setProjection(null);
		}

		List<?> retlist = findByCriteriaPage(criteria, pageAssistTrue.getPageNo(), pageAssistTrue.getPageSize());
		pageAssistTrue.setResult(retlist);
		return pageAssistTrue;
	}

	@Override
	public PageAssist findByCriteriaPage(Criteria criteria, HttpServletRequest request) {
		PageAssist init = pageBuild.build(request);
		return findByCriteriaPage(criteria, init);
	}

	@Override
	public PageAssist findByCriteriaPage(Criteria criteria) {
		PageAssist init = pageBuild.build();
		return findByCriteriaPage(criteria, init);
	}

	@Override
	public PageAssist findByQueryPage(Query queryparam, PageAssist pageAssistparam) {
		PageAssist pageAssistTrue = pageAssistparam == null ? pageBuild.build() : pageAssistparam;
		int pageSize = pageAssistTrue.getPageSize();
		int pageNo = pageAssistTrue.getPageNo();
		long allNum = pageAssistTrue.getAllNum();
		Preconditions.checkState(queryparam != null && pageNo > 0 && pageSize > 0);
		AbstractQueryImpl queryAbs = (AbstractQueryImpl) queryparam;

		if (allNum < 0) {// 需要查询记录数
			String queryStr = queryparam.getQueryString();
			int firstIndex = queryStr.indexOf(" from");
			String queryCountSql = "select COUNT(-1)  " + queryStr.substring(firstIndex);
			Query countQuery = getSession().createQuery(queryCountSql);

			try {
				if (ArrayUtils.isNotEmpty(queryAbs.getNamedParameters())) {
					Map<String, TypedValue> paramMap = (Map<String, TypedValue>) PropertyUtils.getProperty(queryAbs,
							"namedParameters");
					for (String paramName : queryAbs.getNamedParameters()) {
						TypedValue tempobj = paramMap.get(paramName);
						countQuery.setParameter(paramName, tempobj.getValue(), tempobj.getType());
					}
				} else {
					List values = (List) PropertyUtils.getProperty(queryAbs, "values");
					for (int i = 0; i < values.size(); i++) {
						countQuery.setParameter(i + 1, values.get(i));
					}
				}
			} catch (Exception e) {
				logger.error("Query在翻页时查询总记录数出错。", e);
			}

			allNum = ((Long) countQuery.uniqueResult()).longValue();
			pageAssistTrue.setAllNum(allNum);
		}
		int startRes = pageSize * (pageNo - 1);
		int endRes = (int) ((allNum > 0 && allNum < pageSize * pageNo) ? allNum : pageSize * pageNo);
		queryparam.setFirstResult(startRes).setMaxResults(endRes);
		List queryList = queryparam.list();
		pageAssistTrue.setResult(queryList);
		return pageAssistTrue;
	}

	@Override
	public PageAssist findByQueryPage(Query queryparam) {
		return findByQueryPage(queryparam, null);
	}
	@Override
	public Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}

	// ///////////////////////////////////////////////////////////////////////////////
	private final <T extends Serializable> List<T> findByCriteriaPage(Criteria criteria, int pageNo, int pageSize) {
		Preconditions.checkState(criteria != null && pageNo > 0 && pageSize > 0);
		int min = (pageNo - 1) * pageSize;
		criteria.setFirstResult(min).setMaxResults(pageSize);
		return criteria.list();
	}

	///////////////////////// get/set方法区/////////////////////////////////////

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public IPageBuild getPageBuild() {
		return pageBuild;
	}

	public void setPageBuild(IPageBuild pageBuild) {
		this.pageBuild = pageBuild;
	}

}
