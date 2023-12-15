/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* "788b2b22-baa6-4c61-b1bb-01cff1f5f880" */

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("1");
    .print("Hello world - askHow!!!");
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", askHow, {+!run});
    .wait(2000);
    !run.