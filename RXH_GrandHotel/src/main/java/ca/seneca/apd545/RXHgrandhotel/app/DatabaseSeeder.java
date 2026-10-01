package ca.seneca.apd545.RXHgrandhotel.app;

import ca.seneca.apd545.RXHgrandhotel.model.AdminUser;
import ca.seneca.apd545.RXHgrandhotel.model.enums.Role;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.repository.AdminUserRepository;
import ca.seneca.apd545.RXHgrandhotel.repository.RoomRepository;
import ca.seneca.apd545.RXHgrandhotel.security.BCrtptHasher;
import ca.seneca.apd545.RXHgrandhotel.service.RoomFactory;

public class DatabaseSeeder {

    public static void seed(AdminUserRepository adminRepo,
                            RoomRepository roomRepo,
                            BCrtptHasher hasher) {

        seedRooms(roomRepo);
        seedAdmins(adminRepo, hasher);
    }

    private static void seedRooms(RoomRepository roomRepo) {
        if (!roomRepo.findAll().isEmpty()) {
            System.out.println("[Seeder] Rooms already exist — skipping.");
            return;
        }
        roomRepo.save(RoomFactory.createRoom(RoomType.SINGLE, "101"));
        roomRepo.save(RoomFactory.createRoom(RoomType.SINGLE, "102"));
        roomRepo.save(RoomFactory.createRoom(RoomType.SINGLE, "103"));
        roomRepo.save(RoomFactory.createRoom(RoomType.SINGLE, "104"));

        roomRepo.save(RoomFactory.createRoom(RoomType.DOUBLE, "201"));
        roomRepo.save(RoomFactory.createRoom(RoomType.DOUBLE, "202"));
        roomRepo.save(RoomFactory.createRoom(RoomType.DOUBLE, "203"));
        roomRepo.save(RoomFactory.createRoom(RoomType.DOUBLE, "204"));
        roomRepo.save(RoomFactory.createRoom(RoomType.SUITE, "301"));
        roomRepo.save(RoomFactory.createRoom(RoomType.SUITE, "302"));

        System.out.println("[Seeder] Rooms created successfully.");
    }


    private static void seedAdmins(AdminUserRepository adminRepo, BCrtptHasher hasher) {

        if (adminRepo.findByUsername("admin").isEmpty()) {
            adminRepo.save(new AdminUser(
                    "admin",
                    hasher.hash("Admin@123"),
                    Role.ADMIN,
                    "Front Desk Admin"
            ));
            System.out.println("[Seeder] Created ADMIN → username: admin / password: Admin@123");
        }


        if (adminRepo.findByUsername("manager").isEmpty()) {
            adminRepo.save(new AdminUser(
                    "manager",
                    hasher.hash("Manager@123"),
                    Role.MANAGER,
                    "Hotel Manager"
            ));
            System.out.println("[Seeder] Created MANAGER → username: manager / password: Manager@123");
        }

        System.out.println("[Seeder] Admin seeding done.");
    }
}