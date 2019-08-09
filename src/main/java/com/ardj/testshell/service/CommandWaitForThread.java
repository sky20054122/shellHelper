/**
 * All rights Reserved, Designed By ASPIRE
 * Copyright: Copyright(C) 2016-2020
 * Company    ASPIRE  Co., Ltd.
 */
package com.ardj.testshell.service;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * date: 2019/7/31 8:53  *
 *
 * @author xiaobing yang
 * @since JDK 1.8
 */
@Slf4j
public class CommandWaitForThread extends Thread{
    private String cmd;
    private boolean finish = false;
    private int exitValue = -1;

    public CommandWaitForThread(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public void run() {
        Process process = null;
        try {
            log.info("thread execute cmd =" + cmd);
            //执行脚本并等待脚本执行完成
            process = Runtime.getRuntime().exec(cmd);

            //写出脚本执行中的过程信息
            BufferedReader infoInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = "";
            while ((line = infoInput.readLine()) != null) {
                log.info(line);
            }
            while ((line = errorInput.readLine()) != null) {
                log.error(line);
            }
            infoInput.close();
            errorInput.close();

            //阻塞执行线程直至脚本执行完成后返回
            this.exitValue = process.waitFor();
        } catch (Throwable e) {
            log.error("CommandWaitForThread accure exception,shell " + cmd, e);
            exitValue = 110;
        } finally {
            finish = true;
            if (null != process) {
                process.destroy();
            }
        }
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getExitValue() {
        return exitValue;
    }
}
