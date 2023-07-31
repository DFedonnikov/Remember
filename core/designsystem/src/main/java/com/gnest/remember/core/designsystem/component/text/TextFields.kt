package com.gnest.remember.core.designsystem.component.text

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTextField(
    modifier: Modifier = Modifier,
    value: TextSource,
    placeholderText: TextSource = TextSource.Simple(""),
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier,
        value = value.asString,
        textStyle = MaterialTheme.typography.headlineLarge,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            placeholderColor = AppColors.FrenchGray
        ),
        placeholder = {
            Text(
                text = placeholderText.asString,
                color = AppColors.FrenchGray,
                style = MaterialTheme.typography.headlineLarge
            )
        },
        onValueChange = onValueChange
    )
}

@Preview(showBackground = true)
@Composable
private fun TitleTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    TitleTextField(value = TextSource.Simple(value), onValueChange = { value = it  })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeAreaTextField(
    modifier: Modifier,
    value: TextSource,
    placeholderText: TextSource = TextSource.Simple(""),
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier,
        value = value.asString,
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.textFieldColors(
            textColor = AppColors.Topaz,
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            placeholderColor = AppColors.FrenchGray
        ),
        placeholder = {
            Text(
                text = placeholderText.asString,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        onValueChange = onValueChange
    )
}