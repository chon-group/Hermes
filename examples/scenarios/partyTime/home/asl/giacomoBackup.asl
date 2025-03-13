/* Initial beliefs */
/* To generate a random UUID use https://www.uuidgenerator.net/ */
homeAddress("80d9c5b3-5327-4836-b722-7481061affef").
restaurantAddress("af467a22-eafc-4e87-9f57-882740ab0710").
luxNightClubAddress("e2786edc-c48b-4cf6-bf68-22dd0add2025").

enableCallUber(false).
foodStock(0).
energy(1).
fresh(1).

/* Initial goals */

/* Plans */
+!enjoyTheParty: energy(E) & E>0 & fresh(F) & F>0 & hermes::myMAS(MAS)[source(AgtName)] & MAS = "luxNightClub" <-
	!info("Party time!!!");
	-+energy(E-1);
	-+fresh(F-1);
	+party(funny);
	!enjoyTheParty.

+!enjoyTheParty: fresh(F) & F<=0 & foodStock(Qtd) & Qtd<=0 & hermes::myMAS(MAS)[source(AgtName)] & MAS = "luxNightClub" <-
	!info("I'm so tired... I need to go home to rest!");
	!goHome.

+!enjoyTheParty: fresh(F) & F<=1 & energy(E) & foodStock(Qtd) & Qtd>=1 & hermes::myMAS(MAS)[source(AgtName)] & MAS = "luxNightClub" <-
	!info("Break to rest and eat the stocked food!");
	-+energy(E+Qtd);
	-+fresh(F+Qtd);
	-+foodStock(0);
	!enjoyTheParty.

-!enjoyTheParty : true <-
	!info("There is only Party Time if I am at the LuxNightClub!").

+!goHome : hermes::myMAS(MAS)[source(Y)] & MAS \== "home" & homeAddress(Address) <-
	!info("goingHome!");
	.broadcast(askAll,enableCallUber(true));
	.wait(enableCallUber(true)[source(AgtName)]);
	-enableCallUber(true)[source(AgtName)];
	.wait(1000);
	.send(AgtName, achieve, callUber(Address)).

+!goHome : hermes::myMAS(MAS)[source(Y)] & MAS = "home" <-
	!info("estou achando que estou em casa!");
	!rest.

-!goHome : true <-
	!info("entrei na excecao");
	.wait(1000);
	!goHome.

+!rest : fresh(F) & F<10 & hermes::myMAS(MAS)[source(AgtName)] & MAS = "home" <-
	!info("I'm resting");
	-+fresh(F+1);
	.wait(fresh(X));
	!rest.

+!rest : fresh(F) & F>9 & energy(E) & E>9 & hermes::myMAS(MAS)[source(AgtName)] & MAS = "home" <-
	!info("I am fully fresh and satisfied! So, Party time!!!");
	!goToTheParty.

-!rest : hermes::myMAS(MAS)[source(AgtName)] & MAS = "home" <-
	!info("I am fully fresh! So, I'm going to the restaurant");
	!goRestaurant.

-!rest : true <-
	!info("I can only rest at home!").

+!goRestaurant : hermes::myMAS(MAS)[source(Y)] & MAS \== "hellsKitchen" & restaurantAddress(Address) <-
	.broadcast(askAll,enableCallUber(true));
	.wait(enableCallUber(true)[source(AgtName)]);
	-enableCallUber(true)[source(AgtName)];
	.wait(1000);
	.send(AgtName, achieve, callUber(Address)).

+!goRestaurant : hermes::myMAS(MAS)[source(Y)] & MAS = "hellsKitchen" <-
	!lunch.

-!goRestaurant : true <-
	.wait(1000);
	!goRestaurant.

+!lunch: energy(E) & E>9 & hermes::myMAS(MAS)[source(AgtName)] & MAS = "hellsKitchen" <-
	!info("Satisfied");
	!goToParty.

+!lunch: foodStock(C) & C>0 & energy(E) & E<=9 & hermes::myMAS(MAS)[source(AgtName)] & MAS = "hellsKitchen" <-
	-+foodStock(C-1);
	-+energy(E+1);
	!info("Eating");
	+restaurant(delicious);
	!lunch.

+!lunch : hermes::myMAS(MAS)[source(AgtName)] & MAS \== "hellsKitchen" <-
	!info("I can only lunch at the restaurant!").

-!lunch : true <-
	!orderFood.

+!orderFood: not skill(chef)[source(AgtName)] <-
	.broadcast(askAll,skill(chef));
	!info("Looking for a Chef...");
	!lunch.

+!orderFood: skill(chef)[source(AgtName)]<-
	.send(AgtName,askOne,menu(List));
	.wait(menu(List)[source(AgtName)]);
	.random(List,Choice);
	.random(N); R=(N*10); Qtd=((R-(R mod 1))+1);
	!info("Ordering food.....");
	.send(AgtName,achieve,order(Choice,Qtd)).

+!goToParty : hermes::myMAS(MAS)[source(Y)] & MAS \== "luxNightClub" & luxNightClubAddress(Address) <-
	.broadcast(askAll,enableCallUber(true));
	.wait(enableCallUber(true)[source(AgtName)]);
	-enableCallUber(true)[source(AgtName)];
	.wait(1000);
	.send(AgtName, achieve, callUber(Address)).

+!goToParty : hermes::myMAS(MAS)[source(Y)] & MAS = "luxNightClub" <-
	!enjoyTheParty.

-!goToParty : true <-
	.wait(1000);
	!goToParty.

+!info(Msg): foodStock(F) & energy(E) & fresh(G) & .random(R) <-
    .wait(3000*R);
	.print("[foodStock=",F,"] [energy=",E,"] [fresh=",G,"] ",Msg).

/* Belief's Plans */
+foodArrived(Order,Product,Qtd)[source(Vendor)] <-
	-foodArrived(Order,Product,Qtd)[source(Vendor)];
	.wait(1000);
	?foodStock(X);
	-+foodStock(X+Qtd);
	.wait(foodStock(Y));
	!info("Food arrived");
	!lunch.

+hermes::myMAS(MAS)[source(AgtName)] : MAS = "home" & AgtName = mom <-
	!info("I'm home! So, I'm going to rest!");
	!rest.

+hermes::myMAS(MAS)[source(AgtName)] : MAS = "hellsKitchen" & AgtName = maitre <-
	!info("I'm at the restaurant! So, I'm going to have lunch!");
	!lunch.

+hermes::myMAS(MAS)[source(AgtName)] : MAS = "luxNightClub" & AgtName = receptionist <-
	!info("I'm at the LuxNightClub! So, party time!");
	!enjoyTheParty.
