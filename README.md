# Retro AC Example for KeY

This is a an example how Retrospective Assertion Checking can be done
with [KeY](https://www.key-project.org).
To be specific, the example is Quicksort, and we will show that
the sorting is correct for as many recorded control flow traces as you want.

Just a demo, don't expect anything more.

## Dependencies

* A current version of [KeY](https://www.key-project.org), tested with version 2.12.2,
downloadable from [key-project.org](https://www.key-project.org).
* Java Compiler, Java Runtime (anything above Java 8 should be fine)
* (Gradle will download itself in the building step)

## Building

```
gradlew shadowJar
```

## Recording

```
java -jar quicksort-main/build/libs/quicksort-main-all.jar 3 14 1 1455 -10
```

Then an array with the given numbers is sorted. The trace goes into `trace-out/quicksort_*/`.

Or for the other example:

```
java -jar DualPivot-main/build/libs/DualPivot-main-all.jar
```

This also sorts some numbers. The trace goes into `trace-out/DualPivot_*/`.


## Validating

Open KeY, navigate into `trace-out/` and open one of the generated `*.key` proof
obligation files. Just press KeY's play button and it will take around 5 minutes.

## Further Background

The instrumentation for DualPivot was done automatically, the code for doing that
will be published soon. For C, we already [implemented it](https://github.com/lks9/src-tracer),
which is also quite more optimized to minimize run-time overhead and trace size.
Validating is also done fully automatically.
With the exact same pre- and post-condition, we can validate any traces for any
sorting algorithm, not just quicksort. And we don't need loop invariants and the like.

Be aware that validation with KeY is not the most efficient thing to do.
Try validating a trace from sorting an input array of 11 numbers, it still works fine,
but takes about 6 minutes on my computer, includeing 4x pressing the "continue" button in KeY.
The final proof for array input `3 14 1 1455 -10 3 4 1 9 -10 15` consists of about
34000 nodes. I would suggest starting with a smaller array as given in the recording section.

## Directory Structure

* `quicksort/`: Contains `Quicksort.java`, the manually instrumented quicksort implementation.
  The [original implementation](https://github.com/KeYProject/key/tree/main/key.ui/examples/heap/quicksort)
  is one of the examples from the KeY project.
* `quicksort-main/`: Contains a main method to run quicksort from command line arguments.
  Not instrumented.
* `quicksort-tracer/`: Contains the `Trace.java` implementation, used when tracing. To validate the
  trace, a different `Trace.java` is generated into `trace-out/trace*/prorunvis/`.

There is the same directory structure for `DualPivot`.

Technically, `quicksort-tracer/` and `DualPivot-tracer/` work the same, the only difference is
in the names of proof obligations and trace files which we included into the Java sources.
Normally, we would need to post-process the trace files to include this information afterwards,
but for demonstration the current way is sufficient and quick.
