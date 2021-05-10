package uz.instat.mygridrecylersample.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "icon", indices = [Index(value = ["id"], unique = true)])
data class IconModel(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var rowId: Long,
    var columnId: Long,
    var name: String
)
