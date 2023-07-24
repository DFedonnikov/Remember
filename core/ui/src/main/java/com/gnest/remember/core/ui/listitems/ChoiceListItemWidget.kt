package com.gnest.remember.core.ui.listitems

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gnest.remember.core.designsystem.component.text.AnimatedText
import com.gnest.remember.core.designsystem.component.text.Text2XSRegular
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.RememberTheme
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.ui.R
import com.gnest.remember.core.ui.noRippleClickable

@Composable
fun <T : ChoiceListItem> ChoiceListItemWidget(
    item: T,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (T) -> Unit,
    onChangeClick: (T) -> Unit
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .noRippleClickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconAlpha by animateFloatAsState(targetValue = if (isSelected) 1.0f else 0.0f)
        Icon(
            modifier = Modifier
                .alpha(iconAlpha)
                .padding(start = 8.dp, end = 12.dp)
                .size(24.dp),
            painter = painterResource(id = RememberIcons.CheckOutlined),
            tint = AppColors.VistaBlue,
            contentDescription = "Item select icon"
        )
        AnimatedText(
            text = item.title,
            style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall
        )
        val choicesText = item.choices
        Text2XSRegular(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            text = choicesText,
            color = AppColors.FrenchGray
        )
        Text2XSRegular(
            modifier = Modifier
                .clickable { onChangeClick(item) }
                .padding(end = 8.dp),
            text = TextSource.Resource(R.string.change),
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    }
}

interface ChoiceListItem {
    val title: TextSource
    val choices: TextSource
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun ChoiceListItemWidgetPreview() {
    RememberTheme {
        ChoiceListItemWidget(item = object : ChoiceListItem {
            override val title: TextSource = TextSource.Simple("Custom")
            override val choices: TextSource = TextSource.Simple("Mon, Wed, Fri")
        }, isSelected = false, onItemClick = {}, onChangeClick = {})
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun ChoiceListItemWidgetNotSetPreview() {
    RememberTheme {
        ChoiceListItemWidget(item = object : ChoiceListItem {
            override val title: TextSource = TextSource.Simple("Custom")
            override val choices: TextSource = TextSource.Simple("Not set")
        }, isSelected = false, onItemClick = {}, onChangeClick = {})
    }
}