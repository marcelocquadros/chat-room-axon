package io.axoniq.labs.chat.query.rooms.messages;

import io.axoniq.labs.chat.coreapi.MessagePostedEvent;
import io.axoniq.labs.chat.coreapi.RoomMessagesQuery;
import io.axoniq.labs.chat.query.rooms.summary.RoomSummary;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMessageProjection {

    private final ChatMessageRepository repository;
    private final QueryUpdateEmitter updateEmitter;

    public ChatMessageProjection(ChatMessageRepository repository, QueryUpdateEmitter updateEmitter) {
        this.repository = repository;
        this.updateEmitter = updateEmitter;
    }

    @EventHandler
    public void on(MessagePostedEvent evt, @Timestamp Instant timestamp){
        ChatMessage chatMessage = new ChatMessage(evt.getParticipant(),
                                              evt.getRoomId(),
                                              evt.getMessage(),
                                              timestamp.toEpochMilli());
        this.repository.save(chatMessage);

        updateEmitter.emit(
                RoomMessagesQuery.class,
                query -> query.getRoomId().equals(evt.getRoomId()),
                chatMessage);
    }


    @QueryHandler
    public List<ChatMessage> handle(RoomMessagesQuery query){
        return this.repository.findAllByRoomIdOrderByTimestamp(query.getRoomId())
                .stream()
                .collect(Collectors.toList());
    }


    // TODO: Create some event handlers that update this model when necessary.

    // TODO: Create the query handler to read data from this model.

    // TODO: Emit updates when new message arrive to notify subscription query by modifying the event handler.
}
