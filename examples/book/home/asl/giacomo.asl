/* Initial beliefs and rules */
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_175]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_228]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_158]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_135]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_174]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_119]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_82]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_107]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_72]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_36]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_50]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_31]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([],[])[hide_in_mind_inspector,source(self)].
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
enableCallUber(true)[source(receptionist)].
restaurantAddress("af467a22-eafc-4e87-9f57-882740ab0710")[source(self)].
homeAddress("80d9c5b3-5327-4836-b722-7481061affef")[source(self)].
fresh(1)[source(self)].
foodStock(0)[source(self)].
energy(1)[source(self)].
menu(["Lamb rack with sprout salad","Popcorn chicken with basil","Potato tortilla","Butter chicken moussaka","Matt Preston's ice cream brioche rolls"])[source(gordon)].
skill(chef)[source(gordon)].
luxNightClubAddress("e2786edc-c48b-4cf6-bf68-22dd0add2025")[source(self)].
hermes::myMAS("luxNightClub")[source(receptionist)].

/* Initial goals */

/* Plans */
@p__15[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!enjoyTheParty : (energy(E) & ((E > 1) & (fresh(F) & ((F > 1) & (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "luxNightClub")))))) <- !info("Party time!!!"); -+energy((E-1)); -+fresh((F-1)); !enjoyTheParty.
@p__16[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!enjoyTheParty : (fresh(F) & ((F <= 1) & (foodStock(Qtd) & ((Qtd < 1) & (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "luxNightClub")))))) <- !info("I'm so tired... I need to go home to rest!"); !goHome.
@p__17[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!enjoyTheParty : (fresh(F) & ((F <= 1) & (energy(E) & (foodStock(Qtd) & ((Qtd >= 1) & (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "luxNightClub"))))))) <- !info("Break to rest and eat the stocked food!"); -+energy((E+Qtd)); -+fresh((F+Qtd)); -+foodStock(0); !enjoyTheParty.
@p__18[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] -!enjoyTheParty <- !info("There is only Party Time if I am at the LuxNightClub!").
@p__19[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!goHome : (hermes::myMAS(MAS)[source(Y)] & ((MAS \== "home") & homeAddress(Address))) <- !info("goingHome!"); .abolish(enableCallUber(_19)[source(_20)]); .broadcast(askAll,enableCallUber(true)); .wait(3000); ?enableCallUber(true)[source(Communicator)]; .send(Communicator,achieve,callUber(Address)).
@p__20[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!goHome : (hermes::myMAS(MAS)[source(Y)] & (MAS = "home")) <- !info("estou achando que estou em casa!"); !rest.
@p__21[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] -!goHome <- !info("entrei na excecao"); .wait(1000); !goHome.
@p__22[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!rest : (fresh(F) & ((F < 5) & (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "home")))) <- !info("I'm resting"); -+fresh((F+1)); .wait(fresh(X)); !rest.
@p__23[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!rest : (fresh(F) & ((F > 4) & (energy(E) & ((E > 4) & (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "home")))))) <- !info("I am fully fresh and satisfied! So, Party time!!!"); !goToTheParty.
@p__24[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] -!rest : (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "home")) <- !info("I am fully fresh! So, I'm going to the restaurant"); !goRestaurant.
@p__25[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] -!rest <- !info("I can only rest at home!").
@p__26[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!goRestaurant : (hermes::myMAS(MAS)[source(Y)] & ((MAS \== "hellsKitchen") & restaurantAddress(Address))) <- .broadcast(askAll,enableCallUber(true)); .wait(enableCallUber(true)[source(AgtName)]); -enableCallUber(true)[source(AgtName)]; .wait(1000); .send(AgtName,achieve,callUber(Address)).
@p__27[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!goRestaurant : (hermes::myMAS(MAS)[source(Y)] & (MAS = "hellsKitchen")) <- !lunch.
@p__28[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] -!goRestaurant <- .wait(1000); !goRestaurant.
@p__29[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!lunch : (energy(E) & ((E > 4) & (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "hellsKitchen")))) <- !info("Satisfied"); !goToParty.
@p__30[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!lunch : (foodStock(C) & ((C > 0) & (energy(E) & ((E <= 4) & (hermes::myMAS(MAS)[source(AgtName)] & (MAS = "hellsKitchen")))))) <- -+foodStock((C-1)); -+energy((E+1)); !info("Eating"); !lunch.
@p__31[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!lunch : (hermes::myMAS(MAS)[source(AgtName)] & (MAS \== "hellsKitchen")) <- !info("I can only lunch at the restaurant!").
@p__32[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] -!lunch <- !orderFood.
@p__33[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!orderFood : not (skill(chef)[source(AgtName)]) <- .broadcast(askAll,skill(chef)); !info("Looking for a Chef..."); !lunch.
@p__34[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!orderFood : skill(chef)[source(AgtName)] <- .send(AgtName,askOne,menu(List)); .wait(menu(List)[source(AgtName)]); .random(List,Choice); .random(N); (R = (N*10)); (Qtd = ((R-(R mod 1))+1)); !info("Ordering food....."); .send(AgtName,achieve,order(Choice,Qtd)).
@p__35[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!goToParty : (hermes::myMAS(MAS)[source(Y)] & ((MAS \== "luxNightClub") & luxNightClubAddress(Address))) <- .broadcast(askAll,enableCallUber(true)); .wait(enableCallUber(true)[source(AgtName)]); -enableCallUber(true)[source(AgtName)]; .wait(1000); .send(AgtName,achieve,callUber(Address)).
@p__36[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!goToParty : (hermes::myMAS(MAS)[source(Y)] & (MAS = "luxNightClub")) <- !enjoyTheParty.
@p__37[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] -!goToParty <- .wait(1000); !goToParty.
@p__38[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +!info(Msg) : (foodStock(F) & (energy(E) & (fresh(G) & .random(R)))) <- .wait((3000*R)); .print("[foodStock=",F,"] [energy=",E,"] [fresh=",G,"] ",Msg).
@p__39[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +foodArrived(Order,Product,Qtd)[source(Vendor)] <- -foodArrived(Order,Product,Qtd)[source(Vendor)]; .wait(1000); ?foodStock(X); -+foodStock((X+Qtd)); .wait(foodStock(Y)); !info("Food arrived"); !lunch.
@p__40[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +hermes::myMAS(MAS)[source(AgtName)] : ((MAS = "home") & (AgtName = mom)) <- !info("I'm home! So, I'm going to rest!"); !rest.
@p__41[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +hermes::myMAS(MAS)[source(AgtName)] : ((MAS = "hellsKitchen") & (AgtName = maitre)) <- !info("I'm at the restaurant! So, I'm going to have lunch!"); !lunch.
@p__42[source(self),url("file:examples/book/hellsKitchen/asl/giacomo.asl"),url("file:examples/book/home/asl/giacomo.asl"),url("file:examples/book/luxNightClub/asl/giacomo.asl")] +hermes::myMAS(MAS)[source(AgtName)] : ((MAS = "luxNightClub") & (AgtName = receptionist)) <- !info("I'm at the LuxNightClub! So, party time!"); !enjoyTheParty.

