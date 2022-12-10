package com.xjhqre.common.domain.monitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.xjhqre.common.utils.Arith;
import com.xjhqre.common.utils.ip.IpUtils;

import lombok.Data;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

/**
 * 服务器相关信息
 * 
 * @author xjhqre
 */
@Data
public class Server {
    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * CPU相关信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存相关信息
     */
    private Mem mem = new Mem();

    /**
     * JVM相关信息
     */
    private Jvm jvm = new Jvm();

    /**
     * 服务器相关信息
     */
    private Sys sys = new Sys();

    /**
     * 磁盘相关信息
     */
    private List<File> files = new LinkedList<File>();

    public void copyTo() throws Exception {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        this.setCpuInfo(hal.getProcessor());

        this.setMemInfo(hal.getMemory());

        this.setSysInfo();

        this.setJvmInfo();

        this.setSysFiles(si.getOperatingSystem());
    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo(CentralProcessor processor) {
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 休眠一秒
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        this.cpu.setCpuNum(processor.getLogicalProcessorCount());
        this.cpu.setTotal(totalCpu);
        this.cpu.setSys(cSys);
        this.cpu.setUsed(user);
        this.cpu.setWait(iowait);
        this.cpu.setFree(idle);
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory) {
        this.mem.setTotal(memory.getTotal());
        this.mem.setUsed(memory.getTotal() - memory.getAvailable());
        this.mem.setFree(memory.getAvailable());
    }

    /**
     * 设置服务器信息
     */
    private void setSysInfo() {
        Properties props = System.getProperties();
        this.sys.setComputerName(IpUtils.getHostName());
        this.sys.setComputerIp(IpUtils.getHostIp());
        this.sys.setOsName(props.getProperty("os.name"));
        this.sys.setOsArch(props.getProperty("os.arch"));
        this.sys.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * 设置Java虚拟机
     */
    private void setJvmInfo() {
        Properties props = System.getProperties();
        this.jvm.setTotal(Runtime.getRuntime().totalMemory());
        this.jvm.setMax(Runtime.getRuntime().maxMemory());
        this.jvm.setFree(Runtime.getRuntime().freeMemory());
        this.jvm.setVersion(props.getProperty("java.version"));
        this.jvm.setHome(props.getProperty("java.home"));
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        // 获取所有卷
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            // 磁盘空闲空间
            long free = fs.getUsableSpace();
            // 磁盘总大小
            long total = fs.getTotalSpace();
            // 磁盘已使用的大小
            long used = total - free;
            File file = new File();
            file.setDirName(fs.getMount());
            file.setSysTypeName(fs.getType());
            file.setTypeName(fs.getName());
            file.setTotal(this.convertFileSize(total));
            file.setFree(this.convertFileSize(free));
            file.setUsed(this.convertFileSize(used));
            file.setUsage(Arith.mul(Arith.div(used, total, 4), 100));
            this.files.add(file);
        }
    }

    /**
     * 字节转换
     * 
     * @param size
     *            字节大小
     * @return 转换后值
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float)size / gb);
        } else if (size >= mb) {
            float f = (float)size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float)size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }
}
