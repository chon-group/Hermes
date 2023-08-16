/* Initial beliefs and rules */
day.
beautiful(true).

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .print("Hello world!!!").
