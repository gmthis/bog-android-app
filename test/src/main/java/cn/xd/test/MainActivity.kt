package cn.xd.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import cn.xd.test.ui.theme.BogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BogTheme {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .graphicsLayer(alpha = 0.99f)
                ){
                    drawRect(
                        color = Color.Yellow,
                        topLeft = Offset(350f, 300f),
                        size = Size(350f, 350f),
                    )
                    drawCircle(
                        color = Color.Blue,
                        radius = 175f,
                        center = Offset(350f, 300f),
                        blendMode = BlendMode.Clear
                    )
                }
            }
        }
    }
}