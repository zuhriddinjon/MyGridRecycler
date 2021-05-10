package uz.instat.mygridrecylersample.data

import android.content.Context
import androidx.room.Room

object DataBaseProvider {
    fun getInstance(context: Context): AppDataBase {
        return Room.databaseBuilder(context, AppDataBase::class.java, "appicon.db")
            .build()
    }
}