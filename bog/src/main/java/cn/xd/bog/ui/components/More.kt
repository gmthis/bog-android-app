package cn.xd.bog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bog.R
import cn.xd.bog.ui.theme.PinkBackgroundVariants
import cn.xd.bog.ui.theme.PinkText

@Composable
fun MoreItem(
    text: String,
    icon: Int,
    contentDescription: String,
    fontSize: Int,
){
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(PinkBackgroundVariants)
        ){
            Text(
                text = text,
                fontSize = fontSize.sp,
                color = PinkText,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.padding(start = 10.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(PinkBackgroundVariants)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = contentDescription,
                modifier = Modifier
                    .padding(4.dp)
                    .size(26.dp),
                tint = PinkText
            )
        }
    }
}

@Composable
fun More(
    fontSize: Int
){
    Column(
        modifier = Modifier
            .padding(bottom = 20.dp, end = 10.dp)
    ) {
        MoreItem(
            text = "推荐串",
            icon = R.drawable.list,
            contentDescription = stringResource(id = R.string.recommend),
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.padding(bottom = 15.dp))
        MoreItem(
            text = "发布新串",
            icon = R.drawable.edit,
            contentDescription = stringResource(id = R.string.release),
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.padding(bottom = 15.dp))
        MoreItem(
            text = "搜索",
            icon = R.drawable.search,
            contentDescription = stringResource(id = R.string.search),
            fontSize = fontSize
        )
    }
}