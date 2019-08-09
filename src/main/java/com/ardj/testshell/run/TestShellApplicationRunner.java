/**
 * All rights Reserved, Designed By ASPIRE
 * Copyright: Copyright(C) 2016-2020
 * Company    ASPIRE  Co., Ltd.
 */
package com.ardj.testshell.run;

import com.ardj.testshell.service.Shellexecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * date: 2019/7/31 8:50  *
 *
 * @author xiaobing yang
 * @since JDK 1.8
 */
@Slf4j
@Component
public class TestShellApplicationRunner implements ApplicationRunner {

    @Resource
    private Shellexecutor shellexecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(2000);

        log.info("执行命令开始： ");
        long start2 = System.currentTimeMillis();
        shellexecutor.convertFile();
        log.info("执行命令结束，用时： " + (System.currentTimeMillis() - start2));
        log.info("退出系统。");
        System.exit(0);

    }
}
