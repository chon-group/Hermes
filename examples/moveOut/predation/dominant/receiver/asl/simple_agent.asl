/* Initial beliefs and rules */
day.
receiver.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .setTrophicLevel("SECONDARY_CONSUMER");
    .configureContextNetConnection("1", "192.168.1.107", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .connect("1");
    .print("Hello world!!!").
