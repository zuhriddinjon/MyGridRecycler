package uz.instat.mygridrecylersample.data.dao

import androidx.room.*
import uz.instat.mygridrecylersample.data.entity.IconModel

@Dao
interface IconDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(icon: IconModel): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(icons: List<IconModel>)

    @Query("select * from icon")
    suspend fun getAll(): List<IconModel>

    @Query("delete from icon where columnId=:columnId and rowId=:rowId")
    suspend fun deleteIcon(columnId: Int, rowId: Int)

    @Query("select * from icon where columnId=:columnId")
    suspend fun getIconsByColumnId(columnId: Int): List<IconModel>

    @Query("select * from icon where columnId=:columnId and rowId=:rowId")
    suspend fun getIcon(columnId: Int, rowId: Int): IconModel

    @Query("update icon set columnId=:fromColumn, rowId=:fromRow where columnId=:toColumn and rowId=:toRow")
    suspend fun updateIcon(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int)

    @Update
    suspend fun update(icon: IconModel)
}