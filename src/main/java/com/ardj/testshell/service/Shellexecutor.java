/**
 * All rights Reserved, Designed By ARDJ
 * Copyright: Copyright(C) 2016-2020
 * Company    ARDJ  Co., Ltd.
 */
package com.ardj.testshell.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * date: 2019/7/31 8:51  *
 *
 * @author xiaobing yang
 * @since JDK 1.8
 */
@Service
@Slf4j
public class Shellexecutor {

    @Value("${spring.sjbar.convert.path}")
    private String convertShellPath;

    @Value("${spring.sjbar.convert.name}")
    private String convertShellName;

    @Value("${spring.sjbar.fileName}")
    private String fileName;

    /**
     * 转换文件
     *
     * @return
     * @throws Exception
     */
    public void convertFile() throws Exception {
        String shellCommand = convertShellPath + convertShellName;
        String shellFileName = convertShellPath + fileName;
        String cmd = "sh " + shellCommand + " " + shellFileName;
        log.info("convertFile executeCommand = " + cmd);
        File targetFile = new File(shellFileName);
        if (!targetFile.exists()){
            log.error("shellFileName {} is not exist",shellFileName);
            return;
        }
        executeCommand("sh",shellCommand,shellFileName);
        String fileNameNoExt = getFileNameNoExt(fileName);
        String fsetPath = convertShellPath + fileNameNoExt + ".fset";
        String fset3Path = convertShellPath + fileNameNoExt + ".fset3";
        String isetPath = convertShellPath + fileNameNoExt + ".iset";
        File fset = new File(fsetPath);
        File fset3 = new File(fset3Path);
        File iset = new File(isetPath);
        if (fset.exists()) {
            log.info("convert result file success "+ fset.getName());
        }
        if (fset3.exists()) {
            log.info("convert result file success "+ fset3.getName());
        }
        if (iset.exists()) {
            log.info("convert result file success "+ iset.getName());
        }
    }

    /**
     * 执行脚本命令
     *
     * @param command shell 命令
     * @throws Exception
     */
    private void executeCommand(String sh, String command, String shellFileName) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(sh, command, shellFileName);
        pb.redirectErrorStream(true);
        Process p = null;
        BufferedReader br = null;
        try {
            p = pb.start();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            log.info("Invoke shell: {},{},{}", sh, command, shellFileName);
            while ((line = br.readLine()) != null) {
                log.info("[[[ "+line);
            }
            p.waitFor();
        } finally {
            if (br != null) {
                br.close();
            }
            if (p != null) {
                p.destroy();
            }
        }


    }

    /**
     * 获取不带后缀的文件名
     *
     * @param fileName 文件名
     * @return 结果
     */
    private String getFileNameNoExt(String fileName) {
        String fileNameNoExt = null;
        if (fileName != null) {
            if (fileName.contains(".")) {
                fileNameNoExt =
                        fileName.substring(0, fileName.lastIndexOf('.'));
            }
        }
        return fileNameNoExt;
    }


    /**
     * 执行脚本命令
     *
     * @param command shell 命令
     * @throws Exception
     */
    private void executeCommand(String command) throws Exception {
        try {

            //启动独立线程等待process执行完成
            CommandWaitForThread commandThread = new CommandWaitForThread(command);
            commandThread.start();
            while (!commandThread.isFinish()) {
                log.info("shell " + command + " 还未执行完毕,10s后重新探测");
                Thread.sleep(10000);
            }
            //检查脚本执行结果状态码
            if (commandThread.getExitValue() != 0) {
                throw new Exception("shell " + command + "执行失败,exitValue = " + commandThread.getExitValue());
            }
            log.info("shell " + command + "执行成功,exitValue = " + commandThread.getExitValue());
        } catch (Exception e) {
            throw new Exception("执行shell命令发生异常," + command, e);
        }
    }
}
