/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) & day <-
    .hermes.configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .hermes.connect("1");
    .hermes.setTrophicLevel(2);
    -day;
    .print("Hello world - cryogenic - byInternalAction!!!");
    .send(agent1, tell, hello);
    !doCryogenic;
    !start.

-!start: true <-
    .print("was cryogenated - cryogenic - byInternalAction!!!").

+!doCryogenic : true <-
    .print("Preparing for cryonics!!!");
    .wait(500);
    .hermes.cryogenic.
