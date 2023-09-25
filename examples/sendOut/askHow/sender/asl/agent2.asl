/* Initial beliefs and rules */
day.
beautiful(true).

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .print("Hello world!!!").

+!run: true <-
    .print("I am Running: true");
    .wait(500);
    !run.

+!run: day <-
    .print("I am Running: day");
    .wait(500);
    !run.

+!run: night <-
    .print("I am Running: night");
    .wait(500);
    !run.

-!run: true <-
    .print("I am Running: conditional");
    .wait(500);
    !run.