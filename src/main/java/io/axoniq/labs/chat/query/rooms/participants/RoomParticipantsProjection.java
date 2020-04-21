package io.axoniq.labs.chat.query.rooms.participants;

import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomParticipantsQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomParticipantsProjection {

    private final RoomParticipantsRepository repository;

    public RoomParticipantsProjection(RoomParticipantsRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(ParticipantJoinedRoomEvent evt){

        RoomParticipant roomParticipant = new RoomParticipant(
                evt.getRoomId(),
                evt.getParticipant()
        );

        this.repository.save(roomParticipant);
    }

    @EventHandler
    public void on(ParticipantLeftRoomEvent evt){
        this.repository.deleteByParticipantAndRoomId(evt.getParticipant(), evt.getRoomId());
    }

    @QueryHandler
    public List<String> handle(RoomParticipantsQuery query){
       return this.repository.findRoomParticipantsByRoomId(query.getRoomId())
               .stream().map(RoomParticipant::getParticipant)
               .collect(Collectors.toList());
    }


    // TODO: Create some event handlers that update this model when necessary.

    // TODO: Create the query handler to read data from this model.
}
