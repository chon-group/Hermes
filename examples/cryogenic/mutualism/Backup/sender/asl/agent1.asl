/* Initial beliefs and rules */
day.
agent1.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .print("Hello world!!!").

-Hermes::myMAS(X)[source(Y)]: true <-
    .wait(500);
    .send(simple_agent, achieve, cryogenic).