package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Room;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;

public class RoomFactory {


    public static Room createRoom(RoomType type, String roomNumber) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setType(type);

        switch (type) {
            case SINGLE -> {
                room.setBasePrice(120.0);
                room.setMaxAdults(2);
                room.setMaxChildren(1);
            }
            case DOUBLE -> {
                room.setBasePrice(180.0);
                room.setMaxAdults(4);
                room.setMaxChildren(2);
            }
            case SUITE -> {
                room.setBasePrice(300.0);
                room.setMaxAdults(4);
                room.setMaxChildren(3);
            }
        }

        return room;
    }
}
