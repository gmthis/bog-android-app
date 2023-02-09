package cn.xd.bogr.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppStatus @Inject constructor(
    private val store: DataStore<Preferences>
): ViewModel() {

}