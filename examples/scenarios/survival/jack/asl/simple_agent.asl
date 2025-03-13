/* Initial beliefs and rules */
ok.
{include("examples/contextnetAgent.asl")}
roseUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f882").
myUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f881").
/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) & myUUID(ID) <-
    .hermes.setTrophicLevel("PRIMARY_CONSUMER");
    .hermes.configureContextNetConnection("1", IP, PORT, ID);
    .hermes.connect("1");
    .print("Hello world - I am Jack!!!");
    .print("I am okay! I found another boat!").

+helpMe : roseUUID(RoseID) <-
    .print("Oh my God! Only one of us will get out of here alive!");
    .wait(3000);
    .print("And it must be you! You need to take my boat!");
    .wait(3000);
    .hermes.sendOut(RoseID, tell, takeMyBoat).

+anotherWay : roseUUID(RoseID) <-
    .print("There is no other way!");
    .wait(3000);
    .print("The boat only holds one person!");
    .wait(3000);
    .hermes.sendOut(RoseID, tell, doesNotHave).
