/* Initial beliefs and rules */
day.
beautiful(true).
beautiful(false).
beautiful(medium).
beautiful(high).
beautiful(low).

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .print("Hello world!!!").
