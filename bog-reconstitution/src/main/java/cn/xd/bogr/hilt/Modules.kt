package cn.xd.bogr.hilt

import android.content.Context
import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ViewModelComponent::class)
@Module
object DataStoreModule{

    @Provides
    fun provide(@ApplicationContext context: Context) = context.getSharedPreferences("status", ComponentActivity.MODE_PRIVATE)

}