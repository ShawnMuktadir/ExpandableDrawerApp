package www.fiberathome.com.parkingapp.module.room;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static DatabaseClient mInstance;
    private Context mCtx;
    //our app database object
    private AppDatabase appDatabase;

    public DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //alldata is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "sensors").build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
