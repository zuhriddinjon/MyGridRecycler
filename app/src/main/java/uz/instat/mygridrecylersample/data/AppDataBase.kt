package uz.instat.mygridrecylersample.data

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.instat.mygridrecylersample.data.dao.IconDao
import uz.instat.mygridrecylersample.data.entity.IconModel

@Database(
    entities = [IconModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun iconDao(): IconDao
}