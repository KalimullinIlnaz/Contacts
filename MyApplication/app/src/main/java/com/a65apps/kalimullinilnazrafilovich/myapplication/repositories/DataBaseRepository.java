package com.a65apps.kalimullinilnazrafilovich.myapplication.repositories;

import android.content.Context;

import androidx.room.Room;

import com.a65apps.kalimullinilnazrafilovich.myapplication.database.AppDatabase;
import com.a65apps.kalimullinilnazrafilovich.myapplication.models.Contact;
import com.a65apps.kalimullinilnazrafilovich.myapplication.models.Location;

import java.util.List;
public class DataBaseRepository {
    private ContactDetailsRepository contactDetailsRepository;

    private AppDatabase database;

    public DataBaseRepository(Context context, ContactDetailsRepository contactDetailsRepository){
        this.contactDetailsRepository = contactDetailsRepository;
        database = Room.databaseBuilder(context,
                AppDatabase.class,"user_location").build();
    }

    public Contact getContactFromDB(String id)  {
        Contact contact = contactDetailsRepository.getDetailsContact(id);

        if (database.locationDao().isExists(id) == 1){
            contact.setLatitude(database.locationDao().getById(contact.getId()).getLatitude());
            contact.setLongitude(database.locationDao().getById(contact.getId()).getLongitude());
            contact.setAddress(database.locationDao().getById(contact.getId()).getAddress());
        }else {
            contact.setAddress("");
        }

        return contact;
    }

    public void insertData(String id, String address, double latitude, double longitude){
        Location location = new Location(id);
        location.setAddress(address);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        database.locationDao().insert(location);
    }

    public List<Location> getAllLocation(){
        return database.locationDao().getAll();
    }

    public void closeDatabase(){
        database.close();
    }
}
