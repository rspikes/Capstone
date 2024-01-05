package Objects.UserObjects;

import Objects.CustomerObjects.Customer;
import Objects.CustomerObjects.CustomersList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UsersList {
    private static final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private static final ObservableList<User> loggedInUser = FXCollections.observableArrayList();

    /**
     * Adds a new User to the list of customers.
     *
     * @param newUser The customer to be added to the list.
     */
    public static void addUser(User newUser){
        allUsers.add(newUser);
    }
    public static void setLoggedInUser(User user){loggedInUser.add(user);}

    /**
     * Looks up a User by their unique identifier (UserID) in the list.
     *
     * @param userID The unique identifier of the customer to look up.
     * @return The customer with the specified customerID if found; otherwise, returns null.
     */
    public static User lookUpUser(int userID){
        ObservableList<User> currentUserList = UsersList.getAllUsers();
        for(int i = 0; i < currentUserList.size(); i++){
            User user = currentUserList.get(i);
            if(user.getUser_ID() == userID){
                return user;
            }
        }
        return null;
    }

    /**
     * Updates an existing customer in the list.
     *
     * @param index           The index of the customer to update in the list.
     * @param selectedUser The updated customer information.
     */
    public static void updateUser(int index, User selectedUser){allUsers.set(index, selectedUser);}

    /**
     * Deletes a customer from the list.
     *
     * @param selectedUser The customer to be removed from the list.
     * @return True if the customer was successfully removed; false if the customer was not found in the list.
     */
    public static boolean deleteUser(User selectedUser){
        allUsers.remove(selectedUser);
        return allUsers.contains(selectedUser);
    }

    /**
     * Retrieves the list of all customers.
     *
     * @return An ObservableList containing all the customers in the list.
     */
    public static ObservableList<User> getAllUsers(){return allUsers;}
    public static ObservableList<User> getLoggedInUser(){return loggedInUser;}
}
