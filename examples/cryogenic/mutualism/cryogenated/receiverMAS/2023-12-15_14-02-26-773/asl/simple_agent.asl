/* Initial beliefs and rules */
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_11]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::clear_source_self([],[])[hide_in_mind_inspector,source(self)].
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
contextnetServer("192.168.1.121",3273)[source(self)].
day[source(self)].
Hermes::contextNetConfiguration("1","192.168.1.121","3273","788b2b22-baa6-4c61-b1bb-01cff1f5f880","true","NoSecurity")[source(self)].
Hermes::myTrophicLevel("PRODUCER")[source(self)].
Hermes::myMAS("receiverMAS")[source(self)].

/* Initial goals */

/* Plans */
@p__1[source(self),url("file:examples/cryogenic/mutualism/receiver/asl/simple_agent.asl")] +!start : contextnetServer(IP,PORT) <- .hermes.configureContextNetConnection("1",IP,PORT,"788b2b22-baa6-4c61-b1bb-01cff1f5f880"); .hermes.connect("1"); .print("Hello world - cryogenic - mutualism!!!").
@p__2[source(self),url("file:examples/cryogenic/mutualism/receiver/asl/simple_agent.asl")] +!cryogenic <- .print("I must be cryogenated!!!"); .wait(500); .hermes.cryogenic.

