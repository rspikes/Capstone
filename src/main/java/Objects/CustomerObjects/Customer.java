package Objects.CustomerObjects;

/**
 * An abstract class representing a customer.
 * This class encapsulates common properties and methods for customer entities.
 */
public abstract class Customer {

    private int id;
    private String name;
    private String address;

    private String postalCode;
    private String phone;
    private int divisionID;

    /**
     * Constructs a customer with the specified attributes.
     *
     * @param id         The unique identifier of the customer.
     * @param name       The name of the customer.
     * @param address    The address of the customer.
     * @param postalCode The postal code of the customer.
     * @param phone      The phone number of the customer.
     * @param divisionID The identifier of the division to which the customer belongs.
     */
    public Customer(int id, String name, String address, String postalCode, String phone, int divisionID){
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int division) {
        this.divisionID = division;
    }

}
