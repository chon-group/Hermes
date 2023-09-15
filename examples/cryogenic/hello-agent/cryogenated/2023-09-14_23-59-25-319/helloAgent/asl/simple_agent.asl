/* Initial beliefs and rules */
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_191]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::clear_source_self([],[])[hide_in_mind_inspector,source(self)].
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
contextNetConfiguration("1","192.168.0.104","3273","788b2b22-baa6-4c61-b1bb-01cff1f5f880","true","NoSecurity")[source(self)].
myMAS("helloAgent")[source(self)].
myDominanceDegree("DOMINANT")[source(self)].

/* Initial goals */

/* Plans */
@p__11[source(self),url("file:examples/cryogenic/hello-agent/asl/simple_agent.asl")] +!start : day <- .configureContextNetConnection("1","192.168.0.104",3273,"788b2b22-baa6-4c61-b1bb-01cff1f5f880"); .connect("1"); .dominanceDegree(2); -day; .print("Hello world!!!"); .send(agent1,tell,hello); .print("Preparing for cryonics!!!"); .wait(200); .cryogenic.
@p__12[source(self),url("file:examples/cryogenic/hello-agent/asl/simple_agent.asl")] -!start <- .print("was cryogenated!!!").

