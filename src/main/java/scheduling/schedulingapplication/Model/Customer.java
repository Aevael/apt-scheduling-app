/** @author Alexandre Do */
package scheduling.schedulingapplication.Model;

/** This class creates a Customer object. */
public class Customer implements Comparable<Customer> {

    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionID;

    /** This method is the constructor for a Customer object.
     * The method creates a Customer object based on arguments.
     * @param customerID The customer's ID.
     * @param customerName The customer's name.
     * @param address The customer's address.
     * @param postalCode The customer's postal code.
     * @param phone The customer's phone number.
     * @param divisionID The ID of the division in which the customer resides.
     */
    public Customer(int customerID, String customerName, String address, String postalCode, String phone, int divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;
    }

    /** This method returns the customer's ID.
     * @return Returns the customer's ID.
     */
    public int getCustomerID() {
        return this.customerID;
    }

    /** This method returns the customer's name.
     * @return Returns the customer's name.
     */
    public String getCustomerName() {
        return this.customerName;
    }

    /** This method returns the customer's address.
     * @return Returns the customer's address.
     */
    public String getAddress() {
        return this.address;
    }

    /** This method returns the customer's postal code.
     * @return Returns the customer's postal code.
     */
    public String getPostalCode() {
        return this.postalCode;
    }

    /** This method returns the customer's phone number.
     * @return Returns the customer's phone number.
     */
    public String getPhone() {
        return this.phone;
    }

    /** This method returns the ID of the division in which the customer lives.
     * @return Returns the ID of the division in which the customer lives.
     */
    public int getDivisionID() {
        return this.divisionID;
    }

    /** This method updates the customer's ID.
     * @param customerID The ID to set.
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /** This method updates the customer's name.
     * @param customerName The name to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /** This method updates the customer's address.
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** This method updates the customer's postal code.
     * @param postalCode The postal code to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /** This method updates the customer's phone number.
     * @param phone The phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** This method updates the ID of the division in which the customer lives.
     * @param divisionID The division ID to set.
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /** This method compares a customer to another customer based on ID.
     * This method enables comparison between customers by comparing their IDs using Integer's compare method.
     * @param c The customer to be compared.
     * @return Returns 1 if calling customer's ID is greater, returns -1 if calling customer's ID is lesser, returns 0 if customer IDs are the same.
     */
    @Override
    public int compareTo(Customer c) {
        int currentId = this.getCustomerID();
        int otherId = c.getCustomerID();
        return Integer.compare(currentId, otherId);
    }
}
