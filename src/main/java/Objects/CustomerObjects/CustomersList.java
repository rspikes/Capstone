package Objects.CustomerObjects;

import dao.CustomersQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * A class that manages a list of customers and provides utility methods for interacting with the list.
 */
public class CustomersList {
    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final ObservableList<Customer> customerInfoByAppointment = FXCollections.observableArrayList();

    /**
     * Adds a new customer to the list of customers.
     *
     * @param newCustomer The customer to be added to the list.
     */
    public static void addCustomer(Customer newCustomer) {
        allCustomers.add(newCustomer);
    }

    /**
     * Looks up a customer by their customer ID and adds them to a list
     *
     * @param customerID The customer ID used to look up the customers information.
     * @return an observable list containing the customers information
     */
    public static ObservableList<Customer> getCustomerInfo(int customerID) throws SQLException {
        CustomersQuery.getCustomers();
        customerInfoByAppointment.clear();
        customerInfoByAppointment.add(lookupCustomer(customerID));

        return customerInfoByAppointment;
    }

    /**
     * Looks up a customer by their unique identifier (customerID) in the list.
     *
     * @param customerID The unique identifier of the customer to look up.
     * @return The customer with the specified customerID if found; otherwise, returns null.
     */
    public static Customer lookupCustomer(int customerID) {
        ObservableList<Customer> currentCustomerList = CustomersList.getAllCustomers();
        for (int i = 0; i < currentCustomerList.size(); i++) {
            Customer customer = currentCustomerList.get(i);
            if (customer.getId() == customerID) {
                return customer;
            }
        }
        return null;
    }

    /**
     * Updates an existing customer in the list.
     *
     * @param index            The index of the customer to update in the list.
     * @param selectedCustomer The updated customer information.
     */
    public static void updateCustomer(int index, Customer selectedCustomer) {
        allCustomers.set(index, selectedCustomer);
    }

    /**
     * Deletes a customer from the list.
     *
     * @param selectedCustomer The customer to be removed from the list.
     */
    public static void deleteCustomer(Customer selectedCustomer) {
        allCustomers.remove(selectedCustomer);
    }

    /**
     * Retrieves the list of all customers.
     *
     * @return An ObservableList containing all the customers in the list.
     */
    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    public static ObservableList<Customer> searchForCustomer(String customerSearch) {
        ObservableList<Customer> newCustomerList = FXCollections.observableArrayList();
        ObservableList<Customer> currentCustomerList = CustomersList.getAllCustomers();
        for (Customer customer : currentCustomerList) {
            if (customer.getName().contains(customerSearch)) {
                newCustomerList.add(customer);
            }
        }
        return newCustomerList;
    }
}
