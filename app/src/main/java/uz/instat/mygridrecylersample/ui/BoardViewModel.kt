package uz.instat.mygridrecylersample.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.instat.mygridrecylersample.R
import uz.instat.mygridrecylersample.data.DataBaseProvider
import uz.instat.mygridrecylersample.data.entity.IconModel
import uz.instat.mygridrecylersample.util.NetworkStatus

interface IBoardVM {

    val liveIconsStatus: LiveData<NetworkStatus>
    val liveUpdateIconStatus: LiveData<NetworkStatus>
    val liveIcons: LiveData<List<IconModel>>

    fun loadIcons()
    fun updateIcon(fromColumn: Long, fromRow: Long, toColumn: Long, toRow: Long)
}

class BoardViewModel(app: Application) : AndroidViewModel(app), IBoardVM {
    override val liveIconsStatus = MutableLiveData<NetworkStatus>()
    override val liveUpdateIconStatus = MutableLiveData<NetworkStatus>()
    override val liveIcons = MutableLiveData<List<IconModel>>()

    private val iconDao = DataBaseProvider.getInstance(app.applicationContext).iconDao()


    override fun loadIcons() {
        viewModelScope.launch {
            try {
                liveIconsStatus.postValue(NetworkStatus.LOADING)
                var list = iconDao.getAll()
                if (list.isNullOrEmpty()) {
                    val mItemArray = arrayListOf<IconModel>()
                    val addItems = 15
                    var id = 1L
                    for (i in 0 until 5) {
                        val columnId: Long = i.toLong()
                        for (j in 0 until addItems) {
                            val rowId: Long = j.toLong()
                            mItemArray.add(
                                IconModel(
                                    id = id,
                                    rowId = rowId,
                                    columnId = columnId,
                                    name = "Item $columnId - $rowId"
                                )
                            )
                            id++
                        }
                    }
                    iconDao.saveAll(mItemArray)
                    list = iconDao.getAll()
                    liveIcons.postValue(list)
                } else
                    liveIcons.postValue(list)
                liveIconsStatus.postValue(NetworkStatus.SUCCESS)
            } catch (e: Exception) {
                liveIconsStatus.postValue(NetworkStatus.ERROR(R.string.error_load))
            }
        }
    }

    override fun updateIcon(fromColumn: Long, fromRow: Long, toColumn: Long, toRow: Long) {
        viewModelScope.launch {
            try {
                liveUpdateIconStatus.postValue(NetworkStatus.LOADING)
                val icon = iconDao.getIcon(fromColumn, fromRow)
                iconDao.deleteIcon(fromColumn, fromRow)
                Log.d("TAGROW", "updateIcon: $fromRow $toRow")

                if (toColumn == fromColumn) {
                    if (toRow > fromRow) {
                        (fromRow until toRow).forEach { row ->
                            iconDao.updateIcon(fromColumn, row + 1, toColumn, row)
                        }
                    } else if (toRow < fromRow) {
                        (fromRow - 1 downTo toRow).forEach { row ->
                            iconDao.updateIcon(fromColumn, row, toColumn, row + 1)
                        }
                    }
                } else {
                    (fromRow until 14).forEach { row ->
                        iconDao.updateIcon(fromColumn, row + 1, fromColumn, row)
                    }
                    (14 downTo toRow).forEach { row ->
                        iconDao.updateIcon(toColumn, row, toColumn, row + 1)
                    }
                }

                iconDao.save(IconModel(icon.id, toRow, toColumn, icon.name))
                liveUpdateIconStatus.postValue(NetworkStatus.SUCCESS)
            } catch (e: Exception) {
                liveUpdateIconStatus.postValue(NetworkStatus.ERROR(R.string.error_load))
            }
        }
    }

}