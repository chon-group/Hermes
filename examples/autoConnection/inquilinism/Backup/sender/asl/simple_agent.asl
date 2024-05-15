/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .hermes.configureContextNetConnection("3", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .hermes.connect("3");
    .print("Hello world - autoConnection - inquilinism!!!");
    .print("Starting Inquilinism");
    .hermes.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", inquilinism).

+hello[source(X)]: true <-
    .print("Another Hermes agent tell me Hello - Sender!");
    -hello[source(X)].