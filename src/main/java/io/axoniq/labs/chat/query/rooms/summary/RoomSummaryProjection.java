package io.axoniq.labs.chat.query.rooms.summary;

import io.axoniq.labs.chat.coreapi.AllRoomsQuery;
import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomSummaryProjection {

    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(RoomSummaryRepository roomSummaryRepository) {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    @EventHandler
    public void on(RoomCreatedEvent evt){
        this.roomSummaryRepository.save(new RoomSummary(evt.getRoomId(), evt.getName()));
    }

    @EventHandler
    public void on(ParticipantJoinedRoomEvent evt){
       this.roomSummaryRepository.findByRoomId(evt.getRoomId()).addParticipant();
    }

    @EventHandler
    public void on(ParticipantLeftRoomEvent evt){
        this.roomSummaryRepository.findByRoomId(evt.getRoomId()).removeParticipant();
    }

    @QueryHandler
    public List<RoomSummary> on(AllRoomsQuery query){
        return this.roomSummaryRepository.findAll();
    }




    // TODO: Create some event handlers that update this model when necessary.

    // TODO: Create the query handler to read data from this model.
}
