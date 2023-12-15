/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("1");
    .print("Hello world - untell!!!");
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tell, beautiful);
    .wait(1000);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", untell, beautiful);
    .print("Disconnecting");
    .disconnect("1").