/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("1", "169.254.7.146", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .configureContextNetConnection("4", "169.254.7.146", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f884");
    .connect("1");
    .connect("4");
    .print("Hello world!!!").

+hello[source(X)]: true <-
    .print("Another Hermes agent tell me Hello - Receiver!");
    -hello[source(X)].

+hellodois[source(X)]: true <-
    .print("Another Hermes agent tell me Hellodois - Receiver!");
    -hellodois[source(X)].
