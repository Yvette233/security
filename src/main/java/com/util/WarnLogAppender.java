package com.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.LogbackException;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WarnLogAppender extends AppenderBase<ILoggingEvent> {
    private static final ConcurrentLinkedQueue<String> warnLogs = new ConcurrentLinkedQueue<>();

    @Override
    protected void append(ILoggingEvent event) {
        try {
            if (event != null && event.getLevel().isGreaterOrEqual(Level.WARN)) {
                warnLogs.offer(event.getFormattedMessage());
            }
        } catch (Exception e) {
            throw new LogbackException("Failed to append log event", e);
        }
    }

    public static String getWarnMessages() {
        // 获取Logback上下文
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);

        // 获取自定义Appender实例
        WarnLogAppender appender = (WarnLogAppender) rootLogger.getAppender("WARN_LOG");
        if (appender != null) {
            Set<String> uniqueLogs = new LinkedHashSet<>(warnLogs);
            String warnMessages = String.join("; ", uniqueLogs);

            warnLogs.clear();

            return warnMessages;
        }
        return null;
    }
}
