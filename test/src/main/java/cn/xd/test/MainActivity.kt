package cn.xd.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import cn.xd.test.ui.theme.BogTheme
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BogTheme {
                var isTransformed by remember {
                    mutableStateOf(false)
                }
                val function = rememberContentWithOrbitalScope {
                    Image(
                        painter = painterResource(id = R.drawable.loading),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .animateSharedElementTransition(this)
                    )
                }
                Orbital(modifier = Modifier.clickable {
                    isTransformed = !isTransformed
                }) {
                    if (isTransformed){
                        Text(text = "详情")
                    }else{
                        function()
                    }
                }
            }
        }
    }
}
