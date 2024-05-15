//----------------------------------------------------------------------------
// Copyright (C) 2003  Rafael H. Bordini, Jomi F. Hubner, et al.
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// 
// To contact the authors:
// http://www.inf.ufrgs.br/~bordini
// http://www.das.ufsc.br/~jomi
//
//----------------------------------------------------------------------------

package group.chon.agent.hermes.jasonStdLib;

import group.chon.agent.hermes.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;
import group.chon.agent.hermes.core.OutGoingMessage;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredArgsHandle;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredData;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.mapper.BioinspiredDataMapper;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.utils.ArgsUtils;
import group.chon.agent.hermes.core.utils.BioInspiredUtils;
import group.chon.agent.hermes.core.utils.HermesUtils;
import jason.stdlib.broadcast;
import jason.stdlib.my_name;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/**
 * <p>
 * Internal action: <b><code>.send</code></b>.
 *
 * <p>
 * Description: sends a message to an agent.
 *
 * <p>
 * Parameters:
 * <ul>
 *
 * <li>+ receiver (atom, string, or list): the receiver of the message. It is
 * the unique name of the agent that will receive the message (or list of
 * names).<br/>
 *
 * <li>+ ilf (atom): the illocutionary force of the message (tell, achieve,
 * ...).<br/>
 *
 * <li>+ message (literal): the content of the message.<br/>
 *
 * <li><i>+ answer</i> (any term [optional]): the answer of an ask message (for
 * performatives askOne, askAll, and askHow).<br/>
 *
 * <li><i>+ timeout</i> (number [optional]): timeout (in milliseconds) when
 * waiting for an ask answer.<br/>
 *
 * </ul>
 *
 * <p>
 * Messages with an <b>ask</b> illocutionary force can optionally have arguments
 * 3 and 4. In case they are given, <code>.send</code> suspends the intention
 * until an answer is received and unified with <code>arg[3]</code>, or the
 * message request times out as specified by <code>arg[4]</code>. Otherwise, the
 * intention is not suspended and the answer (which is a tell message) produces
 * a belief addition event as usual.
 *
 * <p>
 * Examples (suppose that agent <code>jomi</code> is sending the messages):
 * <ul>
 *
 * <li> <code>.send(rafael,tell,value(10))</code>: sends <code>value(10)</code>
 * to the agent named <code>rafael</code>. The literal
 * <code>value(10)[source(jomi)]</code> will be added as a belief in
 * <code>rafael</code>'s belief base.</li>
 *
 * <li> <code>.send(rafael,achieve,go(10,30)</code>: sends <code>go(10,30)</code>
 * to the agent named <code>rafael</code>. When <code>rafael</code> receives
 * this message, an event <code>&lt;+!go(10,30)[source(jomi)],T&gt;</code> will
 * be added in <code>rafael</code>'s event queue.</li>
 *
 * <li> <code>.send(rafael,askOne,value(beer,X))</code>: sends
 * <code>value(beer,X)</code> to the agent named rafael. This askOne is an
 * asynchronous ask since it does not suspend jomi's intention. If rafael has,
 * for instance, the literal <code>value(beer,2)</code> in its belief base, this
 * belief is automatically sent to jomi. Otherwise an event like
 * <code>+?value(beer,X)[source(self)]</code> is generated in rafael's side and
 * the result of this query is then sent to jomi. In the jomi's side, the
 * rafael's answer is added in the jomi's belief base and an event like
 * <code>+value(beer,10)[source(rafael)]</code> is generated.</li>
 *
 * <li> <code>.send(rafael,askOne,value(beer,X),A)</code>: sends
 * <code>value(beer,X)</code> to the agent named <code>rafael</code>. This
 * askOne is a synchronous askOne, it suspends <code>jomi</code>'s intention
 * until <code>rafael</code>'s answer is received. The answer (something like
 * <code>value(beer,10)</code>) unifies with <code>A</code>.</li>
 *
 * <li> <code>.send(rafael,askOne,value(beer,X),A,2000)</code>: as in the
 * previous example, but agent <code>jomi</code> waits for 2 seconds. If no
 * message is received by then, <code>A</code> unifies with <code>timeout</code>
 * .</li>
 *
 * </ul>
 *
 * @see broadcast
 * @see my_name
 */
@SuppressWarnings("serial")
public class moveOut extends DefaultInternalAction {

    private boolean lastSendWasSynAsk = false;

    @SuppressWarnings("unused")
    private void delegateSendToArch(Term to, TransitionSystem ts, Message m) throws Exception {
        if (!to.isAtom() && !to.isString()) {
            throw new JasonException("The TO parameter ('" + to + "') of the internal action 'moveOut' is not an atom!");
        }

        String rec = null;
        if (to.isString()) {
            rec = ((StringTerm) to).getString();
        } else {
            rec = to.toString();
        }
        if (rec.equals("self")) {
            rec = ts.getAgArch().getAgName();
        }
        //m.setReceiver(rec);
        //ts.getAgArch().sendMsg(m);
    }

    @Override
    public boolean suspendIntention() {
        return lastSendWasSynAsk;
    }

    @Override
    public boolean canBeUsedInContext() {
        return false;
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    protected void checkArguments(Term[] args) throws JasonException {
        // Verifica a quantidade de argumentos.
        if (args.length < getMinArgs() || args.length > getMaxArgs()) {
            BioInspiredUtils.log(Level.SEVERE, "Error: The number of arguments passed was ('"
                    + args.length + "') and must be between "+ getMinArgs() + " and " + getMaxArgs() + "!");
            throw JasonException.createWrongArgumentNb(this);
        }

        if (!args[0].isAtom() && !args[0].isList() && !args[0].isString()) {
            throw JasonException.createWrongArgument(this,
                    "TO parameter ('" + args[0] + "') must be an atom, a string or a list of receivers!");
        }

        if (!args[1].isAtom()) {
            throw JasonException.createWrongArgument(this,
                    "The protocol name parameter ('" + args[1] + "') must be an atom!");
        }
    }

    private void checkArguments(Term[] args, TransitionSystem ts) throws JasonException {
        // verifica a quantidade de parametros.
        this.checkArguments(args);

        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        // verifica o protocolo passado.
        String protocolName = ArgsUtils.getInString(args[1]).toUpperCase();
        BioinspiredProtocolsEnum bioInspiredProtocol = BioinspiredProtocolsEnum.getBioInspiredProtocol(protocolName);
        if (bioInspiredProtocol == null) {
            String msgError = "Error: The bioinspired protocol ('" + protocolName + "') does not exists!";
            BioInspiredUtils.log(Level.SEVERE, msgError);
            throw JasonException.createWrongArgument(this, msgError);
        }

        // Verifica se o protocolo é o Mutualismo para permitir a passagem de 4 argumentos.
        if (args.length > getMinArgs() && !BioinspiredProtocolsEnum.MUTUALISM.equals(bioInspiredProtocol)
                && !BioinspiredProtocolsEnum.CLONING.equals(bioInspiredProtocol)) {
            String msgError = "Error: The number of arguments passed was ('"
                    + args.length + "') with the protocol ('" + protocolName + "') but only the " +
                    BioinspiredProtocolsEnum.MUTUALISM.name() + " and " + BioinspiredProtocolsEnum.CLONING.name()
                    + " protocols allow to pass " + args.length + " or more args!";
            BioInspiredUtils.log(Level.SEVERE, msgError);
            throw JasonException.createWrongArgument(this, msgError);
        }

        if (args.length == getMinArgs() + 1) {
            if (args[2].isList()) {
                ListTerm thirdArgList = ArgsUtils.getInListTerm(args[2], this);
                HermesUtils.verifyAgentNameParameterList(args[2], thirdArgList, this);
                if (HermesUtils.getAgentNamesInList(thirdArgList).containsAll(BioInspiredUtils.getAllHermesAgentsName())
                        && BioinspiredProtocolsEnum.MUTUALISM.equals(bioInspiredProtocol)) {
                    String msgError = "Error: The '" + BioinspiredProtocolsEnum.MUTUALISM.name() + "' protocol does " +
                            "not allow sending all Hermes agents. At least one Hermes agent must stay in the Origin MAS!";
                    BioInspiredUtils.log(Level.SEVERE, msgError);
                    throw JasonException.createWrongArgument(this, msgError);
                }
            } else {
                String thirdArg = ArgsUtils.getInString(args[2]);
                if (!HermesUtils.verifyAgentExist(thirdArg) && !HermesUtils.verifyConnectionIdentifier(thirdArg, hermes)) {
                    String msgError = "Error: The third argument ('" + thirdArg + "') must be the name of an existing " +
                            "agent in the MAS or an connection identifier.";
                    BioInspiredUtils.log(Level.SEVERE, msgError);
                    throw JasonException.createWrongArgument(this, msgError);
                }
            }
        }

        // Verifica se existe um agente com o nome passado.
        if (args.length == getMaxArgs()) {
            if (args[2].isList()) {
                ListTerm thirdArgList = ArgsUtils.getInListTerm(args[2], this);
                HermesUtils.verifyAgentNameParameterList(args[2], thirdArgList, this);
                if (HermesUtils.getAgentNamesInList(thirdArgList).containsAll(BioInspiredUtils.getAllHermesAgentsName())
                        && BioinspiredProtocolsEnum.MUTUALISM.equals(bioInspiredProtocol)) {
                    String msgError = "Error: The '" + BioinspiredProtocolsEnum.MUTUALISM.name() + "' protocol does " +
                            "not allow sending all Hermes agents. At least one Hermes agent must stay in the Origin MAS!";
                    BioInspiredUtils.log(Level.SEVERE, msgError);
                    throw JasonException.createWrongArgument(this, msgError);
                }
            } else {
                String agentName = ArgsUtils.getInString(args[2]);
                if (!HermesUtils.verifyAgentExist(agentName)) {
                    String msgError = "Error: Does not exists an agent named ('" + agentName + "') to be transfer!";
                    BioInspiredUtils.log(Level.SEVERE, msgError);
                    throw JasonException.createWrongArgument(this, msgError);
                }
            }
            String connectionIdentifier = ArgsUtils.getInString(args[3]);
            if (!HermesUtils.verifyConnectionIdentifier(connectionIdentifier, hermes)) {
                String msgError = "Error: Does not exists an connection identifier ('" + connectionIdentifier + "') in the Hermes connections configured!";
                BioInspiredUtils.log(Level.SEVERE, msgError);
                throw JasonException.createWrongArgument(this, msgError);
            }
        }
        
    }


    @Override
    public Object execute(final TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        checkArguments(args, ts);

        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());

        BioinspiredData bioinspiredData = BioinspiredArgsHandle.getBioinspiredDataByArgs(args, hermes);
        hermes.setBioinspiredData(bioinspiredData);

        BioInspiredUtils.log(Level.INFO,"The " + bioinspiredData.getBioinspiredProtocol().name() + " protocol"
                + " starts at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));

        CommunicationMiddleware communicationMiddleware = hermes.getCommunicationMiddleware(
                bioinspiredData.getConnectionIdentifier());

        AgentTransferRequestMessageDto agentTransferRequestMessageDto = BioinspiredDataMapper
                .mapperToAgentTransferRequestMessageDto(bioinspiredData);

        BioInspiredUtils.log(Level.INFO, "Sending the agent transfer request.");
        if (communicationMiddleware.isConnected()) {
            OutGoingMessage.sendMessageBioinspiredMessage(agentTransferRequestMessageDto, communicationMiddleware,
                    bioinspiredData.getReceiverIdentification());
            return true;
        } else {
            String warningMessage = "The moveOut was executed with the connection identifier ('"
                    + communicationMiddleware.getConfiguration().getConnectionIdentifier() + "'), but this connection is not online.";
            BioInspiredUtils.log(Level.WARNING, warningMessage);
            return false;
        }
    }

}