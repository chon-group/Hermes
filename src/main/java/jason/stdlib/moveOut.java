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

package jason.stdlib;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;
import jason.hermes.OutGoingMessage;
import jason.hermes.bioinspired.BioinspiredData;
import jason.hermes.bioinspired.BioinspiredProcessor;
import jason.hermes.bioinspired.BioinspiredProtocolsEnum;
import jason.hermes.bioinspired.dto.AgentTransferRequestMessageDto;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;

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
            rec = ts.getUserAgArch().getAgName();
        }
        //m.setReceiver(rec);
        //ts.getUserAgArch().sendMsg(m);
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
            BioInspiredUtils.LOGGER.log(Level.SEVERE, "Error: The number of arguments passed was ('"
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
        // verifica o protocolo passado.
        String protocolName = HermesUtils.getParameterInString(args[1]).toUpperCase();
        BioinspiredProtocolsEnum bioInspiredProtocol = BioinspiredProtocolsEnum.getBioInspiredProtocol(protocolName);
        if (bioInspiredProtocol == null) {
            String msgError = "Error: The bioinspired protocol ('" + protocolName + "') does not exists!";
            BioInspiredUtils.LOGGER.log(Level.SEVERE, msgError);
            throw JasonException.createWrongArgument(this, msgError);
        }

        // Verifica se o protocolo é o Mutualismo para permitir a passagem de 4 argumentos.
        if (args.length == getMaxArgs() && !BioinspiredProtocolsEnum.MUTUALISM.equals(bioInspiredProtocol)) {
            String msgError = "Error: The number of arguments passed was ('"
                    + args.length + "') with the protocol ('" + protocolName + "') but only the " +
                    BioinspiredProtocolsEnum.MUTUALISM.name() + " protocol allows to pass " + getMaxArgs() + " args!";
            BioInspiredUtils.LOGGER.log(Level.SEVERE, msgError);
            throw JasonException.createWrongArgument(this, msgError);
        }

        // Verifica se existe um agente com o nome passado.
        if (args.length == getMaxArgs()) {
            String agentName = HermesUtils.getParameterInString(args[2]);
            if (!BioInspiredUtils.verifyAgentExist(agentName)) {
                String msgError = "Error: Does not exists an agent named ('" + agentName + "') to be transfer!";
                BioInspiredUtils.LOGGER.log(Level.SEVERE, msgError);
                throw JasonException.createWrongArgument(this, msgError);
            }
        }
    }



    @Override
    public Object execute(final TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        checkArguments(args);

        String receiver = HermesUtils.getParameterInString(args[0]);
        String protocolName = HermesUtils.getParameterInString(args[1]).toUpperCase();
        BioinspiredProtocolsEnum bioinspiredProtocol = BioinspiredProtocolsEnum.getBioInspiredProtocol(protocolName);
        Hermes hermes = HermesUtils.checkArchClass(ts.getAgArch(), this.getClass().getName());
        String connectionIdentification = hermes.getFirstConnectionAvailable();

        BioInspiredUtils.LOGGER.info("The " + bioinspiredProtocol.name() + " protocol"
                + " starts at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));

        BioinspiredData bioinspiredDataToStartTheTransference = BioinspiredProcessor
                .getBioinspiredDataToStartTheTransference(bioinspiredProtocol, args, connectionIdentification);
        String myIdentification = hermes.getCommunicationMiddleware(
                bioinspiredDataToStartTheTransference.getConnectionIdentifier()).getAgentIdentification();
        bioinspiredDataToStartTheTransference.setSenderIdentification(myIdentification);
        bioinspiredDataToStartTheTransference.setReceiverIdentification(receiver);
        hermes.setBioinspiredData(bioinspiredDataToStartTheTransference);

        CommunicationMiddleware communicationMiddleware = hermes.getCommunicationMiddleware(
                bioinspiredDataToStartTheTransference.getConnectionIdentifier());

        // TODO: Fazer um Mapper.
        AgentTransferRequestMessageDto agentTransferRequestMessageDto = new AgentTransferRequestMessageDto(
                bioinspiredDataToStartTheTransference.getSenderIdentification(),
                bioinspiredDataToStartTheTransference.getNameOfAgentsToBeTransferred(),
                bioinspiredDataToStartTheTransference.isHasHermesAgentTransferred(),
                bioinspiredDataToStartTheTransference.getBioinspiredProtocol());

        BioInspiredUtils.LOGGER.log(Level.INFO, "Sending the agent transfer request.");
        OutGoingMessage.sendMessageBioinspiredMessage(agentTransferRequestMessageDto, communicationMiddleware, receiver);

        return true;
    }

}