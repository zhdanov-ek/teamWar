package com.example.gek.teamwar.Utils;

import com.example.gek.teamwar.Data.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Stored methods for work with FireBase database
 */

public class FbHelper {
    public static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public static final String CHILD_LIST_GROUPS = "list_groups";
    public static final String CHILD_DATA_GROUP = "data_groups";
    public static final String CHILD_GROUP_NAME = "group_name";
    public static final String CHILD_GROUP_DESCRIPTION = "group_description";
    public static final String CHILD_GROUP_PASSWORD = "group_password";
    public static final String CHILD_GROUP_EMAIL_OWNER = "group_email_owner";

    public static void createGroup(Group newGroup){
        db.child(CHILD_LIST_GROUPS).child(newGroup.getName()).setValue(newGroup);
    }


}
