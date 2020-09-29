package www.fiberathome.com.parkingapp.module.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookingSensorsDao {

    @Query("SELECT * FROM BookingSensorsRoom")
    List<BookingSensorsRoom> getAll();

    @Insert
    void insert(BookingSensorsRoom recipe);
}
