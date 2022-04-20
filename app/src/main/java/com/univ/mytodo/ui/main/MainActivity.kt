package com.univ.mytodo.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.univ.mytodo.R
import com.univ.mytodo.data.model.Todo
import com.univ.mytodo.ui.components.MainScaffold
import com.univ.mytodo.ui.createoredit.CreateTodoActivity
import com.univ.mytodo.ui.theme.MyTodoTheme
import com.univ.mytodo.util.Constants
import com.univ.mytodo.util.Constants.INTENT_KEY_TODO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTodoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyTodoTheme {
                        MainScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {

    val context = LocalContext.current

    val isSortExpanded = viewModel.isSortExpanded.observeAsState(false)
    val allTodoList = viewModel.allTodoList.observeAsState()

    MainScaffold(
        topBar = {
            TopAppBar(contentPadding = PaddingValues(horizontal = 8.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = R.string.top_bar_all_tasks))

                    Box {
                        IconButton(onClick = {
                            viewModel.onFilterExpanded(true) }) {
                            Icon(
                                Icons.Filled.Sort,
                                contentDescription = "Sort"
                            )
                        }

                        DropdownMenu(
                            expanded = isSortExpanded.value,
                            onDismissRequest = { viewModel.onFilterExpanded(false) }) {

                            DropdownMenuItem(onClick = {
                                viewModel.onFilterExpanded(false)
                                viewModel.onFilterClick( Constants.TodoType.ALL)
                            }) {
                                Text(text = stringResource(id = R.string.dd_all))
                            }

                            DropdownMenuItem(onClick = {
                                viewModel.onFilterExpanded(false)

                                viewModel.onFilterClick( Constants.TodoType.COMPLETED)
                            }) {
                                Text(text = stringResource(id = R.string.dd_completed))
                            }

                            DropdownMenuItem(onClick = {
                                viewModel.onFilterExpanded(false)
                                viewModel.onFilterClick(Constants.TodoType.INCOMPLETE)
                            }) {
                                Text(text = stringResource(id = R.string.dd_in_complete))
                            }
                        }
                    }
                }
            }
        },
        fab = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            CreateTodoActivity::class.java
                        )
                    )
                },
                shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Localized description")
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                allTodoList.value?.run {
                    if (!isNullOrEmpty()) {
                        LazyColumn() {
                            items(items = this@run, key = { item ->
                                item.id
                            }) { item ->
                                TodoItem(
                                    Todo(
                                        item.id,
                                        item.title,
                                        item.desc,
                                        item.date,
                                        item.time,
                                        item.isCompleted
                                    ),
                                    mainViewModel = viewModel
                                )
                            }
                        }
                    } else {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_empty_state),
                                contentDescription = "empty state"
                            )
                            Text(
                                text = "Wow, such empty",
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TodoItem(
    todo: Todo,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {

    val context = LocalContext.current

    Card(
        modifier = modifier.padding(16.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant,
    ) {
        var isExpanded by remember { mutableStateOf(false) }

        Row(
            Modifier
                .fillMaxWidth()
                .background(
                    color = if (todo.isCompleted) androidx.compose.ui.graphics.Color.Black.copy(
                        alpha = 0.7f
                    ) else MaterialTheme.colors.primaryVariant
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = todo.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.body1,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = todo.desc,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = todo.date,
                    maxLines = 1,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                )
            }

            Box(Modifier.align(Alignment.Top)) {

                IconButton(onClick = { isExpanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }

                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }) {

                    if (!todo.isCompleted) {
                        DropdownMenuItem(onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    CreateTodoActivity::class.java
                                ).putExtra(INTENT_KEY_TODO, todo.id)
                            )

                            isExpanded = false
                        }) {
                            Text(text = stringResource(id = R.string.dd_edit))
                        }
                    }

                    DropdownMenuItem(onClick = {
                        mainViewModel.deleteTodo(todo)

                        isExpanded = false
                    }) {
                        Text(text = stringResource(id = R.string.dd_delete))
                    }
                    DropdownMenuItem(onClick = {
                        mainViewModel.changeMarkAsStatus(todo.copy(isCompleted = !todo.isCompleted))

                        isExpanded = false
                    }) {
                        Text(text = stringResource(id = if (todo.isCompleted) R.string.dd_mark_as_uncomplete else R.string.dd_mark_as_complete))
                    }

                }
            }
        }
    }
}