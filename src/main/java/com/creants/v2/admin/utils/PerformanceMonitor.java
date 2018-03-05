package com.creants.v2.admin.utils;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

/**
 * @author LamHM
 *
 */
public class PerformanceMonitor {
	private long lastSystemTime;
    private long lastProcessCpuTime;
    OperatingSystemMXBean osMxBean;
    
    public PerformanceMonitor() {
        this.lastSystemTime = 0L;
        this.lastProcessCpuTime = 0L;
        this.osMxBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        this.baselineCounters();
    }
    
    public synchronized double getProcessCpuLoad() {
        final long systemTime = System.nanoTime();
        final long processCpuTime = this.osMxBean.getProcessCpuTime();
        final double cpuLoad = (processCpuTime - this.lastProcessCpuTime) / (systemTime - this.lastSystemTime);
        this.lastSystemTime = systemTime;
        this.lastProcessCpuTime = processCpuTime;
        return cpuLoad / this.osMxBean.getAvailableProcessors();
    }
    
    public synchronized double getSystemCpuLoad() {
        return this.osMxBean.getSystemCpuLoad();
    }
    
    private void baselineCounters() {
        this.lastSystemTime = System.nanoTime();
        this.lastProcessCpuTime = this.osMxBean.getProcessCpuTime();
    }
    
}
