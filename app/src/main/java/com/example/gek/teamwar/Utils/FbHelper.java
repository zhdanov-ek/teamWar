package com.example.gek.teamwar.Utils;

import com.example.gek.teamwar.Data.Warior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



/**
 * Stored methods for work with FireBase database
 */

public class FbHelper {
    private static final String TAG = "FB_HELPER";
    public static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public static final String CHILD_GROUP_NAME = "group_name";
    public static final String CHILD_WARIORS = "wariors";


    // TODO: 18.04.17 set unique ID for user (email in basis) 
    public static void updateWariorPosition(Warior warior){
        if (Connection.getInstance().getGroupPassword().length() > 0){
            db.child(Connection.getInstance().getGroupPassword())
                    .child(CHILD_WARIORS)
                    .child(warior.getKey())
                    .setValue(warior);
        }
    }


}
