package Objects.AppointmentObjects;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * An abstract class representing an appointment.
 */
public abstract class Appointment {

    private int a_id;
    private int customerID;
    private int userID;
    private String title;
    private String description;
    private String location;
    private String contactName;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private String visibleStart;
    private String visibleEnd;

    /**
     * Constructs an appointment with the specified attributes.
     *
     * @param a_id         The unique identifier for the appointment.
     * @param title        The title of the appointment.
     * @param description  The description of the appointment.
     * @param location     The location of the appointment.
     * @param contactName  The name of the contact associated with the appointment.
     * @param type         The type or category of the appointment.
     * @param start        The start time of the appointment.
     * @param end          The end time of the appointment.
     * @param customerID   The unique identifier of the customer associated with the appointment.
     * @param userID       The unique identifier of the user responsible for the appointment.
     * @param visibleStart A formatted string representing the visible start time.
     * @param visibleEnd   A formatted string representing the visible end time.
     */
    public Appointment(int a_id,
                       String title,
                       String description,
                       String location,
                       String contactName,
                       String type,
                       Timestamp start,
                       Timestamp end,
                       int customerID,
                       int userID,
                       String visibleStart,
                       String visibleEnd) {
        this.a_id = a_id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactName = contactName;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.visibleStart = visibleStart;
        this.visibleEnd = visibleEnd;
    }

    public int getA_id() {
        return a_id;
    }

    public void setA_id(int a_id) {
        this.a_id = a_id;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public String getVisibleStart() {
        return visibleStart;
    }

    public void setVisibleStart(String visibleStart) {
        this.visibleStart = visibleStart;
    }

    public String getVisibleEnd() {
        return visibleEnd;
    }

    public void setVisibleEnd(String visibleEnd) {
        this.visibleEnd = visibleEnd;
    }

}
