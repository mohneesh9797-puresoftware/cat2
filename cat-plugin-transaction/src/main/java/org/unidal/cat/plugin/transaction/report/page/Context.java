package org.unidal.cat.plugin.transaction.report.page;

import org.unidal.cat.core.report.CoreReportContext;

public class Context extends CoreReportContext<Payload> {
	@Override
	public Query getQuery() {
		return new Query(getHttpServletRequest(), true);
	}
}