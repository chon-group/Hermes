/* Initial beliefs and rules */
day.
sender.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("1");
    .print("Hello world - moveOut - predation - withSameTrophicLevel!!!");
    .wait(2000);
    .print("Starting protocol");
    .moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", predation).