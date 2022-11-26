package com.alipay.atec2022;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class Atec2022Application {
    @Value("${activate-flag-file-path}")
    String activateFlagFilePath;

    public static void main(String[] args) throws IOException {
        ApplicationContext applicationContext = SpringApplication.run(Atec2022Application.class, args);
        applicationContext.getBean(Atec2022Application.class).markAsActivated();
    }

    public void markAsActivated() throws IOException {
        System.out.println("markAsActivated");
        FileUtils.writeStringToFile(new File(activateFlagFilePath), "", Charset.defaultCharset());
    }

}
