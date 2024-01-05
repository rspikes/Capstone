package Objects.AppointmentObjects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A utility class for managing a list of appointments.
 * This class provides methods to add, lookup, update, and delete appointments, as well as
 * retrieve a list of all appointments, appointments by month, and appointments by week.
 */
public class AppointmentList {
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    /**
     * Adds a new appointment to the list of all appointments.
     *
     * @param newAppointment The appointment to be added.
     */
    public static void addAppointment(Appointment newAppointment) {allAppointments.add(newAppointment);
    }

    /**
     * Looks up an appointment by its unique identifier.
     *
     * @param appointmentID The unique identifier of the appointment to look up.
     * @return The appointment with the specified unique identifier, or null if not found.
     */
    public static Appointment lookupAppointment(int appointmentID) {
        ObservableList<Appointment> currentAppointmentList = AppointmentList.getAllAppointments();
        for (Appointment appointment : currentAppointmentList) {
            if (appointment.getA_id() == appointmentID) {
                return appointment;
            }
        }
        return null;
    }

    public static ObservableList<Appointment> searchForAppointment(String appointmentSearch){
        ObservableList<Appointment> newAppointmentList = FXCollections.observableArrayList();
        ObservableList<Appointment> currentAppointmentList = AppointmentList.getAllAppointments();
        for(Appointment appointment : currentAppointmentList){
            if(appointment.getTitle().contains(appointmentSearch)){
                newAppointmentList.add(appointment);
            } else if (appointment.getType().contains(appointmentSearch)) {
                newAppointmentList.add(appointment);
            }
        }
        return newAppointmentList;
    }

    /**
     * Updates an appointment at the specified index in the list.
     *
     * @param index             The index of the appointment to be updated.
     * @param selectedAppointment The updated appointment.
     */
    public static void updateAppointment(int index, Appointment selectedAppointment) {
        allAppointments.set(index, selectedAppointment);
    }

    /**
     * Deletes the specified appointment from the list of all appointments.
     *
     * @param selectedAppointment The appointment to be deleted.
     */
    public static void deleteAppointment(Appointment selectedAppointment) {
        allAppointments.remove(selectedAppointment);
    }

    /**
     * Retrieves a list of all appointments.
     *
     * @return An observable list containing all appointments.
     */
    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

    /**
     * Resets the list of all appointments by removing all items.
     */
    public static void resetAllAppointments(){allAppointments.clear();}
}
