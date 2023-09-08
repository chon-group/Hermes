/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("1", "192.168.0.103", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .configureContextNetConnection("4", "192.168.0.103", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f884");
    .connect("1");
    .connect("4");
    .print("Hello world!!!").

+hello[source(X)]: true <-
    .print("Another Hermes agent tell me Hello - Receiver!");
    -hello[source(X)].

/*+contextNetConfiguration(X,Y,Z,A,B,C): true <-
    .print("Configuration: ", X).

-contextNetConfiguration(D,E,F,G,H,I): true <-
    .print("Removed Configuration: ", D).*/