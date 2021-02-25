package ru.javawebinar.topjava.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public abstract class AbstractServiceTest {
    protected static List<String> summary = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(AbstractServiceTest.class);

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            summary.add(description.getMethodName() + " finished " + nanos + " ns");
            log.info("Finished " + nanos + " ns");
        }

    };

    @AfterClass
    public static void getSummaryInfo() {
        StringBuilder result = new StringBuilder();
        result.append("<==========================================>").append("\n");
        for (String s : summary) {
            result.append(s).append("\n");
        }
        result.append("<==========================================>");
        System.out.println(result);
    }

    @BeforeClass
    public static void clearSummaryInfo() {
        summary.clear();
    }
}
