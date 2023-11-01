/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("3", "169.254.7.146", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("3");
    .print("Hello world!!!");
    .print("Starting Inquilinism");
    .moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", inquilinism).

+hello[source(X)]: true <-
    .print("Another Hermes agent tell me Hello - Sender!");
    -hello[source(X)].