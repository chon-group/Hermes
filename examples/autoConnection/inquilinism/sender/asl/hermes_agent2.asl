/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("2", "169.254.7.146", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f882");
    .connect("2");
    .print("Hello world!!!").

+hellodois[source(X)]: true <-
    .print("Another Hermes agent tell me Hellodois - Receiver!");
    -hellodois[source(X)].