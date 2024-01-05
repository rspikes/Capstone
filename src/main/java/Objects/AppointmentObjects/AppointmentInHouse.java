package Objects.AppointmentObjects;

import Objects.AppointmentObjects.Appointment;

import java.sql.Timestamp;

/**
 * A class representing an in-house appointment, which is a specific type of appointment.
 * An in-house appointment is an appointment that takes place at a physical location.
 */
public class AppointmentInHouse extends Appointment {

    /**
     * Constructs an in-house appointment with the specified attributes.
     *
     * @param a_id         The unique identifier for the appointment.
     * @param title        The title of the appointment.
     * @param description  The description of the appointment.
     * @param location     The location where the appointment takes place.
     * @param contact      The name of the contact associated with the appointment.
     * @param type         The type or category of the appointment.
     * @param start        The start time of the appointment.
     * @param end          The end time of the appointment.
     * @param customerID   The unique identifier of the customer associated with the appointment.
     * @param userID       The unique identifier of the user responsible for the appointment.
     * @param visibleStart A formatted string representing the visible start time.
     * @param visibleEnd   A formatted string representing the visible end time.
     */
    public AppointmentInHouse(int a_id, String title, String description, String location, String contact, String type, Timestamp start, Timestamp end, int customerID, int userID, String visibleStart, String visibleEnd) {
        super(a_id, title, description, location, contact, type, start, end, customerID, userID, visibleStart, visibleEnd);
    }
}
