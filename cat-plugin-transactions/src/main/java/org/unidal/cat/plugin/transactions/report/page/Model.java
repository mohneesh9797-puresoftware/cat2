package org.unidal.cat.plugin.transactions.report.page;

import org.unidal.cat.plugin.transactions.report.ReportPage;
import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<ReportPage, Action, Context> {
	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}
}