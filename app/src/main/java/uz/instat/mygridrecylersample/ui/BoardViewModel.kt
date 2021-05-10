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
    val liveIconsByColumnStatus: LiveData<NetworkStatus>
    val liveUpdateIconStatus: LiveData<NetworkStatus>
    val liveIcons: LiveData<List<IconModel>>
    val liveIconsByColumn: LiveData<List<IconModel>>

    fun loadIcons()

    fun loadIconsByColumn(columnId: Int)

    fun updateIcon(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int)
    fun update(iconModel: IconModel)
}

class BoardViewModel(app: Application) : AndroidViewModel(app), IBoardVM {
    override val liveIconsStatus = MutableLiveData<NetworkStatus>()
    override val liveIconsByColumnStatus = MutableLiveData<NetworkStatus>()
    override val liveUpdateIconStatus = MutableLiveData<NetworkStatus>()
    override val liveIcons = MutableLiveData<List<IconModel>>()
    override val liveIconsByColumn = MutableLiveData<List<IconModel>>()

    private val iconDao = DataBaseProvider.getInstance(app.applicationContext).iconDao()


    override fun loadIcons() {
        viewModelScope.launch {
            try {
                liveIconsStatus.postValue(NetworkStatus.LOADING)
                val list = iconDao.getAll()
                Log.d("TAG", "loadIcon1: ${liveIcons.value.toString()}")
                if (list.isNullOrEmpty()) {
                    Log.d("TAG", "loadIcon2: ${liveIcons.value.toString()}")
                    val mItemArray = arrayListOf<IconModel>()
                    val addItems = 15
                    for (i in 0 until 5) {
                        for (j in 0 until addItems) {
                            val rowId: Long = j.toLong()
                            val columnId: Long = i.toLong()
                            mItemArray.add(
                                IconModel(
                                    rowId = rowId,
                                    columnId = columnId,
                                    name = "Item $rowId"
                                )
                            )
                        }
                    }
                    iconDao.saveAll(mItemArray)
                    iconDao.getAll()
                    Log.d("TAG", "loadIcon3: ${iconDao.getAll()}")
                    liveIcons.postValue(mItemArray)
                } else
                    liveIcons.postValue(list)
                liveIconsStatus.postValue(NetworkStatus.SUCCESS)
            } catch (e: Exception) {
                Log.d("TAG", "loadIcon: ${e.message}")
                liveIconsStatus.postValue(NetworkStatus.ERROR(R.string.error_load))
            }
        }
    }

    override fun loadIconsByColumn(columnId: Int) {
        viewModelScope.launch {
            try {
                liveIconsByColumnStatus.postValue(NetworkStatus.LOADING)
                val list = iconDao.getIconsByColumnId(columnId)
                liveIconsByColumn.postValue(list)
                liveIconsByColumnStatus.postValue(NetworkStatus.SUCCESS)
            } catch (e: Exception) {
                liveIconsByColumnStatus.postValue(NetworkStatus.ERROR(R.string.error_load))
            }
        }
    }

    override fun updateIcon(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
        viewModelScope.launch {
            try {
                liveUpdateIconStatus.postValue(NetworkStatus.LOADING)
                iconDao.updateIcon(fromColumn, fromRow, toColumn, toRow)
                liveUpdateIconStatus.postValue(NetworkStatus.SUCCESS)
            } catch (e: Exception) {
                liveUpdateIconStatus.postValue(NetworkStatus.ERROR(R.string.error_load))
            }
        }
    }

    override fun update(iconModel: IconModel) {
        viewModelScope.launch {
            try {
                iconDao.update(iconModel)
            } catch (e: Exception) {
            }
        }
    }
}