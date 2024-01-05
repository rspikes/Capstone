package Objects.ContactObjects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContactList {
    private static final ObservableList<Contact> allContacts = FXCollections.observableArrayList();

    public static void addContact(Contact newContact){allContacts.add(newContact);}
    public static Contact lookupContact(int userID){
        ObservableList<Contact> currentContactList = ContactList.getAllContacts();
        for(Contact contact : currentContactList){
            if(contact.getU_id() == userID){
                return  contact;
            }
        }
        return null;
    }
    public static ObservableList<Contact> getAllContacts(){return allContacts;}
}
