/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .hermes.configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .hermes.connect("1");
    .hermes.setTrophicLevel("PRIMARY_CONSUMER");
    .print("Hello world - setTrophicLevel - withName!!!").

+hermes::myTrophicLevel(X) : true <-
    .print("The new trophic level is: ", X).

-hermes::myTrophicLevel(Y) : true <-
    .print("The trophic level removed was ", Y).