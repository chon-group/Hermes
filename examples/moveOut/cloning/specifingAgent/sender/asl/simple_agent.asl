/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("1", "192.168.250.195", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("1");
    .print("Hello world!!!");
    .moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", cloning, agent1).