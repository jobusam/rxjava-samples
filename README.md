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
* [Interval Flowable](src/main/java/de/busam/samples/rxjava/IntervalFlowable.java)
is flowable the emits events in endless loop until a specific marker file is
created on local file system. With this implementation a flowable can be stopped 
gracefully.
