/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}
bestTractorUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f880").
myUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f882").
/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .hermes.configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f882");
    .hermes.connect("1");
    .print("Hello world - I am the Tractor 2!!!");
    !work.


+!work : day & bestTractorUUID(BestID) & myUUID(MyID) <-
    .print("I am new here!.");
    .wait(2000);
    .print("I don't know how to work efficiently!");
    .wait(2000);
    .print("So the most efficient tractor will have its knowledge cloned for me.");
    .hermes.sendOut(BestID, tell, newDevice(MyID)).