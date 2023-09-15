/* Initial beliefs and rules */
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_47]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_29]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
kqml::clear_source_self([],[])[hide_in_mind_inspector,source(self)].
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
myMAS("receiverMAS")[source(simple_agent)].
day[source(self)].
agent1[source(self)].

/* Initial goals */

/* Plans */
@p__2[source(self),url("file:examples/cryogenic/mutualism/receiver/asl/agent1.asl"),url("file:examples/cryogenic/mutualism/sender/asl/agent1.asl")] +!start : day <- .print("Hello world!!!").
@p__3[source(self),url("file:examples/cryogenic/mutualism/receiver/asl/agent1.asl"),url("file:examples/cryogenic/mutualism/sender/asl/agent1.asl")] -myMAS(X)[source(Y)] <- .wait(500); .send(simple_agent,tell,cryogenic).

