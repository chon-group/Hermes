/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("2", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f882");
    .connect("2");
    .print("Hello world - autoConnection - inquilinism!!!").

+hellodois[source(X)]: true <-
    .print("Another Hermes agent tell me Hellodois - Receiver!");
    -hellodois[source(X)].