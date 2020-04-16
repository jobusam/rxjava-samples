package de.busam.samples.rxjava.endlesslooping;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Run an flowable that emits periodically events in an endless loop
 * and only stops if an specific marker file called "stopApp" is created in
 * the current working directory. Additionally this flowable uses
 * a fixed thread pool for the long running tasks.
 * But otherwise it will not execute an task in parallel. Due to the nature
 * of {@link Flowable#repeat()} it will only process the events in sequence
 * but on different threads (depending on amount of threads in thread pool
 * of the scheduler.
 * <p>
 * # To exit the app also a ShutdownHook could be registered in the JVM
 * but the main problem is the gradle application plugin with it's run
 * command can't forward the SIGKILL signal to the jvm and therefore
 * no ShutdownHooks can be executed when running the app with
 * $ ./gradlew run
 */
public class RepeatedFlowableWithThreadPool {
    private static final Path stopFile = Paths.get("stopApp");

    private Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(2));

    public void execute() {
        log(String.format("Create the file <%s> to finish the flowable gracefully.", stopFile.toAbsolutePath()));

        Flowable.fromCallable(Math::random)
                .delay(10,TimeUnit.SECONDS,scheduler)
                .doOnNext(event -> log("new random value: "+event))
                .map(this::someLongRunningAction)
                .doOnNext(event -> log("long running action executed: "+event))
                .repeat()
                .takeUntil(RepeatedFlowableWithThreadPool::stopFileExist)
                .subscribeOn(scheduler)
                .subscribe(onNext -> log("Finished round " + onNext),
                        throwable -> throwable.printStackTrace(),
                        this::onComplete);
    }

    static Boolean stopFileExist(Double __) {
        if (Files.exists(stopFile)) {
            log("Stop File found. Shutdown flowable gracefully.");
            return true;
        }
        return false;
    }

    void onComplete() throws IOException {
        log("Interval flowable finished (onComplete called).");
        Files.deleteIfExists(stopFile);
        scheduler.shutdown();
        // System.exit has to be called other. Otherwise the app wouldn't exit
        // because the Main-Thread is waiting for something?!?
        System.exit(0);
    }

    Double someLongRunningAction(Double value) throws InterruptedException {
        Thread.sleep(10000L);
        return value;
    }

    static void log(String message) {
        System.out.println(String.format("%s - %s \t: %s", new Date().toString(), Thread.currentThread().getName(), message));
    }
}
