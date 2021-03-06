package org.unidal.cat.plugin.transactions.reducer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.cat.plugin.transactions.TransactionsConstants;
import org.unidal.cat.plugin.transactions.model.entity.TransactionsReport;
import org.unidal.cat.spi.ReportPeriod;
import org.unidal.cat.spi.report.ReportDelegate;
import org.unidal.cat.spi.report.storage.ReportStorage;
import org.unidal.cat.spi.report.task.ReportTask;
import org.unidal.cat.spi.report.task.ReportTaskExecutor;
import org.unidal.cat.spi.report.task.ReportTaskService;
import org.unidal.cat.spi.report.task.internals.ReportTaskTracker;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class TransactionsReportReducerTest extends ComponentTestCase {
   private static AtomicReference<StringBuilder> REF = new AtomicReference<StringBuilder>();

   @Before
   public void before() throws Exception {
      defineComponent(ReportStorage.class, MockReportStorage.class);
      defineComponent(ReportTaskTracker.class, MockReportTaskTracker.class);
      defineComponent(ReportTaskService.class, MockReportTaskService.class);
   }

   @Test
   public void testDay() throws Exception {
      ReportTask task = new MockReportTask(ReportPeriod.HOUR, ReportPeriod.DAY);

      REF.set(new StringBuilder());
      lookup(ReportTaskExecutor.class).execute(task);

      Assert.assertEquals("week,month", REF.get().toString());
   }

   @Test
   public void testMonth() throws Exception {
      ReportTask task = new MockReportTask(ReportPeriod.DAY, ReportPeriod.MONTH);

      REF.set(new StringBuilder());
      lookup(ReportTaskExecutor.class).execute(task);

      Assert.assertEquals("year", REF.get().toString());
   }

   @Test
   public void testWeek() throws Exception {
      ReportTask task = new MockReportTask(ReportPeriod.DAY, ReportPeriod.WEEK);

      REF.set(new StringBuilder());
      lookup(ReportTaskExecutor.class).execute(task);

      Assert.assertEquals("", REF.get().toString());
   }

   public static class MockReportStorage implements ReportStorage<TransactionsReport> {
      @Override
      public List<TransactionsReport> loadAll(ReportDelegate<TransactionsReport> delegate, ReportPeriod period,
            Date startTime, String domain) throws IOException {
         List<TransactionsReport> reports = new ArrayList<TransactionsReport>();

         for (int i = 1; i <= 3; i++) {
            TransactionsReport report = loadReport(delegate, period.getName() + "-" + i + ".xml");

            reports.add(report);
         }

         return reports;
      }

      @Override
      public List<TransactionsReport> loadAllByDateRange(ReportDelegate<TransactionsReport> delegate,
            ReportPeriod period, Date startTime, Date endTime, String domain) throws IOException {
         List<TransactionsReport> reports = new ArrayList<TransactionsReport>();

         for (int i = 1; i <= 3; i++) {
            TransactionsReport report = loadReport(delegate, period.getName() + "-" + i + ".xml");

            reports.add(report);
         }

         return reports;
      }

      private TransactionsReport loadReport(ReportDelegate<TransactionsReport> delegate, String resource)
            throws IOException {
         InputStream in = getClass().getResourceAsStream(resource);

         if (in == null) {
            throw new IllegalStateException(String.format("Unable to load resource(%s)!", resource));
         }

         String xml = Files.forIO().readFrom(in, "utf-8");
         TransactionsReport report = delegate.parseXml(xml);

         return report;
      }

      @Override
      public void store(ReportDelegate<TransactionsReport> delegate, ReportPeriod period, TransactionsReport report,
            int index) throws IOException {
         TransactionsReport expected = loadReport(delegate, period.getName() + ".xml");

         Assert.assertEquals(String.format("TransactionsReport(%s) mismatched!", period), expected.toString(),
               report.toString());
      }
   }

   static class MockReportTask implements ReportTask {
      private ReportPeriod m_source;

      private ReportPeriod m_target;

      public MockReportTask(ReportPeriod source, ReportPeriod target) {
         m_source = source;
         m_target = target;
      }

      @Override
      public int getFailureCount() {
         return 0;
      }

      @Override
      public int getId() {
         return 0;
      }

      @Override
      public String getReportName() {
         return TransactionsConstants.NAME;
      }

      @Override
      public ReportPeriod getSourcePeriod() {
         return m_source;
      }

      @Override
      public Date getTargetEndTime() {
         return m_target.getNextStartTime(new Date());
      }

      @Override
      public ReportPeriod getTargetPeriod() {
         return m_target;
      }

      @Override
      public Date getTargetStartTime() {
         return m_source.getStartTime(new Date());
      }
   }

   public static class MockReportTaskService implements ReportTaskService {
      @Override
      public void add(String id, ReportPeriod targetPeriod, Date startTime, String reportName, Date scheduleTime)
            throws Exception {
         StringBuilder sb = REF.get();

         if (sb.length() > 0) {
            sb.append(',');
         }

         sb.append(targetPeriod.getName());
      }

      @Override
      public void complete(ReportTask task) throws Exception {
         throw new UnsupportedOperationException("Not implemented!");
      }

      @Override
      public void fail(ReportTask task, String reason) throws Exception {
         throw new UnsupportedOperationException("Not implemented!");
      }

      @Override
      public ReportTask pull(String id) throws Exception {
         throw new UnsupportedOperationException("Not implemented!");
      }
   }

   public static class MockReportTaskTracker implements ReportTaskTracker {
      private Set<String> m_domains = new LinkedHashSet<String>();

      public MockReportTaskTracker() {
         m_domains.add("cat");
      }

      @Override
      public void close() {
      }

      @Override
      public void done(String domain) {
      }

      @Override
      public Set<String> getDomains() {
         return m_domains;
      }

      @Override
      public void open(ReportTask task) {
      }
   }
}
