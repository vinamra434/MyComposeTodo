package com.univ.mytodo.ui.createoredit

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.univ.mytodo.R
import com.univ.mytodo.ui.components.MainScaffold
import com.univ.mytodo.ui.theme.MyTodoTheme
import com.univ.mytodo.util.Constants
import com.univ.mytodo.util.Constants.INTENT_KEY_TODO
import com.univ.mytodo.util.Constants.MAX_LENGTH_2
import com.univ.mytodo.util.Constants.MAX_LENGTH_4
import com.univ.mytodo.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTodoActivity : ComponentActivity() {

    private val viewModel: CreateTodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todoId = intent.getIntExtra(INTENT_KEY_TODO, -1)

        setContent {
            MyTodoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CreateTodoScreen(todoId, viewModel = viewModel)
                }
            }

        }
    }
}


@Composable
fun CreateTodoScreen(todoId: Int, viewModel: CreateTodoViewModel) {

    val context = LocalContext.current as Activity

    if (isEditMode(todoId)) {
        viewModel.loadTodoData(todoId)
    }

    val title by viewModel.titleField.observeAsState("")
    val desc by viewModel.descField.observeAsState("")
    val dueDay by viewModel.dueDayField.observeAsState("")
    val dueMonth by viewModel.dueMonthField.observeAsState("")
    val dueYear by viewModel.dueYearField.observeAsState("")
    val dueHour by viewModel.dueHourField.observeAsState("")
    val dueMinute by viewModel.dueMinuteField.observeAsState("")
    val dueMode by viewModel.dueModeField.observeAsState(Constants.TimeMode.AM) //true for am and false for pm

    val isTitleError by viewModel.titleError.observeAsState(false)
    val titleErrorMessage by viewModel.titleErrorMessage.observeAsState("")
    val isDateError by viewModel.dateError.observeAsState(false)
    val dateErrorMessage by viewModel.dateErrorMessage.observeAsState("")
    val isTimeError by viewModel.timeError.observeAsState(false)
    val timeErrorMessage by viewModel.timeErrorMessage.observeAsState("")

    viewModel.titleValidation.observeAsState().value?.apply {
        when (this.status) {
            Status.ERROR ->
                viewModel.setTitleError(true, data?.let { stringResource(it) } ?: "")

            else -> viewModel.setTitleError(false, "")
        }
    }

    viewModel.dateValidation.observeAsState().value?.apply {
        when (this.status) {
            Status.ERROR ->
                viewModel.setDateError(true, data?.let { stringResource(it) } ?: "")

            else -> viewModel.setDateError(false)
        }
    }

    viewModel.timeValidation.observeAsState().value?.apply {
        when (this.status) {
            Status.ERROR ->
                viewModel.setTimeError(true, data?.let { stringResource(it) } ?: "")

            else -> viewModel.setTimeError(false)
        }
    }

    viewModel.closeActivity.observeAsState().value?.apply {
        if (this) {
            context.finish()
        }
    }

    MainScaffold(
        content = {
            val focusManager = LocalFocusManager.current

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)// TODO: why padding not working
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        singleLine = true,
                        onValueChange = {
                            viewModel.onTitleChange(it)
                        },
                        label = { Text(stringResource(id = R.string.title)) },
                        maxLines = 1,
                        isError = isTitleError,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isTitleError) {
                        Text(
                            text = titleErrorMessage,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = desc,
                        singleLine = true,
                        onValueChange = {
                            viewModel.onDescChange(it)
                        },
                        label = { Text(stringResource(id = R.string.description)) },
                        maxLines = 5,

                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(id = R.string.due_date),
                        style = MaterialTheme.typography.body2
                    )

                    Row() {

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            value = dueMonth,
                            onValueChange = {
                                if (it.length < MAX_LENGTH_2) {
                                    viewModel.onDueMonthChange(it)
                                }
                            },
                            isError = isDateError,
                            label = { Text(stringResource(id = R.string.due_month)) },

                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.NumberPassword
                            ),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = dueDay,
                            onValueChange = {
                                if (it.length < MAX_LENGTH_2) {
                                    viewModel.onDueDayChange(it)
                                }
                            },
                            singleLine = true,
                            isError = isDateError,
                            label = { Text(stringResource(id = R.string.due_day)) },

                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.NumberPassword
                            ),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            value = dueYear,
                            onValueChange = {
                                if (it.length < MAX_LENGTH_4) {
                                    viewModel.onDueYearChange(it)
                                }
                            },
                            isError = isDateError,
                            label = { Text(stringResource(id = R.string.due_year)) },

                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.NumberPassword
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isDateError) {
                        Text(
                            text = dateErrorMessage,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(id = R.string.due_time),
                        style = MaterialTheme.typography.body2
                    )

                    Row() {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            value = dueHour,
                            onValueChange = {
                                if (it.length < MAX_LENGTH_2) {
                                    viewModel.onDueHourChange(it)
                                }
                            },
                            isError = isTimeError,
                            label = { Text(stringResource(id = R.string.due_hour)) },

                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.NumberPassword
                            ),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = dueMinute,
                            onValueChange = {
                                if (it.length < MAX_LENGTH_2) {
                                    viewModel.onDueMinuteChange(it)
                                }
                            },
                            singleLine = true,
                            isError = isTimeError,
                            label = { Text(stringResource(id = R.string.due_minute)) },
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.NumberPassword
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isTimeError) {
                        Text(
                            text = timeErrorMessage,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                        )
                    }

                    Row(Modifier.selectableGroup()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = dueMode.value,
                                onClick = { viewModel.onDueModeChange(Constants.TimeMode.AM) })
                            Text(text = stringResource(id = R.string.time_mode_am))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !dueMode.value,
                                onClick = { viewModel.onDueModeChange(Constants.TimeMode.PM) })
                            Text(text = stringResource(id = R.string.time_mode_pm))
                        }
                    }
                }

                Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Button(modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.onSave(todoId)
                        })
                    {
                        Text(stringResource(id = R.string.save))
                    }


                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            context.finish()
                        }
                    ) {

                        Text(
                            stringResource(id = R.string.cancel)
                        )
                    }
                }
            }
        },
        topBar = {
            TopAppBar(contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text(stringResource(id = if (isEditMode(todoId)) R.string.top_bar_edit else R.string.top_bar_create))
            }
        },
        fab = {}
    )

}

//returns true if todoId is valid whcih means it is in edit mode
private fun isEditMode(todoId: Int) = todoId > -1
