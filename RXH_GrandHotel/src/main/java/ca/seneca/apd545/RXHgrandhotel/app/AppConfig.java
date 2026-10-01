package ca.seneca.apd545.RXHgrandhotel.app;

import ca.seneca.apd545.RXHgrandhotel.controller.admin.*;
import ca.seneca.apd545.RXHgrandhotel.controller.feedback.*;
import ca.seneca.apd545.RXHgrandhotel.controller.kiosk.*;
import ca.seneca.apd545.RXHgrandhotel.repository.*;
import ca.seneca.apd545.RXHgrandhotel.security.*;
import ca.seneca.apd545.RXHgrandhotel.service.*;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AppConfig {

    private static AppConfig instance;

    private final EntityManagerFactory emf;
    private final GuestRepository guestRepository;
    private final AdminUserRepository adminUserRepository;
    private final RoomRepository roomRepository;
    private final WaitlistRepository waitlistRepository;
    private final ReservationRepository reservationRepository;
    private final DiscountRepository discountRepository;
    private final LoyaltyRepository loyaltyRepository;
    private final FeedbackRepository feedbackRepository;
    private final PaymentRepository paymentRepository;
    private final ActivityLogRepository activityLogRepository;

    private final PaymentService paymentService;
    private final FeedbackService feedbackService;
    private final BCrtptHasher bcrtptHasher;
    private final AuthService authService;
    private final GuestService guestService;
    private final LoyaltyService loyaltyService;
    private final ReservationService reservationService;
    private final PricingService pricingService;
    private final BookingService bookingService;
    private final WaitlistService waitlistService;
    private final ReportingService reportingService;
    private final DiscountService discountService;

    private AppConfig() {
        this.emf = Persistence.createEntityManagerFactory("RXH_PU");
        LoggerUtil.init();
        this.bcrtptHasher = new BCrtptHasher();
        this.adminUserRepository = new AdminUserRepository(emf);
        this.roomRepository = new RoomRepository(emf);
        this.waitlistRepository = new WaitlistRepository(emf);
        this.reservationRepository = new ReservationRepository(emf);
        this.discountRepository = new DiscountRepository(emf);
        this.loyaltyRepository = new LoyaltyRepository(emf);
        this.guestRepository = new GuestRepository(emf);
        this.feedbackRepository = new FeedbackRepository(emf);
        this.paymentRepository = new PaymentRepository(emf);
        this.activityLogRepository = new ActivityLogRepository(emf);

        DatabaseSeeder.seed(adminUserRepository, roomRepository, bcrtptHasher);

        this.authService = new AuthService(adminUserRepository, bcrtptHasher);
        this.feedbackService = new FeedbackService(feedbackRepository, reservationRepository, guestRepository);
        this.guestService = new GuestService(emf);
        this.loyaltyService = new LoyaltyService(loyaltyRepository, new StandardLoyaltyStrategy());
        this.reservationService = new ReservationService(reservationRepository);

        this.pricingService = new PricingService(new StandardPricingStrategy(1.0, 1.2));

        this.waitlistService = new WaitlistService(
                waitlistRepository,
                reservationService,
                pricingService,
                roomRepository
        );

        this.reportingService = new ReportingService(reservationRepository, roomRepository, activityLogRepository);
        this.discountService = new DiscountService(discountRepository);
        this.bookingService = new BookingService(reservationService, guestService, pricingService);
        this.paymentService = new PaymentService(paymentRepository, reservationRepository);
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public Object createController(Class<?> type) {
        if (type == AdminLoginController.class)
            return new AdminLoginController(authService);
        if (type == AdminDashboardController.class)
            return new AdminDashboardController(reservationService, authService);

        if (type == NewReservationController.class)
            return new NewReservationController(bookingService, roomRepository, reportingService);
        if (type == ReservationEditorController.class)
            return new ReservationEditorController(reservationService, reportingService);
        if (type == CancelReservationController.class)
            return new CancelReservationController(reservationService, reportingService);

        if (type == PaymentController.class)
            return new PaymentController(paymentService, reservationService, loyaltyService, reportingService);
        if (type == DiscountController.class)
            return new DiscountController(authService, reservationService, reportingService);

        if (type == ReportsController.class)
            return new ReportsController(reportingService);
        if (type == ActivityLogController.class)
            return new ActivityLogController(reportingService);
        if (type == SeasonalPromotionController.class)
            return new SeasonalPromotionController(discountService, reportingService);

        if (type == WaitlistController.class)
            return new WaitlistController(waitlistService, reportingService);
        if (type == FeedbackViewerController.class)
            return new FeedbackViewerController(feedbackService);
        if (type == LoyaltyController.class)
            return new LoyaltyController(loyaltyService, reportingService);

        if (type == KioskRulesController.class) return new KioskRulesController();
        if (type == KioskWelcomeController.class) return new KioskWelcomeController();
        if (type == SelectGuestsController.class)
            return new SelectGuestsController(guestService, loyaltyService);
        if (type == KioskRoomSelectionController.class) return new KioskRoomSelectionController();
        if (type == KioskFinalScreenController.class) return new KioskFinalScreenController();
        if (type == KioskConfirmationController.class)
            return new KioskConfirmationController(reservationService, guestService, pricingService);


        if (type == FeedbackFormController.class)
            return new FeedbackFormController();

        return null;
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) emf.close();
    }


    public AuthService getAuthService() { return authService; }
    public ReservationService getReservationService() { return reservationService; }
    public GuestService getGuestService() { return guestService; }
    public BookingService getBookingService() { return bookingService; }
    public WaitlistService getWaitlistService() { return waitlistService; }
    public PricingService getPricingService() { return pricingService; }
    public LoyaltyService getLoyaltyService() { return loyaltyService; }
    public PaymentService getPaymentService() { return paymentService; }
    public FeedbackService getFeedbackService() { return feedbackService; }
    public ReportingService getReportingService() { return reportingService; }
}