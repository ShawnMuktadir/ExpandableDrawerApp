package www.fiberathome.com.parkingapp.service.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BookingSensorsRoom.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookingSensorsDao bookingSensorsDao();
}
