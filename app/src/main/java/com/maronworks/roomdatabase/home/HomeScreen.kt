package com.maronworks.roomdatabase.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maronworks.roomdatabase.home.components.AddTodoDialog
import com.maronworks.roomdatabase.home.components.TodoCard
import com.maronworks.roomdatabase.home.components.UpdateTodoDialog
import com.maronworks.roomdatabase.home.model.Todo
import com.maronworks.roomdatabase.ui.theme.RoomDatabaseTheme


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    val viewModel = HomeViewModel()
    val todoList by viewModel.todoList.observeAsState()
    var showAddDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showUpdateDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var id by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Room Database",
                        fontFamily = FontFamily.Monospace
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = "New",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.W500
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = ""
                    )
                },
                onClick = { showAddDialog = !showAddDialog }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            todoList?.let { todos ->
                LazyColumn(
                    reverseLayout = true,
                    content = {
                        itemsIndexed(todos) { _: Int, item: Todo ->
                            TodoCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                todo = item,
                                onDelete = {
                                    viewModel.deleteTodo(item.id)
                                },
                                onClick = {
                                    title = item.title
                                    id = item.id
                                    showUpdateDialog = !showUpdateDialog
                                }
                            )
                        }
                    }
                )
            }
        }

        if (showAddDialog) {
            AddTodoDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = title,
                onValueChange = { title = it },
                onDismiss = {
                    showAddDialog = false // hide dialog
                    viewModel.addTodo(title.trim()) // remove leading and trailing spaces
                    title = "" // clear title
                }
            )
        }

        if (showUpdateDialog) {
            UpdateTodoDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = title,
                onValueChange = {
                    title = it
                },
                onDismiss = {
                    showUpdateDialog = false
                    viewModel.updateTodo(id = id, title = title.trim())
                    title = ""
                    id = 0
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    RoomDatabaseTheme {
        HomeScreen()
    }
}