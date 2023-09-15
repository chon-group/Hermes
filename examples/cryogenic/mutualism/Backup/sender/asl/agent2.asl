/* Initial beliefs and rules */
day.
agent2.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .print("Hello world!!!");
    .wait(500);
    !start.