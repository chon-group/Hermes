/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .connect("1");
    .setTrophicLevel(2);
    -day;
    .print("Hello world - cryogenic - byFile!!!");
    .send(agent1, tell, hello).

-!start: true <-
    .print("was cryogenated!!!").

