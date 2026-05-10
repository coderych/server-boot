package com.coderych.commons.mybatisflex.autoconfigure;

import com.coderych.commons.mybatisflex.service.DatabaseMetadataService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class MyBatisFlexAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(DataSource.class, StubDataSource::new)
            .withConfiguration(AutoConfigurations.of(MyBatisFlexAutoConfiguration.class));

    @Test
    void shouldCreateDatabaseMetadataServiceByDefault() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("databaseMetadataService"));
            assertNotNull(context.getBean(DatabaseMetadataService.class));
        });
    }

    @Test
    void shouldCreateBeanWhenEnabledPropertyIsTrue() {
        contextRunner
                .withPropertyValues("commons.mybatis-flex.enabled=true")
                .run(context -> {
                    assertTrue(context.containsBean("databaseMetadataService"));
                    assertNotNull(context.getBean(DatabaseMetadataService.class));
                });
    }

    @Test
    void shouldNotCreateBeanWhenEnabledPropertyIsFalse() {
        contextRunner
                .withPropertyValues("commons.mybatis-flex.enabled=false")
                .run(context -> {
                    assertFalse(context.containsBean("databaseMetadataService"));
                });
    }

    @Test
    void shouldNotOverrideExistingBean() {
        DatabaseMetadataService existing = new DatabaseMetadataService(new StubDataSource());
        contextRunner
                .withBean("customDatabaseMetadataService", DatabaseMetadataService.class, () -> existing)
                .run(context -> {
                    assertSame(existing, context.getBean(DatabaseMetadataService.class));
                });
    }

    @Test
    void shouldBindPropertiesBean() {
        contextRunner.run(context -> {
            MyBatisFlexProperties properties = context.getBean(MyBatisFlexProperties.class);
            assertNotNull(properties);
            assertTrue(properties.isEnabled());
        });
    }

    private static class StubDataSource implements DataSource {
        @Override
        public Connection getConnection() throws SQLException {
            throw new SQLException("stub");
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            throw new SQLException("stub");
        }

        @Override
        public PrintWriter getLogWriter() {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) {
        }

        @Override
        public int getLoginTimeout() {
            return 0;
        }

        @Override
        public void setLoginTimeout(int seconds) {
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger("");
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new SQLException("not a wrapper");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return false;
        }
    }
}
