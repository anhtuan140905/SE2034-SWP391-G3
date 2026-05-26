package vn.edu.fpt.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.SettlementStatus;
import vn.edu.fpt.repository.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final VenueRepository venueRepository;
    private final EventCategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final SettlementRepository settlementRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           OrganizerProfileRepository organizerProfileRepository,
                           VenueRepository venueRepository,
                           EventCategoryRepository categoryRepository,
                           EventRepository eventRepository,
                           OrderRepository orderRepository,
                           SettlementRepository settlementRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.organizerProfileRepository = organizerProfileRepository;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
        this.settlementRepository = settlementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ROLE_ADMIN", "System Administrator"));
            roleRepository.save(new Role(null, "ROLE_ORGANIZER", "Event Organizer"));
            roleRepository.save(new Role(null, "ROLE_ATTENDEE", "Event Attendee"));
            roleRepository.save(new Role(null, "ROLE_FINANCE", "Finance / Accounting Staff"));
        }

        Role roleAdmin = roleRepository.findByRoleName("ROLE_ADMIN");
        Role roleOrganizer = roleRepository.findByRoleName("ROLE_ORGANIZER");
        Role roleAttendee = roleRepository.findByRoleName("ROLE_ATTENDEE");
        Role roleFinance = roleRepository.findByRoleName("ROLE_FINANCE");

        // 2. Seed Users & Profiles
        if (userRepository.count() == 0) {
            // Seed Finance User
            User financeUser = new User();
            financeUser.setFirstName("Sarah");
            financeUser.setLastName("Anderson");
            financeUser.setEmail("sarah.anderson@financeportal.com");
            financeUser.setPasswordHash(passwordEncoder.encode("123"));
            financeUser.setPhone("+1 (555) 012-3456");
            financeUser.setGender("Female");
            financeUser.setIsActive(true);
            financeUser.setRole(roleFinance);
            userRepository.save(financeUser);

            // Seed Organizer 1
            User orgUser1 = new User();
            orgUser1.setFirstName("John");
            orgUser1.setLastName("Doe");
            orgUser1.setEmail("organizer1@eventhub.com");
            orgUser1.setPasswordHash(passwordEncoder.encode("123"));
            orgUser1.setPhone("+84987654321");
            orgUser1.setGender("Male");
            orgUser1.setIsActive(true);
            orgUser1.setRole(roleOrganizer);
            User savedOrg1 = userRepository.save(orgUser1);

            OrganizerProfile op1 = new OrganizerProfile();
            op1.setUser(savedOrg1);
            op1.setCompanyName("TechCorp Inc.");
            op1.setTaxCode("TAX-0012938");
            op1.setBankAccount("VIETCOMBANK - 1029384756");
            op1.setStatus("APPROVED");
            organizerProfileRepository.save(op1);

            // Seed Organizer 2
            User orgUser2 = new User();
            orgUser2.setFirstName("Emily");
            orgUser2.setLastName("Smith");
            orgUser2.setEmail("organizer2@eventhub.com");
            orgUser2.setPasswordHash(passwordEncoder.encode("123"));
            orgUser2.setPhone("+84912345678");
            orgUser2.setGender("Female");
            orgUser2.setIsActive(true);
            orgUser2.setRole(roleOrganizer);
            User savedOrg2 = userRepository.save(orgUser2);

            OrganizerProfile op2 = new OrganizerProfile();
            op2.setUser(savedOrg2);
            op2.setCompanyName("EventPro Ltd.");
            op2.setTaxCode("TAX-00998877");
            op2.setBankAccount("BIDV - 9988776655");
            op2.setStatus("APPROVED");
            organizerProfileRepository.save(op2);

            // Seed Attendee
            User attendee = new User();
            attendee.setFirstName("Alex");
            attendee.setLastName("Johnson");
            attendee.setEmail("attendee@gmail.com");
            attendee.setPasswordHash(passwordEncoder.encode("123"));
            attendee.setIsActive(true);
            attendee.setRole(roleAttendee);
            User savedAttendee = userRepository.save(attendee);

            // 3. Seed Venues
            Venue venue = new Venue();
            venue.setVenueName("Hanoi Opera House");
            venue.setCapacity(1250);
            venue.setDescription("Historical classical theater venue.");
            venue.setImageUrl("https://example.com/opera.jpg");
            Venue savedVenue = venueRepository.save(venue);

            // 4. Seed Category
            EventCategory category = new EventCategory();
            category.setCategoryName("Music & Concerts");
            category.setDescription("Live musical events, festivals and concerts.");
            category.setIsActive(true);
            EventCategory savedCategory = categoryRepository.save(category);

            // 5. Seed Ended/Completed Events
            // Event 1 (Settleable)
            Event event1 = new Event();
            event1.setTitle("Tech Summit 2026");
            event1.setDescription("Annual technology breakthrough summit.");
            event1.setOrganizer(savedOrg1);
            event1.setVenue(savedVenue);
            event1.setCategory(savedCategory);
            event1.setStartTime(LocalDateTime.now().minusDays(10));
            event1.setEndTime(LocalDateTime.now().minusDays(8));
            event1.setStatus(EventStatus.ENDED);
            Event savedEvent1 = eventRepository.save(event1);

            // Event 2 (Settleable)
            Event event2 = new Event();
            event2.setTitle("Music Festival Spring");
            event2.setDescription("A weekend full of indie rock music.");
            event2.setOrganizer(savedOrg2);
            event2.setVenue(savedVenue);
            event2.setCategory(savedCategory);
            event2.setStartTime(LocalDateTime.now().minusDays(5));
            event2.setEndTime(LocalDateTime.now().minusDays(3));
            event2.setStatus(EventStatus.ENDED);
            Event savedEvent2 = eventRepository.save(event2);

            // Event 3 (Settleable)
            Event event3 = new Event();
            event3.setTitle("Business Conference 2026");
            event3.setDescription("Startup funding and strategic partnerships.");
            event3.setOrganizer(savedOrg1);
            event3.setVenue(savedVenue);
            event3.setCategory(savedCategory);
            event3.setStartTime(LocalDateTime.now().minusDays(20));
            event3.setEndTime(LocalDateTime.now().minusDays(18));
            event3.setStatus(EventStatus.ENDED);
            Event savedEvent3 = eventRepository.save(event3);

            // Event 4 (Settleable)
            Event event4 = new Event();
            event4.setTitle("Art Exhibition Gala");
            event4.setDescription("Showcasing next-generation Vietnamese digital canvas.");
            event4.setOrganizer(savedOrg2);
            event4.setVenue(savedVenue);
            event4.setCategory(savedCategory);
            event4.setStartTime(LocalDateTime.now().minusDays(30));
            event4.setEndTime(LocalDateTime.now().minusDays(28));
            event4.setStatus(EventStatus.ENDED);
            Event savedEvent4 = eventRepository.save(event4);

            // 6. Seed PAID Orders
            // Orders for Event 1
            Order order1a = new Order(null, savedAttendee, savedEvent1, new BigDecimal("80000.00"), OrderStatus.PAID, new HashSet<>());
            Order order1b = new Order(null, savedAttendee, savedEvent1, new BigDecimal("45000.00"), OrderStatus.PAID, new HashSet<>());
            orderRepository.save(order1a);
            orderRepository.save(order1b);

            // Orders for Event 2
            Order order2a = new Order(null, savedAttendee, savedEvent2, new BigDecimal("150000.00"), OrderStatus.PAID, new HashSet<>());
            Order order2b = new Order(null, savedAttendee, savedEvent2, new BigDecimal("95000.00"), OrderStatus.PAID, new HashSet<>());
            orderRepository.save(order2a);
            orderRepository.save(order2b);

            // Orders for Event 3
            Order order3a = new Order(null, savedAttendee, savedEvent3, new BigDecimal("102000.00"), OrderStatus.PAID, new HashSet<>());
            orderRepository.save(order3a);

            // Orders for Event 4
            Order order4a = new Order(null, savedAttendee, savedEvent4, new BigDecimal("67500.00"), OrderStatus.PAID, new HashSet<>());
            orderRepository.save(order4a);

            // 7. Seed Settlements
            // Settlement 1 (COMPLETED for Event 1)
            Settlement set1 = new Settlement();
            set1.setEvent(savedEvent1);
            set1.setGrossRevenue(new BigDecimal("125000.00"));
            set1.setPlatformFee(new BigDecimal("12500.00"));
            set1.setPayoutAmount(new BigDecimal("112500.00"));
            set1.setStatus(SettlementStatus.COMPLETED);
            set1.setPaidAt(Instant.now().minus(2, ChronoUnit.DAYS));
            set1.setCreatedBy("Sarah Anderson");
            settlementRepository.save(set1);

            // Settlement 2 (COMPLETED for Event 2)
            Settlement set2 = new Settlement();
            set2.setEvent(savedEvent2);
            set2.setGrossRevenue(new BigDecimal("245000.00"));
            set2.setPlatformFee(new BigDecimal("24500.00"));
            set2.setPayoutAmount(new BigDecimal("220500.00"));
            set2.setStatus(SettlementStatus.COMPLETED);
            set2.setPaidAt(Instant.now().minus(1, ChronoUnit.DAYS));
            set2.setCreatedBy("Sarah Anderson");
            settlementRepository.save(set2);

            // Settlement 3 (PENDING for Event 3)
            Settlement set3 = new Settlement();
            set3.setEvent(savedEvent3);
            set3.setGrossRevenue(new BigDecimal("102000.00"));
            set3.setPlatformFee(new BigDecimal("10200.00"));
            set3.setPayoutAmount(new BigDecimal("91800.00"));
            set3.setStatus(SettlementStatus.PENDING);
            set3.setCreatedBy("Sarah Anderson");
            settlementRepository.save(set3);

            // Event 4 remains UNSETTLED (so it shows in "Completed Events" awaiting settlement)
        }
    }
}
