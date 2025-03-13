{include("examples/contextnetAgent.asl")}
/* Initial beliefs and rules */
address(home,mom,"80d9c5b3-5327-4836-b722-7481061affef").
address(hellsKitchen,maitre,"af467a22-eafc-4e87-9f57-882740ab0710").
address(luxNightClub,receptionist,"e2786edc-c48b-4cf6-bf68-22dd0add2025").
enableCallUber(true).

/* Initial goals */
!connectIoT.

/* Plans */
+!connectIoT: contextnetServer(IP, PORT) & .my_name(WhoAmI) & address(MAS,WhoAmI,UUID) <-
	.hermes.configureContextNetConnection("1", IP, PORT, UUID); .hermes.connect("1"); +online;
	.print("Connected in IoT network with address:", UUID); 
	!keepAlive(30).
-!connectIoT <- !keepAlive(0).

+!keepAlive(Limit) <- -+timeout(Limit); !hangUp.
+!hangUp: timeout(T) & T >0  <- -+timeout(T-5); .wait(5000); .print(T); !hangUp.
+!hangUp: timeout(T) & T <=0 <- -online; .hermes.disconnect("1"); .wait(3000); !connectIoT.


+!callUber(Destination)[source(Agt)] <- !sendAnAgent(Destination,Agt).

+!sendAnAgent(Destination,Agt) <-
	!!trySendAnAgent(Destination,Agt);
	.wait(15000);
	!confirm(Agt,Destination).

+!trySendAnAgent(Destination,Agent): online <- 
	.print("Trying to send ",Agent," to: ",Destination);
	-+timeout(10); 
	.hermes.moveOut(Destination, mutualism, Agent).
-!trySendAnAgent(Destination,Agent).

+!confirm(Agent,Destination) <- .all_names(AgtList); !findAgt(Agent,set(AgtList),Destination).

+!findAgt(Wanted,set([Head|Tail]),Destination) <- !compare(Wanted,Head,Destination); !findAgt(Term,set(Tail),Destination).
-!findAgt(Wanted,set([   ]),Destination).

+!compare(Wanted,Agent,Destination): Wanted == Agent <- !sendAnAgent(Destination,Wanted).
-!compare(Wanted,Agent,Destination).