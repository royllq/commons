package net.wicp.tams.commons.mybatis;

import java.util.Properties;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.session.RowBounds;

import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.mybatis.dialect.Dialect;
import net.wicp.tams.commons.mybatis.dialect.PropertiesHelper;


/****
 * 翻页拦截类
 * 
 * @author Administrator
 *
 */
@Intercepts({ @org.apache.ibatis.plugin.Signature(type = org.apache.ibatis.executor.Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class,
		org.apache.ibatis.session.ResultHandler.class }) })
public class OffsetLimitInterceptor implements Interceptor {
	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;
	static int ROWBOUNDS_INDEX = 2;
	static int RESULT_HANDLER_INDEX = 3;
	Dialect dialect;

	public Object intercept(Invocation paramInvocation) throws Throwable {
		processIntercept(paramInvocation.getArgs());
		return paramInvocation.proceed();
	}

	void processIntercept(Object[] paramArrayOfObject) {
		MappedStatement localMappedStatement1 = (MappedStatement) paramArrayOfObject[MAPPED_STATEMENT_INDEX];
		Object localObject = paramArrayOfObject[PARAMETER_INDEX];
		RowBounds localRowBounds = (RowBounds) paramArrayOfObject[ROWBOUNDS_INDEX];
		int i = localRowBounds.getOffset();
		int j = localRowBounds.getLimit();
		if ((!this.dialect.supportsLimit()) || ((i == 0) && (j == 2147483647)))
			return;
		BoundSql localBoundSql1 = localMappedStatement1
				.getBoundSql(localObject);
		String str = localBoundSql1.getSql().trim();
		if (this.dialect.supportsLimitOffset()) {
			str = this.dialect.getLimitString(str, i, j);
			i = 0;
		} else {
			str = this.dialect.getLimitString(str, 0, j);
		}
		j = 2147483647;
		paramArrayOfObject[ROWBOUNDS_INDEX] = new RowBounds(i, j);
		BoundSql localBoundSql2 = new BoundSql(
				localMappedStatement1.getConfiguration(), str,
				localBoundSql1.getParameterMappings(),
				localBoundSql1.getParameterObject());
		MappedStatement localMappedStatement2 = copyFromMappedStatement(
				localMappedStatement1, new BoundSqlSqlSource(localBoundSql2));
		paramArrayOfObject[MAPPED_STATEMENT_INDEX] = localMappedStatement2;
	}

	private MappedStatement copyFromMappedStatement(
			MappedStatement paramMappedStatement, SqlSource paramSqlSource) {
		MappedStatement.Builder localBuilder = new MappedStatement.Builder(
				paramMappedStatement.getConfiguration(),
				paramMappedStatement.getId(), paramSqlSource,
				paramMappedStatement.getSqlCommandType());
		localBuilder.resource(paramMappedStatement.getResource());
		localBuilder.fetchSize(paramMappedStatement.getFetchSize());
		localBuilder.statementType(paramMappedStatement.getStatementType());
		localBuilder.keyGenerator(paramMappedStatement.getKeyGenerator());
		localBuilder.keyProperty(StringUtil.combo(
				paramMappedStatement.getKeyProperties(), ","));
		localBuilder.timeout(paramMappedStatement.getTimeout());
		localBuilder.parameterMap(paramMappedStatement.getParameterMap());
		localBuilder.resultMaps(paramMappedStatement.getResultMaps());
		localBuilder.cache(paramMappedStatement.getCache());
		MappedStatement localMappedStatement = localBuilder.build();
		return localMappedStatement;
	}

	public Object plugin(Object paramObject) {
		return Plugin.wrap(paramObject, this);
	}

	public void setProperties(Properties paramProperties) {
		String str = new PropertiesHelper(paramProperties)
				.getRequiredString("dialectClass");
		try {
			this.dialect = ((Dialect) Class.forName(str).newInstance());
		} catch (Exception localException) {
			throw new RuntimeException(
					"cannot create dialect instance by dialectClass:" + str,
					localException);
		}
		System.out.println(OffsetLimitInterceptor.class.getSimpleName()
				+ ".dialect=" + str);
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql paramBoundSql) {
			this.boundSql = paramBoundSql;
		}

		public BoundSql getBoundSql(Object paramObject) {
			return this.boundSql;
		}
	}
}
