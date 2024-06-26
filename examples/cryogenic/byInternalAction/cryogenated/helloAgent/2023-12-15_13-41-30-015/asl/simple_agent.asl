/* Initial beliefs and rules */
kqml::bel_no_source_self(NS,Content,Ans)[hide_in_mind_inspector,source(self)] :- (NS::Content[|LA] & (kqml::clear_source_self(LA,NLA) & ((Content =.. [F,T,_191]) & (NS::Ans =.. [NS,F,T,NLA])))).
kqml::clear_source_self([],[])[hide_in_mind_inspector,source(self)].
kqml::clear_source_self([source(self)|T],NT)[hide_in_mind_inspector,source(self)] :- kqml::clear_source_self(T,NT).
kqml::clear_source_self([A|T],[A|NT])[hide_in_mind_inspector,source(self)] :- ((A \== source(self)) & kqml::clear_source_self(T,NT)).
contextnetServer("192.168.1.121",3273)[source(self)].
hermes::contextNetConfiguration("1","192.168.1.121","3273","788b2b22-baa6-4c61-b1bb-01cff1f5f880","true","NoSecurity")[source(self)].
hermes::myMAS("helloAgent")[source(self)].
hermes::myTrophicLevel("SECONDARY_CONSUMER")[source(self)].

/* Initial goals */
!start[source(self)].

/* Plans */
@p__11[source(self),url("file:examples/cryogenic/byInternalAction/hello-agent/asl/simple_agent.asl")] +!start : (contextnetServer(IP,PORT) & day) <- .hermes.configureContextNetConnection("1",IP,PORT,"788b2b22-baa6-4c61-b1bb-01cff1f5f880"); .hermes.connect("1"); .hermes.setTrophicLevel(2); -day; .print("Hello world - cryogenic - byInternalAction!!!"); .send(agent1,tell,hello); !doCryogenic; !start.
@p__12[source(self),url("file:examples/cryogenic/byInternalAction/hello-agent/asl/simple_agent.asl")] -!start <- .print("was cryogenated - cryogenic - byInternalAction!!!").
@p__13[source(self),url("file:examples/cryogenic/byInternalAction/hello-agent/asl/simple_agent.asl")] +!doCryogenic <- .print("Preparing for cryonics!!!"); .wait(500); .hermes.cryogenic.

