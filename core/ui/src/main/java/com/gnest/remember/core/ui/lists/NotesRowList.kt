package com.gnest.remember.core.ui.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.text.Text2XSMedium
import com.gnest.remember.core.designsystem.component.text.TextBaseMedium
import com.gnest.remember.core.designsystem.component.text.TextSMBold
import com.gnest.remember.core.designsystem.component.text.TextXSRegular
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.R

@Composable
fun <NOTE : ListNote> NotesRowList(
    modifier: Modifier = Modifier,
    listTitle: TextSource,
    notesList: List<NOTE>,
    onItemClick: (NOTE) -> Unit = {}
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextSMBold(text = listTitle)
            Text2XSMedium(
                text = TextSource.Resource(R.string.view_all),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        }
        LazyRow(
            modifier = Modifier.padding(top = 12.dp),
            contentPadding = PaddingValues(start = 6.dp, end = 6.dp)
        ) {
            items(notesList) {
                Card(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .width(180.dp)
                        .height(240.dp)
                        .clickable { onItemClick(it) },
                    colors = CardDefaults.cardColors(containerColor = it.color)
                ) {
                    TextBaseMedium(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(top = 12.dp, bottom = 16.dp),
                        text = it.title
                    )
                    TextXSRegular(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp), text = it.text
                    )
                }
            }
        }
    }
}

interface ListNote {
    val title: TextSource
    val text: TextSource
    val color: Color
}