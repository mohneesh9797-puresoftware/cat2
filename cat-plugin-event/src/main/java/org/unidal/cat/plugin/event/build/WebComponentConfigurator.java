package org.unidal.cat.plugin.event.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.cat.plugin.event.report.ReportModule;
import org.unidal.lookup.configuration.Component;
import org.unidal.web.configuration.AbstractWebComponentsConfigurator;

class WebComponentConfigurator extends AbstractWebComponentsConfigurator {
	@SuppressWarnings("unchecked")
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		defineModuleRegistry(all, ReportModule.class, ReportModule.class);

		return all;
	}
}
