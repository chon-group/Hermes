/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .connect("1");
    .setTrophicLevel("PRIMARY_CONSUMER");
    .print("Hello world - setTrophicLevel - withName!!!").

+Hermes::myTrophicLevel(X) : true <-
    .print("The new trophic level is: ", X).

-Hermes::myTrophicLevel(Y) : true <-
    .print("The trophic level removed was ", Y).