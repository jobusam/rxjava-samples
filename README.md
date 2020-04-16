# rxjava-samples
This project contains RxJava samples based on RxJava 3 and the Flowable API
(Also Java 13 and Gradle 6.0 is used).

The [Main](src/main/java/de/busam/samples/rxjava/Main.java) executes three
samples how to handle closable resources within Flowables.

* [Sample1](src/main/java/de/busam/samples/rxjava/resourcehandling/Sample1.java): 
The caller is responsible for the resources lifecycle

* [Sample2](src/main/java/de/busam/samples/rxjava/resourcehandling/Sample2.java): 
The flowable itself is responsible for the resources lifecycle. It
closes the resource with the Flowable#doFinally() method!

* [Sample3](src/main/java/de/busam/samples/rxjava/resourcehandling/Sample3.java): 
The flowable itself is responsible for the resources lifecycle
using the Flowable#using() method!


Run the samples with ``./gradlew run`` command.

## Further samples
* The [Interval Flowable](src/main/java/de/busam/samples/rxjava/endlesslooping/IntervalFlowable.java)
is flowable the emits events in an endless loop until a specific marker file is
created on local file system. With this implementation a flowable can be stopped 
gracefully. Additionally the interval flowable itself is executed in the main thread
and processes events sequentially. But there is also a another implementation in this
class where the events will be processed by some long running subtasks that are
executed in parallel by usage of flatMap operator combined with the IO Scheduler.

* The [RepeatedFlowableWithThreadPool](src/main/java/de/busam/samples/rxjava/endlesslooping/RepeatedFlowableWithThreadPool.java)
is another example how execute events in an endless loop an different thread (provided by a thread pool).
This example processes the events also sequentially.

