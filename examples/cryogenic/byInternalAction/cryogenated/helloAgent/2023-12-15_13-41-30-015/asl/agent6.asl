/* Initial beliefs and rules */
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_101]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::clear_source_self([],[])[hide_in_mind_inspector,source(self)].
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
agent6[source(self)].
day[source(self)].
hermes::myMAS("helloAgent")[source(simple_agent)].

/* Initial goals */

/* Plans */
@p__6[source(self),url("file:examples/cryogenic/byInternalAction/hello-agent/asl/agent6.asl")] +!start : day <- .print("Hello world!!!").

