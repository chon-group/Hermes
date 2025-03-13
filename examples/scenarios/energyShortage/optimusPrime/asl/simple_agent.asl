/* Initial beliefs and rules */
{include("examples/contextnetAgent.asl")}
myUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f881").
/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) & myUUID(ID) <-
    .hermes.setTrophicLevel("PRIMARY_CONSUMER");
    .hermes.configureContextNetConnection("1", IP, PORT, ID);
    .hermes.connect("1");
    .print("Hello world - I am Optimus Prime MAS!!!").

+!energyCritical: true <-
    .print("The energy resources agent tell that we are in a critical state!");
    .wait(3000);
    .print("So, I will cryogenate MAS to preserve the little energy resource for an emergency!");
    .wait(3000);
    .hermes.cryogenic.
