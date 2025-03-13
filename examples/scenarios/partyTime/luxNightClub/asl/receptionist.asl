/* Initial beliefs and rules */
/* To generate a random UUID use https://www.uuidgenerator.net/ */
luxNightClubAddress("e2786edc-c48b-4cf6-bf68-22dd0add2025").
enableCallUber(true).
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start : contextnetServer(IP, PORT) & luxNightClubAddress(MyID) <-
	.print("Connecting in IoT network!");
	.hermes.setTrophicLevel("PRIMARY_CONSUMER");
    .hermes.configureContextNetConnection("1", IP, PORT, MyID);
    .hermes.connect("1");
	.print("Connected").

+!callUber(Address) : true <-
	.hermes.moveOut(Address, mutualism, giacomo).

-!callUber : true <-
	.wait(1000);
	.print("Bad internet or no driver accepted the ride.");
	!callUber.