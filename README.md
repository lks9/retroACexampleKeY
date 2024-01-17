# Retro AC Example for KeY

This is a an example how Retrospective Assertion Checking can be done with KeY.
To be specific, the example is Quicksort, and we will show that
the sorting is correct for as many recorded control flow traces as you want.

Just a demo, don't expect anything more.

## Building

```
gradlew shadowJar
```

## Recording

```
java -jar quicksort-main/build/libs/quicksort-main-all.jar 3 14 1 1455 -10
```

Then an array with the given numbers is sorted. The trace goes into `trace-out/trace*`.

## Validating

Open KeY, navigate into `trace-out/` and show the proof obligation.

## Further Background

The instrumentation of Quicksort was done manually, but automatic instrumentation is on the way...
For C, I already have automatic instrumentation!
Apart form that, recording and validating can be done fully automatically.
With the exact same pre- and post-condition, we can validate any traces for any
sorting algorithm, not just quicksort. And we don't need loop invariants and the like.

Be aware that validation with KeY is not the most efficient thing to do.
Try validating a trace from sorting an input array of 11 numbers, it still works fine,
but takes about 6 minutes on my computer, includeing 4x pressing the "continue" button in KeY.
The final proof for array input "3 14 1 1455 -10 3 4 1 9 -10 15" consists of about
34000 nodes. I would suggest starting with a smaller array as given in the recording section.
