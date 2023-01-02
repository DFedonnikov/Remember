package com.gnest.remember.common.extensions

import androidx.compose.ui.graphics.Color
import com.gnest.remember.common.domain.NoteColor
import com.gnest.remember.core.designsystem.theme.AppColors

infix fun NoteColor.isEqualTo(color: Color): Boolean {
    return when (this) {
        NoteColor.WHITE -> color == AppColors.White
        NoteColor.ROSE -> color == AppColors.Rose
        NoteColor.PICTON_BLUE -> color == AppColors.PictonBlue
        NoteColor.AERO_BLUE -> color == AppColors.AeroBlue
        NoteColor.CAPE_HONEY -> color == AppColors.CapeHoney
        NoteColor.WHITE_LILAC -> color == AppColors.WhiteLilac
        NoteColor.ATHENS_GRAY -> color == AppColors.AthensGray
    }
}

fun NoteColor.asComposeColor(): Color {
    return when (this) {
        NoteColor.WHITE -> AppColors.White
        NoteColor.ROSE -> AppColors.Rose
        NoteColor.PICTON_BLUE -> AppColors.PictonBlue
        NoteColor.AERO_BLUE -> AppColors.AeroBlue
        NoteColor.CAPE_HONEY -> AppColors.CapeHoney
        NoteColor.WHITE_LILAC -> AppColors.WhiteLilac
        NoteColor.ATHENS_GRAY -> AppColors.AthensGray
    }
}

fun Color.asNoteColor(): NoteColor {
    return when (this) {
        AppColors.White -> NoteColor.WHITE
        AppColors.Rose -> NoteColor.ROSE
        AppColors.PictonBlue -> NoteColor.PICTON_BLUE
        AppColors.AeroBlue -> NoteColor.AERO_BLUE
        AppColors.CapeHoney -> NoteColor.CAPE_HONEY
        AppColors.WhiteLilac -> NoteColor.WHITE_LILAC
        AppColors.AthensGray -> NoteColor.ATHENS_GRAY
        else -> error("Unsupported color: $this")
    }
}