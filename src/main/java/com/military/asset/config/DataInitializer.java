package com.military.asset.config;

import com.military.asset.model.*;
import com.military.asset.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final PersonnelUnitRepository personnelUnitRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(BaseRepository baseRepository,
                           EquipmentTypeRepository equipmentTypeRepository,
                           PersonnelUnitRepository personnelUnitRepository,
                           UserRepository userRepository,
                           PurchaseRepository purchaseRepository,
                           TransferRepository transferRepository,
                           AssignmentRepository assignmentRepository,
                           ExpenditureRepository expenditureRepository,
                           PasswordEncoder passwordEncoder) {
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.personnelUnitRepository = personnelUnitRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 0. Clean legacy bases not in the official 5 bases
        java.util.List<String> validBases = java.util.List.of("Base A", "Base B", "Base C", "Base D", "Base E");
        baseRepository.findAll().forEach(b -> {
            if (!validBases.contains(b.getName())) {
                try {
                    purchaseRepository.findAll().stream().filter(p -> p.getBase().getId().equals(b.getId())).forEach(purchaseRepository::delete);
                    assignmentRepository.findAll().stream().filter(a -> a.getBase().getId().equals(b.getId())).forEach(assignmentRepository::delete);
                    expenditureRepository.findAll().stream().filter(e -> e.getBase().getId().equals(b.getId())).forEach(expenditureRepository::delete);
                    transferRepository.findAll().stream().filter(t -> t.getFromBase().getId().equals(b.getId()) || t.getToBase().getId().equals(b.getId())).forEach(transferRepository::delete);
                    userRepository.findAll().stream().filter(u -> u.getBase() != null && u.getBase().getId().equals(b.getId())).forEach(u -> {
                        u.setBase(null);
                        userRepository.save(u);
                    });
                    baseRepository.delete(b);
                } catch (Exception ignored) {}
            }
        });

        // 1. Ensure Bases exist
        Base b1 = getOrCreateBase("Base A", "Nellore");
        Base b2 = getOrCreateBase("Base B", "Kurnool");
        Base b3 = getOrCreateBase("Base C", "Kadapa");
        Base b4 = getOrCreateBase("Base D", "Hyderabad");
        Base b5 = getOrCreateBase("Base E", "Chennai");

        // 2. Ensure Equipment Types exist
        EquipmentType eRifle = getOrCreateEquipmentType("Rifle", EquipmentCategory.WEAPON);
        EquipmentType ePistol = getOrCreateEquipmentType("Pistol", EquipmentCategory.WEAPON);
        EquipmentType eRifleBullets = getOrCreateEquipmentType("Rifle Bullets", EquipmentCategory.AMMUNITION);
        EquipmentType ePistolBullets = getOrCreateEquipmentType("Pistol Bullets", EquipmentCategory.AMMUNITION);
        EquipmentType eJeep = getOrCreateEquipmentType("Jeep", EquipmentCategory.VEHICLE);
        EquipmentType eTruck = getOrCreateEquipmentType("Truck", EquipmentCategory.VEHICLE);
        EquipmentType eRadio = getOrCreateEquipmentType("Radio", EquipmentCategory.GEAR);
        EquipmentType eHelmet = getOrCreateEquipmentType("Helmet", EquipmentCategory.GEAR);

        // 3. Ensure Personnel Units exist
        if (personnelUnitRepository.count() == 0) {
            personnelUnitRepository.save(PersonnelUnit.builder().name("Captain Arjun Sharma").build());
            personnelUnitRepository.save(PersonnelUnit.builder().name("Major Vikram Batra").build());
            personnelUnitRepository.save(PersonnelUnit.builder().name("Lieutenant Rajesh Kumar").build());
        }

        // 4. Seed/reset Demo Users with distinct requested passwords
        saveOrUpdateUser("admin@military.gov", "System Admin", passwordEncoder.encode("Admin@123"), Role.ADMIN, null);
        saveOrUpdateUser("commander1@military.gov", "Base Commander", passwordEncoder.encode("Base@123"), Role.BASE_COMMANDER, b1);
        saveOrUpdateUser("logistics1@military.gov", "Logistics Officer", passwordEncoder.encode("Logistics@gmail"), Role.LOGISTICS_OFFICER, b1);

        // 5. Purchases (12 rows)
        if (purchaseRepository.count() == 0) {
            // Base A
            purchaseRepository.save(Purchase.builder().base(b1).equipmentType(eRifle).quantity(200).date(LocalDate.now().minusDays(50)).build());
            purchaseRepository.save(Purchase.builder().base(b1).equipmentType(eRifleBullets).quantity(500).date(LocalDate.now().minusDays(45)).build());
            purchaseRepository.save(Purchase.builder().base(b1).equipmentType(eJeep).quantity(20).date(LocalDate.now().minusDays(40)).build());
            purchaseRepository.save(Purchase.builder().base(b1).equipmentType(eHelmet).quantity(100).date(LocalDate.now().minusDays(35)).build());

            // Base B
            purchaseRepository.save(Purchase.builder().base(b2).equipmentType(eRifle).quantity(100).date(LocalDate.now().minusDays(50)).build());
            purchaseRepository.save(Purchase.builder().base(b2).equipmentType(ePistol).quantity(50).date(LocalDate.now().minusDays(45)).build());

            // Base C
            purchaseRepository.save(Purchase.builder().base(b3).equipmentType(eTruck).quantity(30).date(LocalDate.now().minusDays(40)).build());
            purchaseRepository.save(Purchase.builder().base(b3).equipmentType(ePistolBullets).quantity(300).date(LocalDate.now().minusDays(35)).build());

            // Base D
            purchaseRepository.save(Purchase.builder().base(b4).equipmentType(eRadio).quantity(50).date(LocalDate.now().minusDays(30)).build());
            purchaseRepository.save(Purchase.builder().base(b4).equipmentType(eHelmet).quantity(150).date(LocalDate.now().minusDays(25)).build());

            // Base E
            purchaseRepository.save(Purchase.builder().base(b5).equipmentType(eRifle).quantity(80).date(LocalDate.now().minusDays(20)).build());
            purchaseRepository.save(Purchase.builder().base(b5).equipmentType(eRifleBullets).quantity(400).date(LocalDate.now().minusDays(15)).build());
        }

        // 6. Transfers (6 rows)
        if (transferRepository.count() == 0) {
            transferRepository.save(Transfer.builder().fromBase(b1).toBase(b2).equipmentType(eRifle).quantity(30).date(LocalDate.now().minusDays(30)).build());
            transferRepository.save(Transfer.builder().fromBase(b1).toBase(b3).equipmentType(eRifleBullets).quantity(100).date(LocalDate.now().minusDays(25)).build());
            transferRepository.save(Transfer.builder().fromBase(b2).toBase(b1).equipmentType(ePistol).quantity(20).date(LocalDate.now().minusDays(20)).build());
            transferRepository.save(Transfer.builder().fromBase(b4).toBase(b1).equipmentType(eRadio).quantity(10).date(LocalDate.now().minusDays(15)).build());
            transferRepository.save(Transfer.builder().fromBase(b3).toBase(b5).equipmentType(eTruck).quantity(5).date(LocalDate.now().minusDays(10)).build());
            transferRepository.save(Transfer.builder().fromBase(b5).toBase(b4).equipmentType(eRifleBullets).quantity(50).date(LocalDate.now().minusDays(5)).build());
        }

        // 7. Assignments (5 rows)
        if (assignmentRepository.count() == 0) {
            assignmentRepository.save(Assignment.builder().base(b1).equipmentType(eRifle).personnelName("Captain Arjun Sharma").quantity(25).date(LocalDate.now().minusDays(12)).build());
            assignmentRepository.save(Assignment.builder().base(b1).equipmentType(eHelmet).personnelName("Lieutenant Rajesh Kumar").quantity(15).date(LocalDate.now().minusDays(10)).build());
            assignmentRepository.save(Assignment.builder().base(b2).equipmentType(ePistol).personnelName("Major Vikram Batra").quantity(10).date(LocalDate.now().minusDays(8)).build());
            assignmentRepository.save(Assignment.builder().base(b3).equipmentType(eTruck).personnelName("Captain Arjun Sharma").quantity(2).date(LocalDate.now().minusDays(6)).build());
            assignmentRepository.save(Assignment.builder().base(b4).equipmentType(eRadio).personnelName("Lieutenant Rajesh Kumar").quantity(5).date(LocalDate.now().minusDays(4)).build());
        }

        // 8. Expenditures (5 rows)
        if (expenditureRepository.count() == 0) {
            expenditureRepository.save(Expenditure.builder().base(b1).equipmentType(eRifleBullets).reason("Target Practice Training").quantity(50).date(LocalDate.now().minusDays(8)).build());
            expenditureRepository.save(Expenditure.builder().base(b1).equipmentType(eHelmet).reason("Damaged during exercise").quantity(5).date(LocalDate.now().minusDays(5)).build());
            expenditureRepository.save(Expenditure.builder().base(b2).equipmentType(eRifleBullets).reason("Border Patrol Duty").quantity(20).date(LocalDate.now().minusDays(4)).build());
            expenditureRepository.save(Expenditure.builder().base(b3).equipmentType(ePistolBullets).reason("Range Qualification").quantity(50).date(LocalDate.now().minusDays(3)).build());
            expenditureRepository.save(Expenditure.builder().base(b5).equipmentType(eRifleBullets).reason("Standard Drills").quantity(30).date(LocalDate.now().minusDays(2)).build());
        }
    }

    private Base getOrCreateBase(String name, String location) {
        return baseRepository.findAll().stream().filter(b -> b.getName().equals(name)).findFirst().orElseGet(() ->
                baseRepository.save(Base.builder().name(name).location(location).build())
        );
    }

    private EquipmentType getOrCreateEquipmentType(String name, EquipmentCategory category) {
        return equipmentTypeRepository.findAll().stream().filter(e -> e.getName().equals(name)).findFirst().orElseGet(() ->
                equipmentTypeRepository.save(EquipmentType.builder().name(name).category(category).build())
        );
    }

    private void saveOrUpdateUser(String email, String name, String passwordHash, Role role, Base base) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User u = existing.get();
            u.setName(name);
            u.setPasswordHash(passwordHash);
            u.setRole(role);
            u.setBase(base);
            userRepository.save(u);
        } else {
            userRepository.save(User.builder()
                    .email(email)
                    .name(name)
                    .passwordHash(passwordHash)
                    .role(role)
                    .base(base)
                    .build());
        }
    }
}

