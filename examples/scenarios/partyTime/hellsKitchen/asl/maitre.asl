/* Initial beliefs and rules */
/* To generate a random UUID use https://www.uuidgenerator.net/ */
restaurantAddress("af467a22-eafc-4e87-9f57-882740ab0710").
enableCallUber(true).
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start : contextnetServer(IP, PORT) & restaurantAddress(MyID) <-
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