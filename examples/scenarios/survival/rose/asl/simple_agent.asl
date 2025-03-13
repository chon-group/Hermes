/* Initial beliefs and rules */

{include("examples/contextnetAgent.asl")}
jackUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f881").
myUUID("788b2b22-baa6-4c61-b1bb-01cff1f5f882").
/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) & myUUID(ID) <-
    .hermes.setTrophicLevel("SECONDARY_CONSUMER");
    .hermes.configureContextNetConnection("1", IP, PORT, ID);
    .hermes.connect("1");
    .print("Hello world - I am Rose!!!");
    !safe.


+!safe : jackUUID(JackID) & not jackWillDie <-
    .print("I am safe here in my boat!.");
    .wait(2000);
    .print("Are you ok, Jack?");
    .wait(2000);
    .hermes.sendOut(JackID, askOne, ok).

+ok : not jackWillDie <-
    .print("I'm glad that you are ok!");
    .wait(10000);
    +boat(broke).

+boat(broke) : jackUUID(JackID) & not jackWillDie <-
    .print("Oh my God! My boat broke!");
    .wait(2000);
    .print("Please help me, Jack!");
    .hermes.sendOut(JackID, tell, helpMe).

+takeMyBoat : jackUUID(JackID) & not jackWillDie <-
    .print("No, I won't take your boat and let you die!");
    .wait(3000);
    .print("There has to be another way!");
    .hermes.sendOut(JackID, tell, anotherWay).

+doesNotHave : jackUUID(JackID) & not jackWillDie <-
    .print("Nooooooooooo, I love you, Jack!");
    .wait(3000);
    .print("I will never forget you!");
    .wait(3000);
    .print("I'll take over your boat then!");
    .wait(3000);
    +jackWillDie;
    .wait(jackWillDie);
    .hermes.moveOut(JackID, predation).

+hermes::myMAS(MAS)[source(AgtName)] : MAS = "jackMAS" <-
    .print("I took the jack boat!");
    .wait(3000);
    .print("Now I have to survive at any cost so that Jack's death is not in vain.!");
    .wait(3000);
    !survival.

+!survival : true <-
    .print("Surviving...");
    .wait(2000);
    !survival.