/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("2", "192.168.0.103", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f882");
    .connect("2");
    .print("Hello world!!!").

+hello[source(X)]: true <-
    .print("Another Hermes agent tell me Hello - Sender Hermes 2!");
    -hello[source(X)].