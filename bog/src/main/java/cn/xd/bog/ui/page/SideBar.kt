package cn.xd.bog.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bog.entity.ForumInfo
import cn.xd.bog.ui.components.LeftTextRightIcon
import cn.xd.bog.ui.components.LoadingText
import cn.xd.bog.ui.theme.PinkText
import cn.xd.bog.ui.theme.PinkBackgroundVariants

@Composable
fun SideBar(
    forumList: List<ForumInfo>,
    selectedItem: Int,
    selected: (Int) -> Unit,
    fontSize : Int
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(horizontal = 15.dp)
    ) {
        LeftTextRightIcon(fontSize = fontSize)
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ){
            itemsIndexed(forumList){ index, item ->
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = 0.dp,
                    backgroundColor = if (index == selectedItem)
                        PinkBackgroundVariants
                    else
                        MaterialTheme.colors.background,
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                selected(index)
                            },
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        )
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    LoadingText(
                        text = item.name,
                        modifier = Modifier
                            .height(50.dp)
                            .wrapContentHeight()
                            .padding(start = 30.dp),
                        fontSize = fontSize.sp,
                        color = if (index == selectedItem)
                            PinkText
                        else
                            MaterialTheme.colors.onBackground
                    )
                }
            }
        }
    }
}