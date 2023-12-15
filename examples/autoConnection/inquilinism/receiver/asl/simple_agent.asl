/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .configureContextNetConnection("4", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f884");
    .connect("1");
    .connect("4");
    .print("Hello world - autoConnection - inquilinism!!!").

+hello[source(X)]: true <-
    .print("Another Hermes agent tell me Hello - Receiver!");
    -hello[source(X)].

+hellodois[source(X)]: true <-
    .print("Another Hermes agent tell me Hellodois - Receiver!");
    -hellodois[source(X)].
