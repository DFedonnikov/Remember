package com.gnest.remember.feature.reminder

import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asTextSource
import com.gnest.remember.core.ui.listitems.ChoiceListItem
import kotlinx.datetime.DayOfWeek

sealed interface RepeatPeriodUi {

    val title: TextSource

    object Once : RepeatPeriodUi {
        override val title: TextSource by lazy { TextSource.Resource(R.string.once) }
    }

    object Daily : RepeatPeriodUi {
        override val title: TextSource by lazy { TextSource.Resource(R.string.daily) }
    }

    object Weekdays : RepeatPeriodUi {
        override val title: TextSource by lazy { TextSource.Resource(R.string.weekdays) }
    }

    object Weekend : RepeatPeriodUi {
        override val title: TextSource by lazy { TextSource.Resource(R.string.weekend) }
    }

    class Custom(
        override val choices: TextSource = TextSource.Resource(R.string.not_set),
        val daysOfWeek: List<DayOfWeek> = emptyList()
    ) : RepeatPeriodUi, ChoiceListItem {

        override val title: TextSource by lazy { TextSource.Resource(R.string.custom) }
    }
}

fun RepeatPeriod.toUiModel(): RepeatPeriodUi = when (this) {
    RepeatPeriod.Once -> RepeatPeriodUi.Once
    RepeatPeriod.Daily -> RepeatPeriodUi.Daily
    RepeatPeriod.Weekdays -> RepeatPeriodUi.Weekdays
    RepeatPeriod.Weekend -> RepeatPeriodUi.Weekend
    is RepeatPeriod.Custom -> RepeatPeriodUi.Custom(choices = days.transformDayName(), daysOfWeek = days)
}

fun RepeatPeriodUi.toDomainModel(): RepeatPeriod = when (this) {
    RepeatPeriodUi.Daily -> RepeatPeriod.Daily
    RepeatPeriodUi.Once -> RepeatPeriod.Once
    RepeatPeriodUi.Weekdays -> RepeatPeriod.Weekdays
    RepeatPeriodUi.Weekend -> RepeatPeriod.Weekend
    is RepeatPeriodUi.Custom -> RepeatPeriod.Custom(daysOfWeek)
}

internal fun List<DayOfWeek>.transformDayName(): TextSource = joinToString { day -> day.displayName() }
    .takeIf { it.isNotEmpty() }
    ?.asTextSource
    ?: TextSource.Resource(R.string.not_set)