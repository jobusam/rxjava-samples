package de.busam.samples.rxjava;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Run an flowable that emits periodically events in an endless loop
 * only stop if an specific marker file called "stopApp" is created in
 * the current working directory.
 * <p>
 * # To exit the app also a ShutdownHook could be registered in the JVM
 * but the main problem is the gradle application plugin with it's run
 * command can't forward the SIGKILL signal to the jvm and therefore
 * no ShutdownHooks can be executed when running the app with
 * $ ./gradlew run
 */
public class IntervalFlowable {
    private static final Path stopFile = Paths.get("stopApp");

    public void execute() {
        log(String.format("Create the file <%s> to finish the flowable gracefully.", stopFile.toAbsolutePath()));
        Flowable.interval(1, 5, TimeUnit.SECONDS, Schedulers.io())
                .doOnNext(onNext -> log("Start Round:" + onNext))
                .map(IntervalFlowable::someLongRunningAction)
                .takeUntil(IntervalFlowable::stopFileExist)
                .blockingSubscribe(onNext -> log("Finished round " + onNext),
                        throwable -> throwable.printStackTrace(),
                        IntervalFlowable::onComplete);
    }

    static Boolean stopFileExist(Long __) {
        if (Files.exists(stopFile)) {
            log("Stop File found. Shutdown flowable gracefully.");
            return true;
        }
        return false;
    }

    static void onComplete() throws IOException {
        log("Interval flowable finished");
        Files.delete(stopFile);
    }

    static long someLongRunningAction(long round) throws InterruptedException {
        Thread.sleep(6000L);
        return round;
    }

    static void log(String message) {
        System.out.println(String.format("%s - %s \t: %s", new Date().toString(), Thread.currentThread().getName(), message));
    }
}
