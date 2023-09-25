package automation.base;

import automation.Config;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTestNG
{
    final protected Logger logger = LogManager.getLogger(this.getClass());

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, Object[] testArgs) {
        logger.debug("---------------------------------------------------------------------------");
        logger.debug("-- Run test: " + method.getAnnotation(Test.class).testName());
        logger.debug("---------------------------------------------------------------------------");
        int i = 1;
        for (Object obj : testArgs) {
            logger.debug("Argument " + i ++ + ": " + obj);
        }

    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) {
        logger.debug("---------------------------------------------------------------------------");
        logger.debug("-- End test: " + method.getAnnotation(Test.class).testName());
        logger.debug("---------------------------------------------------------------------------");
        this.attachResourceFile("env/" + Config.getEnvironmentName() + ".properties");
    }

    @Attachment("Resource file: {resourceFilePath}")
    private byte[] attachResourceFile(String resourceFilePath) {
        try {
            URI    uri      = ClassLoader.getSystemResource(resourceFilePath).toURI();
            Path   path     = Paths.get(uri);
            String content  = Files.readString(path);
            //System.out.println(content);
            return content.getBytes();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }
}
