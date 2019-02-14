package de.fhg.iee.ogema.systemd;

import info.faljse.SDNotify.SDNotify;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.TimeResource;
import org.osgi.service.component.annotations.Component;

@Component
public class Watchdog implements Application {

    ApplicationManager am;
    //need system time for scheduling the watchdog
    ScheduledExecutorService ses;
    int pid;

    //XXX depends on an implementation-specific return value!
    //Java 9 has java.lang.ProcessHandle to get the PID...
    int getPid() throws Exception {
        String s = ManagementFactory.getRuntimeMXBean().getName();
        String pidString = s.substring(0, s.indexOf("@"));
        return Integer.parseInt(pidString);
    }

    /**
     * Determines the VM's PID by running the bash shell.
     *
     * @return PID of Java VM
     */
    int getPidSh() throws Exception {
        Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "echo $PPID"});
        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String output = stdin.lines().collect(Collectors.joining());
            return Integer.parseInt(output.trim());
        }
    }

    @Override
    public void start(ApplicationManager am) {
        if (!SDNotify.isAvailable()) {
            am.getLogger().error("SDNotify not available");
            throw new IllegalStateException("SDNotify not available");
        }

        this.am = am;
        try {
            pid = getPidSh();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to determine PID", ex);
        }

        ses = Executors.newSingleThreadScheduledExecutor();
        ses.submit(() -> {
            SDNotify.sendMainPID(pid);
            am.getLogger().info("systemd MAINPID={}", pid);
        });
        ses.submit(() -> {
            SDNotify.sendNotify();
            am.getLogger().info("systemd READY signal sent", pid);
        });
        // do not use SDNotify.isWatchdogEnabled(), PIDs might not match
        long watchdogFrequency = SDNotify.getWatchdogFrequency();
        if (watchdogFrequency > 0) {
            String resname = getClass().getCanonicalName().replace('.', '_');
            TimeResource t = am.getResourceManagement().createResource(resname, TimeResource.class);
            t.activate(true);
            t.addValueListener(r -> {
                SDNotify.sendWatchdog();
                am.getLogger().debug("still alive...");
            }, true);
            ses.scheduleAtFixedRate(() -> {
                t.setValue(System.currentTimeMillis());
            }, 0, watchdogFrequency / 2, TimeUnit.MICROSECONDS
            );
            long freq = SDNotify.getWatchdogFrequency() / 2 / 1_000_000;
            am.getLogger().info("systemd watchdog will be notified every {}s", freq);
        }
    }

    @Override
    public void stop(AppStopReason asr) {
        ses.shutdown();
    }

    public static void main(String[] args) {

        String s = ManagementFactory.getRuntimeMXBean().getName();
        String pid = s.substring(0, s.indexOf("@"));
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        System.out.println(pid);

    }

}
