/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("3", "192.168.0.103", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("3");
    .print("Hello world!!!");
    .wait(2000);
    .print("Starting Inquilinism");
    .moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", inquilinism).

+hello[source(X)]: true <-
    .print("Another Hermes agent tell me Hello - Sender!");
    -hello[source(X)].