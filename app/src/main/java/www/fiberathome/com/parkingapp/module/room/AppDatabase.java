package www.fiberathome.com.parkingapp.module.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BookingSensorsRoom.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookingSensorsDao bookingSensorsDao();
}