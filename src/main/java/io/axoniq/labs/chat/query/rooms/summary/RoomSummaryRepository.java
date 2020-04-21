package io.axoniq.labs.chat.query.rooms.summary;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomSummaryRepository extends JpaRepository<RoomSummary, String> {
    RoomSummary findByRoomId(String roomId);
}
