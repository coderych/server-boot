package com.coderych.commons.mybatisflex.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class MyBatisFlexPropertiesTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(DataSource.class, StubDataSource::new)
            .withConfiguration(AutoConfigurations.of(MyBatisFlexAutoConfiguration.class));

    @Test
    void defaultPropertiesShouldHaveCorrectValues() {
        contextRunner.run(context -> {
            MyBatisFlexProperties properties = context.getBean(MyBatisFlexProperties.class);
            assertTrue(properties.isEnabled());
            assertNotNull(properties.getAutoFill());
            assertTrue(properties.getAutoFill().isEnabled());
            assertEquals(List.of("create_time"), properties.getAutoFill().getCreateTimeFields());
            assertEquals(List.of("update_time"), properties.getAutoFill().getUpdateTimeFields());
            assertEquals(List.of("create_by"), properties.getAutoFill().getCreateByFields());
            assertEquals(List.of("update_by"), properties.getAutoFill().getUpdateByFields());
        });
    }

    @Test
    void customPropertiesShouldOverrideDefaults() {
        contextRunner
                .withPropertyValues(
                        "commons.mybatis-flex.auto-fill.enabled=false",
                        "commons.mybatis-flex.auto-fill.create-time-fields=gmt_create,create_time",
                        "commons.mybatis-flex.auto-fill.update-time-fields=gmt_modified,update_time",
                        "commons.mybatis-flex.auto-fill.create-by-fields=creator,create_by",
                        "commons.mybatis-flex.auto-fill.update-by-fields=modifier,update_by")
                .run(context -> {
                    MyBatisFlexProperties properties = context.getBean(MyBatisFlexProperties.class);
                    assertTrue(properties.isEnabled());
                    assertFalse(properties.getAutoFill().isEnabled());
                    assertEquals(List.of("gmt_create", "create_time"), properties.getAutoFill().getCreateTimeFields());
                    assertEquals(List.of("gmt_modified", "update_time"), properties.getAutoFill().getUpdateTimeFields());
                    assertEquals(List.of("creator", "create_by"), properties.getAutoFill().getCreateByFields());
                    assertEquals(List.of("modifier", "update_by"), properties.getAutoFill().getUpdateByFields());
                });
    }

    @Test
    void partialPropertiesShouldOnlyOverrideSpecified() {
        contextRunner
                .withPropertyValues(
                        "commons.mybatis-flex.auto-fill.create-time-fields=created_at")
                .run(context -> {
                    MyBatisFlexProperties properties = context.getBean(MyBatisFlexProperties.class);
                    assertTrue(properties.isEnabled());
                    assertTrue(properties.getAutoFill().isEnabled());
                    assertEquals(List.of("created_at"), properties.getAutoFill().getCreateTimeFields());
                    assertEquals(List.of("update_time"), properties.getAutoFill().getUpdateTimeFields());
                });
    }

    @Test
    void autoFillShouldHaveDefaultInstance() {
        MyBatisFlexProperties properties = new MyBatisFlexProperties();
        assertNotNull(properties.getAutoFill());
        assertTrue(properties.getAutoFill().isEnabled());
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
