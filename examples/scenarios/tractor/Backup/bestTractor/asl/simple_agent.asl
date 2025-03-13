/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}
myUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f880").
/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) & myUUID(ID) <-
    .hermes.configureContextNetConnection("1", IP, PORT, ID);
    .hermes.connect("1");
    .print("Hello world - I am the BEST Tractor!!!");
    .wait(5000);
    !!work.


+!work : day <-
    .print("Work!");
    .wait(2000);
    !work.

+newDevice(UUID)[source(X)] : myUUID(MyID) <-
    -newDevice(UUID)[source(X)];
    .print("New connected device!");
    .wait(3000);
    .print("Since I am the most efficient tractor!");
    .wait(3000);
    .print("I will clone my knowledge to the new tractor.");
    .wait(3000);
    .hermes.moveOut(UUID, cloning).
