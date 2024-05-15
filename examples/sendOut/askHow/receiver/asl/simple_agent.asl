/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .hermes.configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .hermes.connect("1");
    .print("Hello world - askHow!!!").

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