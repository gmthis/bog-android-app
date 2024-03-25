package cn.xd.bogr.ui.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus

@Composable
fun SnackBar(data: SnackbarData) {
    val viewModel = rememberViewModel<AppStatus>()
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Text(
            text = data.visuals.message,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            fontSize = viewModel.fontSize.sp
        )
    }
}