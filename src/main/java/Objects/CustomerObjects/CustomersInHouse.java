package Objects.CustomerObjects;

import Objects.CustomerObjects.Customer;

/**
 * A subclass representing customers with in-house divisions.
 * This class inherits common properties and methods from the Customer class.
 */
public class CustomersInHouse extends Customer {

    /**
     * Constructs a customer with the specified attributes, associated with an in-house division.
     *
     * @param id         The unique identifier of the customer.
     * @param name       The name of the customer.
     * @param address    The address of the customer.
     * @param postalCode The postal code of the customer.
     * @param phone      The phone number of the customer.
     * @param divisionID The identifier of the in-house division to which the customer belongs.
     */
    public CustomersInHouse(int id, String name, String address, String postalCode, String phone, int divisionID) {
        super(id, name, address, postalCode, phone, divisionID);
    }
}
