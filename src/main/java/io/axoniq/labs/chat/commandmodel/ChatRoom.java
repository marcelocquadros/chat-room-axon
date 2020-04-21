package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class ChatRoom {

    public ChatRoom(){}

    @AggregateIdentifier
    private String roomId;

    private Set<String> participants;

    @CommandHandler
    public ChatRoom(CreateRoomCommand command){
       apply(new RoomCreatedEvent(command.getRoomId(), command.getName()));
    }

    @CommandHandler
    public void handle(JoinRoomCommand cmd){

        if(!participants.contains(cmd.getParticipant())){
            apply(new ParticipantJoinedRoomEvent(cmd.getRoomId(), cmd.getParticipant()));
        }
    }

    @CommandHandler
    public void handle(LeaveRoomCommand cmd){
        if(participants.contains(cmd.getParticipant())){
            apply(new ParticipantLeftRoomEvent(cmd.getRoomId(), cmd.getParticipant()));
        }
    }


    @CommandHandler
    public void handle(PostMessageCommand cmd){
        if(!participants.contains(cmd.getParticipant())){
            throw new IllegalStateException("Only participants who has joined to the room can post messages");
        }
        apply(new MessagePostedEvent(cmd.getRoomId(), cmd.getParticipant(), cmd.getMessage()));
    }


    @EventSourcingHandler
    public void on(ParticipantLeftRoomEvent event){
        this.participants.remove(event.getParticipant());
    }

    @EventSourcingHandler
    public void on(ParticipantJoinedRoomEvent event){
       this.participants.add(event.getParticipant());
    }

    @EventSourcingHandler
    public void on(RoomCreatedEvent event){
        this.roomId = event.getRoomId();
        this.participants = new HashSet<>();
    }
}
