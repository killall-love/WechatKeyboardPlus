package io.github.uhsk.keyboard.utils

import android.content.Context
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import de.robv.android.xposed.XposedBridge
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

object Slf4jUtil {

    private val sXmlContent: String = """
        <configuration>

            <appender name="LOGCAT" class="ch.qos.logback.classic.android.LogcatAppender">
                <encoder>
                    <pattern>\(%F:%L\) [%thread] %m%n</pattern>
                </encoder>
            </appender>

            <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
                <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                    <fileNamePattern>${'$'}{CACHE_DIR}/logs/%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                    <maxHistory>30</maxHistory>
                    <cleanHistoryOnStart>true</cleanHistoryOnStart>
                    <totalSizeCap>500MB</totalSizeCap>
                    <maxFileSize>100MB</maxFileSize>
                </rollingPolicy>
                <encoder>
                    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%-27thread] [%logger{36}:%L] - %m%n</pattern>
                    <charset>UTF-8</charset>
                </encoder>
            </appender>

            <root level="debug">
                <appender-ref ref="LOGCAT" />
                <appender-ref ref="FILE" />
            </root>

        </configuration>

    """.trimIndent()

    fun init(context: Context) {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.reset()
        loggerContext.putProperty("CACHE_DIR", context.externalCacheDir!!.path)

        val byteArrayInputStream = ByteArrayInputStream(sXmlContent.toByteArray())
        val joranConfigurator = JoranConfigurator()
        joranConfigurator.context = loggerContext
        joranConfigurator.doConfigure(byteArrayInputStream)
    }

}
