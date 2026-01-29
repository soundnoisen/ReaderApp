package com.example.feature.book.reader.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.book.reader.R

@Composable
fun FontSizeSetting(
    value: Int,
    onDec: () -> Unit,
    onInc: () -> Unit)
{
    Row(Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.label_font_size),
            fontSize = 16.sp
        )
        Row(
            Modifier.width(140.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDec
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_dec),
                    contentDescription = null,
                )
            }
            Text(
                text = value.toString(),
                fontSize = 16.sp
            )
            IconButton(
                onClick = onInc,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_inc),
                    contentDescription = null,
                )
            }
        }
    }
}