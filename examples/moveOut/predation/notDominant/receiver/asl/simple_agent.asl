/* Initial beliefs and rules */
day.
receiver.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .setTrophicLevel("SECONDARY_CONSUMER");
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .connect("1");
    .print("Hello world - moveOut - predation - notDominant!!!").
