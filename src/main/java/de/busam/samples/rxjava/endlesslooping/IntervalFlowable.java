package de.busam.samples.rxjava.endlesslooping;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
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
 * Due to the fact that the Trampoline Scheduler
 * is uses all interval events are scheduled within the main thread.
 * Therefore following behaviour is present:
 * The interval flowable emits every 5 seconds an event. But
 * if the {@link #someLongRunningAction(long)} needs more time than
 * 5 seconds to process the event the interval flowable will emit
 * the event later (after the {@link #someLongRunningAction(long)} is
 * finished with the previous one. Therefore the next event
 * will be emitted  in max(interval-time, time-of-processing-the-previous-event) seconds.
 * <p>
 * This is the exact behaviour if you want to periodically check any status
 * and process them in sequence without knowing how long the processing takes.
 *
 *
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
        Flowable.interval(0L, 5L, TimeUnit.SECONDS, Schedulers.trampoline())
                .doOnNext(onNext -> log("Start Round:" + onNext))
                .map(IntervalFlowable::someLongRunningAction)
                .takeUntil(IntervalFlowable::stopFileExist)
                .subscribe(onNext -> log("Finished round " + onNext),
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
        Thread.sleep(8000L);
        return round;
    }

    static void log(String message) {
        System.out.println(String.format("%s - %s \t: %s", new Date().toString(), Thread.currentThread().getName(), message));
    }


    /**
     * This method is like original {@link #execute()} method.
     * But now the long running task is splitted up into multiple long running tasks
     * that do some in parallel on IO Scheduler.
     */
    public void executeWithExtendedWorkload() {
        log(String.format("Create the file <%s> to finish the flowable gracefully.", stopFile.toAbsolutePath()));
        Flowable.interval(0L, 5L, TimeUnit.SECONDS, Schedulers.trampoline())
                .doOnNext(onNext -> log("Start Round:" + onNext))
                .map(IntervalFlowable::extendedLongRunningAction)
                .takeUntil(IntervalFlowable::stopFileExist)
                .subscribe(onNext -> log("Finished round " + onNext),
                        throwable -> throwable.printStackTrace(),
                        IntervalFlowable::onComplete);
    }

    static Long extendedLongRunningAction(long round) throws InterruptedException {
        log("Round: "+round+": extended long running action called!");
        return Observable.range(0, 3)
                .flatMap(i -> Observable.just(i)
                        .subscribeOn(Schedulers.io())
                        .doOnNext(value -> log("Round: " + round + " - subround:" + value))
                        .map(IntervalFlowable::someLongRunningAction))
                .count()
                .doOnSuccess(value -> log("Round: "+round+" - finished "+value+" subrounds"))
                .map(count -> round)
                .blockingGet();
    }
}
