/* Initial beliefs and rules */
day.
sender.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .setTrophicLevel("PRIMARY_CONSUMER");
    .configureContextNetConnection("1", "192.168.0.105", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("1");
    .print("Hello world!!!");
    .wait(2000);
    .print("Starting protocol");
    .moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", predation).