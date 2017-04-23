package com.example.gek.teamwar.Utils;

import android.content.Context;
import android.util.Log;

import com.example.gek.teamwar.Data.Mark;
import com.example.gek.teamwar.Data.Warior;
import com.google.firebase.database.DatabaseError;
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
    public static final String CHILD_MARKS = "marks";


    public static void updateWariorPosition(Warior warior, Context ctx){
        Log.d(TAG, "updateWariorPosition: " + Connection.getInstance(ctx).toString());
        if (Connection.getInstance(ctx).getGroupPassword().length() > 0){
            db.child(Connection.getInstance(ctx).getGroupPassword())
                    .child(CHILD_WARIORS)
                    .child(warior.getKey())
                    .setValue(warior);
        }
    }

    public static void updateMark(Mark mark, Context ctx){
        if (Connection.getInstance(ctx).getGroupPassword().length() > 0){
            db.child(Connection.getInstance(ctx).getGroupPassword())
                    .child(CHILD_MARKS)
                    .child(mark.getKey())
                    .setValue(mark);
        }
    }


}
